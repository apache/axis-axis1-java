package org.apache.axis.resolver.sd.schema;

import org.apache.axis.Handler;
import org.apache.axis.resolver.Resolver;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.rpc.namespace.QName;
import java.util.HashMap;

/**
 * Providers are pluggable.  They must register here in order
 * to be recognized during the parse procedure.
 *
 * @author James Snell (jasnell@us.ibm.com)
 */

public abstract class Provider extends SDElement {

    private static HashMap providers = new HashMap();
    protected DefaultHandler handler; 
    protected Resolver resolver;
    
    public static void registerProvider(QName type, Class providerClass) {
        providers.put(type, providerClass);
    }
    
    public static Provider newProvider(QName type) {
        try {
            Class c = (Class)providers.get(type);
            if (c != null) {
                return (Provider)c.newInstance();
            }
        } catch (Exception e) {}
        return null;
    }
    
    public DefaultHandler getDefaultHandler() {
        return handler;
    }
    
    public void setResolver(Resolver resolver) {
        this.resolver = resolver;
    }
    
    public Resolver getResolver() {
        return this.resolver;
    }
    
    public abstract Handler newInstance();
    
}
