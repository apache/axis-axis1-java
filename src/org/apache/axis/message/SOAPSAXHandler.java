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
import org.apache.axis.*;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.ServiceDescription;
import org.apache.axis.utils.NSStack;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

/** The main SOAP envelope parsing class.  This whole system is based on
 * SAX event-style parsing, and this is the core "engine".  Subclasses
 * of this control the actual parsing in appropriate ways, including:
 * 
 * 1) using a SAX parser and two threads (parse and control) to gate
 *    the events (this was the first model we implemented)
 * 2) using a pull parser and simply returning control when we hit
 *    pauseParsing()
 * 
 * Look at the end of the class to see what subclasses must implement.
 * 
 * @author Glen Daniels (gdaniels@allaire.com)
 */

public abstract class SOAPSAXHandler extends DefaultHandler
{
    private static final boolean DEBUG_LOG = false;
    
    private NSStack namespaces = new NSStack();

    ///////////////
    // States
    protected static final int INITIAL_STATE  = 0;
    protected static final int IN_ENVELOPE    = 1;
    protected static final int IN_HEADER      = 2;
    protected static final int IN_BODY        = 3;
    protected static final int FINISHED       = 4;
    
    private static final int TARGET_NONE    = 0;
    private static final int TARGET_HEADER  = 1;
    private static final int TARGET_BODY    = 2;
    private static final int TARGET_ID      = 3;
    
    protected int state = INITIAL_STATE;
    private boolean parsedEnvelope = false;
    private boolean parsedHeaders = false;
    private boolean parsedBody    = false;
    
    // State for stopping the parse when we hit a particular
    // target element (header or body)
    private String targetNS = null;
    private String targetName = null;
    private String targetID = null;
    private int targetType = TARGET_NONE;
    private MessageElement targetElement = null;
    private boolean foundTarget = false;
    
    protected SOAPEnvelope envelope;
    
    protected boolean parsingToEnd = false;
    
    private ContentHandler elementHandler = null;
    private int recordingDepth = 0;

    protected DeserializationContext context;
    protected ServiceDescription serviceDesc = null;
    
    class BodyFactory implements ElementFactory {
        public MessageElement createElement(String namespace,
                                            String localName,
                                            Attributes attributes,
                                            DeserializationContext context)
        {
            if ((serviceDesc != null) && (!serviceDesc.isRPC())) {
                return new SOAPBodyElement(namespace, localName, attributes, context);
            }
            
            return RPCElement.getFactory().createElement(namespace,
                                                         localName,
                                                         attributes,
                                                         context);
        }
    }

    /** These guys know how to create the right MessageElements (and thus
     * sub-handlers) for particular XML elements.  Right now the headers
     * can be registered (see DebugHeader for an example), but the bodies
     * are fixed as RPCElements.
     */
    
    // Header factory.
    ElementRegistry headerRegistry =
                            new ElementRegistry(SOAPHeader.factory());
    
    // Body factory. Only doing rpc bodies for right now...
    ElementFactory bodyFactory = new BodyFactory();

    public SOAPSAXHandler()
    {
        envelope = new SOAPEnvelope(this);
        this.context = new DeserializationContext(this);

        // just testing...
        headerRegistry.registerFactory("urn:myNS", "Debug", DebugHeader.getFactory());
    }
    
    public void setServiceDescription(ServiceDescription serviceDesc)
    {
        this.serviceDesc = serviceDesc;
        envelope.setServiceDescription(serviceDesc);
    }
    
    public int getState()
    {
        return state;
    }
    
    public SOAPEnvelope getEnvelope()
    {
        return envelope;
    }
    
    public DeserializationContext getContext()
    {
        return context;
    }
    
    /** Grab a namespace prefix
     */
    public String getNamespaceURI(String prefix)
    {
        return namespaces.getNamespaceURI(prefix);
    }
    
    /** Parse to the end of the header element
     */
    public void parseHeaders()
    {
        if (!parsedHeaders)
            parseForHeader(null, null);
    }

    /** Parse all the way through, without stopping.
     */
    public void parseToEnd()
    {
        parsingToEnd = true;
        parse();
    }
    
    public MessageElement parseForID(String id)
    {
        if (state == FINISHED)
            return null;
        
        targetID = id;
        targetType = TARGET_ID;
        
        // Because we set the target, this will stop once we hit the
        // desired element.
        parse();
        
        targetType = TARGET_NONE;
        
        return targetElement;
    }
    
