/**
 * ItemShippingStatusList.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package org.apache.axis.wsi.scm.warehouse;

public class ItemShippingStatusList  implements java.io.Serializable {
    private org.apache.axis.wsi.scm.warehouse.ItemShippingStatus[] itemStatus;

    public ItemShippingStatusList() {
    }


    /**
     * Gets the itemStatus value for this ItemShippingStatusList.
     * 
     * @return itemStatus 
     */
    public org.apache.axis.wsi.scm.warehouse.ItemShippingStatus[] getItemStatus() {
        return itemStatus;
    }


    /**
     * Sets the itemStatus value for this ItemShippingStatusList.
     * 
     * @param itemStatus 
     */
    public void setItemStatus(org.apache.axis.wsi.scm.warehouse.ItemShippingStatus[] itemStatus) {
        this.itemStatus = itemStatus;
    }

    public org.apache.axis.wsi.scm.warehouse.ItemShippingStatus getItemStatus(int i) {
        return this.itemStatus[i];
    }

    public void setItemStatus(int i, org.apache.axis.wsi.scm.warehouse.ItemShippingStatus value) {
        this.itemStatus[i] = value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ItemShippingStatusList)) return false;
        ItemShippingStatusList other = (ItemShippingStatusList) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.itemStatus==null && other.getItemStatus()==null) || 
             (this.itemStatus!=null &&
              java.util.Arrays.equals(this.itemStatus, other.getItemStatus())));
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
        if (getItemStatus() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getItemStatus());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getItemStatus(), i);
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
        new org.apache.axis.description.TypeDesc(ItemShippingStatusList.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/Warehouse.xsd", "ItemShippingStatusList"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("itemStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/Warehouse.xsd", "ItemStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/Warehouse.xsd", "ItemShippingStatus"));
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
