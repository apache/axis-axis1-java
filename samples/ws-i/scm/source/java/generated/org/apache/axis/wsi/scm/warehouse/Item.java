/**
 * Item.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package org.apache.axis.wsi.scm.warehouse;

public class Item  implements java.io.Serializable {
    private org.apache.axis.types.NonNegativeInteger productNumber;
    private org.apache.axis.types.UnsignedShort quantity;

    public Item() {
    }


    /**
     * Gets the productNumber value for this Item.
     * 
     * @return productNumber 
     */
    public org.apache.axis.types.NonNegativeInteger getProductNumber() {
        return productNumber;
    }


    /**
     * Sets the productNumber value for this Item.
     * 
     * @param productNumber 
     */
    public void setProductNumber(org.apache.axis.types.NonNegativeInteger productNumber) {
        this.productNumber = productNumber;
    }


    /**
     * Gets the quantity value for this Item.
     * 
     * @return quantity 
     */
    public org.apache.axis.types.UnsignedShort getQuantity() {
        return quantity;
    }


    /**
     * Sets the quantity value for this Item.
     * 
     * @param quantity 
     */
    public void setQuantity(org.apache.axis.types.UnsignedShort quantity) {
        this.quantity = quantity;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Item)) return false;
        Item other = (Item) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.productNumber==null && other.getProductNumber()==null) || 
             (this.productNumber!=null &&
              this.productNumber.equals(other.getProductNumber()))) &&
            ((this.quantity==null && other.getQuantity()==null) || 
             (this.quantity!=null &&
              this.quantity.equals(other.getQuantity())));
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
        if (getProductNumber() != null) {
            _hashCode += getProductNumber().hashCode();
        }
        if (getQuantity() != null) {
            _hashCode += getQuantity().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Item.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/Warehouse.xsd", "Item"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("productNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/Warehouse.xsd", "ProductNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "nonNegativeInteger"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("quantity");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/Warehouse.xsd", "Quantity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "unsignedShort"));
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
