/**
 * ConfigOptionType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package org.apache.axis.wsi.scm.configurator;

public class ConfigOptionType  implements java.io.Serializable {
    private java.lang.String name;
    private java.lang.String selectionParms;
    private org.apache.axis.wsi.scm.configuration.ConfigurationEndpointType configurationEndpoint;

    public ConfigOptionType() {
    }


    /**
     * Gets the name value for this ConfigOptionType.
     * 
     * @return name 
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this ConfigOptionType.
     * 
     * @param name 
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the selectionParms value for this ConfigOptionType.
     * 
     * @return selectionParms 
     */
    public java.lang.String getSelectionParms() {
        return selectionParms;
    }


    /**
     * Sets the selectionParms value for this ConfigOptionType.
     * 
     * @param selectionParms 
     */
    public void setSelectionParms(java.lang.String selectionParms) {
        this.selectionParms = selectionParms;
    }


    /**
     * Gets the configurationEndpoint value for this ConfigOptionType.
     * 
     * @return configurationEndpoint 
     */
    public org.apache.axis.wsi.scm.configuration.ConfigurationEndpointType getConfigurationEndpoint() {
        return configurationEndpoint;
    }


    /**
     * Sets the configurationEndpoint value for this ConfigOptionType.
     * 
     * @param configurationEndpoint 
     */
    public void setConfigurationEndpoint(org.apache.axis.wsi.scm.configuration.ConfigurationEndpointType configurationEndpoint) {
        this.configurationEndpoint = configurationEndpoint;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ConfigOptionType)) return false;
        ConfigOptionType other = (ConfigOptionType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            ((this.selectionParms==null && other.getSelectionParms()==null) || 
             (this.selectionParms!=null &&
              this.selectionParms.equals(other.getSelectionParms()))) &&
            ((this.configurationEndpoint==null && other.getConfigurationEndpoint()==null) || 
             (this.configurationEndpoint!=null &&
              this.configurationEndpoint.equals(other.getConfigurationEndpoint())));
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
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getSelectionParms() != null) {
            _hashCode += getSelectionParms().hashCode();
        }
        if (getConfigurationEndpoint() != null) {
            _hashCode += getConfigurationEndpoint().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ConfigOptionType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/Configurator.xsd", "ConfigOptionType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/Configurator.xsd", "name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("selectionParms");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/Configurator.xsd", "selectionParms"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("configurationEndpoint");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/Configurator.xsd", "configurationEndpoint"));
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
