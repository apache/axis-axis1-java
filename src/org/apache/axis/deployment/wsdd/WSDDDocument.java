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

import org.apache.axis.deployment.DeploymentDocument;
import org.apache.axis.deployment.DeploymentException;
import org.apache.axis.deployment.DeploymentRegistry;
import org.apache.axis.deployment.DeployableItem;
import org.apache.axis.encoding.DeserializerFactory;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.encoding.SOAPTypeMappingRegistry;
import org.apache.axis.utils.XMLUtils;
import org.apache.axis.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * represents a WSDD Document (this is the top level object in this object model)
 */
public class WSDDDocument
    implements DeploymentDocument
{

    /** XXX */
    private Document doc;
    private Element deploymentElement;

    /** XXX */
    private WSDDDeployment dep;

    /**
     *
     */
    public WSDDDocument()
    {
    }

    /**
     *
     * @param doc (Document) XXX
     */
    public WSDDDocument(Document doc)
    {
        this.doc = doc;
        deploymentElement = doc.getDocumentElement();
    }

    /**
     *
     * @param e (Element) XXX
     */
    public WSDDDocument(Element e)
    {
        deploymentElement = e;
        doc = e.getOwnerDocument();
    }

    public Document getDOMDocument() throws DeploymentException {
        return getDocument();
    }

    /**
     *
     * @return XXX
     */
    public WSDDDeployment getDeployment()
    {
        getDocument();

        if (null == dep) {
            try {
                if (null == deploymentElement) {
                    // create both the DOM and WSDD deployment 'child'
                    dep = new WSDDDeployment(doc);
                }
                else {
                    // create the WSDD 'child' from the given DOM deployment
                    dep = new WSDDDeployment(deploymentElement);
                }
            }
            catch (Exception e) {
                e.printStackTrace();

                // log the stack trace?
                //
                // leave dep as null
            }
        }

        return dep;
    }

    /**
     *
     * @return XXX
     */
    public Document getDocument()
    {
        if (null == doc) {
            doc = XMLUtils.newDocument();
            Element el = doc.createElementNS(WSDDConstants.WSDD_NS, "deployment");
            el.setAttributeNS(
                            Constants.NS_URI_XMLNS,
                            "xmlns",
                            WSDDConstants.WSDD_NS);
            doc.appendChild(el);
            try {
                dep = new WSDDDeployment(el);
            } catch (WSDDException e) {
                return null;
            }
        }

        return doc;
    }

    /**
     *
     * @param document XXX
     */
    public void setDocument(Document document)
    {
        doc = document;

        dep = null;
    }

    /**
     * Remove both the DOM and WSDD deployment
     */
    public void removeDeployment()
    {
        if (null == dep) {
            return;
        }

        Element e = dep.getElement();

        e.getParentNode().removeChild(e);

        dep = null;
    }

    /**
     *
     * @param registry XXX
     * @throws DeploymentException XXX
     */
    public void deploy(DeploymentRegistry registry)
        throws DeploymentException
    {
        getDeployment();

        WSDDGlobalConfiguration global = dep.getGlobalConfiguration();

        if (global != null) {
            registry.setGlobalConfiguration(global);
        }

        WSDDHandler[]     handlers   = dep.getHandlers();
        WSDDChain[]       chains     = dep.getChains();
        WSDDTransport[]   transports = dep.getTransports();
        WSDDService[]     services   = dep.getServices();
        WSDDTypeMapping[] mappings   = dep.getTypeMappings();

        for (int n = 0; n < handlers.length; n++) {
            registry.deployHandler(handlers[n]);
        }

        for (int n = 0; n < chains.length; n++) {
            registry.deployHandler(chains[n]);
        }

        for (int n = 0; n < transports.length; n++) {
            registry.deployTransport(transports[n]);
        }

        for (int n = 0; n < services.length; n++) {
            registry.deployService(services[n]);
        }

        for (int n = 0; n < mappings.length; n++) {
            WSDDTypeMapping     mapping = mappings[n];
            deployMappingToRegistry(mapping, registry);
        }
    }

    public static void deployMappingToRegistry(WSDDTypeMapping mapping, 
                                               DeploymentRegistry registry) 
            throws DeploymentException {
        TypeMappingRegistry tmr     =
            registry.getTypeMappingRegistry(mapping.getEncodingStyle());

        if (tmr == null) {
            tmr = new SOAPTypeMappingRegistry();

            registry.addTypeMappingRegistry(mapping.getEncodingStyle(),
                                            tmr);
        }

        Serializer          ser   = null;
        DeserializerFactory deser = null;

        try {
            ser   = (Serializer) mapping.getSerializer().newInstance();
            deser =
                (DeserializerFactory) mapping.getDeserializer()
                    .newInstance();

            if (ser != null) {
                tmr.addSerializer(mapping.getLanguageSpecificType(),
                                  mapping.getQName(), ser);
            }

            if (deser != null) {
                tmr.addDeserializerFactory(mapping.getQName(), mapping
                    .getLanguageSpecificType(), deser);
            }
        }
        catch (Exception e) {
            throw new DeploymentException(e.getMessage());
        }
    }

    public void importItem(DeployableItem item) throws DeploymentException {
        if (!(item instanceof WSDDElement))
            return;
/*
            throw new DeploymentException("Importing non-WSDD item " +
                                          item.getClass().getName() +
                                          " into WSDD document!");
*/
        
        WSDDElement elem = (WSDDElement)item;
        
        // Don't bother importing if we own it already.
        if (elem.getElement().getOwnerDocument().equals(getDOMDocument()))
            return;
        
        getDeployment().addChild((WSDDElement)item);
    }
}
