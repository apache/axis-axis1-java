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

    private Constructor constructor = null;
    /**
     * Note that the factory is constructed with the QName and xmlType.  This is important
     * to allow distinction between primitive values and java.lang wrappers.
     **/
    public SimpleDeserializerFactory(Class javaType, QName xmlType) {
        super(SimpleDeserializer.class, xmlType, javaType);
        try {
            if (!javaType.isPrimitive()) {
                if (QName.class.isAssignableFrom(javaType)) {
                    constructor = 
                        javaType.getDeclaredConstructor(new Class [] {String.class, String.class});
                } else {
                    constructor = 
                        javaType.getDeclaredConstructor(new Class [] {String.class});
                }
            }
            else {
                Class wrapper = JavaUtils.getWrapperClass(javaType);
                if (wrapper != null)
                    constructor = 
                        wrapper.getDeclaredConstructor(new Class [] {String.class});
            }
        } catch (java.lang.NoSuchMethodException e) {
            throw new IllegalArgumentException(e.toString());
        } 
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
        SimpleDeserializer deser = (SimpleDeserializer) super.getDeserializerAs(mechanismType);
        if (deser != null)
            deser.setConstructor(constructor);
        return deser;
    }
            
}
