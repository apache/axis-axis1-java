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

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;

/**
 * The TypeMapping delegate is used to simply delegate to 
 * the indicated type mapping.  It is used by the TypeMappingRegistry
 * to assist with chaining.
 * 
 * @author Rich Scheuerle (scheu@us.ibm.com)
 */
class TypeMappingDelegate implements TypeMapping { 
    TypeMapping delegate;
    
    /**
     * Construct TypeMapping
     */
    TypeMappingDelegate(TypeMapping delegate) {
        this.delegate = delegate;
    }


    /********* JAX-RPC Compliant Method Definitions *****************/

    // Delegate or throw an exception
    
    public String[] getSupportedEncodings() {
        if (delegate != null) {
            return delegate.getSupportedEncodings();
        }
        return null;
    }

    public void setSupportedEncodings(String[] namespaceURIs) {
        if (delegate != null) {
            delegate.setSupportedEncodings(namespaceURIs);
        }
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

        throw new JAXRPCException(
                Messages.getMessage("delegatedTypeMapping"));
    }
    
    public javax.xml.rpc.encoding.SerializerFactory 
        getSerializer(Class javaType, QName xmlType)
        throws JAXRPCException
    {
        if (delegate != null) {
            return delegate.getSerializer(javaType, xmlType);
        }
        return null;
    }
    public javax.xml.rpc.encoding.SerializerFactory
        getSerializer(Class javaType) 
        throws JAXRPCException 
    {
        if (delegate != null) {
            return delegate.getSerializer(javaType);
        }
        return null;
    }

    public javax.xml.rpc.encoding.DeserializerFactory
        getDeserializer(Class javaType, QName xmlType)
        throws JAXRPCException {
        if (delegate != null) {
            return delegate.getDeserializer(javaType, xmlType);
        }
        return null;
    }
    public javax.xml.rpc.encoding.DeserializerFactory 
        getDeserializer(QName xmlType)
        throws JAXRPCException {
        if (delegate != null) {
            return delegate.getDeserializer(xmlType);
        }
        return null;
    }

    /**
     * always throws an exception
     * @param javaType
     * @param xmlType
     * @throws JAXRPCException
     */
    public void removeSerializer(Class javaType, QName xmlType)
        throws JAXRPCException {
        throw new JAXRPCException(
                Messages.getMessage("delegatedTypeMapping"));
    }

    /**
     * always throws an exception
     * @param javaType
     * @param xmlType
     * @throws JAXRPCException
     */
    public void removeDeserializer(Class javaType, QName xmlType)
        throws JAXRPCException {
        throw new JAXRPCException(
                Messages.getMessage("delegatedTypeMapping"));
    }

   public boolean isRegistered(Class javaType, QName xmlType) {
       if (delegate != null) {
           return delegate.isRegistered(javaType, xmlType);
       }
       return false;
   }

    /********* End JAX-RPC Compliant Method Definitions *****************/
     
    /**
     * Gets the QName for the type mapped to Class.
     * @param javaType class or type
     * @return xmlType qname or null
     */
    public QName getTypeQName(Class javaType) {
        if (delegate != null) {
            return delegate.getTypeQName(javaType);
        }
        return null;
    }
    
    /**
     * Gets the Class mapped to QName.
     * @param xmlType qname or null
     * @return javaType class for type or null for no mappingor delegate
     */
    public Class getClassForQName(QName xmlType) {
        if (delegate != null) {
            return delegate.getClassForQName(xmlType);
        }
        return null;
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
        if (delegate != null) {
            return delegate.getTypeQNameExact(javaType);
        }
        return null;
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

    /**
     * Returns an array of all the classes contained within this mapping
     */
    public Class[] getAllClasses() {
        if (delegate == null) return null;
        return delegate.getAllClasses();
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
        if (delegate != null) {
            return delegate.getXMLType(javaType, xmlType, encoded);
        }
        return null;
    }
}
