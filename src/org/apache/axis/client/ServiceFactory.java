package org.apache.axis.client;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.configuration.FileProvider;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.rpc.JAXRPCException;
import java.util.Map;

/**
 * Helper class for obtaining Services from JNDI.
 *
 * !!! WORK IN PROGRESS
 * 
 * @author Glen Daniels (gdaniels@macromedia.com)
 */ 

public class ServiceFactory {
    private static FileProvider defaultEngineConfig =
                           new FileProvider(Constants.CLIENT_CONFIG_FILE);
    private static ThreadLocal threadDefaultConfig = new ThreadLocal();

    public static void setThreadDefaultConfig(EngineConfiguration config)
    {
        threadDefaultConfig.set(config);
    }

    /**
     * Obtain an AxisClient reference, using JNDI if possible, otherwise
     * creating one using the standard Axis configuration pattern.  If we
     * end up creating one and do have JNDI access, bind it to the passed
     * name so we find it next time.
     *
     * @param name the JNDI name we're interested in
     * @param engineConfig a EngineConfiguration which should be used
     *                     to configure any engine we end up creating, or
     *                     null to use the default configuration pattern.
     */
    static public Service getService(Map environment)
        throws JAXRPCException
    {
        Service service = null;
        InitialContext context = null;

        EngineConfiguration configProvider = (EngineConfiguration)environment.
            get(EngineConfiguration.PROPERTY_NAME);
        if (configProvider == null)
            configProvider = (EngineConfiguration)threadDefaultConfig.get();

        if (configProvider == null)
            configProvider = defaultEngineConfig;

        // First check to see if JNDI works
        // !!! Might we need to set up context parameters here?
        try {
            context = new InitialContext();
        } catch (NamingException e) {
        }
        
        if (context != null) {
            String name = (String)environment.get("jndiName");
            if (name == null) {
                name = "axisServiceName";
            }

            // We've got JNDI, so try to find an AxisClient at the
            // specified name.
            try {
                service = (Service)context.lookup(name);
            } catch (NamingException e) {
                service = new Service(configProvider);
                try {
                    context.bind(name, service);
                } catch (NamingException e1) {
                    // !!! Couldn't do it, what should we do here?
                }
            }
        } else {
            service = new Service(configProvider);
        }

        return service;
    }
}
