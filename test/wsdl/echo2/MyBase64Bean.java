/**
 * MyBase64Bean.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package test.wsdl.echo2;

public class MyBase64Bean  implements java.io.Serializable {
    private java.lang.Byte varXsdByte;
    private java.lang.Byte varSoapByte;
    private byte[] varXsdBase64Binary;
    private byte[] varSoapBase64Binary;
    private byte[] varXsdHexBinary;
    private byte[] varSoapBase64;

    public MyBase64Bean() {
    }

    public MyBase64Bean(
           java.lang.Byte varXsdByte,
           java.lang.Byte varSoapByte,
           byte[] varXsdBase64Binary,
           byte[] varSoapBase64Binary,
           byte[] varXsdHexBinary,
           byte[] varSoapBase64) {
           this.varXsdByte = varXsdByte;
           this.varSoapByte = varSoapByte;
           this.varXsdBase64Binary = varXsdBase64Binary;
           this.varSoapBase64Binary = varSoapBase64Binary;
           this.varXsdHexBinary = varXsdHexBinary;
           this.varSoapBase64 = varSoapBase64;
    }


    /**
     * Gets the varXsdByte value for this MyBase64Bean.
     *
     * @return varXsdByte
     */
    public java.lang.Byte getVarXsdByte() {
        return varXsdByte;
    }


    /**
     * Sets the varXsdByte value for this MyBase64Bean.
     *
     * @param varXsdByte
     */
    public void setVarXsdByte(java.lang.Byte varXsdByte) {
        this.varXsdByte = varXsdByte;
    }


    /**
     * Gets the varSoapByte value for this MyBase64Bean.
     *
     * @return varSoapByte
     */
    public java.lang.Byte getVarSoapByte() {
        return varSoapByte;
    }


    /**
     * Sets the varSoapByte value for this MyBase64Bean.
     *
     * @param varSoapByte
     */
    public void setVarSoapByte(java.lang.Byte varSoapByte) {
        this.varSoapByte = varSoapByte;
    }


    /**
     * Gets the varXsdBase64Binary value for this MyBase64Bean.
     *
     * @return varXsdBase64Binary
     */
    public byte[] getVarXsdBase64Binary() {
        return varXsdBase64Binary;
    }


    /**
     * Sets the varXsdBase64Binary value for this MyBase64Bean.
     *
     * @param varXsdBase64Binary
     */
    public void setVarXsdBase64Binary(byte[] varXsdBase64Binary) {
        this.varXsdBase64Binary = varXsdBase64Binary;
    }


    /**
     * Gets the varSoapBase64Binary value for this MyBase64Bean.
     *
     * @return varSoapBase64Binary
     */
    public byte[] getVarSoapBase64Binary() {
        return varSoapBase64Binary;
    }


    /**
     * Sets the varSoapBase64Binary value for this MyBase64Bean.
     *
     * @param varSoapBase64Binary
     */
    public void setVarSoapBase64Binary(byte[] varSoapBase64Binary) {
        this.varSoapBase64Binary = varSoapBase64Binary;
    }


    /**
     * Gets the varXsdHexBinary value for this MyBase64Bean.
     *
     * @return varXsdHexBinary
     */
    public byte[] getVarXsdHexBinary() {
        return varXsdHexBinary;
    }


    /**
     * Sets the varXsdHexBinary value for this MyBase64Bean.
     *
     * @param varXsdHexBinary
     */
    public void setVarXsdHexBinary(byte[] varXsdHexBinary) {
        this.varXsdHexBinary = varXsdHexBinary;
    }


    /**
     * Gets the varSoapBase64 value for this MyBase64Bean.
     *
     * @return varSoapBase64
     */
    public byte[] getVarSoapBase64() {
        return varSoapBase64;
    }


    /**
     * Sets the varSoapBase64 value for this MyBase64Bean.
     *
     * @param varSoapBase64
     *//*
    public void setVarSoapBase64(int i, byte varSoapBase64) {
        this.varSoapBase64[i] = varSoapBase64;
    }*/

    /**
     * Gets the varSoapBase64 value for this MyBase64Bean.
     *
     * @return varSoapBase64
     *//*
    public byte getVarSoapBase64(int i) {
        return varSoapBase64[i];
    }*/


    /**
     * Sets the varSoapBase64 value for this MyBase64Bean.
     *
     * @param varSoapBase64
     */
    public void setVarSoapBase64(byte[] varSoapBase64) {
        this.varSoapBase64 = varSoapBase64;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MyBase64Bean)) return false;
        MyBase64Bean other = (MyBase64Bean) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.varXsdByte==null && other.getVarXsdByte()==null) ||
             (this.varXsdByte!=null &&
              this.varXsdByte.equals(other.getVarXsdByte()))) &&
            ((this.varSoapByte==null && other.getVarSoapByte()==null) ||
             (this.varSoapByte!=null &&
              this.varSoapByte.equals(other.getVarSoapByte()))) &&
            ((this.varXsdBase64Binary==null && other.getVarXsdBase64Binary()==null) ||
             (this.varXsdBase64Binary!=null &&
              java.util.Arrays.equals(this.varXsdBase64Binary, other.getVarXsdBase64Binary()))) &&
            ((this.varSoapBase64Binary==null && other.getVarSoapBase64Binary()==null) ||
             (this.varSoapBase64Binary!=null &&
              java.util.Arrays.equals(this.varSoapBase64Binary, other.getVarSoapBase64Binary()))) &&
            ((this.varXsdHexBinary==null && other.getVarXsdHexBinary()==null) ||
             (this.varXsdHexBinary!=null &&
              java.util.Arrays.equals(this.varXsdHexBinary, other.getVarXsdHexBinary()))) &&
            ((this.varSoapBase64==null && other.getVarSoapBase64()==null) ||
             (this.varSoapBase64!=null &&
              java.util.Arrays.equals(this.varSoapBase64, other.getVarSoapBase64())));
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
        if (getVarXsdByte() != null) {
            _hashCode += getVarXsdByte().hashCode();
        }
        if (getVarSoapByte() != null) {
            _hashCode += getVarSoapByte().hashCode();
        }
        if (getVarXsdBase64Binary() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getVarXsdBase64Binary());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getVarXsdBase64Binary(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getVarSoapBase64Binary() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getVarSoapBase64Binary());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getVarSoapBase64Binary(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getVarXsdHexBinary() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getVarXsdHexBinary());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getVarXsdHexBinary(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getVarSoapBase64() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getVarSoapBase64());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getVarSoapBase64(), i);
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
        new org.apache.axis.description.TypeDesc(MyBase64Bean.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:echo2.wsdl.test", "MyBase64Bean"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("varXsdByte");
        elemField.setXmlName(new javax.xml.namespace.QName("", "varXsdByte"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "byte"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("varSoapByte");
        elemField.setXmlName(new javax.xml.namespace.QName("", "varSoapByte"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "byte"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("varXsdBase64Binary");
        elemField.setXmlName(new javax.xml.namespace.QName("", "varXsdBase64Binary"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("varSoapBase64Binary");
        elemField.setXmlName(new javax.xml.namespace.QName("", "varSoapBase64Binary"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "base64Binary"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("varXsdHexBinary");
        elemField.setXmlName(new javax.xml.namespace.QName("", "varXsdHexBinary"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "hexBinary"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("varSoapBase64");
        elemField.setXmlName(new javax.xml.namespace.QName("", "varSoapBase64"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "base64"));
        elemField.setNillable(true);
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
