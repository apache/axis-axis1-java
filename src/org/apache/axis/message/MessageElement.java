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
import org.xml.sax.helpers.AttributesImpl;
import org.w3c.dom.*;
import org.apache.axis.Constants;
import org.apache.axis.message.events.*;
import org.apache.axis.encoding.*;
import org.apache.axis.utils.QName;
import java.util.*;
import java.io.*;

public class MessageElement
{
    private static final boolean DEBUG_LOG = false;
    
    protected String    name ;
    protected String    prefix ;
    protected String    namespaceURI ;
    protected AttributesImpl attributes;
    protected String    id;
    protected boolean   isRoot = false;
    protected SOAPEnvelope message = null;
    protected DeserializationContext context = null;
    DeserializerBase deserializer;
    
    // The java Object value of this element.  This is either set by
    // deserialization, or by the user creating a message.
    protected Object value = null;
    protected QName typeQName = null;
    
    // String representation of this element.
    protected String stringRep = null;
    
    protected ElementRecorder recorder = null;

    /** No-arg constructor for building messages?
     */
    MessageElement()
    {
    }
    
    MessageElement(String namespace, String localPart,
                    Attributes attributes, DeserializationContext context)
    {
        if (DEBUG_LOG) {
            System.out.println("New MessageElement named " + localPart);
            for (int i = 0; i < attributes.getLength(); i++) {
                System.out.println("  " + attributes.getQName(i) + " = '" + attributes.getValue(i) + "'");
            }
        }
      this.namespaceURI = namespace;
      this.name = localPart;
      this.context = context;
      this.attributes = new AttributesImpl();
      for (int i = 0; i < attributes.getLength(); i++) {
          this.attributes.addAttribute(attributes.getURI(i),
                                       attributes.getLocalName(i),
                                       attributes.getQName(i),
                                       "string",
                                       attributes.getValue(i));
      }
            
      String rootVal = attributes.getValue(Constants.URI_SOAP_ENV, Constants.ATTR_ROOT);
      // !!! This currently defaults to false... should it default to true?
      if (rootVal != null)
          isRoot = rootVal.equals("1");
      
      id = attributes.getValue(Constants.ATTR_ID);
      // Register this ID with the context.....
      if (id != null) {
          context.registerID(id, this);
      }
    }
    
    public boolean getRoot() { return isRoot; }
    public String getID() { return id; }
    
    public String getName() { return( name ); }
    
    public String getPrefix() { return( prefix ); }
    public void setPrefix(String prefix) { this.prefix = prefix; }
    
    public String getNamespaceURI() { return( namespaceURI ); }
    public void setNamespaceURI(String nsURI) { namespaceURI = nsURI; }
    
    public QName getType() { return typeQName; }
    public void setType(QName qName) { typeQName = qName; }

    public void setEnvelope(SOAPEnvelope env)
    {
        message = env;
    }
    public SOAPEnvelope getEnvelope()
    {
        return message;
    }
    
    public Element getAsDOM()
    {
        return null;
    }
    
    public Object getValue() throws IOException
    {
        if (value instanceof ElementRecorder) {
            // !!! Lazy deserialization here... We have the SAX events,
            //     but haven't run them through a deserializer yet.
            return null;
        }
        
        if (deserializer != null) {
            value = deserializer.getValue();
            deserializer = null;
        }
        
        return value;
    }

    public void setValue(Object val)
    {
        value = val;
    }
    
    public ContentHandler getContentHandler()
    {
        if (recorder == null)
            recorder = new ElementRecorder();
        
        return recorder;
    }
    
    public void publishToHandler(ContentHandler handler) throws SAXException
    {
        if (recorder == null)
            throw new SAXException("No event recorder inside element");
        
        recorder.publishToHandler(handler);
    }
    
    public void output(SerializationContext context) throws IOException
    {
        context.registerPrefixForURI(prefix, namespaceURI);
        //System.out.println("In outputToWriter (" + this.getName() + ")");
        if (recorder != null) {
            try {
                recorder.publishToHandler(new SAXOutputter(context));
            } catch (SAXException e) {
                Exception ex = e.getException();
                if (ex instanceof IOException)
                    throw (IOException)ex;
                throw new IOException(e.toString());
            }
            return;
        }

        AttributesImpl attrs = new AttributesImpl();
        Object val = getValue();
        
        if (val != null) {
            if (typeQName == null)
                typeQName = context.getQNameForClass(val.getClass());
            
            if (attributes != null) {
                // Must be writing a message we parsed earlier, so just put out
                // what's already there.
                for (int i = 0; i < attributes.getLength(); i++) {
                    attrs.addAttribute(attributes.getURI(i), attributes.getLocalName(i),
                                       attributes.getQName(i), "string",
                                       attributes.getValue(i));
                }
            } else {
                // Writing a message from memory out to XML...
                // !!! How do we set other attributes when serializing??
                
                ServiceDescription desc = getEnvelope().getServiceDescription();
                if ((desc == null) || desc.getSendTypeAttr()) {
                    if (typeQName != null) {
                        attrs.addAttribute(Constants.URI_SCHEMA_XSI, "type", "xsi:type",
                                           "string",
                                           context.qName2String(typeQName));
                    }
                }
                
                if (val == null)
                    attrs.addAttribute(Constants.URI_SCHEMA_XSI, "null", "xsi:null",
                                       "string", "1");
            }
            
            context.startElement(new QName(getNamespaceURI(), getName()), attrs);
            // Output the value...
            if (val != null)
                context.writeString(value.toString());
            
            context.endElement();
        }
    }
}
