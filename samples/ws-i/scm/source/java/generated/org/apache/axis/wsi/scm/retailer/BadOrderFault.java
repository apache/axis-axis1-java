/**
 * BadOrderFault.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package org.apache.axis.wsi.scm.retailer;

public class BadOrderFault extends org.apache.axis.AxisFault {
    public java.lang.String reason;
    public java.lang.String getReason() {
        return this.reason;
    }

    public BadOrderFault() {
    }

      public BadOrderFault(java.lang.String reason) {
        this.reason = reason;
    }

    /**
     * Writes the exception data to the faultDetails
     */
    public void writeDetails(javax.xml.namespace.QName qname, org.apache.axis.encoding.SerializationContext context) throws java.io.IOException {
        context.serialize(qname, null, reason);
    }
}
