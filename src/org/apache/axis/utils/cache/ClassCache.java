/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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
