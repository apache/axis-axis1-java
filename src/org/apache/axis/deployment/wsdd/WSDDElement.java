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

import java.io.Serializable;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.Hashtable; 
import java.util.Vector;

/**
 * abstract class extended by all WSDD Element classes
 */
public abstract class WSDDElement implements Serializable {
    
    protected Element element;
    protected Hashtable children; 
 
    public WSDDElement(Element e, String name) throws WSDDException {
        if (!e.getNamespaceURI().equals(WSDDConstants.WSDD_NS) ||
            !e.getLocalName().equals(name))
            throw new WSDDException("Invalid WSDD Element");
        element = e;
    }
    
    public Element getElement() {
        return element;
    }
    
    void addChild(Element e, WSDDElement w) {
        if (children == null) children = new Hashtable();
        children.put(e, w);
    }
    
    WSDDElement getChild(Element e) {
        if (children == null) return null;
        return (WSDDElement)children.get(e);
    }
    
    boolean hasChild(Element e) {
        if (children == null) return false;
        return children.containsKey(e);
    }
    
    /**
     * Used to create an array of child elements of a particular type
     */
    WSDDElement[] createArray(String name, Class type) {
        try {
            NodeList nl = element.getChildNodes();
            Vector v = new Vector();
            for (int n = 0; n < nl.getLength(); n++) {
                if (nl.item(n).getNodeType() == Element.ELEMENT_NODE) {
                    Element e = (Element)nl.item(n);
                    if (e.getNamespaceURI().equals(WSDDConstants.WSDD_NS) &&
                        e.getLocalName().equals(name)) {
                        if (hasChild(e)) {
                            v.addElement(e);
                        } else {
                            Class[] c = {Element.class};
                            Object[] o = {e};
                            WSDDElement w = (WSDDElement)type.getConstructor(c).newInstance(o);
                            addChild(e,w);
                            v.addElement(w);
                        }
                    }
                }
            }
            Object[] obj = v.toArray();
            WSDDElement[] ret = new WSDDElement[obj.length];
            System.arraycopy(obj,0,ret,0,obj.length);
            return ret;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Used to create an array of child elements of a particular type
     */
    WSDDElement[] createArray(String n1, String n2, Class type1, Class type2) {
        try {
            NodeList nl = element.getChildNodes();
            Vector v = new Vector();
            for (int n = 0; n < nl.getLength(); n++) {
                if (nl.item(n).getNodeType() == Element.ELEMENT_NODE) {
                    Element e = (Element)nl.item(n);
                    if (e.getNamespaceURI().equals(WSDDConstants.WSDD_NS) &&
                        (e.getLocalName().equals(n1) || e.getLocalName().equals(n2))) {
                        if (hasChild(e)) {
                            v.addElement(e);
                        } else {
                            Class[] c = {Element.class};
                            Object[] o = {e};
                            WSDDElement w = null;
                            if (e.getLocalName().equals(n1)) 
                                w = (WSDDElement)type1.getConstructor(c).newInstance(o);
                            if (e.getLocalName().equals(n2))
                                w = (WSDDElement)type2.getConstructor(c).newInstance(o);
                            addChild(e,w);
                            v.addElement(w);
                        }
                    }
                }
            }
            Object[] obj = v.toArray();
            WSDDElement[] ret = new WSDDElement[obj.length];
            System.arraycopy(obj,0,ret,0,obj.length);
            return ret;
        } catch (Exception e) { return null; }
    }
    
}
