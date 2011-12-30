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

package org.apache.axis.utils.cache;

import java.lang.reflect.Method;
import java.util.Hashtable;
import java.io.Serializable;

/**
 * A simple cache of previously loaded classes, and their methods.
 *
 * @author Sam Ruby <rubys@us.ibm.com>
 */
public class JavaClass implements Serializable {

    private static Hashtable classes = new Hashtable();
    private Hashtable methods = new Hashtable();

    private Class jc;

    /**
     * Find (or create if necessary) a JavaClass associated with a given
     * class
     */
    public static synchronized JavaClass find(Class jc) {
        JavaClass result = (JavaClass) classes.get(jc);

        if (result == null) {
            result = new JavaClass(jc);
            classes.put(jc, result);
        }

        return result;
    }

    /**
     * Create a cache entry for this java.lang.Class
     */
    public JavaClass(Class jc) {
        this.jc = jc;
        classes.put(jc, this);
    }
    
    /**
     * Return the java.lang.Class associated with this entry
     */
    public Class getJavaClass() {
        return jc;
    }

    /**
     * Lookup a method based on name.  This method returns an array just in
     * case there is more than one.
     * @param name name of method
     */
    public Method[] getMethod(String name) {
        JavaMethod jm = (JavaMethod) methods.get(name);

        if (jm == null) {
            methods.put(name, jm=new JavaMethod(jc, name));
        }

        return jm.getMethod();
    }
};
