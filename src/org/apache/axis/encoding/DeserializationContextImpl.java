/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2002 The Apache Software Foundation.  All rights
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
package org.apache.axis.encoding;

import org.apache.axis.attachments.Attachments;

import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.Message;
import org.apache.axis.message.EnvelopeBuilder;
import org.apache.axis.message.EnvelopeHandler;
import org.apache.axis.message.IDResolver;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.NullAttributes;
import org.apache.axis.message.SAX2EventRecorder;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHandler;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.NSStack;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.XMLUtils;

import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler; 
import org.xml.sax.helpers.DefaultHandler;
import org.apache.axis.AxisFault;
import org.apache.axis.soap.SOAPConstants;

import javax.xml.namespace.QName;
import javax.xml.parsers.SAXParser;
import javax.xml.rpc.JAXRPCException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Glen Daniels (gdaniels@macromedia.com)
 * Re-architected for JAX-RPC Compliance by:
 * @author Rich Scheuerle (scheu@us.ibm.com)
 */

public class DeserializationContextImpl extends DefaultHandler implements LexicalHandler, DeserializationContext
{
    protected static Log log =
            LogFactory.getLog(DeserializationContextImpl.class.getName());

    private NSStack namespaces = new NSStack();

    private Locator locator;

    // for performance reasons, keep the top of the stack separate from
    // the remainder of the handlers, and therefore readily available.
    private SOAPHandler topHandler = null;
    private ArrayList pushedDownHandlers = new ArrayList();

    //private SAX2EventRecorder recorder = new SAX2EventRecorder();
    private SAX2EventRecorder recorder = null;
    private SOAPEnvelope envelope;

    /* A map of IDs -> IDResolvers */
    private HashMap idMap;
    private LocalIDResolver localIDs;

    private HashMap fixups;

    static final SOAPHandler nullHandler = new SOAPHandler();

    protected MessageContext msgContext;

    private boolean doneParsing = false;
    protected InputSource inputSource = null;

    private MessageElement curElement;

    protected int startOfMappingsPos = -1;
    
    public void deserializing(boolean isDeserializing) {
        doneParsing = isDeserializing;
    }

    /**
     * Construct Deserializer using MessageContext and EnvelopeBuilder handler
     * @param ctx is the MessageContext
     * @param initialHandler is the EnvelopeBuilder handler
     */
    public DeserializationContextImpl(MessageContext ctx,
                                      SOAPHandler initialHandler)
    {
        msgContext = ctx;

        // If high fidelity is required, record the whole damn thing.
        if (ctx == null || ctx.isHighFidelity())
            recorder = new SAX2EventRecorder();

        if (initialHandler instanceof EnvelopeBuilder) {
            envelope = ((EnvelopeBuilder)initialHandler).getEnvelope();
            envelope.setRecorder(recorder);
        }

        pushElementHandler(new EnvelopeHandler(initialHandler));
    }

    /**
     * Construct Deserializer
     * @param is is the InputSource
     * @param ctx is the MessageContext
     * @param messageType is the MessageType to construct an EnvelopeBuilder
     */
    public DeserializationContextImpl(InputSource is,
                                      MessageContext ctx,
                                      String messageType)
    {
        msgContext = ctx;
        EnvelopeBuilder builder = new EnvelopeBuilder(messageType, getSOAPConstants());
        // If high fidelity is required, record the whole damn thing.
        if (ctx == null || ctx.isHighFidelity())
            recorder = new SAX2EventRecorder();

        envelope = builder.getEnvelope();
        envelope.setRecorder(recorder);

        pushElementHandler(new EnvelopeHandler(builder));

        inputSource = is;
    }

    /**
     * returns the soap constants.
     */
    private SOAPConstants getSOAPConstants(){
        SOAPConstants constants = null;
        if(msgContext != null)
            constants = msgContext.getSOAPConstants();
        if(constants == null)
            constants = SOAPConstants.SOAP11_CONSTANTS;
        return constants;
    }

