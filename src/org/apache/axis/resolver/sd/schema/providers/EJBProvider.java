package org.apache.axis.resolver.sd.schema.providers;

import org.apache.axis.utils.QName;
import org.apache.axis.resolver.ResolverContext;
import org.apache.axis.resolver.sd.schema.SDConstants;
import org.apache.axis.resolver.sd.schema.Provider;
import org.apache.axis.resolver.ejb.EJBResolver;
import org.apache.axis.Handler;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;

/**
 * EJB provider extension.
 *
 * Example:
 *   <service xmlns="http://xml.apache.org/axis/sd/"
 *            xmlns:ejb="http://xml.apache.org/axis/sd/ejb">
 *
 *      <ejb:provider  beanName="bean.name"
 *                     contextClass="context.class.name"
 *                     url="jndi.url"
 *                     user="user.id"
 *                     password="password" />
 *
 *   </service>
 *
 * @author James Snell (jasnell@us.ibm.com)
 */

public class EJBProvider extends Provider {

    public static final QName qname = new QName(SDConstants.SDNS_EJB, "provider");
    static {
        Provider.registerProvider(qname, EJBProvider.class);
    }
    
    public EJBProvider() {
        handler = new EJBProviderHandler();
    }
    
    private String beanName;
    private String contextClass;
    private String url;
    private String user;
    private String password;
    
    public Handler newInstance() {
        try {
            ResolverContext context = new ResolverContext(EJBResolver.CONTEXT_PREFIX + beanName);
            if (contextClass != null)
                context.setProperty(EJBResolver.CONTEXT_JNDICONTEXTCLASS, contextClass);
            if (url != null) 
                context.setProperty(EJBResolver.CONTEXT_JNDIURL, url);
            if (user != null)
                context.setProperty(EJBResolver.CONTEXT_JNDIUSER, user);
            if (password != null)
                context.setProperty(EJBResolver.CONTEXT_JNDIPASSWORD, password);
            return resolver.resolve(context);
        } catch (Exception e) { return null; }
    }
    
    private class EJBProviderHandler extends DefaultHandler {
        public void startElement(String uri, String ln, String rn, org.xml.sax.Attributes attr) throws SAXException {
            beanName = attr.getValue("beanName");
            contextClass = attr.getValue("contextClass");
            url = attr.getValue("url");
            user = attr.getValue("user");
            password = attr.getValue("password");
        }
    }
    
}
