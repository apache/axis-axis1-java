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
package org.apache.axis.management.jmx;

import org.apache.axis.AxisFault;
import org.apache.axis.ConfigurationException;
import org.apache.axis.deployment.wsdd.WSDDGlobalConfiguration;
import org.apache.axis.deployment.wsdd.WSDDHandler;
import org.apache.axis.deployment.wsdd.WSDDService;
import org.apache.axis.deployment.wsdd.WSDDTransport;
import org.apache.axis.management.ServiceAdmin;

import javax.xml.namespace.QName;

public class DeploymentQuery implements DeploymentQueryMBean {
    /**
     * get the global configuration
     *
     * @return
     */
    public WSDDGlobalConfiguration findGlobalConfig() {
        return ServiceAdmin.getGlobalConfig();
    }

    /**
     * find a specific handler
     *
     * @param qname
     * @return
     */
    public WSDDHandler findHandler(String qname) {
        return ServiceAdmin.getHandler(new QName(qname));
    }

    /**
     * get all handlers
     *
     * @return
     */
    public WSDDHandler[] findHandlers() {
        return ServiceAdmin.getHandlers();
    }

    /**
     * fina a specific service
     *
     * @param qname
     * @return
     */
    public WSDDService findService(String qname) {
        return ServiceAdmin.getService(new QName(qname));
    }

    /**
     * get all services
     *
     * @return
     */
    public WSDDService[] findServices() {
        return ServiceAdmin.getServices();
    }

    /**
     * find a specific transport
     *
     * @param qname
     * @return
     */
    public WSDDTransport findTransport(String qname) {
        return ServiceAdmin.getTransport(new QName(qname));
    }

    /**
     * return all transports
     *
     * @return
     */
    public WSDDTransport[] findTransports() {
        return ServiceAdmin.getTransports();
    }

    /**
     * List all registered services
     *
     * @return Map of Services (SOAPService objects, Key is the ServiceName)
     * @throws AxisFault ConfigurationException
     */
    public String[] listServices()
            throws AxisFault, ConfigurationException {
        return org.apache.axis.management.ServiceAdmin.listServices();
    }
}
