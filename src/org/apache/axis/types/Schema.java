/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

package org.apache.axis.types;

/**
 * Custom class for supporting XSD schema
 * 
 * @author Davanum Srinivas <dims@yahoo.com>
 */
public class Schema implements java.io.Serializable {
    private org.apache.axis.message.MessageElement[] _any;
    private org.apache.axis.types.URI targetNamespace;  // attribute
    private org.apache.axis.types.NormalizedString version;  // attribute
    private org.apache.axis.types.Id id;  // attribute

    public Schema() {
    }

    public org.apache.axis.message.MessageElement[] get_any() {
        return _any;
    }

    public void set_any(org.apache.axis.message.MessageElement[] _any) {
        this._any = _any;
    }

    public org.apache.axis.types.URI getTargetNamespace() {
        return targetNamespace;
    }

    public void setTargetNamespace(org.apache.axis.types.URI targetNamespace) {
        this.targetNamespace = targetNamespace;
    }

    public org.apache.axis.types.NormalizedString getVersion() {
        return version;
    }

    public void setVersion(org.apache.axis.types.NormalizedString version) {
        this.version = version;
    }

    public org.apache.axis.types.Id getId() {
        return id;
    }

    public void setId(org.apache.axis.types.Id id) {
        this.id = id;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Schema)) return false;
        Schema other = (Schema) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
                ((_any == null && other.get_any() == null) ||
                (_any != null &&
                java.util.Arrays.equals(_any, other.get_any()))) &&
                ((targetNamespace == null && other.getTargetNamespace() == null) ||
                (targetNamespace != null &&
                targetNamespace.equals(other.getTargetNamespace()))) &&
                ((version == null && other.getVersion() == null) ||
                (version != null &&
                version.equals(other.getVersion()))) &&
                ((id == null && other.getId() == null) ||
                (id != null &&
                id.equals(other.getId())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (get_any() != null) {
            for (int i = 0;
                 i < java.lang.reflect.Array.getLength(get_any());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(get_any(), i);
                if (obj != null &&
                        !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getTargetNamespace() != null) {
            _hashCode += getTargetNamespace().hashCode();
        }
        if (getVersion() != null) {
            _hashCode += getVersion().hashCode();
        }
        if (getId() != null) {
            _hashCode += getId().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
            new org.apache.axis.description.TypeDesc(Schema.class);

    static {
        org.apache.axis.description.FieldDesc field = new org.apache.axis.description.AttributeDesc();
        field.setFieldName("targetNamespace");
        field.setXmlName(new javax.xml.namespace.QName("", "targetNamespace"));
        field.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyURI"));
        typeDesc.addFieldDesc(field);
        field = new org.apache.axis.description.AttributeDesc();
        field.setFieldName("version");
        field.setXmlName(new javax.xml.namespace.QName("", "version"));
        field.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "normalizedString"));
        typeDesc.addFieldDesc(field);
        field = new org.apache.axis.description.AttributeDesc();
        field.setFieldName("id");
        field.setXmlName(new javax.xml.namespace.QName("", "id"));
        field.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "ID"));
        typeDesc.addFieldDesc(field);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
            java.lang.String mechType,
            java.lang.Class _javaType,
            javax.xml.namespace.QName _xmlType) {
        return
                new org.apache.axis.encoding.ser.BeanSerializer(
                        _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
            java.lang.String mechType,
            java.lang.Class _javaType,
            javax.xml.namespace.QName _xmlType) {
        return
                new org.apache.axis.encoding.ser.BeanDeserializer(
                        _javaType, _xmlType, typeDesc);
    }
}
