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
import org.apache.axis.AxisFault;
import org.apache.axis.message.events.*;
import org.apache.axis.encoding.*;
import org.apache.axis.utils.Debug;
import org.apache.axis.utils.QName;
import org.apache.axis.utils.DOM2Writer;
import java.util.*;
import java.io.*;

public class MessageElement extends DeserializerBase
{
    private static final boolean DEBUG_LOG = false;
    
    protected String    name ;
    protected String    prefix ;
    protected String    namespaceURI ;
    protected AttributesImpl attributes;
    protected String    id;
    protected String    href;
    protected boolean   isRoot = false;
    protected SOAPEnvelope message = null;
    
    // The java Object value of this element.  This is either set by
    // deserialization, or by the user creating a message.
    protected QName typeQName = null;
    
    // String representation of this element.
    protected String stringRep = null;
    
    protected ElementRecorder recorder = null;
    protected DeserializerBase deserializer = null;
    protected Boolean deserializing = null;

    /** No-arg constructor for building messages?
     */
    MessageElement()
    {
    }
    
    MessageElement(String namespace, String localPart)
    {
        namespaceURI = namespace;
        name = localPart;
    }
    
    MessageElement(String namespace, String localPart,
                    Attributes attributes, DeserializationContext context)
    {
        if (DEBUG_LOG) {
            System.out.println("New MessageElement named " + localPart);
            for (int i = 0; attributes != null && i < attributes.getLength(); i++) {
                System.out.println("  " + attributes.getQName(i) + " = '" + attributes.getValue(i) + "'");
            }
        }
      this.namespaceURI = namespace;
      this.name = localPart;
      setDeserializationContext(context);

      if (attributes == null) {
        this.attributes = new AttributesImpl();
      } else {
        typeQName = context.getTypeFromAttributes(attributes);

        this.attributes = new AttributesImpl(attributes);
        String rootVal = attributes.getValue(Constants.URI_SOAP_ENV, Constants.ATTR_ROOT);
        // !!! This currently defaults to false... should it default to true?
        if (rootVal != null)
            isRoot = rootVal.equals("1");
      
        id = attributes.getValue(Constants.ATTR_ID);
        // Register this ID with the context.....
        if (id != null) {
            context.registerID(id, this);
        }
            
        href = attributes.getValue(Constants.ATTR_HREF);
        
        // If there's an arrayType attribute, we can pretty well guess that we're an Array???
        if (attributes.getValue(Constants.URI_SOAP_ENC, Constants.ATTR_ARRAY_TYPE) != null)
          typeQName = SOAPTypeMappingRegistry.SOAP_ARRAY;
      }

      if (typeQName == null) {
          QName myQName = new QName(namespaceURI, name);
          if (myQName.equals(SOAPTypeMappingRegistry.SOAP_ARRAY)) {
              typeQName = SOAPTypeMappingRegistry.SOAP_ARRAY;
          } else if (myQName.equals(SOAPTypeMappingRegistry.SOAP_STRING)) {
              typeQName = SOAPTypeMappingRegistry.XSD_STRING;
          } else if (myQName.equals(SOAPTypeMappingRegistry.SOAP_BOOLEAN)) {
              typeQName = SOAPTypeMappingRegistry.XSD_BOOLEAN;
          } else if (myQName.equals(SOAPTypeMappingRegistry.SOAP_DOUBLE)) {
              typeQName = SOAPTypeMappingRegistry.XSD_DOUBLE;
          } else if (myQName.equals(SOAPTypeMappingRegistry.SOAP_FLOAT)) {
              typeQName = SOAPTypeMappingRegistry.XSD_FLOAT;
          } else if (myQName.equals(SOAPTypeMappingRegistry.SOAP_INT)) {
              typeQName = SOAPTypeMappingRegistry.XSD_INT;
          } else if (myQName.equals(SOAPTypeMappingRegistry.SOAP_LONG)) {
              typeQName = SOAPTypeMappingRegistry.XSD_LONG;
          } else if (myQName.equals(SOAPTypeMappingRegistry.SOAP_SHORT)) {
              typeQName = SOAPTypeMappingRegistry.XSD_SHORT;
          } else if (myQName.equals(SOAPTypeMappingRegistry.SOAP_BYTE)) {
              typeQName = SOAPTypeMappingRegistry.XSD_BYTE;
          }
      }
      
      if (typeQName == null) {
          // No type inline, so check service description.
          ServiceDescription serviceDesc = context.getServiceDescription();
          if (serviceDesc != null) {
              setType(serviceDesc.getParamTypeByName(context.getMessageType(),
                                                     name));
          }
      }
      
      // Look up type and set up an appropriate deserializer
      if ((typeQName != null) && isDeserializing()) {
          deserializer = context.getDeserializer(typeQName);
          if (DEBUG_LOG) {
              System.err.println(typeQName + " maps to " + deserializer);
          }
      }

    }
    