    public SOAPHeader parseForHeader(String namespace, String localPart)
    {
        // don't bother if we're done with the headers already.
        if (parsedHeaders)
            return null;
        
        this.targetNS = namespace;
        this.targetName = localPart;
        this.targetElement = null;
        this.targetType = TARGET_HEADER;

        // Because we set the target, this will stop once we hit the
        // desired header.
        parse();
        
        this.targetType = TARGET_NONE;
        
        return (SOAPHeader)this.targetElement;
    }

    public SOAPBodyElement parseForBody(String namespace, String localPart)
    {
        // don't bother if we're done with the body already.
        if (parsedBody)
            return null;

        this.targetNS = namespace;
        this.targetName = localPart;
        this.targetElement = null;
        this.targetType = TARGET_BODY;

        // Because we set the target, this will stop once we hit the
        // desired body element.
        parse();
        
        this.targetType = TARGET_NONE;
        
        return (SOAPBodyElement)this.targetElement;
    }
    
    public SOAPHeader createHeaderElementByQName(String namespace,
                                                 String localName,
                                                 Attributes attributes)
    {
        MessageElement el = headerRegistry.createElement(namespace, localName,
                                                     attributes, context);
        return (SOAPHeader)el;
    }
    
    public boolean hasParsedHeaders()
    {
        return parsedHeaders;
    }
    public boolean hasParsedBody()
    {
        return parsedBody;
    }
    public boolean hasFinished()
    {
        return (getState() == FINISHED);
    }
    
    /****************************************************************
     * Management of sub-handlers (recorders, deserializers)
     */

    /** A little utility class to keep track of our parsing state.
     */
    class HandlerContext {
        public HandlerContext(int recordingDepth, ContentHandler handler)
        {
            this.recordingDepth = recordingDepth;
            this.handler = handler;
        }
        public int recordingDepth;
        public ContentHandler handler;
    }
    
    Stack handlerContexts = new Stack();
    
    public void pushElementHandler(ContentHandler handler)
    {
        // System.out.println("Pushing handler (" + recordingDepth + ") " + handler);
        
        // If we're in the middle of another element handler, keep track
        // of where we were so we can pop contexts.
        if (elementHandler != null) {
            handlerContexts.push(new HandlerContext(recordingDepth,
                                                    elementHandler));
            recordingDepth = 0;
        } else {
            recordingDepth = 1;
        }
        
        elementHandler = handler;
    }
    
    private void popElementHandler() throws SAXException
    {
        
        if (!handlerContexts.empty()) {
            HandlerContext context = (HandlerContext)handlerContexts.pop();
            elementHandler = context.handler;
            recordingDepth = context.recordingDepth;
            //System.out.println("Popping handler (" + recordingDepth + ") " + elementHandler);
        } else {
            //System.out.println("Popping handler...(null)");
            elementHandler = null;

            if (foundTarget) {
                foundTarget = false;
                pauseParsing();
            }
        }
    }
    
    /****************************************************************
     * SAX event handlers
     */
    public void startDocument() throws SAXException {
        // Should never receive this in the midst of a parse.
    }
    
    public void endDocument() throws SAXException {
        // Everything should be closed at this point
        if (state != FINISHED)
            throw new SAXException("End of document reached prematurely!");
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
       namespaces.add(uri, prefix);
       
       // System.out.println("Mapping '" + prefix +"' to '" + uri + "'");
       
       if (elementHandler != null) elementHandler.startPrefixMapping(prefix, uri);
    }
    
    public void endPrefixMapping(String prefix)
        throws SAXException
    {
        if (state == FINISHED)
          return;
        
        if (elementHandler != null) elementHandler.endPrefixMapping(prefix);
    }
    
    public void characters(char[] p1, int p2, int p3) throws SAXException {
        if (elementHandler != null) elementHandler.characters(p1, p2, p3);
    }
    
    public void ignorableWhitespace(char[] p1, int p2, int p3) throws SAXException {
        if (elementHandler != null) elementHandler.ignorableWhitespace(p1, p2, p3);
    }
 
    public void processingInstruction(String p1, String p2) throws SAXException {
        // must throw an error since SOAP doesn't allow
        // processing instructions anywhere in the message
        throw new SAXException("Processing instructions are not allowed within SOAP Messages");
    }

