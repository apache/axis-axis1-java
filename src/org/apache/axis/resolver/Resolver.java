package org.apache.axis.resolver;

import org.apache.axis.Handler;

/**
 * The Resolver interface is used to locate a handler instance.
 *
 * @author James Snell (jasnell@us.ibm.com)
 */

public interface Resolver {

    /**
     * Resolves a handler instance
     *
     */
    public Handler resolve(ResolverContext context) throws ResolverException;
    
    public boolean getAllowCaching();
    
}
