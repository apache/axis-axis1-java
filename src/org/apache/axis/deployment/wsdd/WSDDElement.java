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

import org.apache.axis.components.i18n.Messages;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.utils.JavaUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.Serializable;
import java.util.Vector;


/**
 * abstract class extended by all WSDD Element classes
 */
public abstract class WSDDElement
    extends WSDDConstants
    implements Serializable
{
    /** If we have documentation, it goes here */
    private WSDDDocumentation documentation = null;
    
    private String name;

    /**
     * Default constructor
     */ 
    public WSDDElement()
    {
    }
    
    /**
     * Create an element in WSDD that wraps an extant DOM element
     * @param e (Element) XXX
     * @throws WSDDException XXX
     */
    public WSDDElement(Element e)
        throws WSDDException
    {
        validateCandidateElement(e);
    }

    /**
     * Return the element name of a particular subclass.
     */ 
    protected abstract QName getElementName();
    
    /**
     * Make sure everything looks kosher with the element name.
     */
    private void validateCandidateElement(Element e)
        throws WSDDException
    {
        QName name = getElementName();
        
        if ((null == e) || (null == e.getNamespaceURI())
                || (null == e.getLocalName())
                ||!e.getNamespaceURI().equals(name.getNamespaceURI())
                ||!e.getLocalName().equals(name.getLocalPart())) {
            throw new WSDDException(Messages.getMessage("invalidWSDD00",
                                    e.getLocalName(),
                                    name.getLocalPart()));
        }
    }

    public Element getChildElement(Element e, String name)
    {
        Element [] elements = getChildElements(e, name);
        if (elements.length == 0)
            return null;
        return elements[0];
    }
    
    public Element [] getChildElements(Element e, String name)
    {
        NodeList nl = e.getChildNodes();
        Vector els = new Vector();
        
        for (int i = 0; i < nl.getLength(); i++) {
            Node thisNode = nl.item(i);
            if (!(thisNode instanceof Element))
                continue;
            
            Element el = (Element)thisNode;
            if (el.getLocalName().equals(name)) {
                els.add(el);
            }
        }
        
        Element [] elements = new Element [els.size()];
        els.toArray(elements);

        return elements;
    }
    
    /**
     * Get documentation (if any) for this WSDDElement.
     * 
     * @return the WSDDDocumentation object associated with this element, or
     *         null.
     */
    public WSDDDocumentation getDocumentation()
    {
        return documentation;
    }

    /**
     * Write this element out to a SerializationContext
     */ 
    public abstract void writeToContext(SerializationContext context)
        throws IOException;
}
