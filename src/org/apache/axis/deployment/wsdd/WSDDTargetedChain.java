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
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.io.IOException;


/**
 *
 */
public abstract class WSDDTargetedChain
    extends WSDDDeployableItem
{
    private WSDDRequestFlow requestFlow;
    private WSDDResponseFlow responseFlow;
    private QName pivotQName;
    
    protected WSDDTargetedChain()
    {
    }

    /**
     *
     * @param e (Element) XXX
     * @throws WSDDException XXX
     */
    protected WSDDTargetedChain(Element e)
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
        
        // !!! pivot? use polymorphic method?
        String pivotStr = e.getAttribute(ATTR_PIVOT);
        if (pivotStr != null && !pivotStr.equals(""))
            pivotQName = XMLUtils.getQNameFromString(pivotStr, e);
        
    }

    public WSDDRequestFlow getRequestFlow()
    {
        return requestFlow;
    }
    
    public void setRequestFlow(WSDDRequestFlow flow)
    {
        requestFlow = flow;
    }

    public WSDDResponseFlow getResponseFlow()
    {
        return responseFlow;
    }
    
    public void setResponseFlow(WSDDResponseFlow flow)
    {
        responseFlow = flow;
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
     * @param type XXX
     */
    public void setType(String type) throws WSDDException
    {
        throw new WSDDException(Messages.getMessage(
                "noTypeSetting", getElementName().getLocalPart()));
    }
    
    public QName getPivotQName()
    {
        return pivotQName;
    }

    public void setPivotQName(QName pivotQName) {
        this.pivotQName = pivotQName;
    }

    /**
     *
     * @param pivot XXX
     * @param registry XXX
     * @return XXX
     * @throws ConfigurationException XXX
     */
    public Handler makeNewInstance(EngineConfiguration registry)
        throws ConfigurationException
    {
        Handler reqHandler = null;

        WSDDChain req = getRequestFlow();
        if (req != null)
            reqHandler = req.getInstance(registry);
        
        Handler pivot = null;
        if (pivotQName != null) {
            if (URI_WSDD_JAVA.equals(pivotQName.getNamespaceURI())) {
                try {
                    pivot = (Handler)ClassUtils.forName(pivotQName.getLocalPart()).newInstance();
                } catch (InstantiationException e) {
                    throw new ConfigurationException(e);
                } catch (IllegalAccessException e) {
                    throw new ConfigurationException(e);
                } catch (ClassNotFoundException e) {
                    throw new ConfigurationException(e);
                }
            } else {
                pivot = registry.getHandler(pivotQName);
            }
        }
        
        Handler respHandler = null;
        WSDDChain resp = getResponseFlow();
        if (resp != null)
            respHandler = resp.getInstance(registry);

        Handler retVal = new org.apache.axis.SimpleTargetedChain(reqHandler, pivot,
                                                       respHandler);
        retVal.setOptions(getParametersTable());
        return retVal;
    }

    /**
     * Write this element out to a SerializationContext
     */
    public final void writeFlowsToContext(SerializationContext context)
            throws IOException {
        if (requestFlow != null) {
            requestFlow.writeToContext(context);
        }
        if (responseFlow != null) {
            responseFlow.writeToContext(context);
        }
        
    }
    
    public void deployToRegistry(WSDDDeployment registry)
    {
        // deploy any named subparts
        if (requestFlow != null) {
            requestFlow.deployToRegistry(registry);
        }
        
        if (responseFlow != null) {
            responseFlow.deployToRegistry(registry);
        }
    }
}
