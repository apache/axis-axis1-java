/**
 * ShipmentNoticeType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package org.apache.axis.wsi.scm.manufacturer.sn;

public class ShipmentNoticeType  implements java.io.Serializable {
    private java.lang.String shipNum;
    private java.lang.String orderNum;
    private org.apache.axis.wsi.scm.manufacturer.sn.CustomerReferenceType customerRef;
    private org.apache.axis.wsi.scm.manufacturer.sn.ItemList items;
    private float total;

    public ShipmentNoticeType() {
    }


    /**
     * Gets the shipNum value for this ShipmentNoticeType.
     * 
     * @return shipNum 
     */
    public java.lang.String getShipNum() {
        return shipNum;
    }


    /**
     * Sets the shipNum value for this ShipmentNoticeType.
     * 
     * @param shipNum 
     */
    public void setShipNum(java.lang.String shipNum) {
        this.shipNum = shipNum;
    }


    /**
     * Gets the orderNum value for this ShipmentNoticeType.
     * 
     * @return orderNum 
     */
    public java.lang.String getOrderNum() {
        return orderNum;
    }


    /**
     * Sets the orderNum value for this ShipmentNoticeType.
     * 
     * @param orderNum 
     */
    public void setOrderNum(java.lang.String orderNum) {
        this.orderNum = orderNum;
    }


    /**
     * Gets the customerRef value for this ShipmentNoticeType.
     * 
     * @return customerRef 
     */
    public org.apache.axis.wsi.scm.manufacturer.sn.CustomerReferenceType getCustomerRef() {
        return customerRef;
    }


    /**
     * Sets the customerRef value for this ShipmentNoticeType.
     * 
     * @param customerRef 
     */
    public void setCustomerRef(org.apache.axis.wsi.scm.manufacturer.sn.CustomerReferenceType customerRef) {
        this.customerRef = customerRef;
    }


    /**
     * Gets the items value for this ShipmentNoticeType.
     * 
     * @return items 
     */
    public org.apache.axis.wsi.scm.manufacturer.sn.ItemList getItems() {
        return items;
    }


    /**
     * Sets the items value for this ShipmentNoticeType.
     * 
     * @param items 
     */
    public void setItems(org.apache.axis.wsi.scm.manufacturer.sn.ItemList items) {
        this.items = items;
    }


    /**
     * Gets the total value for this ShipmentNoticeType.
     * 
     * @return total 
     */
    public float getTotal() {
        return total;
    }


    /**
     * Sets the total value for this ShipmentNoticeType.
     * 
     * @param total 
     */
    public void setTotal(float total) {
        this.total = total;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ShipmentNoticeType)) return false;
        ShipmentNoticeType other = (ShipmentNoticeType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.shipNum==null && other.getShipNum()==null) || 
             (this.shipNum!=null &&
              this.shipNum.equals(other.getShipNum()))) &&
            ((this.orderNum==null && other.getOrderNum()==null) || 
             (this.orderNum!=null &&
              this.orderNum.equals(other.getOrderNum()))) &&
            ((this.customerRef==null && other.getCustomerRef()==null) || 
             (this.customerRef!=null &&
              this.customerRef.equals(other.getCustomerRef()))) &&
            ((this.items==null && other.getItems()==null) || 
             (this.items!=null &&
              this.items.equals(other.getItems()))) &&
            this.total == other.getTotal();
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
        if (getShipNum() != null) {
            _hashCode += getShipNum().hashCode();
        }
        if (getOrderNum() != null) {
            _hashCode += getOrderNum().hashCode();
        }
        if (getCustomerRef() != null) {
            _hashCode += getCustomerRef().hashCode();
        }
        if (getItems() != null) {
            _hashCode += getItems().hashCode();
        }
        _hashCode += new Float(getTotal()).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ShipmentNoticeType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-10/ManufacturerSN.xsd", "ShipmentNoticeType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shipNum");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-10/ManufacturerSN.xsd", "shipNum"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("orderNum");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-10/ManufacturerSN.xsd", "orderNum"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customerRef");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-10/ManufacturerSN.xsd", "customerRef"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-10/ManufacturerSN.xsd", "CustomerReferenceType"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("items");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-10/ManufacturerSN.xsd", "items"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-10/ManufacturerSN.xsd", "ItemList"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("total");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-10/ManufacturerSN.xsd", "total"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
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
