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

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.Target;
import org.apache.axis.utils.BeanPropertyDescriptor;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.xml.sax.SAXException;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;

/**
 * Class which knows how to update a bean property
 */
public class BeanPropertyTarget implements Target {
    protected static Log log =
        LogFactory.getLog(BeanPropertyTarget.class.getName());

    private Object object;
    private BeanPropertyDescriptor pd;
    private int index = -1;
    
    /** 
     * This constructor is used for a normal property.
     * @param object is the bean class
     * @param pd is the property
     **/
    public BeanPropertyTarget(Object object, BeanPropertyDescriptor pd) {
        this.object = object;
        this.pd     = pd;
        this.index  = -1;  // disable indexing
    }
    
    /** 
     * This constructor is used for an indexed property.
     * @param object is the bean class
     * @param pd is the property
     * @param i is the index          
     **/
    public BeanPropertyTarget(Object object, BeanPropertyDescriptor pd, int i) {
        this.object = object;
        this.pd     = pd;
        this.index  = i;
    }
    
    /**
     * set the bean property with specified value
     * @param value is the value.
     */
    public void set(Object value) throws SAXException {

        try {
            // Set the value on the bean property. 
            // Use the indexed property method if the 
            // index is set.
            if (index < 0) {
                pd.set(object, value);
            } else {
                pd.set(object, index, value);
            }
        } catch (Exception e) {

            try {
                // If an exception occurred, 
                // see it the value can be converted into
                // the expected type.
                Class type = pd.getType();
                if (JavaUtils.isConvertable(value, type)) {
                    value = JavaUtils.convert(value, type);
                    if (index < 0)
                        pd.set(object, value);
                    else
                        pd.set(object, index, value);
                } else {
                    // It is possible that an indexed
                    // format was expected, but the
                    // entire array was sent.  In such 
                    // cases traverse the array and 
                    // call the setter for each item.
                    if (index == 0 &&
                        value.getClass().isArray() &&
                        !type.getClass().isArray()) {
                        for (int i=0; i<Array.getLength(value); i++) {
                            Object item = 
                                JavaUtils.convert(Array.get(value, i), type);
                            pd.set(object, i, item); 
                        }
                    } else {
                        // Can't proceed.  Throw an exception that
                        // will be caught in the catch block below.
                        throw e;
                    }
                }
            } catch (Exception ex) {
                // Throw a SAX exception with an informative
                // message.
                String field= pd.getName();
                if (index >=0) {
                    field += "[" + index + "]";
                }
                if (log.isErrorEnabled()) {
                    String valueType = "null";
                    if (value != null)
                        valueType = value.getClass().getName();
                    log.error(Messages.getMessage("cantConvert02",
                                                   new String[] {
                                                       valueType,
                                                       field,
                                                       pd.getType().getName()}));
                }
                if(ex instanceof InvocationTargetException) {
                    Throwable t = ((InvocationTargetException)ex).getTargetException();
                    if( t != null) {
                        String classname = this.object.getClass().getName();
                        //show the context where this exception occured.
                        throw new SAXException(Messages.getMessage("cantConvert04",
                                                   new String[] {
                                                       classname,
                                                       field,
                                                       (value==null)?null:value.toString(),
                                                       t.getMessage()}));
                    }
                }
                throw new SAXException(ex);
            }
        }
    }
}

