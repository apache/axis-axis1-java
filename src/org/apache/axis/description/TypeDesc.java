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

import org.apache.axis.utils.BeanPropertyDescriptor;
import org.apache.axis.utils.BeanUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.cache.MethodCache;

import javax.xml.namespace.QName;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * A TypeDesc represents a Java<->XML data binding.  It is essentially
 * a collection of FieldDescs describing how to map each field in a Java
 * class to XML.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class TypeDesc implements Serializable {
    public static final Class [] noClasses = new Class [] {};
    public static final Object[] noObjects = new Object[] {};
    
    /** A map of class -> TypeDesc */
    private static Map classMap = new Hashtable();

    /** Have we already introspected for the special "any" property desc? */
    private boolean lookedForAny = false;

    /** Can this instance search for metadata in parents of the type it describes? */
    private boolean canSearchParents = true;
    private boolean hasSearchedParents = false;
    
    /** My superclass TypeDesc */
    private TypeDesc parentDesc = null;

    /**
     * Creates a new <code>TypeDesc</code> instance.  The type desc can search
     * the metadata of its type'sparent classes.
     *
     * @param javaClass a <code>Class</code> value
     */
    public TypeDesc(Class javaClass) {
        this(javaClass, true);
    }
    
    /**
     * Creates a new <code>TypeDesc</code> instance.
     *
     * @param javaClass a <code>Class</code> value
     * @param canSearchParents whether the type desc can search the metadata of
     * its type's parent classes.
     */
    public TypeDesc(Class javaClass, boolean canSearchParents) {
        this.javaClass = javaClass;
        this.canSearchParents = canSearchParents;
        Class cls = javaClass.getSuperclass();
        if (cls != null && !cls.getName().startsWith("java.")) {
            parentDesc = getTypeDescForClass(cls);
        }        
    }

    /**
     * Static function to explicitly register a type description for
     * a given class.
     * 
     * @param cls the Class we're registering metadata about
     * @param td the TypeDesc containing the metadata
     */ 
    public static void registerTypeDescForClass(Class cls, TypeDesc td)
    {
        classMap.put(cls, td);
    }

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
        // First see if we have one explicitly registered
        TypeDesc result = (TypeDesc)classMap.get(cls);
        if (result != null) {
            return result;
        }
        
        try {
            Method getTypeDesc = MethodCache.getInstance().getMethod(cls, "getTypeDesc", noClasses);
            if (getTypeDesc != null) {
                return (TypeDesc)getTypeDesc.invoke(null, noObjects);
            }
        } catch (Exception e) {
        }
        return null;
    }

    /** The Java class for this type */
    private Class javaClass = null;

    /** The XML type QName for this type */
    private QName xmlType = null;

    /** The various fields in here */
    private FieldDesc [] fields;

    /** A cache of FieldDescs by name */
    private HashMap fieldNameMap = new HashMap();
    
    /** A cache of FieldDescs by Element QName */
    private HashMap fieldElementMap = null;
    
    /** Are there any fields which are serialized as attributes? */
    private boolean _hasAttributes = false;

    /** Introspected property descriptors */
    private BeanPropertyDescriptor[] propertyDescriptors = null;
    /** Map with key = property descriptor name, value = descriptor */
    private Map propertyMap = null;

    /**
     * Indication if this type has support for xsd:any.
     */
    private BeanPropertyDescriptor anyDesc = null;

    public BeanPropertyDescriptor getAnyDesc() {
        return anyDesc;
    }

    /**
     * Obtain the current array of FieldDescs
     */
    public FieldDesc[] getFields() {
        return fields;
    }

    public FieldDesc[] getFields(boolean searchParents) {
        // note that if canSearchParents is false, this is identical
        // to getFields(), because the parent type's metadata is off
        // limits for restricted types which are required to provide a
        // complete description of their content model in their own
        // metadata, per the XML schema rules for
        // derivation-by-restriction
        if (canSearchParents && searchParents && !hasSearchedParents) {
            // check superclasses if they exist
            if (parentDesc != null) {
                FieldDesc [] parentFields = parentDesc.getFields(true);
// START FIX http://nagoya.apache.org/bugzilla/show_bug.cgi?id=17188
                if (parentFields != null) {
                    if (fields != null) {
                        FieldDesc [] ret = new FieldDesc[parentFields.length + fields.length];
                        System.arraycopy(parentFields, 0, ret, 0, parentFields.length);
                        System.arraycopy(fields, 0, ret, parentFields.length, fields.length);
                        fields = ret;
                    } else {
                        FieldDesc [] ret = new FieldDesc[parentFields.length];
                        System.arraycopy(parentFields, 0, ret, 0, parentFields.length);
                        fields = ret;
                    }
// END FIX http://nagoya.apache.org/bugzilla/show_bug.cgi?id=17188
                }
            }
            
            hasSearchedParents = true;
        }

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
        fieldElementMap = null;
        
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
        if (field == null) {
            throw new IllegalArgumentException(
                    Messages.getMessage("nullFieldDesc"));
        }
        
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
        if (desc == null) {
            // check superclasses if they exist
            // and we are allowed to look
            if (canSearchParents) {
                if (parentDesc != null) {
                    return parentDesc.getElementNameForField(fieldName);
                }
            }
        } else if (!desc.isElement()) {
            return null;
        }
        return desc.getXmlName();
    }
    
    /**
     * Get the QName associated with this field, but only if it's
     * marked as an attribute.
     */
    public QName getAttributeNameForField(String fieldName)
    {
        FieldDesc desc = (FieldDesc)fieldNameMap.get(fieldName);
        if (desc == null) {
            // check superclasses if they exist
            // and we are allowed to look
            if (canSearchParents) {
                if (parentDesc != null) {
                    return parentDesc.getAttributeNameForField(fieldName);
                }
            }
        } else if (desc.isElement()) {
            return null;
        }
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
        // have we already computed the answer to this question?
        if (fieldElementMap != null) {
            String cached = (String) fieldElementMap.get(qname);
            if (cached != null) return cached;
        }

        String result = null;

        String localPart = qname.getLocalPart();

        // check fields in this class
        for (int i = 0; fields != null && i < fields.length; i++) {
            FieldDesc field = fields[i];
            if (field.isElement()) {
                QName xmlName = field.getXmlName();
                if (localPart.equals(xmlName.getLocalPart())) {
                    if (ignoreNS || qname.getNamespaceURI().
                                        equals(xmlName.getNamespaceURI())) {
                        result = field.getFieldName();
                    }
                }
            }
        }
        
        // check superclasses if they exist
        // and we are allowed to look
        if (result == null && canSearchParents) {
            if (parentDesc != null) {
                result = parentDesc.getFieldNameForElement(qname, ignoreNS);
            }
        }

        // cache the answer away for quicker retrieval next time.
        if (result != null) {
            if (fieldElementMap == null) fieldElementMap = new HashMap();
            fieldElementMap.put(qname, result);
        }

        return result;
    }
    
    /**
     * Get the field name associated with this QName, but only if it's
     * marked as an attribute.
     */
    public String getFieldNameForAttribute(QName qname)
    {
        String possibleMatch = null;

        for (int i = 0; fields != null && i < fields.length; i++) {
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
        
        if (possibleMatch == null && canSearchParents) {
            // check superclasses if they exist
            // and we are allowed to look
            if (parentDesc != null) {
                possibleMatch = parentDesc.getFieldNameForAttribute(qname);
            }
        }
        
        return possibleMatch;
    }

    /**
     * Get a FieldDesc by field name.
     */
    public FieldDesc getFieldByName(String name)
    {
        FieldDesc ret = (FieldDesc)fieldNameMap.get(name);
        if (ret == null && canSearchParents) {
            if (parentDesc != null) {
                ret = parentDesc.getFieldByName(name);
            }
        }
        return ret;
    }

    /**
     * Do we have any FieldDescs marked as attributes?
     */
    public boolean hasAttributes() {
        if (_hasAttributes)
            return true;
        
        if (canSearchParents) {
            if (parentDesc != null) {
                return parentDesc.hasAttributes();
            }
        }

        return false;
    }

    public QName getXmlType() {
        return xmlType;
    }

    public void setXmlType(QName xmlType) {
        this.xmlType = xmlType;
    }

    /**
     * Get/Cache the property descriptors
     * @return PropertyDescriptor
     */
    public BeanPropertyDescriptor[] getPropertyDescriptors() {
        // Return the propertyDescriptors if already set.
        // If not set, use BeanUtils.getPd to get the property descriptions.
        //
        // Since javaClass is a generated class, there
        // may be a faster way to set the property descriptions than
        // using BeanUtils.getPd.  But for now calling getPd is sufficient.
        if (propertyDescriptors == null) {
            makePropertyDescriptors();
        }
        return propertyDescriptors;
    }
    
    private synchronized void makePropertyDescriptors() {
        if (propertyDescriptors != null)
            return;

        propertyDescriptors = BeanUtils.getPd(javaClass, this);
        if (!lookedForAny) {
            anyDesc = BeanUtils.getAnyContentPD(javaClass);
            lookedForAny = true;
        }
    }

    public BeanPropertyDescriptor getAnyContentDescriptor() {
        if (!lookedForAny) {
            anyDesc = BeanUtils.getAnyContentPD(javaClass);
            lookedForAny = true;
        }
        return anyDesc;
    }

    /**
     * Get/Cache the property descriptor map
     * @return Map with key=propertyName, value=descriptor
     */
    public Map getPropertyDescriptorMap() {
        synchronized (this) {
            // Return map if already set.
            if (propertyMap != null) {
                return propertyMap;
            }

            // Make sure properties exist
            if (propertyDescriptors == null) {
                getPropertyDescriptors();  
            }

            // Build the map
            propertyMap = new HashMap();
            for (int i = 0; i < propertyDescriptors.length; i++) {
                BeanPropertyDescriptor descriptor = propertyDescriptors[i];
                propertyMap.put(descriptor.getName(), descriptor);
            }
        }
        return propertyMap;
    }
}
