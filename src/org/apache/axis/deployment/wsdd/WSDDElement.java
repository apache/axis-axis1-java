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

import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;


/**
 * abstract class extended by all WSDD Element classes
 */
public abstract class WSDDElement
    implements Serializable
{

    /** XXX */
    private Element element;

    /** XXX */
    private Hashtable children;

    /**
     * Create a new element in DOM and wrap it in WSDD
     * @param doc (Document) XXX
     * @param parent (Node) XXX
     * @param name (String) XXX
     * @throws WSDDException XXX
     */
    public WSDDElement(Document doc, Node parent, String name)
        throws WSDDException
    {
        element = doc.createElementNS(WSDDConstants.WSDD_NS, name);
        validateCandidateElement(element, name);
        parent.appendChild(element);
    }

    /**
     * Create an element in WSDD that wraps an extant DOM element
     * @param e (Element) XXX
     * @param name (String) XXX
     * @throws WSDDException XXX
     */
    public WSDDElement(Element e, String name)
        throws WSDDException
    {

        validateCandidateElement(e, name);

        element = e;
    }

    /**
     *
     * @param e XXX
     * @param name XXX
     * @throws WSDDException XXX
     */
    private static void validateCandidateElement(Element e, String name)
        throws WSDDException
    {

        if ((null == e) || (null == e.getNamespaceURI())
                || (null == e.getLocalName())
                ||!e.getNamespaceURI().equals(WSDDConstants.WSDD_NS)
                ||!e.getLocalName().equals(name)) {
            throw new WSDDException("Invalid WSDD Element");
        }
    }

    /**
     *
     * @return XXX
     */
    public Element getElement()
    {
        return element;
    }

    /**
     *
     * @return XXX
     */
    public String elementToString()
    {
        return org.apache.axis.utils.XMLUtils.ElementToString(element);
    }

    /**
     *
     * @return the newly created / tree-ified item,
	 *          so that the caller might mutate it
     */
    public WSDDDocumentation createDocumentation()
    {
        removeDocumentation();

        WSDDElement c = createChild(WSDDDocumentation.class);

        return (WSDDDocumentation) c;
    }

    /**
     *
     */
    public void removeDocumentation()
    {
        removeChild(getDocumentation());
    }

    /**
     *
     * @return XXX
     */
    public WSDDDocumentation getDocumentation()
    {

        WSDDElement[] e = createArray("documentation",
                                      WSDDDocumentation.class);

        if (e.length == 0) {
            return null;
        }

        return (WSDDDocumentation) e[0];
    }

    /**
     *
     * @param name XXX
     * @return XXX
     */
    public String getAttribute(String name)
    {
        return element.getAttribute(name);
    }

    /**
     *
     * @param name XXX
     * @param value XXX
     */
    public void setAttribute(String name, String value)
    {

        try {
            element.setAttribute(name, value);
        }
        catch (Exception e) {

            // fail stoically
        }
    }

    /**
     *
     * @param name XXX
     */
    public void removeAttribute(String name)
    {

        try {
            element.removeAttribute(name);
        }
        catch (Exception e) {

            // fail stoically
        }
    }

    /**
     *
     * @param name XXX
     * @return XXX
     */
    public String getAttributeNS(String nameSpaceUri, String localName)
    {
        return element.getAttributeNS(nameSpaceUri, localName);
    }

    /**
     *
     * @param name XXX
     * @param value XXX
     */
    public void setAttributeNS(String nameSpaceUri,
								String prefix,
								String localName,
								String value)
    {
		String usePrefix;
		String oldPrefix;
		String oldNsUri;

		// generate a prefix if it is absent
		if (null == prefix || prefix.equals(""))
		{
			usePrefix = XMLUtils.getNewPrefix(element.getOwnerDocument(),
								nameSpaceUri);
		}
		else
		{
			oldPrefix = XMLUtils.getPrefix(nameSpaceUri, element);

			// always use 'xmlns' as it is a special-case
			if (prefix.equals("xmlns"))
			{
				usePrefix = prefix;
			}
			// if no pre-existing one for this nsUri, then use the supplied prefix
			//   unless that prefix maps to some (other) nsUri
			else if (null == oldPrefix && null == XMLUtils.getNamespace(prefix, element))
			{
				usePrefix = prefix;
			}
			// duh. use the supplied prefix if this nsUri already had this one
			else if (null != oldPrefix && oldPrefix.equals(prefix))
			{
				usePrefix = prefix;
			}
			else
			{
				usePrefix = XMLUtils.getNewPrefix(element.getOwnerDocument(),
							nameSpaceUri);
			}
		}

        try
		{
			/*********
			System.out.println("About to saNS with " +
					nameSpaceUri + " " +
					usePrefix + ":" + localName + " "+
					value);
					*********/
            element.setAttributeNS(nameSpaceUri, usePrefix + ":" + localName,
								value);
        }
        catch (Exception e)
		{
			// What to do here?
            e.printStackTrace();
        }
    }

    /**
     *
     * @param name XXX
     */
    public void removeAttributeNS(String nameSpace, String localName)
    {

        try {
            element.removeAttributeNS(nameSpace, localName);
        }
        catch (Exception e) {

            // fail stoically
        }
    }

    /**
     * Remove element from DOM and its wrapper from WSDD
     * @param w XXX
     */
    public void removeChild(WSDDElement w)
    {

        if (null == w) {
            return;
        }

        Element e = w.getElement();

        if ((null == e) || (null == children)) {
            return;
        }

        children.remove(e);
        e.getParentNode().removeChild(e);
    }

    /**
     *
     * @param ww XXX
     */
    public void removeChildren(WSDDElement[] ww)
    {

        for (int i = 0; i < ww.length; i++) {
            removeChild(ww[i]);
        }
    }

    /**
     * Insert element at this node in the DOM and in WSDD
     * @param type XXX
     * @return XXX
     */
    public WSDDElement createChild(Class type)
    {

        try {
            Class[]     cc = { Document.class, Node.class };
            Object[]    oo = { element.getOwnerDocument(), element };
            WSDDElement w  =
                (WSDDElement) type.getConstructor(cc).newInstance(oo);

            addChild(w);

            return w;
        }
        catch (Exception e) {
			e.printStackTrace();
            return null;
        }
    }

    /**
     * Insert extant WSDD element at this node of the WSDD
     * @param w XXX
     */
    protected void addChild(WSDDElement w)
    {

        if (children == null) {
            children = new Hashtable();
        }

        children.put(w.getElement(), w);
        
        Document doc = element.getOwnerDocument();
        if (w.getElement().getOwnerDocument().equals(doc))
            return;
        
        Node newEl = element.getOwnerDocument().importNode(w.getElement(), true);
        element.appendChild(newEl);
    }

    /**
     *
     * @param e XXX
     * @return XXX
     */
    protected WSDDElement getChild(Element e)
    {

        if (children == null) {
            return null;
        }

        return (WSDDElement) children.get(e);
    }

    /**
     *
     * @param e XXX
     * @return XXX
     */
    protected boolean hasChild(Element e)
    {

        if (children == null) {
            return false;
        }

        return children.containsKey(e);
    }

    /**
     * Used to create an array of child elements of a particular type
     * @param name XXX
     * @param type XXX
     * @return XXX
     */
    WSDDElement[] createArray(String name, Class type)
    {

        String[] names = { name };
        Class[]  types = { type };

        return createArray(names, types);
    }

    /**
     * Used to create an array of child elements of a particular type
     *     NB: Result-array could possibly become stale, eg if its elements
     *          are removed from DOM or WSDD trees
     * @param names XXX
     * @param types XXX
     * @return XXX
     */
    WSDDElement[] createArray(String[] names, Class[] types)
    {

        try {
            NodeList nl = element.getChildNodes();
            Vector   v  = new Vector();

            for (int n = 0; n < nl.getLength(); n++) {
                if (nl.item(n).getNodeType() == Element.ELEMENT_NODE) {
                    Element e = (Element) nl.item(n);

                    if (e.getNamespaceURI().equals(WSDDConstants.WSDD_NS)) {
                        for (int i = 0; i < names.length; i++) {
                            if (e.getLocalName().equals(names[i])) {
                                if (hasChild(e)) {
                                    v.addElement(getChild(e));
                                }
                                else {
                                    Class[]     c = { Element.class };
                                    Object[]    o = { e };
                                    WSDDElement w = null;

                                    w = (WSDDElement) types[i]
                                        .getConstructor(c).newInstance(o);

                                    addChild(w);
                                    v.addElement(w);
                                }
                            }
                        }
                    }
                }
            }

            Object[]      obj = v.toArray();
            WSDDElement[] ret = new WSDDElement[obj.length];

            System.arraycopy(obj, 0, ret, 0, obj.length);

            return ret;
        }
        catch (Exception e) {
            return null;
        }
    }
}
