package org.apache.axis.resolver;

import org.apache.axis.Handler;

/**
 * 
 * Resolves handlers, chains, transports, and services
 * that are listed in the Axis configuration file.
 * 
 * Will be finished soon.
 * 
 * @author James Snell (jasnell@us.ibm.com)
 */

public class ConfigurationResolver implements Resolver {

    public Handler resolve(ResolverContext context) throws ResolverException {
        return null;
    }
    
    public boolean getAllowCaching() { return true; }
}
