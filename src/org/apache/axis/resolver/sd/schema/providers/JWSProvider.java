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

package org.apache.axis.resolver.sd.schema.providers;

import org.apache.axis.Handler;
import org.apache.axis.resolver.ResolverContext;
import org.apache.axis.resolver.java.JavaResolver;
import org.apache.axis.resolver.sd.schema.Provider;
import org.apache.axis.resolver.sd.schema.SDConstants;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.rpc.namespace.QName;

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
