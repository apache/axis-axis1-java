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
package org.apache.axis.wsdl.symbolTable;

import javax.xml.namespace.QName;

/**
 * Simple utility struct for holding element declarations.
 * <p/>
 * This simply correlates a QName to a TypeEntry.
 * 
 * @author Glen Daniels (gdaniels@apache.org)
 * @author Tom Jordahl (tomj@apache.org)
 */
public class ElementDecl {

    /** Field name */
    private QName name;

    /** Field type */
    private TypeEntry type;
    
    /** Field documentation */
    private String documentation;

    // The following property is set if minOccurs=0.
    // An item that is not set and has minOccurs=0
    // should not be passed over the wire.  This
    // is slightly different than nillable=true which
    // causes nil=true to be passed over the wire.

    /** Field minOccursIs0 */
    private boolean minOccursIs0 = false;

    /** Field nillable */
    private boolean nillable = false;

    // Indicate if the ElementDecl represents
    // an xsd:any element

    /** Field anyElement */
    private boolean anyElement = false;

    /**
     * Constructor ElementDecl
     */
    public ElementDecl() {
    }

    /**
     * Constructor ElementDecl
     * 
     * @param type 
     * @param name 
     */
    public ElementDecl(TypeEntry type, QName name) {
        this.type = type;
        this.name = name;
    }

    /**
     * Method getType
     * 
     * @return 
     */
    public TypeEntry getType() {
        return type;
    }

    /**
     * Method setType
     * 
     * @param type 
     */
    public void setType(TypeEntry type) {
        this.type = type;
    }

    /**
     * Method getName
     * 
     * @return 
     */
    public QName getName() {
        return name;
    }

    /**
     * Method setName
     * 
     * @param name 
     */
    public void setName(QName name) {
        this.name = name;
    }

    /**
     * Method getMinOccursIs0
     * 
     * @return 
     */
    public boolean getMinOccursIs0() {
        return minOccursIs0;
    }

    /**
     * Method setMinOccursIs0
     * 
     * @param minOccursIs0 
     */
    public void setMinOccursIs0(boolean minOccursIs0) {
        this.minOccursIs0 = minOccursIs0;
    }

    /**
     * Method setNillable
     * 
     * @param nillable 
     */
    public void setNillable(boolean nillable) {
        this.nillable = nillable;
    }

    /**
     * Method getNillable
     * 
     * @return 
     */
    public boolean getNillable() {
        return nillable;
    }

    /**
     * Method getAnyElement
     * 
     * @return 
     */
    public boolean getAnyElement() {
        return anyElement;
    }

    /**
     * Method setAnyElement
     * 
     * @param anyElement 
     */
    public void setAnyElement(boolean anyElement) {
        this.anyElement = anyElement;
    }

    /**
     * Method getDocumentation
     * 
     * @return string
     */
    public String getDocumentation() {
        return documentation;
    }

    /**
     * Method setDocumentation
     * @param documentation
     */
    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }
}
