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

package org.apache.axis.encoding.ser;

import org.apache.axis.Constants;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.SerializerFactory;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

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
public abstract class BaseSerializerFactory extends BaseFactory
    implements SerializerFactory {

    protected static Log log =
            LogFactory.getLog(BaseSerializerFactory.class.getName());
    
    transient static Vector mechanisms = null;
    
    protected Class serClass = null;
    protected QName xmlType = null;
    protected Class javaType = null;

    transient protected Serializer ser = null;
    transient protected Constructor serClassConstructor = null;
    transient protected Method getSerializer = null;

    /**
     * Constructor
     * @param serClass is the class of the Serializer
     * Sharing is only valid for xml primitives.
     */
    public BaseSerializerFactory(Class serClass) {
        if (!Serializer.class.isAssignableFrom(serClass)) {
            throw new ClassCastException(
                    Messages.getMessage("BadImplementation00",
                            serClass.getName(),
                            Serializer.class.getName()));
        }
        this.serClass = serClass;
    }

    public BaseSerializerFactory(Class serClass,
                                 QName xmlType, Class javaType) {
        this(serClass);
        this.xmlType = xmlType;
        this.javaType = javaType;
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
        	Constructor serClassConstructor = getSerClassConstructor();
            if (serClassConstructor != null) {
                try {
                    return (Serializer) 
                        serClassConstructor.newInstance(
                            new Object[] {javaType, xmlType});
                } catch (InstantiationException e) {
                    if(log.isDebugEnabled()) {
                        log.debug(org.apache.axis.utils.Messages.getMessage("exception00"), e);
                    }
                } catch (IllegalAccessException e) {
                    if(log.isDebugEnabled()) {
                        log.debug(org.apache.axis.utils.Messages.getMessage("exception00"), e);
                    }
                } catch (InvocationTargetException e) {
                    if(log.isDebugEnabled()) {
                        log.debug(org.apache.axis.utils.Messages.getMessage("exception00"), e);
                    }
                }
            }
        }
        return null;
    }


    private static final Class[] CLASS_QNAME_CLASS = new Class[] { Class.class, QName.class };
    
    /**
     * return constructor for class if any
     */ 
    private Constructor getConstructor(Class clazz) {
        try {
            return clazz.getConstructor(CLASS_QNAME_CLASS);
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
                    if(log.isDebugEnabled()) {
                        log.debug(org.apache.axis.utils.Messages.getMessage("exception00"), e);
                    }
                } catch (InvocationTargetException e) {
                    if(log.isDebugEnabled()) {
                        log.debug(org.apache.axis.utils.Messages.getMessage("exception00"), e);
                    }
                }
            }
        }
        return null;
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
        if (factory == null) {
            return null;
        }

        SerializerFactory sf = null;
        try {
            Method method = 
                factory.getMethod("create", CLASS_QNAME_CLASS);
            sf = (SerializerFactory) 
                method.invoke(null, 
                              new Object[] {javaType, xmlType});
        } catch (NoSuchMethodException e) {
            if(log.isDebugEnabled()) {
                log.debug(org.apache.axis.utils.Messages.getMessage("exception00"), e);
            }
        } catch (IllegalAccessException e) {
            if(log.isDebugEnabled()) {
                log.debug(org.apache.axis.utils.Messages.getMessage("exception00"), e);
            }
        } catch (InvocationTargetException e) {
            if(log.isDebugEnabled()) {
                log.debug(org.apache.axis.utils.Messages.getMessage("exception00"), e);
            }
        }

        if (sf == null) {
            try {
                Constructor constructor =  
                    factory.getConstructor(CLASS_QNAME_CLASS);
                sf = (SerializerFactory) 
                    constructor.newInstance(
                        new Object[] {javaType, xmlType});
            } catch (NoSuchMethodException e) {
                if(log.isDebugEnabled()) {
                    log.debug(org.apache.axis.utils.Messages.getMessage("exception00"), e);
                }
            } catch (InstantiationException e) {
                if(log.isDebugEnabled()) {
                    log.debug(org.apache.axis.utils.Messages.getMessage("exception00"), e);
                }
            } catch (IllegalAccessException e) {
                if(log.isDebugEnabled()) {
                    log.debug(org.apache.axis.utils.Messages.getMessage("exception00"), e);
                }
            } catch (InvocationTargetException e) {
                if(log.isDebugEnabled()) {
                    log.debug(org.apache.axis.utils.Messages.getMessage("exception00"), e);
                }
            }
        }
        
        if (sf == null) {
            try {
                sf = (SerializerFactory) factory.newInstance();
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {}
        }
        return sf;
    }
    /**
     * Returns the getSerializer.
     * @return Method
     */
	protected Method getGetSerializer() {
    	if (getSerializer == null) {
            getSerializer = getMethod(javaType, "getSerializer");
        }
    	return getSerializer;
    }

    /**
     * Returns the serClassConstructor.
     * @return Constructor
     */
	protected Constructor getSerClassConstructor() {
    	if (serClassConstructor == null) {
            serClassConstructor = getConstructor(serClass);
        }
    	return serClassConstructor;
    }

}
