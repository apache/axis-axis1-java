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
}
