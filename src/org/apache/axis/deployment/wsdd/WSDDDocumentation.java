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
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.io.IOException;


/**
 * represents a WSDD documentation 
 * All WSDDElement can have a documentation but it is used only for 
 * Services, Operations and Parameters for now
 */
public class WSDDDocumentation
    extends WSDDElement
{
    private String value; /** the documentation */
 
    protected QName getElementName()
    {
        return WSDDConstants.QNAME_DOC;
    }
    
    public WSDDDocumentation(String value)
    {
    	this.value = value;
    }
    
    /**
     *
     * @param e (Element) XXX
     * @throws WSDDException XXX
     */
    public WSDDDocumentation(Element e)
        throws WSDDException
    {
        super(e);
        value = XMLUtils.getChildCharacterData(e);
    }

    /**
     * get the documentation
     */
    public String getValue()
    {
        return value;
    }

    /**
     * set the documentation
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    /**
     * Write this element out to a SerializationContext
     */ 
    public void writeToContext(SerializationContext context)
            throws IOException
    {
        context.startElement(QNAME_DOC, null);
        context.writeString(value);
        context.endElement();
    }
}
