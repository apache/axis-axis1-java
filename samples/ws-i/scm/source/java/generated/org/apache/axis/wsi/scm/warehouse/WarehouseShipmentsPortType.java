/**
 * WarehouseShipmentsPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package org.apache.axis.wsi.scm.warehouse;

public interface WarehouseShipmentsPortType extends java.rmi.Remote {

    // Ship the specified number of the specified part to the specified customer.
    public org.apache.axis.wsi.scm.warehouse.ItemShippingStatusList shipGoods(org.apache.axis.wsi.scm.warehouse.ItemList itemList, org.apache.axis.wsi.scm.warehouse.CustomerReferenceType customer, org.apache.axis.wsi.scm.configuration.ConfigurationType configurationHeader) throws java.rmi.RemoteException, org.apache.axis.wsi.scm.configuration.ConfigurationFaultType;
}
