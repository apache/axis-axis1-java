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
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.SOAPTypeMappingRegistry;
import org.apache.axis.MessageContext;
import org.apache.axis.utils.Debug;
import org.apache.axis.utils.QName;
import org.apache.axis.utils.DOM2Writer;
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
    protected String    href;
    protected boolean   isRoot = true;
    protected SOAPEnvelope message = null;
    
    protected DeserializationContext context;
    
    // The java Object value of this element.  This is either set by
    // deserialization, or by the user creating a message.
    protected QName typeQName = null;
    
    protected Vector qNameAttrs = null;

    public Hashtable nsDecls = new Hashtable();
                                            
    protected SAX2EventRecorder recorder = null;
    protected int startEventIndex = 0;
    protected int endEventIndex = -1;

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
            System.out.println("New MessageElement (" + this + ") named " + localPart);
            for (int i = 0; attributes != null && i < attributes.getLength(); i++) {
                System.out.println("  " + attributes.getQName(i) + " = '" + attributes.getValue(i) + "'");
            }
        }
        this.namespaceURI = namespace;
        this.name = localPart;
        this.context = context;
        this.startEventIndex = context.getCurrentRecordPos();
        this.recorder = context.getRecorder();

        if (attributes == null) {
            this.attributes = new AttributesImpl();
        } else {
            this.attributes = new AttributesImpl(attributes);
            String rootVal = attributes.getValue(Constants.URI_SOAP_ENC, Constants.ATTR_ROOT);
            if (rootVal != null)
                isRoot = rootVal.equals("1");
            
            id = attributes.getValue(Constants.ATTR_ID);
            // Register this ID with the context.....
            if (id != null) {
                context.registerElementByID(id, this);
            }
            
            href = attributes.getValue(Constants.ATTR_HREF);
            
            // If there's an arrayType attribute, we can pretty well guess that we're an Array???
            if (attributes.getValue(Constants.URI_SOAP_ENC, Constants.ATTR_ARRAY_TYPE) != null)
                typeQName = SOAPTypeMappingRegistry.SOAP_ARRAY;
        }
    }
    
    /** !!! TODO : Make sure this handles multiple targets
     */
    Deserializer fixupDeserializer;
    
    public void setFixupDeserializer(Deserializer dser)
    {
        // !!! Merge targets here if already set?
        fixupDeserializer = dser;
    }
    
    public Deserializer getFixupDeserializer()
    {
        return fixupDeserializer;
    }
    
    public void setEndIndex(int endIndex)
    {
        endEventIndex = endIndex;
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
    
    public SAX2EventRecorder getRecorder() { return recorder; }
    public void setRecorder(SAX2EventRecorder rec) { recorder = rec; }
    
    public Object getValueAsType(QName type)
    {
        // !!! TODO : Implement
        return null;
    }
    
    protected static class QNameAttr {
        QName name;
        QName value;
    }
    
    public void addAttribute(String namespace, String localName,
                             QName value)
    {
        if (qNameAttrs == null)
            qNameAttrs = new Vector();
        
        QNameAttr attr = new QNameAttr();
        attr.name = new QName(namespace, localName);
        attr.value = value;
        
        qNameAttrs.addElement(attr);
    }

    public void setEnvelope(SOAPEnvelope env)
    {
        message = env;
    }
    public SOAPEnvelope getEnvelope()
    {
        return message;
    }
    
    public MessageElement getRealElement()
    {
        if (href == null)
            return this;
        
        Object obj = context.getObjectByRef(href);
        if (obj == null)
            return null;
        
        if (!(obj instanceof MessageElement))
            return null;
        
        return (MessageElement)obj;
    }

    public void publishToHandler(ContentHandler handler) throws SAXException
    {
        if (recorder == null)
            throw new SAXException("No event recorder inside element");
        
        recorder.replay(startEventIndex, endEventIndex, handler);
    }
    
    public void publishContents(ContentHandler handler) throws SAXException
    {
        if (recorder == null)
            throw new SAXException("No event recorder inside element");
        
        recorder.replay(startEventIndex+1, endEventIndex-1, handler);
    }
    
    /** This is the public output() method, which will always simply use
     * the recorded SAX stream for this element if it is available.  If
     * not, this method calls outputImpl() to allow subclasses and
     * programmatically created messages to serialize themselves.
     * 
     * @param context the SerializationContext we will write to.
     */
    public final void output(SerializationContext context) throws Exception
    {
        context.registerPrefixForURI(prefix, namespaceURI);
        //stem.out.println("In output (" + this.getName() + ")");
        if (recorder != null) {
            recorder.replay(startEventIndex, endEventIndex, new SAXOutputter(context));
            return;
        }
        
        outputImpl(context);
    }
    
    /** Subclasses can override
     */
    protected void outputImpl(SerializationContext context) throws Exception
    {
    }
}
