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


import org.apache.axis.client.AxisClient;
import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.SOAPTypeMappingRegistry;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.utils.Mapping;
import org.apache.axis.utils.QName;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Vector;

public class MessageElement
{
    private static final boolean DEBUG_LOG = false;

    protected String    name ;
    protected String    prefix ;
    protected String    namespaceURI ;
    protected AttributesImpl attributes;
    protected String    id;
    protected String    href;
    protected boolean   _isRoot = true;
    protected SOAPEnvelope message = null;
    protected boolean   _isDirty = false;

    protected DeserializationContext context;

    protected QName typeQName = null;

    protected Vector qNameAttrs = null;

    // Some message representations - as recorded SAX events...
    protected SAX2EventRecorder recorder = null;
    protected int startEventIndex = 0;
    protected int startContentsIndex = 0;
    protected int endEventIndex = -1;

    // ...or as DOM
    protected Element elementRep = null;

    protected MessageElement parent = null;
    // Do we need links to our children too?

    public ArrayList namespaces = null;

    /** No-arg constructor for building messages?
     */
    public MessageElement()
    {
    }

    MessageElement(String namespace, String localPart)
    {
        namespaceURI = namespace;
        name = localPart;
    }

    MessageElement(Element elem)
    {
        elementRep = elem;
        namespaceURI = elem.getNamespaceURI();
        name = elem.getTagName();
    }

    public MessageElement(String namespace, String localPart, String qName,
                   Attributes attributes, DeserializationContext context)
    {
        if (DEBUG_LOG) {
            System.out.println("New MessageElement (" + this + ") named " + qName);
            for (int i = 0; attributes != null && i < attributes.getLength(); i++) {
                System.out.println("  " + attributes.getQName(i) + " = '" + attributes.getValue(i) + "'");
            }
        }
        this.namespaceURI = namespace;
        this.name = localPart;

        int idx = qName.indexOf(":");
        if (idx > 0)
            this.prefix = qName.substring(0, idx);

        this.context = context;
        this.startEventIndex = context.getStartOfMappingsPos();

        setNSMappings(context.getCurrentNSMappings());

        this.recorder = context.getRecorder();

        if (attributes == null) {
            this.attributes = new AttributesImpl();
        } else {
            this.attributes = new AttributesImpl(attributes);
            String rootVal = attributes.getValue(Constants.URI_SOAP_ENC, Constants.ATTR_ROOT);
            if (rootVal != null)
                _isRoot = rootVal.equals("1");

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

    public boolean isDirty() { return _isDirty; }
    public void setDirty(boolean dirty) { _isDirty = dirty; };

    public boolean isRoot() { return _isRoot; }
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

    public MessageElement getParent() { return parent; }
    public void setParent(MessageElement parent) { this.parent = parent; }

    public void setContentsIndex(int index)
    {
        startContentsIndex = index;
    }

    public void setNSMappings(ArrayList namespaces)
    {
        this.namespaces = namespaces;
    }

    public String getPrefix(String namespaceURI) {
        if ((namespaceURI == null) || (namespaceURI.equals("")))
            return null;

        if (href != null) {
            return getRealElement().getPrefix(namespaceURI);
        }

        if (namespaces != null) {
            for (int i = 0; i < namespaces.size(); i++) {
                Mapping map = (Mapping)namespaces.get(i);
                if (map.getNamespaceURI().equals(namespaceURI))
                    return map.getPrefix();
            }
        }

        if (parent != null)
            return parent.getPrefix(namespaceURI);

        return null;
    }

    public String getNamespaceURI(String prefix) {
        if (prefix == null)
            prefix = "";

        if (href != null) {
            return getRealElement().getNamespaceURI(prefix);
        }

        if (namespaces != null) {
            for (int i = 0; i < namespaces.size(); i++) {
                Mapping map = (Mapping)namespaces.get(i);
                if (map.getPrefix().equals(prefix)) {
                    return map.getNamespaceURI();
                }
            }
        }

        if (parent != null)
            return parent.getNamespaceURI(prefix);

        if (DEBUG_LOG) {
            System.err.println(this + " didn't find prefix '" + prefix + "'");
        }

        return null;
    }

    public Object getValueAsType(QName type) throws Exception
    {
        if (context == null)
            throw new Exception(
             "No deserialization context to use in getValueAsType()!");

        TypeMappingRegistry tmr = context.getTypeMappingRegistry();
        Deserializer dser = tmr.getDeserializer(type);
        if (dser == null)
            throw new Exception("No deserializer for requested type " +
                                type);

        context.pushElementHandler(new EnvelopeHandler(dser));

        publishToHandler(context);

        return dser.getValue();
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
        // !!! Add attribute to attributes!
    }

    public void addAttribute(String namespace, String localName,
                             String value)
    {
        if (attributes == null) {
            attributes = new AttributesImpl();
        }
        attributes.addAttribute(namespace, localName, "", "CDATA",
                                value);
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

    public Element getAsDOM() throws Exception
    {
        MessageContext msgContext = context.getMessageContext();

        StringWriter writer = new StringWriter();
        output(new SerializationContext(writer, msgContext));
        writer.close();

        Reader reader = new StringReader(writer.getBuffer().toString());
        Document doc = XMLUtils.newDocument(new InputSource(reader));
        if (doc == null)
            throw new Exception("Couldn't get DOM document: XML was \"" +
                                writer.getBuffer().toString() + "\"");

        return doc.getDocumentElement();
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

        recorder.replay(startContentsIndex, endEventIndex-1, handler);
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
        if ((recorder != null) && (!_isDirty)) {
            recorder.replay(startEventIndex, endEventIndex, new SAXOutputter(context));
            return;
        }

        // Turn QName attributes into strings
        if (qNameAttrs != null) {
            for (int i = 0; i < qNameAttrs.size(); i++) {
                QNameAttr attr = (QNameAttr)qNameAttrs.get(i);
                QName attrName = attr.name;
                addAttribute(attrName.getNamespaceURI(),
                             attrName.getLocalPart(),
                             context.qName2String(attr.value));
            }
            qNameAttrs = null;
        }

        outputImpl(context);
    }

    /** Subclasses can override
     */
    protected void outputImpl(SerializationContext context) throws Exception
    {
        if (elementRep != null) {
            context.writeDOMElement(elementRep);
            return;
        }

        if (prefix != null)
            context.registerPrefixForURI(prefix, namespaceURI);

        context.startElement(new QName(namespaceURI, name), attributes);
        context.endElement();
    }

    public String toString() {
        try {
            StringWriter  writer = new StringWriter();
            SerializationContext context = null ;
            AxisClient     tmpEngine = new AxisClient(null);
            tmpEngine.addOption(tmpEngine.PROP_XML_DECL, new Boolean(false));
            MessageContext msgContext = new MessageContext(tmpEngine);
            context = new SerializationContext(writer, msgContext);
            this.output(context);
            return( writer.toString() );
        }
        catch( Exception exp ) {
            exp.printStackTrace();
            return( null );
        }
    }
}
