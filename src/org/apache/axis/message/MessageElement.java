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

import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.SerializationContextImpl;
import org.apache.axis.utils.Mapping;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.XMLUtils;

import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.Name;
import javax.xml.namespace.QName;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Iterator;

/*
 * MessageElement is the base type of nodes of the SOAP message parse tree.
 *
 * Note: it was made Serializable to help users of Apache SOAP who had
 * exploited the serializability of the DOM tree to migrate to Axis.
 */
public class MessageElement implements SOAPElement, Serializable
{
    protected static Log log =
        LogFactory.getLog(MessageElement.class.getName());

    private static final Mapping encMapping =
            new Mapping(Constants.URI_DEFAULT_SOAP_ENC,
                        "SOAP-ENC");

    protected String    name ;
    protected String    prefix ;
    protected String    namespaceURI ;
    protected transient AttributesImpl attributes = new AttributesImpl();
    protected String    id;
    protected String    href;
    protected boolean   _isRoot = true;
    protected SOAPEnvelope message = null;
    protected boolean   _isDirty = false;

    protected transient DeserializationContext context;

    protected transient QName typeQName = null;

    protected Vector qNameAttrs = null;

    // Some message representations - as recorded SAX events...
    protected transient SAX2EventRecorder recorder = null;
    protected int startEventIndex = 0;
    protected int startContentsIndex = 0;
    protected int endEventIndex = -1;

    // ...or as DOM
    protected Element elementRep = null;
    protected Text textRep = null;

    protected MessageElement parent = null;

    public ArrayList namespaces = null;

    /** Our encoding style, if any */
    protected String encodingStyle = null;

    /** Object value, possibly supplied by subclass */
    private Object objectValue = null;

    /** No-arg constructor for building messages?
     */
    public MessageElement()
    {
    }

    public MessageElement(String namespace, String localPart)
    {
        namespaceURI = namespace;
        name = localPart;
    }

    public MessageElement(String localPart, String prefix, String namespace)
    {
        this.namespaceURI = namespace;
        this.name = localPart;
        this.prefix = prefix;
    }

    public MessageElement(Name eltName)
    {
        this(eltName.getURI(), eltName.getLocalName());
        prefix = eltName.getPrefix();
    }

    public MessageElement(String namespace, String localPart, Object value)
    {
        this(namespace, localPart);
        objectValue = value;
    }

    public MessageElement(Element elem)
    {
        elementRep = elem;
        namespaceURI = elem.getNamespaceURI();
        name = elem.getLocalName();
    }

    public MessageElement(Text text)
    {
        textRep = text;
        namespaceURI = text.getNamespaceURI();
        name = text.getLocalName();
    }

