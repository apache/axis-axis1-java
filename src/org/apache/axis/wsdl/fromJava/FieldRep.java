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

import java.lang.reflect.Field;
import java.util.Vector;

/**
 * FieldRep is the representation of a class used inside the Java2WSDL
 * emitter.  The information in the FieldRep can be changed by 
 * user provided code to affect the emitted wsdl file.  (See ClassRep)
 * @author Rich Scheuerle  (scheu@us.ibm.com)
 */
public class FieldRep extends BaseRep {
    
    private String   _name       = "";
    private Class    _type       = void.class;                                   
    private boolean  _indexed    = false;                                        

    /**
     * Constructor
     * Create an empty FieldRep (represents void)
     */ 
    public FieldRep() {
    }

    /**
     * Constructor
     * Create a default representation of FieldRep
     * @param cls Class to use to create default FieldRep
     */ 
    public FieldRep(Field field) {
        _name = field.getName();
        _type = field.getType();
        _indexed = false;
    }
       
    /**
     * Getters/Setters
     **/
    public String   getName()                { return _name; }
    public void     setName(String name)     { _name = name; }
    public Class    getType()                { return _type; }
    public void     setType(Class cls)       { _type = cls; }
    public boolean  getIndexed()             { return _indexed; }
    public void     setIndexed(boolean b)    { _indexed = b;}

    /**
     * Field properties are considered equal if their names are the 
     * same (ignoring case).
     */
    public boolean equals(FieldRep rep) {return _name.equalsIgnoreCase(rep._name); }
};
