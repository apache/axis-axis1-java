/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

import org.apache.axis.description.FaultDesc;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Element;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * Parse the WSDD operation elements.
 * 
 * Example: 
 * <operation name="name" qname="element QName" returnQName="QName">
 *   <parameter ... />
 * </operation>
 * 
 */
public class WSDDOperation extends WSDDElement
{
    /** Holds all our actual data */
    OperationDesc desc = new OperationDesc();

    /**
     * Constructor
     */
    public WSDDOperation(OperationDesc desc) {
        this.desc = desc;
    }
        
    /**
     * Constructor from XML
     *
     * @param e (Element) the <operation> element
     * @param parent our ServiceDesc.
     * @throws WSDDException XXX
     */
    public WSDDOperation(Element e, ServiceDesc parent)
        throws WSDDException
    {
        super(e);

        desc.setParent(parent);
        desc.setName(e.getAttribute(ATTR_NAME));

        String qNameStr = e.getAttribute(ATTR_QNAME);
        if (qNameStr != null && !qNameStr.equals(""))
            desc.setElementQName(XMLUtils.getQNameFromString(qNameStr, e));
        
        String retQNameStr = e.getAttribute(ATTR_RETQNAME);
        if (retQNameStr != null && !retQNameStr.equals(""))
            desc.setReturnQName(XMLUtils.getQNameFromString(retQNameStr, e));
        
        String retTypeStr = e.getAttribute(ATTR_RETTYPE);
        if (retTypeStr != null && !retTypeStr.equals(""))
            desc.setReturnType(XMLUtils.getQNameFromString(retTypeStr, e));

        String retHStr = e.getAttribute(ATTR_RETHEADER);
        if (retHStr != null) {
            desc.setReturnHeader(JavaUtils.isTrueExplicitly(retHStr));
        }

        Element [] parameters = getChildElements(e, ELEM_WSDD_PARAM);
        for (int i = 0; i < parameters.length; i++) {
            Element paramEl = parameters[i];
            WSDDParameter parameter = new WSDDParameter(paramEl, desc);
            desc.addParameter(parameter.getParameter());
        }
        
        Element [] faultElems = getChildElements(e, ELEM_WSDD_FAULT);
        for (int i = 0; i < faultElems.length; i++) {
            Element faultElem = faultElems[i];
            WSDDFault fault = new WSDDFault(faultElem);
            desc.addFault(fault.getFaultDesc());
        }
    }

    /**
     * Write this element out to a SerializationContext
     */
    public void writeToContext(SerializationContext context)
            throws IOException {
        AttributesImpl attrs = new AttributesImpl();

        if (desc.getReturnQName() != null) {
            attrs.addAttribute("", ATTR_RETQNAME, ATTR_RETQNAME,
                               "CDATA",
                               context.qName2String(desc.getReturnQName()));
        }

        if (desc.getReturnType() != null) {
            attrs.addAttribute("", ATTR_RETTYPE, ATTR_RETTYPE,
                               "CDATA",
                               context.qName2String(desc.getReturnType()));
        }
        if (desc.isReturnHeader()) {
            attrs.addAttribute("", ATTR_RETHEADER, ATTR_RETHEADER,
                               "CDATA", "true");
        }

        if (desc.getName() != null) {
            attrs.addAttribute("", ATTR_NAME, ATTR_NAME, "CDATA", desc.getName());
        }
        
        if (desc.getElementQName() != null) {
            attrs.addAttribute("", ATTR_QNAME, ATTR_QNAME, 
                               "CDATA", 
                               context.qName2String(desc.getElementQName()));
        }

        context.startElement(getElementName(), attrs);

        ArrayList params = desc.getParameters();
        for (Iterator i = params.iterator(); i.hasNext();) {
            ParameterDesc parameterDesc = (ParameterDesc) i.next();
            WSDDParameter p = new WSDDParameter(parameterDesc);
            p.writeToContext(context);
        }
        
        ArrayList faults = desc.getFaults();
        if (faults != null) {
            for (Iterator i = faults.iterator(); i.hasNext();) {
                FaultDesc faultDesc = (FaultDesc) i.next();
                WSDDFault f = new WSDDFault(faultDesc);
                f.writeToContext(context);
            }
        }

        context.endElement();
    }

    protected QName getElementName() {
        return QNAME_OPERATION;
    }

    public OperationDesc getOperationDesc()
    {
        return desc;
    }
}
