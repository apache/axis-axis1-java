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

import org.apache.axis.encoding.Target;
import org.apache.axis.utils.BeanPropertyDescriptor;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;

import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

import org.xml.sax.SAXException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Array;

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
                        throw new SAXException(t.getMessage());
                    }
                }
                throw new SAXException(ex);
            }
        }
    }
}

