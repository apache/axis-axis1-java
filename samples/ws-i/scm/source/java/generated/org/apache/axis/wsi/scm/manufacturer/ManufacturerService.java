/**
 * ManufacturerService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package org.apache.axis.wsi.scm.manufacturer;

public interface ManufacturerService extends javax.xml.rpc.Service {
    public java.lang.String getManufacturerCPortAddress();

    public org.apache.axis.wsi.scm.manufacturer.ManufacturerPortType getManufacturerCPort() throws javax.xml.rpc.ServiceException;

    public org.apache.axis.wsi.scm.manufacturer.ManufacturerPortType getManufacturerCPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
    public java.lang.String getManufacturerBPortAddress();

    public org.apache.axis.wsi.scm.manufacturer.ManufacturerPortType getManufacturerBPort() throws javax.xml.rpc.ServiceException;

    public org.apache.axis.wsi.scm.manufacturer.ManufacturerPortType getManufacturerBPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
    public java.lang.String getManufacturerAPortAddress();

    public org.apache.axis.wsi.scm.manufacturer.ManufacturerPortType getManufacturerAPort() throws javax.xml.rpc.ServiceException;

    public org.apache.axis.wsi.scm.manufacturer.ManufacturerPortType getManufacturerAPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
    public java.lang.String getWarehouseCallbackPortAddress();

    public org.apache.axis.wsi.scm.manufacturer.WarehouseCallbackPortType getWarehouseCallbackPort() throws javax.xml.rpc.ServiceException;

    public org.apache.axis.wsi.scm.manufacturer.WarehouseCallbackPortType getWarehouseCallbackPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
