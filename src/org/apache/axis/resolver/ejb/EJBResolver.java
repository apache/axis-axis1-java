package org.apache.axis.resolver.ejb;

import org.apache.axis.Handler;
import org.apache.axis.providers.java.EJBProvider;
import org.apache.axis.resolver.Resolver;
import org.apache.axis.resolver.ResolverContext;
import org.apache.axis.resolver.ResolverException;

/**
 * @author James Snell (jasnell@us.ibm.com)
 */

public class EJBResolver implements Resolver {

    public static final String CONTEXT_JNDICONTEXTCLASS = "jndiContextClass";
    public static final String CONTEXT_JNDIURL = "jndiURL";
    public static final String CONTEXT_JNDIUSER = "jndiUser";
    public static final String CONTEXT_JNDIPASSWORD = "jndiPassword";
    public static final String CONTEXT_PREFIX = "ejb:";
    
    public Handler resolve(ResolverContext context) throws ResolverException {
        try {
            String beanName = context.getKey();
            if (!beanName.startsWith(CONTEXT_PREFIX)) return null;
            beanName = beanName.substring(CONTEXT_PREFIX.length());
            Handler h = new EJBProvider();
            String jndiContextClass = (String)context.getProperty(CONTEXT_JNDICONTEXTCLASS);
            String jndiUrl = (String)context.getProperty(CONTEXT_JNDIURL);
            String jndiUser = (String)context.getProperty(CONTEXT_JNDIUSER);
            String jndiPassword = (String)context.getProperty(CONTEXT_JNDIPASSWORD);
            h.addOption("beanNameOption", beanName);
            if (jndiContextClass != null)
                h.addOption("jndiContextClass", jndiContextClass);
            if (jndiUrl != null)
                h.addOption("jndiURL", jndiUrl);
            if (jndiUser != null)
                h.addOption("jndiUser", jndiUser);
            if (jndiPassword != null)
                h.addOption("jndiPassword", jndiPassword);
            return h;
        } catch (Exception e) {
            return null;
        }
    }
    
    public boolean getAllowCaching() { return true; }
}
