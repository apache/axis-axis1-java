/**
 * ConfigurationType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package org.apache.axis.wsi.scm.configuration;

public class ConfigurationType  implements java.io.Serializable {
    private java.lang.String userId;
    private org.apache.axis.wsi.scm.configuration.ConfigurationEndpointType[] serviceUrl;

    public ConfigurationType() {
    }


    /**
     * Gets the userId value for this ConfigurationType.
     * 
     * @return userId 
     */
    public java.lang.String getUserId() {
        return userId;
    }


    /**
     * Sets the userId value for this ConfigurationType.
     * 
     * @param userId 
     */
    public void setUserId(java.lang.String userId) {
        this.userId = userId;
    }


    /**
     * Gets the serviceUrl value for this ConfigurationType.
     * 
     * @return serviceUrl 
     */
    public org.apache.axis.wsi.scm.configuration.ConfigurationEndpointType[] getServiceUrl() {
        return serviceUrl;
    }


    /**
     * Sets the serviceUrl value for this ConfigurationType.
     * 
     * @param serviceUrl 
     */
    public void setServiceUrl(org.apache.axis.wsi.scm.configuration.ConfigurationEndpointType[] serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public org.apache.axis.wsi.scm.configuration.ConfigurationEndpointType getServiceUrl(int i) {
        return this.serviceUrl[i];
    }

    public void setServiceUrl(int i, org.apache.axis.wsi.scm.configuration.ConfigurationEndpointType value) {
        this.serviceUrl[i] = value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ConfigurationType)) return false;
        ConfigurationType other = (ConfigurationType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.userId==null && other.getUserId()==null) || 
             (this.userId!=null &&
              this.userId.equals(other.getUserId()))) &&
            ((this.serviceUrl==null && other.getServiceUrl()==null) || 
             (this.serviceUrl!=null &&
              java.util.Arrays.equals(this.serviceUrl, other.getServiceUrl())));
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
        if (getUserId() != null) {
            _hashCode += getUserId().hashCode();
        }
        if (getServiceUrl() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getServiceUrl());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getServiceUrl(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ConfigurationType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/Configuration.xsd", "ConfigurationType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/Configuration.xsd", "UserId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceUrl");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/Configuration.xsd", "ServiceUrl"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/Configuration.xsd", "ConfigurationEndpointType"));
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
          new  org.apache.axis.encoding.ser.BeanSerializer(
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
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
