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
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.server.AxisServer;

import javax.xml.namespace.QName;
import java.util.HashMap;
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
    static public HashMap listServices()
            throws AxisFault, ConfigurationException {
        AxisServer server = getEngine();
        HashMap serviceMap = new HashMap();
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
            System.out.println("Service Name is : " + name);
            SOAPService service = server.getConfig().getService(
                    new QName("", name));
            serviceMap.put(name, service);
        }
        return serviceMap;
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
     * @param axisSrv
     */ 
    static public void setEngine(AxisServer axisSrv) {
        ServiceAdmin.axisServer = axisSrv;
    }

}