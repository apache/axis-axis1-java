package org.apache.axis.client;

import org.apache.axis.ConfigurationProvider;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.configuration.FileProvider;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Helper class for obtaining AxisClients, which hides the complexity
 * of JNDI accesses, etc.
 *
 * !!! QUESTION : Does this class need to play any ClassLoader tricks?
 * 
 * @author Glen Daniels (gdaniels@macromedia.com)
 */ 

public class AxisClientFactory {
    private static FileProvider defaultConfigProvider =
                           new FileProvider(Constants.CLIENT_CONFIG_FILE);

    /**
     * Obtain an AxisClient reference, using JNDI if possible, otherwise
     * creating one using the standard Axis configuration pattern.  If we
     * end up creating one and do have JNDI access, bind it to the passed
     * name so we find it next time.
     *
     * @param name the JNDI name we're interested in
     * @param configProvider a ConfigurationProvider which should be used
     *                       to configure any engine we end up creating, or
     *                       null to use the default configuration pattern.
     */
    static public AxisClient getClient(String name,
                                       ConfigurationProvider configProvider)
        throws AxisFault
    {
        AxisClient client = null;
        InitialContext context = null;

        // First check to see if JNDI works
        // !!! Might we need to set up context parameters here?
        try {
            context = new InitialContext();
        } catch (NamingException e) {
        }
        
        if (context != null) {
            // We've got JNDI, so try to find an AxisClient at the
            // specified name.
            try {
                client = (AxisClient)context.lookup(name);
                System.err.println("found client : " + client);
            } catch (NamingException e) {
                // Didn't find it.
                client = createNewClient(configProvider);
                try {
                    context.bind(name, client);
                } catch (NamingException e1) {
                    // !!! Couldn't do it, what should we do here?
                }
            }
        } else {
            client = createNewClient(configProvider);
        }

        return client;
    }

    /**
     * Do the actual work of creating a new AxisClient, using the passed
     * configuration provider, or going through the default configuration
     * steps if null is passed.
     *
     * @return a shiny new AxisClient, ready for use.
     */
    static private AxisClient createNewClient(ConfigurationProvider provider)
    {
        // Just use the passed provider if there is one.
        if (provider != null) {
            return new AxisClient(provider);
        }

        // Default configuration steps...
        //
        // 1. Check for a system property telling us which Configuration
        //    Provider to use.  If we find it, try creating one.
        String configClass = System.getProperty("axis.configProviderClass");
        if (configClass != null) {
            // Got one - so try to make it (which means it had better have
            // a default constructor - may make it possible later to pass in
            // some kind of environmental parameters...)
            try {
                Class cls = Class.forName(configClass);
                provider = (ConfigurationProvider)cls.newInstance();
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
        if (provider == null) {
            provider = defaultConfigProvider;
        }

        // 3. Create an AxisClient using the appropriate provider
        return new AxisClient(provider);
    }
}
