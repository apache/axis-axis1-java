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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;


/**
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class FieldPropertyDescriptor extends BeanPropertyDescriptor {
    private Field field = null;

    /**
      * Construct a BPD with a field
      * Both must be set
      * @param _name is the name of the property
      * @param _field is the name of the public instance field
      */
     public FieldPropertyDescriptor(String _name,
                                   Field _field) {
         field = _field;
         try {
             myPD = new PropertyDescriptor(_name, null, null);
         } catch (Exception e) {
             // ???
         }
         if (_field == null || _name == null) {
             throw new IllegalArgumentException(
                     Messages.getMessage(_field == null ?
                                          "badField00" : "badProp03"));
         }
     }

    public String getName() {
        return field.getName();
    }

    /**
     * Query if property is readable
     * @return true if readable
     */
    public boolean isReadable() {
        return true;
    }

    /**
     * Query if property is writeable
     * @return true if writeable
     */
    public boolean isWriteable() {
        return true;
    }

    /**
     * Query if property is indexed.
     * Indexed properties require valid setters/getters
     * @return true if indexed methods exist
     */
    public boolean isIndexed() {
        return (field.getType().getComponentType() != null);
    }

    /**
     * Get the property value
     * @param obj is the object
     * @return the entire propery value
     */
    public Object get(Object obj)
            throws InvocationTargetException, IllegalAccessException {
        return field.get(obj);
    }

    /**
     * Set the property value
     * @param obj is the object
     * @param newValue is the new value
     */
    public void set(Object obj, Object newValue)
            throws InvocationTargetException, IllegalAccessException {
        field.set(obj, newValue);
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
            throw new IllegalAccessException("Not an indexed property");
        }

        Object array = field.get(obj);
        return Array.get(array, i);
    }

    /**
     * Set an indexed property value
     * @param obj is the object
     * @param i the index
     * @param newValue is the new value
     */
    public void set(Object obj, int i, Object newValue)
            throws InvocationTargetException, IllegalAccessException {
        if (!isIndexed()) {
            throw new IllegalAccessException("Not an indexed field!");
        }
        Class componentType = field.getType().getComponentType();
        growArrayToSize(obj, componentType, i);
        Array.set(get(obj), i, newValue);
    }

    /**
     * Get the type of a property
     * @return the type of the property
     */
    public Class getType() {
        if (isIndexed()) {
            return field.getType().getComponentType();
        } else {
            return field.getType();
        }
    }

    public Field getField() {
        return field;
    }
}
