/**
 * ManufacturerServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package org.apache.axis.wsi.scm.manufacturer;

public class ManufacturerServiceLocator extends org.apache.axis.client.Service implements org.apache.axis.wsi.scm.manufacturer.ManufacturerService {

    // Use to get a proxy class for ManufacturerCPort
    private java.lang.String ManufacturerCPort_address = "http://localhost:8080/axis/services/ManufacturerCPort";

    public java.lang.String getManufacturerCPortAddress() {
        return ManufacturerCPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ManufacturerCPortWSDDServiceName = "ManufacturerCPort";

    public java.lang.String getManufacturerCPortWSDDServiceName() {
        return ManufacturerCPortWSDDServiceName;
    }

    public void setManufacturerCPortWSDDServiceName(java.lang.String name) {
        ManufacturerCPortWSDDServiceName = name;
    }

    public org.apache.axis.wsi.scm.manufacturer.ManufacturerPortType getManufacturerCPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ManufacturerCPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getManufacturerCPort(endpoint);
    }

    public org.apache.axis.wsi.scm.manufacturer.ManufacturerPortType getManufacturerCPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.apache.axis.wsi.scm.manufacturer.ManufacturerSoapBindingStub _stub = new org.apache.axis.wsi.scm.manufacturer.ManufacturerSoapBindingStub(portAddress, this);
            _stub.setPortName(getManufacturerCPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setManufacturerCPortEndpointAddress(java.lang.String address) {
        ManufacturerCPort_address = address;
    }


    // Use to get a proxy class for ManufacturerBPort
    private java.lang.String ManufacturerBPort_address = "http://localhost:8080/axis/services/ManufacturerBPort";

    public java.lang.String getManufacturerBPortAddress() {
        return ManufacturerBPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ManufacturerBPortWSDDServiceName = "ManufacturerBPort";

    public java.lang.String getManufacturerBPortWSDDServiceName() {
        return ManufacturerBPortWSDDServiceName;
    }

    public void setManufacturerBPortWSDDServiceName(java.lang.String name) {
        ManufacturerBPortWSDDServiceName = name;
    }

    public org.apache.axis.wsi.scm.manufacturer.ManufacturerPortType getManufacturerBPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ManufacturerBPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getManufacturerBPort(endpoint);
    }

    public org.apache.axis.wsi.scm.manufacturer.ManufacturerPortType getManufacturerBPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.apache.axis.wsi.scm.manufacturer.ManufacturerSoapBindingStub _stub = new org.apache.axis.wsi.scm.manufacturer.ManufacturerSoapBindingStub(portAddress, this);
            _stub.setPortName(getManufacturerBPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setManufacturerBPortEndpointAddress(java.lang.String address) {
        ManufacturerBPort_address = address;
    }


    // Use to get a proxy class for ManufacturerAPort
    private java.lang.String ManufacturerAPort_address = "http://localhost:8080/axis/services/ManufacturerAPort";

    public java.lang.String getManufacturerAPortAddress() {
        return ManufacturerAPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ManufacturerAPortWSDDServiceName = "ManufacturerAPort";

    public java.lang.String getManufacturerAPortWSDDServiceName() {
        return ManufacturerAPortWSDDServiceName;
    }

    public void setManufacturerAPortWSDDServiceName(java.lang.String name) {
        ManufacturerAPortWSDDServiceName = name;
    }

    public org.apache.axis.wsi.scm.manufacturer.ManufacturerPortType getManufacturerAPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ManufacturerAPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getManufacturerAPort(endpoint);
    }

    public org.apache.axis.wsi.scm.manufacturer.ManufacturerPortType getManufacturerAPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.apache.axis.wsi.scm.manufacturer.ManufacturerSoapBindingStub _stub = new org.apache.axis.wsi.scm.manufacturer.ManufacturerSoapBindingStub(portAddress, this);
            _stub.setPortName(getManufacturerAPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setManufacturerAPortEndpointAddress(java.lang.String address) {
        ManufacturerAPort_address = address;
    }


    // Use to get a proxy class for WarehouseCallbackPort
    private java.lang.String WarehouseCallbackPort_address = "http://localhost:8080/axis/services/WarehouseCallbackPort";

    public java.lang.String getWarehouseCallbackPortAddress() {
        return WarehouseCallbackPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String WarehouseCallbackPortWSDDServiceName = "WarehouseCallbackPort";

    public java.lang.String getWarehouseCallbackPortWSDDServiceName() {
        return WarehouseCallbackPortWSDDServiceName;
    }

    public void setWarehouseCallbackPortWSDDServiceName(java.lang.String name) {
        WarehouseCallbackPortWSDDServiceName = name;
    }

    public org.apache.axis.wsi.scm.manufacturer.WarehouseCallbackPortType getWarehouseCallbackPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(WarehouseCallbackPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getWarehouseCallbackPort(endpoint);
    }

    public org.apache.axis.wsi.scm.manufacturer.WarehouseCallbackPortType getWarehouseCallbackPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.apache.axis.wsi.scm.manufacturer.WarehouseCallbackSoapBindingStub _stub = new org.apache.axis.wsi.scm.manufacturer.WarehouseCallbackSoapBindingStub(portAddress, this);
            _stub.setPortName(getWarehouseCallbackPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setWarehouseCallbackPortEndpointAddress(java.lang.String address) {
        WarehouseCallbackPort_address = address;
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
            if (org.apache.axis.wsi.scm.manufacturer.ManufacturerPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                org.apache.axis.wsi.scm.manufacturer.ManufacturerSoapBindingStub _stub = new org.apache.axis.wsi.scm.manufacturer.ManufacturerSoapBindingStub(new java.net.URL(ManufacturerCPort_address), this);
                _stub.setPortName(getManufacturerCPortWSDDServiceName());
                return _stub;
            }
            if (org.apache.axis.wsi.scm.manufacturer.ManufacturerPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                org.apache.axis.wsi.scm.manufacturer.ManufacturerSoapBindingStub _stub = new org.apache.axis.wsi.scm.manufacturer.ManufacturerSoapBindingStub(new java.net.URL(ManufacturerBPort_address), this);
                _stub.setPortName(getManufacturerBPortWSDDServiceName());
                return _stub;
            }
            if (org.apache.axis.wsi.scm.manufacturer.ManufacturerPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                org.apache.axis.wsi.scm.manufacturer.ManufacturerSoapBindingStub _stub = new org.apache.axis.wsi.scm.manufacturer.ManufacturerSoapBindingStub(new java.net.URL(ManufacturerAPort_address), this);
                _stub.setPortName(getManufacturerAPortWSDDServiceName());
                return _stub;
            }
            if (org.apache.axis.wsi.scm.manufacturer.WarehouseCallbackPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                org.apache.axis.wsi.scm.manufacturer.WarehouseCallbackSoapBindingStub _stub = new org.apache.axis.wsi.scm.manufacturer.WarehouseCallbackSoapBindingStub(new java.net.URL(WarehouseCallbackPort_address), this);
                _stub.setPortName(getWarehouseCallbackPortWSDDServiceName());
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
        if ("ManufacturerCPort".equals(inputPortName)) {
            return getManufacturerCPort();
        }
        else if ("ManufacturerBPort".equals(inputPortName)) {
            return getManufacturerBPort();
        }
        else if ("ManufacturerAPort".equals(inputPortName)) {
            return getManufacturerAPort();
        }
        else if ("WarehouseCallbackPort".equals(inputPortName)) {
            return getWarehouseCallbackPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-10/Manufacturer.wsdl", "ManufacturerService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("ManufacturerCPort"));
            ports.add(new javax.xml.namespace.QName("ManufacturerBPort"));
            ports.add(new javax.xml.namespace.QName("ManufacturerAPort"));
            ports.add(new javax.xml.namespace.QName("WarehouseCallbackPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        if ("ManufacturerCPort".equals(portName)) {
            setManufacturerCPortEndpointAddress(address);
        }
        if ("ManufacturerBPort".equals(portName)) {
            setManufacturerBPortEndpointAddress(address);
        }
        if ("ManufacturerAPort".equals(portName)) {
            setManufacturerAPortEndpointAddress(address);
        }
        if ("WarehouseCallbackPort".equals(portName)) {
            setWarehouseCallbackPortEndpointAddress(address);
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
