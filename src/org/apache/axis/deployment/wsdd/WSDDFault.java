/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
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

/**
 * @author Glen Daniels (gdaniels@apache.org)
 */
package org.apache.axis.deployment.wsdd;

import org.w3c.dom.Element;
import org.apache.axis.description.FaultDesc;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.utils.XMLUtils;
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
