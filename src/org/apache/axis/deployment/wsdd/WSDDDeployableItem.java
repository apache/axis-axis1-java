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
import org.apache.axis.deployment.DeployableItem;
import javax.xml.rpc.namespace.QName;
import org.apache.axis.deployment.DeploymentRegistry;
import org.apache.axis.utils.LockableHashtable;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * WSDD DeployableItem complexType
 *
 */
public abstract class WSDDDeployableItem
    extends WSDDElement
    implements DeployableItem
{
    /** XXX */
    LockableHashtable parms;

    /** XXX */
    QName qname;

    /**
     *
     * @param e (Element) XXX
     * @param n (String) XXX
     * @throws WSDDException XXX
     */
    public WSDDDeployableItem(Element e, String n)
        throws WSDDException
    {
        super(e, n);
    }

    /**
     *
     * @param d (Document) XXX
     * @param n (Node) XXX
     * @param s (String) XXX
     * @throws WSDDException XXX
     */
    public WSDDDeployableItem(Document d, Node n, String s)
        throws WSDDException
    {
        super(d, n, s);
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
     * @return XXX
     */
    public QName getQName()
    {
        if (qname == null) {
            String nsURI =
                getElement().getOwnerDocument().getDocumentElement()
                    .getAttributeNS(org.apache.axis.Constants
                        .URI_2000_SCHEMA_XSI, "targetNamespace");

            if (nsURI.equals("")) {
                qname = new QName(null, getName());
            }
            else {
                qname = new QName(nsURI, getName());
            }
        }

        return qname;
    }

    /**
     *
     * @return XXX
     */
    public QName getType()
    {
        String type = getAttribute("type");
        if (type != null && !type.equals(""))
            return XMLUtils.getQNameFromString(type, getElement());

        return null;
    }

    /**
     *
     * @param type XXX
     */
    public void setType(String type) throws WSDDException
    {
        setAttribute("type", type);
    }

    /**
     * Returns the config parameters as a hashtable (lockable)
     * @return XXX
     */
    public LockableHashtable getParametersTable()
    {
        if (parms == null) {
            parms = new LockableHashtable();

            WSDDParameter[] ps = getParameters();

            for (int n = 0; n < ps.length; n++) {
                WSDDParameter p = (WSDDParameter) ps[n];

                // parms.put(p.getName(), p.getValue(), p.getLocked());
                parms.put(p.getName(), "", p.getLocked());
            }
        }

        return parms;
    }

    /**
     *
     * @return XXX
     */
    public WSDDParameter[] getParameters()
    {
        WSDDElement[]   e = createArray("parameter", WSDDParameter.class);
        WSDDParameter[] p = new WSDDParameter[e.length];

        System.arraycopy(e, 0, p, 0, e.length);

        return p;
    }

    /**
     *
     * @param name XXX
     * @return XXX
     */
    public WSDDParameter createParameter(String name)
    {
        WSDDParameter p = (WSDDParameter) createChild(WSDDParameter.class);

        p.setName(name);

        return p;
    }

    /**
     *
     * @param name XXX
     */
    public void removeParameter(String name)
    {
        WSDDParameter p = getParameter(name);

        removeChild(p);
    }

    /**
     *
     * @param name XXX
     * @return XXX
     */
    public WSDDParameter getParameter(String name)
    {
        WSDDParameter[] e = getParameters();

        for (int n = 0; n < e.length; n++) {
            if (e[n].getName().equals(name)) {
                return e[n];
            }
        }

        return null;
    }

    /**
     *
     * @param registry XXX
     * @return XXX
     * @throws Exception XXX
     */
    abstract public Handler newInstance(DeploymentRegistry registry)
        throws Exception;

    /**
     * Creates a new instance of this deployable.  if the
     * java class is not found, the registry is queried to
     * find a suitable item
     * @param registry XXX
     * @return XXX
     * @throws Exception XXX
     */
    Handler makeNewInstance(DeploymentRegistry registry)
        throws Exception
    {
        try {
            Class   c = getJavaClass();
            Handler h = null;

            if (c != null) {
                h = (Handler)createInstance(c);
            } else {
                h = registry.getDeployedItem(getType());
            }

            if (h != null) {
                h.setOptions(getParametersTable());
            }

            return h;
        }
        catch (Exception e) {
            throw e;
        }
    }

    /**
     *
     * @param _class XXX
     * @return XXX
     * @throws Exception XXX
     */
    Object createInstance(Class _class)
        throws Exception
    {
        return _class.newInstance();
    }

    /**
     *
     * @param type XXX
     * @return XXX
     * @throws ClassNotFoundException XXX
     */
    public Class getJavaClass()
        throws ClassNotFoundException
    {
        QName type = getType();
        if (type != null &&
                WSDDConstants.WSDD_JAVA.equals(type.getNamespaceURI())) {
            return Class.forName(type.getLocalPart());
        }
        return null;
    }
}
