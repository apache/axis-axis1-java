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

import org.apache.axis.utils.JavaUtils;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import java.lang.reflect.Constructor;

/**
 * A deserializer for any simple type with a (String) constructor.  Note:
 * this class is designed so that subclasses need only override the makeValue 
 * method in order to construct objects of their own type.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 * @author Sam Ruby (rubys@us.ibm.com)
 * Modified for JAX-RPC @author Rich Scheuerle (scheu@us.ibm.com)
 */
public class SimpleDeserializerFactory extends BaseDeserializerFactory {

    private static final Class[] STRING_STRING_CLASS = 
        new Class [] {String.class, String.class};
    
    private static final Class[] STRING_CLASS = 
        new Class [] {String.class};

    private Constructor constructor = null;
    private boolean isBasicType = false;
    /**
     * Note that the factory is constructed with the QName and xmlType.  This is important
     * to allow distinction between primitive values and java.lang wrappers.
     **/
    public SimpleDeserializerFactory(Class javaType, QName xmlType) {
        super(SimpleDeserializer.class, xmlType, javaType);
        this.isBasicType = isBasic(javaType);
        if (!this.isBasicType) {
            // discover the constructor for non basic types
            try {
                if (QName.class.isAssignableFrom(javaType)) {
                    constructor = 
                        javaType.getDeclaredConstructor(STRING_STRING_CLASS);
                } else {
                    constructor = 
                        javaType.getDeclaredConstructor(STRING_CLASS);
                }
            } catch (java.lang.NoSuchMethodException e) {
                try {
                    constructor = 
                        javaType.getDeclaredConstructor(new Class[]{});
                } catch (java.lang.NoSuchMethodException ex) {
                    throw new IllegalArgumentException(ex.toString());
                } 
            } 
        }
    }
    
    /*
     * Any builtin type that has a constructor that takes a String is a basic
     * type.
     * This is for optimization purposes, so that we don't introspect
     * primitive java types or some basic Axis types.
     */
    private static boolean isBasic(Class javaType) {
        return (javaType.isPrimitive() || 
                javaType == java.lang.String.class ||
                javaType == java.lang.Boolean.class ||
                javaType == java.lang.Float.class ||
                javaType == java.lang.Double.class ||
                javaType == org.apache.axis.types.URI.class);
    }

    /**
     * Get the Deserializer and the set the Constructor so the
     * deserializer does not have to do introspection.
     */
    public javax.xml.rpc.encoding.Deserializer getDeserializerAs(String mechanismType)
        throws JAXRPCException {
        if (javaType == java.lang.Object.class) {
            return null;
        }
        if (this.isBasicType) {
            return new SimpleDeserializer(javaType, xmlType);
        } else {
            // XXX: don't think we can always expect to be SimpleDeserializer
            // since getSpecialized() might return a different type
            SimpleDeserializer deser = 
                (SimpleDeserializer) super.getDeserializerAs(mechanismType);
            if (deser != null) {
                deser.setConstructor(constructor);
            }
            return deser;
        }
    }
    
}
