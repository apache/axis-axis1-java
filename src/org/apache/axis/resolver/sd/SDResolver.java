package org.apache.axis.resolver.sd;

import org.apache.axis.Handler;
import org.apache.axis.resolver.Resolver;
import org.apache.axis.resolver.ResolverContext;
import org.apache.axis.resolver.ResolverException;

/**
 * Resolves services defined using the simplified service descriptor (sd)
 * format.  Resolution is based on the file path or URL of the sd.
 * If the sd is loaded properly, a new handler instance is returned.
 *
 * @author James Snell (jasnell@us.ibm.com)
 */

public class SDResolver implements Resolver {

    public Handler resolve(ResolverContext context) throws ResolverException {
        try {
            ServiceDescriptor sd = new ServiceDescriptor(context.getKey(), context.getResolver());
            return sd.newInstance();
        } catch (Exception e) { 
            return null; }
    }
    
    public boolean getAllowCaching() { return true; }
}
