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

import javax.xml.namespace.QName;

import org.apache.axis.Message;
import org.apache.axis.MessageContext;

import org.w3c.dom.Element;

import org.xml.sax.Attributes;

/**
 * This interface describes the AXIS SerializationContext.
 */
public interface SerializationContext extends javax.xml.rpc.encoding.SerializationContext {

    /**
     * Serialize the indicated value as an element with the name 
     * indicated by elemQName.
     * The attributes are additional attribute to be serialized on the element.
     * The value is the object being serialized.  (It may be serialized
     * directly or serialized as an mult-ref'd item)
     * The value is an Object, which may be a wrapped primitive, the 
     * javaType is the actual unwrapped object type.
     * The xmlType (if specified) is the QName of the type that is used to set
     * xsi:type.  If not specified, xsi:type is set by using the javaType to 
     * find an appopriate xmlType from the TypeMappingRegistry.  
     * The sendNull flag indicates whether null values should be sent over the
     * wire (default is to send such values with xsi:nil="true").
     * The sendType flag indicates whether the xsi:type flag should be sent 
     * (default is true).
     * @param elemQName is the QName of the element
     * @param attributes are additional attributes
     * @param value is the object to serialize
     */
    public void serialize(QName elemQName,
                          Attributes attributes,
                          Object value)
        throws IOException;

    /**
     * Serialize the indicated value as an element with the name
     * indicated by elemQName.
     * The attributes are additional attribute to be serialized on the element.
     * The value is the object being serialized.  (It may be serialized
     * directly or serialized as an mult-ref'd item)
     * The xmlType (if specified) is the QName of the type that is used to set
     * xsi:type.  
     * The sendNull flag indicates whether null values should be sent over the
     * wire (default is to send such values with xsi:nil="true").
     * The sendType flag indicates whether the xsi:type flag should be sent
     * (default is true).
     * @param elemQName is the QName of the element
     * @param attributes are additional attributes
     * @param value is the object to serialize
     * @param xmlType is the qname of the type or null. (default is null)
     * @param sendNull determines whether to send null values. (default is true)
     * @param sendType determines whether to set xsi:type attribute. (default is true)
     */
    public void serialize(QName elemQName,
                          Attributes attributes,
                          Object value,
                          QName xmlType,
                          boolean sendNull,
                          Boolean sendType)
        throws IOException;


    /**
     * Obtains the type attribute that should be serialized and returns the new list of Attributes
     * @param attributes of the qname
     * @param type is the qname of the type
     * @return new list of Attributes
     */
    public Attributes setTypeAttribute(Attributes attributes, QName type);

    /**
     * Convenience method to get the Serializer for a specific
     * java type
     * @param javaType is Class for a type to serialize
     * @return Serializer
     */ 
    public Serializer getSerializerForJavaType(Class javaType) ; 

    /**
     * Get whether the serialization should be pretty printed.
     * @return true/false
     */
    public boolean getPretty();

    /**
     * Indicate whether the serialization should be pretty printed.
     * @param pretty true/false
     */
    public void setPretty(boolean pretty);

    /**
     * Set whether we are doing multirefs.
     * @param shouldDo true/false
     */ 
    public void setDoMultiRefs (boolean shouldDo);

    /**
     * Set whether or not we should write XML declarations.
     * @param sendDecl true/false
     */ 
    public void setSendDecl(boolean sendDecl);

    /**
     * Get whether or not to write xsi:type attributes.
     * @return true/false
     */ 
    public boolean shouldSendXSIType();

    /**
     * Get the TypeMapping we're using.
     * @return TypeMapping or null
     */ 
    public TypeMapping getTypeMapping();

    /**
     * Get the TypeMappingRegistry we're using.
     * @return TypeMapping or null
     */ 
    public TypeMappingRegistry getTypeMappingRegistry();

    /**
     * Get a prefix for a namespace URI.  This method will ALWAYS
     * return a valid prefix - if the given URI is already mapped in this
     * serialization, we return the previous prefix.  If it is not mapped,
     * we will add a new mapping and return a generated prefix of the form
     * "ns<num>".
     * @param uri is the namespace uri
     * @return prefix
     */ 
    public String getPrefixForURI(String uri);

    /**
     * Get a prefix for a namespace URI.  This method will ALWAYS
     * return a valid prefix - if the given URI is already mapped in this
     * serialization, we return the previous prefix.  If it is not mapped,
     * we will add a new mapping and return a generated prefix of the form
     * "ns<num>".
     * @param uri is the namespace uri
     * @param defaultPrefix optional parameter which is the default prefix
     * @return prefix
     */
    public String getPrefixForURI(String uri, String defaultPrefix);

    /**
     * Register prefix for the indicated uri
     * @param prefix
     * @param uri is the namespace uri
     */
    public void registerPrefixForURI(String prefix, String uri);

    /**
     * Get the current message.
     * @return Message
     */
    public Message getCurrentMessage();

    /**
     * Get the MessageContext we're operating with
     */
    public MessageContext getMessageContext();

    /**
     * Convert QName to a string of the form <prefix>:<localpart>
     * @param qName
     * @return prefixed qname representation for serialization.
     */
    public String qName2String(QName qName);

    /**
     * Convert attribute QName to a string of the form <prefix>:<localpart>
     * There are some special rules for attributes
     * @param qName
     * @return prefixed qname representation for serialization.
     */
    public String attributeQName2String(QName qName);

    /**
     * Get the QName associated with the specified class.
     * @param cls Class of an object requiring serialization.
     * @return appropriate QName associated with the class.
     */
    public QName getQNameForClass(Class cls);

    /**
     * Indicates whether the object should be interpretted as a primitive
     * for the purposes of multi-ref processing.  A primitive value
     * is serialized directly instead of using id/href pairs.  Thus 
     * primitive serialization/deserialization is slightly faster.
     * @param value to be serialized
     * @return true/false
     */
    public boolean isPrimitive(Object value);

    /**
     * The serialize method uses hrefs to reference all non-primitive
     * values.  These values are stored and serialized by calling
     * outputMultiRefs after the serialize method completes.
     */
    public void outputMultiRefs() throws IOException;

    /**
     * Writes (using the Writer) the start tag for element QName along with the
     * indicated attributes and namespace mappings.
     * @param qName is the name of the element
     * @param attributes are the attributes to write
     */
    public void startElement(QName qName, Attributes attributes) throws IOException;

    /**
     * Writes the end element tag for the open element.
     **/
    public void endElement()  throws IOException;

    /**
     * Convenience operation to write out (to Writer) the characters
     * in p1 starting at index p2 for length p3.
     * @param p1 character array to write
     * @param p2 starting index in array
     * @param p3 length to write
     */
    public void writeChars(char [] p1, int p2, int p3) throws IOException;;

    /**
     * Convenience operation to write out (to Writer) the String
     * @param string is the String to write.
     */
    public void writeString(String string) throws IOException;

    /**
     * Convenience operation to write out (to Writer) the String
     * properly encoded with xml entities (like &amp)
     * @param string is the String to write.
     */
    public void writeSafeString(String string) throws IOException;

    /** 
     * Output a DOM representation to a SerializationContext
     * @param el is a DOM Element
     */
    public void writeDOMElement(Element el) throws IOException;

    public String getValueAsString(Object value, QName xmlType) throws IOException;

    /**
     * Get the currently prefered xmlType
     `* @return QName of xmlType or null
     */
    public QName getCurrentXMLType();

}


