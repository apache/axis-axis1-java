package org.apache.axis.resolver.java;

import org.apache.axis.Handler;
import org.apache.axis.providers.java.MsgProvider;
import org.apache.axis.providers.java.RPCProvider;
import org.apache.axis.resolver.Resolver;
import org.apache.axis.resolver.ResolverContext;
import org.apache.axis.resolver.ResolverException;

/**
 * Resolves a Java class as either a RPCProvider or MSGProvider.
 * 
 * @author James Snell (jasnell@us.ibm.com)
 */

public class JavaResolver implements Resolver {

    public static final String CONTEXT_STYLE = "style";
    public static final String CONTEXT_STATIC = "isStatic";
    public static final String CONTEXT_CLASSPATH = "classPath";
    public static final String CONTEXT_SCOPE = "scope";
    public static final String CONTEXT_PREFIX = "java:";
    public static final String CONTEXT_STYLE_RPC = "rpc";
    public static final String CONTEXT_STYLE_MSG = "message";
    public static final String CONTEXT_STYLE_DEFAULT = CONTEXT_STYLE_RPC;
    
    public Handler resolve(ResolverContext context) throws ResolverException {
        try {
            String clsName = context.getKey();
            if (!clsName.startsWith(CONTEXT_PREFIX)) return null;
            clsName = clsName.substring(CONTEXT_PREFIX.length());
            String style = (String)context.getProperty(CONTEXT_STYLE);
            String isStatic = (String)context.getProperty(CONTEXT_STATIC);
            String classPath = (String)context.getProperty(CONTEXT_CLASSPATH);
            String scope = (String)context.getProperty(CONTEXT_SCOPE);
            if (style == null) style = CONTEXT_STYLE_DEFAULT;
            Handler h = null;
            if (CONTEXT_STYLE_RPC.equals(style)) {
                h = new RPCProvider();
            }
            if (CONTEXT_STYLE_MSG.equals(style)) {
                h = new MsgProvider();
            }
            if (clsName != null) h.addOption("className", clsName);
            if (isStatic != null) h.addOption("isStatic", isStatic);
            if (classPath != null) h.addOption("classPath", classPath);
            if (scope != null) h.addOption("scope", scope);
            return h;
        } catch (Exception e) {
            return null;
        }
    }
    
    public boolean getAllowCaching() { return true; }
}
