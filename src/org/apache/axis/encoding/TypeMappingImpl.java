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

import org.apache.axis.Constants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Rich Scheuerle (scheu@us.ibm.com)
 * 
 * This is the implementation of the axis TypeMapping interface (which extends
 * the JAX-RPC TypeMapping interface).
 * 
 * A TypeMapping is obtained from the singleton TypeMappingRegistry using
 * the namespace of the webservice.  The TypeMapping contains the tuples
 * {Java type, SerializerFactory, DeserializerFactory, Type QName)
 *
 * So if you have a Web Service with the namespace "XYZ", you call 
 * the TypeMappingRegistry.getTypeMapping("XYZ").
 *
 * The wsdl in your web service will use a number of types.  The tuple
 * information for each of these will be accessed via the TypeMapping.
 *
 * Because every web service uses the soap, schema, wsdl primitives, we could 
 * pre-populate the TypeMapping with these standard tuples.  Instead, 
 * if the namespace/class matches is not found in the TypeMapping 
 * the request is delegated to the 
 * Default TypeMapping or another TypeMapping
 * 
 */
public class TypeMappingImpl implements TypeMapping
{
    protected static Log log =
        LogFactory.getLog(TypeMappingImpl.class.getName());

    public class Pair {
        public Class javaType;
        public QName xmlType;
        public Pair(Class javaType, QName xmlType) {
            this.javaType = javaType;
            this.xmlType = xmlType;
        }
        public boolean equals(Object o) {
            if (o == null) return false;
            Pair p = (Pair) o;
            if (p.xmlType == null) {
                if (this.xmlType != null)
                    return false;
            } else {
                if (!p.xmlType.equals(this.xmlType))
                    return false;
            }
            if (p.javaType == null) {
                return (this.javaType == null);
            } else {
                return (p.javaType.equals(this.javaType));
            }
        }
        public int hashCode() {
            return javaType.hashCode();
        }
    }

    private HashMap qName2Pair;     // QName to Pair Mapping                              
    private HashMap class2Pair;     // Class Name to Pair Mapping                           
    private HashMap pair2SF;        // Pair to Serialization Factory
    private HashMap pair2DF;        // Pair to Deserialization Factory
    protected TypeMapping delegate;   // Pointer to delegate or null
    private ArrayList namespaces;   // Supported namespaces

    /**
     * Construct TypeMapping
     */
    public TypeMappingImpl(TypeMapping delegate) {
        qName2Pair  = new HashMap();
        class2Pair  = new HashMap();
        pair2SF     = new HashMap();
        pair2DF     = new HashMap();
        this.delegate = delegate;
        namespaces  = new ArrayList();
    }

    /**
     * setDelegate sets the new Delegate TypeMapping
     */
    public void setDelegate(TypeMapping delegate) {
        this.delegate = delegate;
    }

    /**
     * getDelegate gets the new Delegate TypeMapping
     */
    public TypeMapping getDelegate() {
        return delegate;
    }

    /********* JAX-RPC Compliant Method Definitions *****************/
    
    /**
     * Gets the list of encoding styles supported by this TypeMapping object.
     *
     * @return  String[] of namespace URIs for the supported encoding 
     * styles and XML schema namespaces.
     */
    public String[] getSupportedEncodings() {
        String[] stringArray = new String[namespaces.size()];
        return (String[]) namespaces.toArray(stringArray);
    }

    /**
     * Sets the list of encoding styles supported by this TypeMapping object.
     * (Not sure why this is useful...this information is automatically updated
     * during registration.
     *
     * @param namespaceURIs String[] of namespace URI's                
     */
    public void setSupportedEncodings(String[] namespaceURIs) {
        namespaces.clear();
        for (int i =0; i< namespaceURIs.length; i++) {
            if (!namespaces.contains(namespaceURIs[i])) {
                namespaces.add(namespaceURIs[i]);
            }
        }
    }

