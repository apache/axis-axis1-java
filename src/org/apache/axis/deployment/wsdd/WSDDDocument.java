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
 */
public class WSDDDocument extends WSDDConstants
{
    protected static Log log =
        LogFactory.getLog(WSDDDocument.class.getName());

    private Document doc;

    private WSDDDeployment deployment;
    private WSDDUndeployment undeployment;

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
    public WSDDDocument(Document document) throws WSDDException
    {
        setDocument(document);
    }

    /**
     *
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
     *
     * @return XXX
     */
    public WSDDDeployment getDeployment()
    {
        if (deployment == null)
            deployment = new WSDDDeployment();
        return deployment;
    }

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

    public void writeToContext(SerializationContext context)
        throws IOException
    {
        getDeployment().writeToContext(context);
    }

    /**
     *
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

    public void deploy(WSDDDeployment registry) throws ConfigurationException {
        if (deployment != null) {
            deployment.deployToRegistry(registry);
        }
        if (undeployment != null) {
            undeployment.undeployFromRegistry(registry);
        }
    }
}
