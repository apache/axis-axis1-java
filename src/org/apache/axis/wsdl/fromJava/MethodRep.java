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
package org.apache.axis.wsdl.fromJava;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Vector;

import javax.xml.rpc.ParameterMode;

/**
 * MethodRep is the representation of a class used inside the Java2WSDL
 * emitter.  The information in the MethodRep can be changed by 
 * user provided code to affect the emitted wsdl file.  (See ClassRep)
 * @author Rich Scheuerle  (scheu@us.ibm.com)
 */
public class MethodRep {
    
    private String   _name       = "";
    private ParamRep _returns    = null;                                           
    private Vector   _parameters = new Vector();    

    /**
     * Constructor
     * Create an empty MethodRep
     */ 
    public MethodRep() {
    }

    /**
     * Constructor
     * Create a default representation of MethodRep
     * @param method Method to use to create default MethodRep
     * @param types  This is an array of parameter types                       
     * @param modes  This is an array of retrurn/parameter modes (IN, OUT, INOUT) or ParameterMode    
     * @param paramNames This is an array of names to be used for the
     *                   return/parameter names.  If null, default names
     *                   are constructed.                                          
     */ 
    public MethodRep(Method method, Class[] types, ParameterMode[] pmodes, String[] paramNames) {
        short[] modes = null;
        if (pmodes != null) {
            modes = new short[pmodes.length];
            for (int i=0; i < modes.length; i++) {
                if (pmodes[i] != null && pmodes[i].equals(ParameterMode.PARAM_MODE_IN)) {
                    modes[i] = ParamRep.IN;
                } else if (pmodes[i] != null && pmodes[i].equals(ParameterMode.PARAM_MODE_INOUT)) { 
                    modes[i] = ParamRep.INOUT;
                } else {
                    modes[i] = ParamRep.OUT;
                }
           }
        }
        init(method, types, modes, paramNames);
    }
    public MethodRep(Method method, Class[] types, short[] modes, String[] paramNames) {
        init(method, types, modes, paramNames);
    }
    protected void init(Method method, Class[] types, short[] modes, String[] paramNames) {
        _name = method.getName();
        String retName = "return";
        if ((paramNames != null) && (paramNames[0] != null) && !paramNames.equals("")) {
            retName = paramNames[0];
        }
        _returns = new ParamRep(retName, method.getReturnType(), modes[0]);

        // Create a ParamRep for each parameter.  The holderClass() method
        // returns the name of the held type if this is a holder class.
        for (int i=0; i < method.getParameterTypes().length; i++) {
            String name = null; 
            if (paramNames !=null) {
                name = (String) paramNames[i+1];
            }
            if (name == null || name.equals("") ) {
                if (modes[i+1] == ParamRep.IN) {
                    name = "in" + i;
                } else if (modes[i+1] == ParamRep.OUT) {
                    name = "out" + i;
                } else {
                    name = "inOut" + i;
                }
            }
            _parameters.add(new ParamRep(name, types[i], modes[i+1]));
        }
    }
       
    /**
     * Getters/Setters
     **/
    public String   getName()                { return _name; }
    public void     setName(String name)     { _name = name; }
    public ParamRep getReturns()             { return _returns; }
    public void     setReturns(ParamRep pr)  { _returns = pr; }
    public Vector   getParameters()          { return _parameters; }
    public void     setParameters(Vector v)  { _parameters = v; }
};
