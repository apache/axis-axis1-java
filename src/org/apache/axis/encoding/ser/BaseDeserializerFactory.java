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
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerFactory;
import org.apache.axis.i18n.Messages;
import org.apache.commons.logging.Log;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Vector;

/**
 * Base class for Axis Deserialization Factory classes for code reuse
 *
 * @author Rich Scheuerle <scheu@us.ibm.com>
 */
public abstract class BaseDeserializerFactory extends BaseFactory 
    implements DeserializerFactory {

    protected static Log log =
            LogFactory.getLog(BaseDeserializerFactory.class.getName());
    transient static Vector mechanisms = null;
    
    protected Class deserClass = null;
    protected QName xmlType = null;
    protected Class javaType = null;
    
    transient protected Constructor deserClassConstructor = null;
    transient protected Method getDeserializer = null;

    /**
     * Constructor
     * @param deserClass is the class of the Deserializer
     */
    public BaseDeserializerFactory(Class deserClass) {
        if (!Deserializer.class.isAssignableFrom(deserClass)) {
            throw new ClassCastException(
                    Messages.getMessage("BadImplementation00",
                            deserClass.getName(),
                            Deserializer.class.getName()));
        }
        this.deserClass = deserClass;
        mechanisms = new Vector();
        mechanisms.add(Constants.AXIS_SAX);
    }

    public BaseDeserializerFactory(Class deserClass,
                                   QName xmlType,
                                   Class javaType) {
        this(deserClass);
        this.xmlType = xmlType;
        this.javaType = javaType;
    }

    public javax.xml.rpc.encoding.Deserializer
        getDeserializerAs(String mechanismType)
        throws JAXRPCException {
        Deserializer deser = null;

        // Need to add code to check against mechanisms vector.

        // Try getting a specialized Deserializer
        deser = getSpecialized(mechanismType);
        
        // Try getting a general purpose Deserializer via constructor
        // invocation
        if (deser == null) {
            deser = getGeneralPurpose(mechanismType);
        }
        
        try {
            // If not successfull, try newInstance
            if (deser == null) {
                deser = (Deserializer) deserClass.newInstance();
            }
        } catch (Exception e) {
            throw new JAXRPCException(e);
        }
        return deser;
    }

   /**
     * Obtains a deserializer by invoking <constructor>(javaType, xmlType) 
     * on the deserClass.
     */
    protected Deserializer getGeneralPurpose(String mechanismType) {
        if (javaType != null && xmlType != null) {
        	Constructor deserClassConstructor = getDeserClassConstructor();
            if (deserClassConstructor != null) {
                try {
                    return (Deserializer) 
                        deserClassConstructor.newInstance(
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

    private static final Class[] CLASS_QNAME_CLASS = new Class[] {Class.class, QName.class};
    
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
     * Obtains a deserializer by invoking getDeserializer method in the 
     * javaType class or its Helper class.
     */
    protected Deserializer getSpecialized(String mechanismType) {
        if (javaType != null && xmlType != null) {
        	Method getDeserializer = getGetDeserializer();
            if (getDeserializer != null) {
                try {
                    return (Deserializer) 
                        getDeserializer.invoke(
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
     * Returns a list of all XML processing mechanism types supported by this DeserializerFactory.
     *
     * @return List of unique identifiers for the supported XML processing mechanism types
     */
    public Iterator getSupportedMechanismTypes() {
        return mechanisms.iterator();
    }

    /**
     * Utility method that intospects on a factory class to decide how to 
     * create the factory.  Tries in the following order:
     * public static create(Class javaType, QName xmlType)
     * public <constructor>(Class javaType, QName xmlType)
     * public <constructor>()
     * @param factory class
     * @param javaType
     * @param xmlType
     */
    public static DeserializerFactory createFactory(Class factory, 
                                                    Class javaType, 
                                                    QName xmlType) {
        if (factory == null) {
            return null;
        }

        DeserializerFactory df = null;
        try {
            Method method = 
                factory.getMethod("create", CLASS_QNAME_CLASS);
            df = (DeserializerFactory) 
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

        if (df == null) {
            try {
                Constructor constructor =  
                    factory.getConstructor(CLASS_QNAME_CLASS);
                df = (DeserializerFactory) 
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
        
        if (df == null) {
            try {
                df = (DeserializerFactory) factory.newInstance();
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {}
        }
        return df;
    }
    /**
     * Returns the deserClassConstructor.
     * @return Constructor
     */
	protected Constructor getDeserClassConstructor() {
    	if (deserClassConstructor == null) { 
            deserClassConstructor = getConstructor(deserClass);
        } 
    	return deserClassConstructor;
    }

    /**
     * Returns the getDeserializer.
     * @return Method
     */
	protected Method getGetDeserializer() {
    	if (getDeserializer == null) {
            getDeserializer = getMethod(javaType,"getDeserializer");    
        }
    	return getDeserializer;
    }

}
