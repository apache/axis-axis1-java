/**
 * CallbackFaultType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package org.apache.axis.wsi.scm.manufacturer.callback;

public class CallbackFaultType  extends org.apache.axis.AxisFault  implements java.io.Serializable {
    private org.apache.axis.wsi.scm.manufacturer.callback._CallbackFaultType_Reason reason;
    private java.lang.String conversationID;

    public CallbackFaultType() {
    }

    public CallbackFaultType(
           org.apache.axis.wsi.scm.manufacturer.callback._CallbackFaultType_Reason reason,
           java.lang.String conversationID) {
        this.reason = reason;
        this.conversationID = conversationID;
    }


    /**
     * Gets the reason value for this CallbackFaultType.
     * 
     * @return reason 
     */
    public org.apache.axis.wsi.scm.manufacturer.callback._CallbackFaultType_Reason getReason() {
        return reason;
    }


    /**
     * Sets the reason value for this CallbackFaultType.
     * 
     * @param reason 
     */
    public void setReason(org.apache.axis.wsi.scm.manufacturer.callback._CallbackFaultType_Reason reason) {
        this.reason = reason;
    }


    /**
     * Gets the conversationID value for this CallbackFaultType.
     * 
     * @return conversationID 
     */
    public java.lang.String getConversationID() {
        return conversationID;
    }


    /**
     * Sets the conversationID value for this CallbackFaultType.
     * 
     * @param conversationID 
     */
    public void setConversationID(java.lang.String conversationID) {
        this.conversationID = conversationID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CallbackFaultType)) return false;
        CallbackFaultType other = (CallbackFaultType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.reason==null && other.getReason()==null) || 
             (this.reason!=null &&
              this.reason.equals(other.getReason()))) &&
            ((this.conversationID==null && other.getConversationID()==null) || 
             (this.conversationID!=null &&
              this.conversationID.equals(other.getConversationID())));
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
        if (getReason() != null) {
            _hashCode += getReason().hashCode();
        }
        if (getConversationID() != null) {
            _hashCode += getConversationID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CallbackFaultType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-10/Manufacturer/CallBack", "CallbackFaultType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reason");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-10/Manufacturer/CallBack", "Reason"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-10/Manufacturer/CallBack", ">CallbackFaultType>Reason"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conversationID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-10/Manufacturer/CallBack", "conversationID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
