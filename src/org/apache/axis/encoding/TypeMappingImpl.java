/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * This is the implementation of the axis TypeMapping interface (which extends
 * the JAX-RPC TypeMapping interface).
 * </p>
 * <p>
 * A TypeMapping is obtained from the singleton TypeMappingRegistry using
 * the namespace of the webservice.  The TypeMapping contains the tuples
 * {Java type, SerializerFactory, DeserializerFactory, Type QName)
 * </p>
 * <p>
 * So if you have a Web Service with the namespace "XYZ", you call
 * the TypeMappingRegistry.getTypeMapping("XYZ").
 * </p>
 * <p>
 * The wsdl in your web service will use a number of types.  The tuple
 * information for each of these will be accessed via the TypeMapping.
 * </p>
 * <p>
 * Because every web service uses the soap, schema, wsdl primitives, we could
 * pre-populate the TypeMapping with these standard tuples.  Instead,
 * if the namespace/class matches is not found in the TypeMapping
 * the request is delegated to the
 * Default TypeMapping or another TypeMapping
 * </p>
 *
 * @author Rich Scheuerle (scheu@us.ibm.com)
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
            // Test straight equality
            if (p.xmlType == this.xmlType &&
                p.javaType == this.javaType) {
                return true;
            }
            return (p.xmlType.equals(this.xmlType) &&
                    p.javaType.equals(this.javaType));
        }
        public int hashCode() {
            int hashcode = 0;
            if (javaType != null) {
                hashcode ^= javaType.hashCode();
            }
            if (xmlType != null) {
                hashcode ^= xmlType.hashCode();
            }
            return hashcode;
        }
    }

    private HashMap qName2Pair;     // QName to Pair Mapping
    private HashMap class2Pair;     // Class Name to Pair Mapping
    private HashMap pair2SF;        // Pair to Serialization Factory
    private HashMap pair2DF;        // Pair to Deserialization Factory
    protected TypeMapping delegate;   // Pointer to delegate or null
    private ArrayList namespaces;   // Supported namespaces

    /**
     * Should we "auto-type" classes we don't recognize into the "java:"
     * namespace?
     */
    private boolean doAutoTypes = false;

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
     * @return true if there is a mapping for the given pair, or
     * false if the pair is not specifically registered.
     *
     * For example if called with (java.lang.String[], soapenc:Array)
     * this routine will return false because this pair is
     * probably not specifically registered.
     * However if getSerializer is called with the same pair,
     * the default TypeMapping will use extra logic to find
     * a serializer (i.e. array serializer)
     */
    public boolean isRegistered(Class javaType, QName xmlType) {
        if (javaType == null || xmlType == null) {
            // REMOVED_FOR_TCK
            // return false;
            throw new JAXRPCException(
                    Messages.getMessage(javaType == null ?
                                         "badJavaType" : "badXmlType"));
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
        // At least a serializer or deserializer factory must be specified.
        if (sf == null && dsf == null) {
            throw new JAXRPCException(Messages.getMessage("badSerFac"));
        }

        internalRegister(javaType, xmlType, sf, dsf);
    }

    /**
     * Internal version of register(), which allows null factories.
     *
     * @param javaType
     * @param xmlType
     * @param sf
     * @param dsf
     * @throws JAXRPCException
     */
    protected void internalRegister(Class javaType, QName xmlType,
                         javax.xml.rpc.encoding.SerializerFactory sf,
                         javax.xml.rpc.encoding.DeserializerFactory dsf)
            throws JAXRPCException {
        // Both javaType and xmlType must be specified.
        if (javaType == null || xmlType == null) {
            throw new JAXRPCException(
                    Messages.getMessage(javaType == null ?
                                         "badJavaType" : "badXmlType"));
        }

        //REMOVED_FOR_TCK
        //if (sf != null &&
        //    !(sf instanceof javax.xml.rpc.encoding.SerializerFactory)) {
        //    throw new JAXRPCException(message text);
        //}
        //if (dsf != null &&
        //    !(dsf instanceof javax.xml.rpc.encoding.DeserializerFactory)) {
        //    throw new JAXRPCException(message text);
        //}

        Pair pair = new Pair(javaType, xmlType);

        // Only register the appropriate mappings.
        if ((dsf != null) || (qName2Pair.get(xmlType) == null))
            qName2Pair.put(xmlType, pair);
        if ((sf != null) || (class2Pair.get(javaType) == null))
            class2Pair.put(javaType, pair);

        if (sf != null)
            pair2SF.put(pair, sf);
        if (dsf != null)
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
        throws JAXRPCException {

        javax.xml.rpc.encoding.SerializerFactory sf = null;

        // If the xmlType was not provided, get one
        if (xmlType == null) {
            xmlType = getTypeQName(javaType);
            // If we couldn't find one, we're hosed, since getTypeQName()
            // already asked all of our delegates.
            if (xmlType == null) {
                return null;
            }

            // If we're doing autoTyping, and we got a type in the right
            // namespace, we can use the default serializer.
            if (doAutoTypes &&
                    xmlType.getNamespaceURI().equals(Constants.NS_URI_JAVA)) {
                return new BeanSerializerFactory(javaType, xmlType);
            }
        }

        // Try to get the serializer associated with this pair
        Pair pair = new Pair(javaType, xmlType);

        // Now get the serializer with the pair
        sf = (javax.xml.rpc.encoding.SerializerFactory) pair2SF.get(pair);

        // If not successful, use the javaType to get another Pair unless
        // we've got an array, in which case make sure we get the
        // ArraySerializer.
        if (sf == null) {
            if (javaType.isArray()) {
                pair = (Pair) qName2Pair.get(Constants.SOAP_ARRAY);
            } else {
                pair = (Pair) class2Pair.get(pair.javaType);
            }
            if (pair != null) {
                sf = (javax.xml.rpc.encoding.SerializerFactory) pair2SF.get(pair);
            }
        }

        if (sf == null && delegate != null) {
            sf = delegate.getSerializer(javaType, xmlType);
        }
        return sf;
    }

    /**
     * Get the exact XML type QName which will be used when serializing a
     * given Class to a given type QName.  In other words, if we have:
     *
     * Class        TypeQName
     * ----------------------
     * Base         myNS:Base
     * Child        myNS:Child
     *
     * and call getXMLType(Child.class, BASE_QNAME), we should get
     * CHILD_QNAME.
     *
     * @param javaType
     * @param xmlType
     * @return the type's QName
     * @throws JAXRPCException
     */
    public QName getXMLType(Class javaType, QName xmlType)
        throws JAXRPCException
    {
        javax.xml.rpc.encoding.SerializerFactory sf = null;

        // If the xmlType was not provided, get one
        if (xmlType == null) {
            xmlType = getTypeQNameRecursive(javaType);

            // If we couldn't find one, we're hosed, since getTypeQName()
            // already asked all of our delegates.
            if (xmlType == null) {
                return null;
            }

            // If we're doing autoTyping, we can use the default.
            if (doAutoTypes &&
                    xmlType.getNamespaceURI().equals(Constants.NS_URI_JAVA)) {
                return xmlType;
            }
        }

        // Try to get the serializer associated with this pair
        Pair pair = new Pair(javaType, xmlType);

        // Now get the serializer with the pair
        sf = (javax.xml.rpc.encoding.SerializerFactory) pair2SF.get(pair);

        // If not successful, use the xmlType to get
        // another pair.  For some xmlTypes (like SOAP_ARRAY)
        // all of the possible javaTypes are not registered.
        if (sf == null) {
            if (javaType.isArray()) {
                pair = (Pair) qName2Pair.get(pair.xmlType);
            } else {
                pair = (Pair) class2Pair.get(pair.javaType);
            }
            if (pair != null) {
                sf = (javax.xml.rpc.encoding.SerializerFactory) pair2SF.get(pair);
            }
        }

        if (sf == null && delegate != null) {
            return ((TypeMappingImpl)delegate).getXMLType(javaType, xmlType);
        }

        if (pair != null) {
            xmlType = pair.xmlType;
        }

        return xmlType;
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

        if (javaType == null) {
            javaType = getClassForQName(xmlType);
            // If we don't have a mapping, we're hosed since getClassForQName()
            // has already asked all our delegates.
            if (javaType == null) {
                return null;
            }

            if (doAutoTypes &&
                Constants.NS_URI_JAVA.equals(xmlType.getNamespaceURI())) {
                try {
                    javaType = ClassUtils.forName(xmlType.getLocalPart());
                } catch (ClassNotFoundException e) {
                    return null;
                }
                return new BeanDeserializerFactory(javaType, xmlType);
            }
        }

        Pair pair = new Pair(javaType, xmlType);

        df = (javax.xml.rpc.encoding.DeserializerFactory) pair2DF.get(pair);

        if (df == null && delegate != null) {
            df = delegate.getDeserializer(javaType, xmlType);
        }
        return df;
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
     */
    public void removeSerializer(Class javaType, QName xmlType)
        throws JAXRPCException {
        if (javaType == null || xmlType == null) {
            throw new JAXRPCException(
                    Messages.getMessage(javaType == null ?
                                         "badJavaType" : "badXmlType"));
        }

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
     */
    public void removeDeserializer(Class javaType, QName xmlType)
        throws JAXRPCException {
        if (javaType == null || xmlType == null) {
            throw new JAXRPCException(
                    Messages.getMessage(javaType == null ?
                                         "badJavaType" : "badXmlType"));
        }
        Pair pair = new Pair(javaType, xmlType);
        pair2DF.remove(pair);
    }


     /********* End JAX-RPC Compliant Method Definitions *****************/

    /**
     * Gets the QName for the type mapped to Class.
     * @param javaType class or type
     * @return xmlType qname or null
     */
    public QName getTypeQNameRecursive(Class javaType) {
        QName ret = null;
        while (javaType != null) {
            ret = getTypeQName(javaType);
            if (ret != null)
                return ret;

            // Walk my interfaces...
            Class [] interfaces = javaType.getInterfaces();
            if (interfaces != null) {
                for (int i = 0; i < interfaces.length; i++) {
                    Class iface = interfaces[i];
                    ret = getTypeQName(iface);
                    if (ret != null)
                        return ret;
                }
            }

            javaType = javaType.getSuperclass();
        }
        return null;
    }

    public QName getTypeQName(Class javaType) {
        //log.debug("getTypeQName javaType =" + javaType);
        if (javaType == null)
            return null;
        
        QName xmlType = null;
        Pair pair = (Pair) class2Pair.get(javaType);
        if (pair == null && delegate != null) {
            xmlType = delegate.getTypeQName(javaType);
        } else if (pair != null) {
            xmlType = pair.xmlType;
        }

        if (xmlType == null && doAutoTypes) {
            xmlType = new QName(Constants.NS_URI_JAVA,
                                javaType.getName());
        }

        // Can only detect arrays via code
        if (xmlType == null && (javaType.isArray() ||
             javaType == List.class ||
             List.class.isAssignableFrom(javaType))) {

            // get the registered array if any
            pair = (Pair) class2Pair.get(Object[].class);
            // TODO: it always returns the last registered one,
            //  so that's why the soap 1.2 typemappings have to 
            //  move to an other registry to differentiate them
            if (pair != null) {
                xmlType = pair.xmlType;
            } else {
                xmlType = Constants.SOAP_ARRAY;
            }
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
        if (xmlType == null)
            return null;
        
        //log.debug("getClassForQName xmlType =" + xmlType);
        Class javaType = null;
        Pair pair = (Pair) qName2Pair.get(xmlType);
        if (pair == null && delegate != null) {
            javaType = delegate.getClassForQName(xmlType);
        } else if (pair != null) {
            javaType = pair.javaType;
        }

        if (javaType == null && doAutoTypes &&
                Constants.NS_URI_JAVA.equals(xmlType.getNamespaceURI())) {
            // Classloader?
            try {
                javaType = ClassUtils.forName(xmlType.getLocalPart());
            } catch (ClassNotFoundException e) {
            }
        }

        //log.debug("getClassForQName javaType =" + javaType);
        return javaType;
    }

    /**
     * Gets the SerializerFactory registered for the Java type.
     *
     * @param javaType - Class of the Java type
     *
     * @return Registered SerializerFactory
     *
     * @throws JAXRPCException - If there is no registered SerializerFactory
     * for this pair of Java type and XML data type
     * java.lang.IllegalArgumentException -
     * If invalid or unsupported XML/Java type is specified
     */
    public javax.xml.rpc.encoding.SerializerFactory
        getSerializer(Class javaType)
        throws JAXRPCException
    {
        return getSerializer(javaType, null);
    }

    /**
     * Gets the DeserializerFactory registered for the xmlType.
     *
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
        getDeserializer(QName xmlType)
        throws JAXRPCException {
        return getDeserializer(null, xmlType);
    }

    public void setDoAutoTypes(boolean doAutoTypes) {
        this.doAutoTypes = doAutoTypes;
    }

    /**
     * Returns an array of all the classes contained within this mapping
     */
    public Class [] getAllClasses()
    {
        java.util.HashSet temp = new java.util.HashSet();
        if (delegate != null)
        {
            temp.addAll(java.util.Arrays.asList(delegate.getAllClasses()));
        }
        temp.addAll(class2Pair.keySet());
        return (Class[])temp.toArray(new Class[temp.size()]);
    }
}
