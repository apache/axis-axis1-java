package org.apache.axis.message;

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

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.apache.axis.encoding.*;
import org.apache.axis.utils.QName;
import java.util.*;
import java.io.*;

/** An RPC body element.
 * 
 * Assumes all sub-elements are parameters (see RPCParam).
 * 
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class RPCElement extends SOAPBodyElement
{
    static class RPCElementFactory implements ElementFactory
    {
        public MessageElement createElement(String namespace, String localName,
                                        Attributes attributes, DeserializationContext context)
        {
            return new RPCElement(namespace, localName, attributes, context);
        }
    }
    
    public static ElementFactory getFactory()
    {
        return new RPCElementFactory();
    }
    
    class RPCContentHandler extends DefaultHandler
    {
        private boolean passedMyStart = false;
        
        public void startElement(String namespace, String name, String qName,
                                 Attributes attributes)
        {
            if (!passedMyStart) {
                passedMyStart = true;
                return;
            }
            
            // Start of an arg...
            RPCParam param = new RPCParam(namespace, name, attributes, context);
            params.addElement(param);
            if (param.getType() == null) {
                // No type inline, so check service description.
                ServiceDescription serviceDesc = getEnvelope().getServiceDescription();
                if (serviceDesc != null) {
                    param.setType(serviceDesc.getParamTypeByName(getEnvelope().getMessageType(),
                                                                 param.getName()));
                }
            } else {
                /** !!! If we have a service description and this is an
                 * explicitly-typed param, we might want to check here to
                 * see if the xsi:type val is indeed a subtype of the type
                 * we expect from the service description.
                 */
                
            }
            context.pushElementHandler(param.getContentHandler());
        }
    }
    public ContentHandler getContentHandler() { return new RPCContentHandler(); }
    
    ///////////////////////////////////////////////////////////////
    
    protected String methodName;    
    protected Vector params = new Vector();
    
    public RPCElement(String namespace, String localName, Attributes attrs,
                      DeserializationContext context)
    {
        super(namespace, localName, attrs, context);
        this.methodName = localName;
    }
    
    public RPCElement(String methodName, Object [] args)
    {
        this.methodName = methodName;
        this.name = methodName;
        
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof RPCParam) {
                addParam((RPCParam)args[i]);
            } else {
                addParam(new RPCParam("arg" + i, args[i]));
            }
        }
    }
    
    public RPCElement(String methodName)
    {
        this.methodName = methodName;
        this.name = methodName;
    }
    
    public void output(SerializationContext context)
        throws IOException
    {
        context.registerPrefixForURI(prefix, namespaceURI);
        context.startElement(new QName(this.getNamespaceURI(), this.getName()), attributes);
        Enumeration e = params.elements();
        while (e.hasMoreElements()) {
            ((RPCParam)e.nextElement()).output(context);
        }
        context.endElement();
    }
    
    public String getMethodName()
    {
        return methodName;
    }
    
    /** This gets the FIRST param whose name matches.
     * !!! Should it return more in the case of duplicates?
     */
    public RPCParam getParam(String name)
    {
        for (int i = 0; i < params.size(); i++) {
            RPCParam param = (RPCParam)params.elementAt(i);
            if (param.getName().equals(name))
                return param;
        }
        
        return null;
    }
    
    public Vector getParams()
    {
        return params;
    }
    
    public void addParam(RPCParam param)
    {
        param.setRPCElement(this);
        params.addElement(param);
    }
}
