package org.apache.axis.resolver;

import java.util.Vector;
import java.util.Iterator;
import java.util.HashMap;
import org.apache.axis.Handler;

/**
 * This is the primary top level resolver in Axis.
 * It maintains a list of registered Resolvers and 
 * loops through them when a resolve request is made
 * The first non-null result is returned.
 *
 * @author James Snell (jasnell@us.ibm.com)
 */

public class AxisResolver implements Resolver {

    private Vector resolvers = new Vector();
    private HashMap resolved = new HashMap();
    
    /**  
     * Register a new resolver
     */
    public void registerResolver(Resolver resolver) {
        resolvers.add(resolver);
    }
    
    /**
     * Resolve a handler by looping through the list of 
     * registered resolvers.  The first non-null result 
     * is returned.  Resolved handlers are cached so 
     * we don't have to resolve them every time.
     */
    public Handler resolve(ResolverContext context) throws ResolverException {
        if (resolved.containsKey(context.getKey())) {
            return (Handler)resolved.get(context.getKey());
        }
        context.setResolver(this);
        for (Iterator i = resolvers.iterator();i.hasNext();) {
            Resolver r = (Resolver)i.next();
            Handler h = r.resolve(context);
            if (h != null && r.getAllowCaching()) {
                resolved.put(context.getKey(), h);
                return h;
            }
        }
        return null;
    }
    
    public boolean getAllowCaching() {
        for (Iterator i = resolvers.iterator(); i.hasNext();) {
            if (!((Resolver)i.next()).getAllowCaching()) return false;
        }
        return true;
    }
}
