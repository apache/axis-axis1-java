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


package org.apache.axis.encoding;

import java.io.IOException;
import java.io.Writer;

import org.apache.axis.Message;
import org.apache.axis.MessageContext;


import org.apache.axis.message.IDResolver;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.SAX2EventRecorder;
import org.apache.axis.message.SOAPHandler;
import org.apache.axis.utils.NSStack;
import org.apache.axis.message.SOAPEnvelope;

import org.w3c.dom.Element;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.parsers.SAXParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 * This interface describes the AXIS DeserializationContext, note that
 * an AXIS compliant DeserializationContext must extend the org.xml.sax.helpers.DefaultHandler.
 */

public interface DeserializationContext extends javax.xml.rpc.encoding.DeserializationContext {

    /**
     * Create a parser and parse the inputSource
     */
    public void parse() throws SAXException;

    /**
     * Get current MessageElement
     **/
    public MessageElement getCurElement();

    /**
     * Set current MessageElement
     **/
    public void setCurElement(MessageElement el);

    /**
     * Get MessageContext         
     */
    public MessageContext getMessageContext();
    
    /**
     * Get Envelope               
     */
    public SOAPEnvelope getEnvelope();
    
    /**
     * Get Event Recorder         
     */
    public SAX2EventRecorder getRecorder();

    /**
     * Set Event Recorder         
     */
    public void setRecorder(SAX2EventRecorder recorder);

   /**
     * Get the Namespace Mappings.  Returns null if none are present.
     **/
    public ArrayList getCurrentNSMappings();
    
    /** 
     * Get the Namespace for a particular prefix
     */
    public String getNamespaceURI(String prefix);

    /**
     * Construct a QName from a string of the form <prefix>:<localName>
     * @param qNameStr is the prefixed name from the xml text
     * @return QName
     */
    public QName getQNameFromString(String qNameStr);

    /** 
     * Create a QName for the type of the element defined by localName and
     * namespace from the XSI type.
     * @param namespace of the element
     * @param localName is the local name of the element
     * @param attrs are the attributes on the element
     */
    public QName getTypeFromXSITypeAttr(String namespace, String localName,
                                        Attributes attrs);
    /** 
     * Create a QName for the type of the element defined by localName and
     * namespace with the specified attributes.
     * @param namespace of the element
     * @param localName is the local name of the element
     * @param attrs are the attributes on the element
     */
    public QName getTypeFromAttributes(String namespace, String localName,
                                       Attributes attrs);

    /**
     * Convenenience method that returns true if the value is nil 
     * (due to the xsi:nil) attribute.
     * @param attrs are the element attributes.
     * @return true if xsi:nil is true
     */
    public boolean isNil(Attributes attrs);

    /**
     * Get a Deserializer which can turn a given xml type into a given
     * Java type
     */ 
    public Deserializer getDeserializer(Class cls, QName xmlType);

    /**
     * Convenience method to get the Deserializer for a specific
     * xmlType.
     * @param xmlType is QName for a type to deserialize
     * @return Deserializer
     */
    public Deserializer getDeserializerForType(QName xmlType);

    /** 
     * Get the TypeMapping for this DeserializationContext
     */
    public TypeMapping getTypeMapping();

    /**
     * Get the TypeMappingRegistry we're using.
     * @return TypeMapping or null
     */ 
    public TypeMappingRegistry getTypeMappingRegistry();

    /**
     * Get the MessageElement for the indicated id (where id is the #value of an href)
     * If the MessageElement has not been processed, the MessageElement will 
     * be returned.  If the MessageElement has been processed, the actual object
     * value is stored with the id and this routine will return null.
     * @param id is the value of an href attribute
     * @return MessageElement or null
     */ 
    public MessageElement getElementByID(String id);

    /**
     * Gets the MessageElement or actual Object value associated with the href value.  
     * The return of a MessageElement indicates that the referenced element has 
     * not been processed.  If it is not a MessageElement, the Object is the
     * actual deserialized value.  
     * In addition, this method is invoked to get Object values via Attachments.
     * @param id is the value of an href attribute (or an Attachment id)
     * @return MessageElement other Object or null
     */ 
    public Object getObjectByRef(String href);

    /**
     * Add the object associated with this id (where id is the value of an id= attribute,
     * i.e. it does not start with #).  
     * This routine is called to associate the deserialized object
     * with the id specified on the XML element.
     * @param id (id name without the #)
     * @param obj is the deserialized object for this id.
     */
    public void addObjectById(String _id, Object obj);

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
    public void registerFixup(String href, Deserializer dser);

   
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
    public void registerElementByID(String id, MessageElement elem);

    /**
     * Each id can have its own kind of resolver.  This registers a 
     * resolver for the id.
     */
    public void registerResolverForID(String id, IDResolver resolver);

    /**
     * Get the current position in the record.
     */
    public int getCurrentRecordPos();

    /**
     * Get the start of the mapping position  
     */
    public int getStartOfMappingsPos();

    /**
     * Push the MessageElement into the recorder
     */
    public void pushNewElement(MessageElement elem);

    /**
     * Handler management methods
     */
    public void pushElementHandler(SOAPHandler handler);
    public void replaceElementHandler(SOAPHandler handler);
    public SOAPHandler popElementHandler();

    /**
     * Return if done parsing document.
     */
    public boolean isDoneParsing();

    /**
     * Indicate if we're in the midst of processing an href target, in which
     * case we shouldn't be pushing the element stack.
     * @param ref
     */
    void setProcessingRef(boolean ref);

    /**
     * Are we in the midst of processing an href target?  If so, we shouldn't
     * be pushing the element stack...
     * @return
     */
    boolean isProcessingRef();
}


