/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.axis.deployment.wsdd;

import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.utils.Messages;
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
     * Write this element out to a SerializationContext
     */ 
    public abstract void writeToContext(SerializationContext context)
        throws IOException;
        
}
