package org.apache.axis.encoding;

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

import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.message.EnvelopeBuilder;
import org.apache.axis.message.EnvelopeHandler;
import org.apache.axis.message.HandlerFactory;
import org.apache.axis.message.IDResolver;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.SAX2EventRecorder;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHandler;
import org.apache.axis.utils.NSStack;
import org.apache.axis.utils.XMLUtils;
import org.apache.log4j.Category;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.rpc.namespace.QName;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/** 
 * @author Glen Daniels (gdaniels@macromedia.com)
 */

public class DeserializationContext extends DefaultHandler
{
    static Category category =
            Category.getInstance(DeserializationContext.class.getName());
    
    static class LocalIDResolver implements IDResolver
    {
        HashMap idMap = null;

        /**
         * Add object associated with id
         */
        public void addReferencedObject(String id, Object referent)
        {
            if (idMap == null)
                idMap = new HashMap();
            
            idMap.put(id, referent);
        }
        
        /**
         * Get object regferenced by href
         */
        public Object getReferencedObject(String href)
        {
            if ((idMap == null) || (href == null))
                return null;
            return idMap.get(href);
        }
    }
    
    private NSStack namespaces = new NSStack();
    
    private Locator locator;
                                             
    Stack handlerStack = new Stack();
    
    SAX2EventRecorder recorder = new SAX2EventRecorder();
    public SOAPEnvelope envelope;
    
    /** A map of IDs -> IDResolvers
     */
    HashMap idMap;
    LocalIDResolver localIDs;
    
    HashMap fixups;
    
    static final SOAPHandler nullHandler = new SOAPHandler();
    
    protected MessageContext msgContext;
    
    protected HandlerFactory initialFactory;
    
    public boolean doneParsing = false;
    protected InputSource inputSource = null;
        
    public DeserializationContext(MessageContext ctx, EnvelopeBuilder initialHandler)
    {
        msgContext = ctx;
        
        envelope = initialHandler.getEnvelope();
        envelope.setRecorder(recorder);
        
        pushElementHandler(new EnvelopeHandler(initialHandler));
    }
    
    MessageElement curElement;
    
    public void setCurElement(MessageElement el)
    {
        curElement = el;
    }
    
    public DeserializationContext(InputSource is, MessageContext ctx, 
                                  String messageType)
    {
        EnvelopeBuilder builder = new EnvelopeBuilder(messageType);
        
        msgContext = ctx;
        
        envelope = builder.getEnvelope();
        envelope.setRecorder(recorder);
        
        pushElementHandler(new EnvelopeHandler(builder));

        inputSource = is;
    }
    
    public DeserializationContext(InputSource is, MessageContext ctx, 
                                  String messageType, SOAPEnvelope env)
    {
        EnvelopeBuilder builder = new EnvelopeBuilder(env, messageType);
        
        msgContext = ctx;
        
        envelope = builder.getEnvelope();
        envelope.setRecorder(recorder);
        
        pushElementHandler(new EnvelopeHandler(builder));

        inputSource = is;
    }
    