    /**
     * Construct Deserializer
     * @param is is the InputSource
     * @param ctx is the MessageContext
     * @param messageType is the MessageType to construct an EnvelopeBuilder
     * @param env is the SOAPEnvelope to construct an EnvelopeBuilder
     */
    public DeserializationContextImpl(InputSource is,
                                      MessageContext ctx,
                                      String messageType,
                                      SOAPEnvelope env)
    {
        EnvelopeBuilder builder = new EnvelopeBuilder(env, messageType);

        msgContext = ctx;

        // If high fidelity is required, record the whole damn thing.
        if (ctx == null || ctx.isHighFidelity())
            recorder = new SAX2EventRecorder();

        envelope = builder.getEnvelope();
        envelope.setRecorder(recorder);

        pushElementHandler(new EnvelopeHandler(builder));

        inputSource = is;
    }

    /**
     * Create a parser and parse the inputSource
     */
    public void parse() throws SAXException
    {
        if (inputSource != null) {
            SAXParser parser = XMLUtils.getSAXParser();
            try {
                parser.setProperty("http://xml.org/sax/properties/lexical-handler", this);
                parser.parse(inputSource, this);

                // only release the parser for reuse if there wasn't an
                // error.  While parsers should be reusable, don't trust
                // parsers that died to clean up appropriately.
                XMLUtils.releaseSAXParser(parser);
            } catch (IOException e) {
                throw new SAXException(e);
            }
            inputSource = null;
        }
    }

    /**
     * Get current MessageElement
     **/
    public MessageElement getCurElement() {
        return curElement;
    }

    /**
     * Set current MessageElement
     **/
    public void setCurElement(MessageElement el)
    {
        curElement = el;
        if (curElement != null && curElement.getRecorder() != recorder) {
            recorder = curElement.getRecorder();
        }
    }


    /**
     * Get MessageContext
     */
    public MessageContext getMessageContext()
    {
        return msgContext;
    }

    /**
     * Get Envelope
     */
    public SOAPEnvelope getEnvelope()
    {
        return envelope;
    }

    /**
     * Get Event Recorder
     */
    public SAX2EventRecorder getRecorder()
    {
        return recorder;
    }

    /**
     * Set Event Recorder
     */
    public void setRecorder(SAX2EventRecorder recorder)
    {
        this.recorder = recorder;
    }

    /**
     * Get the Namespace Mappings.  Returns null if none are present.
     **/
    public ArrayList getCurrentNSMappings()
    {
        return namespaces.cloneFrame();
    }

    /**
     * Get the Namespace for a particular prefix
     */
    public String getNamespaceURI(String prefix)
    {
        String result = namespaces.getNamespaceURI(prefix);
        if (result != null)
            return result;

        if (curElement != null)
            return curElement.getNamespaceURI(prefix);

        return null;
    }

    /**
     * Construct a QName from a string of the form <prefix>:<localName>
     * @param qNameStr is the prefixed name from the xml text
     * @return QName
     */
    public QName getQNameFromString(String qNameStr)
    {
        if (qNameStr == null)
            return null;

        // OK, this is a QName, so look up the prefix in our current mappings.
        int i = qNameStr.indexOf(':');
        String nsURI;
        if (i == -1) {
            nsURI = getNamespaceURI("");
        } else {
            nsURI = getNamespaceURI(qNameStr.substring(0, i));
        }

        return new QName(nsURI, qNameStr.substring(i + 1));
    }

    /**
     * Create a QName for the type of the element defined by localName and
     * namespace from the XSI type.
     * @param namespace of the element
     * @param localName is the local name of the element
     * @param attrs are the attributes on the element
     */
    public QName getTypeFromXSITypeAttr(String namespace, String localName,
                                          Attributes attrs) {
        // Check for type
        String type = Constants.getValue(attrs, Constants.URIS_SCHEMA_XSI,
                                         "type");
        if (type != null) {
            // Return the type attribute value converted to a QName
            return getQNameFromString(type);
        } 
        return null;
    }

