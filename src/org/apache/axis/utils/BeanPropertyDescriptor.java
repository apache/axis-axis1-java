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
package org.apache.axis.utils;

import java.io.ByteArrayOutputStream;

import org.apache.axis.utils.JavaUtils;

import java.lang.reflect.Method;
import java.beans.IntrospectionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;

/**
 * This class is essentially a copy of the PropertyDescriptor information, except
 * that the values in it can be modified.
 * Updated this to include fields that don't have getter/setters.
 * @author Rich Scheuerle <scheu@us.ibm.com>
 **/
public class BeanPropertyDescriptor
{
    protected static Log log =
        LogFactory.getLog(BeanPropertyDescriptor.class.getName());

    private String name = null;
    private Method getter = null;
    private Method setter = null;
    private Method getterIndexed = null;
    private Method setterIndexed = null;
    private Field field = null;
    private static final Object[] noArgs = new Object[] {};    

    /** 
     * Construct a BPD with getter/setter methods
     * Both must be set
     * @param String _name is the name of the property
     * @param Method _getter is the accessor method
     * @param Method _setter is the modifier method
     */
    public BeanPropertyDescriptor(String _name,
                                  Method _getter, 
                                  Method _setter) {
        name = _name;
        getter = _getter;
        setter = _setter;
        if (_getter == null || _setter == null || _name == null) {
            throw new IllegalArgumentException(
                    JavaUtils.getMessage(_getter == null ?
                                         "badGetter00" :
                                         (_setter == null ?
                                         "badSetter00" : "badProp03")));
        }
    }

    /** 
     * Construct a BPD with getter/setter methods for
     * an indexed property.  All params must be set.
     * @param String _name is the name of the property
     * @param Method _getter is the accessor method
     * @param Method _setter is the modifier method
     * @param Method _getterIndexed is the accessor method
     * @param Method _setterIndexed is the modifier method
     */
    public BeanPropertyDescriptor(String _name,
                                  Method _getter, 
                                  Method _setter,
                                  Method _getterIndexed,
                                  Method _setterIndexed) {
        this(_name, _getter, _setter);
        getterIndexed = _getterIndexed;
        setterIndexed = _setterIndexed;
        if (_getterIndexed == null || _setterIndexed == null) {
            throw new IllegalArgumentException(
                    JavaUtils.getMessage(_getterIndexed == null ?
                                         "badAccessor00" : "badModifier00"));
        }
    }

    /** 
     * Construct a BPD with a field
     * Both must be set
     * @param String _name is the name of the property
     * @param Field _field is the name of the public instance field
     */
    public BeanPropertyDescriptor(String _name,
                                  Field _field) {
        name = _name;
        field = _field;
        if (_field == null || _name == null) {
            throw new IllegalArgumentException(
                    JavaUtils.getMessage(_field == null ?
                                         "badField00" : "badProp03"));
        }
    }
    
    /** 
     * Query if property is readable
     * @return true if readable
     */
    public boolean isReadable() {
        return (getter != null ||
                field != null); 
    }
    /** 
     * Query if property is writeable
     * @return true if writeable
     */
    public boolean isWriteable() {
        return (setter != null ||
                field != null);
    }
    /** 
     * Query if property is indexed.
     * Indexed properties require valid setters/getters
     * @return true if indexed methods exist
     */
    public boolean isIndexed() {
        return (getterIndexed != null && 
                setterIndexed != null);
    }

    /**
     * Get the property value
     * @param obj is the object
     * @return the entire propery value
     */
    public Object get(Object obj) 
        throws InvocationTargetException, IllegalAccessException {
        if (getter != null) {
            return getter.invoke(obj, noArgs);
        } else if (field != null) {
            return field.get(obj);
        }
        return null;
    }
    /**
     * Set the property value
     * @param obj is the object
     * @param is the new value
     */
    public void set(Object obj, Object newValue) 
        throws InvocationTargetException, IllegalAccessException {
        if (setter != null) {
            setter.invoke(obj, new Object[] {newValue});
        } else if (field != null) {
            field.set(obj, newValue);
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
            return getterIndexed.invoke(obj, new Object[] { new Integer(i)});
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

        // Get the entire array and make sure it is large enough
        Object array = get(obj);
        if (array == null || Array.getLength(array) <= i) {
            // Construct a larger array of the same type
            Class componentType = null;
            if (getterIndexed != null) {
                componentType = getterIndexed.getReturnType();
            } else if (getter != null) {
                componentType = getter.getReturnType().getComponentType();
            } else {
                componentType = field.getType().getComponentType();
            }
            Object newArray = 
                Array.newInstance(componentType,i+1);
            
            // Set the object to use the larger array
            set(obj, newArray);

            // Copy over the old elements
            int len = 0;
            if (array != null) {
                len = Array.getLength(array);
            }
            for (int index=0; index<len; index++) {
                set(obj, index, Array.get(array, index));
            }
        }
        
        // Set the new value
        if (isIndexed()) {
            setterIndexed.invoke(obj, new Object[] {new Integer(i), newValue});
        } else {
            Array.set(get(obj), i, newValue);
        }
    }    

    /**
     * Get the name of a property
     * @return String name of the property
     */     
    public String getName() {
        return name;
    }

    /**
     * Get the type of a property
     * @return the type of the property
     */     
    public Class getType() {
        if (isIndexed()) {
            return getterIndexed.getReturnType();
        } else if (getter != null) {
            return getter.getReturnType();
        } else {
            return field.getType();
        }
    }

    /**
     * Get the read Method.
     * (This is package visibility so that Bean Utils
     * can access this information.  The other methods
     * should be used during serialization/deserialization.)
     */     
    Method getReadMethod() {
        return getter;
    }
    /**
     * Get the write Method.
     * (This is package visibility so that Bean Utils
     * can access this information.  The other methods
     * should be used during serialization/deserialization.)
     */   
    Method getWriteMethod() {
        return setter;
    }
}
