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

import org.apache.axis.encoding.SerializationContext;
import org.w3c.dom.Element;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.namespace.QName;
import java.io.IOException;


/**
 *
 */
public class WSDDTransport
    extends WSDDTargetedChain
{
    /**
     * Default constructor
     */ 
    public WSDDTransport()
    {
    }
    
    /**
     *
     * @param e (Element) XXX
     * @throws WSDDException XXX
     */
    public WSDDTransport(Element e)
        throws WSDDException
    {
        super(e);
    }

    protected QName getElementName() {
        return WSDDConstants.QNAME_TRANSPORT;
    }

    /**
     * Write this element out to a SerializationContext
     */
    public void writeToContext(SerializationContext context)
            throws IOException {
        AttributesImpl attrs = new AttributesImpl();
        QName name = getQName();
        if (name != null) {
            attrs.addAttribute("", ATTR_NAME, ATTR_NAME,
                               "CDATA", context.qName2String(name));
        }
        
        name = getPivotQName();
        if (name != null) {
            attrs.addAttribute("", ATTR_PIVOT, ATTR_PIVOT,
                               "CDATA", context.qName2String(name));
        }
        
        context.startElement(WSDDConstants.QNAME_TRANSPORT, attrs);
        writeFlowsToContext(context);
        writeParamsToContext(context);
        context.endElement();
    }

    public void deployToRegistry(WSDDDeployment registry)
    {
        registry.addTransport(this);
        
        super.deployToRegistry(registry);
    }
}
