/**
 * GetEventsResponseType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package org.apache.axis.wsi.scm.logging;

public class GetEventsResponseType  implements java.io.Serializable {
    private org.apache.axis.wsi.scm.logging._getEventsResponseType_LogEntry[] logEntry;

    public GetEventsResponseType() {
    }


    /**
     * Gets the logEntry value for this GetEventsResponseType.
     * 
     * @return logEntry 
     */
    public org.apache.axis.wsi.scm.logging._getEventsResponseType_LogEntry[] getLogEntry() {
        return logEntry;
    }


    /**
     * Sets the logEntry value for this GetEventsResponseType.
     * 
     * @param logEntry 
     */
    public void setLogEntry(org.apache.axis.wsi.scm.logging._getEventsResponseType_LogEntry[] logEntry) {
        this.logEntry = logEntry;
    }

    public org.apache.axis.wsi.scm.logging._getEventsResponseType_LogEntry getLogEntry(int i) {
        return this.logEntry[i];
    }

    public void setLogEntry(int i, org.apache.axis.wsi.scm.logging._getEventsResponseType_LogEntry value) {
        this.logEntry[i] = value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetEventsResponseType)) return false;
        GetEventsResponseType other = (GetEventsResponseType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.logEntry==null && other.getLogEntry()==null) || 
             (this.logEntry!=null &&
              java.util.Arrays.equals(this.logEntry, other.getLogEntry())));
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
        if (getLogEntry() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getLogEntry());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getLogEntry(), i);
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
        new org.apache.axis.description.TypeDesc(GetEventsResponseType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/LoggingFacility.xsd", "getEventsResponseType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("logEntry");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/LoggingFacility.xsd", "LogEntry"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/LoggingFacility.xsd", ">getEventsResponseType>LogEntry"));
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
