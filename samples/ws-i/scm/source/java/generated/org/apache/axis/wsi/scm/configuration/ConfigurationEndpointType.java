/**
 * ConfigurationEndpointType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package org.apache.axis.wsi.scm.configuration;

public class ConfigurationEndpointType  implements java.io.Serializable, org.apache.axis.encoding.SimpleType {
    private org.apache.axis.types.URI value;
    private org.apache.axis.wsi.scm.configuration.ConfigurationEndpointRole role;  // attribute

    public ConfigurationEndpointType() {
    }

    // Simple Types must have a String constructor
    public ConfigurationEndpointType(org.apache.axis.types.URI value) {
        this.value = value;
    }
    public ConfigurationEndpointType(java.lang.String value) {
        try {
            this.value = new org.apache.axis.types.URI(value);
        }
        catch (org.apache.axis.types.URI.MalformedURIException mue) {
            this.value = new org.apache.axis.types.URI();
       }
    }

    // Simple Types must have a toString for serializing the value
    public java.lang.String toString() {
        return value == null ? null : value.toString();
    }


    /**
     * Gets the value value for this ConfigurationEndpointType.
     * 
     * @return value 
     */
    public org.apache.axis.types.URI getValue() {
        return value;
    }


    /**
     * Sets the value value for this ConfigurationEndpointType.
     * 
     * @param value 
     */
    public void setValue(org.apache.axis.types.URI value) {
        this.value = value;
    }


    /**
     * Gets the role value for this ConfigurationEndpointType.
     * 
     * @return role 
     */
    public org.apache.axis.wsi.scm.configuration.ConfigurationEndpointRole getRole() {
        return role;
    }


    /**
     * Sets the role value for this ConfigurationEndpointType.
     * 
     * @param role 
     */
    public void setRole(org.apache.axis.wsi.scm.configuration.ConfigurationEndpointRole role) {
        this.role = role;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ConfigurationEndpointType)) return false;
        ConfigurationEndpointType other = (ConfigurationEndpointType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.value==null && other.getValue()==null) || 
             (this.value!=null &&
              this.value.equals(other.getValue()))) &&
            ((this.role==null && other.getRole()==null) || 
             (this.role!=null &&
              this.role.equals(other.getRole())));
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
        if (getValue() != null) {
            _hashCode += getValue().hashCode();
        }
        if (getRole() != null) {
            _hashCode += getRole().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ConfigurationEndpointType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/Configuration.xsd", "ConfigurationEndpointType"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("role");
        attrField.setXmlName(new javax.xml.namespace.QName("", "Role"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/Configuration.xsd", "ConfigurationEndpointRole"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("value");
        elemField.setXmlName(new javax.xml.namespace.QName("", "value"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyURI"));
        typeDesc.addFieldDesc(elemField);
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
          new  org.apache.axis.encoding.ser.SimpleSerializer(
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
          new  org.apache.axis.encoding.ser.SimpleDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
