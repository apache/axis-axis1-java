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


package org.apache.axis.encoding;

import org.apache.axis.MessageContext;
import org.apache.axis.message.IDResolver;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.SAX2EventRecorder;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import java.util.ArrayList;

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
     * Convenience method to get the Deserializer for a specific
     * java class from its meta data.
     * @param cls is the Class used to find the deserializer 
     * @return Deserializer
     */
    public Deserializer getDeserializerForClass(Class cls);

    /**
     * Allows the destination class to be set so that downstream
     * deserializers like ArrayDeserializer can pick it up when
     * deserializing its components using getDeserializerForClass
     * @param destClass is the Class of the component to be deserialized 
     */
    public void setDestinationClass(Class destClass);

    /**
     * Allows the destination class to be retrieved so that downstream
     * deserializers like ArrayDeserializer can pick it up when
     * deserializing its components using getDeserializerForClass
     * @return the Class of the component to be deserialized 
     */
    public Class getDestinationClass();

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
     * @param href is the value of an href attribute (or an Attachment id)
     * @return MessageElement other Object or null
     */ 
    public Object getObjectByRef(String href);

    /**
     * Add the object associated with this id (where id is the value of an id= attribute,
     * i.e. it does not start with #).  
     * This routine is called to associate the deserialized object
     * with the id specified on the XML element.
     * @param _id (id name without the #)
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
     * @return true if we are
     */
    boolean isProcessingRef();
}


