/**
 * _submitPOFaultType_Reason.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package org.apache.axis.wsi.scm.manufacturer.po;

public class _submitPOFaultType_Reason implements java.io.Serializable {
    private org.apache.axis.types.NMToken _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected _submitPOFaultType_Reason(org.apache.axis.types.NMToken value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final org.apache.axis.types.NMToken _MalformedOrder = new org.apache.axis.types.NMToken("MalformedOrder");
    public static final org.apache.axis.types.NMToken _InvalidProduct = new org.apache.axis.types.NMToken("InvalidProduct");
    public static final org.apache.axis.types.NMToken _InvalidQty = new org.apache.axis.types.NMToken("InvalidQty");
    public static final _submitPOFaultType_Reason MalformedOrder = new _submitPOFaultType_Reason(_MalformedOrder);
    public static final _submitPOFaultType_Reason InvalidProduct = new _submitPOFaultType_Reason(_InvalidProduct);
    public static final _submitPOFaultType_Reason InvalidQty = new _submitPOFaultType_Reason(_InvalidQty);
    public org.apache.axis.types.NMToken getValue() { return _value_;}
    public static _submitPOFaultType_Reason fromValue(org.apache.axis.types.NMToken value)
          throws java.lang.IllegalArgumentException {
        _submitPOFaultType_Reason enumeration = (_submitPOFaultType_Reason)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static _submitPOFaultType_Reason fromString(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        try {
            return fromValue(new org.apache.axis.types.NMToken(value));
        } catch (Exception e) {
            throw new java.lang.IllegalArgumentException();
        }
    }
    public boolean equals(java.lang.Object obj) {return (obj == this);}
    public int hashCode() { return toString().hashCode();}
    public java.lang.String toString() { return _value_.toString();}
    public java.lang.Object readResolve() throws java.io.ObjectStreamException { return fromValue(_value_);}
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumSerializer(
            _javaType, _xmlType);
    }
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumDeserializer(
            _javaType, _xmlType);
    }
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(_submitPOFaultType_Reason.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-10/ManufacturerPO.xsd", ">submitPOFaultType>Reason"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
