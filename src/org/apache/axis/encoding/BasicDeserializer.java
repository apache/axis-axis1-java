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

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.axis.InternalException;
import org.apache.axis.message.SOAPHandler;
import org.apache.axis.utils.JavaUtils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * A deserializer for any simple type with a (String) constructor.  Note:
 * this class is designed so that subclasses need only override the makeValue 
 * method in order to construct objects of their own type.
 *
 * @author Glen Daniels (gdaniels@macromedia.com)
 * @author Sam Ruby (rubys@us.ibm.com)
 */
public class BasicDeserializer extends Deserializer {

    StringBuffer val = new StringBuffer();
    private Constructor constructor;
        
    /**
     * Disallow nested elements.  This method is defined by Deserializer.
     */
    public SOAPHandler onStartChild(String namespace,
                                    String localName,
                                    String prefix,
                                    Attributes attributes,
                                    DeserializationContext context)
        throws SAXException
    {
        throw new SAXException(
                JavaUtils.getMessage("cantHandle00", "BasicDeser"));
    }
    
    /**
     * Append any characters received to the value.  This method is defined 
     * by Deserializer.
     */
    public void characters(char [] chars, int start, int end)
        throws SAXException
    {
        val.append(chars, start, end);
    }
    
    /**
     * Append any characters to the value.  This method is defined by 
     * Deserializer.
     */
    public void onEndElement(String namespace, String localName,
                           DeserializationContext context)
        throws SAXException
    {
        try {
            value = makeValue(val.toString());
        } catch (InvocationTargetException ite) {
            Throwable realException = ite.getTargetException();
            if (realException instanceof Exception)
               throw new SAXException((Exception)realException);
            else
               throw new SAXException(ite.getMessage());
        } catch (Exception e) {
            throw new SAXException(e);
        }
    }
    
    /**
     * Setter for the private Constructor member.
     * @param constructor the desired constructor to use
     */
    public void setConstructor(Constructor constructor) {
        this.constructor = constructor;
    }

    /**
     * Convert the string that has been accumulated into an Object.  Subclasses
     * may override this.
     * @param string the serialized value to be deserialized
     * @throws Exception any exception thrown by this method will be wrapped
     */
    public Object makeValue(String source) throws Exception
    {
        return constructor.newInstance(new Object [] { source });
    }
    
    /**
     * Obtain a factory for the specified class.
     * @param cls the desired class
     */
    public static DeserializerFactory getFactory(Class cls)
        throws IntrospectionException
    {
        DeserializerFactory factory = new BasicDeserializerFactory();
        factory.setJavaClass(cls);
        return factory;
    }

    /**
     * Factory to create preinitialized BasicDeserializer instances.  This
     * factory will obtain desired constructor so that it does not need
     * to be refound on every new BeanDeserializer.
     */
    public static class BasicDeserializerFactory 
        implements DeserializerFactory 
    {
        Constructor constructor;

        /**
         * Determine the constructor for the specified class.  Ensure that the 
         * no attempt is made to reuse this factory for another class.
         * @param cls the desired class
         * @throws IntrospectionException class does not contain an appropriate
         *         constructor.
         */
        public void setJavaClass(Class cls) throws IntrospectionException {
            try {
                Constructor constructor = cls.getDeclaredConstructor( 
                    new Class [] {String.class});

                if (this.constructor!=null) 
                    if (!constructor.equals(this.constructor))
                        throw new InternalException("Attempt to reuse factory");

                this.constructor = constructor;
            } catch (NoSuchMethodException e) {
                throw new IntrospectionException(e.toString());
            }
        }

        /**
         * Obtain an initialized deserializer.
         */
        public Deserializer getDeserializer() {
            BasicDeserializer deserializer = new BasicDeserializer();
            deserializer.setConstructor(constructor);
            return deserializer;
        }
    }

}
