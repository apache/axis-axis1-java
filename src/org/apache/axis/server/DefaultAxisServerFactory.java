package org.apache.axis.server;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.configuration.FileProvider;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Map;

/**
 * Helper class for obtaining AxisServers.  Default implementation.
 * 
 * @author Glen Daniels (gdaniels@macromedia.com)
 */ 

public class DefaultAxisServerFactory implements AxisServerFactory {
    private static FileProvider defaultEngineConfig =
                           new FileProvider(Constants.SERVER_CONFIG_FILE);

    /**
     * Get an AxisServer.  This factory looks for an "engineConfig" in the
     * environment Map, and if one is found, uses that.  Otherwise it
     * uses the default initialization.
     * 
     */
    public AxisServer getServer(Map environment)
        throws AxisFault
    {
        EngineConfiguration config = null;
        try {
            config = (EngineConfiguration)environment.
                get(EngineConfiguration.PROPERTY_NAME);
        } catch (ClassCastException e) {
            // Just in case, fall through here.
        }
        
        return createNewServer(config);
    }

    /**
     * Do the actual work of creating a new AxisServer, using the passed
     * configuration provider, or going through the default configuration
     * steps if null is passed.
     *
     * @return a shiny new AxisServer, ready for use.
     */
    static private AxisServer createNewServer(EngineConfiguration config)
    {
        if (config == null) {
            // Default configuration steps...
            //
            // 1. Check for a system property telling us which Configuration
            //    Provider to use.  If we find it, try creating one.
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
        }

        // 3. Create an AxisServer using the appropriate config
        return new AxisServer(config);
    }
}
