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

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.InternalException;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.description.FieldDesc;
import org.apache.axis.description.TypeDesc;
import org.apache.commons.logging.Log;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Vector;

public class BeanUtils {

    public static final Object[] noArgs = new Object[] {}; 
    protected static Log log =
        LogFactory.getLog(BeanUtils.class.getName());

    /**
     * Create a BeanPropertyDescriptor array for the indicated class.
     * @param javaType
     * @return an ordered array of properties
     */
    public static BeanPropertyDescriptor[] getPd(Class javaType) {
        return getPd(javaType, null);
    }

    /**
     * Create a BeanPropertyDescriptor array for the indicated class.
     * @param javaType
     * @param typeDesc
     * @return an ordered array of properties
     */
    public static BeanPropertyDescriptor[] getPd(Class javaType, TypeDesc typeDesc) {
        BeanPropertyDescriptor[] pd;
        try {
            final Class secJavaType = javaType;

            // Need doPrivileged access to do introspection.
            PropertyDescriptor[] rawPd = getPropertyDescriptors(secJavaType);
            pd = processPropertyDescriptors(rawPd,javaType,typeDesc);
        } catch (Exception e) {
            // this should never happen
            throw new InternalException(e);
        }
        return pd;
    }

    private static PropertyDescriptor[] getPropertyDescriptors(final Class secJavaType) {
        return (PropertyDescriptor[])AccessController.doPrivileged(
                new PrivilegedAction() {
                    public Object run() {
                        PropertyDescriptor[] result = null;
// START FIX http://nagoya.apache.org/bugzilla/showattachment.cgi?attach_id=4937
                        try {
                            // privileged code goes here
                            if (AxisFault.class.isAssignableFrom(secJavaType)) {
                                // Don't include AxisFault data
                                result = Introspector.
                                        getBeanInfo(secJavaType,AxisFault.class).
                                        getPropertyDescriptors();
                            } else if (Throwable.class != secJavaType && Throwable.class.isAssignableFrom(secJavaType)) {
                                // Don't include Throwable data
                                result = Introspector.
                                        getBeanInfo(secJavaType,Throwable.class).
                                        getPropertyDescriptors();
                            } else {
                                // privileged code goes here
                                result = Introspector.
                                        getBeanInfo(secJavaType).
                                        getPropertyDescriptors();
                            }
// END FIX http://nagoya.apache.org/bugzilla/showattachment.cgi?attach_id=4937
                        } catch (java.beans.IntrospectionException Iie) {
                        }
                        return result;
                    }
                });
    }

    /**
     * Return a list of properties in the bean which should be attributes
     */
    public static Vector getBeanAttributes(Class javaType, TypeDesc typeDesc) {
        Vector ret = new Vector();

        if (typeDesc == null) {
            // !!! Support old-style beanAttributeNames for now

            // See if this object defined the 'getAttributeElements' function
            // which returns a Vector of property names that are attributes
            try {
                Method getAttributeElements =
                        javaType.getMethod("getAttributeElements",
                                           new Class [] {});
                // get string array
                String[] array = (String[])getAttributeElements.invoke(null, noArgs);

                // convert it to a Vector
                ret = new Vector(array.length);
                for (int i = 0; i < array.length; i++) {
                    ret.add(array[i]);
                }
            } catch (Exception e) {
                ret.clear();
            }
        } else {
            FieldDesc [] fields = typeDesc.getFields();
            if (fields != null) {
                for (int i = 0; i < fields.length; i++) {
                    FieldDesc field = fields[i];
                    if (!field.isElement()) {
                        ret.add(field.getFieldName());
                    }
                }
            }
        }

        return ret;
    }
    /**
     * This method attempts to sort the property descriptors using
     * the typeDesc and order defined in the class.
     *
     * This routine also looks for set(i, type) and get(i) methods and adjusts the
     * property to use these methods instead.  These methods are generated by the
     * emitter for "collection" of properties (i.e. maxOccurs="unbounded" on an element).
     * JAX-RPC is silent on this issue, but web services depend on this kind of behaviour.
     * The method signatures were chosen to match bean indexed properties.
     */
    public static BeanPropertyDescriptor[] processPropertyDescriptors(
                  PropertyDescriptor[] rawPd, Class cls) {
        return processPropertyDescriptors(rawPd, cls, null);
    }

