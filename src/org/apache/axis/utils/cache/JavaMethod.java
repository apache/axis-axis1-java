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

package org.apache.axis.utils.cache;

import java.util.Vector;
import java.lang.reflect.Method;

/**
 * A simple cache of previously loaded methods
 *
 * @author Sam Ruby <rubys@us.ibm.com>
 */
public class JavaMethod {

    // at most, one of the following two are non-null, depending
    // on the number of methods by this name found in the class
    private Method unique = null;
    private Method[] methods = null;

    /**
     * Create a cache entry for this java.lang.Class
     * @param jc java.lang.Class which will be searched for methods
     * @param name name of the method
     */
    public JavaMethod(Class jc, String name) {
        Method[] methods = jc.getMethods();
        Vector workinglist = null;

        // scan for matching names, saving the match if it is unique,
        // otherwise accumulating a list
        for (int i=0; i<methods.length; i++) {
            if (methods[i].getName().equals(name)) {
                if (unique != null) {
                    workinglist = new Vector();
                    workinglist.addElement(unique);
                    workinglist.addElement(methods[i]);
                    unique = null;
                } else if (workinglist != null) {
                    workinglist.addElement(methods[i]);
                } else {
                    unique = methods[i];
                }
            }
        }

        // If a list was found, convert it into an array
        if (workinglist != null) {
            this.methods = new Method[workinglist.size()];
            workinglist.copyInto(this.methods);
        }
    }
  
    /**
     * Attempt to find the closest matching method based on the number
     * of arguments only.  Note: if there are multiple matches, one
     * will be picked randomly.  If the name is unique, it is simply
     * returned without checking as attempts to invoke a method based
     * on this will undoubtably fail anyway.
     * @param numargs number of arguments
     * @return closest match
     */
    public Method getMethod(int numargs) {
        if (methods != null) {
            for (int i=0; i<methods.length; i++) {
                if (methods[i].getParameterTypes().length == numargs) {
                    return methods[i];
                }
            }
        } 

        return unique;
    }
};
