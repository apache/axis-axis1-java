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
import org.apache.axis.encoding.TypeMappingRegistryImpl;
import org.apache.axis.handlers.soap.SOAPService;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

/**
 * A SimpleProvider is an EngineConfiguration which contains a simple
 * HashMap-based registry of Handlers, Transports, and Services.  This is
 * for when you want to programatically deploy components which you create.
 *
 * SimpleProvider may also optionally contain a reference to a "default"
 * EngineConfiguration, which will be scanned for components not found in
 * the internal registry.  This is handy when you want to start with a base
 * configuration (like the default WSDD) and then quickly add stuff without
 * changing the WSDD document.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class SimpleProvider implements EngineConfiguration
{
    /** Handler registry */
    HashMap handlers = new HashMap();
    /** Transport registry */
    HashMap transports = new HashMap();
    /** Service registry */
    HashMap services = new HashMap();

    /** Global configuration stuff */
    Hashtable globalOptions = null;
    Handler globalRequest = null;
    Handler globalResponse = null;
    List roles = new ArrayList();

    /** Our TypeMappingRegistry */
    TypeMappingRegistry tmr = null;

    /** An optional "default" EngineConfiguration */
    EngineConfiguration defaultConfiguration = null;
    private AxisEngine engine;

    /**
     * Default constructor.
     */
    public SimpleProvider() {
    }

    /**
     * Constructor which takes an EngineConfiguration which will be used
     * as the default.
     */
    public SimpleProvider(EngineConfiguration defaultConfiguration) {
        this.defaultConfiguration = defaultConfiguration;
    }

    /**
     * Configure an AxisEngine.  Right now just calls the default
     * configuration if there is one, since we don't do anything special.
     */
    public void configureEngine(AxisEngine engine) throws ConfigurationException
    {
        this.engine = engine;

        if (defaultConfiguration != null)
            defaultConfiguration.configureEngine(engine);

        for (Iterator i = services.values().iterator(); i.hasNext(); ) {
            ((SOAPService)i.next()).setEngine(engine);
        }
    }

    /**
     * We don't write ourselves out, so this is a noop.
     */
    public void writeEngineConfig(AxisEngine engine) throws ConfigurationException
    {
    }

    /**
     * Returns the global configuration options.
     */
    public Hashtable getGlobalOptions() throws ConfigurationException {
        if (globalOptions != null)
            return globalOptions;

        if (defaultConfiguration != null)
            return defaultConfiguration.getGlobalOptions();

        return null;
    }

    /**
     * Set the global options Hashtable
     *
     * @param options
     */
    public void setGlobalOptions(Hashtable options) {
        globalOptions = options;
    }

    /**
     * Returns a global request handler.
     */
    public Handler getGlobalRequest() throws ConfigurationException {
        if (globalRequest != null)
            return globalRequest;

        if (defaultConfiguration != null)
            return defaultConfiguration.getGlobalRequest();

        return null;
    }

    /**
     * Set the global request Handler
     *
     * @param globalRequest
     */
    public void setGlobalRequest(Handler globalRequest) {
        this.globalRequest = globalRequest;
    }

    /**
     * Returns a global response handler.
     */
    public Handler getGlobalResponse() throws ConfigurationException {
        if (globalResponse != null)
            return globalResponse;

        if (defaultConfiguration != null)
            return defaultConfiguration.getGlobalResponse();

        return null;
    }

    /**
     * Set the global response Handler
     *
     * @param globalResponse
     */
    public void setGlobalResponse(Handler globalResponse) {
        this.globalResponse = globalResponse;
    }

    /**
     * Get our TypeMappingRegistry.  Returns our specific one if we have
     * one, otherwise the one from our defaultConfiguration.  If we don't
     * have one and also don't have a defaultConfiguration, we create one.
     *
     */
    public TypeMappingRegistry getTypeMappingRegistry() throws ConfigurationException {
        if (tmr != null)
            return tmr;

        if (defaultConfiguration != null)
            return defaultConfiguration.getTypeMappingRegistry();

        // No default config, but we need a TypeMappingRegistry...
        // (perhaps the TMRs could just be chained?)
        tmr = new TypeMappingRegistryImpl();
        return tmr;
    }

    public TypeMapping getTypeMapping(String encodingStyle) throws ConfigurationException {
        return (TypeMapping)getTypeMappingRegistry().getTypeMapping(encodingStyle);
    }

    public Handler getTransport(QName qname) throws ConfigurationException {
        Handler transport = (Handler)transports.get(qname);
        if ((defaultConfiguration != null) && (transport == null))
            transport = defaultConfiguration.getTransport(qname);
        return transport;
    }

    public SOAPService getService(QName qname) throws ConfigurationException {
        SOAPService service = (SOAPService)services.get(qname);
        if ((defaultConfiguration != null) && (service == null))
            service = defaultConfiguration.getService(qname);
        return service;
    }

    /**
     * Get a service which has been mapped to a particular namespace
     * 
     * @param namespace a namespace URI
     * @return an instance of the appropriate Service, or null
     */
    public SOAPService getServiceByNamespaceURI(String namespace)
            throws ConfigurationException {
        SOAPService service = (SOAPService)services.get(new QName("",namespace));
        if ((service == null) && (defaultConfiguration != null))
            service = defaultConfiguration.getServiceByNamespaceURI(namespace);
        return service;
    }

    public Handler getHandler(QName qname) throws ConfigurationException {
        Handler handler = (Handler)handlers.get(qname);
        if ((defaultConfiguration != null) && (handler == null))
            handler = defaultConfiguration.getHandler(qname);
        return handler;
    }

    public void deployService(QName qname, SOAPService service)
    {
        services.put(qname, service);
        if (engine != null)
            service.setEngine(engine);
    }

    public void deployService(String name, SOAPService service)
    {
        deployService(new QName(null, name), service);
    }

    public void deployTransport(QName qname, Handler transport)
    {
        transports.put(qname, transport);
    }

    public void deployTransport(String name, Handler transport)
    {
        deployTransport(new QName(null, name), transport);
    }

    /**
     * Get an enumeration of the services deployed to this engine
     */
    public Iterator getDeployedServices() throws ConfigurationException {
        ArrayList serviceDescs = new ArrayList();
        Iterator i = services.values().iterator();
        while (i.hasNext()) {
            SOAPService service = (SOAPService)i.next();
            serviceDescs.add(service.getServiceDescription());
        }
        return serviceDescs.iterator();
    }

    /**
     * Set the global role list for this configuration.  Note that we use
     * the actual passed value, so if anyone else changes that collection,
     * our role list will change.  Be careful to pass this a cloned list if
     * you want to change the list later without affecting the config.
     *
     * @param roles
     */
    public void setRoles(List roles) {
        this.roles = roles;
    }

    /**
     * Add a role to the configuration's global list
     *
     * @param role
     */
    public void addRole(String role) {
        roles.add(role);
    }

    /**
     * Remove a role from the configuration's global list
     * 
     * @param role
     */
    public void removeRole(String role) {
        roles.remove(role);
    }

    /**
     * Get a list of roles that this engine plays globally.  Services
     * within the engine configuration may also add additional roles.
     *
     * @return a <code>List</code> of the roles for this engine
     */
    public List getRoles() {
        return roles;
    }
}
