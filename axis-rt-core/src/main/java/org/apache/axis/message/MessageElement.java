/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.axis.message;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.TextSerializationContext;
import org.apache.axis.constants.Style;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.Mapping;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.namespace.QName;
import javax.xml.rpc.encoding.TypeMapping;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * MessageElement is the base type of nodes of the SOAP message parse tree.
 *
 * Note: it was made Serializable to help users of Apache SOAP who had
 * exploited the serializability of the DOM tree to migrate to Axis.
 */
// TODO: implement the NodeList methods properly, with tests. 
public class MessageElement extends NodeImpl implements SOAPElement,
        Serializable,
        org.w3c.dom.NodeList,  // ADD Nodelist Interfaces for SAAJ 1.2
        Cloneable
{
    protected static Log log =
        LogFactory.getLog(MessageElement.class.getName());

    private static final Mapping enc11Mapping =
            new Mapping(Constants.URI_SOAP11_ENC,
                        "SOAP-ENC");

    private static final Mapping enc12Mapping =
            new Mapping(Constants.URI_SOAP12_ENC,
                        "SOAP-ENC");

    protected String    id;
    protected String    href;
    protected boolean   _isRoot = true;
    protected SOAPEnvelope message = null;

    protected transient DeserializationContext context;

    protected transient QName typeQName = null;

    protected Vector qNameAttrs = null;

    // Some message representations - as recorded SAX events...
    protected transient SAX2EventRecorder recorder = null;
    protected int startEventIndex = 0;
    protected int startContentsIndex = 0;
    protected int endEventIndex = -1;

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

    /**
     * constructor
     * @param namespace namespace of element
     * @param localPart local name
     */
    public MessageElement(String namespace, String localPart)
    {
        namespaceURI = namespace;
        name = localPart;
    }

    /**
     * constructor. Automatically adds a namespace-prefix mapping to the mapping table
     * @param localPart local name
     * @param prefix prefix
     * @param namespace namespace
     */
    public MessageElement(String localPart, String prefix, String namespace)
    {
        this.namespaceURI = namespace;
        this.name = localPart;
        this.prefix = prefix;
        addMapping(new Mapping(namespace, prefix));
    }

    /**
     * construct using a {@link javax.xml.soap.Name} implementation,
     * @see #MessageElement(String, String, String)
     * @param eltName
     */
    public MessageElement(Name eltName)
    {
        this(eltName.getLocalName(),eltName.getPrefix(), eltName.getURI());
    }

    /**
     * constructor binding the internal object value field to the
     * value parameter
     * @param namespace namespace of the element
     * @param localPart local name
     * @param value value of the node
     */
    public MessageElement(String namespace, String localPart, Object value)
    {
        this(namespace, localPart);
        objectValue = value;
    }

    /**
     * constructor declaring the qualified name of the node
     * @param name naming information
     */
    public MessageElement(QName name)
    {
        this(name.getNamespaceURI(), name.getLocalPart());
    }

    /**
     * constructor declaring the qualified name of the node
     * and its value
     * @param name naming information
     * @param value value of the node
     */
    public MessageElement(QName name, Object value)
    {
        this(name.getNamespaceURI(), name.getLocalPart());
        objectValue = value;
    }

    /**
     * create a node through a deep copy of the passed in element.
     * @param elem name to copy from
     */
    public MessageElement(Element elem)
    {
        namespaceURI = elem.getNamespaceURI();
        name = elem.getLocalName();
        copyNode(elem);
    }

    /**
     * construct a text element.
     * @param text text data. This is <i>not</i> copied; it is referred to in the MessageElement.
     */
    public MessageElement(CharacterData text)
    {
        textRep = text;
        namespaceURI = text.getNamespaceURI();
        name = text.getLocalName();
    }

    /**
     * Advanced constructor used for deserialization.
     * <ol>
     * <li>The context provides the mappings and Sax event recorder
     * <li>The soap messaging style is determined from the current message context, defaulting
     * to SOAP1.1 if there is no current context.
     * <li>if there is an id attribute (any namespace), then the ID is registered
     * with {@link DeserializationContext#registerElementByID(String, MessageElement)} ;a  new recorder is
     * created if needed.
     * <li>If there is an attribute "root" in the default SOAP namespace, then it is examined
     * to see if it marks the element as root (value=="1" or not)
     * <li>If there is an arrayType attribute then we assume we are an array and set our
     * {@link #typeQName} field appropriately.
     * <li>The {@link #href} field is set if there is a relevant href value
     * </ol>
     *
     * @param namespace namespace namespace of element
     * @param localPart local name local name of element
     * @param prefix prefix prefix of element
     * @param attributes attributes to save as our attributes
     * @param context deserialization context for this message element
     * @throws AxisFault if the encoding style is not recognized/supported
     */
    public MessageElement(String namespace, String localPart, String prefix,
                   Attributes attributes, DeserializationContext context)
        throws AxisFault
    {
        if (log.isDebugEnabled()) {
            log.debug(Messages.getMessage("newElem00", super.toString(),
                                            "{" + prefix + "}" + localPart));
            for (int i = 0; attributes != null && i < attributes.getLength(); i++) {
                log.debug("  " + attributes.getQName(i) + " = '" + attributes.getValue(i) + "'");
            }
        }
        this.namespaceURI = namespace;
        this.name = localPart;
        this.prefix = prefix;

        this.context = context;
        this.startEventIndex = context.getStartOfMappingsPos();

        setNSMappings(context.getCurrentNSMappings());

        this.recorder = context.getRecorder();

        if (attributes != null && attributes.getLength() > 0) {
            this.attributes = attributes;

            this.typeQName = context.getTypeFromAttributes(namespace,
                                                      localPart,
                                                      attributes);

            String rootVal = attributes.getValue(Constants.URI_DEFAULT_SOAP_ENC, Constants.ATTR_ROOT);

            if (rootVal != null) {
                _isRoot = "1".equals(rootVal);
            }

            id = attributes.getValue(Constants.ATTR_ID);
            // Register this ID with the context.....
            if (id != null) {
                context.registerElementByID(id, this);
                if (recorder == null) {
                    recorder = new SAX2EventRecorder();
                    context.setRecorder(recorder);
                }
            }

            // Set the encoding style to the attribute value.  If null,
            // we just automatically use our parent's (see getEncodingStyle)
            MessageContext mc = context.getMessageContext();
            SOAPConstants sc = (mc != null) ?
                                            mc.getSOAPConstants() :
                                            SOAPConstants.SOAP11_CONSTANTS;

            href = attributes.getValue(sc.getAttrHref());

            // If there's an arrayType attribute, we can pretty well guess that we're an Array???
            if (attributes.getValue(Constants.URI_DEFAULT_SOAP_ENC, Constants.ATTR_ARRAY_TYPE) != null) {
                typeQName = Constants.SOAP_ARRAY;
            }


            encodingStyle =
                    attributes.getValue(sc.getEncodingURI(),
                                        Constants.ATTR_ENCODING_STYLE);

            // if no-encoding style was defined, we don't define as well
            if (Constants.URI_SOAP12_NOENC.equals(encodingStyle))
                encodingStyle = null;

            // If we have an encoding style, and are not a MESSAGE style
            // operation (in other words - we're going to do some data
            // binding), AND we're SOAP 1.2, check the encoding style against
            // the ones we've got type mappings registered for.  If it isn't
            // registered, throw a DataEncodingUnknown fault as per the
            // SOAP 1.2 spec.
            if (encodingStyle != null &&
                    sc.equals(SOAPConstants.SOAP12_CONSTANTS) &&
                    (mc.getOperationStyle() != Style.MESSAGE)) {
                TypeMapping tm = mc.getTypeMappingRegistry().
                        getTypeMapping(encodingStyle);
                if (tm == null ||
                        (tm.equals(mc.getTypeMappingRegistry().
                                                getDefaultTypeMapping()))) {
                    AxisFault badEncodingFault = new AxisFault(
                            Constants.FAULT_SOAP12_DATAENCODINGUNKNOWN,
                            "bad encoding style", null, null);
                    throw badEncodingFault;
                }
            }

        }
    }

    /**
     * Retrieve the DeserializationContext associated with this MessageElement
     *
     * @return The DeserializationContext associated with this MessageElement
     */
    public DeserializationContext getDeserializationContext()
    {
        return context;
    }

    /** !!! TODO : Make sure this handles multiple targets
     */
    protected Deserializer fixupDeserializer;

    public void setFixupDeserializer(Deserializer dser)
    {
        // !!! Merge targets here if already set?
        fixupDeserializer = dser;
    }

    public Deserializer getFixupDeserializer()
    {
        return fixupDeserializer;
    }

    /**
     * record the end index of the SAX recording.
     * @param endIndex end value
     */
    public void setEndIndex(int endIndex)
    {
        endEventIndex = endIndex;
        //context.setRecorder(null);
    }

    /**
     * get the is-root flag
     * @return true if the element is considered a document root.
     */
    public boolean isRoot() { return _isRoot; }

    /**
     * get a saved ID
     * @return ID or null for no ID
     */
    public String getID() { return id; }

    /**
     * get a saved href
     * @return href or null
     */
    public String getHref() { return href; }

    /**
     * get the attributes
     * @return attributes. If this equals {@link NullAttributes#singleton} it is null
     *
     */
    public Attributes getAttributesEx() { return attributes; }


    /**
     * Returns a duplicate of this node, i.e., serves as a generic copy
     * constructor for nodes. The duplicate node has no parent; (
     * <code>parentNode</code> is <code>null</code>.).
     * <br>Cloning an <code>Element</code> copies all attributes and their
     * values, including those generated by the XML processor to represent
     * defaulted attributes, but this method does not copy any text it
     * contains unless it is a deep clone, since the text is contained in a
     * child <code>Text</code> node. Cloning an <code>Attribute</code>
     * directly, as opposed to be cloned as part of an <code>Element</code>
     * cloning operation, returns a specified attribute (
     * <code>specified</code> is <code>true</code>). Cloning any other type
     * of node simply returns a copy of this node.
     * <br>Note that cloning an immutable subtree results in a mutable copy,
     * but the children of an <code>EntityReference</code> clone are readonly
     * . In addition, clones of unspecified <code>Attr</code> nodes are
     * specified. And, cloning <code>Document</code>,
     * <code>DocumentType</code>, <code>Entity</code>, and
     * <code>Notation</code> nodes is implementation dependent.
     *
     * @param deep If <code>true</code>, recursively clone the subtree under
     *             the specified node; if <code>false</code>, clone only the node
     *             itself (and its attributes, if it is an <code>Element</code>).
     * @return The duplicate node.
     */
    public Node cloneNode(boolean deep) {
        try{
            MessageElement clonedSelf = (MessageElement) cloning();

            if(deep){
                if(children != null){
                    for(int i =0; i < children.size(); i++){
                        NodeImpl child = (NodeImpl)children.get(i);
                        if(child != null) {  // why child can be null?
                            NodeImpl clonedChild = (NodeImpl)child.cloneNode(deep); // deep == true
                            clonedChild.setParent(clonedSelf);
                            clonedChild.setOwnerDocument(getOwnerDocument());
                            
                            clonedSelf.childDeepCloned( child, clonedChild );
                        }
                    }
                }
            }
            return clonedSelf;
        }
        catch(Exception e){
            return null;
        }
    }

    // Called when a child is cloned from cloneNode().
    //
    // This is used by sub-classes to update internal state when specific elements
    // are cloned.
    protected void childDeepCloned( NodeImpl oldNode, NodeImpl newNode )
    {
    }

    /**
     *  protected clone method (not public)
     *
     *  copied status
     *  -------------------
     *  protected String    name ;             Y
     *  protected String    prefix ;           Y
     *  protected String    namespaceURI ;     Y
     *  protected transient Attributes attributes  Y
     *  protected String    id;               Y?
     *  protected String    href;             Y?
     *  protected boolean   _isRoot = true;   Y?
     *  protected SOAPEnvelope message = null; N?
     *  protected transient DeserializationContext context;  Y?
     *  protected transient QName typeQName = null;          Y?
     *  protected Vector qNameAttrs = null;                  Y?
     *  protected transient SAX2EventRecorder recorder = null; N?
     *  protected int startEventIndex = 0;                   N?
     *  protected int startContentsIndex = 0;                N?
     *  protected int endEventIndex = -1;                    N?
     *  protected CharacterData textRep = null;             Y?
     *  protected MessageElement parent = null;             N
     *  public ArrayList namespaces = null;                 Y
     *  protected String encodingStyle = null;              N?
     *   private Object objectValue = null;                 N?
     *
     * @return
     * @throws CloneNotSupportedException
     */
    protected Object cloning() throws CloneNotSupportedException
    {
        try{
            MessageElement clonedME = null;
            clonedME = (MessageElement)this.clone();

            clonedME.setName(name);
            clonedME.setNamespaceURI(namespaceURI);
            clonedME.setPrefix(prefix);

            // new AttributesImpl will copy all data not set referencing only
            clonedME.setAllAttributes(new AttributesImpl(attributes));
            //       clonedME.addNamespaceDeclaration((namespaces.clone()); // cannot do this. since we cannot access the namepace arraylist

            clonedME.namespaces = new ArrayList();
            if(namespaces != null){
                for(int i = 0; i < namespaces.size(); i++){
                    //     jeus.util.Logger.directLog( " Debug :  namspace.size() = " + namespaces.size());
                    Mapping namespace = (Mapping)namespaces.get(i);
                    clonedME.addNamespaceDeclaration(namespace.getPrefix(), namespace.getNamespaceURI()); // why exception here!!
                }
            }
            clonedME.children = new ArrayList();

            // clear parents relationship to old parent
            clonedME.parent = null;
            // clonedME.setObjectValue(objectValue); // how to copy this???
            clonedME.setDirty(this._isDirty);
            if(encodingStyle != null){
                clonedME.setEncodingStyle(encodingStyle);
            }
            return clonedME;
        }catch(Exception ex){
            return null;
        }
    }


    /**
     * set all the attributes of this instance
     * @param attrs a new attributes list
     */
    public void setAllAttributes(Attributes attrs){
        attributes = attrs;
    }

    /**
     * remove all children.
     */
    public void detachAllChildren()
    {
        removeContents();
    }

    /**
     * Obtain an Attributes collection consisting of all attributes
     * for this MessageElement, including namespace declarations.
     *
     * @return Attributes collection
     */
    public Attributes getCompleteAttributes() {
        if (namespaces == null) {
            return attributes;
        }

        AttributesImpl attrs = null;
        if (attributes == NullAttributes.singleton) {
            attrs = new AttributesImpl();
        } else {
            attrs = new AttributesImpl(attributes);
        }

        for (Iterator iterator = namespaces.iterator(); iterator.hasNext();) {
            Mapping mapping = (Mapping) iterator.next();
            String prefix = mapping.getPrefix();
            String nsURI = mapping.getNamespaceURI();
            attrs.addAttribute(Constants.NS_URI_XMLNS, prefix,
                               "xmlns:" + prefix, nsURI, "CDATA");
        }
        return attrs;
    }

    /**
     * get the local name of this element
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * set the local part of this element's name
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * get the fully qualified name of this element
     * @return a QName describing the name of thsi element
     */
    public QName getQName() {
        return new QName(namespaceURI, name);
    }

    /**
     * set the name and namespace of this element
     * @param qName qualified name
     */
    public void setQName(QName qName) {
        this.name = qName.getLocalPart();
        this.namespaceURI = qName.getNamespaceURI();
    }

    /**
     * set the namespace URI of the element
     * @param nsURI new namespace URI
     */
    public void setNamespaceURI(String nsURI) {
        namespaceURI = nsURI;
    }

    /**
     * get the element's type.
     * If we are a reference, we look up our target in the context and
     * return (and cache) its type.
     * @return
     */
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

    /**
     * set the element's type
     * @param qname
     */
    public void setType(QName qname) {
        typeQName = qname;
    }

    /**
     * get the event recorder
     * @return recorder or null
     */
    public SAX2EventRecorder getRecorder() {
        return recorder;
    }

    /**
     * set the event recorder
     * @param rec
     */
    public void setRecorder(SAX2EventRecorder rec) {
        recorder = rec;
    }

    /**
     * Get the encoding style.  If ours is null, walk up the hierarchy
     * and use our parent's.  Default if we're the root is "".
     *
     * @return the currently in-scope encoding style
     */
    public String getEncodingStyle() {
        if (encodingStyle == null) {
            if (parent == null) {
                return "";
            }
            return ((MessageElement) parent).getEncodingStyle();
        }
        return encodingStyle;
    }

    /**
     * remove all chidlren.
     * All SOAPExceptions which can get thrown in this process are ignored.
     */
    public void removeContents() {
        // unlink
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                try {
                    ((NodeImpl) children.get(i)).setParent(null);
                } catch (SOAPException e) {
                    log.debug("ignoring", e);
                }
            }
            // empty the collection
            children.clear();
            setDirty();
        }
    }

    /**
     * get an iterator over visible prefixes. This includes all declared in
     * parent elements
     * @return an iterator.
     */
    public Iterator getVisibleNamespacePrefixes() {
        Vector prefixes = new Vector();

        // Add all parents namespace definitions
        if(parent !=null){
            Iterator parentsPrefixes = ((MessageElement)parent).getVisibleNamespacePrefixes();
            if(parentsPrefixes != null){
                while(parentsPrefixes.hasNext()){
                    prefixes.add(parentsPrefixes.next());
                }
            }
        }
        Iterator mine = getNamespacePrefixes();
        if(mine != null){
            while(mine.hasNext()){
                prefixes.add(mine.next());
            }
        }
        return prefixes.iterator();
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

        this.encodingStyle = encodingStyle;

        // Wherever we set the encoding style, map the SOAP-ENC prefix
        // just for fun (if it's a known style)
        if (encodingStyle.equals(Constants.URI_SOAP11_ENC)) {
            addMapping(enc11Mapping);
        } else if (encodingStyle.equals(Constants.URI_SOAP12_ENC)) {
            addMapping(enc12Mapping);
        }
    }

    /**
     * Note that this method will log a error and no-op if there is
     * a value (set using setObjectValue) in the MessageElement.
     */
    public void addChild(MessageElement el) throws SOAPException
    {
        if (objectValue != null) {
            IllegalStateException exc =
                new IllegalStateException(Messages.getMessage("valuePresent"));
            log.error(Messages.getMessage("valuePresent"), exc);
            throw exc;
        }
        initializeChildren();
        children.add(el);
        el.parent = this;
    }

    /**
     * get a list of children
     * @return a list, or null if there are no children
     */
    public List getChildren()
    {
        return children;
    }

    /**
     * set the index point of our content's starting in the
     * event recording
     * @param index index value of the first event of our recorder.
     */
    public void setContentsIndex(int index)
    {
        startContentsIndex = index;
    }

    /**
     * set a new namespace mapping list
     * @param namespaces
     */
    public void setNSMappings(ArrayList namespaces)
    {
        this.namespaces = namespaces;
    }

    /**
     * get the prefix for a given namespace URI
     * @param searchNamespaceURI namespace
     * @return null for null or emtpy uri, null for no match, and the prefix iff there is a match
     */
    public String getPrefix(String searchNamespaceURI) {
        if ((searchNamespaceURI == null) || ("".equals(searchNamespaceURI)))
            return null;

        if (href != null && getRealElement() != null) {
            return getRealElement().getPrefix(searchNamespaceURI);
        }

        for (int i = 0; namespaces != null && i < namespaces.size(); i++) {
            Mapping map = (Mapping) namespaces.get(i);
            if (map.getNamespaceURI().equals(searchNamespaceURI)) {
                return map.getPrefix();
            }
        }

        if (parent != null) {
            return ((MessageElement) parent).getPrefix(searchNamespaceURI);
        }

        return null;
    }

    /**
     * map from a prefix to a namespace.
     * Will recurse <i>upward the element tree</i> until we get a match
     * @param searchPrefix
     * @return the prefix, or null for no match
     */
    public String getNamespaceURI(String searchPrefix) {
        if (searchPrefix == null) {
            searchPrefix = "";
        }

        if (href != null && getRealElement() != null) {
            return getRealElement().getNamespaceURI(searchPrefix);
        }

        for (int i = 0; namespaces != null && i < namespaces.size(); i++) {
            Mapping map = (Mapping) namespaces.get(i);
            if (map.getPrefix().equals(searchPrefix)) {
                return map.getNamespaceURI();
            }
        }

        if (parent != null) {
            return ((MessageElement) parent).getNamespaceURI(searchPrefix);
        }

        if (log.isDebugEnabled()) {
            log.debug(Messages.getMessage("noPrefix00", "" + this, searchPrefix));
        }

        return null;
    }

    /**
     * Returns value of the node as an object of registered type.
     * @return Object of proper type, or null if no mapping could be found.
     */
    public Object getObjectValue() {
        Object obj = null;
        try {
            obj = getObjectValue(null);
        } catch (Exception e) {
            log.debug("getValue()", e);
        }
        return obj;
    }

    /**
     * Returns value of the node as an object of registered type.
     * @param cls Class that contains top level deserializer metadata
     * @return Object of proper type, or null if no mapping could be found.
     */
    public Object getObjectValue(Class cls) throws Exception {
        if (objectValue == null) {
            objectValue = getValueAsType(getType(), cls);
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
            SOAPException exc = new SOAPException(Messages.getMessage("childPresent"));
            log.error(Messages.getMessage("childPresent"), exc);
            throw exc;
        }
        if (textRep != null) {
            SOAPException exc = new SOAPException(Messages.getMessage("xmlPresent"));
            log.error(Messages.getMessage("xmlPresent"), exc);
            throw exc;
        }
        this.objectValue = newValue;
    }

    public Object getValueAsType(QName type) throws Exception
    {
        return getValueAsType(type, null);
    }

    /**
     * This is deserialization logic mixed in to our element class.
     * It is only valid we have a deserializer, which means that we were created
     * using {@link MessageElement#MessageElement(String, String, String, org.xml.sax.Attributes, org.apache.axis.encoding.DeserializationContext)}
     * @param type type to look up a deserializer for.
     * @param cls class to use for looking up the deserializer. This takes precedence over the type field.
     * @return the value of the deserializer
     * @throws Exception
     */
    public Object getValueAsType(QName type, Class cls) throws Exception
    {
        if (context == null) {
            throw new Exception(Messages.getMessage("noContext00"));
        }

        Deserializer dser = null;
        if (cls == null) {
            dser = context.getDeserializerForType(type);
        } else {
            dser = context.getDeserializerForClass(cls);
        }
        if (dser == null) {
            throw new Exception(Messages.getMessage("noDeser00", "" + type));
        }

        boolean oldVal = context.isDoneParsing();
        context.deserializing(true);
        context.pushElementHandler(new EnvelopeHandler((SOAPHandler)dser));

        publishToHandler((org.xml.sax.ContentHandler) context);

        context.deserializing(oldVal);

        return dser.getValue();
    }

    /**
     * class that represents a qname in a the qNameAttrs vector.
     */
    protected static class QNameAttr {
        public QName name;
        public QName value;
    }

    /**
     * add an attribute to the qname vector. This is a separate vector from the
     * main attribute list.
     * @param namespace
     * @param localName
     * @param value
     */

    public void addAttribute(String namespace, String localName,
                             QName value)
    {
        if (qNameAttrs == null) {
            qNameAttrs = new Vector();
        }

        QNameAttr attr = new QNameAttr();
        attr.name = new QName(namespace, localName);
        attr.value = value;

        qNameAttrs.addElement(attr);
        // !!! Add attribute to attributes!
    }

    /**
     * add a normal CDATA/text attribute.
     * There is no check whether or not the attribute already exists.
     * @param namespace namespace URI
     * @param localName local anme
     * @param value value
     */
    public void addAttribute(String namespace, String localName,
                             String value)
    {
        AttributesImpl attributes = makeAttributesEditable();
        attributes.addAttribute(namespace, localName, "", "CDATA",
                                value);
    }

    /**
     * add an attribute.
     * Note that the prefix is not added to our mapping list.
     * Also, there is no check whether or not the attribute already exists.
     * @param attrPrefix prefix.
     * @param namespace namespace URI
     * @param localName
     * @param value
     */
    public void addAttribute(String attrPrefix, String namespace, String localName,
                             String value)
    {
        AttributesImpl attributes = makeAttributesEditable();
        String attrName = localName;
        if (attrPrefix != null && attrPrefix.length() > 0) {
            attrName = attrPrefix + ":" + localName;
        }
        attributes.addAttribute(namespace, localName, attrName, "CDATA",
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
        AttributesImpl attributes = makeAttributesEditable();

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

        addAttribute(namespace, localName, value);
    }

    /**
     * get the value of an attribute
     * @param localName
     * @return the value or null
     */
    public String getAttributeValue(String localName)
    {
        if (attributes == null) {
           return null;
        }
        return attributes.getValue(localName);
    }

    /**
     * bind a a new soap envelope. sets the dirty bit.
     * @param env
     */
    public void setEnvelope(SOAPEnvelope env)
    {
        env.setDirty();
        message = env;
    }

    /**
     * get our current envelope
     * @return envelope or null.
     */
    public SOAPEnvelope getEnvelope()
    {
        return message;
    }

    /**
     * get the 'real' element -will follow hrefs.
     * @return the message element or null if there is a href to something
     * that is not a MessageElemeent.
     */
    public MessageElement getRealElement()
    {
        if (href == null) {
            return this;
        }

        Object obj = context.getObjectByRef(href);
        if (obj == null) {
            return null;
        }

        if (!(obj instanceof MessageElement)) {
            return null;
        }

        return (MessageElement) obj;
    }

    /**
     * get the message element as a document.
     * This serializes the element to a string and then parses it.
     * @see #getAsString()
     * @return
     * @throws Exception
     */
    public Document getAsDocument() throws Exception
    {
        String elementString = getAsString();

        Reader reader = new StringReader(elementString);
        Document doc = XMLUtils.newDocument(new InputSource(reader));
        if (doc == null) {
            throw new Exception(
                    Messages.getMessage("noDoc00", elementString));
        }
        return doc;
    }

    /**
     * get the message element as a string.
     * This is not a cheap operation, as we have to serialise the
     * entire message element to the current context, then
     * convert it to a string.
     * Nor is it cached; repeated calls repeat the operation.
     * @return an XML fragment in a string.
     * @throws Exception if anything went wrong
     */
    public String getAsString() throws Exception {
        SerializationContext serializeContext = null;
        StringWriter writer = new StringWriter();
        MessageContext msgContext;
        if (context != null) {
            msgContext = context.getMessageContext();
        } else {
            msgContext = MessageContext.getCurrentContext();
        }
        serializeContext = new SerializationContext(writer, msgContext);
        serializeContext.setSendDecl(false);
        setDirty(false);
        output(serializeContext);
        writer.close();

        return writer.getBuffer().toString();
    }

    /**
     * create a DOM from the message element, by
     * serializing and deserializing the element
     * @see #getAsString()
     * @see #getAsDocument()
     * @return the root document element of the element
     * @throws Exception
     */
    public Element getAsDOM() throws Exception
    {
        return getAsDocument().getDocumentElement();
    }

    /**
     * replay the sax events to a handler
     * @param handler
     * @throws SAXException
     */
    public void publishToHandler(ContentHandler handler) throws SAXException
    {
        if (recorder == null) {
            throw new SAXException(Messages.getMessage("noRecorder00"));
        }

        recorder.replay(startEventIndex, endEventIndex, handler);
    }

    /**
     * replay the sax events to a SAX content handles
     * @param handler
     * @throws SAXException
     */
    public void publishContents(ContentHandler handler) throws SAXException
    {
        if (recorder == null) {
            throw new SAXException(Messages.getMessage("noRecorder00"));
        }

        recorder.replay(startContentsIndex, endEventIndex-1, handler);
    }

    /** This is the public output() method, which will always simply use
     * the recorded SAX stream for this element if it is available.  If
     * not, this method calls outputImpl() to allow subclasses and
     * programmatically created messages to serialize themselves.
     *
     * @param outputContext the SerializationContext we will write to.
     */
    public final void output(SerializationContext outputContext) throws Exception
    {
        if ((recorder != null) && (!_isDirty)) {
            recorder.replay(startEventIndex,
                            endEventIndex,
                            new SAXOutputter(outputContext));
            return;
        }

        // Turn QName attributes into strings
        if (qNameAttrs != null) {
            for (int i = 0; i < qNameAttrs.size(); i++) {
                QNameAttr attr = (QNameAttr)qNameAttrs.get(i);
                QName attrName = attr.name;
                setAttribute(attrName.getNamespaceURI(),
                             attrName.getLocalPart(),
                             outputContext.qName2String(attr.value));
            }
        }

        /**
         * Write the encoding style attribute IF it's different from
         * whatever encoding style is in scope....
         */
        if (encodingStyle != null) {
            MessageContext mc = outputContext.getMessageContext();
            SOAPConstants soapConstants = (mc != null) ?
                                            mc.getSOAPConstants() :
                                            SOAPConstants.SOAP11_CONSTANTS;
            if (parent == null) {
                // don't emit an encoding style if its "" (literal)
                if (!"".equals(encodingStyle)) {
                    setAttribute(soapConstants.getEnvelopeURI(),
                                 Constants.ATTR_ENCODING_STYLE,
                                 encodingStyle);
                }
            } else if (!encodingStyle.equals(((MessageElement)parent).getEncodingStyle())) {
                setAttribute(soapConstants.getEnvelopeURI(),
                             Constants.ATTR_ENCODING_STYLE,
                             encodingStyle);
            }
        }

        outputImpl(outputContext);
    }

    /**
     * override point -output to a serialization context.
     * @param outputContext destination.
     * @throws Exception if something went wrong.
     */
    protected void outputImpl(SerializationContext outputContext) throws Exception
    {
        if (textRep != null) {
            boolean oldPretty = outputContext.getPretty();
            outputContext.setPretty(false);
            if (textRep instanceof CDATASection) {
                outputContext.writeString("<![CDATA[");
                outputContext.writeString(textRep.getData());
                outputContext.writeString("]]>");
            } else if (textRep instanceof Comment) {
                outputContext.writeString("<!--");
                outputContext.writeString(textRep.getData());
                outputContext.writeString("-->");
            } else if (textRep instanceof Text) {
                outputContext.writeSafeString(textRep.getData());
            }
            outputContext.setPretty(oldPretty);
            return;
        }

        if (prefix != null)
            outputContext.registerPrefixForURI(prefix, namespaceURI);

        if (namespaces != null) {
            for (Iterator i = namespaces.iterator(); i.hasNext();) {
                Mapping mapping = (Mapping) i.next();
                outputContext.registerPrefixForURI(mapping.getPrefix(), mapping.getNamespaceURI());
            }
        }

        if (objectValue != null) {
            outputContext.serialize(new QName(namespaceURI, name),
                              attributes,
                              objectValue);
            return;
        }

        outputContext.startElement(new QName(namespaceURI, name), attributes);
        if (children != null) {
            for (Iterator it = children.iterator(); it.hasNext();) {
                ((NodeImpl)it.next()).output(outputContext);
            }
        }
        outputContext.endElement();
    }

    /**
     * Generate a string representation by serializing our contents
     * This is not a lightweight operation, and is repeated whenever
     * you call this method.
     * If the serialization fails, an error is logged and the classic
     * {@link Object#toString()} operation invoked instead.
     * @return a string representing the class
     */
    public String toString() {
        try {
            return getAsString();
        }
        catch( Exception exp ) {
            //couldn't turn to a string.
            //log it
            log.error(Messages.getMessage("exception00"), exp);
            //then hand off to our superclass, which is probably object
            return super.toString();
        }
    }

    /**
     * add a new namespace/prefix mapping
     * @param map new mapping to add
     */
    // TODO: this code does not verify that the mapping does not exist already; it
    // is possible to create duplicate mappings.
    public void addMapping(Mapping map) {
        if (namespaces == null) {
            namespaces = new ArrayList();
        }
        namespaces.add(map);
    }

    // JAXM SOAPElement methods...

    /**
     * add the child element
     * @param childName uri, prefix and local name of the element to add
     * @return the child element
     * @throws SOAPException
     * @see javax.xml.soap.SOAPElement#addChildElement(javax.xml.soap.Name)
     */
    public SOAPElement addChildElement(Name childName) throws SOAPException {
        MessageElement child = new MessageElement(childName.getLocalName(),
                                                  childName.getPrefix(),
                                                  childName.getURI());
        addChild(child);
        return child;
    }

    /**
     * add a child element in the message element's own namespace
     * @param localName
     * @return the child element
     * @throws SOAPException
     * @see javax.xml.soap.SOAPElement#addChildElement(String)
     */
    public SOAPElement addChildElement(String localName) throws SOAPException {
        // Inherit parent's namespace
        MessageElement child = new MessageElement(getNamespaceURI(),
                                                  localName);
        addChild(child);
        return child;
    }

    /**
     * add a child element
     * @param localName
     * @param prefixName
     * @return the child element
     * @throws SOAPException
     * @see javax.xml.soap.SOAPElement#addChildElement(String, String)
     */
    public SOAPElement addChildElement(String localName,
                                       String prefixName) throws SOAPException {
        MessageElement child = new MessageElement(getNamespaceURI(prefixName),
                                                  localName);
        child.setPrefix(prefixName);
        addChild(child);
        return child;
    }

    /**
     * add a child element
     * @param localName
     * @param childPrefix
     * @param uri
     * @return the child element
     * @throws SOAPException
     * @see javax.xml.soap.SOAPElement#addChildElement(String, String, String)
     */
    public SOAPElement addChildElement(String localName,
                                       String childPrefix,
                                       String uri) throws SOAPException {
        MessageElement child = new MessageElement(uri, localName);
        child.setPrefix(childPrefix);
        child.addNamespaceDeclaration(childPrefix, uri);
        addChild(child);
        return child;
    }

    /**
     * The added child must be an instance of MessageElement rather than
     * an abitrary SOAPElement otherwise a (wrapped) ClassCastException
     * will be thrown.
     * @see javax.xml.soap.SOAPElement#addChildElement(javax.xml.soap.SOAPElement)
     */
    public SOAPElement addChildElement(SOAPElement element)
        throws SOAPException {
        try {
            addChild((MessageElement)element);
            setDirty();
            return element;
        } catch (ClassCastException e) {
            throw new SOAPException(e);
        }
    }

    /**
     * add a text node to the document.
     * @return ourselves
     * @see javax.xml.soap.SOAPElement#addTextNode(String)
     */
    public SOAPElement addTextNode(String s) throws SOAPException {
        try {
            Text text = getOwnerDocument().createTextNode(s);
            ((org.apache.axis.message.Text)text).setParentElement(this);
            return this;
        } catch (java.lang.IncompatibleClassChangeError e) {
            Text text = new org.apache.axis.message.Text(s);
            this.appendChild(text);
            return this;
        } catch (ClassCastException e) {
            throw new SOAPException(e);
        }
    }

    /**
     * add a new attribute
     * @param attrName name of the attribute
     * @param value a string value
     * @return ourselves
     * @throws SOAPException
     * @see javax.xml.soap.SOAPElement#addAttribute(javax.xml.soap.Name, String)
     */
    public SOAPElement addAttribute(Name attrName, String value)
        throws SOAPException {
        try {
            addAttribute(attrName.getPrefix(), attrName.getURI(), attrName.getLocalName(), value);
        } catch (RuntimeException t) {
            throw new SOAPException(t);
        }
        return this;
    }

    /**
     * create a {@link Mapping} mapping and add to our namespace list.
     * @param prefix
     * @param uri
     * @return
     * @throws SOAPException for any {@link RuntimeException} caught
     * @see javax.xml.soap.SOAPElement#addNamespaceDeclaration(String, String)
     */
    // TODO: for some reason this logic catches all rutime exceptions and
    // rethrows them as SOAPExceptions. This is unusual behavio, and should
    // be looked at closely.
    public SOAPElement addNamespaceDeclaration(String prefix,
                                               String uri)
        throws SOAPException {
        try {
            Mapping map = new Mapping(uri, prefix);
            addMapping(map);
        } catch (RuntimeException t) {
            //TODO: why is this here? Nowhere else do we turn runtimes into SOAPExceptions.
            throw new SOAPException(t);
        }
        return this;
    }

    /**
     * Get the value of an attribute whose namespace and local name are described.
     * @param attrName qualified name of the attribute
     * @return the attribute or null if there was no match
     * @see SOAPElement#getAttributeValue(javax.xml.soap.Name)
     */
    public String getAttributeValue(Name attrName) {
        return attributes.getValue(attrName.getURI(), attrName.getLocalName());
    }

    /**
     * Get an interator to all the attributes of the node.
     * The iterator is over a static snapshot of the node names; if attributes
     * are added or deleted during the iteration, this iterator will be not
     * be updated to follow the changes.
     * @return an iterator of the attributes.
     * @see javax.xml.soap.SOAPElement#getAllAttributes()
     */
    public Iterator getAllAttributes() {
        int num = attributes.getLength();
        Vector attrs = new Vector(num);
        for (int i = 0; i < num; i++) {
            String q = attributes.getQName(i);
            String prefix = "";
            if (q != null) {
                int idx = q.indexOf(":");
                if (idx > 0) {
                    prefix = q.substring(0, idx);
                } else {
                    prefix= "";
                }
            }

            attrs.add(new PrefixedQName(attributes.getURI(i),
                                        attributes.getLocalName(i),
                                        prefix));
        }
        return attrs.iterator();
    }

    // getNamespaceURI implemented above

    /**
     * get an iterator of the prefixes. The iterator
     * does not get updated in response to changes in the namespace list.
     * @return an iterator over a vector of prefixes
     * @see javax.xml.soap.SOAPElement#getNamespacePrefixes()
     */
    public Iterator getNamespacePrefixes() {
        Vector prefixes = new Vector();
        for (int i = 0; namespaces != null && i < namespaces.size(); i++) {
            prefixes.add(((Mapping)namespaces.get(i)).getPrefix());
        }
        return prefixes.iterator();
    }

    /**
     * get the full name of the element
     * @return
     * @see javax.xml.soap.SOAPElement#getElementName()
     */
    public Name getElementName() {
        return new PrefixedQName(getNamespaceURI(), getName(), getPrefix());
    }

    /**
     * remove an element
     * @param attrName name of the element
     * @return true if the attribute was found and removed.
     * @see javax.xml.soap.SOAPElement#removeAttribute(javax.xml.soap.Name)
     */
    public boolean removeAttribute(Name attrName) {
        AttributesImpl attributes = makeAttributesEditable();
        boolean removed = false;

        for (int i = 0; i < attributes.getLength() && !removed; i++) {
            if (attributes.getURI(i).equals(attrName.getURI()) &&
                attributes.getLocalName(i).equals(attrName.getLocalName())) {
                attributes.removeAttribute(i);
                removed = true;
            }
        }
        return removed;
    }

    /**
     * remove a namespace declaration.
     * @param namespacePrefix
     * @return true if the prefix was found and removed.
     * @see javax.xml.soap.SOAPElement#removeNamespaceDeclaration(String)
     */
    public boolean removeNamespaceDeclaration(String namespacePrefix) {
        makeAttributesEditable();
        boolean removed = false;

        for (int i = 0; namespaces != null && i < namespaces.size() && !removed; i++) {
            if (((Mapping)namespaces.get(i)).getPrefix().equals(namespacePrefix)) {
                namespaces.remove(i);
                removed = true;
            }
        }
        return removed;
    }

    /**
     * get an iterator over the children
     * This iterator <i>may</i> get confused if changes are made to the
     * children while the iteration is in progress.
     * @return an iterator over child elements.
     * @see javax.xml.soap.SOAPElement#getChildElements()
     */
    public Iterator getChildElements() {
        initializeChildren();
        return children.iterator();
    }

    /**
     * Convenience method to get the first matching child for a given QName.
     *
     * @param qname
     * @return child element or null
     * @see javax.xml.soap.SOAPElement#getChildElements()
     */
    public MessageElement getChildElement(QName qname) {
        if (children != null) {
            for (Iterator i = children.iterator(); i.hasNext();) {
                MessageElement child = (MessageElement) i.next();
                if (child.getQName().equals(qname))
                    return child;
            }
        }
        return null;
    }

    /**
     * get an iterator over child elements
     * @param qname namespace/element name of parts to find.
     * This iterator is not (currently) susceptible to change in the element
     * list during its lifetime, though changes in the contents of the elements
     * are picked up.
     * @return an iterator.
     */
    public Iterator getChildElements(QName qname) {
        initializeChildren();
        int num = children.size();
        Vector c = new Vector(num);
        for (int i = 0; i < num; i++) {
            MessageElement child = (MessageElement)children.get(i);
            Name cname = child.getElementName();
            if (cname.getURI().equals(qname.getNamespaceURI()) &&
                cname.getLocalName().equals(qname.getLocalPart())) {
                c.add(child);
            }
        }
        return c.iterator();
    }

    /**
     * get an iterator over child elements
     * @param childName namespace/element name of parts to find.
     * This iterator is not (currently) susceptible to change in the element
     * list during its lifetime, though changes in the contents of the elements
     * are picked up.
     * @return an iterator.
     * @see javax.xml.soap.SOAPElement#getChildElements(javax.xml.soap.Name)
     */
    public Iterator getChildElements(Name childName) {
        return getChildElements(new QName(childName.getURI(), childName.getLocalName()));
    }

    //DOM methods

    /**
     * @see org.w3c.dom.Element#getTagName()
     * @return the name of the element
     */
    public String getTagName() {
        return prefix == null ? name : prefix + ":" + name;
    }

    /**
     * remove a named attribute.
     * @see org.w3c.dom.Element#removeAttribute(String)
     * @param attrName name of the attributes
     * @throws DOMException
     */
    public void removeAttribute(String attrName) throws DOMException {
        AttributesImpl impl =  (AttributesImpl)attributes;
        int index = impl.getIndex(attrName);
        if(index >= 0){
            AttributesImpl newAttrs = new AttributesImpl();
            // copy except the removed attribute
            for(int i = 0; i < impl.getLength(); i++){ // shift after removal
                if(i != index){
                    String uri = impl.getURI(i);
                    String local = impl.getLocalName(i);
                    String qname = impl.getQName(i);
                    String type = impl.getType(i);
                    String value = impl.getValue(i);
                    newAttrs.addAttribute(uri,local,qname,type,value);
                }
            }
            // replace it
            attributes = newAttrs;
        }
    }

    /**
     * test for an attribute existing
     * @param attrName name of attribute (or null)
     * @return true if it exists
     * Note that the behaviour for a null parameter (returns false) is not guaranteed in future
     * @see org.w3c.dom.Element#hasAttribute(String)
     */
    public boolean hasAttribute(String attrName) {
        if(attrName == null)  // Do I have to send an exception?
            attrName = "";

        for(int i = 0; i < attributes.getLength(); i++){
            if(attrName.equals(attributes.getQName(i)))
                return true;
        }
        return false;
    }

    /**
     * get an attribute by name
     * @param attrName of attribute
     * @return the attribute value or null
     * @see org.w3c.dom.Element#getAttribute(String)
     */
    public String getAttribute(String attrName) {
        return attributes.getValue(attrName);
    }

    /**
     * Remove an attribute. If the removed
     * attribute has a default value it is immediately replaced. The
     * replacing attribute has the same namespace URI and local name, as
     * well as the original prefix.
     * If there is no matching attribute, the operation is a no-op.
     * @see org.w3c.dom.Element#removeAttributeNS(String, String)
     * @param namespace namespace of attr
     * @param localName local name
     * @throws DOMException
     */
    public void removeAttributeNS(String namespace, String localName) throws DOMException {
        makeAttributesEditable();
        Name name =  new PrefixedQName(namespace, localName, null);
        removeAttribute(name);
    }

    /**
     * set or update an attribute.
     * @see org.w3c.dom.Element#setAttribute(String, String)
     * @param name attribute name
     * @param value attribute value
     * @throws DOMException
     */
    public void setAttribute(String name, String value) throws DOMException {
        AttributesImpl impl =  makeAttributesEditable();
        int index = impl.getIndex(name);
        if (index < 0) { // not found
            String uri = "";
            String localname = name;
            String qname = name;
            String type = "CDDATA";
            impl.addAttribute(uri, localname, qname, type, value);
        } else {         // found
            impl.setLocalName(index, value);
        }
    }

    /**
     * Test for an attribute
     * @see org.w3c.dom.Element#hasAttributeNS(String, String)
     * @param namespace
     * @param localName
     * @return
     */
    public boolean hasAttributeNS(String namespace, String localName) {
        if (namespace == null) {
            namespace = "";
        }
        if (localName == null)  // Do I have to send an exception? or just return false
        {
            localName = "";
        }

        for(int i = 0; i < attributes.getLength(); i++){
            if( namespace.equals(attributes.getURI(i))
                    && localName.equals(attributes.getLocalName(i)))
                return true;
        }
        return false;
    }

    /**
     * This unimplemented operation is meand to return an attribute as a node
     * @see org.w3c.dom.Element#getAttributeNode(String)
     * @param attrName
     * @return null, always.
     * @deprecated this is not implemented
     */
    // TODO: Fix this for SAAJ 1.2 Implementation. marked as deprecated to warn people
    // it is broken
    public Attr getAttributeNode(String attrName) {
        return null;
    }

    /**
     * remove a an attribue
     * @param oldAttr
     * @return oldAttr
     * @throws DOMException
     */
    public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
        makeAttributesEditable();
        Name name =  new PrefixedQName(oldAttr.getNamespaceURI(), oldAttr.getLocalName(), oldAttr.getPrefix());
        removeAttribute(name);
        return oldAttr;
    }

    /**
     * set the attribute node.
     * @see org.w3c.dom.Element#setAttributeNode(org.w3c.dom.Attr)
     * @param newAttr
     * @return newAttr
     * @throws DOMException
     * @deprecated this is not implemented
     */
    // TODO: implement
    public Attr setAttributeNode(Attr newAttr) throws DOMException {
        return newAttr;
    }

    /**
     * set an attribute as a node
     * @see org.w3c.dom.Element#setAttributeNodeNS(org.w3c.dom.Attr)
     * @param newAttr
     * @return null
     * @throws DOMException
     */
    // TODO: implement properly.
    public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
        //attributes.
        AttributesImpl attributes = makeAttributesEditable();
        // how to convert to DOM ATTR
        attributes.addAttribute(newAttr.getNamespaceURI(),
                newAttr.getLocalName(),
                newAttr.getLocalName(),
                "CDATA",
                newAttr.getValue());
        return null;
    }

    /**
     * @see org.w3c.dom.Element#getElementsByTagName(String)
     * @param tagName tag to look for.
     * @return a list of elements
     */
    public NodeList getElementsByTagName(String tagName) {
        NodeListImpl nodelist = new NodeListImpl();
        for (int i = 0; children != null && i < children.size(); i++) {
            if (children.get(i) instanceof Node) {
                Node el = (Node)children.get(i);
                if (el.getLocalName() != null && el.getLocalName()
                                .equals(tagName))
                    nodelist.addNode(el);
                if (el instanceof Element) {
                    NodeList grandchildren =
                            ((Element)el).getElementsByTagName(tagName);
                    for (int j = 0; j < grandchildren.getLength(); j++) {
                        nodelist.addNode(grandchildren.item(j));
                    }
                }
            }
        }
        return nodelist;
    }
    
    /**
     * get the attribute with namespace/local name match.
     * @see org.w3c.dom.Element#getAttributeNS(String, String)
     * @param namespaceURI namespace
     * @param localName name
     * @return string value or null if not found
     */
    // TODO: this could be refactored to use getAttributeValue()
    public String getAttributeNS(String namespaceURI, String localName) {
    	if(namespaceURI == null) {
    		namespaceURI = "";
    	}
        for (int i = 0; i < attributes.getLength(); i++) {
            if (attributes.getURI(i).equals(namespaceURI) &&
                    attributes.getLocalName(i).equals(localName)) {
                return  attributes.getValue(i);
            }
        }
        return null;
    }

    /**
     * set an attribute or alter an existing one
     * @see org.w3c.dom.Element#setAttributeNS(String, String, String)
     * @param namespaceURI namepsace
     * @param qualifiedName qualified name of the attribue
     * @param value value
     * @throws DOMException
     */
    public void setAttributeNS(String namespaceURI, String qualifiedName,
                               String value)
        throws DOMException
    {
        AttributesImpl attributes = makeAttributesEditable();
        String localName =  qualifiedName.substring(qualifiedName.indexOf(":")+1, qualifiedName.length());

        if (namespaceURI == null) {
            namespaceURI = "intentionalNullURI";
        }
        attributes.addAttribute(namespaceURI,
                localName,
                qualifiedName,
                "CDATA",
                value);
    }

    /**
     * @see org.w3c.dom.Element#getAttributeNS(String, String)
     * @deprecated not implemented!
     * @param namespace namespace
     * @param localName local name
     * @return null
     */
    public Attr getAttributeNodeNS(String namespace, String localName) {
        return null;  //TODO: Fix this for SAAJ 1.2 Implementation
    }

    /**
     * @see org.w3c.dom.Element#getElementsByTagNameNS(String, String)
     * @param namespace namespace
     * @param localName local name of element
     * @return (potentially empty) list of elements that match the (namespace,localname) tuple
     */
    public NodeList getElementsByTagNameNS(String namespace,
                                           String localName)
    {
        return getElementsNS(this,namespace,localName);
    }

    /**
     * helper method for recusively getting the element that has namespace URI and localname
     * @param parentElement parent element
     * @param namespace namespace
     * @param localName local name of element
     * @return (potentially empty) list of elements that match the (namespace,localname) tuple
     */
    protected NodeList getElementsNS(org.w3c.dom.Element parentElement,
                                     String namespace, String localName)
    {
        NodeList children = parentElement.getChildNodes();
        NodeListImpl matches = new NodeListImpl();

        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i) instanceof Text) {
                continue;
            }
            Element child = (Element) children.item(i);
            if (namespace.equals(child.getNamespaceURI()) &&
                    localName.equals(child.getLocalName())) {
                matches.addNode(child);
            }
            // search the grand-children.
            matches.addNodeList(child.getElementsByTagNameNS(namespace,
                    localName));
        }
        return matches;
    }

    /**
     * get a child node
     * @param index index value
     * @return child or null for out of range value
     * @see org.w3c.dom.NodeList#item(int)
     */
    public Node item(int index) {
        if (children != null && children.size() > index) {
            return (Node) children.get(index);
        } else {
            return null;
        }
    }

    /**
     * The number of nodes in the list. The range of valid child node indices
     * is 0 to <code>length-1</code> inclusive.
     * @return number of children
     * @since SAAJ 1.2 : Nodelist Interface
     * @see org.w3c.dom.NodeList#getLength()
     */
    public int getLength()
    {
        return (children == null) ? 0 : children.size();
    }

    // setEncodingStyle implemented above

    // getEncodingStyle() implemented above

    protected MessageElement findElement(Vector vec, String namespace,
                               String localPart)
    {
        if (vec.isEmpty()) {
            return null;
        }

        QName qname = new QName(namespace, localPart);
        Enumeration e = vec.elements();
        MessageElement element;
        while (e.hasMoreElements()) {
            element = (MessageElement) e.nextElement();
            if (element.getQName().equals(qname)) {
                return element;
            }
        }

        return null;
    }

    /**
     * equality test. Does a string match of the two message elements,
     * so is fairly brute force.
     * @see #toString()
     * @param obj
     * @return
     */
    public boolean equals(Object obj)
    {
        if (obj == null || !(obj instanceof MessageElement)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!this.getLocalName().equals(((MessageElement) obj).getLocalName())) {
            return false;
        }
        return toString().equals(obj.toString());
    }

    /**
     * recursively copy.
     * Note that this does not reset many of our fields, and must be used with caution.
     * @param element
     */
    private void copyNode(org.w3c.dom.Node element) {
        copyNode(this, element);
    }

    /**
     * recursive copy
     * @param dest element to copy into
     * @param source child element
     */
    private void copyNode(MessageElement dest, org.w3c.dom.Node source)
    {
        dest.setPrefix(source.getPrefix());
        if(source.getLocalName() != null) {
            dest.setQName(new QName(source.getNamespaceURI(), source.getLocalName()));
        }
        else
        {
            dest.setQName(new QName(source.getNamespaceURI(), source.getNodeName()));
        }

        NamedNodeMap attrs = source.getAttributes();
        for(int i = 0; i < attrs.getLength(); i++){
            Node att = attrs.item(i);
        if (att.getNamespaceURI() != null &&
                att.getPrefix() != null &&
                att.getNamespaceURI().equals(Constants.NS_URI_XMLNS) &&
                "xmlns".equals(att.getPrefix())) {
                Mapping map = new Mapping(att.getNodeValue(), att.getLocalName());
                dest.addMapping(map);
            }
            if(att.getLocalName() != null) {
                dest.addAttribute(att.getPrefix(),
                        (att.getNamespaceURI() != null ? att.getNamespaceURI() : ""),
                        att.getLocalName(),
                        att.getNodeValue());
            } else if (att.getNodeName() != null) {
                dest.addAttribute(att.getPrefix(),
                        (att.getNamespaceURI() != null ? att.getNamespaceURI() : ""),
                        att.getNodeName(),
                        att.getNodeValue());
            }
        }

        NodeList children = source.getChildNodes();
        for(int i = 0; i < children.getLength(); i++){
            Node child = children.item(i);
            if(child.getNodeType()==TEXT_NODE ||
               child.getNodeType()==CDATA_SECTION_NODE ||
               child.getNodeType()==COMMENT_NODE ) {
                org.apache.axis.message.Text childElement = 
                    new org.apache.axis.message.Text((CharacterData)child);
                dest.appendChild(childElement);
            } else {
                MessageElement childElement = new MessageElement();
                dest.appendChild(childElement);
                copyNode(childElement, child);
            }
        }
    }

    /**
     * Get the value of the doc as a string.
     * This uses {@link #getAsDOM()} so is a heavyweight operation.
     * @return the value of any child node, or null if there is no node/something went
     * wrong during serialization. If the first child is text, the return value
     * is the text itself.
     * @see javax.xml.soap.Node#getValue() ;
     */
    public String getValue() {
        /*--- Fix for AXIS-1817
        if ((recorder != null) && (!_isDirty)) {
            StringWriter writer = new StringWriter();
            TextSerializationContext outputContext = 
                new TextSerializationContext(writer);
            try {
                recorder.replay(startEventIndex,
                                endEventIndex,
                                new SAXOutputter(outputContext));
            } catch (Exception t) {
                log.debug("getValue()", t);
                return null;
            }
            String value = writer.toString();
            return (value.length() == 0) ? null : value;
        } 
        ---*/

        if (textRep != null) {
            // weird case: error?
            return textRep.getNodeValue();
        }

        if (objectValue != null) {
            return getValueDOM();
        }

        for (Iterator i = getChildElements(); i.hasNext(); ) {
            org.apache.axis.message.NodeImpl n = (org.apache.axis.message.NodeImpl) i.next();
            if (n instanceof org.apache.axis.message.Text) {
                org.apache.axis.message.Text textNode = (org.apache.axis.message.Text) n;
                return textNode.getNodeValue();
            }
        }

        return null;
    }

    protected String getValueDOM() {
        try {
            Element element = getAsDOM();
            if (element.hasChildNodes()) {
                Node node = element.getFirstChild();
                if (node.getNodeType() == Node.TEXT_NODE) {
                    return node.getNodeValue();
                }
            }
        } catch (Exception t) {
            log.debug("getValue()", t);
        }
        return null;
    }

    public void setValue( String value )
    {
        // if possible, get objectValue in sync with Node value
        if (children==null) {
            try {
                setObjectValue(value);
            } catch ( SOAPException soape ) {
                log.debug("setValue()", soape);
            }
        }
        super.setValue(value);
    }

    public Document getOwnerDocument() {
        Document doc = null;
        if (context != null && context.getEnvelope() != null &&
                context.getEnvelope().getOwnerDocument() != null) {
            doc = context.getEnvelope().getOwnerDocument();
        }
        if(doc == null) {
            doc = super.getOwnerDocument();
        }
        if (doc == null) {
            doc = new SOAPDocumentImpl(null);
        }
        return doc;
    }
}
