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

package org.apache.axis.components.bytecode;

import com.techtrader.modules.tools.bytecode.BCClass;
import com.techtrader.modules.tools.bytecode.BCMethod;
import com.techtrader.modules.tools.bytecode.Code;
import com.techtrader.modules.tools.bytecode.LocalVariableTableAttribute;
import com.techtrader.modules.tools.bytecode.Constants;
import com.techtrader.modules.tools.bytecode.LocalVariable;

import java.lang.reflect.Method;
import java.util.Vector;
import java.util.Hashtable;
import java.io.IOException;

import org.apache.axis.utils.JavaUtils;

/**
 * This class implements an Extractor using "TechTrader Bytecode Toolkit"
 * from <a href="http://tt-bytecode.sourceforge.net/">tt-bytecode</a>
 * @author <a href="mailto:dims@yahoo.com">Davanum Srinivas</a>
 * @version $Revision: 1.1 $ $Date: 2002/04/02 19:07:17 $
 */
public class TechTrader implements Extractor {

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

        if(bclass == null) {
            try {
                bclass = new BCClass(c);
                ttClassCache.put(c, bclass);
            } catch (IOException e) {
                // bail out - no parameter names for you!
                return null;
            }
        }

        // Obtain the exact method we're interested in.
        BCMethod bmeth = bclass.getMethod(method.getName(),
                                          method.getParameterTypes());

        if (bmeth == null)
            return null;

        // Get the Code object, which contains the local variable table.
        Code code = bmeth.getCode();
        if (code == null)
            return null;

        LocalVariableTableAttribute attr =
                (LocalVariableTableAttribute)code.getAttribute(Constants.ATTR_LOCALS);

        if (attr == null)
            return null;

        // OK, found it.  Now scan through the local variables and record
        // the names in the right indices.
        LocalVariable [] vars = attr.getLocalVariables();

        String [] argNames = new String[numParams];
        argNames[0] = null; // don't know return name

        // NOTE: we scan through all the variables here, because I have been
        // told that jikes sometimes produces unpredictable ordering of the
        // local variable table.
        for (int j = 0; j < vars.length; j++) {
            LocalVariable var = vars[j];
            if (! var.getName().equals("this")) {
                if(temp.size() < var.getIndex() + 1)
                    temp.setSize(var.getIndex() + 1);
                temp.setElementAt(var.getName(), var.getIndex());
            }
        }
        int k = 0;
        for (int j = 0; j < temp.size(); j++) {
            if(k == argNames.length)
                break;
            if (temp.elementAt(j) != null) {
                argNames[k++] = (String)temp.elementAt(j);
            }
        }
        return argNames;
    }
}