    /**
     * isRegistered returns true if the [javaType, xmlType]
     * pair is registered.
     * @param javaType - Class of the Java type
     * @param xmlType - Qualified name of the XML data type
     * @return: false if either javaType or xmlType is null
     * or if the pair is not specifically registered.
     * For example if called with (java.lang.String[], soapenc:Array)
     * this routine will return false because this pair is
     * probably not specifically registered.
     * However if getSerializer is called with the same pair,
     * the default TypeMapping will use extra logic to find
     * a serializer (i.e. array serializer)
     */
    public boolean isRegistered(Class javaType, QName xmlType) {
        if (javaType == null || xmlType == null) {
            return false;
        }
        if (pair2SF.keySet().contains(new Pair(javaType, xmlType))) {
            return true;
        }
        if (delegate != null) {
            return delegate.isRegistered(javaType, xmlType);
        }
        return false;
    }
    
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
    public void register(Class javaType, QName xmlType, 
                         javax.xml.rpc.encoding.SerializerFactory sf,
                         javax.xml.rpc.encoding.DeserializerFactory dsf) 
        throws JAXRPCException {        
        Pair pair = new Pair(javaType, xmlType);

        // Only register the appropriate mappings.
        if ((dsf != null) || (qName2Pair.get(xmlType) == null))
            qName2Pair.put(xmlType, pair);
        if ((sf != null) || (class2Pair.get(javaType) == null))
            class2Pair.put(javaType, pair);   
        
        pair2SF.put(pair, sf);
        pair2DF.put(pair, dsf);
    }
    
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
     * java.lang.IllegalArgumentException -
     * If invalid or unsupported XML/Java type is specified
     */
    public javax.xml.rpc.encoding.SerializerFactory 
        getSerializer(Class javaType, QName xmlType)
        throws JAXRPCException
    {
        javax.xml.rpc.encoding.SerializerFactory sf = null;
        // Try to get the serializer associated with this pair
        Pair pair = new Pair(javaType, xmlType);

        // If the xmlType was not provided, get one
        if (xmlType == null) {
            pair.xmlType = getTypeQName(javaType);
        }
        // Now get the serializer with the pair
        if (pair.xmlType != null) {
            sf = (javax.xml.rpc.encoding.SerializerFactory) pair2SF.get(pair);
            // If not successful, use the xmlType to get
            // another pair.  For some xmlTypes (like SOAP_ARRAY)
            // all of the possible javaTypes are not registered.
            if (sf == null) {
                pair = (Pair) qName2Pair.get(pair.xmlType);
                if (pair != null) {
                    sf = (javax.xml.rpc.encoding.SerializerFactory) pair2SF.get(pair);
                }
            }
        }
        if (sf == null && delegate != null) {
            sf = (SerializerFactory) 
                delegate.getSerializer(javaType, xmlType);
        }
        return sf;
    }
    public javax.xml.rpc.encoding.SerializerFactory 
        getSerializer(Class javaType) 
        throws JAXRPCException 
    {
        return getSerializer(javaType, null);
    }

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
     * java.lang.IllegalArgumentException - 
     * If invalid or unsupported XML/Java type is specified
     */
    public javax.xml.rpc.encoding.DeserializerFactory
        getDeserializer(Class javaType, QName xmlType)
        throws JAXRPCException {
        javax.xml.rpc.encoding.DeserializerFactory df = null;
        Pair pair = new Pair(javaType, xmlType);
        if (javaType == null) {
            pair.javaType = getClassForQName(xmlType);
        }
        if (pair.javaType != null) {
            df = (javax.xml.rpc.encoding.DeserializerFactory) pair2DF.get(pair);
        } 
        if (df == null && delegate != null) {
            df = (javax.xml.rpc.encoding.DeserializerFactory)
                delegate.getDeserializer(javaType, xmlType);
        }
        return df;
    }
    public javax.xml.rpc.encoding.DeserializerFactory 
        getDeserializer(QName xmlType)
        throws JAXRPCException {
        return getDeserializer(null, xmlType);
    }

    /**
     * Removes the SerializerFactory registered for the specified 
     * pair of Java type and XML data type.
     *
     * @param javaType - Class of the Java type
     * @param xmlType - Qualified name of the XML data type
     *
     * @throws JAXRPCException - If there is error in 
     * removing the registered SerializerFactory 
     * java.lang.IllegalArgumentException - 
     * If invalid or unsupported XML/Java type is specified
     */
    public void removeSerializer(Class javaType, QName xmlType)
        throws JAXRPCException {
        Pair pair = new Pair(javaType, xmlType);
        pair2SF.remove(pair);
    }

    /**
     * Removes the DeserializerFactory registered for the specified 
     * pair of Java type and XML data type.
     *
     * @param javaType - Class of the Java type
     * @param xmlType - Qualified name of the XML data type
     *
     * @throws JAXRPCException - If there is error in 
     * removing the registered DeserializerFactory
     * java.lang.IllegalArgumentException - 
     * If invalid or unsupported XML/Java type is specified
     */
    public void removeDeserializer(Class javaType, QName xmlType)
        throws JAXRPCException {
        Pair pair = new Pair(javaType, xmlType);
        pair2DF.remove(pair);
    }


     /********* End JAX-RPC Compliant Method Definitions *****************/
     
    /**
     * Gets the QName for the type mapped to Class.
     * @param javaType class or type
     * @return xmlType qname or null
     */
    public QName getTypeQName(Class javaType) {
        //log.debug("getTypeQName javaType =" + javaType);
        QName xmlType = null;
        Pair pair = (Pair) class2Pair.get(javaType);
        if (pair == null && delegate != null) {
            xmlType = delegate.getTypeQName(javaType);
        } else if (pair != null) {
            xmlType = pair.xmlType;
        }
        
        //log.debug("getTypeQName xmlType =" + xmlType);
        return xmlType;
    }
    
    /**
     * Gets the Class mapped to QName.
     * @param xmlType qname or null
     * @return javaType class or type
     */
    public Class getClassForQName(QName xmlType) {
        //log.debug("getClassForQName xmlType =" + xmlType);
        Class javaType = null;
        Pair pair = (Pair) qName2Pair.get(xmlType);
        if (pair == null && delegate != null) {
            javaType = delegate.getClassForQName(xmlType);
        } else if (pair != null) {
            javaType = pair.javaType;
        }

        //log.debug("getClassForQName javaType =" + javaType);
        return javaType;
    }
}
