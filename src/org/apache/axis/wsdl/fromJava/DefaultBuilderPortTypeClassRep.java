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

import java.util.Vector;
import java.util.List;

/**
 * DefaultBuilderPortTypeClassRep:
 * Extend this class to provide your own functionality.
 * See Java2WSDLFactory and ClassRep for more details.
 *
 * @author Rich Scheuerle (scheu@us.ibm.com)
 *
 */

public class DefaultBuilderPortTypeClassRep implements BuilderPortTypeClassRep {      
    /**
     * Default constructor
     **/
    DefaultBuilderPortTypeClassRep() {};
    /**
     * Construct a ClassRep from a Class
     * @param cls is the Class 
     * @param inhMethods if true, then the ClassRep will contain all methods inherited and
     *                   declared. If false, then ClassRep will contain just the declared methods.
     * @param stopClasses An optional vector of class names which if inhMethods
     *                    is true, will stop the inheritence search if encountered.
     * @param implClass  An optional implClass can be passed in that implements/extends cls.
     *                   The purpose of the implClass is to find method parameter names.             
     **/
    public ClassRep build(Class cls, boolean inhMethods, List stopClasses, Class implClass) {
        // Constructs a default ClassRep from the class
        // The Java2WSDL code examines the names/methods/params in ClassRep (and its super classes)
        // when constructing complexTypes.  So if you want to change the WSDL
        // processing, you could add/change/remove/rename the ClassRep, MethodRep and ParamRep objects.
        // For example, if you want to supply your own parameter names, you 
        // could walk the ParamRep objects of each MethodRep and supply your own names.
        // (See getResolvedMethods for a way to deal with overloading conflicts)
        ClassRep cr = new ClassRep(cls, inhMethods, stopClasses, implClass);

        return cr;
    }

    /**
     * Returns a list of MethodReps to be used for portType operation processing.
     * @param cr is the ClassRep for the PortType class
     * @param allowedMethods is a vector that contains the names of the methods to consider.
     *                       if empty or null, consider all methods.
     * @param disallowedMethods is a vector that contains the names of the methods NOT to consider.
     *                       if empty or null, consider all methods.
     * @return Vector of MethodRep objects
     **/
    public Vector getResolvedMethods(ClassRep cr,
                                     Vector allowedMethods,
                                     Vector disallowedMethods) {
        Vector methods = new Vector(cr.getMethods());

        // Remove from the array methods not contained in allowedMethods
        if (allowedMethods != null && 
            allowedMethods.size() > 0) {
            int i = 0;
            while(i < methods.size()) {
                if (!allowedMethods.contains(((MethodRep)methods.elementAt(i)).getName())) {
                    methods.remove(i);
                } else {
                    i++;
                }
            }
        }
        
        // Remove from the array methods that are listed in disallowedMethods
        if (disallowedMethods != null && disallowedMethods.size() > 0) {
            int i = 0;
            while(i < methods.size()) {
                if (disallowedMethods.contains(((MethodRep)methods.elementAt(i)).getName())) {
                    methods.remove(i);
                } else {
                    i++;
                }
            }
        }

        // It is possible that methods have the same name.
        // The following is a lame attempt to give the names unique names.
        int id = 0;
        for (int i=0; i < methods.size(); i++ ) {
            for (int j=i+1; j < methods.size(); j++ ) {
                MethodRep m1 = (MethodRep) methods.elementAt(i);
                MethodRep m2 = (MethodRep) methods.elementAt(j);
                if (m1.getName() == m2.getName()) {
                    m2.setName(m2.getName() + "_unique_"  + id);
                    id++;
                }
            }
        }
        return methods;
    }
}
