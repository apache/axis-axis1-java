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

import java.io.IOException;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.apache.axis.Chain;
import org.apache.axis.ConfigurationException;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.encoding.SerializationContext;
import org.w3c.dom.Element;
import org.xml.sax.helpers.AttributesImpl;


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
