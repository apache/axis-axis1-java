/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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
package org.apache.axis.utils;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Utility methods for Class Loading.
 *
 * @author Davanum Srinvas (dims@yahoo.com)
 */
public final class ClassUtils {

    /** hash table of class loaders */
    private static java.util.Hashtable classloaders = new java.util.Hashtable();

    /**
     * Set the ClassLoader associated with the given className.
     *
     * @param className the name of a class
     */
    public static void setClassLoader(String className, ClassLoader loader) {
        if (className != null && loader != null)
            classloaders.put(className, loader);
    }

    /**
     * Obtain the ClassLoader (if any) associated with the given
     * className.
     *
     * @param className the name of a class
     * @return class loader
     */
    public static ClassLoader getClassLoader(String className) {
        if (className == null) return null;
        return (ClassLoader) classloaders.get(className);
    }

    /**
     * Deregister the ClassLoader for a given className.
     *
     * @param className the name of a class
     */
    public static void removeClassLoader(String className) {
        classloaders.remove(className);
    }


    /**
     * Use this method instead of Class.forName
     *
     * @param className Class name
     * @return java class
     * @throws ClassNotFoundException if the class is not found
     */
    public static Class forName(String className)
            throws ClassNotFoundException {
        return loadClass(className);
    }

    /**
     * Use this method instead of Class.forName (String className, boolean init, ClassLoader loader)
     *
     * @param className Class name
     * @param init initialize the class
     * @param loader class loader
     * @return java class
     *
     * @throws ClassNotFoundException if the class is not found
     */
    public static Class forName(
            String _className, boolean init, ClassLoader _loader)
            throws ClassNotFoundException {
        
        // Create final vars for doPrivileged block
        final String className = _className;
        final ClassLoader loader = _loader;
        try {
            // Get the class within a doPrivleged block
            Object ret = 
                AccessController.doPrivileged(
                    new PrivilegedAction() {
                        public Object run() {
                            try {
                                return Class.forName(className, true, loader);
                            } catch (Throwable e) {
                                return e;
                            }
                        }
                    });
            // If the class was located, return it.  Otherwise throw exception
            if (ret instanceof Class) {
                return (Class) ret;
            } else if (ret instanceof ClassNotFoundException) {
                throw (ClassNotFoundException) ret;
            } else {
                throw new ClassNotFoundException(_className);
            }
        } catch (ClassNotFoundException cnfe) {
            return loadClass(className);
        }
    }

    /**
     * Loads the class from the context class loader and then falls back to Class.forName
     *
     * @param className Class name
     * @return java class
     * @throws ClassNotFoundException if the class is not found
     */
    private static Class loadClass(String _className)
            throws ClassNotFoundException {
        // Create final vars for doPrivileged block
        final String className = _className;

        // Get the class within a doPrivleged block
        Object ret = 
            AccessController.doPrivileged(
                    new PrivilegedAction() {
                        public Object run() {
                            try {
                                // Check if the class is a registered class then
                                // use the classloader for that class.
                                ClassLoader classLoader = getClassLoader(className);
                                return Class.forName(className, true, classLoader);
                            } catch (ClassNotFoundException cnfe) {
                            }
                            
                            try {
                                // Try the context class loader
                                ClassLoader classLoader =
                                    Thread.currentThread().getContextClassLoader();
                                return Class.forName(className, true, classLoader);
                            } catch (ClassNotFoundException cnfe2) {
                                try {
                                    // Try the classloader that loaded this class.
                                    ClassLoader classLoader =
                                        ClassUtils.class.getClassLoader();
                                    return Class.forName(className, true, classLoader);
                                } catch (ClassNotFoundException cnfe3) {
                                    // Try the default class loader.
                                    try {
                                        return Class.forName(className);
                                    } catch (Throwable e) {
                                        // Still not found, return exception
                                        return e;
                                    }
                                }
                            } 
                        }
                    });

        // If the class was located, return it.  Otherwise throw exception
        if (ret instanceof Class) {
            return (Class) ret;
        } else if (ret instanceof ClassNotFoundException) {
            throw (ClassNotFoundException) ret;
        } else {
            throw new ClassNotFoundException(_className);
        }
    }
}
