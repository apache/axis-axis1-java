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

import org.apache.axis.ConfigurationException;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.MessageContext;
import org.apache.axis.utils.Messages;
import org.w3c.dom.Element;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;


/**
 * WSDD deployment element
 *
 * @author James Snell
 */
public class WSDDUndeployment
    extends WSDDElement
    implements WSDDTypeMappingContainer
{
    private Vector handlers = new Vector();
    private Vector chains = new Vector();
    private Vector services = new Vector();
    private Vector transports = new Vector();
    private Vector typeMappings = new Vector();

    public void addHandler(QName handler)
    {
        handlers.add(handler);
    }

    public void addChain(QName chain)
    {
        chains.add(chain);
    }

    public void addTransport(QName transport)
    {
        transports.add(transport);
    }
    
    public void addService(QName service)
    {
        services.add(service);
    }
    
    public void deployTypeMapping(WSDDTypeMapping typeMapping)
        throws WSDDException
    {
        typeMappings.add(typeMapping);
    }

    /**
     * Default constructor
     */ 
    public WSDDUndeployment()
    {
    }

    private QName getQName(Element el) throws WSDDException
    {
        String attr = el.getAttribute(ATTR_NAME);
        if (attr == null || "".equals(attr))
            throw new WSDDException(Messages.getMessage("badNameAttr00"));
        return new QName("", attr);
    }

    /**
     * Constructor - build an undeployment from a DOM Element.
     *
     * @param e the DOM Element to initialize from
     * @throws WSDDException if there is a problem
     */
    public WSDDUndeployment(Element e)
        throws WSDDException
    {
        super(e);
        
        Element [] elements = getChildElements(e, ELEM_WSDD_HANDLER);
        int i;

        for (i = 0; i < elements.length; i++) {
            addHandler(getQName(elements[i]));
        }

        elements = getChildElements(e, ELEM_WSDD_CHAIN);
        for (i = 0; i < elements.length; i++) {
            addChain(getQName(elements[i]));
        }
        
        elements = getChildElements(e, ELEM_WSDD_TRANSPORT);
        for (i = 0; i < elements.length; i++) {
            addTransport(getQName(elements[i]));
        }
        
        elements = getChildElements(e, ELEM_WSDD_SERVICE);
        for (i = 0; i < elements.length; i++) {
            addService(getQName(elements[i]));
        }

        /*
        // How to deal with undeploying mappings?

        elements = getChildElements(e, ELEM_WSDD_TYPEMAPPING);
        for (i = 0; i < elements.length; i++) {
            WSDDTypeMapping mapping = new WSDDTypeMapping(elements[i]);
            addTypeMapping(mapping);
        }

        elements = getChildElements(e, ELEM_WSDD_BEANMAPPING);
        for (i = 0; i < elements.length; i++) {
            WSDDBeanMapping mapping = new WSDDBeanMapping(elements[i]);
            addTypeMapping(mapping);
        }
        */
    }

    protected QName getElementName()
    {
        return QNAME_UNDEPLOY;
    }

    public void undeployFromRegistry(WSDDDeployment registry)
        throws ConfigurationException
    {
        QName qname;
        for (int n = 0; n < handlers.size(); n++) {
            qname = (QName)handlers.get(n);
            registry.undeployHandler(qname);
        }

        for (int n = 0; n < chains.size(); n++) {
            qname = (QName)chains.get(n);
            registry.undeployHandler(qname);
        }

        for (int n = 0; n < transports.size(); n++) {
            qname = (QName)transports.get(n);
            registry.undeployTransport(qname);
        }

        for (int n = 0; n < services.size(); n++) {
            qname = (QName)services.get(n);
 
            try {
                String sname = qname.getLocalPart();
                MessageContext messageContext = MessageContext.getCurrentContext();
                if (messageContext != null) {
                    SOAPService service = messageContext.getAxisEngine()
                            .getService(sname);
                    if ( service != null ) service.clearSessions();
                }
            } catch(Exception exp) {
                throw new ConfigurationException(exp);
            }
            registry.undeployService(qname);
        }
    }

    private void writeElement(SerializationContext context,
                              QName elementQName,
                              QName qname)
        throws IOException
    {
        AttributesImpl attrs = new org.xml.sax.helpers.AttributesImpl();
        attrs.addAttribute("", ATTR_NAME, ATTR_NAME, "CDATA",
                           context.qName2String(qname));
         
        context.startElement(elementQName, attrs);
        context.endElement();
    }

    public void writeToContext(SerializationContext context)
        throws IOException
    {
        context.registerPrefixForURI(NS_PREFIX_WSDD, URI_WSDD);
        context.startElement(WSDDConstants.QNAME_UNDEPLOY, null);
        
        Iterator i = handlers.iterator();
        QName qname;
        while (i.hasNext()) {
            qname = (QName)i.next();
            writeElement(context, QNAME_HANDLER, qname);
        }
        
        i = chains.iterator();
        while (i.hasNext()) {
            qname = (QName)i.next();
            writeElement(context, QNAME_CHAIN, qname);
        }

        i = services.iterator();
        while (i.hasNext()) {
            qname = (QName)i.next();
            writeElement(context, QNAME_SERVICE, qname);
        }
        
        i = transports.iterator();
        while (i.hasNext()) {
            qname = (QName)i.next();
            writeElement(context, QNAME_TRANSPORT, qname);
        }
        
        i = typeMappings.iterator();
        while (i.hasNext()) {
            WSDDTypeMapping mapping = (WSDDTypeMapping)i.next();
            mapping.writeToContext(context);
        }

        context.endElement();
    }
    
    /**
     *
     * @return XXX
     */
    public WSDDTypeMapping[] getTypeMappings()
    {
        WSDDTypeMapping[] t = new WSDDTypeMapping[typeMappings.size()];
        typeMappings.toArray(t);
        return t;
    }

}