    public void parse() throws SAXException
    {
        if (inputSource != null) {
            SAXParser parser = XMLUtils.getSAXParser();
            try {
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
    
    public MessageContext getMessageContext()
    {
        return msgContext;
    }
    
    public SOAPEnvelope getEnvelope()
    {
        return envelope;
    }
    
    public SAX2EventRecorder getRecorder()
    {
        return recorder;
    }
    
    public ArrayList getCurrentNSMappings()
    {
        return (ArrayList)namespaces.peek().clone();
    }
    
    /** Grab a namespace prefix
     */
    public String getNamespaceURI(String prefix)
    {
        if (curElement != null)
            return curElement.getNamespaceURI(prefix);

        return namespaces.getNamespaceURI(prefix);
    }
    
    public QName getQNameFromString(String qNameStr)
    {
        if (qNameStr == null)
            return null;
        
        // OK, this is a QName, so look up the prefix in our current mappings.
        
        int i = qNameStr.indexOf(':');
        if (i == -1)
            return null;
        
        String nsURI = getNamespaceURI(qNameStr.substring(0, i));
        
        //System.out.println("namespace = " + nsURI);
        
        if (nsURI == null)
            return null;
        
        return new QName(nsURI, qNameStr.substring(i + 1));
    }
    
    public QName getTypeFromAttributes(String namespace, String localName,
                                       Attributes attrs)
    {
        QName typeQName = null;
        
        if (typeQName == null) {
            QName myQName = new QName(namespace, localName);
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

        if (typeQName != null)
            return typeQName;
        
        if (attrs == null)
            return null;
        
        // Check for type
        String type = null;
        for (int i=0; i<Constants.URIS_SCHEMA_XSI.length && type==null; i++)
            type = attrs.getValue(Constants.URIS_SCHEMA_XSI[i], "type");
        
        if (type == null)
            return null;

        return getQNameFromString(type);
    }
    
    public TypeMappingRegistry getTypeMappingRegistry()
    {
        return msgContext.getTypeMappingRegistry();
    }
    
    /**
     * Get the object referenced by the href.
     * The object returned may be a MessageElement requiring deserialization or it 
     * may be a deserialized java object.
     */
    public Object getObjectByRef(String href)
    {
        if ((idMap == null) || (href == null))
            return null;
        
        IDResolver resolver = (IDResolver)idMap.get(href);
        if (resolver == null)
            return null;
        
        return resolver.getReferencedObject(href);
    }
    
    /**
     * Add the object associated with this id.
     * This routine is called to associate the deserialized object
     * with the id specified on the XML element. 
    */
    public void addObjectById(String _id, Object obj)
    {
        // The resolver uses the href syntax as the key.
        String id = "#" + _id;
        if ((idMap == null) || (id == null))
            return ;
        
        IDResolver resolver = (IDResolver)idMap.get(id);
        if (resolver == null)
            return ;
        
        resolver.addReferencedObject(id, obj);
        return;
    }

    public void registerFixup(String id, Deserializer dser)
    {
        if (fixups == null)
            fixups = new HashMap();
        
        fixups.put(id, dser);
    }
    
    public void registerElementByID(String id, MessageElement elem)
    {
        if (localIDs == null)
            localIDs = new LocalIDResolver();
        
        String absID = "#" + id;
        
        localIDs.addReferencedObject(absID, elem);
        
        registerResolverForID(absID, localIDs);
        
        if (fixups != null) {
            Deserializer dser = (Deserializer)fixups.get(absID);
            if (dser != null) {
                elem.setFixupDeserializer(dser);
            }
        }
    }
    
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
    
    public int getCurrentRecordPos()
    {
        if (recorder == null) return -1;
        return recorder.getLength() - 1;
    }
    
    protected int startOfMappingsPos = -1;
    
    public int getStartOfMappingsPos()
    {
        if (startOfMappingsPos == -1) {
            return getCurrentRecordPos() + 1;
        }
        
        return startOfMappingsPos;
    }
    
    public void pushNewElement(MessageElement elem)
    {
        if (recorder != null) {
            recorder.newElement(elem);
        }
        
        elem.setParent(curElement);
        curElement = elem;
    }
    
    /****************************************************************
     * Management of sub-handlers (deserializers)
     */
    
    public SOAPHandler getTopHandler()
    {
        try {
            return (SOAPHandler)handlerStack.peek();
        } catch (Exception e) {
            return null;
        }
    }
    
    public void pushElementHandler(SOAPHandler handler)
    {
        if (category.isDebugEnabled()) {
            category.debug("Pushing handler " + handler);
        }
        
        handlerStack.push(handler);
    }
    
    /** Replace the handler at the top of the stack.
     * 
     * This is only used when we have a placeholder Deserializer
     * for a referenced object which doesn't know its type until we
     * hit the referent.
     */
    void replaceElementHandler(SOAPHandler handler)
    {
        handlerStack.pop();
        handlerStack.push(handler);
    }
    
    public SOAPHandler popElementHandler()
    {
        if (!handlerStack.empty()) {
            SOAPHandler handler = getTopHandler();
            if (category.isDebugEnabled()) {
                category.debug("Popping handler " + handler);
            }
            handlerStack.pop();
            return handler;
        } else {
            if (category.isDebugEnabled()) {
                category.debug("Popping handler...(null)");
            }
            return null;
        }
    }
    
    /****************************************************************
     * SAX event handlers
     */
    public void startDocument() throws SAXException {
        // Should never receive this in the midst of a parse.
        if (recorder != null)
            recorder.startDocument();
    }
    
    public void endDocument() throws SAXException {
        if (category.isDebugEnabled()) {
            category.debug("EndDocument");
        }
        if (recorder != null)
            recorder.endDocument();
        
        doneParsing = true;
    }
    
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
        if (recorder != null)
            recorder.startPrefixMapping(prefix, uri);
        
        if (startOfMappingsPos == -1)
            startOfMappingsPos = getCurrentRecordPos();
        
        if (prefix != null) {
            namespaces.add(uri, prefix);
        } else {
            namespaces.add(uri, "");
        }
       
        if (category.isDebugEnabled()) {
            category.debug("StartPrefixMapping '" + prefix + "'->'" + uri + "'");
        }
        
        SOAPHandler handler = getTopHandler();
        if (handler != null)
            handler.startPrefixMapping(prefix, uri);
    }
    
    public void endPrefixMapping(String prefix)
        throws SAXException
    {
        if (category.isDebugEnabled()) {
            category.debug("EndPrefixMapping '" + prefix + "'");
        }
        
        if (recorder != null)
            recorder.endPrefixMapping(prefix);
        
        SOAPHandler handler = getTopHandler();
        if (handler != null)
            handler.endPrefixMapping(prefix);
    }
    
    public void setDocumentLocator(Locator locator) 
    {
        if (recorder != null)
            recorder.setDocumentLocator(locator);
        this.locator = locator;
    }

    public void characters(char[] p1, int p2, int p3) throws SAXException {
        if (recorder != null)
            recorder.characters(p1, p2, p3);
        if (getTopHandler() != null)
            getTopHandler().characters(p1, p2, p3);
    }
    
    public void ignorableWhitespace(char[] p1, int p2, int p3) throws SAXException {
        if (recorder != null)
            recorder.ignorableWhitespace(p1, p2, p3);
        if (getTopHandler() != null)
            getTopHandler().ignorableWhitespace(p1, p2, p3);
    }
 
    public void processingInstruction(String p1, String p2) throws SAXException {
        // must throw an error since SOAP 1.1 doesn't allow
        // processing instructions anywhere in the message
        throw new SAXException("Processing instructions are not allowed within SOAP Messages");
    }

    public void skippedEntity(String p1) throws SAXException {
        if (recorder != null)
            recorder.skippedEntity(p1);
        getTopHandler().skippedEntity(p1);
    }

    /** This is a big workhorse.  Manage the state of the parser, check for
     * basic SOAP compliance (envelope, then optional header, then body, etc).
     * 
     * This guy also handles monitoring the recording depth if we're recording
     * (so we know when to stop), and might eventually do things to help with
     * ID/HREF management as well.
     * 
     */
    public void startElement(String namespace, String localName,
                             String qName, Attributes attributes)
        throws SAXException
    {
        SOAPHandler nextHandler = null;

        if (category.isDebugEnabled()) {
            category.debug("startElement ['" + namespace + "' " +
                           localName + "]");
        }
        
        String prefix = "";
        int idx = qName.indexOf(":");
        if (idx > 0)
            prefix = qName.substring(0, idx);

        if (!handlerStack.isEmpty()) {
            nextHandler = getTopHandler().onStartChild(namespace,
                                                       localName,
                                                       prefix,
                                                       attributes,
                                                       this);
        }
        
        if (nextHandler == null) {
            nextHandler = new SOAPHandler();
        }
        
        pushElementHandler(nextHandler);

        nextHandler.startElement(namespace, localName, qName,
                                 attributes, this);
        
        if (recorder != null) {
            recorder.startElement(namespace, localName, qName,
                                  attributes);
            if (!doneParsing)
                curElement.setContentsIndex(recorder.getLength());
        }
        
        namespaces.push();
        
        startOfMappingsPos = -1;
    }
    
    public void endElement(String namespace, String localName, String qName)
        throws SAXException
    {
        if (category.isDebugEnabled()) {
            category.debug("endElement ['" + namespace + "' " +
                           localName + "]");
        }
        
        if (recorder != null)
            recorder.endElement(namespace, localName, qName);
        
        try {
            SOAPHandler handler = popElementHandler();
            handler.endElement(namespace, localName, this);
            
            if (!handlerStack.isEmpty()) {
                getTopHandler().onEndChild(namespace, localName, this);
            } else {
                // We should be done!
                if (category.isDebugEnabled()) {
                    category.debug("Done with document!");
                }
            }
            
        } catch (SAXException e) {
            e.printStackTrace();
        } finally {
            if (curElement != null)
                curElement = curElement.getParent();
        }
    }
}

