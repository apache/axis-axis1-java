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

        String soapAction = e.getAttribute(ATTR_SOAPACTION);
        if (soapAction != null) {
            desc.setSoapAction(soapAction);
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

        Element docElem = getChildElement(e, ELEM_WSDD_DOC);
        if (docElem != null) {
            WSDDDocumentation documentation = new WSDDDocumentation(docElem);
            desc.setDocumentation(documentation.getValue());
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
        if (desc.getSoapAction() != null) {
            attrs.addAttribute("", ATTR_SOAPACTION, ATTR_SOAPACTION, "CDATA", desc.getSoapAction());
        }

        context.startElement(getElementName(), attrs);

        if (desc.getDocumentation() != null) {
            WSDDDocumentation documentation = new WSDDDocumentation(desc.getDocumentation());
            documentation.writeToContext(context);
        }
        
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
