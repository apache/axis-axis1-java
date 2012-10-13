/*
 * Copyright 2002-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.axis.description;

import javax.xml.namespace.QName;
import java.io.Serializable;

/**
 * FieldDescs are metadata objects which control the mapping of a given
 * Java field to/from XML.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class FieldDesc implements Serializable {
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

    /** An indication that minoccurs is zero */
    private boolean minOccursIs0 = false;
    
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
     * Returns the XML type (e.g. xsd:string) for this field
     */ 
    public QName getXmlType() {
        return xmlType;
    }

    /**
     * Returns the XML type (e.g. xsd:string) for this field
     */ 
    public void setXmlType(QName xmlType) {
        this.xmlType = xmlType;
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

    /**
     * Check if this field can be omitted.
     */ 
    public boolean isMinOccursZero() {
        return minOccursIs0;
    }

    /**
     * 
     * 
     * @param minOccursIs0
     * @deprecated this functionality, which is only relevant to ElementDescs,
     *             now lives in ElementDesc and is more flexible (you can set
     *             minOccurs and maxOccurs as you please)
     */ 
    public void setMinOccursIs0(boolean minOccursIs0) {
        this.minOccursIs0 = minOccursIs0;
    }
}
