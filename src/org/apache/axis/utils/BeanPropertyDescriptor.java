/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
package org.apache.axis.utils;

import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;


/**
 * This class represents a field/property in a value type (a class with either
 * bean-style getters/setters or public fields).
 *
 * It is essentially a thin wrapper around the PropertyDescriptor from the
 * JavaBean utilities.  We wrap it with this class so that we can create
 * the subclass FieldPropertyDescriptor and access public fields (who
 * wouldn't have PropertyDescriptors normally) via the same interface.
 *
 * There are also some interesting tricks where indexed properties are
 * concerned, mostly involving the fact that we manage the arrays here
 * rather than relying on the value type class to do it itself.
 *
 * @author Rich Scheuerle <scheu@us.ibm.com>
 * @author Glen Daniels (gdaniels@apache.org)
 **/
public class BeanPropertyDescriptor
{
    protected static Log log =
        LogFactory.getLog(BeanPropertyDescriptor.class.getName());

    protected PropertyDescriptor myPD = null;

    protected static final Object[] noArgs = new Object[] {};

    /**
     * Constructor (takes a PropertyDescriptor)
     *
     * @param pd
     */
    public BeanPropertyDescriptor(PropertyDescriptor pd) {
        myPD = pd;
    }

    /**
     * Protected constructor for use by our children
     */
    protected BeanPropertyDescriptor() {
    }

    /**
     * Get our property name.
     */
    public String getName(){
        return myPD.getName();
    }

    /**
     * Query if property is readable
     * @return true if readable
     */
    public boolean isReadable() {
        return (myPD.getReadMethod() != null);
    }

    /**
     * Query if property is writeable
     * @return true if writeable
     */
    public boolean isWriteable() {
        return (myPD.getWriteMethod() != null);
    }
    /** 
     * Query if property is indexed.
     * @return true if indexed methods exist
     */
    public boolean isIndexed() {
        return (myPD instanceof IndexedPropertyDescriptor);
    }

    /**
     * Get the property value
     * @param obj is the object
     * @return the entire propery value
     */
    public Object get(Object obj) 
        throws InvocationTargetException, IllegalAccessException {
        if (myPD.getReadMethod() != null) {
            return myPD.getReadMethod().invoke(obj, noArgs);
        }
        throw new IllegalAccessException(Messages.getMessage("badGetter00"));
    }
    /**
     * Set the property value
     * @param obj is the object
     * @param newValue is the new value
     */
    public void set(Object obj, Object newValue) 
        throws InvocationTargetException, IllegalAccessException {
        if (myPD.getWriteMethod() != null) {
            myPD.getWriteMethod().invoke(obj, new Object[] {newValue});
        } else {
            throw new IllegalAccessException(Messages.getMessage("badSetter00"));
        }
    }    
    /** 
     * Get an indexed property
     * @param obj is the object
     * @param i the index
     * @return the object at the indicated index
     */
    public Object get(Object obj, int i) 
        throws InvocationTargetException, IllegalAccessException {
        if (!isIndexed()) {
            return Array.get(get(obj), i);
        } else {
            IndexedPropertyDescriptor id = (IndexedPropertyDescriptor)myPD;
            return id.getIndexedReadMethod().invoke(obj,
                                                    new Object[] {
                                                        new Integer(i)});
        }
    }

    /**
     * Set an indexed property value
     * @param obj is the object
     * @param i the index
     * @param newValue is the new value
     */
    public void set(Object obj, int i, Object newValue) 
        throws InvocationTargetException, IllegalAccessException {
        // Set the new value
        if (isIndexed()) {
            IndexedPropertyDescriptor id = (IndexedPropertyDescriptor)myPD;
            growArrayToSize(obj, id.getIndexedPropertyType(), i);
            id.getIndexedWriteMethod().invoke(obj,
                                              new Object[] {
                                                  new Integer(i), newValue});
        } else {
            Array.set(get(obj), i, newValue);
        }
    }

    /**
     * Grow the array 
     * @param obj
     * @param componentType
     * @param i
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */ 
    protected void growArrayToSize(Object obj, Class componentType, int i) throws InvocationTargetException, IllegalAccessException {
        // Get the entire array and make sure it is large enough
        Object array = get(obj);
        if (array == null || Array.getLength(array) <= i) {
            // Construct a larger array of the same type            
            Object newArray = Array.newInstance(componentType, i + 1);
            // Copy over the old elements
            if (array != null) {
                System.arraycopy(array, 0, newArray, 0, Array.getLength(array));
            }
            // Set the object to use the larger array
            set(obj, newArray);
        }
    }

    /**
     * Get the type of a property
     * @return the type of the property
     */     
    public Class getType() {
        if (isIndexed()) {
            return ((IndexedPropertyDescriptor)myPD).getIndexedPropertyType();
        } else {
            return myPD.getPropertyType();
        }
    }
}