    /** 
     * Create a QName for the type of the element defined by localName and
     * namespace with the specified attributes.
     * @param namespace of the element
     * @param localName is the local name of the element
     * @param attrs are the attributes on the element
     */
    public QName getTypeFromAttributes(String namespace, String localName,
                                       Attributes attrs)
    {
        QName typeQName = getTypeFromXSITypeAttr(namespace, localName, attrs);
        if ( (typeQName == null) && Constants.isSOAP_ENC(namespace) ) {

            // If the element is a SOAP-ENC element, the name of the element is the type.
            // If the default type mapping accepts SOAP 1.2, then use then set
            // the typeQName to the SOAP-ENC type.
            // Else if the default type mapping accepts SOAP 1.1, then
            // convert the SOAP-ENC type to the appropriate XSD Schema Type.
            if (namespace.equals(Constants.URI_SOAP12_ENC)) {
                typeQName = new QName(namespace, localName);
            } else if (localName.equals(Constants.SOAP_ARRAY.getLocalPart())) {
                typeQName = Constants.SOAP_ARRAY;
            } else if (localName.equals(Constants.SOAP_STRING.getLocalPart())) {
                typeQName = Constants.SOAP_STRING;
            } else if (localName.equals(Constants.SOAP_BOOLEAN.getLocalPart())) {
                typeQName = Constants.SOAP_BOOLEAN;
            } else if (localName.equals(Constants.SOAP_DOUBLE.getLocalPart())) {
                typeQName = Constants.SOAP_DOUBLE;
            } else if (localName.equals(Constants.SOAP_FLOAT.getLocalPart())) {
                typeQName = Constants.SOAP_FLOAT;
            } else if (localName.equals(Constants.SOAP_INT.getLocalPart())) {
                typeQName = Constants.SOAP_INT;
            } else if (localName.equals(Constants.SOAP_LONG.getLocalPart())) {
                typeQName = Constants.SOAP_LONG;
            } else if (localName.equals(Constants.SOAP_SHORT.getLocalPart())) {
                typeQName = Constants.SOAP_SHORT;
            } else if (localName.equals(Constants.SOAP_BYTE.getLocalPart())) {
                typeQName = Constants.SOAP_BYTE;
            }

        }

        return typeQName;
    }

    /**
     * Convenenience method that returns true if the value is nil
     * (due to the xsi:nil) attribute.
     * @param attrs are the element attributes.
     * @return true if xsi:nil is true
     */
    public boolean isNil(Attributes attrs) {
        return JavaUtils.isTrueExplicitly(
                    Constants.getValue(attrs, Constants.QNAMES_NIL),
                    false);
    }

    /**
     * Get a Deserializer which can turn a given xml type into a given
     * Java type
     */
    public final Deserializer getDeserializer(Class cls, QName xmlType) {
        if (xmlType == null)
            return null;

        DeserializerFactory dserF = null;
        Deserializer dser = null;
        try {
            dserF = (DeserializerFactory) getTypeMapping().
                    getDeserializer(cls, xmlType);
        } catch (JAXRPCException e) {
            log.error(Messages.getMessage("noFactory00", xmlType.toString()));
        }
        if (dserF != null) {
            try {
                dser = (Deserializer) dserF.getDeserializerAs(Constants.AXIS_SAX);
            } catch (JAXRPCException e) {
                log.error(Messages.getMessage("noDeser00", xmlType.toString()));
            }
        }
        return dser;
    }

    /**
     * Convenience method to get the Deserializer for a specific
     * xmlType.
     * @param xmlType is QName for a type to deserialize
     * @return Deserializer
     */
    public final Deserializer getDeserializerForType(QName xmlType) {
        return getDeserializer(null, xmlType);
    }

