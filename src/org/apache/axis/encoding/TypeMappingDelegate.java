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

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Rich Scheuerle (scheu@us.ibm.com)
 * 
 * The TypeMapping delegate is used to simply delegate to 
 * the indicated type mapping.  It is used by the TypeMappingRegistry
 * to assist with chaining.
 * 
 */
class TypeMappingDelegate extends TypeMappingImpl { 

    /**
     * Construct TypeMapping
     */
    TypeMappingDelegate(TypeMapping delegate) {
        super(delegate);
    }


    /********* JAX-RPC Compliant Method Definitions *****************/

    // Delegate or throw an exception
    
    public String[] getSupportedEncodings() {
        if (delegate != null)
            return delegate.getSupportedEncodings();
        return null;
    }

    public void setSupportedEncodings(String[] namespaceURIs) {
        if (delegate != null)
            delegate.setSupportedEncodings(namespaceURIs);
    }
    
    public void register(Class javaType, QName xmlType, 
                         javax.xml.rpc.encoding.SerializerFactory sf,
                         javax.xml.rpc.encoding.DeserializerFactory dsf)
        throws JAXRPCException {        

        throw new JAXRPCException();
    }
    
    public javax.xml.rpc.encoding.SerializerFactory 
        getSerializer(Class javaType, QName xmlType)
        throws JAXRPCException
    {
        if (delegate != null)
            return delegate.getSerializer(javaType, xmlType);
        return null;
    }
    public javax.xml.rpc.encoding.SerializerFactory
        getSerializer(Class javaType) 
        throws JAXRPCException 
    {
        if (delegate != null)
            return delegate.getSerializer(javaType);
        return null;
    }

    public javax.xml.rpc.encoding.DeserializerFactory
        getDeserializer(Class javaType, QName xmlType)
        throws JAXRPCException {
        if (delegate != null)
            return delegate.getDeserializer(javaType, xmlType);
        return null;
    }
    public javax.xml.rpc.encoding.DeserializerFactory 
        getDeserializer(QName xmlType)
        throws JAXRPCException {
        if (delegate != null)
            return delegate.getDeserializer(xmlType);
        return null;
    }

    public void removeSerializer(Class javaType, QName xmlType)
        throws JAXRPCException {
        throw new JAXRPCException();
    }

    public void removeDeserializer(Class javaType, QName xmlType)
        throws JAXRPCException {
        throw new JAXRPCException();
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
        if (delegate != null)
            return delegate.getTypeQName(javaType);
        return null;
    }
    
    /**
     * Gets the Class mapped to QName.
     * @param xmlType qname or null
     * @return javaType class or type
     */
    public Class getClassForQName(QName xmlType) {
        if (delegate != null)
            return delegate.getClassForQName(xmlType);
        return null;
    }
}
