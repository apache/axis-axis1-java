/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

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
