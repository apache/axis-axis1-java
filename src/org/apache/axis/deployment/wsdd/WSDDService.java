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

import org.apache.axis.AxisEngine;
import org.apache.axis.ConfigurationException;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.FaultableHandler;
import org.apache.axis.Handler;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.attachments.Attachments;
import org.apache.axis.attachments.AttachmentsImpl;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.encoding.DeserializerFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.SerializerFactory;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.encoding.TypeMappingRegistryImpl;
import org.apache.axis.encoding.ser.BaseDeserializerFactory;
import org.apache.axis.encoding.ser.BaseSerializerFactory;
import org.apache.axis.enum.Style;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.providers.java.JavaProvider;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Element;
import org.xml.sax.helpers.AttributesImpl;
import org.apache.axis.handlers.HandlerInfoChainFactory;
import org.apache.axis.Constants;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.List;
import java.util.Iterator;

/**
 * A service represented in WSDD.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class WSDDService
    extends WSDDTargetedChain
    implements WSDDTypeMappingContainer
{
    private TypeMappingRegistry tmr = null;

    private Vector faultFlows = new Vector();
    private Vector typeMappings = new Vector();
    private Vector operations = new Vector();

    /** Which namespaces should auto-dispatch to this service? */
    private Vector namespaces = new Vector();

    private String descriptionURL;

    /** Style - document or RPC (the default) */
    private Style style = Style.DEFAULT;

    private SOAPService cachedService = null;

    /**
     * Our provider - used to figure out which Handler we use as a service
     * pivot (see getInstance() below)
     */
    private QName providerQName;

