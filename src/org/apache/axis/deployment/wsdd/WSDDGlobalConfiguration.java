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
import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;


/**
 * Represents the global configuration of the Axis engine.
 *
 * @author James Snell
 */
public class WSDDGlobalConfiguration
    extends WSDDDeployableItem
{
    private WSDDRequestFlow requestFlow;
    private WSDDResponseFlow responseFlow;
    private ArrayList roles = new ArrayList();

    /**
     * Default constructor
     */ 
    public WSDDGlobalConfiguration()
    {
    }
    
    /**
     *
     * @param e (Element) XXX
     * @throws WSDDException XXX
     */
    public WSDDGlobalConfiguration(Element e)
        throws WSDDException
    {
        super(e);
        Element reqEl = getChildElement(e, ELEM_WSDD_REQFLOW);
        if (reqEl != null && reqEl.getElementsByTagName("*").getLength()>0) {
            requestFlow = new WSDDRequestFlow(reqEl);
        }
        Element respEl = getChildElement(e, ELEM_WSDD_RESPFLOW);
        if (respEl != null && respEl.getElementsByTagName("*").getLength()>0) {
            responseFlow = new WSDDResponseFlow(respEl);
        }

        Element [] roleElements = getChildElements(e, ELEM_WSDD_ROLE);
        for (int i = 0; i < roleElements.length; i++) {
            String role = XMLUtils.getChildCharacterData(roleElements[i]);
            roles.add(role);
        }
    }
    
    protected QName getElementName()
    {
        return WSDDConstants.QNAME_GLOBAL;
    }

    /**
     * Get our request flow 
     */
    public WSDDRequestFlow getRequestFlow()
    {
        return requestFlow;
    }
    
    /**
     * Set our request flow
     */ 
    public void setRequestFlow(WSDDRequestFlow reqFlow)
    {
        requestFlow = reqFlow;
    }

    /**
     * Get our response flow
     */
    public WSDDResponseFlow getResponseFlow()
    {
        return responseFlow;
    }
    
    /**
     * Set the response flow
     */
    public void setResponseFlow(WSDDResponseFlow responseFlow) {
        this.responseFlow = responseFlow;
    }

    /**
     *
     * @return XXX
     */
    public WSDDFaultFlow[] getFaultFlows()
    {
        return null;
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
     * @return XXX
     */
    public QName getType()
    {
        return null;
    }

    /**
     *
     * @param type XXX
     */
    public void setType(String type) throws WSDDException
    {
        throw new WSDDException(Messages.getMessage("noTypeOnGlobalConfig00"));
    }



    /**
     *
     * @param registry XXX
     * @return XXX
     */
    public Handler makeNewInstance(EngineConfiguration registry)
    {
        return null;
    }

    /**
     * Write this element out to a SerializationContext
     */
    public void writeToContext(SerializationContext context)
            throws IOException {
        context.startElement(QNAME_GLOBAL, null);
        writeParamsToContext(context);
        if (requestFlow != null)
            requestFlow.writeToContext(context);
        if (responseFlow != null)
            responseFlow.writeToContext(context);
        context.endElement();
    }

    public void deployToRegistry(WSDDDeployment registry)
            throws ConfigurationException {
        if (requestFlow != null)
            requestFlow.deployToRegistry(registry);
        if (responseFlow != null)
            responseFlow.deployToRegistry(registry);
    }

    public List getRoles() {
        return (List)roles.clone();
    }
}

