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

import org.apache.axis.ConfigurationException;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.JavaUtils;
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

        return new org.apache.axis.SimpleTargetedChain(reqHandler, pivot,
                                                       respHandler);
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
