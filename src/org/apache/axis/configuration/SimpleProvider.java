/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.axis.configuration;

import org.apache.axis.AxisEngine;
import org.apache.axis.ConfigurationException;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.encoding.TypeMappingRegistryImpl;

import javax.xml.rpc.namespace.QName;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

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
 * @author Glen Daniels (gdaniels@macromedia.com)
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

    /** Our TypeMappingRegistry */
    TypeMappingRegistry tmr = null;

    /** An optional "default" EngineConfiguration */
    EngineConfiguration defaultConfiguration = null;

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
        if (defaultConfiguration != null)
            defaultConfiguration.configureEngine(engine);
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
}
