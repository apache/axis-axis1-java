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

package javax.xml.rpc.encoding;

import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.namespace.QName;

/**
 * The javax.xml.rpc.encoding.TypeMapping is the base interface for 
 * representation of a type mapping.  A TypeMapping implementation class 
 * may support one or more encoding styles and XML schema namespaces. 
 * 
 * For its supported encoding styles and XML schema namespaces, 
 * a TypeMapping instance maintains a set of tuples of 
 * the type {Java type, SerializerFactory, DeserializerFactory, XML data type}. 
 *
 * @version 0.6
 */
public interface TypeMapping {

    /**
     * Gets the list of encoding styles supported by this TypeMapping object.
     *
     * @return  String[] of namespace URIs for the supported encoding 
     * styles and XML schema namespaces.
     */
    public String[] getSupportedEncodings();

    /**
     * Sets the list of encoding styles supported by this TypeMapping object.
     *
     * @param namespaceURIs String[] of namespace URI's                
     */
    public String[] setSupportedEncodings(String[] namespaceURIs);

    /**
     * Registers SerializerFactory and DeserializerFactory for a 
     * specific type mapping between an XML type and Java type.
     *
     * @param javaType - Class of the Java type
     * @param xmlType - Qualified name of the XML data type
     * @param sf - SerializerFactory
     * @param dsf - DeserializerFactory
     *
     * @throws JAXRPCException - If any error during the registration
     */
    public void register(
        Class javaType, QName xmlType, SerializerFactory sf,
            DeserializerFactory dsf) throws JAXRPCException;

    /**
     * Gets the SerializerFactory registered for the specified pair
     * of Java type and XML data type.
     *
     * @param javaType - Class of the Java type
     * @param xmlType - Qualified name of the XML data type
     *
     * @return Registered SerializerFactory
     *
     * @throws JAXRPCException - If there is no registered SerializerFactory 
     * for this pair of Java type and XML data type 
     * java.lang.IllegalArgumentException - If invalid or unsupported XML/Java type is specified
     */
    public SerializerFactory getSerializer(Class javaType, QName xmlType)
        throws JAXRPCException;

    /**
     * Gets the DeserializerFactory registered for the specified pair 
     * of Java type and XML data type.
     *
     * @param javaType - Class of the Java type
     * @param xmlType - Qualified name of the XML data type
     *
     * @return Registered DeserializerFactory
     *
     * @throws JAXRPCException - If there is no registered DeserializerFactory
     * for this pair of Java type and  XML data type 
     * java.lang.IllegalArgumentException - If invalid or unsupported XML/Java type is specified
     */
    public DeserializerFactory getDeserializer(Class javaType, QName xmlType)
        throws JAXRPCException;

    /**
     * Removes the SerializerFactory registered for the specified 
     * pair of Java type and XML data type.
     *
     * @param javaType - Class of the Java type
     * @param xmlType - Qualified name of the XML data type
     *
     * @throws JAXRPCException - If there is error in removing the registered SerializerFactory 
     * java.lang.IllegalArgumentException - If invalid or unsupported XML/Java type is specified
     */
    public void removeSerializer(Class javaType, QName xmlType)
        throws JAXRPCException;

    /**
     * Removes the DeserializerFactory registered for the specified 
     * pair of Java type and XML data type.
     *
     * @param javaType - Class of the Java type
     * @param xmlType - Qualified name of the XML data type
     *
     * @throws JAXRPCException - If there is error in removing the registered DeserializerFactory
     * java.lang.IllegalArgumentException - If invalid or unsupported XML/Java type is specified
     */
    public void removeDeserializer(Class javaType, QName xmlType)
        throws JAXRPCException;
}