//    private HandlerInfoChainFactory _hiChainFactory;
	private WSDDJAXRPCHandlerInfoChain _wsddHIchain;

    ServiceDesc desc = new ServiceDesc();

    /**
     * Is streaming (i.e. NO high-fidelity recording, deserialize on the fly)
     * on for this service?
     */
    private boolean streaming = false;

    /**
     * What attachment format should be used?
     */
    private int sendType = Attachments.SEND_TYPE_NOTSET;

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

        desc.setName(getQName().getLocalPart());

        String styleStr = e.getAttribute(ATTR_STYLE);
        if (styleStr != null && !styleStr.equals("")) {
            style = Style.getStyle(styleStr, Style.DEFAULT);
            desc.setStyle(style);
            providerQName = style.getProvider();
        }

        String streamStr = e.getAttribute(ATTR_STREAMING);
        if (streamStr != null && streamStr.equals("on")) {
            streaming = true;
        }

        String attachmentStr = e.getAttribute(ATTR_ATTACHMENT_FORMAT);
        if (attachmentStr != null && !attachmentStr.equals("")) {
            sendType = AttachmentsImpl.getSendType(attachmentStr);
        }

        Element [] operationElements = getChildElements(e, ELEM_WSDD_OPERATION);
        for (int i = 0; i < operationElements.length; i++) {
            WSDDOperation operation = new WSDDOperation(operationElements[i],
                                                        desc);
            addOperation(operation);
        }

        Element [] typeMappingElements = getChildElements(e, ELEM_WSDD_TYPEMAPPING);
        for (int i = 0; i < typeMappingElements.length; i++) {
            WSDDTypeMapping mapping =
                    new WSDDTypeMapping(typeMappingElements[i]);
            typeMappings.add(mapping);
        }

        Element [] beanMappingElements = getChildElements(e, ELEM_WSDD_BEANMAPPING);
        for (int i = 0; i < beanMappingElements.length; i++) {
            WSDDBeanMapping mapping =
                    new WSDDBeanMapping(beanMappingElements[i]);
            typeMappings.add(mapping);
        }

        Element [] namespaceElements = getChildElements(e, ELEM_WSDD_NAMESPACE);
        for (int i = 0; i < namespaceElements.length; i++) {
            // Register a namespace for this service
            String ns = XMLUtils.getChildCharacterData(namespaceElements[i]);
            namespaces.add(ns);
        }
        if (!namespaces.isEmpty())
            desc.setNamespaceMappings(namespaces);

        Element wsdlElem = getChildElement(e, ELEM_WSDD_WSDLFILE);
        if (wsdlElem != null) {
            String fileName = XMLUtils.getChildCharacterData(wsdlElem);
            desc.setWSDLFile(fileName);
        }

        Element urlElem = getChildElement(e, ELEM_WSDD_ENDPOINTURL);
        if (urlElem != null) {
            String endpointURL = XMLUtils.getChildCharacterData(urlElem);
            desc.setEndpointURL(endpointURL);
        }

        String providerStr = e.getAttribute(ATTR_PROVIDER);
        if (providerStr != null && !providerStr.equals("")) {
            providerQName = XMLUtils.getQNameFromString(providerStr, e);
            if (WSDDConstants.QNAME_JAVAMSG_PROVIDER.equals(providerQName)) {
                // Message style if message provider...
                desc.setStyle(Style.MESSAGE);
            }
        }

	// Add in JAX-RPC support for HandlerInfo chains
        Element hcEl = getChildElement(e, ELEM_WSDD_JAXRPC_CHAIN);
        if (hcEl != null) {
	    	_wsddHIchain = new WSDDJAXRPCHandlerInfoChain(hcEl);
        }

        initTMR();

        // call to validate standard descriptors for this service
        validateDescriptors();
    }

    /**
     * This method can be used for dynamic deployment using new WSDDService()
     * etc.  It validates some standard parameters for some standard providers
     * (if present).  Do this before deployment.deployService().
     */
    public void validateDescriptors()
    {
        desc.setTypeMappingRegistry(tmr);
        desc.setTypeMapping(getTypeMapping(desc.getStyle().getEncoding()));

        String allowedMethods = getParameter(JavaProvider.OPTION_ALLOWEDMETHODS);
        if (allowedMethods != null && !"*".equals(allowedMethods)) {
            ArrayList methodList = new ArrayList();
            StringTokenizer tokenizer = new StringTokenizer(allowedMethods, " ,");
            while (tokenizer.hasMoreTokens()) {
                methodList.add(tokenizer.nextToken());
            }
            desc.setAllowedMethods(methodList);
        }
    }

    /**
     * Add a WSDDTypeMapping to the Service.
     * @param mapping
     **/
    public void addTypeMapping(WSDDTypeMapping mapping) {
        typeMappings.add(mapping);
    }

    /**
     * Add a WSDDOperation to the Service.
     * @param operation the operation to add
     **/
    public void addOperation(WSDDOperation operation) {
        operations.add(operation);
        desc.addOperationDesc(operation.getOperationDesc());
    }

    protected QName getElementName()
    {
        return QNAME_SERVICE;
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
     * Get the service style - document or RPC
     */
    public Style getStyle() {
        return style;
    }

    public ServiceDesc getServiceDesc() {
        return desc;
    }

    /**
     * Set the service style - document or RPC
     */
    public void setStyle(Style style) {
        this.style = style;
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
     * Obtain the list of namespaces registered for this service
     * @return a Vector of namespaces (Strings) which should dispatch to
     *         this service
     */
    public Vector getNamespaces()
    {
        return namespaces;
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
                          Messages.getMessage("couldntConstructProvider00"));
        }

        Handler respHandler = null;
        WSDDChain response = getResponseFlow();

        if (response != null) {
            respHandler = response.getInstance(registry);
        }

        SOAPService service = new SOAPService(reqHandler, providerHandler,
                                              respHandler);
        service.setStyle(style);
        service.setHighFidelityRecording(!streaming);
        service.setSendType(sendType);

        if ( getQName() != null )
            service.setName(getQName().getLocalPart());
        service.setOptions(getParametersTable());

        service.setEngine(((WSDDDeployment)registry).getEngine());

        if (style != Style.RPC) {
            // No Multirefs/xsi:types
            service.setOption(AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
            service.setOption(AxisEngine.PROP_SEND_XSI, Boolean.FALSE);
        }

	// Set handlerInfoChain
	if (_wsddHIchain != null) {
            HandlerInfoChainFactory hiChainFactory = _wsddHIchain.getHandlerChainFactory();

	    service.setOption(Constants.ATTR_HANDLERINFOCHAIN, hiChainFactory);
	}

        AxisEngine.normaliseOptions(service);

        initTMR();
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

        service.setServiceDescription(desc);
        try {
            service.getInitializedServiceDesc(MessageContext.getCurrentContext());
        } catch (AxisFault axisFault) {
            throw new ConfigurationException(axisFault);
        }

        cachedService = service;
        return service;
    }

    public void deployTypeMapping(WSDDTypeMapping mapping)
        throws WSDDException
    {
        if (!typeMappings.contains(mapping)) {
            typeMappings.add(mapping);
        }
        if (tmr == null) {
            tmr = new TypeMappingRegistryImpl();
        }
        try {
            // Get the encoding style from the mapping, if it isn't set
            // use the style of the service to map doc/lit or rpc/enc
            String encodingStyle = mapping.getEncodingStyle();
            if (encodingStyle == null) {
                encodingStyle = style.getEncoding();
            }
            TypeMapping tm = (TypeMapping) tmr.getTypeMapping(encodingStyle);
            TypeMapping df = (TypeMapping) tmr.getDefaultTypeMapping();
            if (tm == null || tm == df) {
                tm = (TypeMapping) tmr.createTypeMapping();
                tm.setSupportedEncodings(new String[] {encodingStyle});
                tmr.register(encodingStyle, tm);
            }
            desc.setTypeMappingRegistry(tmr);
            desc.setTypeMapping(tm);

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
            attrs.addAttribute("", ATTR_NAME, ATTR_NAME,
                               "CDATA", context.qName2String(name));
        }
        if (providerQName != null) {
            attrs.addAttribute("", ATTR_PROVIDER, ATTR_PROVIDER,
                               "CDATA", context.qName2String(providerQName));
        }
        if (style != Style.DEFAULT) {
            attrs.addAttribute("", ATTR_STYLE, ATTR_STYLE,
                               "CDATA", style.getName());
        }
        
        if (streaming) {
            attrs.addAttribute("", ATTR_STREAMING, ATTR_STREAMING,
                               "CDATA", "on");
        }

        if (sendType != Attachments.SEND_TYPE_NOTSET) {
            attrs.addAttribute("", ATTR_ATTACHMENT_FORMAT,
                               ATTR_ATTACHMENT_FORMAT, "CDATA",
                               AttachmentsImpl.getSendTypeString(sendType));
        }
        context.startElement(WSDDConstants.QNAME_SERVICE, attrs);

        if (desc.getWSDLFile() != null) {
            context.startElement(QNAME_WSDLFILE, null);
            context.writeSafeString(desc.getWSDLFile());
            context.endElement();
        }

        for (int i = 0; i < operations.size(); i++) {
            WSDDOperation operation = (WSDDOperation) operations.elementAt(i);
            operation.writeToContext(context);
        }
        writeFlowsToContext(context);
        writeParamsToContext(context);


        for (int i=0; i < typeMappings.size(); i++) {
            ((WSDDTypeMapping) typeMappings.elementAt(i)).writeToContext(context);
        }

        for (int i=0; i < namespaces.size(); i++ ) {
            context.startElement(QNAME_NAMESPACE, null);
            context.writeString((String)namespaces.get(i));
            context.endElement();
        }

        String endpointURL = desc.getEndpointURL();
        if (endpointURL != null) {
            context.startElement(QNAME_ENDPOINTURL, null);
            context.writeSafeString(endpointURL);
            context.endElement();
        }

		if (_wsddHIchain != null) {
			_wsddHIchain.writeToContext(context);

		}
		
        context.endElement();


    }

    public void setCachedService(SOAPService service)
    {
        cachedService = service;
    }

    public Vector getTypeMappings() {
        return typeMappings;
    }

    public void setTypeMappings(Vector typeMappings) {
        this.typeMappings = typeMappings;
    }

    public void deployToRegistry(WSDDDeployment registry)
    {
        registry.addService(this);

        // Register the name of the service as a valid namespace, just for
        // backwards compatibility
        registry.registerNamespaceForService(getQName().getLocalPart(), this);

        for (int i = 0; i < namespaces.size(); i++) {
            String namespace = (String) namespaces.elementAt(i);
            registry.registerNamespaceForService(namespace, this);
        }

        super.deployToRegistry(registry);
    }

    public void removeNamespaceMappings(WSDDDeployment registry)
    {
        for (int i = 0; i < namespaces.size(); i++) {
            String namespace = (String) namespaces.elementAt(i);
            registry.removeNamespaceMapping(namespace);
        }
        registry.removeNamespaceMapping(getQName().getLocalPart());
    }

    public void initTMR() throws WSDDException
    {
        if (tmr == null) {
            tmr = new TypeMappingRegistryImpl();
            for (int i = 0; i < typeMappings.size(); i++) {
                deployTypeMapping((WSDDTypeMapping)typeMappings.get(i));
            }
        }
    }

    public TypeMapping getTypeMapping(String encodingStyle) {
        return (TypeMapping) tmr.getTypeMapping(encodingStyle);
    }
}
