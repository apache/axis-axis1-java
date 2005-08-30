/*
 * Copyright 2003,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.axis.management;

import org.apache.axis.AxisFault;
import org.apache.axis.ConfigurationException;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.WSDDEngineConfiguration;
import org.apache.axis.deployment.wsdd.WSDDGlobalConfiguration;
import org.apache.axis.deployment.wsdd.WSDDHandler;
import org.apache.axis.deployment.wsdd.WSDDService;
import org.apache.axis.deployment.wsdd.WSDDTransport;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.management.jmx.DeploymentAdministrator;
import org.apache.axis.management.jmx.DeploymentQuery;
import org.apache.axis.management.jmx.ServiceAdministrator;
import org.apache.axis.server.AxisServer;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * The ServiceControl Object is responsible for starting and
 * stopping specific services
 *
 * @author bdillon
 * @version 1.0
 */
public class ServiceAdmin {
    //Singleton AxisServer for Management
            static private AxisServer axisServer = null;

    /**
     * Start the Service
     *
     * @param serviceName
     * @throws AxisFault ConfigurationException
     */
    static public void startService(String serviceName)
            throws AxisFault, ConfigurationException {
        AxisServer server = getEngine();
        try {
            SOAPService service = server.getConfig().getService(
                    new QName("", serviceName));
            service.start();
        } catch (ConfigurationException configException) {
            if (configException.getContainedException() instanceof AxisFault) {
                throw (AxisFault) configException.getContainedException();
            } else {
                throw configException;
            }
        }
    }

    /**
     * Stop the Service
     *
     * @param serviceName
     * @throws AxisFault ConfigurationException
     */
    static public void stopService(String serviceName)
            throws AxisFault, ConfigurationException {
        AxisServer server = getEngine();
        try {
            SOAPService service = server.getConfig().getService(
                    new QName("", serviceName));
            service.stop();
        } catch (ConfigurationException configException) {
            if (configException.getContainedException() instanceof AxisFault) {
                throw (AxisFault) configException.getContainedException();//Throw Axis fault if ist. of
            } else {
                throw configException;
            }
        }
    }

    /**
     * List all registered services
     *
     * @return Map of Services (SOAPService objects, Key is the ServiceName)
     * @throws AxisFault ConfigurationException
     */
    static public String[] listServices()
            throws AxisFault, ConfigurationException {
        ArrayList list = new ArrayList();
        AxisServer server = getEngine();
        Iterator iter; // get list of ServiceDesc objects
        try {
            iter = server.getConfig().getDeployedServices();
        } catch (ConfigurationException configException) {
            if (configException.getContainedException() instanceof AxisFault) {
                throw (AxisFault) configException.getContainedException();//Throw Axis fault if inst. of
            } else {
                throw configException;
            }
        }
        while (iter.hasNext()) {
            ServiceDesc sd = (ServiceDesc) iter.next();
            String name = sd.getName();
            list.add(name);
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * Get the singleton engine for this management object
     *
     * @return
     * @throws AxisFault
     */
    static public AxisServer getEngine() throws AxisFault {
        if (axisServer == null) {
            //Throw a could not get AxisEngine Exception
            throw new AxisFault(
                    "Unable to locate AxisEngine for ServiceAdmin Object");
        }
        return axisServer;
    }

    /**
     * Set the singleton engine
     *
     * @param axisSrv
     */
    static public void setEngine(AxisServer axisSrv, String name) {
        ServiceAdmin.axisServer = axisSrv;
        Registrar.register(new ServiceAdministrator(), "axis:type=server", "ServiceAdministrator");
        Registrar.register(new DeploymentAdministrator(), "axis:type=deploy", "DeploymentAdministrator");
        Registrar.register(new DeploymentQuery(), "axis:type=query", "DeploymentQuery");
    }

    static public void start() {
        if (axisServer != null) {
            axisServer.start();
        }
    }

    static public void stop() {
        if (axisServer != null) {
            axisServer.stop();
        }
    }

    static public void restart() {
        if (axisServer != null) {
            axisServer.stop();
            axisServer.start();
        }
    }

    static public void saveConfiguration() {
        if (axisServer != null) {
            axisServer.saveConfiguration();
        }
    }

    static private WSDDEngineConfiguration getWSDDEngineConfiguration() {
        if (axisServer != null) {
            EngineConfiguration config = axisServer.getConfig();
            if (config instanceof WSDDEngineConfiguration) {
                return (WSDDEngineConfiguration) config;
            } else {
                throw new RuntimeException("WSDDDeploymentHelper.getWSDDEngineConfiguration(): EngineConguration not of type WSDDEngineConfiguration");
            }
        }
        return null;
    }

    static public void setGlobalConfig(WSDDGlobalConfiguration globalConfig) {
        getWSDDEngineConfiguration().getDeployment().setGlobalConfiguration(globalConfig);
    }

    static public WSDDGlobalConfiguration getGlobalConfig() {
        return getWSDDEngineConfiguration().getDeployment().getGlobalConfiguration();
    }

    static public WSDDHandler getHandler(QName qname) {
        return getWSDDEngineConfiguration().getDeployment().getWSDDHandler(qname);
    }

    static public WSDDHandler[] getHandlers() {
        return getWSDDEngineConfiguration().getDeployment().getHandlers();
    }

    static public WSDDService getService(QName qname) {
        return getWSDDEngineConfiguration().getDeployment().getWSDDService(qname);
    }

    static public WSDDService[] getServices() {
        return getWSDDEngineConfiguration().getDeployment().getServices();
    }

    static public WSDDTransport getTransport(QName qname) {
        return getWSDDEngineConfiguration().getDeployment().getWSDDTransport(qname);
    }

    static public WSDDTransport[] getTransports() {
        return getWSDDEngineConfiguration().getDeployment().getTransports();
    }

    static public void deployHandler(WSDDHandler handler) {
        getWSDDEngineConfiguration().getDeployment().deployHandler(handler);
    }

    static public void deployService(WSDDService service) {
        getWSDDEngineConfiguration().getDeployment().deployService(service);
    }

    static public void deployTransport(WSDDTransport transport) {
        getWSDDEngineConfiguration().getDeployment().deployTransport(transport);
    }

    static public void undeployHandler(QName qname) {
        getWSDDEngineConfiguration().getDeployment().undeployHandler(qname);
    }

    static public void undeployService(QName qname) {
        getWSDDEngineConfiguration().getDeployment().undeployService(qname);
    }

    static public void undeployTransport(QName qname) {
        getWSDDEngineConfiguration().getDeployment().undeployTransport(qname);
    }
}