/**
 * RetailerPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package org.apache.axis.wsi.scm.retailer;

public interface RetailerPortType extends java.rmi.Remote {

    // returns a product catalog
    public org.apache.axis.wsi.scm.retailer.catalog.CatalogType getCatalog() throws java.rmi.RemoteException;

    // Accept an order for quantities of multiple products
    public org.apache.axis.wsi.scm.retailer.order.PartsOrderResponseType submitOrder(org.apache.axis.wsi.scm.retailer.order.PartsOrderType partsOrder, org.apache.axis.wsi.scm.retailer.order.CustomerDetailsType customerDetails, org.apache.axis.wsi.scm.configuration.ConfigurationType configurationHeader) throws java.rmi.RemoteException, org.apache.axis.wsi.scm.retailer.order.InvalidProductCodeType, org.apache.axis.wsi.scm.retailer.BadOrderFault, org.apache.axis.wsi.scm.configuration.ConfigurationFaultType;
}
