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

package org.apache.axis.encoding.ser;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.namespace.QName;

import org.apache.axis.InternalException;
import org.apache.axis.message.SOAPHandler;
import org.apache.axis.utils.JavaUtils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.SerializerFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerFactory;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.DeserializerImpl;

/**
 * A deserializer for any simple type with a (String) constructor.  Note:
 * this class is designed so that subclasses need only override the makeValue 
 * method in order to construct objects of their own type.
 *
 * @author Glen Daniels (gdaniels@macromedia.com)
 * @author Sam Ruby (rubys@us.ibm.com)
 * Modified for JAX-RPC @author Rich Scheuerle (scheu@us.ibm.com)
 */
public class SimpleDeserializerFactory extends BaseDeserializerFactory {

    private Constructor constructor = null;
    /**
     * Note that the factory is constructed with the QName and xmlType.  This is important
     * to allow distinction between primitive values and java.lang wrappers.
     **/
    public SimpleDeserializerFactory(Class javaType, QName xmlType) {
        super(SimpleDeserializer.class, false, xmlType, javaType); 
        try {
            if (!javaType.isPrimitive()) {
                constructor = 
                    javaType.getDeclaredConstructor(new Class [] {String.class});
            }
            else {
                Class wrapper = null;
                if (javaType == int.class)
                    wrapper = java.lang.Integer.class;
                else if (javaType == short.class)
                    wrapper = java.lang.Short.class;
                else if (javaType == boolean.class)
                    wrapper = java.lang.Boolean.class;
                else if (javaType == byte.class)
                    wrapper = java.lang.Byte.class;
                else if (javaType == long.class)
                    wrapper = java.lang.Long.class;
                else if (javaType == double.class)
                    wrapper = java.lang.Double.class;
                else if (javaType == float.class)
                    wrapper = java.lang.Float.class;
                else if (javaType == char.class)
                    wrapper = java.lang.Character.class;
                if (wrapper != null)
                    constructor = 
                        wrapper.getDeclaredConstructor(new Class [] {String.class});
            }
        } catch (Exception e) {} 
    }
    
    /**
     * Get the Deserializer and the set the Constructor so the
     * deserializer does not have to do introspection.
     */
    public javax.xml.rpc.encoding.Deserializer getDeserializerAs(String mechanismType)
        throws JAXRPCException {
        if (javaType == java.lang.Object.class) {
            return null;
        }
        SimpleDeserializer deser = (SimpleDeserializer) super.getDeserializerAs(mechanismType);
        if (deser != null)
            deser.setConstructor(constructor);
        return deser;
    }
            
}
