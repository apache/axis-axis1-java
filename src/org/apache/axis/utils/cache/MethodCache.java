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
	 * Returns the specified method - if any.
	 * 
	 * @param clazz the class to get the method from
	 * @param methodName the name of the method
	 * @param parameterTypes the parameters of the method
	 * @return the found method
	 * 
	 * @throws NoSuchMethodException if the method can't be found
	 */
    public Method getMethod(Class clazz, String methodName, Class[] parameterTypes) throws NoSuchMethodException {
        String className = clazz.getName();
        Map cache = getMethodCache();
        Method method = null;
		Collection methods = null;
        
        // Check the cache first.
        if (cache.containsKey(className)) {
            methods = (Collection) cache.get(clazz);
            if (methods != null) {
                Iterator it = methods.iterator();
                while (it.hasNext()) {
                    method = (Method) it.next();
                    if (method.getName().equals(methodName) && method.getParameterTypes().equals(parameterTypes)) {
                        return method;
                    }
                }
            }
        } 
        
        try {
            method = clazz.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e1) {
            try {
                Class helper = ClassUtils.forName(clazz.getName() + "_Helper");
                method = helper.getMethod(methodName, parameterTypes);
            } catch (ClassNotFoundException e2) {
            	// should never happen --> assert false;
            }
        }

		if (methods == null) {
			methods = new ArrayList();
			cache.put(className, methods);
		}
		
        methods.add(method);
        return method;
    }

}
