/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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

import java.net.URL;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.apache.axis.Handler;
import org.apache.axis.Chain;
import org.apache.axis.TargetedChain;
import org.apache.axis.deployment.DeploymentRegistry;
import org.apache.axis.deployment.DeployableItem;
import org.apache.axis.description.ServiceDescription;

public class WSDDService extends WSDDDeployableItem implements DeployableItem { 
    
    public WSDDService(Element e) throws WSDDException { super(e, "service"); }
   
    public String getType() {
        String type = super.getType();
        if (type.equals(""))
            type = "java:org.apache.axis.SimpleTargetedChain";
        return type;
    }
    
    public String getServiceDescriptionURL() {
        return getElement().getAttribute("description");
    }
    
    /**
     * Eventually need to fill this in with the code to 
     * actually return the Service Description object
     */
    public ServiceDescription getServiceDescription() {
        return null;
    }
    
    public WSDDTypeMapping[] getTypeMappings() {
        WSDDElement[] e = createArray("typeMapping", WSDDTypeMapping.class);
        WSDDTypeMapping[] t = new WSDDTypeMapping[e.length];
        System.arraycopy(e,0,t,0,e.length);
        return t;
    }
    
    public WSDDTypeMapping getTypeMapping(String name) {
        WSDDTypeMapping[] t = getTypeMappings();
        for (int n = 0; n < t.length; n++) {
            if (t[n].getName().equals(name))
                return t[n];
        }
        return null;
    }
    
    public WSDDRequestFlow getRequestFlow() {
        WSDDElement[] e = createArray("requestFlow", WSDDRequestFlow.class);
        if (e.length != 0) {
            return (WSDDRequestFlow)e[0];
        }
        return null;
    }
    
    public WSDDProvider getProvider() throws Exception {
        NodeList nl = element.getElementsByTagNameNS(WSDDConstants.WSDD_NS, "provider");
        Element e = (Element)nl.item(0);
        Node ex = null;
        ex = e.getFirstChild();
        while (ex != null) {
            if (ex.getNodeType() == Element.ELEMENT_NODE)
                if (ex.getLocalName().equals("provider")) break;
            ex = ex.getNextSibling();
        }
        if (ex == null) {
            return null;
        } else {
            if (hasChild(e)) {
                return (WSDDProvider)getChild(e);
            } else {
                String exuri = ex.getNamespaceURI();
                Class c = WSDDProvider.getProviderClass(exuri);
                Class[] cs = {Element.class};
                Object[] p = {e};
                WSDDElement w = (WSDDElement)c.getConstructor(cs).newInstance(p);
                addChild(e,w);
                return (WSDDProvider)w;
            }
        }
    }
    
    public WSDDResponseFlow getResponseFlow() {
        WSDDElement[] e = createArray("responseFlow", WSDDResponseFlow.class);
        if (e.length != 0) {
            return (WSDDResponseFlow)e[0];
        }
        return null;
    }
    
    public WSDDFaultFlow[] getFaultFlows() {
        WSDDElement[] e = createArray("faultFlow", WSDDFaultFlow.class);
        WSDDFaultFlow[] t = new WSDDFaultFlow[e.length];
        System.arraycopy(e,0,t,0,e.length);
        return t;
    }
    
    public WSDDFaultFlow getFaultFlow(String name) {
        WSDDFaultFlow[] t = getFaultFlows();
        for (int n = 0; n < t.length; n++) {
            if (t[n].getName().equals(name))
                return t[n];
        } 
        return null;
    }
   
    public Handler newInstance(DeploymentRegistry registry) throws Exception {
        Handler h = super.makeNewInstance(registry);
        TargetedChain c = (TargetedChain)h;
        c.setInputChain((Chain)getRequestFlow().newInstance(registry));
        c.setPivotHandler(getProvider().newInstance(registry));
        c.setOutputChain((Chain)getResponseFlow().newInstance(registry));
        return c;
    }
    
}
