/**
 * LogEventRequestType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package org.apache.axis.wsi.scm.logging;

public class LogEventRequestType  implements java.io.Serializable, org.apache.axis.encoding.AnyContentType {
    private java.lang.String demoUserID;
    private java.lang.String serviceID;
    private java.lang.String eventID;
    private java.lang.String eventDescription;
    private org.apache.axis.message.MessageElement [] _any;

    public LogEventRequestType() {
    }


    /**
     * Gets the demoUserID value for this LogEventRequestType.
     * 
     * @return demoUserID 
     */
    public java.lang.String getDemoUserID() {
        return demoUserID;
    }


    /**
     * Sets the demoUserID value for this LogEventRequestType.
     * 
     * @param demoUserID 
     */
    public void setDemoUserID(java.lang.String demoUserID) {
        this.demoUserID = demoUserID;
    }


    /**
     * Gets the serviceID value for this LogEventRequestType.
     * 
     * @return serviceID 
     */
    public java.lang.String getServiceID() {
        return serviceID;
    }


    /**
     * Sets the serviceID value for this LogEventRequestType.
     * 
     * @param serviceID 
     */
    public void setServiceID(java.lang.String serviceID) {
        this.serviceID = serviceID;
    }


    /**
     * Gets the eventID value for this LogEventRequestType.
     * 
     * @return eventID 
     */
    public java.lang.String getEventID() {
        return eventID;
    }


    /**
     * Sets the eventID value for this LogEventRequestType.
     * 
     * @param eventID 
     */
    public void setEventID(java.lang.String eventID) {
        this.eventID = eventID;
    }


    /**
     * Gets the eventDescription value for this LogEventRequestType.
     * 
     * @return eventDescription 
     */
    public java.lang.String getEventDescription() {
        return eventDescription;
    }


    /**
     * Sets the eventDescription value for this LogEventRequestType.
     * 
     * @param eventDescription 
     */
    public void setEventDescription(java.lang.String eventDescription) {
        this.eventDescription = eventDescription;
    }


    /**
     * Gets the _any value for this LogEventRequestType.
     * 
     * @return _any 
     */
    public org.apache.axis.message.MessageElement [] get_any() {
        return _any;
    }


    /**
     * Sets the _any value for this LogEventRequestType.
     * 
     * @param _any 
     */
    public void set_any(org.apache.axis.message.MessageElement [] _any) {
        this._any = _any;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof LogEventRequestType)) return false;
        LogEventRequestType other = (LogEventRequestType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.demoUserID==null && other.getDemoUserID()==null) || 
             (this.demoUserID!=null &&
              this.demoUserID.equals(other.getDemoUserID()))) &&
            ((this.serviceID==null && other.getServiceID()==null) || 
             (this.serviceID!=null &&
              this.serviceID.equals(other.getServiceID()))) &&
            ((this.eventID==null && other.getEventID()==null) || 
             (this.eventID!=null &&
              this.eventID.equals(other.getEventID()))) &&
            ((this.eventDescription==null && other.getEventDescription()==null) || 
             (this.eventDescription!=null &&
              this.eventDescription.equals(other.getEventDescription()))) &&
            ((this._any==null && other.get_any()==null) || 
             (this._any!=null &&
              java.util.Arrays.equals(this._any, other.get_any())));
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
        if (getDemoUserID() != null) {
            _hashCode += getDemoUserID().hashCode();
        }
        if (getServiceID() != null) {
            _hashCode += getServiceID().hashCode();
        }
        if (getEventID() != null) {
            _hashCode += getEventID().hashCode();
        }
        if (getEventDescription() != null) {
            _hashCode += getEventDescription().hashCode();
        }
        if (get_any() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(get_any());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(get_any(), i);
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
        new org.apache.axis.description.TypeDesc(LogEventRequestType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/LoggingFacility.xsd", "logEventRequestType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("demoUserID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/LoggingFacility.xsd", "DemoUserID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/LoggingFacility.xsd", "ServiceID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eventID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/LoggingFacility.xsd", "EventID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eventDescription");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/LoggingFacility.xsd", "EventDescription"));
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
