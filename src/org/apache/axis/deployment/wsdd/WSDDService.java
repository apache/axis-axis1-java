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

import org.apache.axis.utils.XMLUtils;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.encoding.ser.BaseSerializerFactory;
import org.apache.axis.encoding.ser.BaseDeserializerFactory;
import org.apache.axis.encoding.*;
import org.apache.axis.*;
import org.apache.axis.deployment.DeploymentRegistry;
import org.apache.axis.deployment.DeploymentException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.rpc.namespace.QName;
import java.util.Vector;
import java.io.IOException;
import java.beans.IntrospectionException;

/**
 *
 */
public class WSDDService
    extends WSDDTargetedChain
    implements WSDDTypeMappingContainer
{
    public TypeMappingRegistry tmr = null;
    
    private Vector faultFlows = new Vector();
    private Vector typeMappings = new Vector();
    
    private String descriptionURL;

    private SOAPService cachedService = null;
    
    /**
     * Our provider - used to figure out which Handler we use as a service
     * pivot (see getInstance() below)
     */ 
    private QName providerQName;

    /**
     * Default constructor
     */ 
    public WSDDService()
    {
    }
    
    /**
     *
     * @param e (Element) XXX
     * @throws WSDDException XXX
     */
    public WSDDService(Element e)
        throws WSDDException
    {
        super(e);
        
        Element [] typeMappingElements = getChildElements(e, "typeMapping");
        for (int i = 0; i < typeMappingElements.length; i++) {
            WSDDTypeMapping mapping =
                    new WSDDTypeMapping(typeMappingElements[i]);
            typeMappings.add(mapping);
        }
        
        Element [] beanMappingElements = getChildElements(e, "beanMapping");
        for (int i = 0; i < beanMappingElements.length; i++) {
            WSDDBeanMapping mapping =
                    new WSDDBeanMapping(beanMappingElements[i]);
            typeMappings.add(mapping);
        }

        String typeStr = e.getAttribute("provider");
        if (typeStr != null && !typeStr.equals(""))
            providerQName = XMLUtils.getQNameFromString(typeStr, e);
    }

    protected QName getElementName()
    {
        return WSDDConstants.SERVICE_QNAME;
    }
    
    /**
     * Get any service description URL which might be associated with this
     * service.
     * 
     * @return a String containing a URL, or null.
     */
    public String getServiceDescriptionURL()
    {
        return descriptionURL;
    }

    /**
     * Set the service description URL for this service.
     * 
     * @param sdUrl a String containing a URL
     */
    public void setServiceDescriptionURL(String sdUrl)
    {
        descriptionURL = sdUrl;
    }

    public QName getProviderQName() {
        return providerQName;
    }

    public void setProviderQName(QName providerQName) {
        this.providerQName = providerQName;
    }

    /**
     *
     * @return XXX
     */
    public WSDDFaultFlow[] getFaultFlows()
    {
        WSDDFaultFlow[] t = new WSDDFaultFlow[faultFlows.size()];
        faultFlows.toArray(t);
        return t;
    }

    /**
     *
     * @param name XXX
     * @return XXX
     */
    public WSDDFaultFlow getFaultFlow(QName name)
    {
        WSDDFaultFlow[] t = getFaultFlows();

        for (int n = 0; n < t.length; n++) {
            if (t[n].getQName().equals(name)) {
                return t[n];
            }
        }

        return null;
    }

    /**
     *
     * @param registry XXX
     * @return XXX
     * @throws ConfigurationException XXX
     */
    public Handler makeNewInstance(EngineConfiguration registry)
        throws ConfigurationException
    {
        if (cachedService != null) {
            return cachedService;
        }
        
        Handler reqHandler = null;
        WSDDChain request = getRequestFlow();
 
        if (request != null) {
            reqHandler = request.getInstance(registry);
        }

        Handler providerHandler = null;

        if (providerQName != null) {
            try {
                providerHandler = WSDDProvider.getInstance(providerQName,
                                                           this,
                                                           registry);
            } catch (Exception e) {
                throw new ConfigurationException(e);
            }
            if (providerHandler == null)
                throw new WSDDException(
                          JavaUtils.getMessage("couldntConstructProvider00"));
        }

        Handler respHandler = null;
        WSDDChain response = getResponseFlow();

        if (response != null) {
            respHandler = response.getInstance(registry);
        }
  
        SOAPService service = new SOAPService(reqHandler, providerHandler,
                                              respHandler);

        if ( getQName() != null )
            service.setName(getQName().getLocalPart());
        service.setOptions(getParametersTable());

        if (tmr == null) {
            tmr = new TypeMappingRegistryImpl();
        }
        for (int i = 0; i < typeMappings.size(); i++) {
            deployTypeMapping((WSDDTypeMapping)typeMappings.get(i));
        }

        service.setTypeMappingRegistry(tmr);
        tmr.delegate(registry.getTypeMappingRegistry());

        WSDDFaultFlow [] faultFlows = getFaultFlows();
        if (faultFlows != null && faultFlows.length > 0) {
            FaultableHandler wrapper = new FaultableHandler(service);
            for (int i = 0; i < faultFlows.length; i++) {
                WSDDFaultFlow flow = faultFlows[i];
                Handler faultHandler = flow.getInstance(registry);
                wrapper.setOption("fault-" + flow.getQName().getLocalPart(),
                                  faultHandler);
            }
        }
        
        cachedService = service;
        return service;
    }
    
    public void deployTypeMapping(WSDDTypeMapping mapping)
        throws WSDDException
    {
        if (tmr == null) {
            tmr = new TypeMappingRegistryImpl();
        }
        try {
            TypeMapping tm = (TypeMapping) tmr.getTypeMapping(mapping.getEncodingStyle());
            TypeMapping df = (TypeMapping) tmr.getDefaultTypeMapping();
            if (tm == null || tm == df) {
                tm = (TypeMapping) tmr.createTypeMapping();
                String namespace = mapping.getEncodingStyle();
                if (mapping.getEncodingStyle() == null) {
                    namespace = Constants.URI_CURRENT_SOAP_ENC;
                }
                tm.setSupportedNamespaces(new String[] {namespace});
                tmr.register(namespace, tm);
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
            if (mapping.getSerializerName() != null &&
                !mapping.getSerializerName().equals("")) {
                ser = BaseSerializerFactory.createFactory(mapping.getSerializer(), 
                                                          mapping.getLanguageSpecificType(),
                                                          mapping.getQName());
            }
            
            if (mapping.getDeserializerName() != null &&
                !mapping.getDeserializerName().equals("")) {
                deser = BaseDeserializerFactory.createFactory(mapping.getDeserializer(), 
                                                          mapping.getLanguageSpecificType(),
                                                          mapping.getQName());
            }
            tm.register( mapping.getLanguageSpecificType(), mapping.getQName(), ser, deser);
        } catch (ClassNotFoundException e) {
            throw new WSDDException(e);
        } catch (Exception e) {
            throw new WSDDException(e);
        }
    }

    /**
     * Write this element out to a SerializationContext
     */
    public void writeToContext(SerializationContext context)
            throws IOException {
        AttributesImpl attrs = new AttributesImpl();
        QName name = getQName();
        if (name != null) {
            attrs.addAttribute("", "name", "name",
                               "CDATA", context.qName2String(name));
        }
        if (providerQName != null) {
            attrs.addAttribute("", "provider", "provider",
                               "CDATA", context.qName2String(providerQName));
        }
        
        context.startElement(WSDDConstants.SERVICE_QNAME, attrs);
        writeFlowsToContext(context);
        writeParamsToContext(context);

        for (int i=0; i < typeMappings.size(); i++) {
            ((WSDDTypeMapping) typeMappings.elementAt(i)).writeToContext(context);
        }

        context.endElement();
    }
    
    public void setCachedService(SOAPService service)
    {
        cachedService = service;
    }

    public void deployToRegistry(WSDDDeployment registry)
            throws WSDDException {
        registry.deployService(this);
        
        super.deployToRegistry(registry);
    }
}