    public boolean isDeserializing()
    {
        boolean deser;
        if (deserializing == null) {
            ServiceDescription s = context.getServiceDescription();
            deser = ((s == null) || (s.isRPC()));
            deserializing = new Boolean(deser);
        } else {
            deser = deserializing.booleanValue();
        }
        return deser;
    }
    
    public boolean getRoot() { return isRoot; }
    public String getID() { return id; }
    
    public String getName() { return( name ); }
    public void setName(String name) { this.name = name; }
    
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
    
    public MessageElement getRealElement()
    {
      if (href == null)
        return this;
      
      return context.getElementByID(href.substring(1));
    }

    public Object getValue()
    {
        if (value != null) {
            if (DEBUG_LOG) {
                System.out.println(this + " returning val " + value);
            }
            return value;
        }
        
        if (href != null) {
            if (DEBUG_LOG) {
                System.out.println(this + " looking up ref element " + href);
            }
            return getRealElement().getValue();
        }
        
        if (deserializer != null) {
            value = deserializer.getValue();
            if (DEBUG_LOG) {
                System.out.println(this + " returning dser (" + deserializer +
                                   ") val=" + value);
            }
            deserializer = null;
        } else {
            // No attached deserializer, try it as a String...
            try {
                value = getValueAsType(SOAPTypeMappingRegistry.XSD_STRING);
            } catch (AxisFault fault) {
                Debug.Print(1, "Couldn't deserialize as String : " + fault);
            }
        }
        
        if (DEBUG_LOG) {
            System.out.println(this + " returning val=" + value);
        }
        return value;
    }

    public Object getValueAsType(QName typeQName) throws AxisFault
    {
      MessageElement realEl = getRealElement();
      
      if (realEl.typeQName != null) {
          if (!realEl.typeQName.equals(typeQName))
            throw new AxisFault("Couldn't convert " + realEl.typeQName +
                                " to requested type " + typeQName);
          return getValue();
      }
      
      DeserializerBase dser = realEl.context.getDeserializer(typeQName);
      if (dser == null)
        throw new AxisFault("No deserializer for type " + typeQName);
      
      try {
        realEl.publishToHandler(dser);
      } catch (SAXException e) {
        throw new AxisFault(e);
      }
      
      return dser.getValue();
    }
    
    public DeserializerBase getContentHandler()
    {
        if (isDeserializing()) {
          if (href != null) {
            deserializer = context.getElementByID(href.substring(1));
            if (deserializer != null)
              return deserializer;
          }
            if (deserializer != null) {
                return deserializer;
            }
        }
            
        /** !!! Is it possible that we'll do this now, but
        * later on we'll figure out the type (via some OOB
        * means)?  In that case we want an easy way to
        * squirt these SAX events to a deserializer.
        */
        if (DEBUG_LOG) {
            System.err.println("Creating recorder for " + this.getName());
        }
        recorder = new ElementRecorder();
        return recorder;
    }
    
    public void setContentHandler(DeserializerBase handler)
    {
        if (deserializer != null) {
            System.err.println("Non-null deser while setting content handler?");
        }
        
        deserializer = handler;
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

        AttributesImpl attrs;
        Object val = getValue();
        
        if (val != null) {
            if (typeQName == null)
                typeQName = context.getQNameForClass(val.getClass());
            
            if (attributes == null) {
                attrs = new AttributesImpl();
                // Writing a message from memory out to XML...
                // !!! How do we set other attributes when serializing??
                
                ServiceDescription desc = context.getServiceDescription();
                if ((desc == null) || desc.getSendTypeAttr()) {
                    if (typeQName != null) {
                        attrs.addAttribute(Constants.URI_CURRENT_SCHEMA_XSI, "type", "xsi:type",
                                           "CDATA",
                                           context.qName2String(typeQName));
                    }
                }
                
                if (val == null)
                    attrs.addAttribute(Constants.URI_CURRENT_SCHEMA_XSI, "null", "xsi:null",
                                       "CDATA", "1");
            } else {
                attrs = new AttributesImpl(attributes);
            }
            
            context.startElement(new QName(getNamespaceURI(), getName()), attrs);
            // Output the value...
            if (val != null)
                context.writeString(DOM2Writer.normalize(value.toString()));
            
            context.endElement();
        }
    }
}
