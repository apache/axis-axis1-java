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
 * Java provider extension.
 *
 * Example:
 *   <service xmlns="http://xml.apache.org/axis/sd/"
 *            xmlns:java="http://xml.apache.org/axis/sd/java">
 *
 *      <java:provider style="rpc" 
 *                     className="some.class.name"
 *                     static="false"
 *                     classPath="some;class;path"
 *                     scope="Request" />
 *
 *   </service>
 *
 * The style attribute determines whether or not a Java
 * RPCProvider or MsgProvider is used. 
 *
 * @author James Snell (jasnell@us.ibm.com)
 */

public class JavaProvider extends Provider {

    public static final QName qname = new QName(SDConstants.SDNS_JAVA, "provider");
    static {
        Provider.registerProvider(qname, JavaProvider.class);
    }

    private String className;
    private String isStatic;
    private String classPath;
    private String scope;
    private String style;
    
    public JavaProvider() {
        handler = new JavaProviderHandler();
    }
    
    public Handler newInstance() {
        try {
            ResolverContext context = new ResolverContext(JavaResolver.CONTEXT_PREFIX + className);
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
 
    private class JavaProviderHandler extends DefaultHandler {
        public void startElement(String uri, String ln, String rn, org.xml.sax.Attributes attr) throws SAXException {
            className = attr.getValue("className");
            isStatic = attr.getValue("static");
            classPath = attr.getValue("classPath");
            scope = attr.getValue("scope");
            style = attr.getValue("style");
        }
    }
}

