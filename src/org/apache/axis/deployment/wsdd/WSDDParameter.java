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

import org.w3c.dom.Element;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.utils.XMLUtils;
import org.apache.axis.encoding.SerializationContext;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.rpc.namespace.QName;
import java.io.IOException;

public class WSDDParameter extends WSDDElement
{
    OperationDesc parent;
    ParameterDesc parameter = new ParameterDesc();
    
    public WSDDParameter(Element e, OperationDesc parent) 
            throws WSDDException {
        super(e);
        this.parent = parent;
        
        // Get the parameter's name.  If a qname is specified, use that,
        // otherwise also look for a "name" attribute.  (name specifies
        // an unqualified name)
        String nameStr = e.getAttribute("qname");
        if (nameStr != null && !nameStr.equals("")) {
            parameter.setQName(XMLUtils.getQNameFromString(nameStr, e));
        } else {
            nameStr = e.getAttribute("name");
            if (nameStr != null && !nameStr.equals("")) {
                parameter.setQName(new QName(null, nameStr));
            }
        }
        
        String modeStr = e.getAttribute("mode");
        if (modeStr != null && !modeStr.equals("")) {
            parameter.setMode(ParameterDesc.modeFromString(modeStr));
        }
        
        String typeStr = e.getAttribute("type");
        if (typeStr != null && !typeStr.equals("")) {
            parameter.setTypeQName(XMLUtils.getQNameFromString(typeStr, e));
        }
    }

    public WSDDParameter() {
    }

    public WSDDParameter(ParameterDesc parameter) {
        this.parameter = parameter;
    }

    /**
     * Write this element out to a SerializationContext
     */
    public void writeToContext(SerializationContext context)
            throws IOException {
        AttributesImpl attrs = new AttributesImpl();
        
        QName qname = parameter.getQName(); 
        if (qname != null) {
            if (qname.getNamespaceURI() != null &&
                !qname.getNamespaceURI().equals("")) {
                attrs.addAttribute("", "qname", "qname",
                               "CDATA",
                               context.qName2String(parameter.getQName()));
            } else {
                attrs.addAttribute("", "name", "name", "CDATA", 
                                   parameter.getQName().getLocalPart());
            }
        }

        // Write the mode attribute, but only if it's not the default (IN)
        byte mode = parameter.getMode();
        if (mode != ParameterDesc.IN) {
            String modeStr = ParameterDesc.getModeAsString(mode);
            attrs.addAttribute("", "mode", "mode", "CDATA", modeStr);
        }
        
        QName typeQName = parameter.getTypeQName();
        if (typeQName != null) {
            attrs.addAttribute("", "type", "type", "CDATA",
                               context.qName2String(typeQName));            
        }
        
        context.startElement(getElementName(), attrs);
        context.endElement();
    }

    public ParameterDesc getParameter() {
        return parameter;
    }

    public void setParameter(ParameterDesc parameter) {
        this.parameter = parameter;
    }

    /**
     * Return the element name of a particular subclass.
     */
    protected QName getElementName() {
        return WSDDConstants.PARAM_QNAME;
    }
}
