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
import java.beans.IntrospectionException;
import java.lang.reflect.Method;

import org.apache.axis.utils.cache.MethodCache;

/**
 * Deserializer for a JAX-RPC enum.
 *
 * @author Rich Scheuerle <scheu@us.ibm.com>
 * @author Sam Ruby <rubys@us.ibm.com>
 */
public class EnumDeserializer extends SimpleDeserializer {

    private Method fromStringMethod = null;

    private static final Class[] STRING_CLASS = new Class[] { java.lang.String.class };

    public EnumDeserializer(Class javaType, QName xmlType) {
        super(javaType, xmlType);
    }

    public Object makeValue(String source) throws Exception
    {
        // Invoke the fromString static method to get the Enumeration value
        if (isNil)
            return null;
        if (fromStringMethod == null) {
            try {
                fromStringMethod = MethodCache.getInstance().getMethod(javaType, "fromString", STRING_CLASS);
            } catch (Exception e) {
                throw new IntrospectionException(e.toString());
            }
        }
        return fromStringMethod.invoke(null,new Object [] { source });
    }
}
