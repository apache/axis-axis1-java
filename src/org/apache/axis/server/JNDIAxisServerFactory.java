package org.apache.axis.server;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.configuration.FileProvider;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import java.util.Map;

/**
 * Helper class for obtaining AxisServers, which hides the complexity
 * of JNDI accesses, etc.
 *
 * !!! QUESTION : Does this class need to play any ClassLoader tricks?
 * 
 * @author Glen Daniels (gdaniels@macromedia.com)
 */ 

public class JNDIAxisServerFactory implements AxisServerFactory {
    private static FileProvider defaultEngineConfig =
                           new FileProvider(Constants.SERVER_CONFIG_FILE);

    /**
     * Obtain an AxisServer reference, using JNDI if possible, otherwise
     * creating one using the standard Axis configuration pattern.  If we
     * end up creating one and do have JNDI access, bind it to the passed
     * name so we find it next time.
     * 
     * NOTE : REQUIRES SERVLET 2.3 FOR THE GetServletContextName() CALL!
     *
     * @param name the JNDI name we're interested in
     * @param configProvider a EngineConfiguration which should be used
     *                       to configure any engine we end up creating, or
     *                       null to use the default configuration pattern.
     */
    public AxisServer getServer(Map environment)
        throws AxisFault
    {
        AxisServer server = null;
        InitialContext context = null;

        // First check to see if JNDI works
        // !!! Might we need to set up context parameters here?
        try {
            context = new InitialContext();
        } catch (NamingException e) {
        }
        
        EngineConfiguration config = null;
        try {
            config = (EngineConfiguration)environment.get("engineConfig");
        } catch (ClassCastException e) {
            // Just in case, fall through here.
        }
        
        if (context != null) {
            // Figure out the name by looking in the servlet context (for
            // now)
            ServletContext servletContext = 
                    (ServletContext)environment.get("servletContext");
            if (servletContext != null) {
                
                /**
                 * !!! WARNING - THIS CLASS NEEDS TO FIGURE OUT THE CORRECT
                 * NAMING SCHEME FOR GETTING/PUTTING SERVERS FROM/TO JNDI!
                 * 
                 */
                
                // For servlet 2.3....?
                // String name = servletContext.getServletContextName();
                
                // THIS IS NOT ACCEPTABLE JNDI NAMING...
                String name = servletContext.getRealPath("/WEB-INF/Server");
                
                // We've got JNDI, so try to find an AxisServer at the
                // specified name.
                try {
                    server = (AxisServer)context.lookup(name);
                } catch (NamingException e) {
                    // Didn't find it.
                    server = createNewServer(config);
                    try {
                        context.bind(name, server);
                    } catch (NamingException e1) {
                        // !!! Couldn't do it, what should we do here?
                    }
                }
            } else {
                server = createNewServer(config);
            }
        }

        return server;
    }

    /**
     * Do the actual work of creating a new AxisServer, using the passed
     * engine configuration, or going through the default configuration
     * steps if null is passed.
     *
     * @return a shiny new AxisServer, ready for use.
     */
    static private AxisServer createNewServer(EngineConfiguration config)
    {
        // Just use the passed config if there is one.
        if (config != null) {
            return new AxisServer(config);
        }

        // Default configuration steps...
        //
        // 1. Check for a system property telling us which Engine
        //    Configuration to use.  If we find it, try creating one.
        String configClass = System.getProperty("axis.engineConfigClass");
        if (configClass != null) {
            // Got one - so try to make it (which means it had better have
            // a default constructor - may make it possible later to pass in
            // some kind of environmental parameters...)
            try {
                Class cls = Class.forName(configClass);
                config = (EngineConfiguration)cls.newInstance();
            } catch (ClassNotFoundException e) {
                // Fall through???
            } catch (InstantiationException e) {
                // Fall through???
            } catch (IllegalAccessException e) {
                // Fall through???
            }
        }

        // 2. If we couldn't make one above, use the default one.
        // !!! May want to add options here for getting another system
        //     property which is the config file name...
        if (config == null) {
            config = defaultEngineConfig;
        }

        // 3. Create an AxisServer using the appropriate config
        return new AxisServer(config);
    }
}