    public MessageElement(String namespace, String localPart, String qName,
                   Attributes attributes, DeserializationContext context)
    {
        if (log.isDebugEnabled()) {
            log.debug(JavaUtils.getMessage("newElem00", super.toString(), "" + qName));
            for (int i = 0; attributes != null && i < attributes.getLength(); i++) {
                log.debug("  " + attributes.getQName(i) + " = '" + attributes.getValue(i) + "'");
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

            typeQName = context.getTypeFromAttributes(namespace,
                                                      localPart,
                                                      attributes);

            String rootVal = attributes.getValue(Constants.URI_DEFAULT_SOAP_ENC, Constants.ATTR_ROOT);
            if (rootVal != null)
                _isRoot = rootVal.equals("1");

            id = attributes.getValue(Constants.ATTR_ID);
            // Register this ID with the context.....
            if (id != null) {
                context.registerElementByID(id, this);
                if (recorder == null) {
                    recorder = new SAX2EventRecorder();
                    context.setRecorder(recorder);
                }
            }

            href = attributes.getValue(Constants.ATTR_HREF);

            // If there's an arrayType attribute, we can pretty well guess that we're an Array???
            if (attributes.getValue(Constants.URI_DEFAULT_SOAP_ENC, Constants.ATTR_ARRAY_TYPE) != null)
                typeQName = Constants.SOAP_ARRAY;

            // Set the encoding style to the attribute value.  If null,
            // we just automatically use our parent's (see getEncodingStyle)
            encodingStyle =
                    attributes.getValue(Constants.URI_DEFAULT_SOAP_ENC,
                                        Constants.ATTR_ENCODING_STYLE);
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
        //context.setRecorder(null);
    }

    public boolean isDirty() { return _isDirty; }
    public void setDirty(boolean dirty) { _isDirty = dirty; };

    public boolean isRoot() { return _isRoot; }
    public String getID() { return id; }

    public String getHref() { return href; }

    public Attributes getAttributes() { return attributes; }

    /**
     * Obtain an Attributes collection consisting of all attributes
     * for this MessageElement, including namespace declarations.
     *
     * @return Attributes collection
     */
    public Attributes getCompleteAttributes() {
        if (namespaces == null)
            return attributes;

        AttributesImpl attrs = new AttributesImpl(attributes);
        for (Iterator iterator = namespaces.iterator(); iterator.hasNext();) {
            Mapping mapping = (Mapping) iterator.next();
            String prefix = mapping.getPrefix();
            String nsURI = mapping.getNamespaceURI();
            attrs.addAttribute(Constants.NS_URI_XMLNS, prefix,
                               "xmlns:" + prefix, nsURI, "CDATA");
        }
        return attrs;
    }

    public String getName() { return( name ); }
    public void setName(String name) { this.name = name; }

    public String getPrefix() { return( prefix ); }
    public void setPrefix(String prefix) { this.prefix = prefix; }

    public String getNamespaceURI() { return( namespaceURI ); }
    public void setNamespaceURI(String nsURI) { namespaceURI = nsURI; }

    public QName getType() {
        // Try to get the type from our target if we're a reference...
        if (typeQName == null && href != null && context != null) {
            MessageElement referent = context.getElementByID(href);
            if (referent != null) {
                typeQName = referent.getType();
            }
        }
        return typeQName;
    }
    public void setType(QName qName) { typeQName = qName; }

    public SAX2EventRecorder getRecorder() { return recorder; }
    public void setRecorder(SAX2EventRecorder rec) { recorder = rec; }

    /**
     * Get the encoding style.  If ours is null, walk up the hierarchy
     * and use our parent's.  Default if we're the root is "".
     *
     * @return the currently in-scope encoding style
     */
    public String getEncodingStyle() {
        if (encodingStyle == null) {
            if (parent == null)
                return "";
            return parent.getEncodingStyle();
        }
        return encodingStyle;
    }

    /**
     * Sets the encoding style for this <CODE>SOAPElement</CODE>
     * object to one specified. The semantics of a null value,
     * as above in getEncodingStyle() are to just use the parent's value,
     * but null here means set to "".
     *
     * @param   encodingStyle a <CODE>String</CODE>
     *     giving the encoding style
     * @throws  java.lang.IllegalArgumentException  if
     *     there was a problem in the encoding style being set.
     * @see #getEncodingStyle() getEncodingStyle()
     */
    public void setEncodingStyle(String encodingStyle) throws SOAPException {
        if (encodingStyle == null) {
            encodingStyle = "";
        }

        if(!encodingStyle.equals("") && !Constants.isSOAP_ENC(encodingStyle))
                throw new IllegalArgumentException(JavaUtils.getMessage("illegalArgumentException01",encodingStyle));

        this.encodingStyle = encodingStyle;

        // Wherever we set the encoding style, map the SOAP-ENC prefix
        // just for fun.
        if (encodingStyle.equals(Constants.URI_DEFAULT_SOAP_ENC)) {
            addMapping(encMapping);
        }
    }

    private MessageElement getParent() { return parent; }
    private void setParent(MessageElement parent) throws SOAPException
    {
        this.parent = parent;
        if (parent != null) {
            parent.addChild(this);
        }
    }

    private ArrayList children = null;

    /**
     * Note that this method will log a error and no-op if there is
     * a value (set using setObjectValue) in the MessageElement.
     */
    public void addChild(MessageElement el) throws SOAPException
    {
        if (objectValue != null) {
            SOAPException exc = new SOAPException(JavaUtils.
                                                  getMessage("valuePresent"));
            log.error(JavaUtils.getMessage("valuePresent"), exc);
            throw exc;
        }
        if (children == null)
            children = new ArrayList();
        children.add(el);
        el.parent = this;
    }

    /**
     * Remove a child element.
     */
    private void removeChild(MessageElement child) {
        // Remove all occurrences in case it has been added multiple times.
        int i;
        while ((i = children.indexOf(child)) != -1) {
            children.remove(i);
        }
    }

    public ArrayList getChildren()
    {
        return children;
    }

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

        if (log.isDebugEnabled()) {
            log.debug(JavaUtils.getMessage("noPrefix00", "" + this, prefix));
        }

        return null;
    }

    /**
     * Returns value of the node as an object of registered type.
     * @return Object of proper type, or null if no mapping could be found.
     */
    public Object getObjectValue(){
        if (objectValue == null) {
            try {
                objectValue = getValueAsType(getType());
            } catch (Exception e) {
                log.debug("getValue()", e);
            }
        }
        return objectValue;
    }

    /**
     * Sets value of this node to an Object.
     * A serializer needs to be registered for this object class for proper
     * operation.
     * <p>
     * Note that this method will log an error and no-op if there are
     * any children in the MessageElement or if the MessageElement was
     * constructed from XML.
     * @param newValue node's value or null.
     */
    public void setObjectValue(Object newValue) throws SOAPException {
        if (children != null && !children.isEmpty()) {
            SOAPException exc = new SOAPException(JavaUtils.
                                                  getMessage("childPresent"));
            log.error(JavaUtils.getMessage("childPresent"), exc);
            throw exc;
        }
        if (elementRep != null) {
            SOAPException exc = new SOAPException(JavaUtils.
                                                  getMessage("xmlPresent"));
            log.error(JavaUtils.getMessage("xmlPresent"), exc);
            throw exc;
        }
        if (textRep != null) {
            SOAPException exc = new SOAPException(JavaUtils.
                                                  getMessage("xmlPresent"));
            log.error(JavaUtils.getMessage("xmlPresent"), exc);
            throw exc;
        }
        this.objectValue = newValue;
    }

    public Object getValueAsType(QName type) throws Exception
    {
        if (context == null)
            throw new Exception(JavaUtils.getMessage("noContext00"));

        Deserializer dser = context.getDeserializerForType(type);
        if (dser == null)
            throw new Exception(JavaUtils.getMessage("noDeser00", "" + type));

        context.pushElementHandler(new EnvelopeHandler((SOAPHandler)dser));

        publishToHandler((org.xml.sax.ContentHandler) context);

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

    /**
     * Set an attribute, adding the attribute if it isn't already present
     * in this element, and changing the value if it is.  Passing null as the
     * value will cause any pre-existing attribute by this name to go away.
     */
    public void setAttribute(String namespace, String localName,
                             String value)
    {
        if (attributes != null) {
            int idx = attributes.getIndex(namespace, localName);
            if (idx > -1) {
                // Got it, so replace it's value.
                if (value != null) {
                    attributes.setValue(idx, value);
                } else {
                    attributes.removeAttribute(idx);
                }
                return;
            }
        } else if (value != null) {
            attributes = new AttributesImpl();
        }

        addAttribute(namespace, localName, value);
    }

    public String getAttributeValue(String localName)
    {
        if (attributes == null) {
           return null;
        }
        return attributes.getValue(localName);
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

    public Document getAsDocument() throws Exception
    {
        String elementString = getAsString();

        Reader reader = new StringReader(elementString);
        Document doc = XMLUtils.newDocument(new InputSource(reader));
        if (doc == null)
            throw new Exception(
                    JavaUtils.getMessage("noDoc00", elementString));
        return doc;
    }

    private String getAsString() throws Exception {
        SerializationContext serializeContext = null;
        StringWriter writer = new StringWriter();
        MessageContext msgContext;
        if (context != null) {
            msgContext = context.getMessageContext();
        } else {
            msgContext = MessageContext.getCurrentContext();
        }
        serializeContext = new SerializationContextImpl(writer, msgContext);
        serializeContext.setSendDecl(false);
        output(serializeContext);
        writer.close();

        return writer.getBuffer().toString();
    }

    public Element getAsDOM() throws Exception
    {
        return getAsDocument().getDocumentElement();
    }

    public void publishToHandler(ContentHandler handler) throws SAXException
    {
        if (recorder == null)
            throw new SAXException(JavaUtils.getMessage("noRecorder00"));

        recorder.replay(startEventIndex, endEventIndex, handler);
    }

    public void publishContents(ContentHandler handler) throws SAXException
    {
        if (recorder == null)
            throw new SAXException(JavaUtils.getMessage("noRecorder00"));

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
            recorder.replay(startEventIndex,
                            endEventIndex,
                            new SAXOutputter(context));
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

        /**
         * Write the encoding style attribute IF it's different from
         * whatever encoding style is in scope....
         */
        if (encodingStyle != null) {
            MessageContext mc = context.getMessageContext();
            SOAPConstants soapConstants = (mc != null) ?
                                            mc.getSOAPConstants() :
                                            SOAPConstants.SOAP11_CONSTANTS;
            if (parent == null) {
                // don't emit an encoding style if its "" (literal)
                if (!encodingStyle.equals("")) {
                    setAttribute(soapConstants.getEnvelopeURI(),
                                 Constants.ATTR_ENCODING_STYLE,
                                 encodingStyle);
                }
            } else if (!encodingStyle.equals(parent.getEncodingStyle())) {
                setAttribute(soapConstants.getEnvelopeURI(),
                             Constants.ATTR_ENCODING_STYLE,
                             encodingStyle);
            }
        }

        outputImpl(context);
    }

    /** Subclasses can override
     */
    protected void outputImpl(SerializationContext context) throws Exception
    {
        if (elementRep != null) {
            boolean oldPretty = context.getPretty();
            context.setPretty(false);
            context.writeDOMElement(elementRep);
            context.setPretty(oldPretty);
            return;
        }

        if (textRep != null) {
            boolean oldPretty = context.getPretty();
            context.setPretty(false);
            context.writeSafeString(((Text)textRep).getData());
            context.setPretty(oldPretty);
            return;
        }

        if (prefix != null)
            context.registerPrefixForURI(prefix, namespaceURI);

        if (objectValue != null) {
            context.serialize(new QName(namespaceURI, name),
                              attributes,
                              objectValue);
            return;
        }

        context.startElement(new QName(namespaceURI, name), attributes);
        if (children != null) {
            for (Iterator it = children.iterator(); it.hasNext();) {
                ((MessageElement)it.next()).output(context);
            }
        }
        context.endElement();
    }

    public String toString() {
        try {
            return getAsString();
        }
        catch( Exception exp ) {
            log.error(JavaUtils.getMessage("exception00"), exp);
            return null;
        }
    }

    public void addMapping(Mapping map) {
        if (namespaces == null) namespaces = new ArrayList();
        namespaces.add(map);
    }

    /*
     * Handle transient fields.
     * NB. order of writes in writeObject must match order of reads in
     * readObject.
     */

    private void writeObject(ObjectOutputStream out) throws IOException {
        if (typeQName == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            out.writeObject(typeQName.getNamespaceURI());
            out.writeObject(typeQName.getLocalPart());
        }

        if (attributes == null) {
            attributes = new AttributesImpl();
        }
        int n = attributes.getLength();
        out.writeInt(n);
        for (int i = 0; i < n; i++) {
            out.writeObject(attributes.getLocalName(i));
            out.writeObject(attributes.getQName(i));
            out.writeObject(attributes.getURI(i));
            out.writeObject(attributes.getType(i));
            out.writeObject(attributes.getValue(i));
        }
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {

        if (in.readBoolean()) {
            typeQName = new QName((String)in.readObject(),
                                  (String)in.readObject());
        } else {
            typeQName = null;
        }

        attributes = new AttributesImpl();
        int n = in.readInt();
        for (int i = 0; i < n; i++) {
            String localName = (String)in.readObject();
            String qName = (String)in.readObject();
            String uri = (String)in.readObject();
            String type = (String)in.readObject();
            String value = (String)in.readObject();
            attributes.addAttribute(uri, localName, qName, type, value);
        }
        in.defaultReadObject();
    }

    // JAXM Node methods...
    /**
     * Returns the the value of the immediate child of this <code>Node</code>
     * object if a child exists and its value is text.
     * @return  a <code>String</code> with the text of the immediate child of
     *    this <code>Node</code> object if (1) there is a child and
     *    (2) the child is a <code>Text</code> object;
     *      <code>null</code> otherwise
     */
    public String getValue() {
        try {
            Element element = getAsDOM();
            if(element.hasChildNodes()){
                org.w3c.dom.Node node = element.getFirstChild();
                if(node.getNodeType()==org.w3c.dom.Node.TEXT_NODE){
                    return node.getNodeValue();
                }
            }
        } catch (Exception t){
            log.debug("getValue()", t);
        }
        return null;
    }

    public void setParentElement(SOAPElement parent) throws SOAPException {
        if(parent == null)
            throw new IllegalArgumentException(JavaUtils.getMessage("nullParent00"));
        try {
            setParent((MessageElement)parent);
        } catch (Throwable t) {
            throw new SOAPException(t);
        }
    }

    public SOAPElement getParentElement() {
        return getParent();
    }

    /**
     * Break the relationship between this element and its parent, if any.
     */
    public void detachNode() {
        if (parent != null) {
            parent.removeChild(this);
            parent = null;
        }
    }

    /**
     * No-opped - Axis does not recycle nodes.
     */
    public void recycleNode() {}

    // JAXM SOAPElement methods...

    public SOAPElement addChildElement(Name name) throws SOAPException {
        MessageElement child = new MessageElement(name.getURI(),
                                                  name.getLocalName());
        addChild(child);
        return child;
    }

    public SOAPElement addChildElement(String localName) throws SOAPException {
        // Inherit parent's namespace
        MessageElement child = new MessageElement(getNamespaceURI(),
                                                  localName);
        addChild(child);
        return child;
    }

    public SOAPElement addChildElement(String localName,
                                       String prefix) throws SOAPException {
        MessageElement child = new MessageElement(getNamespaceURI(prefix),
                                                  localName);
        child.setPrefix(prefix);
        addChild(child);
        return child;
    }

    public SOAPElement addChildElement(String localName,
                                       String prefix,
                                       String uri) throws SOAPException {
            MessageElement child = new MessageElement(uri, localName);
            child.setPrefix(prefix);
            child.addNamespaceDeclaration(prefix, uri);
            addChild(child);
            return child;
    }

    /**
     * The added child must be an instance of MessageElement rather than
     * an abitrary SOAPElement otherwise a (wrapped) ClassCastException
     * will be thrown.
     */
    public SOAPElement addChildElement(SOAPElement element)
        throws SOAPException {
        try {
            addChild((MessageElement)element);
            return element;
        } catch (ClassCastException e) {
            throw new SOAPException(e);
        }
    }

    /**
     * Text nodes are not supported.
     */
    public SOAPElement addTextNode(String s) throws SOAPException {
        org.apache.axis.message.Text text = new org.apache.axis.message.Text(s);
        try {
            addChild((MessageElement)text);
            return this;
        } catch (ClassCastException e) {
            throw new SOAPException(e);
        }
    }

    public SOAPElement addAttribute(Name name, String value)
        throws SOAPException {
        try {
            addAttribute(name.getURI(), name.getLocalName(), value);
        } catch (RuntimeException t) {
            throw new SOAPException(t);
        }
        return this;
    }

    public SOAPElement addNamespaceDeclaration(String prefix,
                                               String uri)
        throws SOAPException {
        try {
            Mapping map = new Mapping(uri, prefix);
            addMapping(map);
        } catch (RuntimeException t) {
            throw new SOAPException(t);
        }
        return this;
    }

    public String getAttributeValue(Name name) {
        return attributes.getValue(name.getURI(), name.getLocalName());
    }

    public Iterator getAllAttributes() {
        int num = attributes.getLength();
        Vector attrs = new Vector(num);
        for (int i = 0; i < num; i++) {
            String q = attributes.getQName(i);
            String prefix = "";
            if (q != null) {
                int idx = q.indexOf(":");
                if (idx > 0)
                    prefix = q.substring(0, idx);
            }

            attrs.add(new PrefixedQName(attributes.getURI(i),
                                        attributes.getLocalName(i),
                                        prefix));
        }
        return attrs.iterator();
    }

    // getNamespaceURI implemented above

    public Iterator getNamespacePrefixes() {
        int num = namespaces.size();
        Vector prefixes = new Vector(num);
        for (int i = 0; i < num; i++) {
            prefixes.add(((Mapping)namespaces.get(i)).getPrefix());
        }
        return prefixes.iterator();
    }

    public Name getElementName() {
        return new PrefixedQName(getNamespaceURI(), getName(), getPrefix());
    }

    public boolean removeAttribute(Name name) {
        boolean removed = false;

        for (int i = 0; i < attributes.getLength() && !removed; i++) {
            if (attributes.getURI(i).equals(name.getURI()) &&
                attributes.getLocalName(i).equals(name.getLocalName())) {
                attributes.removeAttribute(i);
                removed = true;
            }
        }
        return removed;
    }

    public boolean removeNamespaceDeclaration(String prefix) {
        boolean removed = false;

        for (int i = 0; i < namespaces.size() && !removed; i++) {
            if (((Mapping)namespaces.get(i)).getPrefix().equals(prefix)) {
                namespaces.remove(i);
                removed = true;
            }
        }
        return removed;
    }

    public Iterator getChildElements() {
        if (children == null)
            children = new ArrayList();
        return children.iterator();
    }

    public Iterator getChildElements(Name name) {
        if (children == null)
            children = new ArrayList();
        int num = children.size();

        Vector c = new Vector(num);
        for (int i = 0; i < num; i++) {
            MessageElement child = (MessageElement)children.get(i);
            Name cname = child.getElementName();
            if (cname.getURI().equals(name.getURI()) &&
                cname.getLocalName().equals(name.getLocalName())) {
                c.add(child);
            }
        }
        return c.iterator();
    }

    // setEncodingStyle implemented above

    // getEncodingStyle() implemented above
}
