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
package org.apache.axis.deployment.wsdd;

import org.apache.axis.ConfigurationException;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.SerializationContextImpl;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;


/**
 * represents a WSDD Document (this is the top level object in this object model)
 * Only one of {@link #deployment} and {@link #undeployment} should be valid.
 */
public class WSDDDocument extends WSDDConstants
{
    protected static Log log =
        LogFactory.getLog(WSDDDocument.class.getName());

    /** owner doc */
    private Document doc;

    /**
     *  deployment tree. may be null
     */
    private WSDDDeployment deployment;
    /** undeployment tree. may be null */
    private WSDDUndeployment undeployment;

    /**
     * empty constructor
     */
    public WSDDDocument()
    {
    }

    /**
     * create and bind to a document
     * @param document (Document) XXX
     */
    public WSDDDocument(Document document) throws WSDDException
    {
        setDocument(document);
    }

    /**
     * bind to a sub-element in a document.
     * @param e (Element) XXX
     */
    public WSDDDocument(Element e) throws WSDDException
    {
        doc = e.getOwnerDocument();
        if (ELEM_WSDD_UNDEPLOY.equals(e.getLocalName())) {
            undeployment = new WSDDUndeployment(e);
        } else {
            deployment = new WSDDDeployment(e);
        }
    }

    /**
     * Get the deployment. If there is no deployment, create an empty one
     * @return the deployment document
     */
    public WSDDDeployment getDeployment()
    {
        if (deployment == null) {
            deployment = new WSDDDeployment();
        }
        return deployment;
    }

    /**
     * get the deployment as a DOM.
     * Requires that the deployment member variable is not null.
     * @return
     * @throws ConfigurationException
     */
    public Document getDOMDocument() throws ConfigurationException {
        StringWriter writer = new StringWriter();
        SerializationContext context = new SerializationContextImpl(writer, null);
        context.setPretty(true);
        try {
            deployment.writeToContext(context);
        } catch (Exception e) {
            log.error(Messages.getMessage("exception00"), e);
        }
        try {
            writer.close();
            return XMLUtils.newDocument(new InputSource(new StringReader(writer.getBuffer().toString())));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * write the deployment to the supplied serialization context.
     * @param context
     * @throws IOException
     */
    public void writeToContext(SerializationContext context)
        throws IOException
    {
        getDeployment().writeToContext(context);
    }

    /**
     * Bind to a new document, setting the undeployment nodes if it is an undeployment,
     * the deployment tree if it is anything else.
     * @param document XXX
     */
    public void setDocument(Document document) throws WSDDException {
        this.doc = document;
        Element docEl = doc.getDocumentElement();
        if (ELEM_WSDD_UNDEPLOY.equals(docEl.getLocalName())) {
            undeployment = new WSDDUndeployment(docEl);
        } else {
            deployment = new WSDDDeployment(docEl);
        }
    }

    /**
     * do a deploy and/or undeploy, depending on what is in the document.
     * If both trees are set, then undeploy follows deploy.
     * @param registry
     * @throws ConfigurationException
     */
    public void deploy(WSDDDeployment registry) throws ConfigurationException {
        if (deployment != null) {
            deployment.deployToRegistry(registry);
        }
        if (undeployment != null) {
            undeployment.undeployFromRegistry(registry);
        }
    }
}
