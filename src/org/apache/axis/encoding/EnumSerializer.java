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

import org.apache.axis.InternalException;

import org.xml.sax.Attributes;

import javax.xml.rpc.namespace.QName;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.beans.IntrospectionException;

/**
 * Serializer for a JAX-RPC enum.
 *
 * @author Rich Scheuerle <scheu@us.ibm.com>
 * @author Sam Ruby <rubys@us.ibm.com>
 */
public class EnumSerializer implements Serializer, Serializable {

    private Class cls;
    private java.lang.reflect.Method toStringMethod = null;
    public EnumSerializer(Class cls) {
        super();
        this.cls = cls;
    }

   /** 
     * Serialize an enumeration
     */
    public void serialize(QName name, Attributes attributes,
                          Object value, SerializationContext context)
        throws IOException
    {
        context.startElement(name, attributes);

        // Invoke the toString method on the enumeration class and
        // write out the result as a string.
        try {
            if (toStringMethod == null) {
                toStringMethod = cls.getMethod("toString", null);
            }
            String propValue = (String) toStringMethod.invoke(value, null);
            context.writeString(propValue);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException(e.toString());
        }

        context.endElement();
    }

    public static DeserializerFactory getFactory(Class cls) 
        throws IntrospectionException 
    {
        DeserializerFactory factory = new EnumDeserializerFactory();
        factory.setJavaClass(cls);
        return factory;
    }

    /**
     * Deserialize an Enumeration data type.  Prior to calling makeValue,
     * the setFromStringMethod method must be called with an enumeration
     * data type.  
     */
    public static class EnumDeserializer extends BasicDeserializer {
        private Method fromStringMethod;

        protected void setFromStringMethod(Method fsm) {
            this.fromStringMethod = fsm;
        }
        
        public Object makeValue(String source) throws Exception
        {
            // Invoke the fromString static method to get the Enumeration value
            if (isNil)
                return null;
            return fromStringMethod.invoke(null,new Object [] { source });
        }
    }

    /**
     * Create a deserializer for a enumeration data type.
     */
    static public class EnumDeserializerFactory implements DeserializerFactory {
        private Method fromStringMethod;

        public void setJavaClass(Class cls) throws IntrospectionException {
            if ( (this.fromStringMethod != null) ) {
               throw new InternalException("Attempt to reuse factory");
            }

            try {
                fromStringMethod = cls.getMethod("fromString", 
                   new Class[] {java.lang.String.class});
            } catch (Exception e) {
                throw new IntrospectionException(e.toString());
            }
        }

        public Deserializer getDeserializer() {
            EnumDeserializer deserializer = new EnumDeserializer();
            deserializer.setFromStringMethod(fromStringMethod);
            return deserializer;
        }
    }
}

