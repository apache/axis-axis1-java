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
package org.apache.axis.deployment.wsdd.providers;

import org.apache.axis.Handler;
import org.apache.axis.deployment.DeploymentException;
import org.apache.axis.deployment.DeploymentRegistry;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.deployment.wsdd.WSDDException;
import org.apache.axis.deployment.wsdd.WSDDProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * This is a simple provider for using Handler-based services which don't
 * need further configuration (such as Java classes, etc).
 * 
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class WSDDHandlerProvider
    extends WSDDProvider
{
    /**
     *
     * Wrap an extant DOM element in WSDD
     *
     * @param e (Element) XXX
     * @throws WSDDException XXX
     */
    public WSDDHandlerProvider(Element e)
        throws WSDDException
    {
        super(e);
    }

    /**
     *
     * Create a new DOM element and wrap in WSDD
     *
     * @param d (Document) XXX
     * @param n (Node) XXX
     * @throws WSDDException XXX
     */
    public WSDDHandlerProvider(Document d, Node n)
        throws WSDDException
    {
        super(d, n);

		Element specificProvider =
			d.createElementNS(WSDDConstants.WSDD_HANDLER, "handler:provider");
		getElement().appendChild(specificProvider);
    }

	protected Element getProviderElement()
		throws WSDDException
	{
		Element prov =
		    (Element) getElement()
		        .getElementsByTagNameNS(WSDDConstants.WSDD_HANDLER, "provider")
		        .item(0);

		if (prov == null) {
		    throw new WSDDException(
		        "The Handler Provider requires the presence of a handler:provider element in the WSDD");
		}

		return prov;
	}

    /**
     *
     * @param registry XXX
     * @return XXX
     * @throws Exception XXX
     */
    public Handler newProviderInstance(DeploymentRegistry registry)
        throws Exception
    {
        Class _class = getJavaClass();

        if (_class == null) {
            throw new DeploymentException("No class specified!");
        }
        
        if (!(Handler.class.isAssignableFrom(_class))) {
            throw new DeploymentException("Class " + _class.getName() +
                " is not a Handler!");
        }

        return (Handler)_class.newInstance();
    }
}
