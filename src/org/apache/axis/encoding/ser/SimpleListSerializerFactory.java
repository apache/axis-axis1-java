/*
 * Copyright 2002,2004 The Apache Software Foundation.
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

/**
 * SerializerFactory for 
 * <xsd:simpleType ...>
 *   <xsd:list itemType="...">
 * </xsd:simpleType>
 * based on SimpleSerializerFactory
 * 
 * @author Ias (iasandcb@tmax.co.kr)
 */
package org.apache.axis.encoding.ser;
import javax.xml.namespace.QName;

public class SimpleListSerializerFactory extends BaseSerializerFactory {
    /**
     * Note that the factory is constructed with the QName and xmlType.  This is important
     * to allow distinction between primitive values and java.lang wrappers.
     **/
    public SimpleListSerializerFactory(Class javaType, QName xmlType) {
        super(SimpleListSerializer.class, xmlType, javaType);
    }
}
