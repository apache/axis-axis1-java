/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.axis.test.interop.mock;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Locale;

import javax.el.FunctionMapper;

public class Functions extends FunctionMapper {
    public Method resolveFunction(String prefix, String localName) {
        if (prefix.equals("fn")) {
            for (Method method : Functions.class.getMethods()) {
                if (Modifier.isStatic(method.getModifiers()) && method.getName().equals(localName)) {
                    return method;
                }
            }
        }
        return null;
    }
    
    public static String toUpperCase(String s) {
        return s.toUpperCase(Locale.ENGLISH);
    }
}