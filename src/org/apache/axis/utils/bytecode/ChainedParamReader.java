/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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
package org.apache.axis.utils.bytecode;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Description: In ParamReader class, user can not get inherited method parameter
 * from the class they passed in. This is done because of performance. This class
 * is intended to setup the inheritant chain. If the method could not be found in
 * the derived class, it will try to search it from super class, if not in the
 * immedidate super class it will search super class's super class, until it reaches
 * the root which is java.lang.Object. This is not an eager load since it only
 * start searching the super class when it is asked to
 * User: pengyu
 * Date: Sep 6, 2003
 * Time: 11:43:24 PM
 * 
 */
public class ChainedParamReader {
    private List chain = new ArrayList();
    private List clsChain = new ArrayList();
    private Map methodToParamMap = new HashMap();

    /**
     * Process a given class's parameter names
     * @param cls the class which user wants to get parameter info from
     * @throws IOException
     */
    public ChainedParamReader(Class cls) throws IOException {
        ParamReader reader = new ParamReader(cls);
        chain.add(reader);
        clsChain.add(cls);
    }

    //now I need to create deligate methods
    /**
     * return the names of the declared parameters for the given constructor.
     * If we cannot determine the names, return null.  The returned array will
     * have one name per parameter.  The length of the array will be the same
     * as the length of the Class[] array returned by Constructor.getParameterTypes().
     * @param ctor
     * @return array of names, one per parameter, or null
     */
    public String[] getParameterNames(Constructor ctor) {
        //there is no need for the constructor chaining.
        return ((ParamReader) chain.get(0)).getParameterNames(ctor);
    }

    /**
     * return the names of the declared parameters for the given method.
     * If we cannot determine the names in the current class, we will try
     * to search its parent class until we reach java.lang.Object. If we
     * still can not find the method we will return null. The returned array
     * will have one name per parameter. The length of the array will be the same
     * as the length of the Class[] array returned by Method.getParameterTypes().
     * @param method
     * @return String[] array of names, one per parameter, or null
     **/
    public String[] getParameterNames(Method method) {
        //go find the one from the cache first
        if (methodToParamMap.containsKey(method)) {
            return (String[]) methodToParamMap.get(method);
        }

        String[] ret = null;
        for (Iterator it = chain.iterator(); it.hasNext();) {
            ParamReader reader = (ParamReader) it.next();
            ret = reader.getParameterNames(method);
            if (ret != null) {
                methodToParamMap.put(method, ret);
                return ret;
            }
        }
        //if we here, it means we need to create new chain.
        Class cls = (Class) clsChain.get(chain.size() - 1);
        while (cls.getSuperclass() != null) {
            Class superClass = cls.getSuperclass();
            try {
                ParamReader _reader = new ParamReader(superClass);
                chain.add(_reader);
                clsChain.add(cls);
                ret = _reader.getParameterNames(method);
                if (ret != null) { //we found it so just return it.
                    methodToParamMap.put(method, ret);
                    return ret;
                }
            } catch (IOException e) {
                //can not find the super class in the class path, abort here
                return null;
            }
        }
        methodToParamMap.put(method, ret);
        return null;
    }
}
