/**
 * ConfiguratorServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package org.apache.axis.wsi.scm.configurator;

public class ConfiguratorServiceLocator extends org.apache.axis.client.Service implements org.apache.axis.wsi.scm.configurator.ConfiguratorService {

    // Use to get a proxy class for ConfiguratorPort
    private java.lang.String ConfiguratorPort_address = "http://localhost:8080/axis/services/ConfiguratorPort";

    public java.lang.String getConfiguratorPortAddress() {
        return ConfiguratorPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ConfiguratorPortWSDDServiceName = "ConfiguratorPort";

    public java.lang.String getConfiguratorPortWSDDServiceName() {
        return ConfiguratorPortWSDDServiceName;
    }

    public void setConfiguratorPortWSDDServiceName(java.lang.String name) {
        ConfiguratorPortWSDDServiceName = name;
    }

    public org.apache.axis.wsi.scm.configurator.ConfiguratorPortType getConfiguratorPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ConfiguratorPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getConfiguratorPort(endpoint);
    }

    public org.apache.axis.wsi.scm.configurator.ConfiguratorPortType getConfiguratorPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.apache.axis.wsi.scm.configurator.ConfiguratorBindingStub _stub = new org.apache.axis.wsi.scm.configurator.ConfiguratorBindingStub(portAddress, this);
            _stub.setPortName(getConfiguratorPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setConfiguratorPortEndpointAddress(java.lang.String address) {
        ConfiguratorPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.apache.axis.wsi.scm.configurator.ConfiguratorPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                org.apache.axis.wsi.scm.configurator.ConfiguratorBindingStub _stub = new org.apache.axis.wsi.scm.configurator.ConfiguratorBindingStub(new java.net.URL(ConfiguratorPort_address), this);
                _stub.setPortName(getConfiguratorPortWSDDServiceName());
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
        if ("ConfiguratorPort".equals(inputPortName)) {
            return getConfiguratorPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.ws-i.org/SampleApplications/SupplyChainManagement/2002-08/Configurator.wsdl", "ConfiguratorService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("ConfiguratorPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        if ("ConfiguratorPort".equals(portName)) {
            setConfiguratorPortEndpointAddress(address);
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
