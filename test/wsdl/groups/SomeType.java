/**
 * SomeType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC1 Oct 20, 2004 (05:49:44 EDT) WSDL2Java emitter.
 */

package com.epok.auth;

public class SomeType  implements java.io.Serializable {
    private java.lang.String z;
    private java.lang.String a;
    private java.lang.String b;

    public SomeType() {
    }

    public SomeType(
           java.lang.String z,
           java.lang.String a,
           java.lang.String b) {
           this.z = z;
           this.a = a;
           this.b = b;
    }


    /**
     * Gets the z value for this SomeType.
     * 
     * @return z
     */
    public java.lang.String getZ() {
        return z;
    }


    /**
     * Sets the z value for this SomeType.
     * 
     * @param z
     */
    public void setZ(java.lang.String z) {
        this.z = z;
    }


    /**
     * Gets the a value for this SomeType.
     * 
     * @return a
     */
    public java.lang.String getA() {
        return a;
    }


    /**
     * Sets the a value for this SomeType.
     * 
     * @param a
     */
    public void setA(java.lang.String a) {
        this.a = a;
    }


    /**
     * Gets the b value for this SomeType.
     * 
     * @return b
     */
    public java.lang.String getB() {
        return b;
    }


    /**
     * Sets the b value for this SomeType.
     * 
     * @param b
     */
    public void setB(java.lang.String b) {
        this.b = b;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SomeType)) return false;
        SomeType other = (SomeType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.z==null && other.getZ()==null) || 
             (this.z!=null &&
              this.z.equals(other.getZ()))) &&
            ((this.a==null && other.getA()==null) || 
             (this.a!=null &&
              this.a.equals(other.getA()))) &&
            ((this.b==null && other.getB()==null) || 
             (this.b!=null &&
              this.b.equals(other.getB())));
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
        if (getZ() != null) {
            _hashCode += getZ().hashCode();
        }
        if (getA() != null) {
            _hashCode += getA().hashCode();
        }
        if (getB() != null) {
            _hashCode += getB().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SomeType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:auth:epok:com", "SomeType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("z");
        elemField.setXmlName(new javax.xml.namespace.QName("", "z"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("a");
        elemField.setXmlName(new javax.xml.namespace.QName("", "a"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("b");
        elemField.setXmlName(new javax.xml.namespace.QName("", "b"));
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

}
