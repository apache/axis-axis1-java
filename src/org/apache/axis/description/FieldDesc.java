/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
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

package org.apache.axis.description;

import javax.xml.rpc.namespace.QName;

/**
 * FieldDescs are metadata objects which control the mapping of a given
 * Java field to/from XML.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class FieldDesc {
    /** The name of the Java field in question */
    private String fieldName;
    /** The XML QName this field maps to */
    private QName xmlName;
    /** The XML Type this field maps to/from */
    private QName xmlType;
    /** The Java type of this field */
    private Class javaType;

    /** An indication of whether this should be an element or an attribute */
    // Q : should this be a boolean, or just "instanceof ElementDesc", etc.
    private boolean _isElement = true;

    /**
     * Can't construct the base class directly, must construct either an
     * ElementDesc or an AttributeDesc.
     */
    protected FieldDesc(boolean isElement)
    {
        _isElement = isElement;
    }

    /**
     * Obtain the field name.
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Set the field name.
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Obtain the XML QName for this field
     */
    public QName getXmlName() {
        return xmlName;
    }

    /**
     * Set the XML QName for this field
     */
    public void setXmlName(QName xmlName) {
        this.xmlName = xmlName;
    }

    public Class getJavaType() {
        return javaType;
    }

    public void setJavaType(Class javaType) {
        this.javaType = javaType;
    }

    /**
     * Check if this is an element or an attribute.
     *
     * @return true if this is an ElementDesc, or false if an AttributeDesc
     */
    public boolean isElement() {
        return _isElement;
    }

    public boolean isIndexed() {
        return false;
    }
}
