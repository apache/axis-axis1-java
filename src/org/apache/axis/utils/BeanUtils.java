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

import org.apache.axis.InternalException;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.description.FieldDesc;

import java.beans.PropertyDescriptor;
import java.beans.Introspector;
import java.util.Vector;
import java.lang.reflect.Method;

public class BeanUtils {

    public static final Object[] noArgs = new Object[] {};  // For convenience

    /**
     * Create a BeanPropertyDescriptor array for the indicated class.
     */
    public static BeanPropertyDescriptor[] getPd(Class javaType) {
        BeanPropertyDescriptor[] pd;
        try {
            PropertyDescriptor[] rawPd = Introspector.getBeanInfo(javaType).getPropertyDescriptors();
            pd = processPropertyDescriptors(rawPd,javaType);
        } catch (Exception e) {
            // this should never happen
            throw new InternalException(e);
        }
        return pd;
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
     * This method attempts to sort the property descriptors to match the
     * order defined in the class.  This is necessary to support
     * xsd:sequence processing, which means that the serialized order of
     * properties must match the xml element order.  (This method assumes that the
     * order of the set methods matches the xml element order...the emitter
     * will always order the set methods according to the xml order.)
     *
     * This routine also looks for set(i, type) and get(i) methods and adjusts the
     * property to use these methods instead.  These methods are generated by the
     * emitter for "collection" of properties (i.e. maxOccurs="unbounded" on an element).
     * JAX-RPC is silent on this issue, but web services depend on this kind of behaviour.
     * The method signatures were chosen to match bean indexed properties.
     */
    public static BeanPropertyDescriptor[] processPropertyDescriptors(
                  PropertyDescriptor[] rawPd, Class cls) {
        BeanPropertyDescriptor[] myPd = new BeanPropertyDescriptor[rawPd.length];

        for (int i=0; i < rawPd.length; i++) {
            myPd[i] = new BeanPropertyDescriptor(rawPd[i].getName(),
                                               rawPd[i].getReadMethod(),
                                               rawPd[i].getWriteMethod());
        }

        try {
            // Create a new pd array and index into the array
            int index = 0;

            // Build a new pd array
            // defined by the order of the get methods.
            BeanPropertyDescriptor[] newPd = new BeanPropertyDescriptor[rawPd.length];
            Method[] methods = cls.getMethods();
            for (int i=0; i < methods.length; i++) {
                Method method = methods[i];
                if (method.getName().startsWith("set")) {
                    boolean found = false;
                    for (int j=0; j < myPd.length && !found; j++) {
                        if (myPd[j].getWriteMethod() != null &&
                            myPd[j].getWriteMethod().equals(method)) {
                            found = true;
                            newPd[index] = myPd[j];
                            index++;
                        }
                    }
                }
            }
            // Now if there are any additional property descriptors, add them to the end.
            if (index < myPd.length) {
                for (int m=0; m < myPd.length && index < myPd.length; m++) {
                    boolean found = false;
                    for (int n=0; n < index && !found; n++) {
                        found = (myPd[m]==newPd[n]);
                    }
                    if (!found) {
                        newPd[index] = myPd[m];
                        index++;
                    }
                }
            }
            // If newPd has same number of elements as myPd, use newPd.
            if (index == myPd.length) {
                myPd = newPd;
            }

            // Get the methods of the class and look for the special set and
            // get methods for property "collections"
            for (int i=0; i < methods.length; i++) {
                if (methods[i].getName().startsWith("set") &&
                    methods[i].getParameterTypes().length == 2) {
                    for (int j=0; j < methods.length; j++) {
                        if ((methods[j].getName().startsWith("get") ||
                             methods[j].getName().startsWith("is")) &&
                            methods[j].getParameterTypes().length == 1 &&
                            methods[j].getReturnType() == methods[i].getParameterTypes()[1] &&
                            methods[j].getParameterTypes()[0] == int.class &&
                            methods[i].getParameterTypes()[0] == int.class) {
                            for (int k=0; k < myPd.length; k++) {
                                if (myPd[k].getReadMethod() != null &&
                                    myPd[k].getWriteMethod() != null &&
                                    myPd[k].getReadMethod().getName().equals(methods[j].getName()) &&
                                    myPd[k].getWriteMethod().getName().equals(methods[i].getName())) {
                                    myPd[k] = new BeanPropertyDescriptor(myPd[k].getName(),
                                                                       methods[j],
                                                                       methods[i]);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Don't process Property Descriptors if problems occur
            return myPd;
        }
        return myPd;
    }
}
