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

public interface DeploymentQueryMBean {
    /**
     * get the global configuration
     *
     * @return
     */
    public WSDDGlobalConfiguration findGlobalConfig();

    /**
     * find the handler
     *
     * @param qname
     * @return
     */
    public WSDDHandler findHandler(String qname);

    /**
     * return all handlers
     *
     * @return
     */
    public WSDDHandler[] findHandlers();

    /**
     * find the service
     *
     * @param qname
     * @return
     */
    public WSDDService findService(String qname);

    /**
     * return all services
     *
     * @return
     */
    public WSDDService[] findServices();

    /**
     * find the transport
     *
     * @param qname
     * @return
     */
    public WSDDTransport findTransport(String qname);

    /**
     * return all transports
     *
     * @return
     */
    public WSDDTransport[] findTransports();

    /**
     * List all registered services
     *
     * @return string array
     * @throws org.apache.axis.AxisFault ConfigurationException
     */
    public String[] listServices() throws AxisFault, ConfigurationException;
}
