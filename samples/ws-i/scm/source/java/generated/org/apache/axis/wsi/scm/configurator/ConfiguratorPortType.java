/**
 * ConfiguratorPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package org.apache.axis.wsi.scm.configurator;

public interface ConfiguratorPortType extends java.rmi.Remote {
    public org.apache.axis.wsi.scm.configurator.ConfigOptionsType getConfigurationOptions(boolean refresh) throws java.rmi.RemoteException, org.apache.axis.wsi.scm.configurator.ConfiguratorFailedFault;
}
