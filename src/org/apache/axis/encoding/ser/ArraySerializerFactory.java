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

import org.apache.axis.Constants;
import org.apache.axis.encoding.Serializer;

import javax.xml.namespace.QName;


/**
 * SerializerFactory for arrays
 *
 * @author Rich Scheuerle <scheu@us.ibm.com>
 */
public class ArraySerializerFactory extends BaseSerializerFactory {
    public ArraySerializerFactory() {
        this(Object[].class, Constants.SOAP_ARRAY);
    }
    public ArraySerializerFactory(Class javaType, QName xmlType) {
        super(ArraySerializer.class, xmlType, javaType);
    }

    private QName componentType;
    public ArraySerializerFactory(QName componentType) {
        super(ArraySerializer.class, Constants.SOAP_ARRAY, Object[].class);
        this.componentType = componentType;
    }

    /**
     * Obtains a serializer by invoking <constructor>(javaType, xmlType)
     * on the serClass.
     */
    protected Serializer getGeneralPurpose(String mechanismType)
    {
        // Do something special only if we have an array component type

        if (componentType == null)
            return super.getGeneralPurpose(mechanismType);
        else
            return new ArraySerializer(javaType, xmlType, componentType);
    }
}
