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

import org.apache.axis.utils.Messages;
import org.apache.axis.Constants;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;

/**
 * The TypeMapping delegate is used to simply delegate to 
 * the indicated type mapping.  It is used by the TypeMappingRegistry
 * to assist with chaining.
 * 
 * @author Rich Scheuerle (scheu@us.ibm.com)
 */
public class TypeMappingDelegate implements TypeMapping {
    static final TypeMappingImpl placeholder = new TypeMappingImpl();

    TypeMappingImpl delegate;
    TypeMappingDelegate next;

    /**
     * Construct TypeMapping
     */
    TypeMappingDelegate(TypeMappingImpl delegate) {
        if (delegate == null) {
            throw new RuntimeException(Messages.getMessage("NullDelegate"));
        }
        this.delegate = delegate;
    }


    /********* JAX-RPC Compliant Method Definitions *****************/

    // Delegate or throw an exception
    
    public String[] getSupportedEncodings() {
        return delegate.getSupportedEncodings();
    }

    public void setSupportedEncodings(String[] namespaceURIs) {
        delegate.setSupportedEncodings(namespaceURIs);
    }

    /**
     * always throws an exception
     * @param javaType
     * @param xmlType
     * @param sf
     * @param dsf
     * @throws JAXRPCException
     */
    public void register(Class javaType, QName xmlType,
                         javax.xml.rpc.encoding.SerializerFactory sf,
                         javax.xml.rpc.encoding.DeserializerFactory dsf)
        throws JAXRPCException {
        delegate.register(javaType, xmlType, sf, dsf);
    }
    
    public javax.xml.rpc.encoding.SerializerFactory 
        getSerializer(Class javaType, QName xmlType)
        throws JAXRPCException
    {
        javax.xml.rpc.encoding.SerializerFactory sf = delegate.getSerializer(javaType, xmlType);

        if (sf == null && next != null) {
            sf = next.getSerializer(javaType, xmlType);
        }

        if (sf == null) {
            sf = delegate.finalGetSerializer(javaType);
        }

        return sf;
    }
    public javax.xml.rpc.encoding.SerializerFactory
        getSerializer(Class javaType) 
        throws JAXRPCException 
    {
        return getSerializer(javaType, null);
    }

    public javax.xml.rpc.encoding.DeserializerFactory
        getDeserializer(Class javaType, QName xmlType)
        throws JAXRPCException {
        return getDeserializer(javaType, xmlType, this);
    }

    public javax.xml.rpc.encoding.DeserializerFactory
            getDeserializer(Class javaType, QName xmlType, TypeMappingDelegate start)
            throws JAXRPCException {
        javax.xml.rpc.encoding.DeserializerFactory df =
                delegate.getDeserializer(javaType, xmlType, start);
        if (df == null && next != null) {
            df = next.getDeserializer(javaType, xmlType, start);
        }
        if (df == null) {
            df = delegate.finalGetDeserializer(javaType, xmlType, start);
        }
        return df;
    }

    public javax.xml.rpc.encoding.DeserializerFactory
        getDeserializer(QName xmlType)
        throws JAXRPCException {
        return getDeserializer(null, xmlType);
    }

    public void removeSerializer(Class javaType, QName xmlType)
        throws JAXRPCException {
        delegate.removeSerializer(javaType, xmlType);
    }

    public void removeDeserializer(Class javaType, QName xmlType)
        throws JAXRPCException {
        delegate.removeDeserializer(javaType, xmlType);
    }

   public boolean isRegistered(Class javaType, QName xmlType) {
       boolean result = delegate.isRegistered(javaType, xmlType);
       if (result == false && next != null) {
           return next.isRegistered(javaType, xmlType);
       }
       return result;
   }

    /********* End JAX-RPC Compliant Method Definitions *****************/
     
    /**
     * Gets the QName for the type mapped to Class.
     * @param javaType class or type
     * @return xmlType qname or null
     */
    public QName getTypeQName(Class javaType) {
        return delegate.getTypeQName(javaType, next);
    }
    
    /**
     * Gets the Class mapped to QName.
     * @param xmlType qname or null
     * @return javaType class for type or null for no mappingor delegate
     */
    public Class getClassForQName(QName xmlType) {
        return getClassForQName(xmlType, null);
    }

    /**
     * Gets the Class mapped to QName, preferring the passed Class if possible
     * @param xmlType qname or null
     * @param javaType a Java class
     * @return javaType class for type or null for no mappingor delegate
     */
    public Class getClassForQName(QName xmlType, Class javaType) {
        return delegate.getClassForQName(xmlType, javaType, next);
    }

    /**
     * Get the QName for this Java class, but only return a specific
     * mapping if there is one.  In other words, don't do special array
     * processing, etc.
     * 
     * @param javaType
     * @return
     */
    public QName getTypeQNameExact(Class javaType) {
        QName result = delegate.getTypeQNameExact(javaType, next);

        return result;
    }

    /**
     * setDelegate sets the new Delegate TypeMapping
     */
    public void setNext(TypeMappingDelegate next) {
        if (next == this) {
            return; // Refuse to set up tight loops (throw exception?)
        }
        this.next = next;
    }

    /**
     * getDelegate gets the new Delegate TypeMapping
     */
    public TypeMappingDelegate getNext() {
        return next;
    }

    /**
     * Returns an array of all the classes contained within this mapping
     */
    public Class[] getAllClasses() {
        return delegate.getAllClasses(next);
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
    public QName getXMLType(Class javaType, QName xmlType, boolean encoded)
            throws JAXRPCException {
        QName result = delegate.getXMLType(javaType, xmlType, encoded);
        if (result == null && next != null) {
            return next.getXMLType(javaType, xmlType, encoded);
        }
        return result;
    }

    public void setDoAutoTypes(boolean doAutoTypes) {
        delegate.setDoAutoTypes(doAutoTypes);
    }
}
