/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
package org.apache.axis.deployment.wsdd;

import org.apache.axis.AxisEngine;
import org.apache.axis.ConfigurationException;
import org.apache.axis.Constants;
import org.apache.axis.Handler;
import org.apache.axis.WSDDEngineConfiguration;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.DeserializerFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.SerializerFactory;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.encoding.TypeMappingRegistryImpl;
import org.apache.axis.encoding.ser.BaseDeserializerFactory;
import org.apache.axis.encoding.ser.BaseSerializerFactory;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;


/**
 * WSDD deployment element
 *
 * @author James Snell
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class WSDDDeployment
    extends WSDDElement
    implements WSDDTypeMappingContainer,
               WSDDEngineConfiguration
{
    protected static Log log =
        LogFactory.getLog(WSDDDeployment.class.getName());

    private HashMap handlers = new HashMap();
    private HashMap services = new HashMap();
    private HashMap transports = new HashMap();
    private Vector typeMappings = new Vector();
    private WSDDGlobalConfiguration globalConfig = null;
    
    /** Mapping of namespaces -> services */
    private HashMap namespaceToServices = new HashMap();
    private AxisEngine engine;

    protected void addHandler(WSDDHandler handler)
    {
        handlers.put(handler.getQName(), handler);
    }
    
    protected void addService(WSDDService service)
    {
        WSDDService oldService = (WSDDService)services.get(service.getQName());
        if (oldService != null) {
            oldService.removeNamespaceMappings(this);
        }
        services.put(service.getQName(), service);
    }
    
    protected void addTransport(WSDDTransport transport)
    {
        transports.put(transport.getQName(), transport);
    }
    

    /**
     * Put a WSDDHandler into this deployment, replacing any other
     * WSDDHandler which might already be present with the same QName.
     *
     * @param handler a WSDDHandler to insert in this deployment
     */
    public void deployHandler(WSDDHandler handler)
    {
        handler.deployToRegistry(this);
    }

    /**
     * Put a WSDDTransport into this deployment, replacing any other
     * WSDDTransport which might already be present with the same QName.
     *
     * @param transport a WSDDTransport to insert in this deployment
     */
    public void deployTransport(WSDDTransport transport)
    {
        transport.deployToRegistry(this);
    }

    /**
     * Put a WSDDService into this deployment, replacing any other
     * WSDDService which might already be present with the same QName.
     *
     * @param service a WSDDHandler to insert in this deployment
     */
    public void deployService(WSDDService service)
    {
        service.deployToRegistry(this);
    }

    /**
     * Remove a named handler
     * @param qname the QName of the handler to remove
     */
    public void undeployHandler(QName qname)
    {
        handlers.remove(qname);
    }

    /**
     * Remove a named service
     * @param qname the QName of the service to remove
     */
    public void undeployService(QName qname)
    {
        WSDDService service = (WSDDService)services.get(qname);
        if (service != null) {
            service.removeNamespaceMappings(this);
            services.remove(qname);
        }
    }

    /**
     * Remove a named transport
     * @param qname the QName of the transport to remove
     */
    public void undeployTransport(QName qname)
    {
        transports.remove(qname);
    }

    public void deployTypeMapping(WSDDTypeMapping typeMapping)
        throws WSDDException
    {
        if (!typeMappings.contains(typeMapping)) {
            typeMappings.add(typeMapping);
        }
        if (tmrDeployed)
            deployMapping(typeMapping);
    }

    /**
     * Default constructor
     */
    public WSDDDeployment()
    {
    }

    /**
     * Create an element in WSDD that wraps an extant DOM element
     * @param e (Element) XXX
     * @throws WSDDException XXX
     */
    public WSDDDeployment(Element e)
        throws WSDDException
    {
        super(e);

        Element [] elements = getChildElements(e, ELEM_WSDD_HANDLER);
        int i;

        for (i = 0; i < elements.length; i++) {
            WSDDHandler handler = new WSDDHandler(elements[i]);
            deployHandler(handler);
        }

        elements = getChildElements(e, ELEM_WSDD_CHAIN);
        for (i = 0; i < elements.length; i++) {
            WSDDChain chain = new WSDDChain(elements[i]);
            deployHandler(chain);
        }

        elements = getChildElements(e, ELEM_WSDD_TRANSPORT);
        for (i = 0; i < elements.length; i++) {
            WSDDTransport transport = new WSDDTransport(elements[i]);
            deployTransport(transport);
        }

        elements = getChildElements(e, ELEM_WSDD_SERVICE);
        for (i = 0; i < elements.length; i++) {
            try {
                WSDDService service = new WSDDService(elements[i]);
                deployService(service);
            } catch (WSDDNonFatalException ex) {
                // If it's non-fatal, just keep on going
            } catch (WSDDException ex) {
                // otherwise throw it upwards
                throw ex;
            }
        }

        elements = getChildElements(e, ELEM_WSDD_TYPEMAPPING);
        for (i = 0; i < elements.length; i++) {
            try {
                WSDDTypeMapping mapping = new WSDDTypeMapping(elements[i]);
                deployTypeMapping(mapping);
            } catch (WSDDNonFatalException ex) {
                // If it's non-fatal, just keep on going
            } catch (WSDDException ex) {
                // otherwise throw it upwards
                throw ex;
            }
        }

        elements = getChildElements(e, ELEM_WSDD_BEANMAPPING);
        for (i = 0; i < elements.length; i++) {
            WSDDBeanMapping mapping = new WSDDBeanMapping(elements[i]);
            deployTypeMapping(mapping);
        }

        Element el = getChildElement(e, ELEM_WSDD_GLOBAL);
        if (el != null)
            globalConfig = new WSDDGlobalConfiguration(el);
    }

    protected QName getElementName()
    {
        return QNAME_DEPLOY;
    }

    public void deployToRegistry(WSDDDeployment target)
        throws ConfigurationException
    {

        WSDDGlobalConfiguration global = getGlobalConfiguration();

        if (global != null) {
            target.setGlobalConfiguration(global);
        }

        Iterator i = handlers.values().iterator();
        while (i.hasNext()) {
            WSDDHandler handler = (WSDDHandler) i.next();
            target.deployHandler(handler);
        }

        i = transports.values().iterator();
        while (i.hasNext()) {
            WSDDTransport transport = (WSDDTransport) i.next();
            target.deployTransport(transport);
        }

        i = services.values().iterator();
        while (i.hasNext()) {
            WSDDService service = (WSDDService) i.next();
            service.deployToRegistry(target);
        }

        i = typeMappings.iterator();
        while (i.hasNext()) {
            WSDDTypeMapping mapping = (WSDDTypeMapping) i.next();
            target.deployTypeMapping(mapping);
        }
    }

    private void deployMapping(WSDDTypeMapping mapping)
            throws WSDDException
    {
        try {
            String encodingStyle = mapping.getEncodingStyle();
            if (encodingStyle == null) {
                encodingStyle = Constants.URI_DEFAULT_SOAP_ENC;
            }
            TypeMapping tm = tmr.getOrMakeTypeMapping(encodingStyle);

            SerializerFactory   ser   = null;
            DeserializerFactory deser = null;

            // Try to construct a serializerFactory by introspecting for the
            // following:
            // public static create(Class javaType, QName xmlType)
            // public <constructor>(Class javaType, QName xmlType)
            // public <constructor>()
            //
            // The BaseSerializerFactory createFactory() method is a utility
            // that does this for us.
            //log.debug("start creating sf and df");
            if (mapping.getSerializerName() != null &&
                !mapping.getSerializerName().equals("")) {
                ser = BaseSerializerFactory.createFactory(mapping.getSerializer(),
                                                          mapping.getLanguageSpecificType(),
                                                          mapping.getQName());
            }
            //log.debug("set ser factory");

            if (mapping.getDeserializerName() != null &&
                !mapping.getDeserializerName().equals("")) {
                deser = BaseDeserializerFactory.createFactory(mapping.getDeserializer(),
                                                          mapping.getLanguageSpecificType(),
                                                          mapping.getQName());
            }
            //log.debug("set dser factory");
            tm.register( mapping.getLanguageSpecificType(), mapping.getQName(), ser, deser);
            //log.debug("registered");
        } catch (ClassNotFoundException e) {
            log.error(Messages.getMessage("unabletoDeployTypemapping00", mapping.getQName().toString()), e);
            throw new WSDDNonFatalException(e);
        } catch (Exception e) {
            throw new WSDDException(e);
        }
    }

    public void writeToContext(SerializationContext context)
        throws IOException
    {
        context.registerPrefixForURI(NS_PREFIX_WSDD, URI_WSDD);
        
        context.registerPrefixForURI(NS_PREFIX_WSDD_JAVA, URI_WSDD_JAVA);
                                     
        context.startElement(QNAME_DEPLOY, null);

        if (globalConfig != null) {
            globalConfig.writeToContext(context);
        }

        Iterator i = handlers.values().iterator();
        while (i.hasNext()) {
            WSDDHandler handler = (WSDDHandler)i.next();
            handler.writeToContext(context);
        }

        i = services.values().iterator();
        while (i.hasNext()) {
            WSDDService service = (WSDDService)i.next();
            service.writeToContext(context);
        }

        i = transports.values().iterator();
        while (i.hasNext()) {
            WSDDTransport transport = (WSDDTransport)i.next();
            transport.writeToContext(context);
        }

        i = typeMappings.iterator();
        while (i.hasNext()) {
            WSDDTypeMapping mapping = (WSDDTypeMapping)i.next();
            mapping.writeToContext(context);
        }
        context.endElement();
    }

    /**
     * Get our global configuration
     *
     * @return XXX
     */
    public WSDDGlobalConfiguration getGlobalConfiguration()
    {
        return globalConfig;
    }

    public void setGlobalConfiguration(WSDDGlobalConfiguration globalConfig) {
        this.globalConfig = globalConfig;
    }

    /**
     *
     * @return XXX
     */
    public WSDDTypeMapping[] getTypeMappings()
    {
        WSDDTypeMapping[] t = new WSDDTypeMapping[typeMappings.size()];
        typeMappings.toArray(t);
        return t;
    }

    /**
     * Return an array of the services in this deployment
     */
    public WSDDService[] getServices()
    {
        WSDDService [] serviceArray = new WSDDService[services.size()];
        services.values().toArray(serviceArray);
        return serviceArray;
    }

    /**
     * Return the WSDD description for a given named service
     */ 
    public WSDDService getWSDDService(QName qname)
    {
        return (WSDDService)services.get(qname);
    }
    
    /**
     *
     * @param name XXX
     * @return XXX
     */
    public Handler getHandler(QName name) throws ConfigurationException
    {
        WSDDHandler h = (WSDDHandler)handlers.get(name);
        if (h != null) {
            return h.getInstance(this);
        }

        return null;
    }

    /**
     *
     * @param name XXX
     * @return XXX
     */
    public Handler getTransport(QName name) throws ConfigurationException
    {
        WSDDTransport t = (WSDDTransport)transports.get(name);
        if (t != null) {
            return t.getInstance(this);
        }

        return null;
    }

    /**
     *
     * @param name XXX
     * @return XXX
     */
    public SOAPService getService(QName name) throws ConfigurationException
    {
        WSDDService s = (WSDDService)services.get(name);
        if (s != null) {
            return (SOAPService)s.getInstance(this);
        }

        return null;
    }

    public SOAPService getServiceByNamespaceURI(String namespace)
        throws ConfigurationException
    {
        WSDDService s = (WSDDService)namespaceToServices.get(namespace);
        if (s != null) {
            return (SOAPService)s.getInstance(this);
        }
        
        return null;
    }

    public void configureEngine(AxisEngine engine)
            throws ConfigurationException {
        this.engine = engine;
    }

    public void writeEngineConfig(AxisEngine engine) throws ConfigurationException {
    }

    TypeMappingRegistry tmr = new TypeMappingRegistryImpl();
    public TypeMapping getTypeMapping(String encodingStyle) throws ConfigurationException {
        return (TypeMapping)getTypeMappingRegistry().getTypeMapping(encodingStyle);
    }

    private boolean tmrDeployed = false;
    public TypeMappingRegistry getTypeMappingRegistry() throws ConfigurationException {
        if (false == tmrDeployed) {
            for (int i = 0; i < typeMappings.size(); i++) {
                WSDDTypeMapping mapping = (WSDDTypeMapping)typeMappings.get(i);
                deployMapping(mapping);
            }
            tmrDeployed = true;
        }
        return tmr;
    }

    public Handler getGlobalRequest() throws ConfigurationException {
        if (globalConfig != null) {
            WSDDRequestFlow reqFlow = globalConfig.getRequestFlow();
            if (reqFlow != null)
                return reqFlow.getInstance(this);
        }

        return null;
    }

    public Handler getGlobalResponse() throws ConfigurationException {
        if (globalConfig != null) {
            WSDDResponseFlow respFlow = globalConfig.getResponseFlow();
            if (respFlow != null)
                return respFlow.getInstance(this);
        }

        return null;
    }

    public Hashtable getGlobalOptions() throws ConfigurationException {
        return globalConfig.getParametersTable();
    }

    /**
     * Get an enumeration of the services deployed to this engine
     */
    public Iterator  getDeployedServices() throws ConfigurationException {
        ArrayList serviceDescs = new ArrayList();
        for (Iterator i = services.values().iterator(); i.hasNext();) {
            WSDDService service = (WSDDService) i.next();
            try {
                service.makeNewInstance(this);
                serviceDescs.add(service.getServiceDesc());
            } catch (WSDDNonFatalException ex) {
                // If it's non-fatal, just keep on going
                log.debug("Ingoring non-fatal exception: ", ex);
            }
        }
        return serviceDescs.iterator();
    }

    /**
     * Register a particular namepsace which maps to a given WSDDService.
     * This will be used for namespace-based dispatching.
     * 
     * @param namespace a namespace URI
     * @param service the target WSDDService
     */ 
    public void registerNamespaceForService(String namespace,
                                            WSDDService service)
    {
        namespaceToServices.put(namespace, service);
    }
    
    /**
     * Remove a namespace -> WSDDService mapping.
     * 
     * @param namespace the namespace URI to unmap
     */ 
    public void removeNamespaceMapping(String namespace)
    {
        namespaceToServices.remove(namespace);
    }

    public AxisEngine getEngine() {
        return engine;
    }

    public WSDDDeployment getDeployment() { return this; }
}