    public static BeanPropertyDescriptor[] processPropertyDescriptors(
                  PropertyDescriptor[] rawPd, Class cls, TypeDesc typeDesc) {

        // Create a copy of the rawPd called myPd
        BeanPropertyDescriptor[] myPd = new BeanPropertyDescriptor[rawPd.length];

        ArrayList pd = new ArrayList();

        try {
            for (int i=0; i < rawPd.length; i++) {
                // Skip the special "any" field
                if (rawPd[i].getName().equals(Constants.ANYCONTENT))
                    continue;
                pd.add(new BeanPropertyDescriptor(rawPd[i]));
            }

            // Now look for public fields
            Field fields[] = cls.getFields();
            if (fields != null && fields.length > 0) {
                // See if the field is in the list of properties
                // add it if not.
                for (int i=0; i < fields.length; i++) {
                    Field f = fields[i];
                    // skip if field come from a java.* or javax.* package
                    // WARNING: Is this going to make bad things happen for
                    // users JavaBeans?  Do they WANT these fields serialzed?
                    String clsName = f.getDeclaringClass().getName();
                    if (clsName.startsWith("java.") ||
                            clsName.startsWith("javax.")) {
                        continue;
                    }
                    // skip field if it is final, transient, or static
                    if (!(Modifier.isStatic(f.getModifiers()) ||
                            Modifier.isFinal(f.getModifiers()) ||
                            Modifier.isTransient(f.getModifiers()))) {
                        String fName = f.getName();
                        boolean found = false;
                        for (int j=0; j< rawPd.length && !found; j++) {
                            String pName =
                                    ((BeanPropertyDescriptor)pd.get(j)).getName();
                            if (pName.length() == fName.length() &&
                                    pName.substring(0,1).equalsIgnoreCase(
                                            fName.substring(0,1))) {

                                found = pName.length() == 1  ||
                                        pName.substring(1).equals(fName.substring(1));
                            }
                        }

                        if (!found) {
                            pd.add(new FieldPropertyDescriptor(f.getName(), f));
                        }
                    }
                }
            }

            // If typeDesc meta data exists, re-order according to the fields
            if (typeDesc != null &&
                    typeDesc.getFields(true) != null) {
                ArrayList ordered = new ArrayList();
                // Add the TypeDesc elements first
                FieldDesc[] fds = typeDesc.getFields(true);
                for (int i=0; i<fds.length; i++) {
                    FieldDesc field = fds[i];
                    if (field.isElement()) {
                        boolean found = false;
                        for (int j=0;
                             j<pd.size() && !found;
                             j++) {
                            if (field.getFieldName().equals(
                                    ((BeanPropertyDescriptor)pd.get(j)).getName())) {
                                ordered.add(pd.remove(j));
                                found = true;
                            }
                        }
                    }
                }
                // Add the remaining elements
                while (pd.size() > 0) {
                    ordered.add(pd.remove(0));
                }
                // Use the ordered list
                pd = ordered;
            }

            myPd = new BeanPropertyDescriptor[pd.size()];
            for (int i=0; i <pd.size(); i++) {
                myPd[i] = (BeanPropertyDescriptor) pd.get(i);
            }
        } catch (Exception e) {
            log.error(Messages.getMessage("badPropertyDesc00",
                                           cls.getName()), e);
            throw new InternalException(e);
        }

        return myPd;
    }

    public static BeanPropertyDescriptor getAnyContentPD(Class javaType) {
        PropertyDescriptor [] pds = getPropertyDescriptors(javaType);
        for (int i = 0; i < pds.length; i++) {
            PropertyDescriptor pd = pds[i];
            if (pd.getName().equals(Constants.ANYCONTENT))
                return new BeanPropertyDescriptor(pd);
        }
        return null;
    }
}
