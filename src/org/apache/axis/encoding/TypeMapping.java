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

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.encoding.DeserializerFactory;
import javax.xml.rpc.encoding.SerializerFactory;



/**
 * This interface describes the AXIS TypeMapping.
 */
public interface TypeMapping 
    extends javax.xml.rpc.encoding.TypeMapping {

    /**
     * setDelegate sets the new Delegate TypeMapping
     */
    public void setDelegate(TypeMapping delegate);

    /**
     * getDelegate gets the new Delegate TypeMapping
     */
    public TypeMapping getDelegate();

    /**
     * Gets the SerializerFactory registered for the specified pair
     * of Java type and XML data type.
     *
     * @param javaType - Class of the Java type
     *
     * @return Registered SerializerFactory
     *
     * @throws JAXRPCException - If there is no registered SerializerFactory 
     * for this pair of Java type and XML data type 
     * java.lang.IllegalArgumentException  
     * If invalid or unsupported XML/Java type is specified
     */
    public SerializerFactory getSerializer(Class javaType) 
        throws JAXRPCException;

    /**
     * Gets the DeserializerFactory registered for the specified XML data type.
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
    public DeserializerFactory getDeserializer(QName xmlType) 
        throws JAXRPCException;

    /**
     * Gets the QName for the type mapped to Class.
     * @param javaType class or type
     * @return xmlType qname or null
     */
    public QName getTypeQName(Class javaType);
    
    /**
     * Get the QName for this Java class, but only return a specific
     * mapping if there is one.  In other words, don't do special array
     * processing, etc.
     * 
     * @param javaType
     * @return
     */ 
    public QName getTypeQNameExact(Class javaType);

    /**
     * Gets the Class mapped to QName.
     * @param xmlType qname or null
     * @return javaType class for type or null for no mapping
     */
    public Class getClassForQName(QName xmlType);

    /**
     * Returns an array of all the classes contained within this mapping
     */
    public Class [] getAllClasses();

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
     * @throws javax.xml.rpc.JAXRPCException
     */
    QName getXMLType(Class javaType, QName xmlType, boolean encoded)
        throws JAXRPCException;
}


