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

package org.apache.axis.utils.bytecode;

import serp.bytecode.BCClass;
import serp.bytecode.BCMethod;
import serp.bytecode.Project;
import serp.bytecode.Code;
import serp.bytecode.LocalVariableTable;
import serp.bytecode.LocalVariable;

import java.lang.reflect.Method;
import java.util.Vector;
import java.util.Hashtable;
import java.io.IOException;

/**
 * This class implements an Extractor using "Serp"
 * from <a href="http://serp.sourceforge.net/"></a> which is 
 * a follow up to the TechTrader Bytecode Toolkit. 
 *
 * NOTE: Currently this is just a place holder.
 *       (Does not work!!! but compiles clean!!!)
 *
 * @author <a href="mailto:dims@yahoo.com">Davanum Srinivas</a>
 * @version $Revision$ $Date$
 */
public class Serp implements Extractor {

    /**
     * Cache of tt-bytecode BCClass objects which correspond to particular
     * Java classes.
     *
     * !!! NOTE : AT PRESENT WE DO NOT CLEAN UP THIS CACHE.
     */
    private static Hashtable ttClassCache = new Hashtable();

    /**
     * Get Parameter Names using tt-bytecode
     *
     * @param method the Java method we're interested in
     * @return list of names or null
     */
    public String[] getParameterNamesFromDebugInfo(Method method) {
        Class c = method.getDeclaringClass();
        int numParams = method.getParameterTypes().length;
        Vector temp = new Vector();

        // Don't worry about it if there are no params.
        if (numParams == 0)
            return null;

        // Try to obtain a tt-bytecode class object
        BCClass bclass = (BCClass)ttClassCache.get(c);
        Project project = new Project();
        if(bclass == null) {
            bclass = project.loadClass(c);
            ttClassCache.put(c, bclass);
        }

        // Obtain the exact method we're interested in.
        BCMethod bmeth = bclass.getDeclaredMethod(method.getName(),
                                          method.getParameterTypes());

        if (bmeth == null)
            return null;

        // Get the Code object, which contains the local variable table.
        Code code = bmeth.getCode(false);
        if (code == null)
            return null;

        LocalVariableTable table = code.getLocalVariableTable(false);

        if (table == null)
            return null;

        // OK, found it.  Now scan through the local variables and record
        // the names in the right indices.
        LocalVariable [] vars = table.getLocalVariables();

        String [] argNames = new String[numParams + 1];
        argNames[0] = null; // don't know return name

        // NOTE: we scan through all the variables here, because I have been
        // told that jikes sometimes produces unpredictable ordering of the
        // local variable table.
        for (int j = 0; j < vars.length; j++) {
            LocalVariable var = vars[j];
            if (! var.getName().equals("this")) {
                if(temp.size() < var.getLocal() + 1)
                    temp.setSize(var.getLocal() + 1);
                temp.setElementAt(var.getName(), var.getLocal());
            }
        }
        int k = 0;
        for (int j = 0; j < temp.size(); j++) {
            if (temp.elementAt(j) != null) {
                k++;
                argNames[k] = (String)temp.elementAt(j);
                if(k + 1 == argNames.length)
                    break;
            }
        }
        return argNames;
    }
}