    /**
     * Get the TypeMapping for this DeserializationContext
     */
    public TypeMapping getTypeMapping()
    {
        TypeMappingRegistry tmr = msgContext.getTypeMappingRegistry();
        return (TypeMapping) tmr.getTypeMapping(msgContext.getEncodingStyle());
        /*
         * TODO: This code doesn't yet work, but we aren't looking up the right
         * TypeMapping by just using SOAP_ENC.

        String encStyle = curElement == null ? Constants.NS_URI_CURRENT_SOAP_ENC :
                                               curElement.getEncodingStyle();
        return (TypeMapping) tmr.getTypeMapping(encStyle);
        */
    }

    /**
     * Get the TypeMappingRegistry we're using.
     * @return TypeMapping or null
     */
    public TypeMappingRegistry getTypeMappingRegistry() {
        return (TypeMappingRegistry) msgContext.getTypeMappingRegistry();
    }

    /**
     * Get the MessageElement for the indicated id (where id is the #value of an href)
     * If the MessageElement has not been processed, the MessageElement will
     * be returned.  If the MessageElement has been processed, the actual object
     * value is stored with the id and this routine will return null.
     * @param id is the value of an href attribute
     * @return MessageElement or null
     */
    public MessageElement getElementByID(String id)
    {
        if((idMap !=  null)) {
            IDResolver resolver = (IDResolver)idMap.get(id);
            if(resolver != null) {
                Object ret = resolver.getReferencedObject(id);
                if (ret instanceof MessageElement)
                    return (MessageElement)ret;
            }
        }

        return null;
    }

    /**
     * Gets the MessageElement or actual Object value associated with the href value.
     * The return of a MessageElement indicates that the referenced element has
     * not been processed.  If it is not a MessageElement, the Object is the
     * actual deserialized value.
     * In addition, this method is invoked to get Object values via Attachments.
     * @param href is the value of an href attribute (or an Attachment id)
     * @return MessageElement other Object or null
     */
    public Object getObjectByRef(String href) {
        Object ret= null;
        if(href != null){
            if((idMap !=  null)){
                IDResolver resolver = (IDResolver)idMap.get(href);
                if(resolver != null)
                   ret = resolver.getReferencedObject(href);
            }
            if( null == ret && !href.startsWith("#")){
                //Could this be an attachment?
                Message msg= null;
                if(null != (msg=msgContext.getCurrentMessage())){
                    Attachments attch= null;
                    if( null != (attch= msg.getAttachmentsImpl())){
                        try{
                        ret= attch.getAttachmentByReference(href);
                        }catch(AxisFault e){
                            throw new RuntimeException(e.toString() + JavaUtils.stackToString(e));
                        }
                    }
                }
            }
        }

        return ret;
    }

    /**
     * Add the object associated with this id (where id is the value of an id= attribute,
     * i.e. it does not start with #).
     * This routine is called to associate the deserialized object
     * with the id specified on the XML element.
     * @param id (id name without the #)
     * @param obj is the deserialized object for this id.
     */
    public void addObjectById(String id, Object obj)
    {
        // The resolver uses the href syntax as the key.
        String idStr = '#' + id;
        if ((idMap == null) || (id == null))
            return ;

        IDResolver resolver = (IDResolver)idMap.get(idStr);
        if (resolver == null)
            return ;

        resolver.addReferencedObject(idStr, obj);
        return;
    }

    /**
     * During deserialization, an element with an href=#id<int>
     * may be encountered before the element defining id=id<int> is
     * read.  In these cases, the getObjectByRef method above will
     * return null.  The deserializer is placed in a table keyed
     * by href (a fixup table). After the element id is processed,
     * the deserializer is informed of the value so that it can
     * update its target(s) with the value.
     * @param href (#id syntax)
     * @param dser is the deserializer of the element
     */
    public void registerFixup(String href, Deserializer dser)
    {
        if (fixups == null)
            fixups = new HashMap();

        Deserializer prev = (Deserializer) fixups.put(href, dser);

        // There could already be a deserializer in the fixup list
        // for this href.  If so, the easiest way to get all of the
        // targets updated is to move the previous deserializers
        // targets to dser.
        if (prev != null && prev != dser) {
            dser.moveValueTargets(prev);
            if (dser.getDefaultType() == null) {
                dser.setDefaultType(prev.getDefaultType());
            }
        }
    }

