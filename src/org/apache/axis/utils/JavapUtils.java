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

import org.apache.log4j.Category;

import java.lang.reflect.Method;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Hashtable;

/**
 * This is a utility class that can be used to extract information 
 * from class files using javap.  Currently the class is used to 
 * extract method parameter names for classes compiled with debug
 * information.
 *
 * @author Rich Scheuerle (scheu@us.ibm.com)      
 */
public class JavapUtils
{
    static Category category =
            Category.getInstance(JavapUtils.class.getName());

    private static Hashtable cache = new Hashtable();

    /**
     * Get the parameter names for the indicated method.
     * Returns null if no parameter names are available or accessible.
     * @param method is the Method
     * @return String[] of parameter names or null
     **/
    public static String[] getParameterNames(Method method) {
        // Get the javap output
        Vector text = javap(method.getDeclaringClass());
        if (text == null)
            return null;

        // Allocate the parameter names array
        int numParams = method.getParameterTypes().length;
        if (numParams ==0)
            return null;
        String[] paramNames = new String[numParams];
        
        // Get the Method signature without modifiers, return type and throws
        // Also javap puts a space after each comma
        String signature = method.toString();
        int start = signature.indexOf(method.getName()); 
        int end = signature.indexOf(")") +1;                
        signature = signature.substring(start,end);

        signature = JavaUtils.replace(signature, ",", ", ");

        // Find the Method
        String search = "Local variables for method ";
        int index = -1;    
        for (int i=0; i<text.size() && index<0; i++) {
            String line = (String) text.elementAt(i);
            if (line.startsWith(search) && 
                line.endsWith(signature)) {
                index = i;
            }
        }
        // If the line was not found, the parameter names are not available.
        // (Probably the class was not compiled with -g)
        if (index < 0)
            return null;
        
        // The lines after "Local variables..." will list 
        // the types and names of the parameters.  The "this" parameter
        // is ignored.  Here is an example javap snippet.
        //
        //Local variables for method int foo(short, int, java.lang.Object, short)
        //   my.Test this  pc=0, length=6, slot=0
        //   short parameter0  pc=0, length=6, slot=1
        //   int parameter1  pc=0, length=6, slot=2
        //   java.lang.Object parameter2  pc=0, length=6, slot=3
        //   short parameter3  pc=0, length=6, slot=4
        //   int localvar  pc=3, length=3, slot=5
        int paramIndex = 0;
        index++;
        while(paramIndex < paramNames.length && index < text.size()) {
            String line = (String) text.elementAt(index++);
            StringTokenizer st = new StringTokenizer(line);
            if (st.countTokens() >= 2) {
                st.nextToken();
                String name = st.nextToken();  // name is the second token
                if (!name.equals("this")) {
                    paramNames[paramIndex++] = name;
                }                    
            }
        }
        if (paramIndex == paramNames.length) {
            return paramNames;
        }
        return null;
    }

    /**
     * Invokes javap and returns each line of output.
     * @param class
     * @return Vector containing String objects representing output lines.
     **/
    public static synchronized Vector javap(Class cls) {
        if (cache.containsKey(cls)) {
            return (Vector) cache.get(cls);
        }

        Vector cachedInfo = null;        
        BufferedReader br = null;
        try {
            Runtime rt = Runtime.getRuntime();
            // The -l option is used to access the local variables.
            Process pr = rt.exec("javap -private -l " + cls.getName());
            br = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            if (br != null) {
                cachedInfo = new Vector();
                String line = br.readLine();
                while (line != null) {
                    cachedInfo.add(line);
                    line = br.readLine();
                }
            }
        } catch (Exception e) {
            cachedInfo = null;  // Ignore anything that was read
        }
        cache.put(cls, cachedInfo);
        return cachedInfo;
    }
}
