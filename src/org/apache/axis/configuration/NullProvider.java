/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

package org.apache.axis.configuration;

import org.apache.axis.AxisEngine;
import org.apache.axis.ConfigurationException;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.handlers.soap.SOAPService;

import javax.xml.namespace.QName;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

/**
 * A do-nothing ConfigurationProvider
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class NullProvider implements EngineConfiguration
{
    public void configureEngine(AxisEngine engine) throws ConfigurationException
    {
    }

    public void writeEngineConfig(AxisEngine engine) throws ConfigurationException
    {
    }

    public Hashtable getGlobalOptions() throws ConfigurationException {
        return null;
    }

    public Handler getGlobalResponse() throws ConfigurationException {
        return null;
    }

    public Handler getGlobalRequest() throws ConfigurationException {
        return null;
    }

    public TypeMappingRegistry getTypeMappingRegistry() throws ConfigurationException {
        return null;
    }

    public TypeMapping getTypeMapping(String encodingStyle) throws ConfigurationException {
        return null;
    }

    public Handler getTransport(QName qname) throws ConfigurationException {
        return null;
    }

    public SOAPService getService(QName qname) throws ConfigurationException {
        return null;
    }

    public SOAPService getServiceByNamespaceURI(String namespace)
            throws ConfigurationException {
        return null;
    }

    public Handler getHandler(QName qname) throws ConfigurationException {
        return null;
    }

    /**
     * Get an enumeration of the services deployed to this engine
     */
    public Iterator getDeployedServices() throws ConfigurationException {
        return null;
    }

    /**
     * Get a list of roles that this engine plays globally.  Services
     * within the engine configuration may also add additional roles.
     *
     * @return a <code>List</code> of the roles for this engine
     */
    public List getRoles() {
        return null;
    }
}
