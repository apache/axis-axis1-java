/**
 * ConfigOptionsType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package org.apache.axis.wsi.scm.configurator;

public class ConfigOptionsType  implements java.io.Serializable {
    private org.apache.axis.wsi.scm.configurator.ConfigOptionType[] configOption;

    public ConfigOptionsType() {
    }


    /**
     * Gets the configOption value for this ConfigOptionsType.
     * 
     * @return configOption 
     */
    public org.apache.axis.wsi.scm.configurator.ConfigOptionType[] getConfigOption() {
        return configOption;
    }


    /**
     * Sets the configOption value for this ConfigOptionsType.
     * 
     * @param configOption 
     */
    public void setConfigOption(org.apache.axis.wsi.scm.configurator.ConfigOptionType[] configOption) {
        this.configOption = configOption;
    }

    public org.apache.axis.wsi.scm.configurator.ConfigOptionType getConfigOption(int i) {
        return this.configOption[i];
    }

    public void setConfigOption(int i, org.apache.axis.wsi.scm.configurator.ConfigOptionType value) {
        this.configOption[i] = value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ConfigOptionsType)) return false;
        ConfigOptionsType other = (ConfigOptionsType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.configOption==null && other.getConfigOption()==null) || 
             (this.configOption!=null &&
              java.util.Arrays.equals(this.configOption, other.getConfigOption())));
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
        if (getConfigOption() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getConfigOption());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getConfigOption(), i);
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
        new org.apache.axis.description.TypeDesc(ConfigOptionsType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/Configurator.xsd", "ConfigOptionsType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("configOption");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/Configurator.xsd", "configOption"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/Configurator.xsd", "ConfigOptionType"));
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
