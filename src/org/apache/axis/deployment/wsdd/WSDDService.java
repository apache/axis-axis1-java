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

import org.apache.axis.Handler;
import org.apache.axis.TargetedChain;
import org.apache.axis.deployment.DeploymentRegistry;
import org.apache.axis.description.ServiceDescription;
import org.apache.axis.utils.QName;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 *
 */
public class WSDDService
    extends WSDDDeployableItem
{
    public static final QName DEFAULT_QNAME =
            new QName(WSDDConstants.WSDD_JAVA,
                      "org.apache.axis.SimpleTargetedChain");

    /**
     *
     * @param e (Element) XXX
     * @throws WSDDException XXX
     */
    public WSDDService(Element e)
        throws WSDDException
    {
        super(e, "service");
    }

    /**
     *
     * @param d (Document) XXX
     * @param n (Node) XXX
     * @throws WSDDException XXX
     */
    public WSDDService(Document d, Node n)
        throws WSDDException
    {
        super(d, n, "service");
    }

    /**
     *
     * @return XXX
     */
    public QName getType()
    {
        QName type = super.getType();

        if (type == null) {
            type = DEFAULT_QNAME;
        }

        return type;
    }

    /**
     *
     * @return XXX
     */
    public String getServiceDescriptionURL()
    {
        return getAttribute("description");
    }

    /**
     *
     * @param sdUrl XXX
     */
    public void setServiceDescriptionURL(String sdUrl)
    {
        setAttribute("description", sdUrl);
    }

    /**
     * Eventually need to fill this in with the code to
     * actually return the Service Description object
     * @return XXX
     */
    public ServiceDescription getServiceDescription()
    {
		// Nothing here yet
        return null;
    }

    /**
     *
     * @return XXX
     */
    public WSDDTypeMapping[] getTypeMappings()
    {
        WSDDElement[]     e = createArray("typeMapping",
                                          WSDDTypeMapping.class);
        WSDDTypeMapping[] t = new WSDDTypeMapping[e.length];

        System.arraycopy(e, 0, t, 0, e.length);

        return t;
    }

    /**
     *
     * @param name XXX
     * @return XXX
     */
    public WSDDTypeMapping getTypeMapping(String name)
    {
        WSDDTypeMapping[] t = getTypeMappings();

        for (int n = 0; n < t.length; n++) {
            if (t[n].getName().equals(name)) {
                return t[n];
            }
        }

        return null;
    }

    /**
     *
     * @param name XXX
     * @return the newly created / tree-ified item,
	 *          so that the caller might mutate it
     */
    public WSDDTypeMapping createTypeMapping()
    {
        return (WSDDTypeMapping) createChild(WSDDTypeMapping.class);
    }

    /**
     *
     */
    public void removeTypeMapping(WSDDTypeMapping victim)
    {
        removeChild(victim);
    }

    /**
     *
     * @return XXX
     */
    public WSDDRequestFlow getRequestFlow()
    {
        WSDDElement[] e = createArray("requestFlow", WSDDRequestFlow.class);

        if (e.length != 0) {
            return (WSDDRequestFlow) e[0];
        }

        return null;
    }

    /**
     *
     * @return the newly created / tree-ified item,
	 *          so that the caller might mutate it
     */
    public WSDDRequestFlow createRequestFlow()
    {
        removeRequestFlow();

        return (WSDDRequestFlow) createChild(WSDDRequestFlow.class);
    }

    /**
     *
     */
    public void removeRequestFlow()
    {
        removeChild(getRequestFlow());
    }

    /**
     *
     * @return XXX
     */
    public WSDDProvider getProvider()
    {
        NodeList nl =
            getElement().getElementsByTagNameNS(WSDDConstants.WSDD_NS,
                                                "provider");
        Element  e  = (Element) nl.item(0);

		if (null == e) return null;

        Node     ex = null;

        ex = e.getFirstChild();

        while (ex != null) {
            if (ex.getNodeType() == Element.ELEMENT_NODE) {
                if (ex.getLocalName().equals("provider")) {
                    break;
                }
            }

            ex = ex.getNextSibling();
        }

        if (ex == null) {
            return null;
        }
        else {
            if (hasChild(e)) {
                return (WSDDProvider) getChild(e);
            }
            else {
				try
				{
	                String      exuri = ex.getNamespaceURI();
	                Class       c     = WSDDProvider.getProviderClass(exuri);
	                Class[]     cs    = { Element.class };
	                Object[]    p     = { e };
	                WSDDElement w     =
	                    (WSDDElement) c.getConstructor(cs).newInstance(p);

	                addChild(w);

	                return (WSDDProvider) w;
				}
				catch (Exception exception)
				{
					return null;
				}
            }
        }
    }

    /**
     *
     * @return the newly created / tree-ified item,
	 *          so that the caller might mutate it
     */
    public WSDDProvider createProvider(Class providerSubclass)
    {
        removeProvider();

        return (WSDDProvider) createChild(providerSubclass);
    }

    /**
     *
     */
    public void removeProvider()
    {
        removeChild(getProvider());
    }

    /**
     *
     * @return XXX
     */
    public WSDDResponseFlow getResponseFlow()
    {
        WSDDElement[] e = createArray("responseFlow", WSDDResponseFlow.class);

        if (e.length != 0) {
            return (WSDDResponseFlow) e[0];
        }

        return null;
    }

    /**
     *
     * @return the newly created / tree-ified item,
	 *          so that the caller might mutate it
     */
    public WSDDResponseFlow createResponseFlow()
    {
        removeResponseFlow();

        return (WSDDResponseFlow) createChild(WSDDResponseFlow.class);
    }

    /**
     *
     */
    public void removeResponseFlow()
    {
        removeChild(getResponseFlow());
    }

    /**
     *
     * @return XXX
     */
    public WSDDFaultFlow[] getFaultFlows()
    {
        WSDDElement[]   e = createArray("faultFlow", WSDDFaultFlow.class);
        WSDDFaultFlow[] t = new WSDDFaultFlow[e.length];

        System.arraycopy(e, 0, t, 0, e.length);

        return t;
    }

    /**
     *
     * @param name XXX
     * @return XXX
     */
    public WSDDFaultFlow getFaultFlow(String name)
    {
        WSDDFaultFlow[] t = getFaultFlows();

        for (int n = 0; n < t.length; n++) {
            if (t[n].getName().equals(name)) {
                return t[n];
            }
        }

        return null;
    }

    /**
     *
     * @param name XXX
     * @return the newly created / tree-ified item,
	 *          so that the caller might mutate it
     */
    public WSDDFaultFlow createFaultFlow()
    {
        return (WSDDFaultFlow) createChild(WSDDFaultFlow.class);
    }

    /**
     *
     */
    public void removeFaultFlow(WSDDFaultFlow victim)
    {
        removeChild(victim);
    }

    /**
     *
     * @param registry XXX
     * @return XXX
     * @throws Exception XXX
     */
    public Handler newInstance(DeploymentRegistry registry)
        throws Exception
    {

        try {
            Handler       h        = super.makeNewInstance(registry);
            TargetedChain c        = (TargetedChain) h;
            WSDDFlow      request  = getRequestFlow();
            WSDDFlow      response = getResponseFlow();

            if (request != null) {
                c.setRequestHandler(request.newInstance(registry));
            }

            c.setPivotHandler(getProvider().newInstance(registry));

            if (response != null) {
                c.setResponseHandler(response.newInstance(registry));
            }

            return c;
        }
        catch (Exception e) {
            return null;
        }
    }
}
