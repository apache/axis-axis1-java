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
package org.apache.axis.deployment.wsdd;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.apache.axis.Constants;
import org.apache.axis.deployment.DeploymentRegistry;
import org.apache.axis.deployment.DeploymentException;
import org.apache.axis.encoding.ser.BaseSerializerFactory;
import org.apache.axis.encoding.ser.BaseDeserializerFactory;
import org.apache.axis.encoding.*;

import javax.xml.rpc.namespace.QName;
import java.util.Vector;
import java.util.Iterator;
import java.io.IOException;


/**
 * WSDD deployment element
 *
 * @author James Snell
 */
public class WSDDDeployment
    extends WSDDElement
    implements WSDDTypeMappingContainer
{
    private Vector handlers = new Vector();
    private Vector services = new Vector();
    private Vector transports = new Vector();
    private Vector typeMappings = new Vector();
    private WSDDGlobalConfiguration globalConfig = null; 
    
    public void addHandler(WSDDHandler handler)
    {
        handlers.add(handler);
    }
    
    public void addTransport(WSDDTransport transport)
    {
        transports.add(transport);
    }
    
    public void addService(WSDDService service)
    {
        services.add(service);
    }
    
    public void addTypeMapping(WSDDTypeMapping typeMapping)
        throws WSDDException
    {
        typeMappings.add(typeMapping);
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
        
        Element [] elements = getChildElements(e, "handler");
        int i;

        for (i = 0; i < elements.length; i++) {
            WSDDHandler handler = new WSDDHandler(elements[i]);
            addHandler(handler);
        }

        elements = getChildElements(e, "chain");
        for (i = 0; i < elements.length; i++) {
            WSDDChain chain = new WSDDChain(elements[i]);
            addHandler(chain);
        }
        
        elements = getChildElements(e, "transport");
        for (i = 0; i < elements.length; i++) {
            WSDDTransport transport = new WSDDTransport(elements[i]);
            addTransport(transport);
        }
        
        elements = getChildElements(e, "service");
        for (i = 0; i < elements.length; i++) {
            WSDDService service = new WSDDService(elements[i]);
            addService(service);
        }
        
        elements = getChildElements(e, "typeMapping");
        for (i = 0; i < elements.length; i++) {
            WSDDTypeMapping mapping = new WSDDTypeMapping(elements[i]);
            addTypeMapping(mapping);
        }

        elements = getChildElements(e, "beanMapping");
        for (i = 0; i < elements.length; i++) {
            WSDDBeanMapping mapping = new WSDDBeanMapping(elements[i]);
            addTypeMapping(mapping);
        }

        Element el = getChildElement(e, "globalConfiguration");
        if (el != null)
            globalConfig = new WSDDGlobalConfiguration(el);
    }

    protected QName getElementName()
    {
        return WSDDConstants.DEPLOY_QNAME;
    }

    public void deployToRegistry(DeploymentRegistry registry)
        throws DeploymentException
    {

        WSDDGlobalConfiguration global = getGlobalConfiguration();

        if (global != null) {
            registry.setGlobalConfiguration(global);
        }

        WSDDHandler[]     handlers   = getHandlers();
        WSDDTransport[]   transports = getTransports();
        WSDDService[]     services   = getServices();
        WSDDTypeMapping[] mappings   = getTypeMappings();

        for (int n = 0; n < handlers.length; n++) {
            handlers[n].deployToRegistry(registry);
        }

        for (int n = 0; n < transports.length; n++) {
            transports[n].deployToRegistry(registry);
        }

        for (int n = 0; n < services.length; n++) {
            services[n].deployToRegistry(registry);
        }
        for (int n = 0; n < mappings.length; n++) {
            WSDDTypeMapping     mapping = mappings[n];
            deployMappingToRegistry(mapping, registry);
        }
    }

    public static void deployMappingToRegistry(WSDDTypeMapping mapping,
                                               DeploymentRegistry registry)
            throws DeploymentException
    {
        try {
            //System.out.println(mapping.getQName() + " " +
            //                   mapping.getLanguageSpecificType() + " " +
            //                   mapping.getSerializer() + " " + 
            //                   mapping.getDeserializer() + " " +
            //                   mapping.getEncodingStyle());

            TypeMappingRegistry tmr     = 
                registry.getTypeMappingRegistry();
            
            TypeMapping tm = (TypeMapping) tmr.getTypeMapping(mapping.getEncodingStyle());
            TypeMapping df = (TypeMapping) tmr.getDefaultTypeMapping();
            if (tm == null || tm == df) {
                tm = (TypeMapping) tmr.createTypeMapping();
                String namespace = mapping.getEncodingStyle();
                if (mapping.getEncodingStyle() == null) {
                    namespace = Constants.URI_CURRENT_SOAP_ENC;
                }
                tm.setSupportedEncodings(new String[] {namespace});
                tmr.register(tm, new String[] {namespace});
            }
            
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
            //System.out.println("start creating sf and df");
            if (mapping.getSerializerName() != null &&
                !mapping.getSerializerName().equals("")) {
                ser = BaseSerializerFactory.createFactory(mapping.getSerializer(), 
                                                          mapping.getLanguageSpecificType(),
                                                          mapping.getQName());
            }
            //System.out.println("set ser factory");
            
            if (mapping.getDeserializerName() != null &&
                !mapping.getDeserializerName().equals("")) {
                deser = BaseDeserializerFactory.createFactory(mapping.getDeserializer(), 
                                                          mapping.getLanguageSpecificType(),
                                                          mapping.getQName());
            }
            //System.out.println("set dser factory");
            tm.register( mapping.getLanguageSpecificType(), mapping.getQName(), ser, deser);
            //System.out.println("registered");
        }
        catch (Exception e) {
            throw new DeploymentException(e);
        }
    }

    public void writeToContext(SerializationContext context)
        throws IOException
    {
        context.registerPrefixForURI("", WSDDConstants.WSDD_NS);
        context.registerPrefixForURI("java", WSDDConstants.WSDD_JAVA);
        context.startElement(new QName(WSDDConstants.WSDD_NS, "deployment"),
                             null);
        
        if (globalConfig != null) {
            globalConfig.writeToContext(context);
        }
        
        Iterator i = handlers.iterator();
        while (i.hasNext()) {
            WSDDHandler handler = (WSDDHandler)i.next();
            handler.writeToContext(context);
        }
        
        i = services.iterator();
        while (i.hasNext()) {
            WSDDService service = (WSDDService)i.next();
            service.writeToContext(context);
        }
        
        i = transports.iterator();
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
     *
     * @return XXX
     */
    public WSDDHandler[] getHandlers()
    {
        WSDDHandler[] h = new WSDDHandler[handlers.size()];
        handlers.toArray(h);
        return h;
    }

    /**
     *
     * @param name XXX
     * @return XXX
     */
    public WSDDHandler getHandler(QName name)
    {

        WSDDHandler[] h = getHandlers();

        for (int n = 0; n < h.length; n++) {
            if (h[n].getQName().equals(name)) {
                return h[n];
            }
        }

        return null;
    }

    /**
     *
     * @return XXX
     */
    public WSDDTransport[] getTransports()
    {
        WSDDTransport[] t = new WSDDTransport[transports.size()];
        transports.toArray(t);
        return t;
    }

    /**
     *
     * @param name XXX
     * @return XXX
     */
    public WSDDTransport getTransport(QName name)
    {

        WSDDTransport[] t = getTransports();

        for (int n = 0; n < t.length; n++) {
            if (t[n].getQName().equals(name)) {
                return t[n];
            }
        }

        return null;
    }

    /**
     *
     * @return XXX
     */
    public WSDDService[] getServices()
    {
        WSDDService[] s = new WSDDService[services.size()];
        services.toArray(s);
        return s;
    }

    /**
     *
     * @param name XXX
     * @return XXX
     */
    public WSDDService getService(QName name)
    {

        WSDDService[] s = getServices();

        for (int n = 0; n < s.length; n++) {
            if (s[n].getQName().equals(name)) {
                return s[n];
            }
        }

        return null;
    }
}
