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

import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.utils.BeanPropertyDescriptor;
import org.apache.axis.utils.BeanUtils;
import org.apache.axis.utils.JavaUtils;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;

/**
 * SerializerFactory for Bean
 *
 * @author Rich Scheuerle <scheu@us.ibm.com>
 */
public class BeanSerializerFactory extends BaseSerializerFactory {

    protected TypeDesc typeDesc = null;
    protected BeanPropertyDescriptor[] propertyDescriptor = null;

    public BeanSerializerFactory(Class javaType, QName xmlType) {
        super(BeanSerializer.class, xmlType, javaType);
        // Sometimes an Enumeration class is registered as a Bean.
        // If this is the case, silently switch to the EnumSerializer
        if (JavaUtils.isEnumClass(javaType)) {
            serClass = EnumSerializer.class;
        }
        
        typeDesc = TypeDesc.getTypeDescForClass(javaType);

        if (typeDesc != null) {
            propertyDescriptor = typeDesc.getPropertyDescriptors();
        } else {
            propertyDescriptor = BeanUtils.getPd(javaType, null);
        }
    }

    public javax.xml.rpc.encoding.Serializer getSerializerAs(String mechanismType)
        throws JAXRPCException {
        return (Serializer) super.getSerializerAs(mechanismType);
    }

    /**
     * Optimize construction of a BeanSerializer by caching the
     * type and property descriptors.
     */
    protected Serializer getGeneralPurpose(String mechanismType) {
        if (javaType == null || xmlType == null) {
           return super.getGeneralPurpose(mechanismType);
        }

        if (serClass == EnumSerializer.class) {
           return super.getGeneralPurpose(mechanismType);
        }

        return new BeanSerializer(javaType, xmlType, typeDesc, 
                                  propertyDescriptor);
    }
}
