/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.axis.configuration;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axis.ConfigurationException;
import org.apache.axis.Handler;
import org.apache.axis.WSDDEngineConfiguration;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDGlobalConfiguration;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.utils.Messages;

/**
 * {@link WSDDEngineConfiguration} implementation that delegates to the {@link WSDDDeployment}
 * returned by {@link WSDDEngineConfiguration#getDeployment()}.
 */
public abstract class DelegatingWSDDEngineConfiguration implements WSDDEngineConfiguration {
    /**
     * retrieve an instance of the named handler
     * @param qname XXX
     * @return XXX
     * @throws ConfigurationException XXX
     */
    public final Handler getHandler(QName qname) throws ConfigurationException {
        return getDeployment().getHandler(qname);
    }

    /**
     * retrieve an instance of the named service
     * @param qname XXX
     * @return XXX
     * @throws ConfigurationException XXX
     */
    public final SOAPService getService(QName qname) throws ConfigurationException {
        SOAPService service = getDeployment().getService(qname);
        if (service == null) {
            throw new ConfigurationException(Messages.getMessage("noService10",
                                                           qname.toString()));
        }
        return service;
    }

    /**
     * Get a service which has been mapped to a particular namespace
     * 
     * @param namespace a namespace URI
     * @return an instance of the appropriate Service, or null
     */
    public final SOAPService getServiceByNamespaceURI(String namespace)
            throws ConfigurationException {
        return getDeployment().getServiceByNamespaceURI(namespace);
    }

    /**
     * retrieve an instance of the named transport
     * @param qname XXX
     * @return XXX
     * @throws ConfigurationException XXX
     */
    public final Handler getTransport(QName qname) throws ConfigurationException {
        return getDeployment().getTransport(qname);
    }

    public final TypeMappingRegistry getTypeMappingRegistry()
        throws ConfigurationException {
        return getDeployment().getTypeMappingRegistry();
    }

    /**
     * Returns a global request handler.
     */
    public final Handler getGlobalRequest() throws ConfigurationException {
        return getDeployment().getGlobalRequest();
    }

    /**
     * Returns a global response handler.
     */
    public final Handler getGlobalResponse() throws ConfigurationException {
        return getDeployment().getGlobalResponse();
    }

    /**
     * Returns the global configuration options.
     */
    public final Hashtable getGlobalOptions() throws ConfigurationException {
        WSDDGlobalConfiguration globalConfig
            = getDeployment().getGlobalConfiguration();
            
        if (globalConfig != null)
            return globalConfig.getParametersTable();

        return null;
    }

    /**
     * Get an enumeration of the services deployed to this engine
     */
    public final Iterator getDeployedServices() throws ConfigurationException {
        return getDeployment().getDeployedServices();
    }

    /**
     * Get a list of roles that this engine plays globally.  Services
     * within the engine configuration may also add additional roles.
     *
     * @return a <code>List</code> of the roles for this engine
     */
    public final List getRoles() {
        return getDeployment().getRoles();
    }
}
