/*
 * Copyright 2002-2004 The Apache Software Foundation.
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
 * @author Glen Daniels (gdaniels@apache.org)
 */
package org.apache.axis.encoding.ser;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;

public class SimpleSerializerFactory extends BaseSerializerFactory {

    private boolean isBasicType = false;

    /**
     * Note that the factory is constructed with the QName and xmlType.  This is important
     * to allow distinction between primitive values and java.lang wrappers.
     **/
    public SimpleSerializerFactory(Class javaType, QName xmlType) {
        super(SimpleSerializer.class, xmlType, javaType);
        this.isBasicType = isBasic(javaType);
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
                javaType == org.apache.axis.types.URI.class);
    }

    public javax.xml.rpc.encoding.Serializer getSerializerAs(String mechanismType) throws JAXRPCException {
        if (this.isBasicType) {
            return new SimpleSerializer(javaType, xmlType);
        } else {
            return super.getSerializerAs(mechanismType);
        }
    }
    
}
