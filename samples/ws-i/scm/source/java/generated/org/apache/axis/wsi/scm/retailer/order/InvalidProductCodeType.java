/**
 * InvalidProductCodeType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package org.apache.axis.wsi.scm.retailer.order;

public class InvalidProductCodeType  extends org.apache.axis.AxisFault  implements java.io.Serializable {
    private org.apache.axis.wsi.scm.retailer.order._InvalidProductCodeType_Reason reason;
    private org.apache.axis.wsi.scm.retailer.order.ProductNumber productNumber;

    public InvalidProductCodeType() {
    }

    public InvalidProductCodeType(
           org.apache.axis.wsi.scm.retailer.order._InvalidProductCodeType_Reason reason,
           org.apache.axis.wsi.scm.retailer.order.ProductNumber productNumber) {
        this.reason = reason;
        this.productNumber = productNumber;
    }


    /**
     * Gets the reason value for this InvalidProductCodeType.
     * 
     * @return reason 
     */
    public org.apache.axis.wsi.scm.retailer.order._InvalidProductCodeType_Reason getReason() {
        return reason;
    }


    /**
     * Sets the reason value for this InvalidProductCodeType.
     * 
     * @param reason 
     */
    public void setReason(org.apache.axis.wsi.scm.retailer.order._InvalidProductCodeType_Reason reason) {
        this.reason = reason;
    }


    /**
     * Gets the productNumber value for this InvalidProductCodeType.
     * 
     * @return productNumber 
     */
    public org.apache.axis.wsi.scm.retailer.order.ProductNumber getProductNumber() {
        return productNumber;
    }


    /**
     * Sets the productNumber value for this InvalidProductCodeType.
     * 
     * @param productNumber 
     */
    public void setProductNumber(org.apache.axis.wsi.scm.retailer.order.ProductNumber productNumber) {
        this.productNumber = productNumber;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof InvalidProductCodeType)) return false;
        InvalidProductCodeType other = (InvalidProductCodeType) obj;
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
            ((this.productNumber==null && other.getProductNumber()==null) || 
             (this.productNumber!=null &&
              this.productNumber.equals(other.getProductNumber())));
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
        if (getProductNumber() != null) {
            _hashCode += getProductNumber().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(InvalidProductCodeType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/RetailOrder.xsd", "InvalidProductCodeType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reason");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/RetailOrder.xsd", "Reason"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/RetailOrder.xsd", ">InvalidProductCodeType>Reason"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("productNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/RetailOrder.xsd", "ProductNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/RetailOrder.xsd", "productNumber"));
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
