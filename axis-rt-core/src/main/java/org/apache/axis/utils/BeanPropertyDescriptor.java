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
package org.apache.axis.utils;

import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
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
 * @author Rich Scheuerle (scheu@us.ibm.com)
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
     * Query if property is indexed 
     * 
     * @return true if indexed methods exist 
     */
    public boolean isIndexed() {
        return (myPD instanceof IndexedPropertyDescriptor);
    }

    /**
     * Query if property is indexed or if it' an array.
     *
     * @return true if indexed methods exist or if it's an array
     */
    public boolean isIndexedOrArray() {
        return (isIndexed() || isArray());
    }

    /**
     * Query if property is an array (excluded byte[]).
     * @return true if it's an array (excluded byte[])
     */
    public boolean isArray() {
        return ((myPD.getPropertyType() != null) && myPD.getPropertyType()
                                   .isArray());
    }

    /**
     * Get the property value
     * @param obj is the object
     * @return the entire propery value
     */
    public Object get(Object obj) 
        throws InvocationTargetException, IllegalAccessException {
        Method readMethod = myPD.getReadMethod();
        if (readMethod != null) {
            return readMethod.invoke(obj, noArgs);
        } else {
            throw new IllegalAccessException(Messages.getMessage("badGetter00"));
        }
    }

    /**
     * Set the property value
     * @param obj is the object
     * @param newValue is the new value
     */
    public void set(Object obj, Object newValue) 
        throws InvocationTargetException, IllegalAccessException {
        Method writeMethod = myPD.getWriteMethod();
        if (writeMethod != null) {
            writeMethod.invoke(obj, new Object[] {newValue});
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
            // Not calling 'growArrayToSize' to avoid an extra call to the
            // property's setter. The setter will be called at the end anyway.
            // growArrayToSize(obj, myPD.getPropertyType().getComponentType(), i);
            Object array = get(obj);
            if (array == null || Array.getLength(array) <= i) {
                Class componentType = getType().getComponentType();
                Object newArray = Array.newInstance(componentType, i + 1);
                // Copy over the old elements
                if (array != null) {
                    System.arraycopy(array, 0, newArray, 0, Array.getLength(array));
                }
                array = newArray;
            }
            Array.set(array, i, newValue);
            // Fix for non-indempondent array-type propertirs.
            // Make sure we call the property's setter.
            set(obj, array);
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

    public Class getActualType() {
        return myPD.getPropertyType();
    }
}
