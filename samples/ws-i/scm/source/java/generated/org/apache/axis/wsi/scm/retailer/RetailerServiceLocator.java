/**
 * RetailerServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package org.apache.axis.wsi.scm.retailer;

public class RetailerServiceLocator extends org.apache.axis.client.Service implements org.apache.axis.wsi.scm.retailer.RetailerService {

    // Use to get a proxy class for RetailerPort
    private java.lang.String RetailerPort_address = "http://localhost:8080/axis/services/RetailerPort";

    public java.lang.String getRetailerPortAddress() {
        return RetailerPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String RetailerPortWSDDServiceName = "RetailerPort";

    public java.lang.String getRetailerPortWSDDServiceName() {
        return RetailerPortWSDDServiceName;
    }

    public void setRetailerPortWSDDServiceName(java.lang.String name) {
        RetailerPortWSDDServiceName = name;
    }

    public org.apache.axis.wsi.scm.retailer.RetailerPortType getRetailerPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(RetailerPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getRetailerPort(endpoint);
    }

    public org.apache.axis.wsi.scm.retailer.RetailerPortType getRetailerPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.apache.axis.wsi.scm.retailer.RetailerSoapBindingStub _stub = new org.apache.axis.wsi.scm.retailer.RetailerSoapBindingStub(portAddress, this);
            _stub.setPortName(getRetailerPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setRetailerPortEndpointAddress(java.lang.String address) {
        RetailerPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.apache.axis.wsi.scm.retailer.RetailerPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                org.apache.axis.wsi.scm.retailer.RetailerSoapBindingStub _stub = new org.apache.axis.wsi.scm.retailer.RetailerSoapBindingStub(new java.net.URL(RetailerPort_address), this);
                _stub.setPortName(getRetailerPortWSDDServiceName());
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
        if ("RetailerPort".equals(inputPortName)) {
            return getRetailerPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/Retailer.wsdl", "RetailerService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("RetailerPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        if ("RetailerPort".equals(portName)) {
            setRetailerPortEndpointAddress(address);
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
