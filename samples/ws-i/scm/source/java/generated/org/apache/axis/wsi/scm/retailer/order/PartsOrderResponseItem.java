/**
 * PartsOrderResponseItem.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package org.apache.axis.wsi.scm.retailer.order;

public class PartsOrderResponseItem  implements java.io.Serializable {
    private org.apache.axis.wsi.scm.retailer.order.ProductNumber productNumber;
    private org.apache.axis.types.NonNegativeInteger quantity;
    private java.math.BigDecimal price;
    private java.lang.String comment;

    public PartsOrderResponseItem() {
    }


    /**
     * Gets the productNumber value for this PartsOrderResponseItem.
     * 
     * @return productNumber 
     */
    public org.apache.axis.wsi.scm.retailer.order.ProductNumber getProductNumber() {
        return productNumber;
    }


    /**
     * Sets the productNumber value for this PartsOrderResponseItem.
     * 
     * @param productNumber 
     */
    public void setProductNumber(org.apache.axis.wsi.scm.retailer.order.ProductNumber productNumber) {
        this.productNumber = productNumber;
    }


    /**
     * Gets the quantity value for this PartsOrderResponseItem.
     * 
     * @return quantity 
     */
    public org.apache.axis.types.NonNegativeInteger getQuantity() {
        return quantity;
    }


    /**
     * Sets the quantity value for this PartsOrderResponseItem.
     * 
     * @param quantity 
     */
    public void setQuantity(org.apache.axis.types.NonNegativeInteger quantity) {
        this.quantity = quantity;
    }


    /**
     * Gets the price value for this PartsOrderResponseItem.
     * 
     * @return price 
     */
    public java.math.BigDecimal getPrice() {
        return price;
    }


    /**
     * Sets the price value for this PartsOrderResponseItem.
     * 
     * @param price 
     */
    public void setPrice(java.math.BigDecimal price) {
        this.price = price;
    }


    /**
     * Gets the comment value for this PartsOrderResponseItem.
     * 
     * @return comment 
     */
    public java.lang.String getComment() {
        return comment;
    }


    /**
     * Sets the comment value for this PartsOrderResponseItem.
     * 
     * @param comment 
     */
    public void setComment(java.lang.String comment) {
        this.comment = comment;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PartsOrderResponseItem)) return false;
        PartsOrderResponseItem other = (PartsOrderResponseItem) obj;
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
              this.quantity.equals(other.getQuantity()))) &&
            ((this.price==null && other.getPrice()==null) || 
             (this.price!=null &&
              this.price.equals(other.getPrice()))) &&
            ((this.comment==null && other.getComment()==null) || 
             (this.comment!=null &&
              this.comment.equals(other.getComment())));
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
        if (getPrice() != null) {
            _hashCode += getPrice().hashCode();
        }
        if (getComment() != null) {
            _hashCode += getComment().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PartsOrderResponseItem.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/RetailOrder.xsd", "PartsOrderResponseItem"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("productNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/RetailOrder.xsd", "productNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/RetailOrder.xsd", "productNumber"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("quantity");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/RetailOrder.xsd", "quantity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "nonNegativeInteger"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("price");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/RetailOrder.xsd", "price"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("comment");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/RetailOrder.xsd", "comment"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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

}
