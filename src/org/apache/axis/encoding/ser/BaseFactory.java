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

import java.lang.reflect.Method;

import javax.xml.namespace.QName;

import org.apache.axis.utils.cache.MethodCache;

/**
 * Base Factory for BaseDeserializerFactory and BaseSerializerFactory.
 * Code Reuse for the method cache
 * 
 * @author Davanum Srinivas <dims@yahoo.com> 
 */
public abstract class BaseFactory {

    private static final Class[] STRING_CLASS_QNAME_CLASS = new Class[] {
        String.class, Class.class, QName.class
    };
    
    /**
     * Returns the the specified method - if any.
     */
    protected Method getMethod(Class clazz, String methodName) {
        Method method = null;
        try {
            method = MethodCache.getInstance().getMethod(clazz, methodName, STRING_CLASS_QNAME_CLASS);
        } catch (NoSuchMethodException e) {
        }
        return method;
    }
}
