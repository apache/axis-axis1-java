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

import org.apache.axis.encoding.ser.BeanSerializer;

import javax.xml.rpc.namespace.QName;
import java.util.HashMap;
import java.lang.reflect.Array;
import java.lang.reflect.Method;

/**
 * A TypeDesc represents a Java<->XML data binding.  It is essentially
 * a collection of FieldDescs describing how to map each field in a Java
 * class to XML.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class TypeDesc {
    public static Class [] noClasses = new Class [] {};

    /**
     * Static function for centralizing access to type metadata for a
     * given class.  
     *
     * This checks for a static getTypeDesc() method on the
     * class or _Helper class.
     * Eventually we may extend this to provide for external
     * metadata config (via files sitting in the classpath, etc).
     *
     * (Could introduce a cache here for speed as an optimization)
     */
    public static TypeDesc getTypeDescForClass(Class cls)
    {
        try {
            Method getTypeDesc = null;
            try {
                getTypeDesc =
                    cls.getMethod("getTypeDesc", noClasses);
            } catch (NoSuchMethodException e) {}
            if (getTypeDesc == null) {
                // Look for a Helper Class
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                Class helper = Class.forName(cls.getName() + "_Helper", true, cl);
                try {
                    getTypeDesc =
                        helper.getMethod("getTypeDesc", noClasses);
                } catch (NoSuchMethodException e) {}
            }
            if (getTypeDesc != null) {
                return (TypeDesc)getTypeDesc.invoke(null,
                                                    BeanSerializer.noArgs);
            }
        } catch (Exception e) {
        }
        return null;
    }

    private FieldDesc [] fields;

    /** A cache of FieldDescs by name */
    private HashMap fieldNameMap = new HashMap();
    
    /** Are there any fields which are serialized as attributes? */
    private boolean _hasAttributes = false;

    /**
     * Obtain the current array of FieldDescs
     */
    public FieldDesc[] getFields() {
        return fields;
    }

    /**
     * Replace the array of FieldDescs, making sure we keep our convenience
     * caches in sync.
     */
    public void setFields(FieldDesc [] newFields)
    {
        fieldNameMap = new HashMap();
        fields = newFields;
        _hasAttributes = false;
        
        for (int i = 0; i < newFields.length; i++) {
            FieldDesc field = newFields[i];
            if (field.isElement()) {
                fieldNameMap.put(field.getFieldName(), field);
            } else {
                _hasAttributes = true;
            }
        }
    }

    /**
     * Add a new FieldDesc, keeping the convenience fields in sync.
     */
    public void addFieldDesc(FieldDesc field)
    {
        if (field == null)
            throw new NullPointerException();
        
        int numFields = 0;
        if (fields != null) {
            numFields = fields.length;
        }
        FieldDesc [] newFields = new FieldDesc[numFields + 1];
        if (fields != null) {
            System.arraycopy(fields, 0, newFields, 0, numFields);
        }
        newFields[numFields] = field;
        fields = newFields;
        
        // Keep track of the field by name for fast lookup
        fieldNameMap.put(field.getFieldName(), field);
        
        if (!_hasAttributes && !field.isElement())
            _hasAttributes = true;
    }

    /**
     * Get the QName associated with this field, but only if it's
     * marked as an element.
     */
    public QName getElementNameForField(String fieldName)
    {
        FieldDesc desc = (FieldDesc)fieldNameMap.get(fieldName);
        if (desc == null || !desc.isElement())
            return null;
        return desc.getXmlName();
    }
    
    /**
     * Get the QName associated with this field, but only if it's
     * marked as an attribute.
     */
    public QName getAttributeNameForField(String fieldName)
    {
        FieldDesc desc = (FieldDesc)fieldNameMap.get(fieldName);
        if (desc == null || desc.isElement())
            return null;
        QName ret = desc.getXmlName();
        if (ret == null) {
            ret = new QName("", fieldName);
        }
        return ret;
    }

    /**
     * Get the field name associated with this QName, but only if it's
     * marked as an element.
     * 
     * If the "ignoreNS" argument is true, just compare localNames.
     */
    public String getFieldNameForElement(QName qname, boolean ignoreNS)
    {
        if (fields == null)
            return null;

        for (int i = 0; i < fields.length; i++) {
            FieldDesc field = fields[i];
            if (field.isElement()) {
                QName xmlName = field.getXmlName();
                if (qname.getLocalPart().equals(xmlName.getLocalPart())) {
                    if (ignoreNS || qname.getNamespaceURI().
                                        equals(xmlName.getNamespaceURI())) {
                        return field.getFieldName();
                    }
                }
            }
        }

        return null;
    }
    
    /**
     * Get the field name associated with this QName, but only if it's
     * marked as an attribute.
     */
    public String getFieldNameForAttribute(QName qname)
    {
        if (fields == null)
            return null;

        String possibleMatch = null;

        for (int i = 0; i < fields.length; i++) {
            FieldDesc field = fields[i];
            if (!field.isElement()) {
                // It's an attribute, so if we have a solid match, return
                // its name.
                if (qname.equals(field.getXmlName())) {
                    return field.getFieldName();
                }
                // Not a solid match, but it's still possible we might match
                // the default (i.e. QName("", fieldName))
                if (qname.getNamespaceURI().equals("") &&
                    qname.getLocalPart().equals(field.getFieldName())) {
                    possibleMatch = field.getFieldName();
                }
            }
        }
        
        return possibleMatch;
    }

    /**
     * Get a FieldDesc by field name.
     */
    public FieldDesc getFieldByName(String name)
    {
        return (FieldDesc)fieldNameMap.get(name);
    }

    /**
     * Do we have any FieldDescs marked as attributes?
     */
    public boolean hasAttributes() {
        return _hasAttributes;
    }
}
