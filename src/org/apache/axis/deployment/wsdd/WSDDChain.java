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

import org.apache.axis.Chain;
import org.apache.axis.Handler;
import org.apache.axis.deployment.DeploymentRegistry;
import org.apache.axis.utils.QName;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * WSDD chain element
 *
 */
public class WSDDChain
    extends WSDDHandler
{
    public static final QName DEFAULT_QNAME =
            new QName(WSDDConstants.WSDD_JAVA, "org.apache.axis.SimpleChain");
    /**
     *
     * @param e (Element) XXX
     * @throws WSDDException XXX
     */
    public WSDDChain(Element e)
        throws WSDDException
    {
        super(e, "chain");
    }

    /**
     *
     * @param d (Document) XXX
     * @param n (Node) XXX
     * @throws WSDDException XXX
     */
    public WSDDChain(Document d, Node n)
        throws WSDDException
    {
        super(d, n, "chain");
    }

    /**
     *
     * @return XXX
     */
    public WSDDHandler[] getHandlers()
    {

        WSDDElement[] w = createArray("handler", WSDDHandler.class);
        WSDDHandler[] h = new WSDDHandler[w.length];

        System.arraycopy(w, 0, h, 0, w.length);

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
    public QName getType()
    {
        QName type = super.getType();

        if (type == null) {
            type = DEFAULT_QNAME;
        }

        return type;
    }

    /**
     * Creates a new instance of this Chain
     * @param registry XXX
     * @return XXX
     * @throws Exception XXX
     */
    public Handler newInstance(DeploymentRegistry registry)
        throws Exception
    {

        try {
            Handler       h        = super.newInstance(registry);
            Chain         c        = (Chain) h;
            WSDDHandler[] handlers = getHandlers();

            for (int n = 0; n < handlers.length; n++) {
                c.addHandler(handlers[n].newInstance(registry));
            }

            return c;
        }
        catch (Exception e) {
            return null;
        }
    }
}