    public void skippedEntity(String p1) throws SAXException {
        if (elementHandler != null) elementHandler.skippedEntity(p1);
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
        MessageElement element = null;
        
        if (DEBUG_LOG) {
            System.out.println("startElement ['" + namespace + "' " +
                           localName + "]");
            namespaces.dump();
        }
        
        if (elementHandler != null) {
            elementHandler.startElement(namespace, localName, qName, attributes);
            
            recordingDepth++;
            
            return;
        }

        try {
            switch (state) {
            case FINISHED:
                throw new SAXException("Found XML outside <envelope>!");
                
            case INITIAL_STATE:
                // Confirm envelope looks right.
                if (namespace.equals(Constants.URI_SOAP_ENV) &&
                    localName.equals(Constants.ELEM_ENVELOPE)) {
                    state = IN_ENVELOPE;
                    
                    envelope.nsDecls = (Hashtable)namespaces.peek().clone();
                    
                    if (targetType == TARGET_NONE) {
                        // Stop here to let the other thread decide what to do next.
                        // !!! Do we need to synchronize this?
                        pauseParsing();
                    }
                    return;
                }
                throw new SAXException("Wanted <SOAP-ENV:envelope> element, got <" + qName + ">");

            case IN_ENVELOPE:
                if (namespace.equals(Constants.URI_SOAP_ENV)) {
                    if (localName.equals(Constants.ELEM_HEADER)) {
                        if (parsedHeaders)
                            throw new SAXException("Found duplicate header element");
                        state = IN_HEADER;
                        return;
                    }
                    
                    if (localName.equals(Constants.ELEM_BODY)) {
                        if (parsedBody)
                            throw new SAXException("Found duplicate body element");
                        state = IN_BODY;
                        return;
                    }
                }
                // Independent elements are OK at this level
                // !!! Might want a factory-style creation for this too?
                element = new MessageElement(namespace, localName, attributes, context);
                envelope.addIndependentElement(element);
                break;
                
            case IN_HEADER:
                // Each element inside the header gets turned into a
                // SOAPHeader.
                
                // Create this header from a factory, so people can put in
                // their own implementations which don't require storing SAX
                // events, but parse them as the object gets scanned.
                element = createHeaderElementByQName(namespace, localName,
                                                     attributes);
                envelope.addHeader((SOAPHeader)element);
                
                if ((targetType == TARGET_HEADER) && (targetNS != null)) {
                    if (namespace.equals(targetNS) &&
                        localName.equals(targetName)) {
                        foundTarget = true;
                        targetElement = element;
                    }
                }
                break;
                
            case IN_BODY:
                // Each element inside the body gets turned into a
                // SOAPBodyElement
                element = bodyFactory.createElement(namespace, localName,
                                                    attributes, context);
                envelope.addBodyElement((SOAPBodyElement)element);
                
                if ((targetType == TARGET_BODY) && (targetNS != null)) {
                    if (namespace.equals(targetNS) &&
                        localName.equals(targetName)) {
                        foundTarget = true;
                        targetElement = element;
                    }
                }
                break;
                
            default:
                throw new SAXException("In unknown parsing state!");
            }
            
            // Let the event stream run until the end of this element,
            // sending the events to an appropriate handler.
            if (element != null) {
                
                if (targetType == TARGET_ID) {
                    if (targetID.equals(element.getID())) {
                        targetElement = element;
                        foundTarget = true;
                    }
                }
                
                element.setEnvelope(envelope);
                pushElementHandler(element.getContentHandler());
                element.setPrefix(namespaces.getPrefix(namespace));
                elementHandler.startElement(namespace, localName, qName, attributes);
            }
        } catch (SAXException saxEx) {
            throw saxEx;
        } finally {        
            namespaces.push();
        }
    }
    
    public void endElement(String namespace, String localName, String qName)
        throws SAXException
    {
        if (DEBUG_LOG) {
            System.out.println("endElement ['" + namespace + "' " +
                           localName + "]");
        }
        
        recordingDepth--;

        if (elementHandler != null) {
            //System.out.println("  depth is " + recordingDepth);
            
            elementHandler.endElement(namespace, localName, qName);

            if (recordingDepth == 0)
                popElementHandler();

            return;
        }
        
        if (state == IN_HEADER) {
            state = IN_ENVELOPE;
            parsedHeaders = true;

            if (targetType == TARGET_HEADER) {
                pauseParsing();
            }
            return;
        }
        
        if (state == IN_BODY) {
            parsedBody = true;
            state = IN_ENVELOPE;
            
            /** If we were looking for a body element and got here,
             * we ain't gonna find it.  !!! How do we indicate the
             * error back to the other thread?  Right now we'll just
             * return a null... good enough?
             */
            if (targetType == TARGET_BODY) {
                pauseParsing();
            }
            return;
        }
        
        if (state == IN_ENVELOPE) {
            if (DEBUG_LOG)
                System.out.println("Done parsing envelope!");
            state = FINISHED;
            
            return;
        }
        
        throw new SAXException("Invalid state in endElement!");
    }
    
    public abstract void parse();
    protected abstract void continueParsing();
    protected abstract void pauseParsing() throws SAXException;
}

