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

import javax.xml.namespace.QName;


/**
 * DeserializerFactory for arrays
 *
 * @author Rich Scheuerle (scheu@us.ibm.com)
 */
public class ArrayDeserializerFactory extends BaseDeserializerFactory {
    private QName componentXmlType;

    public ArrayDeserializerFactory() {
        super(ArrayDeserializer.class);
    }

    /**
     * Constructor
     * @param componentXmlType the desired component type for this deser
     */
    public ArrayDeserializerFactory(QName componentXmlType) {
        super(ArrayDeserializer.class);
        this.componentXmlType = componentXmlType;
    }

    /**
     * getDeserializerAs() is overloaded here in order to set the default
     * item type on the ArrayDeserializers we create.
     *
     * @param mechanismType
     * @return
     */
    public javax.xml.rpc.encoding.Deserializer getDeserializerAs(String mechanismType) {
        ArrayDeserializer dser = (ArrayDeserializer) super.getDeserializerAs(mechanismType);
        dser.defaultItemType = componentXmlType;
        return dser;
    }

    public void setComponentType(QName componentType) {
        componentXmlType = componentType;
    }
}