/**
 * ConfiguratorFailedFault.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package org.apache.axis.wsi.scm.configurator;

public class ConfiguratorFailedFault extends org.apache.axis.AxisFault {
    public java.lang.String configError;
    public java.lang.String getConfigError() {
        return this.configError;
    }

    public ConfiguratorFailedFault() {
    }

      public ConfiguratorFailedFault(java.lang.String configError) {
        this.configError = configError;
    }

    /**
     * Writes the exception data to the faultDetails
     */
    public void writeDetails(javax.xml.namespace.QName qname, org.apache.axis.encoding.SerializationContext context) throws java.io.IOException {
        context.serialize(qname, null, configError);
    }
}
