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
import org.apache.axis.ConfigurationException;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.components.i18n.Messages;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.utils.JavaUtils;
import org.w3c.dom.Element;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.Vector;


/**
 * WSDD chain element
 *
 */
public class WSDDChain
    extends WSDDHandler
{
    private Vector handlers = new Vector();
    
    /**
     * Default constructor
     */ 
    public WSDDChain()
    {
    }
    
    /**
     *
     * @param e (Element) XXX
     * @throws WSDDException XXX
     */
    public WSDDChain(Element e)
        throws WSDDException
    {
        super(e);
        
        // If we're simply a reference to an existing chain, return.
        // !!! should check to make sure it's a valid chain?
        if (type != null)
            return;
        
        Element [] elements = getChildElements(e, ELEM_WSDD_HANDLER);
        if (elements.length != 0) {
            for (int i = 0; i < elements.length; i++) {
                WSDDHandler handler = new WSDDHandler(elements[i]);
                addHandler(handler);
            }
        }
        
        elements = getChildElements(e, ELEM_WSDD_CHAIN);
        if (elements.length != 0) {
            for (int i = 0; i < elements.length; i++) {
                WSDDChain chain = new WSDDChain(elements[i]);
                addHandler(chain);
            }
        }

        if (handlers.isEmpty())
            throw new WSDDException(Messages.getMessage("noHandlersInChain00", 
                                    getElementName().getLocalPart(),
                                    (getQName()!=null)?getQName().toString():"null"));
    }
    
    protected QName getElementName()
    {
        return WSDDConstants.QNAME_CHAIN;
    }
    
    /**
     * Add a Handler to the chain (at the end)
     */ 
    public void addHandler(WSDDHandler handler)
    {
        handlers.add(handler);
    }

    /**
     * Obtain our handler list
     * 
     * @return a Vector containing our Handlers
     */
    public Vector getHandlers()
    {
        return handlers;
    }

    /**
     * Remove a Handler from the chain
     */
    public void removeHandler(WSDDHandler victim)
    {
        handlers.remove(victim);
    }

    /**
     * Creates a new instance of this Chain
     * @param registry XXX
     * @return XXX
     * @throws ConfigurationException XXX
     */
    public Handler makeNewInstance(EngineConfiguration registry)
        throws ConfigurationException
    {
        Chain c = new org.apache.axis.SimpleChain();
        
        for (int n = 0; n < handlers.size(); n++) {
            WSDDHandler handler = (WSDDHandler)handlers.get(n); 
            Handler h = handler.getInstance(registry);
            if ( h != null )
              c.addHandler(h);
            else
              throw new ConfigurationException("Can't find handler name:'" +
                                               handler.getQName() + "' type:'"+
                                               handler.getType() +
                                               "' in the registry");
        }
        
        return c;
    }
    
    /**
     * Write this element out to a SerializationContext
     */ 
    public void writeToContext(SerializationContext context)
        throws IOException
    {
        AttributesImpl attrs = new AttributesImpl();
        QName name = getQName();
        if (name != null) {
            attrs.addAttribute("", ATTR_NAME, ATTR_NAME,
                               "CDATA", context.qName2String(name));
        }
        if (getType() != null) {
            attrs.addAttribute("", ATTR_TYPE, ATTR_TYPE,
                           "CDATA", context.qName2String(getType()));
        }
        
        context.startElement(getElementName(), attrs);
        for (int n = 0; n < handlers.size(); n++) {
            WSDDHandler handler = (WSDDHandler)handlers.get(n); 
            handler.writeToContext(context);
        } 
        context.endElement();
    }

    public void deployToRegistry(WSDDDeployment registry)
    {
        if (getQName() != null)
            registry.addHandler(this);
        
        for (int n = 0; n < handlers.size(); n++) {
            WSDDHandler handler = (WSDDHandler)handlers.get(n);
            if (handler.getQName() != null)
                handler.deployToRegistry(registry);
        }         
    }
}
