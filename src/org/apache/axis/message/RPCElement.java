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

package org.apache.axis.message;

import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.Constants;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.SAXException;

import javax.xml.rpc.namespace.QName;
import java.util.Vector;

public class RPCElement extends SOAPBodyElement
{
    protected Vector params = new Vector();
    protected boolean needDeser = false;
    
    public RPCElement(String namespace, String localName, String prefix,
                      Attributes attributes, DeserializationContext context)
    {
        super(namespace, localName, prefix, attributes, context);

        // This came from parsing XML, so we need to deserialize it sometime
        needDeser = true;
    }
    
    public RPCElement(String namespace, String methodName, Object [] args)
    {
        this.setNamespaceURI(namespace);
        this.name = methodName;
        
        for (int i = 0; args != null && i < args.length; i++) {
            if (args[i] instanceof RPCParam) {
                addParam((RPCParam)args[i]);
            } else {
                String name = null;
                if (name == null) name = "arg" + i;
                addParam(new RPCParam(name, args[i]));
            }
        }
    }
    
    public RPCElement(String methodName)
    {
        this.name = methodName;
    }
    
    public String getMethodName()
    {
        return name;
    }
    
    public void deserialize() throws SAXException
    {
        context.pushElementHandler(new EnvelopeHandler(new RPCHandler(this)));
        context.setCurElement(this);
        
        needDeser = false;
        
        publishToHandler(context);
    }
    
    /** This gets the FIRST param whose name matches.
     * !!! Should it return more in the case of duplicates?
     */
    public RPCParam getParam(String name) throws SAXException
    {
        if (needDeser) {
            deserialize();
        }
        
        for (int i = 0; i < params.size(); i++) {
            RPCParam param = (RPCParam)params.elementAt(i);
            if (param.getName().equals(name))
                return param;
        }
        
        return null;
    }
    
    public Vector getParams() throws SAXException
    {
        if (needDeser) {
            deserialize();
        }

        return params;
    }
    
    public void addParam(RPCParam param)
    {
        param.setRPCCall(this);
        params.addElement(param);
    }

    protected void outputImpl(SerializationContext context) throws Exception
    {
        context.startElement(new QName(namespaceURI,name), attributes);
        for (int i = 0; i < params.size(); i++) {
            ((RPCParam)params.elementAt(i)).serialize(context);
        }
        context.endElement();
    }
}
