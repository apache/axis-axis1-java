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

import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Element;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.namespace.QName;
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
        String nameStr = e.getAttribute(ATTR_QNAME);
        if (nameStr != null && !nameStr.equals("")) {
            parameter.setQName(XMLUtils.getQNameFromString(nameStr, e));
        } else {
            nameStr = e.getAttribute(ATTR_NAME);
            if (nameStr != null && !nameStr.equals("")) {
                parameter.setQName(new QName(null, nameStr));
            }
        }
        
        String modeStr = e.getAttribute(ATTR_MODE);
        if (modeStr != null && !modeStr.equals("")) {
            parameter.setMode(ParameterDesc.modeFromString(modeStr));
        }

        String inHStr = e.getAttribute(ATTR_INHEADER);
        if (inHStr != null) {
            parameter.setInHeader(JavaUtils.isTrueExplicitly(inHStr));
        }
        String outHStr = e.getAttribute(ATTR_OUTHEADER);
        if (outHStr != null) {
            parameter.setOutHeader(JavaUtils.isTrueExplicitly(outHStr));
        }
        
        String typeStr = e.getAttribute(ATTR_TYPE);
        if (typeStr != null && !typeStr.equals("")) {
            parameter.setTypeQName(XMLUtils.getQNameFromString(typeStr, e));
        }
        
        Element docElem = getChildElement(e, ELEM_WSDD_DOC);
        if (docElem != null) {
            WSDDDocumentation documentation = new WSDDDocumentation(docElem);
            parameter.setDocumentation(documentation.getValue());
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
                attrs.addAttribute("", ATTR_QNAME, ATTR_QNAME,
                               "CDATA",
                               context.qName2String(parameter.getQName()));
            } else {
                attrs.addAttribute("", ATTR_NAME, ATTR_NAME, "CDATA", 
                                   parameter.getQName().getLocalPart());
            }
        }

        // Write the mode attribute, but only if it's not the default (IN)
        byte mode = parameter.getMode();
        if (mode != ParameterDesc.IN) {
            String modeStr = ParameterDesc.getModeAsString(mode);
            attrs.addAttribute("", ATTR_MODE, ATTR_MODE, "CDATA", modeStr);
        }

        if (parameter.isInHeader()) {
            attrs.addAttribute("", ATTR_INHEADER, ATTR_INHEADER,
                               "CDATA", "true");
        }

        if (parameter.isOutHeader()) {
            attrs.addAttribute("", ATTR_OUTHEADER, ATTR_OUTHEADER,
                               "CDATA", "true");
        }
        
        QName typeQName = parameter.getTypeQName();
        if (typeQName != null) {
            attrs.addAttribute("", ATTR_TYPE, ATTR_TYPE, "CDATA",
                               context.qName2String(typeQName));            
        }
        
        context.startElement(getElementName(), attrs);
        
        if (parameter.getDocumentation() != null) {
            WSDDDocumentation documentation = new WSDDDocumentation(parameter.getDocumentation());
            documentation.writeToContext(context);
        }
		        
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
        return WSDDConstants.QNAME_PARAM;
    }
}
