/**
 * ManufacturerPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package org.apache.axis.wsi.scm.manufacturer;

public interface ManufacturerPortType extends java.rmi.Remote {

    // Submit a purchase order for specified items to the manufacturer.
    public boolean submitPO(org.apache.axis.wsi.scm.manufacturer.po.PurchOrdType purchaseOrder, org.apache.axis.wsi.scm.configuration.ConfigurationType configurationHeader, org.apache.axis.wsi.scm.manufacturer.callback.StartHeaderType startHeader) throws java.rmi.RemoteException, org.apache.axis.wsi.scm.configuration.ConfigurationFaultType, org.apache.axis.wsi.scm.manufacturer.po.SubmitPOFaultType;
}
