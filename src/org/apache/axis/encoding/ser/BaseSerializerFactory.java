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

import org.apache.axis.Constants;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.SerializerFactory;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.Messages;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Vector;

/**
 * Base class for Axis Serialization Factory classes for code reuse
 *
 * @author Rich Scheuerle <scheu@us.ibm.com>
 */
public abstract class BaseSerializerFactory 
    implements SerializerFactory {

    static Vector mechanisms = null;
    
    protected Class serClass = null;
    protected Serializer ser = null;
    protected QName xmlType = null;
    protected Class javaType = null;
    protected Constructor serClassConstructor = null;
    protected Method getSerializer = null;

    /**
     * Constructor
     * @param serClass is the class of the Serializer
     * Sharing is only valid for xml primitives.
     */
    public BaseSerializerFactory(Class serClass) {
        this.serClass = serClass;
    }
    public BaseSerializerFactory(Class serClass,
                                 QName xmlType, Class javaType) {
        this(serClass);
        this.xmlType = xmlType;
        this.javaType = javaType;
        this.serClassConstructor = getConstructor(serClass);
        this.getSerializer = getSerializerMethod(javaType);
    }

    public javax.xml.rpc.encoding.Serializer 
        getSerializerAs(String mechanismType)
        throws JAXRPCException {
        synchronized (this) {
            if (ser==null) {
                ser = getSerializerAsInternal(mechanismType);
            }
            return ser;
        }
    }
    
    protected Serializer getSerializerAsInternal(String mechanismType)
        throws JAXRPCException {
        // Try getting a specialized Serializer
        Serializer serializer = getSpecialized(mechanismType);
        
        // Try getting a general purpose Serializer via constructor
        // invocation
        if (serializer == null) {
            serializer = getGeneralPurpose(mechanismType);
        }

        try {            
            // If not successfull, try newInstance
            if (serializer == null) {
                serializer = (Serializer) serClass.newInstance();
            }
        } catch (Exception e) {
            throw new JAXRPCException(
                Messages.getMessage("CantGetSerializer", 
                                     serClass.getName()),
                e);
        }
        return serializer;
    }
    
    /**
     * Obtains a serializer by invoking <constructor>(javaType, xmlType) 
     * on the serClass.
     */
    protected Serializer getGeneralPurpose(String mechanismType) {
        if (javaType != null && xmlType != null) {
            if (serClassConstructor != null) {
                try {
                    return (Serializer) 
                        serClassConstructor.newInstance(
                            new Object[] {javaType, xmlType});
                } catch (InstantiationException e) {
                } catch (IllegalAccessException e) {
                } catch (InvocationTargetException e) {}
            }
        }
        return null;
    }
    
   /**
    * return constructor for class if any
    */ 
    private Constructor getConstructor(Class clazz) {
        try {
           return clazz.getConstructor(
                   new Class[] {Class.class, QName.class});
        } catch (NoSuchMethodException e) {}
        return null;
    }
    
    /**
     * Obtains a serializer by invoking getSerializer method in the 
     * javaType class or its Helper class.
     */
    protected Serializer getSpecialized(String mechanismType) {
        if (javaType != null && xmlType != null) {
            if (getSerializer != null) {
                try {
                    return (Serializer) 
                        getSerializer.invoke(
                                             null,
                                             new Object[] {mechanismType, 
                                                           javaType, 
                                                           xmlType});
                } catch (IllegalAccessException e) {
                } catch (InvocationTargetException e) {}
            }
        }
        return null;
    }

    /**
     * Returns the "getSerializer" method if any.
     */
    private Method getSerializerMethod(Class clazz) {
        Method method = null;
        try {
            method = 
                clazz.getMethod("getSerializer",
                                   new Class[] {String.class, 
                                                Class.class, 
                                                QName.class});
        } catch (NoSuchMethodException e) {}
        
        if (method == null) {
            try {
                Class helper = ClassUtils.forName(
                    clazz.getName() + "_Helper");
                method =
                    helper.getMethod("getSerializer", 
                                     new Class[] {String.class, 
                                                  Class.class, 
                                                  QName.class});
            } catch (NoSuchMethodException e) {
            } catch (ClassNotFoundException e) {}
        }
        return method;
    }

    /**
     * Returns a list of all XML processing mechanism types supported
     * by this SerializerFactory.
     *
     * @return List of unique identifiers for the supported XML 
     * processing mechanism types
     */
    public Iterator getSupportedMechanismTypes() {
        if (mechanisms == null) {
            mechanisms = new Vector();
            mechanisms.add(Constants.AXIS_SAX);
        }
        return mechanisms.iterator();
    }

    /**
     * get xmlType
     * @return xmlType QName for this factory
     */
    public QName getXMLType() {
        return xmlType;
    }
    
    /**
     * get javaType
     * @return javaType Class for this factory
     */
    public Class getJavaType() {
        return javaType;
    }

    /**
     * Utility method that intospects on a factory class to decide how to 
     * create the factory.  Tries in the following order:
     * public static create(Class javaType, QName xmlType)
     * public <constructor>(Class javaType, QName xmlType)
     * public <constructor>()
     * @param factory class
     * @param xmlType
     * @param javaType
     */
    public static SerializerFactory createFactory(Class factory, 
                                                  Class javaType, 
                                                  QName xmlType) {

        SerializerFactory sf = null;
        try {
            Method method = 
                factory.getMethod(
                    "create",
                    new Class[] {Class.class, QName.class});
            sf = (SerializerFactory) 
                method.invoke(null, 
                              new Object[] {javaType, xmlType});
        } catch (NoSuchMethodException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {}

        if (sf == null) {
            try {
                Constructor constructor =  
                    factory.getConstructor(
                        new Class[] {Class.class, QName.class});
                sf = (SerializerFactory) 
                    constructor.newInstance(
                        new Object[] {javaType, xmlType});
            } catch (NoSuchMethodException e) {
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {}
        }
        
        if (sf == null) {
            try {
                sf = (SerializerFactory) factory.newInstance();
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {}
        }
        return sf;
    }
}
