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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.apache.axis.Constants;
import org.apache.axis.utils.XMLUtils;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.deployment.DeploymentRegistry;
import org.apache.axis.deployment.DeploymentException;
import org.apache.axis.encoding.*;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.rpc.namespace.QName;
import java.util.Vector;
import java.util.Iterator;
import java.io.IOException;


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
    
    public void addTypeMapping(WSDDTypeMapping typeMapping)
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
        String attr = el.getAttribute("name");
        if (attr == null || "".equals(attr))
            throw new WSDDException(JavaUtils.getMessage("badNameAttr00"));
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
        
        Element [] elements = getChildElements(e, "handler");
        int i;

        for (i = 0; i < elements.length; i++) {
            addHandler(getQName(elements[i]));
        }

        elements = getChildElements(e, "chain");
        for (i = 0; i < elements.length; i++) {
            addChain(getQName(elements[i]));
        }
        
        elements = getChildElements(e, "transport");
        for (i = 0; i < elements.length; i++) {
            addTransport(getQName(elements[i]));
        }
        
        elements = getChildElements(e, "service");
        for (i = 0; i < elements.length; i++) {
            addService(getQName(elements[i]));
        }

        /*
        // How to deal with undeploying mappings?

        elements = getChildElements(e, "typeMapping");
        for (i = 0; i < elements.length; i++) {
            WSDDTypeMapping mapping = new WSDDTypeMapping(elements[i]);
            addTypeMapping(mapping);
        }

        elements = getChildElements(e, "beanMapping");
        for (i = 0; i < elements.length; i++) {
            WSDDBeanMapping mapping = new WSDDBeanMapping(elements[i]);
            addTypeMapping(mapping);
        }
        */
    }

    protected QName getElementName()
    {
        return WSDDConstants.UNDEPLOY_QNAME;
    }

    public void undeployFromRegistry(DeploymentRegistry registry)
        throws DeploymentException
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
            registry.undeployService(qname);
        }
    }

    private void writeElement(SerializationContext context,
                              QName elementQName,
                              QName qname)
        throws IOException
    {
        AttributesImpl attrs = new org.xml.sax.helpers.AttributesImpl();
        attrs.addAttribute("", "name", "name", "CDATA",
                           context.qName2String(qname));
        context.writeElement(elementQName, attrs);
    }

    public void writeToContext(SerializationContext context)
        throws IOException
    {
        context.registerPrefixForURI("", WSDDConstants.WSDD_NS);
        context.startElement(WSDDConstants.UNDEPLOY_QNAME,
                             null);
        
        Iterator i = handlers.iterator();
        QName qname;
        while (i.hasNext()) {
            qname = (QName)i.next();
            writeElement(context, WSDDConstants.HANDLER_QNAME, qname);
        }
        
        i = chains.iterator();
        while (i.hasNext()) {
            qname = (QName)i.next();
            writeElement(context, WSDDConstants.CHAIN_QNAME, qname);
        }

        i = services.iterator();
        while (i.hasNext()) {
            qname = (QName)i.next();
            writeElement(context, WSDDConstants.SERVICE_QNAME, qname);
        }
        
        i = transports.iterator();
        while (i.hasNext()) {
            qname = (QName)i.next();
            writeElement(context, WSDDConstants.TRANSPORT_QNAME, qname);
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
