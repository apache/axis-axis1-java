/**
 * WarehouseService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package org.apache.axis.wsi.scm.warehouse;

public interface WarehouseService extends javax.xml.rpc.Service {
    public java.lang.String getWarehouseBPortAddress();

    public org.apache.axis.wsi.scm.warehouse.WarehouseShipmentsPortType getWarehouseBPort() throws javax.xml.rpc.ServiceException;

    public org.apache.axis.wsi.scm.warehouse.WarehouseShipmentsPortType getWarehouseBPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
    public java.lang.String getWarehouseCPortAddress();

    public org.apache.axis.wsi.scm.warehouse.WarehouseShipmentsPortType getWarehouseCPort() throws javax.xml.rpc.ServiceException;

    public org.apache.axis.wsi.scm.warehouse.WarehouseShipmentsPortType getWarehouseCPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
    public java.lang.String getWarehouseAPortAddress();

    public org.apache.axis.wsi.scm.warehouse.WarehouseShipmentsPortType getWarehouseAPort() throws javax.xml.rpc.ServiceException;

    public org.apache.axis.wsi.scm.warehouse.WarehouseShipmentsPortType getWarehouseAPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
