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
package org.apache.axis.message;

import org.apache.axis.InternalException;
import org.apache.axis.AxisFault;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import java.io.InputStream;

/** 
 * A Body element.
 */
public class SOAPBodyElement extends MessageElement
    implements javax.xml.soap.SOAPBodyElement
{
    private static Log log =
        LogFactory.getLog(SOAPBodyElement.class.getName());

    public SOAPBodyElement(String namespace,
                           String localPart,
                           String prefix,
                           Attributes attributes,
                           DeserializationContext context)
        throws AxisFault
    {
        super(namespace, localPart, prefix, attributes, context);
    }

    public SOAPBodyElement(Name name)
    {
        super(name);
    }
    
    public SOAPBodyElement(Element elem)
    {
        super(elem);
    }
    
    public SOAPBodyElement()
    {
    }

    public SOAPBodyElement(InputStream input) 
    {
        super( getDocumentElement(input) );
    }

    private static Element getDocumentElement(InputStream input) {
        try {
            return XMLUtils.newDocument(input).getDocumentElement();
        } catch (Exception e) {
            throw new InternalException(e);
        }
    }

    public void setParentElement(SOAPElement parent) throws SOAPException {
        if(parent == null)
            throw new IllegalArgumentException(Messages.getMessage("nullParent00")); 
        // migration aid
        if (parent instanceof SOAPEnvelope) {
            log.warn(Messages.getMessage("bodyElementParent"));
            parent = ((SOAPEnvelope)parent).getBody();
        }
        try {
            // cast to force exception if wrong type
            super.setParentElement(parent);
        } catch (Throwable t) {
            throw new SOAPException(t);
        }
    }

    public void detachNode() {
        if (parent != null) {
            ((SOAPBody)parent).removeBodyElement(this);
        }
        super.detachNode();
    }
}
