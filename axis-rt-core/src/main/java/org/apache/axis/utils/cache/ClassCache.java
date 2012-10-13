/*
 * Copyright 2001-2002,2004 The Apache Software Foundation.
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

import org.apache.axis.utils.ClassUtils;

import java.util.Hashtable;

/**
 * A cache class for JavaClass objects, which enables us to quickly reference
 * methods.
 *
 * @author Doug Davis (dug@us.ibm.com)
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class ClassCache {
    Hashtable classCache = new Hashtable();

    public ClassCache() {
    }

    /**
     * Register a class in the cache.  Creates a new JavaClass object
     * around the given class, and inserts it into the Hashtable, replacing
     * any previous entry.
     *
     * @param name the name of the class.
     * @param cls a Java Class.
     */
    public synchronized void registerClass(String name, Class cls) {
        if (name == null) return; // ??? Should we let this NPE?
        JavaClass oldClass = (JavaClass) classCache.get(name);
        if (oldClass != null && oldClass.getJavaClass() == cls) return;
        classCache.put(name, new JavaClass(cls));
    }

    /**
     * Remove an entry from the cache.
     *
     * @param name the name of the class to remove.
     */
    public synchronized void deregisterClass(String name) {
        classCache.remove(name);
    }

    /**
     * Query a given class' cache status.
     *
     * @param name a class name
     * @return true if the class is in the cache, false otherwise
     */
    public boolean isClassRegistered(String name) {
        return (classCache != null && classCache.get(name) != null);
    }

    /**
     * Find the cached JavaClass entry for this class, creating one
     * if necessary.
     * @param className name of the class desired
     * @param cl ClassLoader to use if we need to load the class
     * @return JavaClass entry
     */
    public JavaClass lookup(String className, ClassLoader cl) 
            throws ClassNotFoundException {
        if (className == null) {
            return null;
        }
        JavaClass jc = (JavaClass) classCache.get(className);
        if ((jc == null) && (cl != null)) {
            // Try to load the class with the specified classloader
            Class cls = ClassUtils.forName(className, true, cl);
            jc = new JavaClass(cls);
        }
        return jc;
    }
}

;
