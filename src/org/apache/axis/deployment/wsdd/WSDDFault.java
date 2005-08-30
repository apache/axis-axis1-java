/*
 * Copyright 2002-2004 The Apache Software Foundation.
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

/**
 * @author Glen Daniels (gdaniels@apache.org)
 */
package org.apache.axis.deployment.wsdd;

import org.apache.axis.description.FaultDesc;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Element;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.namespace.QName;
import java.io.IOException;

public class WSDDFault extends WSDDElement {
    FaultDesc desc;

    public WSDDFault(FaultDesc desc) {
        this.desc = desc;
    }

    /**
     * Construct a WSDDFault from a DOM Element
     * @param e the &lt;fault&gt; Element
     * @throws WSDDException
     */
    public WSDDFault(Element e) throws WSDDException {
        super(e);

        desc = new FaultDesc();
        
        String nameStr = e.getAttribute(ATTR_NAME);
        if (nameStr != null && !nameStr.equals(""))
            desc.setName(nameStr);

        String qNameStr = e.getAttribute(ATTR_QNAME);
        if (qNameStr != null && !qNameStr.equals(""))
            desc.setQName(XMLUtils.getQNameFromString(qNameStr, e));

        String classNameStr = e.getAttribute(ATTR_CLASS);
        if (classNameStr != null && !classNameStr.equals(""))
            desc.setClassName(classNameStr);

        String xmlTypeStr = e.getAttribute(ATTR_TYPE);
        if (xmlTypeStr != null && !xmlTypeStr.equals(""))
            desc.setXmlType(XMLUtils.getQNameFromString(xmlTypeStr, e));
    }

    /**
     * Return the element name of a particular subclass.
     */
    protected QName getElementName() {
        return QNAME_FAULT;
    }

    /**
     * Write this element out to a SerializationContext
     */
    public void writeToContext(SerializationContext context)
            throws IOException {
        AttributesImpl attrs = new AttributesImpl();

        attrs.addAttribute("", ATTR_QNAME, ATTR_QNAME,
                           "CDATA",
                           context.qName2String(desc.getQName()));

        attrs.addAttribute("", ATTR_CLASS, ATTR_CLASS,
                           "CDATA", desc.getClassName());

        attrs.addAttribute("", ATTR_TYPE, ATTR_TYPE,
                           "CDATA",
                           context.qName2String(desc.getXmlType()));

        context.startElement(getElementName(), attrs);
        context.endElement();
    }

    public FaultDesc getFaultDesc() {
        return desc;
    }

    public void setFaultDesc(FaultDesc desc) {
        this.desc = desc;
    }
}
