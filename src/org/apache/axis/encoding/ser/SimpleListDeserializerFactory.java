/*
 * Copyright 2001,2004 The Apache Software Foundation.
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
 * DeserializerFactory for 
 * <xsd:simpleType ...>
 *   <xsd:list itemType="...">
 * </xsd:simpleType>
 * based on SimpleDeserializerFactory
 *
 * @author Ias (iasandcb@tmax.co.kr)
 */
public class SimpleListDeserializerFactory extends BaseDeserializerFactory {

    private Constructor constructor = null;
    /**
     * Note that the factory is constructed with the QName and xmlType.  This is important
     * to allow distinction between primitive values and java.lang wrappers.
     **/
    public SimpleListDeserializerFactory(Class javaType, QName xmlType) {
        super(SimpleListDeserializer.class, xmlType, javaType.getComponentType());
        Class componentType = javaType.getComponentType();
        try {
            if (!componentType.isPrimitive()) {
                constructor = 
                componentType.getDeclaredConstructor(new Class [] {String.class});
            }
            else {
                Class wrapper = JavaUtils.getWrapperClass(componentType);
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
        SimpleListDeserializer deser = (SimpleListDeserializer) super.getDeserializerAs(mechanismType);
        if (deser != null)
            deser.setConstructor(constructor);
        return deser;
    }
            
}
