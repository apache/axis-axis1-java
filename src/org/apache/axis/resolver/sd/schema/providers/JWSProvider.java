package org.apache.axis.resolver.sd.schema.providers;

import javax.rpc.namespace.QName;
import org.apache.axis.Handler;
import org.apache.axis.resolver.ResolverContext;
import org.apache.axis.resolver.java.JavaResolver;
import org.apache.axis.resolver.sd.schema.Provider;
import org.apache.axis.resolver.sd.schema.SDConstants;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * JWS provider extension.
 *
 * Example:
 *   <service xmlns="http://xml.apache.org/axis/sd/"
 *            xmlns:jws="http://xml.apache.org/axis/sd/jws">
 *
 *      <jws:provider  style="rpc" 
 *                     jwsFile="jws.file.path"
 *                     static="false"
 *                     classPath="some;class;path"
 *                     scope="Request" />
 *
 *   </service>
 * 
 * @author James Snell (jasnell@us.ibm.com)
 */

public class JWSProvider extends Provider {

    public static final QName qname = new QName(SDConstants.SDNS_JWS, "provider");
    static {
        Provider.registerProvider(qname, JWSProvider.class);
    }
    
    private String jwsFile;
    private String isStatic;
    private String classPath;
    private String scope;
    private String style;
    
    public JWSProvider() {
        handler = new JWSProviderHandler();
    }
    
    public Handler newInstance() {
        try {
            ResolverContext context = new ResolverContext(jwsFile);
            if (style != null) 
                context.setProperty(JavaResolver.CONTEXT_STYLE, style);
            else 
                context.setProperty(JavaResolver.CONTEXT_STYLE, JavaResolver.CONTEXT_STYLE_DEFAULT);
            if (isStatic != null)
                context.setProperty(JavaResolver.CONTEXT_STATIC, isStatic);
            if (classPath != null)
                context.setProperty(JavaResolver.CONTEXT_CLASSPATH, classPath);
            if (scope != null)
                context.setProperty(JavaResolver.CONTEXT_SCOPE, scope);
            return resolver.resolve(context);
        } catch (Exception e) { return null; }
    }
    
    private class JWSProviderHandler extends DefaultHandler {
        public void startElement(String uri, String ln, String rn, org.xml.sax.Attributes attr) throws SAXException {
            jwsFile = attr.getValue("jwsFile");
            isStatic = attr.getValue("static");
            classPath = attr.getValue("classPath");
            scope = attr.getValue("scope");
            style = attr.getValue("style");
        }
    }
}