    /**
     * Register the MessageElement with this id (where id is id= form without the #)
     * This routine is called when the MessageElement with an id is read.
     * If there is a Deserializer in our fixup list (described above),
     * the 'fixup' deserializer is given to the MessageElement.  When the
     * MessageElement is completed, the 'fixup' deserializer is informed and
     * it can set its targets.
     * @param id (id name without the #)
     * @param elem is the MessageElement
     */
    public void registerElementByID(String id, MessageElement elem)
    {
        if (localIDs == null)
            localIDs = new LocalIDResolver();

        String absID = '#' + id;

        localIDs.addReferencedObject(absID, elem);

        registerResolverForID(absID, localIDs);

        if (fixups != null) {
            Deserializer dser = (Deserializer)fixups.get(absID);
            if (dser != null) {
                elem.setFixupDeserializer(dser);
            }
        }
    }

    /**
     * Each id can have its own kind of resolver.  This registers a
     * resolver for the id.
     */
    public void registerResolverForID(String id, IDResolver resolver)
    {
        if ((id == null) || (resolver == null)) {
            // ??? Throw nullPointerException?
            return;
        }

        if (idMap == null)
            idMap = new HashMap();

        idMap.put(id, resolver);
    }

    /**
     * Get the current position in the record.
     */
    public int getCurrentRecordPos()
    {
        if (recorder == null) return -1;
        return recorder.getLength() - 1;
    }

    /**
     * Get the start of the mapping position
     */
    public int getStartOfMappingsPos()
    {
        if (startOfMappingsPos == -1) {
            return getCurrentRecordPos() + 1;
        }

        return startOfMappingsPos;
    }

    /**
     * Push the MessageElement into the recorder
     */
    public void pushNewElement(MessageElement elem)
    {
        if (log.isDebugEnabled()) {
            log.debug("Pushing element " + elem.getName());
        }

        if (!doneParsing && (recorder != null)) {
            recorder.newElement(elem);
        }

        try {
            if(curElement != null)
                elem.setParentElement(curElement);
        } catch (Exception e) {
            /*
             * The only checked exception that may be thrown from setParent
             * occurs if the parent already has an explicit object value,
             * which should never occur during deserialization.
             */
            log.fatal(Messages.getMessage("exception00"), e);
        }
        curElement = elem;

        if (elem.getRecorder() != recorder)
            recorder = elem.getRecorder();
    }

    /****************************************************************
     * Management of sub-handlers (deserializers)
     */

    public void pushElementHandler(SOAPHandler handler)
    {
        if (log.isDebugEnabled()) {
            log.debug(Messages.getMessage("pushHandler00", "" + handler));
        }

        if (topHandler != null) pushedDownHandlers.add(topHandler);
        topHandler = handler;
    }

    /** Replace the handler at the top of the stack.
     *
     * This is only used when we have a placeholder Deserializer
     * for a referenced object which doesn't know its type until we
     * hit the referent.
     */
    public void replaceElementHandler(SOAPHandler handler)
    {
        topHandler = handler;
    }

    public SOAPHandler popElementHandler()
    {
        SOAPHandler result = topHandler;

        int size = pushedDownHandlers.size();
        if (size > 0) {
            topHandler = (SOAPHandler) pushedDownHandlers.remove(size-1);
        } else {
            topHandler = null;
        }

        if (log.isDebugEnabled()) {
            if (result == null) {
                log.debug(Messages.getMessage("popHandler00", "(null)"));
            } else {
                log.debug(Messages.getMessage("popHandler00", "" + result));
            }
        }

        return result;
    }

    boolean processingRef = false;
    public void setProcessingRef(boolean ref) {
        processingRef = ref;
    }
    public boolean isProcessingRef() {
        return processingRef;
    }

