package org.apache.axis.resolver;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains the information necessary for resolving a specific handler.
 *
 * @author James Snell (jasnell@us.ibm.com)
 */

public class ResolverContext {

    private String   key = null;
    private Map      properties = null;
    private Resolver resolver;
    
    public ResolverContext() {}
    
    public ResolverContext(String key) {
        setKey(key);
    }
    
    /**
     * Return the top level resolver
     */
    public Resolver getResolver() {
        return this.resolver;
    }
    
    /**
     * Set the top level resolver
     */
    public void setResolver(Resolver resolver) {
        this.resolver = resolver;
    }
    
    /**
     * Return the handler key
     */
    public String getKey() {
        return this.key;
    }
    
    /**
     * Set the handler key
     */
    public void setKey(String key) {
        this.key = key;
    }
    
    /**
     * Context properties are used to provide additional information 
     * that may be needed to either resolve or instantiate a handler
     */
    public void setProperty(String name, Object value) {
        if (properties == null) properties = new HashMap();
        properties.put(name,value);
    }
     
    /**
     * Return a context property
     */   
    public Object getProperty(String name) {
        if (properties == null) return null;
        return properties.get(name);
    }

    /**
     * Get all of the context properties
     */    
    public Map getProperties() {
        return this.properties;
    }
    
    /**
     * Set the context properties
     */
    public void setProperties(Map properties) {
        this.properties = properties;
    }
}
