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
import org.apache.axis.deployment.DeploymentRegistry;
import org.apache.axis.deployment.wsdd.providers.WSDDBsfProvider;
import org.apache.axis.deployment.wsdd.providers.WSDDComProvider;
import org.apache.axis.deployment.wsdd.providers.WSDDJavaProvider;
import org.apache.axis.deployment.wsdd.providers.WSDDHandlerProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Hashtable;


/**
 * WSDD provider element
 *
 * Represents the liason to the application being exposed
 * as a Web Service.
 *
 * Specific provider extension classes must be registered
 * by namespace URI.
 *
 * @author James Snell
 */
public abstract class WSDDProvider
    extends WSDDDeployableItem
{

// ** STATIC PROVIDER REGISTRY ** //

    /** XXX */
    private static Hashtable providers = new Hashtable();

    static {
        providers.put(WSDDConstants.WSDD_JAVA, WSDDJavaProvider.class);
        providers.put(WSDDConstants.WSDD_COM, WSDDComProvider.class);
        providers.put(WSDDConstants.WSDD_BSF, WSDDBsfProvider.class);
        providers.put(WSDDConstants.WSDD_HANDLER, WSDDHandlerProvider.class);
    }

    /**
     *
     * @param uri XXX
     * @param _class XXX
     */
    public static void registerProvider(String uri, Class _class)
    {
        providers.put(uri, _class);
    }

    /**
     *
     * @param uri XXX
     * @return XXX
     */
    public static Class getProviderClass(String uri)
    {
        return (Class) providers.get(uri);
    }

    /**
     *
     * @param uri XXX
     * @return XXX
     */
    public static boolean hasProviderClass(String uri)
    {
        return providers.containsKey(uri);
    }

////////////////////////////////////

    /**
     * Wrap an extant DOM element in WSDD
	 *
     * @param e (Element) XXX
     * @throws WSDDException XXX
     */
    public WSDDProvider(Element e)
        throws WSDDException
    {
        super(e, "provider");
    }

    /**
     *
     * Create a new DOM element and wrap in WSDD
     *
     * @param d (Document) XXX
     * @param n (Node) XXX
     * @throws WSDDException XXX
     */
    public WSDDProvider(Document d, Node n)
        throws WSDDException
    {
        super(d, n, "provider");
    }

    abstract protected Element getProviderElement()
    	throws WSDDException;

	/**
	 *
	 * @param name XXX
	 * @return XXX
	 */
	public String getProviderAttribute(String name)
		throws WSDDException
	{
		Element element = getProviderElement();

	    return element.getAttribute(name);
	}

	/**
	 *
	 * @param name XXX
	 * @param value XXX
	 */
	public void setProviderAttribute(String name, String value)
		throws WSDDException
	{
		Element element = getProviderElement();

	    try {
	        element.setAttribute(name, value);
	    }
	    catch (Exception e) {

	        throw new WSDDException(e);
	    }
	}


    /**
     *
     * @return XXX
     */
    public WSDDOperation[] getOperations()
    {
        WSDDElement[]   e = createArray("operation", WSDDOperation.class);
        WSDDOperation[] t = new WSDDOperation[e.length];

        System.arraycopy(e, 0, t, 0, e.length);

        return t;
    }

    /**
     *
     * @param name XXX
     * @return XXX
     */
    public WSDDOperation getOperation(String name)
    {
        WSDDOperation[] t = getOperations();

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
    public WSDDOperation createOperation()
    {
        return (WSDDOperation) createChild(WSDDOperation.class);
    }

    /**
     *
     */
    public void removeOperation(WSDDOperation victim)
    {
        removeChild(victim);
    }

    /**
     *
     * @param registry XXX
     * @return XXX
     * @throws Exception XXX
     */
    public Handler getInstance(DeploymentRegistry registry)
        throws Exception
    {
        return newProviderInstance(registry);
    }

    /**
     *
     * @param registry XXX
     * @return XXX
     * @throws Exception XXX
     */
    public abstract Handler newProviderInstance(DeploymentRegistry registry)
        throws Exception;
}
