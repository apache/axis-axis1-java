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

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * WSDD deployment element
 *
 * @author James Snell
 */
public class WSDDDeployment
    extends WSDDElement
{

    /**
     * Create an element in WSDD that wraps an extant DOM element
     * @param e (Element) XXX
     * @throws WSDDException XXX
     */
    public WSDDDeployment(Element e)
        throws WSDDException
    {
        super(e, "deployment");
    }

    /**
     * Create a new element in DOM and wrap it in WSDD
     *     This ctor is different from those of similar classes in part
     *       because Deployment is the entry-node into the DOM Document
     * @param doc (Document) XXX
     * @throws WSDDException XXX
     */
    public WSDDDeployment(Document doc)
        throws WSDDException
    {
        super(doc, doc, "deployment");
    }

    /**
     *
     * @return XXX
     */
    public String getName()
    {
        return getAttribute("name");
    }

    /**
     *
     * @param name XXX
     */
    public void setName(String name)
    {
        setAttribute("name", name);
    }

    /**
     *
	 * Convenience method to return just the first one.
	 *
     * @return XXX
     */
    public WSDDGlobalConfiguration getGlobalConfiguration()
    {

        WSDDElement[] e = createArray("globalConfiguration",
                                      WSDDGlobalConfiguration.class);

        if (e.length == 0) {
            return null;
        }

        return (WSDDGlobalConfiguration) e[0];
    }

    /**
     *
     * @return XXX
     */
    public WSDDGlobalConfiguration[] getGlobalConfigurations()
    {

        WSDDElement[]     e = createArray("globalConfiguration",
                                          WSDDGlobalConfiguration.class);
        WSDDGlobalConfiguration[] t = new WSDDGlobalConfiguration[e.length];

        System.arraycopy(e, 0, t, 0, e.length);

        return t;
    }

    /**
     *
     * @param name XXX
     * @return XXX
     */
    public WSDDGlobalConfiguration getGlobalConfiguration(String name)
    {

        WSDDGlobalConfiguration[] t = getGlobalConfigurations();

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
    public WSDDGlobalConfiguration createGlobalConfiguration()
    {
        return (WSDDGlobalConfiguration) createChild(WSDDGlobalConfiguration.class);
    }

    /**
     *
     */
    public void removeGlobalConfiguration(WSDDGlobalConfiguration victim)
    {
        removeChild(victim);
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
    public WSDDChain[] getChains()
    {

        WSDDElement[] e = createArray("chain", WSDDChain.class);
        WSDDChain[]   c = new WSDDChain[e.length];

        System.arraycopy(e, 0, c, 0, e.length);

        return c;
    }

    /**
     *
     * @param name XXX
     * @return XXX
     */
    public WSDDChain getChain(String name)
    {

        WSDDChain[] c = getChains();

        for (int n = 0; n < c.length; n++) {
            if (c[n].getName().equals(name)) {
                return c[n];
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
    public WSDDChain createChain()
    {
        return (WSDDChain) createChild(WSDDChain.class);
    }

    /**
     *
     */
    public void removeChain(WSDDChain victim)
    {
        removeChild(victim);
    }

    /**
     *
     * @return XXX
     */
    public WSDDHandler[] getHandlers()
    {

        WSDDElement[] e = createArray("handler", WSDDHandler.class);
        WSDDHandler[] h = new WSDDHandler[e.length];

        System.arraycopy(e, 0, h, 0, e.length);

        return h;
    }

    /**
     *
     * @param name XXX
     * @return XXX
     */
    public WSDDHandler getHandler(String name)
    {

        WSDDHandler[] h = getHandlers();

        for (int n = 0; n < h.length; n++) {
            if (h[n].getName().equals(name)) {
                return h[n];
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
    public WSDDHandler createHandler()
    {
        return (WSDDHandler) createChild(WSDDHandler.class);
    }

    /**
     *
     */
    public void removeHandler(WSDDHandler victim)
    {
        removeChild(victim);
    }

    /**
     *
     * @return XXX
     */
    public WSDDTransport[] getTransports()
    {

        WSDDElement[]   e = createArray("transport", WSDDTransport.class);
        WSDDTransport[] t = new WSDDTransport[e.length];

        System.arraycopy(e, 0, t, 0, e.length);

        return t;
    }

    /**
     *
     * @param name XXX
     * @return XXX
     */
    public WSDDTransport getTransport(String name)
    {

        WSDDTransport[] t = getTransports();

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
    public WSDDTransport createTransport()
    {
        return (WSDDTransport) createChild(WSDDTransport.class);
    }

    /**
     *
     */
    public void removeTransport(WSDDTransport victim)
    {
        removeChild(victim);
    }

    /**
     *
     * @return XXX
     */
    public WSDDService[] getServices()
    {

        WSDDElement[] e = createArray("service", WSDDService.class);
        WSDDService[] s = new WSDDService[e.length];

        System.arraycopy(e, 0, s, 0, e.length);

        return s;
    }

    /**
     *
     * @param name XXX
     * @return XXX
     */
    public WSDDService getService(String name)
    {

        WSDDService[] s = getServices();

        for (int n = 0; n < s.length; n++) {
            if (s[n].getName().equals(name)) {
                return s[n];
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
    public WSDDService createService()
    {
        return (WSDDService) createChild(WSDDService.class);
    }

    /**
     *
     */
    public void removeService(WSDDService victim)
    {
        removeChild(victim);
    }

}
