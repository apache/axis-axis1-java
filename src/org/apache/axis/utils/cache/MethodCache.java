/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Method;

/**
 * A cache for methods.
 * Used to get methods by their signature and stores them in a local
 * cache for performance reasons.
 * This class is a singleton - so use getInstance to get an instance of it.
 *
 * @author Davanum Srinivas <dims@yahoo.com>
 * @author Sebastian Dietrich <sebastian.dietrich@anecon.com>
 */
public class MethodCache {
	/**
	 * The only instance of this class
	 */
	transient private static MethodCache instance;

    /**
     * Cache for Methods
     * In fact this is a map (with classes as keys) of a map (with method-names as keys)
     */
    transient private static ThreadLocal cache;

	/**
	 * The <i>private</i> constructor for this class.
	 * Use getInstance to get an instance (the only one).
	 */
	private MethodCache() {
		cache = new ThreadLocal();
	}

	/**
	 * Gets the only instance of this class
	 * @return the only instance of this class
	 */
	public static MethodCache getInstance() {
		if (instance == null) {
			instance = new MethodCache();
		}
		return instance;
	}

    /**
     * Returns the per thread hashmap (for method caching)
     */
    private Map getMethodCache() {
        Map map = (Map) cache.get();
        if (map == null) {
            map = new HashMap();
			cache.set(map);
        }
        return map;
    }

    /**
     * Class used as the key for the method cache table.
     *
     */
    static class MethodKey {
        /** the name of the method in the cache */
        private final String methodName;
        /** the list of types accepted by the method as arguments */
        private final Class[] parameterTypes;

        /**
         * Creates a new <code>MethodKey</code> instance.
         *
         * @param methodName a <code>String</code> value
         * @param parameterTypes a <code>Class[]</code> value
         */
        MethodKey(String methodName, Class[] parameterTypes) {
            this.methodName = methodName;
            this.parameterTypes = parameterTypes;
        }

        public boolean equals(Object other) {
            MethodKey that = (MethodKey) other;
            return this.methodName.equals(that.methodName)
                && Arrays.equals(this.parameterTypes,
                                 that.parameterTypes);
        }

        public int hashCode() {
            // allow overloaded methods to collide; we'll sort it out
            // in equals().  Note that String's implementation of
            // hashCode already caches its value, so there's no point
            // in doing so here.
            return methodName.hashCode();
        }
    }

    /** used to track methods we've sought but not found in the past */
    private static final Object NULL_OBJECT = new Object();

	/**
	 * Returns the specified method - if any.
	 *
	 * @param clazz the class to get the method from
	 * @param methodName the name of the method
	 * @param parameterTypes the parameters of the method
	 * @return the found method
	 *
	 * @throws NoSuchMethodException if the method can't be found
	 */
    public Method getMethod(Class clazz,
                            String methodName,
                            Class[] parameterTypes)
        throws NoSuchMethodException {
        String className = clazz.getName();
        Map cache = getMethodCache();
        Method method = null;
        Map methods = null;

        // Strategy is as follows:
        // construct a MethodKey to represent the name/arguments
        // of a method's signature.
        //
        // use the name of the class to retrieve a map of that
        // class' methods
        //
        // if a map exists, use the MethodKey to find the
        // associated value object.  if that object is a Method
        // instance, return it.  if that object is anything
        // else, then it's a reference to our NULL_OBJECT
        // instance, indicating that we've previously tried
        // and failed to find a method meeting our requirements,
        // so return null
        //
        // if the map of methods for the class doesn't exist,
        // or if no value is associated with our key in that map,
        // then we perform a reflection-based search for the method.
        //
        // if we find a method in that search, we store it in the
        // map using the key; otherwise, we store a reference
        // to NULL_OBJECT using the key.
        
        // Check the cache first.
        MethodKey key = new MethodKey(methodName, parameterTypes);
        if (cache.containsKey(className)) {
            methods = (Map) cache.get(className);
            if (methods != null) {
                Object o = methods.get(key);
                if (o != null) {  // cache hit
                    if (o instanceof Method) { // good cache hit
                        return (Method) o;
                    } else {                   // bad cache hit
                        // we hit the NULL_OBJECT, so this is a search
                        // that previously failed; no point in doing
                        // it again as it is a worst case search
                        // through the entire classpath.
                        return null;
                    }
                } else {
                    // cache miss: fall through to reflective search
                }
            } else {
                // cache miss: fall through to reflective search
            }
        }

        try {
            method = clazz.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e1) {
            try {
                Class helper = ClassUtils.forName(className + "_Helper");
                method = helper.getMethod(methodName, parameterTypes);
            } catch (ClassNotFoundException e2) {
                method = null;
            }
        }

        // first time we've seen this class: set up its method cache
        if (methods == null) {
            methods = new HashMap();
            cache.put(className, methods);
        }

        // when no method is found, cache the NULL_OBJECT
        // so that we don't have to repeat worst-case searches
        // every time.

        if (null == method) {
            methods.put(key, NULL_OBJECT);
        } else {
            methods.put(key, method);
        }
        return method;
    }
}
