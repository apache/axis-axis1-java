/**
 * WarehouseCallbackPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package org.apache.axis.wsi.scm.manufacturer;

public interface WarehouseCallbackPortType extends java.rmi.Remote {

    // Submit a shipment notice for specified items to the retailer.
    public boolean submitSN(org.apache.axis.wsi.scm.manufacturer.sn.ShipmentNoticeType shipmentNotice, org.apache.axis.wsi.scm.configuration.ConfigurationType configurationHeader, org.apache.axis.wsi.scm.manufacturer.callback.CallbackHeaderType callbackHeader) throws java.rmi.RemoteException, org.apache.axis.wsi.scm.configuration.ConfigurationFaultType, org.apache.axis.wsi.scm.manufacturer.callback.CallbackFaultType;

    // Notify warehouse  there was an error in processing a submitted PO.
    public boolean errorPO(org.apache.axis.wsi.scm.manufacturer.po.SubmitPOFaultType processPOFault, org.apache.axis.wsi.scm.configuration.ConfigurationType configurationHeader, org.apache.axis.wsi.scm.manufacturer.callback.CallbackHeaderType callbackHeader) throws java.rmi.RemoteException, org.apache.axis.wsi.scm.configuration.ConfigurationFaultType, org.apache.axis.wsi.scm.manufacturer.callback.CallbackFaultType;
}