    /****************************************************************
     * SAX event handlers
     */
    public void startDocument() throws SAXException {
        // Should never receive this in the midst of a parse.
        if (!doneParsing && (recorder != null))
            recorder.startDocument();
    }

    /**
     * endDocument is invoked at the end of the document.
     */
    public void endDocument() throws SAXException {
        if (log.isDebugEnabled()) {
            log.debug("Enter: DeserializationContextImpl::endDocument()");
        }
        if (!doneParsing && (recorder != null)) 
            recorder.endDocument();

        doneParsing = true;

        if (log.isDebugEnabled()) {
            log.debug("Exit: DeserializationContextImpl::endDocument()");
        }
    }
    /**
     * Return if done parsing document.
     */
    public boolean isDoneParsing() {return doneParsing;}

    /** Record the current set of prefix mappings in the nsMappings table.
     *
     * !!! We probably want to have this mapping be associated with the
     *     MessageElements, since they may potentially need access to them
     *     long after the end of the prefix mapping here.  (example:
     *     when we need to record a long string of events scanning forward
     *     in the document to find an element with a particular ID.)
     */
    public void startPrefixMapping(String prefix, String uri)
        throws SAXException
    {
        if (log.isDebugEnabled()) {
            log.debug("Enter: DeserializationContextImpl::startPrefixMapping(" + prefix + ", " + uri + ")");
        }

        if (!doneParsing && (recorder != null)) {
            recorder.startPrefixMapping(prefix, uri);
        }

        if (startOfMappingsPos == -1) {
            startOfMappingsPos = getCurrentRecordPos();
        }

        if (prefix != null) {
            namespaces.add(uri, prefix);
        } else {
            namespaces.add(uri, "");
        }

        if (topHandler != null) {
            topHandler.startPrefixMapping(prefix, uri);
        }

        if (log.isDebugEnabled()) {
            log.debug("Exit: DeserializationContextImpl::startPrefixMapping()");
        }
    }

    public void endPrefixMapping(String prefix)
        throws SAXException
    {
        if (log.isDebugEnabled()) {
            log.debug("Enter: DeserializationContextImpl::endPrefixMapping(" + prefix + ")");
        }

        if (!doneParsing && (recorder != null)) {
            recorder.endPrefixMapping(prefix);
        }

        if (topHandler != null) {
            topHandler.endPrefixMapping(prefix);
        }

        if (log.isDebugEnabled()) {
            log.debug("Exit: DeserializationContextImpl::endPrefixMapping()");
        }
    }

    public void setDocumentLocator(Locator locator)
    {
        if (!doneParsing && (recorder != null)) {
            recorder.setDocumentLocator(locator);
        }
        this.locator = locator;
    }

    public Locator getDocumentLocator() {
        return locator;
    }

    public void characters(char[] p1, int p2, int p3) throws SAXException {
        if (!doneParsing && (recorder != null)) {
            recorder.characters(p1, p2, p3);
        }
        if (topHandler != null) {
            topHandler.characters(p1, p2, p3);
        }
    }

    public void ignorableWhitespace(char[] p1, int p2, int p3) throws SAXException {
        if (!doneParsing && (recorder != null)) {
            recorder.ignorableWhitespace(p1, p2, p3);
        }
        if (topHandler != null) {
            topHandler.ignorableWhitespace(p1, p2, p3);
        }
    }

    public void processingInstruction(String p1, String p2) throws SAXException {
        // must throw an error since SOAP 1.1 doesn't allow
        // processing instructions anywhere in the message
        throw new SAXException(Messages.getMessage("noInstructions00"));
    }

    public void skippedEntity(String p1) throws SAXException {
        if (!doneParsing && (recorder != null)) {
            recorder.skippedEntity(p1);
        }
        topHandler.skippedEntity(p1);
    }

