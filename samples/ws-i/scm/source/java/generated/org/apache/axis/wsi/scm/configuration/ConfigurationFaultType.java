/**
 * ConfigurationFaultType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package org.apache.axis.wsi.scm.configuration;

public class ConfigurationFaultType  extends org.apache.axis.AxisFault  implements java.io.Serializable {
    private java.lang.String message;
    private org.apache.axis.wsi.scm.configuration.ConfigurationEndpointRole[] erroneousElement;

    public ConfigurationFaultType() {
    }

    public ConfigurationFaultType(
           java.lang.String message,
           org.apache.axis.wsi.scm.configuration.ConfigurationEndpointRole[] erroneousElement) {
        this.message = message;
        this.erroneousElement = erroneousElement;
    }


    /**
     * Gets the message value for this ConfigurationFaultType.
     * 
     * @return message 
     */
    public java.lang.String getMessage() {
        return message;
    }


    /**
     * Sets the message value for this ConfigurationFaultType.
     * 
     * @param message 
     */
    public void setMessage(java.lang.String message) {
        this.message = message;
    }


    /**
     * Gets the erroneousElement value for this ConfigurationFaultType.
     * 
     * @return erroneousElement 
     */
    public org.apache.axis.wsi.scm.configuration.ConfigurationEndpointRole[] getErroneousElement() {
        return erroneousElement;
    }


    /**
     * Sets the erroneousElement value for this ConfigurationFaultType.
     * 
     * @param erroneousElement 
     */
    public void setErroneousElement(org.apache.axis.wsi.scm.configuration.ConfigurationEndpointRole[] erroneousElement) {
        this.erroneousElement = erroneousElement;
    }

    public org.apache.axis.wsi.scm.configuration.ConfigurationEndpointRole getErroneousElement(int i) {
        return this.erroneousElement[i];
    }

    public void setErroneousElement(int i, org.apache.axis.wsi.scm.configuration.ConfigurationEndpointRole value) {
        this.erroneousElement[i] = value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ConfigurationFaultType)) return false;
        ConfigurationFaultType other = (ConfigurationFaultType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.message==null && other.getMessage()==null) || 
             (this.message!=null &&
              this.message.equals(other.getMessage()))) &&
            ((this.erroneousElement==null && other.getErroneousElement()==null) || 
             (this.erroneousElement!=null &&
              java.util.Arrays.equals(this.erroneousElement, other.getErroneousElement())));
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
        if (getMessage() != null) {
            _hashCode += getMessage().hashCode();
        }
        if (getErroneousElement() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getErroneousElement());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getErroneousElement(), i);
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
        new org.apache.axis.description.TypeDesc(ConfigurationFaultType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/Configuration.xsd", "ConfigurationFaultType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("message");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/Configuration.xsd", "Message"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("erroneousElement");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/Configuration.xsd", "ErroneousElement"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/Configuration.xsd", "ConfigurationEndpointRole"));
        elemField.setMinOccurs(0);
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


    /**
     * Writes the exception data to the faultDetails
     */
    public void writeDetails(javax.xml.namespace.QName qname, org.apache.axis.encoding.SerializationContext context) throws java.io.IOException {
        context.serialize(qname, null, this);
    }
}
