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

import java.io.*;
import java.util.*;
import org.apache.axis.Constants;
import org.apache.axis.encoding.*;
import org.apache.axis.utils.QName;
import org.xml.sax.*;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

/** An RPC parameter element.
 * 
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class RPCParam extends MessageElement
{
    private static boolean DEBUG_LOG = false;
    
    // Who's your daddy?
    RPCElement myRPCElement;

    /** This constructor is called during XML parsing.
     */
    public RPCParam(String namespace, String localName, Attributes attrs,
                    DeserializationContext context)
    {
        super(namespace, localName, attrs, context);
        name = localName;

        // Check for type
        String type = attrs.getValue(Constants.URI_SCHEMA_XSI, "type");
        
        if (DEBUG_LOG)
            System.out.println("Got param.  Name ='" + name + "' Type is '" + type + "'");
        
        if (type == null)
          return;
        
        // OK, this is a QName, so look up the prefix in our current mappings.
        
        int i = type.indexOf(":");
        
        // !!! Can we have types that are in the default namespace?
        String nsURI = context.getNamespaceURI(type.substring(0, i));
        
        //System.out.println("namespace = " + nsURI);
        
        if (nsURI == null) {
            // !!! Should we just fault here (invalid NS prefix)?
            //     This of course implies that all the MessageElement
            //     constructors would be able to throw exceptions...
            type = null;
            return;
        }
        
        typeQName = new QName(nsURI, type.substring(i + 1));
    }
    
    /** Constructor for building up messages.
     */
    public RPCParam(String name, Object value)
    {
        this.name = name;
        this.value = value;
    }
    
    public RPCParam(String namespace, String name, Object value)
    {
        this.namespaceURI = namespace;
        this.name = name;
        this.value = value;
    }
    
    public void setRPCElement(RPCElement element)
    {
        myRPCElement = element;
    }
    
    /** !!! This is a little messy... need to think about
     * how elements get connected with their envelopes a bit
     * more?
     */
    public SOAPEnvelope getEnvelope()
    {
        if (myRPCElement != null)
            return myRPCElement.getEnvelope();
        
        return super.getEnvelope();
    }
    
    public ContentHandler getContentHandler()
    {
        // Look up type and return an appropriate deserializer
        if (typeQName != null) {
            deserializer = context.getDeserializer(typeQName);
            if (deserializer != null) {
                return deserializer;
            }
        }
        
        // If we couldn't find one, just record...
        
        /** !!! Is it possible that we'll do this now, but
         * later on we'll figure out the type (via some OOB
         * means)?  In that case we want an easy way to
         * squirt these SAX events to a deserializer.
         */
        if (DEBUG_LOG) {
            System.err.println("Creating recorder for " + this.getName());
        }
        value = new ElementRecorder();
        return (ElementRecorder)value;
    }
    
    public Object getValue()
    {
        if (value instanceof ElementRecorder) {
            // !!! Lazy deserialization here... We have the SAX events,
            //     but haven't run them through a deserializer yet.
            StringWriter xml = new StringWriter();
            try {
               ((ElementRecorder)value).output(new SerializationContext(xml));
            } catch (Exception e) {
               if (DEBUG_LOG) e.printStackTrace();
               return null;
            }
            return xml.getBuffer().toString();
        }
        
        if (deserializer != null) {
            value = deserializer.getValue();
            deserializer = null;
        }
        
        return value;
    }
    
    public void output(SerializationContext context) throws IOException
    {
        AttributesImpl attrs;
        if (deserializer != null) getValue();
        
        if (value instanceof ElementRecorder) {
            try {
                ((ElementRecorder)value).publishToHandler(new SAXOutputter(context));
            } catch (SAXException ex) {
                throw new IOException(ex.getMessage());
            }
            return;
        } else {
            if ((value != null) && (typeQName == null)) {
                typeQName = context.getQNameForClass(value.getClass());
            }
        }
        
        if (attributes != null) {
            // Must be writing a message we parsed earlier, so just put out
            // what's already there.
            attrs = new AttributesImpl(attributes);
        } else {
            // Writing a message from memory out to XML...
            // !!! How do we set other attributes when serializing??
            attrs = new AttributesImpl();
            
            ServiceDescription desc = getEnvelope().getServiceDescription();
            if ((desc == null) || desc.getSendTypeAttr()) {
                
                if (typeQName != null) {
                    attrs.addAttribute(Constants.URI_SCHEMA_XSI, "type", "xsi:type",
                                       "CDATA",
                                       context.qName2String(typeQName));
                }
            }
        
            if (value == null)
                attrs.addAttribute(Constants.URI_SCHEMA_XSI, "null", "xsi:null",
                                   "CDATA", "1");
        }

        if (typeQName == null)
            typeQName = context.getQNameForClass(value.getClass());
        
        context.serialize(new QName(getNamespaceURI(), getName()), attrs, value);
    }
}
