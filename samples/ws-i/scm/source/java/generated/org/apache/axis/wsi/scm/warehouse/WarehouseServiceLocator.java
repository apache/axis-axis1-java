/**
 * WarehouseServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package org.apache.axis.wsi.scm.warehouse;

public class WarehouseServiceLocator extends org.apache.axis.client.Service implements org.apache.axis.wsi.scm.warehouse.WarehouseService {

    // Use to get a proxy class for WarehouseBPort
    private java.lang.String WarehouseBPort_address = "http://localhost:8080/axis/services/WarehouseBPort";

    public java.lang.String getWarehouseBPortAddress() {
        return WarehouseBPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String WarehouseBPortWSDDServiceName = "WarehouseBPort";

    public java.lang.String getWarehouseBPortWSDDServiceName() {
        return WarehouseBPortWSDDServiceName;
    }

    public void setWarehouseBPortWSDDServiceName(java.lang.String name) {
        WarehouseBPortWSDDServiceName = name;
    }

    public org.apache.axis.wsi.scm.warehouse.WarehouseShipmentsPortType getWarehouseBPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(WarehouseBPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getWarehouseBPort(endpoint);
    }

    public org.apache.axis.wsi.scm.warehouse.WarehouseShipmentsPortType getWarehouseBPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.apache.axis.wsi.scm.warehouse.WarehouseSoapBindingStub _stub = new org.apache.axis.wsi.scm.warehouse.WarehouseSoapBindingStub(portAddress, this);
            _stub.setPortName(getWarehouseBPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setWarehouseBPortEndpointAddress(java.lang.String address) {
        WarehouseBPort_address = address;
    }


    // Use to get a proxy class for WarehouseCPort
    private java.lang.String WarehouseCPort_address = "http://localhost:8080/axis/services/WarehouseCPort";

    public java.lang.String getWarehouseCPortAddress() {
        return WarehouseCPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String WarehouseCPortWSDDServiceName = "WarehouseCPort";

    public java.lang.String getWarehouseCPortWSDDServiceName() {
        return WarehouseCPortWSDDServiceName;
    }

    public void setWarehouseCPortWSDDServiceName(java.lang.String name) {
        WarehouseCPortWSDDServiceName = name;
    }

    public org.apache.axis.wsi.scm.warehouse.WarehouseShipmentsPortType getWarehouseCPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(WarehouseCPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getWarehouseCPort(endpoint);
    }

    public org.apache.axis.wsi.scm.warehouse.WarehouseShipmentsPortType getWarehouseCPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.apache.axis.wsi.scm.warehouse.WarehouseSoapBindingStub _stub = new org.apache.axis.wsi.scm.warehouse.WarehouseSoapBindingStub(portAddress, this);
            _stub.setPortName(getWarehouseCPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setWarehouseCPortEndpointAddress(java.lang.String address) {
        WarehouseCPort_address = address;
    }


    // Use to get a proxy class for WarehouseAPort
    private java.lang.String WarehouseAPort_address = "http://localhost:8080/axis/services/WarehouseAPort";

    public java.lang.String getWarehouseAPortAddress() {
        return WarehouseAPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String WarehouseAPortWSDDServiceName = "WarehouseAPort";

    public java.lang.String getWarehouseAPortWSDDServiceName() {
        return WarehouseAPortWSDDServiceName;
    }

    public void setWarehouseAPortWSDDServiceName(java.lang.String name) {
        WarehouseAPortWSDDServiceName = name;
    }

    public org.apache.axis.wsi.scm.warehouse.WarehouseShipmentsPortType getWarehouseAPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(WarehouseAPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getWarehouseAPort(endpoint);
    }

    public org.apache.axis.wsi.scm.warehouse.WarehouseShipmentsPortType getWarehouseAPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.apache.axis.wsi.scm.warehouse.WarehouseSoapBindingStub _stub = new org.apache.axis.wsi.scm.warehouse.WarehouseSoapBindingStub(portAddress, this);
            _stub.setPortName(getWarehouseAPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setWarehouseAPortEndpointAddress(java.lang.String address) {
        WarehouseAPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     * This service has multiple ports for a given interface;
     * the proxy implementation returned may be indeterminate.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.apache.axis.wsi.scm.warehouse.WarehouseShipmentsPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                org.apache.axis.wsi.scm.warehouse.WarehouseSoapBindingStub _stub = new org.apache.axis.wsi.scm.warehouse.WarehouseSoapBindingStub(new java.net.URL(WarehouseBPort_address), this);
                _stub.setPortName(getWarehouseBPortWSDDServiceName());
                return _stub;
            }
            if (org.apache.axis.wsi.scm.warehouse.WarehouseShipmentsPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                org.apache.axis.wsi.scm.warehouse.WarehouseSoapBindingStub _stub = new org.apache.axis.wsi.scm.warehouse.WarehouseSoapBindingStub(new java.net.URL(WarehouseCPort_address), this);
                _stub.setPortName(getWarehouseCPortWSDDServiceName());
                return _stub;
            }
            if (org.apache.axis.wsi.scm.warehouse.WarehouseShipmentsPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                org.apache.axis.wsi.scm.warehouse.WarehouseSoapBindingStub _stub = new org.apache.axis.wsi.scm.warehouse.WarehouseSoapBindingStub(new java.net.URL(WarehouseAPort_address), this);
                _stub.setPortName(getWarehouseAPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        String inputPortName = portName.getLocalPart();
        if ("WarehouseBPort".equals(inputPortName)) {
            return getWarehouseBPort();
        }
        else if ("WarehouseCPort".equals(inputPortName)) {
            return getWarehouseCPort();
        }
        else if ("WarehouseAPort".equals(inputPortName)) {
            return getWarehouseAPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/Warehouse.wsdl", "WarehouseService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("WarehouseBPort"));
            ports.add(new javax.xml.namespace.QName("WarehouseCPort"));
            ports.add(new javax.xml.namespace.QName("WarehouseAPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        if ("WarehouseBPort".equals(portName)) {
            setWarehouseBPortEndpointAddress(address);
        }
        if ("WarehouseCPort".equals(portName)) {
            setWarehouseCPortEndpointAddress(address);
        }
        if ("WarehouseAPort".equals(portName)) {
            setWarehouseAPortEndpointAddress(address);
        }
        else { // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
