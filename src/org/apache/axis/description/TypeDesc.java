
package org.apache.axis.description;

import javax.xml.rpc.namespace.QName;
import java.util.HashMap;
import java.lang.reflect.Array;

public class TypeDesc {
    private FieldDesc [] fields;
    private HashMap fieldNameMap = new HashMap();
    
    /** Are there any fields which are serialized as attributes? */
    private boolean _hasAttributes = false;

    public FieldDesc[] getFields() {
        return fields;
    }

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
    
    public QName getElementNameForField(String fieldName)
    {
        FieldDesc desc = (FieldDesc)fieldNameMap.get(fieldName);
        if (desc == null || !desc.isElement())
            return null;
        return desc.getXmlName();
    }
    
    public QName getAttributeNameForField(String fieldName)
    {
        FieldDesc desc = (FieldDesc)fieldNameMap.get(fieldName);
        if (desc == null || desc.isElement())
            return null;
        return desc.getXmlName();
    }
    
    public String getFieldNameForElement(QName qname)
    {
        for (int i = 0; i < fields.length; i++) {
            FieldDesc field = fields[i];
            if (field.isElement() && qname.equals(field.getXmlName()))
                return field.getFieldName();
        }

        return null;
    }
    
    public String getFieldNameForAttribute(QName qname)
    {
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
    
    public FieldDesc getFieldByName(String name)
    {
        return (FieldDesc)fieldNameMap.get(name);
    }

    public boolean hasAttributes() {
        return _hasAttributes;
    }
}
