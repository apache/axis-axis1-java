/**
 * ConfigurationEndpointRole.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package org.apache.axis.wsi.scm.configuration;

public class ConfigurationEndpointRole implements java.io.Serializable {
    private org.apache.axis.types.NMToken _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected ConfigurationEndpointRole(org.apache.axis.types.NMToken value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final org.apache.axis.types.NMToken _LoggingFacility = new org.apache.axis.types.NMToken("LoggingFacility");
    public static final org.apache.axis.types.NMToken _Retailer = new org.apache.axis.types.NMToken("Retailer");
    public static final org.apache.axis.types.NMToken _WarehouseA = new org.apache.axis.types.NMToken("WarehouseA");
    public static final org.apache.axis.types.NMToken _WarehouseB = new org.apache.axis.types.NMToken("WarehouseB");
    public static final org.apache.axis.types.NMToken _WarehouseC = new org.apache.axis.types.NMToken("WarehouseC");
    public static final org.apache.axis.types.NMToken _ManufacturerA = new org.apache.axis.types.NMToken("ManufacturerA");
    public static final org.apache.axis.types.NMToken _ManufacturerB = new org.apache.axis.types.NMToken("ManufacturerB");
    public static final org.apache.axis.types.NMToken _ManufacturerC = new org.apache.axis.types.NMToken("ManufacturerC");
    public static final ConfigurationEndpointRole LoggingFacility = new ConfigurationEndpointRole(_LoggingFacility);
    public static final ConfigurationEndpointRole Retailer = new ConfigurationEndpointRole(_Retailer);
    public static final ConfigurationEndpointRole WarehouseA = new ConfigurationEndpointRole(_WarehouseA);
    public static final ConfigurationEndpointRole WarehouseB = new ConfigurationEndpointRole(_WarehouseB);
    public static final ConfigurationEndpointRole WarehouseC = new ConfigurationEndpointRole(_WarehouseC);
    public static final ConfigurationEndpointRole ManufacturerA = new ConfigurationEndpointRole(_ManufacturerA);
    public static final ConfigurationEndpointRole ManufacturerB = new ConfigurationEndpointRole(_ManufacturerB);
    public static final ConfigurationEndpointRole ManufacturerC = new ConfigurationEndpointRole(_ManufacturerC);
    public org.apache.axis.types.NMToken getValue() { return _value_;}
    public static ConfigurationEndpointRole fromValue(org.apache.axis.types.NMToken value)
          throws java.lang.IllegalArgumentException {
        ConfigurationEndpointRole enumeration = (ConfigurationEndpointRole)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static ConfigurationEndpointRole fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(ConfigurationEndpointRole.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/Configuration.xsd", "ConfigurationEndpointRole"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