    /**
     * startElement is called when an element is read.  This is the big work-horse.
     *
     * This guy also handles monitoring the recording depth if we're recording
     * (so we know when to stop).
     */
    public void startElement(String namespace, String localName,
                             String qName, Attributes attributes)
        throws SAXException
    {
        if (log.isDebugEnabled()) {
            log.debug("Enter: DeserializationContextImpl::startElement(" + namespace + ", " + localName + ")");
        }

        if (attributes == null || attributes.getLength() == 0) {
            attributes = NullAttributes.singleton;
        } else {
            attributes = new AttributesImpl(attributes);
        }

        SOAPHandler nextHandler = null;

        String prefix = "";
        int idx = qName.indexOf(':');
        if (idx > 0) {
            prefix = qName.substring(0, idx);
        }

        if (topHandler != null) {
            nextHandler = topHandler.onStartChild(namespace,
                                                       localName,
                                                       prefix,
                                                       attributes,
                                                       this);
        }

        if (nextHandler == null) {
            nextHandler = new SOAPHandler();
        }

        pushElementHandler(nextHandler);

        nextHandler.startElement(namespace, localName, prefix,
                                 attributes, this);

        if (!doneParsing && (recorder != null)) {
            recorder.startElement(namespace, localName, qName,
                                  attributes);
            if (!doneParsing) {
                curElement.setContentsIndex(recorder.getLength());
            }
        }

        namespaces.push();

        startOfMappingsPos = -1;

        if (log.isDebugEnabled()) {
            log.debug("Exit: DeserializationContextImpl::startElement()");
        }
    }

    /**
     * endElement is called at the end tag of an element
     */
    public void endElement(String namespace, String localName, String qName)
        throws SAXException
    {
        if (log.isDebugEnabled()) {
            log.debug("Enter: DeserializationContextImpl::endElement(" + namespace + ", " + localName + ")");
        }

        if (!doneParsing && (recorder != null)) {
            recorder.endElement(namespace, localName, qName);
        }

        try {
            SOAPHandler handler = popElementHandler();
            handler.endElement(namespace, localName, this);

            if (topHandler != null) {
                topHandler.onEndChild(namespace, localName, this);
            } else {
                // We should be done!
            }

        } finally {
            if (curElement != null) {
                curElement = (MessageElement)curElement.getParentElement();
            }

            namespaces.pop();

            if (log.isDebugEnabled()) {
                String name = curElement != null ?
                        curElement.getClass().getName() + ":" +
                        curElement.getName() : null;
                log.debug("Popped element stack to " + name);
                log.debug("Exit: DeserializationContextImpl::endElement()");
            }
        }
    }

    /**
     * This class is used to map ID's to an actual value Object or Message
     */
    private static class LocalIDResolver implements IDResolver
    {
        HashMap idMap = null;

        /**
         * Add object associated with id
         */
        public void addReferencedObject(String id, Object referent)
        {
            if (idMap == null) {
                idMap = new HashMap();
            }

            idMap.put(id, referent);
        }

        /**
         * Get object referenced by href
         */
        public Object getReferencedObject(String href)
        {
            if ((idMap == null) || (href == null)) {
                return null;
            }
            return idMap.get(href);
        }
    }
    
    public void startDTD(java.lang.String name,
                     java.lang.String publicId,
                     java.lang.String systemId)
              throws SAXException
    {
        recorder.startDTD(name, publicId, systemId);
    }
    
    public void endDTD()
            throws SAXException
    {
        recorder.endDTD();
    }
    
    public void startEntity(java.lang.String name)
                 throws SAXException
    {
        recorder.startEntity(name);
    }
    
    public void endEntity(java.lang.String name)
               throws SAXException
    {
        recorder.endEntity(name);
    }
    
    public void startCDATA()
                throws SAXException
    {
        recorder.startCDATA();
    }
    
    public void endCDATA()
              throws SAXException
    {
        recorder.endCDATA();
    }
    
    public void comment(char[] ch,
                    int start,
                    int length)
             throws SAXException
    {
        recorder.comment(ch, start, length);
    }
}

