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

import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.rpc.encoding.TypeMapping;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.DeserializationContextImpl;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.SerializationContextImpl;
import org.apache.axis.enum.Style;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.Mapping;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/*
 * MessageElement is the base type of nodes of the SOAP message parse tree.
 *
 * Note: it was made Serializable to help users of Apache SOAP who had
 * exploited the serializability of the DOM tree to migrate to Axis.
 */
public class MessageElement implements SOAPElement,
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

    protected String    name ;
    protected String    prefix ;
    protected String    namespaceURI ;
    protected transient Attributes attributes = NullAttributes.singleton;
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
        addMapping(new Mapping(namespace, prefix));
    }

    public MessageElement(Name eltName)
    {
        this(eltName.getLocalName(),eltName.getPrefix(), eltName.getURI());
    }

    public MessageElement(String namespace, String localPart, Object value)
    {
        this(namespace, localPart);
        objectValue = value;
    }

    public MessageElement(QName name, Object value)
    {
        this(name.getNamespaceURI(), name.getLocalPart());
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

            // Set the encoding style to the attribute value.  If null,
            // we just automatically use our parent's (see getEncodingStyle)
            MessageContext mc = context.getMessageContext();
            SOAPConstants sc = (mc != null) ?
                                            mc.getSOAPConstants() :
                                            SOAPConstants.SOAP11_CONSTANTS;

            href = attributes.getValue(sc.getAttrHref());

            // If there's an arrayType attribute, we can pretty well guess that we're an Array???
            if (attributes.getValue(Constants.URI_DEFAULT_SOAP_ENC, Constants.ATTR_ARRAY_TYPE) != null)
                typeQName = Constants.SOAP_ARRAY;


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
    public void setDirty(boolean dirty) { _isDirty = dirty; }

    public boolean isRoot() { return _isRoot; }
    public String getID() { return id; }

    public String getHref() { return href; }

    public Attributes getAttributesEx() { return attributes; }

    public Node getFirstChild() {
        if(children != null && !children.isEmpty()){
            return (Node)children.get(0);
        }else{
            return null;
        }
    }

    public Node getLastChild() {
        ArrayList children = getChildren();
        if(children != null)
            return (Node)children.get(children.size()-1);
        else
            return null;
    }

    public Node getNextSibling() {
        SOAPElement parent = getParentElement();
        if(parent == null){
            return null;
        }
        Iterator iter = parent.getChildElements();
        Node nextSibling = null;
        while(iter.hasNext()) {
            if(iter.next().equals(this)){
                if(iter.hasNext()){
                    return (Node)iter.next();
                }else{
                    return null;
                }
            }
        }
        return nextSibling; // should be null.
    }

    public Node getParentNode() {
        return parent;
    }

    public Node getPreviousSibling() {
        SOAPElement parent = getParentElement();
        Iterator iter = parent.getChildElements();
        Node previousSibling = null;
        while(iter.hasNext()) {
            if(iter.next().equals(this)){
                return  previousSibling;
            }
        }
        return previousSibling; // should be null.
    }

    public Node cloneNode(boolean deep) {
        try{
            MessageElement clonedSelf = (MessageElement) this.clonning();

            if(deep == true){
                if(children != null){
                    for(int i =0; i < children.size(); i++){
                        MessageElement child = (MessageElement)children.get(i);
                        if(child != null) {  // why child can be null?
                            MessageElement clonedChild = (MessageElement)child.cloneNode(deep); // deep == true
                            clonedChild.setParent(clonedSelf);
                            clonedChild.setOwnerDocument(soapPart);
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
     *  protected boolean   _isDirty = false;  Y?
     *  protected transient DeserializationContext context;  Y?
     *  protected transient QName typeQName = null;          Y?
     *  protected Vector qNameAttrs = null;                  Y?
     *  protected transient SAX2EventRecorder recorder = null; N?
     *  protected int startEventIndex = 0;                   N?
     *  protected int startContentsIndex = 0;                N?
     *  protected int endEventIndex = -1;                    N?
     *  protected Element elementRep = null;                N?
     *  protected Text textRep = null;                      Y?
     *  protected MessageElement parent = null;             N
     *  public ArrayList namespaces = null;                 Y
     *  protected String encodingStyle = null;              N?
     *   private Object objectValue = null;                 N?
     *
     * @return
     * @throws CloneNotSupportedException
     */
    protected Object clonning() throws CloneNotSupportedException
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
            // clear reference to old children
            clonedME.detachAllChildren();

            // clear parents relationship to old parent
            clonedME.setParent(null);
            // clonedME.setObjectValue(objectValue); // how to copy this???
            clonedME.setDirty(this._isDirty);
            if(encodingStyle != null){
                clonedME.setEncodingStyle(new String(encodingStyle));
            }
            clonedME.setRecorder(recorder);
            return clonedME;
        }catch(Exception ex){
            return null;
        }
    }

    // called in MESerialaizationContext
    public void setAllAttributes(Attributes attrs){
        attributes = attrs;
    }

    public void detachAllChildren(){
        children =  new ArrayList();
    }
    
    public NodeList getChildNodes() {
        return this; 
    }

    public boolean isSupported(String feature, String version) {
        return false;  //TODO: Fix this for SAAJ 1.2 Implementation
    }

    public Node appendChild(Node newChild) throws DOMException {
        if(children == null) children = new ArrayList();
        children.add(newChild);
        return newChild;
    }

    public Node removeChild(Node oldChild) throws DOMException {
        if(children == null) children = new ArrayList();
        int position = children.indexOf(oldChild);
        if(position < 0)
            throw new  DOMException(DOMException.NOT_FOUND_ERR,"MessageElement Not found");;
            children.remove(position);
            return oldChild;
    }

    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        if(children == null) children = new ArrayList();
        int position = children.indexOf(refChild);
        if(position < 0)  position = 0;
        children.add(position,newChild);
        return newChild;
    }

    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        if(children == null) children = new ArrayList();
        int position = children.indexOf(oldChild);
        if(position < 0)
            throw new  DOMException(DOMException.NOT_FOUND_ERR,"MessageElement Not found");;
            children.remove(position);
            children.add(position, newChild);
            return oldChild;
    }

    /**
     * Obtain an Attributes collection consisting of all attributes
     * for this MessageElement, including namespace declarations.
     *
     * @return Attributes collection
     */
    public Attributes getCompleteAttributes() {
        if (namespaces == null)
            return attributes;
        
        AttributesImpl attrs = null;
        if (attributes == NullAttributes.singleton)
            attrs = new AttributesImpl();
        else
            attrs = new AttributesImpl(attributes);
        
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
    
    public QName getQName() { return new QName(namespaceURI, name); }
    public void setQName(QName qName) {
        this.name = qName.getLocalPart();
        this.namespaceURI = qName.getNamespaceURI();
    }

    public String getPrefix() { return( prefix ); }

    public void setNodeValue(String nodeValue) throws DOMException {
        throw new DOMException(DOMException.NO_DATA_ALLOWED_ERR,
                "Cannot use TextNode.set in " + this);
    }

    public void setPrefix(String prefix) { this.prefix = prefix; }

    public Document getOwnerDocument() {
        return soapPart;
    }

    public NamedNodeMap getAttributes() {
        // make first it is editable.
        makeAttributesEditable();
        return convertAttrSAXtoDOM(attributes);
    }

    /**
     * @todo  In order to be compatible SAAJ Spec(ver 1.2),
     * The internal representation of Attributes cannot help being changed
     * It is because Attribute is not immutible Type, so if we keep out value and
     * just return it in another form, the application may chnae it, which we cannot
     * detect without some kind back track method (call back notifying the chnage.)
     * I am not sure which approach is better.
     *
     */

    private NamedNodeMap convertAttrSAXtoDOM(Attributes saxAttr)
    {
        try{
            org.w3c.dom.Document doc = org.apache.axis.utils.XMLUtils.newDocument();

            AttributesImpl saxAttrs =  (AttributesImpl)saxAttr;
            NamedNodeMap domAttributes = new NamedNodeMapImpl();
            for(int i = 0; i < saxAttrs.getLength(); i++){
                String uri = saxAttrs.getURI(i);
                String qname = saxAttrs.getQName(i);
                String value = saxAttrs.getValue(i);

                if(uri != null && uri.trim().length() > 0){
                    // filterring out the tricky method to differentiate the null namespace
                    // -ware case
                    if(uri.equals("intentionalNullURI")){
                        uri = null;
                    }
                    Attr attr = doc.createAttributeNS(uri,qname);
                    attr.setValue(value);
                    domAttributes.setNamedItemNS(attr);
                }else{

                    Attr attr = doc.createAttribute(qname);
                    attr.setValue(value);
                    domAttributes.setNamedItem(attr);
                }
            }
            return domAttributes;

        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }

    }
    
    public short getNodeType() {
        if(this.textRep != null) {
            return TEXT_NODE;
        }else if(false){
            return DOCUMENT_FRAGMENT_NODE;
        }else if(false){
            return Node.ELEMENT_NODE;
        }else{ // most often but we cannot give prioeity now
            return Node.ELEMENT_NODE;
        }
    }

    public void normalize() {
        //TODO: Fix this for SAAJ 1.2 Implementation
    }

    public boolean hasAttributes() {
        return attributes.getLength() > 0;
    }

    public boolean hasChildNodes() {
        return children.size() > 0;
    }

    public String getLocalName() {
        return name;  
    }

    public String getNamespaceURI() { return( namespaceURI ); }

    public String getNodeName() {
        return (prefix != null)? prefix + ":" + name : name;
    }

    public String getNodeValue() throws DOMException {
        throw new DOMException(DOMException.NO_DATA_ALLOWED_ERR,
                "Cannot use TextNode.get in " + this);
    }

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

    public void setType(QName qname) {
        typeQName = qname;
    }

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

    public void removeContents() {
        // unlink
        if(children != null){
            for(int i = 0; i < children.size(); i++){
                try{
                    ((MessageElement)children.get(i)).setParent(null);
                }catch(Exception e){
                }
            }
            // empty the collection
            children.clear();
        }
    }

    public Iterator getVisibleNamespacePrefixes() {
        Vector prefixes = new Vector();

        // Add all parents namespace definitions
        if(parent !=null){
            Iterator parentsPrefixes = parent.getVisibleNamespacePrefixes();
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
            SOAPException exc = new SOAPException(Messages.getMessage("valuePresent"));
            log.error(Messages.getMessage("valuePresent"), exc);
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
    public void removeChild(MessageElement child) {
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

        if (href != null && getRealElement() != null) {
            return getRealElement().getPrefix(namespaceURI);
        }

        for (int i = 0; namespaces != null && i < namespaces.size(); i++) {
            Mapping map = (Mapping)namespaces.get(i);
            if (map.getNamespaceURI().equals(namespaceURI))
                return map.getPrefix();
        }

        if (parent != null)
            return parent.getPrefix(namespaceURI);

        return null;
    }

    public String getNamespaceURI(String prefix) {
        if (prefix == null)
            prefix = "";

        if (href != null && getRealElement() != null) {
            return getRealElement().getNamespaceURI(prefix);
        }

        for (int i = 0; namespaces != null && i < namespaces.size(); i++) {
            Mapping map = (Mapping)namespaces.get(i);
            if (map.getPrefix().equals(prefix)) {
                return map.getNamespaceURI();
            }
        }

        if (parent != null)
            return parent.getNamespaceURI(prefix);

        if (log.isDebugEnabled()) {
            log.debug(Messages.getMessage("noPrefix00", "" + this, prefix));
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
        if (elementRep != null) {
            SOAPException exc = new SOAPException(Messages.getMessage("xmlPresent"));
            log.error(Messages.getMessage("xmlPresent"), exc);
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
    public Object getValueAsType(QName type, Class cls) throws Exception
    {
        if (context == null)
            throw new Exception(Messages.getMessage("noContext00"));

        Deserializer dser = null;
        if (cls == null) {
            dser = context.getDeserializerForType(type);
        } else {
            dser = context.getDeserializerForClass(cls);
        }
        if (dser == null)
            throw new Exception(Messages.getMessage("noDeser00", "" + type));

        boolean oldVal = context.isDoneParsing();
        ((DeserializationContextImpl)context).deserializing(true);
        context.pushElementHandler(new EnvelopeHandler((SOAPHandler)dser));

        publishToHandler((org.xml.sax.ContentHandler) context);

        ((DeserializationContextImpl)context).deserializing(oldVal);

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

    protected AttributesImpl makeAttributesEditable() {
        if (attributes == null || attributes instanceof NullAttributes) {
            attributes =  new AttributesImpl();
        } else if (!(attributes instanceof AttributesImpl)) {
            attributes = new AttributesImpl(attributes);
        }

        return (AttributesImpl) attributes;
    }

    public void addAttribute(String namespace, String localName,
                             String value)
    {
        AttributesImpl attributes = makeAttributesEditable();
        attributes.addAttribute(namespace, localName, "", "CDATA",
                                value);
    }

    public void addAttribute(String prefix, String namespace, String localName,
                             String value)
    {
        AttributesImpl attributes = makeAttributesEditable();
        String attrName = localName;
        if (prefix != null && prefix.length() > 0) {
            attrName = prefix + ":" + localName;
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

    public String getAttributeValue(String localName)
    {
        if (attributes == null) {
           return null;
        }
        return attributes.getValue(localName);
    }

    public void setEnvelope(SOAPEnvelope env)
    {
        env.setDirty(true);
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
                    Messages.getMessage("noDoc00", elementString));
        return doc;
    }

    public String getAsString() throws Exception {
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
            throw new SAXException(Messages.getMessage("noRecorder00"));

        recorder.replay(startEventIndex, endEventIndex, handler);
    }

    public void publishContents(ContentHandler handler) throws SAXException
    {
        if (recorder == null)
            throw new SAXException(Messages.getMessage("noRecorder00"));

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
                setAttribute(attrName.getNamespaceURI(),
                             attrName.getLocalPart(),
                             context.qName2String(attr.value));
            }
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
            context.writeSafeString(textRep.getData());
            context.setPretty(oldPretty);
            return;
        }

        if (prefix != null)
            context.registerPrefixForURI(prefix, namespaceURI);

        if (namespaces != null) {
            for (Iterator i = namespaces.iterator(); i.hasNext();) {
                Mapping mapping = (Mapping) i.next();
                context.registerPrefixForURI(mapping.getPrefix(), mapping.getNamespaceURI());
            }
        }            

        if (objectValue != null) {
            context.serialize(new QName(namespaceURI, name),
                              attributes,
                              objectValue, null, false, null);
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
            log.error(Messages.getMessage("exception00"), exp);
            return null;
        }
    }

    public void addMapping(Mapping map) {
        if (namespaces == null) 
            namespaces = new ArrayList();
        namespaces.add(map);
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
            throw new IllegalArgumentException(Messages.getMessage("nullParent00"));
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

    public void setValue(String value) {
        if(this instanceof org.apache.axis.message.Text){
            this.setNodeValue(value);
            return;
        }
        if(children != null)
            for(int i = 0; i < children.size(); i++){
                MessageElement child = (MessageElement)children.get(i);
                if(child instanceof org.apache.axis.message.Text){
                    child.setValue(value);
                    return;
                }
            }
        throw new IllegalStateException("Cannot call set for Non Text Node");
    }

    // JAXM SOAPElement methods...

    public SOAPElement addChildElement(Name name) throws SOAPException {
        MessageElement child = new MessageElement(name.getLocalName(), 
                                                  name.getPrefix(),
                                                  name.getURI());
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
            addChild(text);
            return this;
        } catch (ClassCastException e) {
            throw new SOAPException(e);
        }
    }

    public SOAPElement addAttribute(Name name, String value)
        throws SOAPException {
        try {
            addAttribute(name.getPrefix(), name.getURI(), name.getLocalName(), value);
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

    public Iterator getNamespacePrefixes() {
        Vector prefixes = new Vector();
        for (int i = 0; namespaces != null && i < namespaces.size(); i++) {
            prefixes.add(((Mapping)namespaces.get(i)).getPrefix());
        }
        return prefixes.iterator();
    }

    public Name getElementName() {
        return new PrefixedQName(getNamespaceURI(), getName(), getPrefix());
    }

    public boolean removeAttribute(Name name) {
        AttributesImpl attributes = makeAttributesEditable();
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
        makeAttributesEditable();
        boolean removed = false;

        for (int i = 0; namespaces != null && i < namespaces.size() && !removed; i++) {
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

    public String getTagName() {
        return prefix == null ? name : prefix + ":" + name;
    }

    public void removeAttribute(String name) throws DOMException {
        AttributesImpl impl =  (AttributesImpl)attributes;
        int index = impl.getIndex(name);
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

    public boolean hasAttribute(String name) {
        if(name == null)  // Do I have to send an exception?
            name = "";

        for(int i = 0; i < attributes.getLength(); i++){
            if(name.equals(attributes.getQName(i)))
                return true;
        }
        return false;
    }

    public String getAttribute(String name) {
        return  attributes.getValue(name);
    }

    public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
        makeAttributesEditable();
        Name name =  new PrefixedQName(namespaceURI, localName, null);
        removeAttribute(name);
    }

    public void setAttribute(String name, String value) throws DOMException {
        AttributesImpl impl =  makeAttributesEditable();
        int index = impl.getIndex(name);
        if(index < 0){ // not found
            String uri = "";
            String localname  = name;
            String qname = name;     
            String type = "CDDATA";  
            impl.addAttribute(uri,localname,qname,type,value);
        }else{         // found
            impl.setLocalName(index, value);
        }
    }

    public boolean hasAttributeNS(String namespaceURI, String localName) {
        if(namespaceURI == null)
            namespaceURI ="";
        if(localName == null)  // Do I have to send an exception? or just return false
            localName = "";

        for(int i = 0; i < attributes.getLength(); i++){
            if( namespaceURI.equals(attributes.getURI(i))
                    && localName.equals(attributes.getLocalName(i)))
                return true;
        }
        return false;
    }

    public Attr getAttributeNode(String name) {
        return null;  //TODO: Fix this for SAAJ 1.2 Implementation
    }

    public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
        makeAttributesEditable();
        Name name =  new PrefixedQName(oldAttr.getNamespaceURI(), oldAttr.getLocalName(), oldAttr.getPrefix());
        removeAttribute(name);
        return oldAttr;
    }

    public Attr setAttributeNode(Attr newAttr) throws DOMException {
        return newAttr;
    }

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

    public NodeList getElementsByTagName(String name) {
        //use this MessageElement class for Nodelist store
        MessageElement nodelist = new MessageElement();

        try{
            if(children != null){
                // add 2nd Generation
                for(int i =0; i < children.size(); i++){
                    nodelist.addChild((MessageElement)children.get(i));
                }
                // add 3rd Generation
                for(int i =0; i < children.size(); i++){
                    MessageElement child = (MessageElement)children.get(i);
                    NodeList grandsons = child.getElementsByTagName(name);
                    for(int j =0; j < children.size(); j++){
                        nodelist.addChild((MessageElement)grandsons.item(j));
                    }
                }
            }
        }catch(SOAPException se){
            // Shame on me
        }
        return nodelist;
    }

    public String getAttributeNS(String namespaceURI, String localName) {
        for (int i = 0; i < attributes.getLength(); i++) {
            if (attributes.getURI(i).equals(namespaceURI) &&
                    attributes.getLocalName(i).equals(localName)) {
                return  attributes.getValue(i);
            }
        }
        return null;
    }

    public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
        AttributesImpl attributes = makeAttributesEditable();
        String localName =  qualifiedName.substring(qualifiedName.indexOf(":")+1, qualifiedName.length());

        if(namespaceURI == null){
            namespaceURI = "intentionalNullURI";
        }
        attributes.addAttribute(namespaceURI,
                localName,
                qualifiedName,
                "CDATA",
                value);
    }

    public Attr getAttributeNodeNS(String namespaceURI, String localName) {
        return null;  //TODO: Fix this for SAAJ 1.2 Implementation
    }

    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
        return getElementsNS(this,namespaceURI,localName);
    }

    /**
     * helper method for recusively getting the element that has namespace URI and localname
     */
    protected NodeList getElementsNS(org.w3c.dom.Element parent,
                                     String namespaceURI, String localName)
    {
        NodeList children = parent.getChildNodes();
        NodeListImpl matches = new NodeListImpl();
        // Add fisrt the imeediate child
        for(int i =0; i < children.getLength();  i++){
            Element child = (Element)children.item(i);
            if(! (child instanceof Text)){
                if(namespaceURI.equals(child.getNamespaceURI()) &&
                        localName.equals(child.getLocalName())){
                    matches.addNode(child);
                }
                // search the grand-children.
                matches.addNodeList(
                        child.getElementsByTagNameNS(namespaceURI, localName));
            }
        }
        return matches;
    }
    
    protected org.apache.axis.SOAPPart soapPart = null;

    public void setOwnerDocument(org.apache.axis.SOAPPart sp){
        soapPart = sp;
    }

    public Node item(int index) {
        if(children !=null && children.size() > index){
            return (Node)children.get(index);
        }else{
            return null;
        }
    }
    
    /**
     * The number of nodes in the list. The range of valid child node indices
     * is 0 to <code>length-1</code> inclusive.
     *
     * @since SAAJ 1.2 : Nodelist Interface
     */
    public int getLength(){
        if(children  == null){
            children = new ArrayList();
            return 0;
        }
        return children.size();
    }

    // setEncodingStyle implemented above

    // getEncodingStyle() implemented above
    
}
