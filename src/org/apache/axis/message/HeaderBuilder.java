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

/**
 *
 * @author Glen Daniels (gdaniels@allaire.com)
 */

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.Constants;
import org.apache.axis.utils.Messages;
import org.apache.axis.AxisFault;
import org.apache.axis.soap.SOAPConstants;
import org.apache.commons.logging.Log;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class HeaderBuilder extends SOAPHandler
{
    protected static Log log =
        LogFactory.getLog(HeaderBuilder.class.getName());

    private SOAPHeaderElement header;
    private SOAPEnvelope envelope;

    HeaderBuilder(SOAPEnvelope envelope)
    {
        this.envelope = envelope;
    }

    public void startElement(String namespace, String localName,
                             String prefix, Attributes attributes,
                             DeserializationContext context)
        throws SAXException
    {
        SOAPConstants soapConstants = Constants.DEFAULT_SOAP_VERSION;
        if (context.getMessageContext() != null)
            soapConstants = context.getMessageContext().getSOAPConstants();

        if (soapConstants == SOAPConstants.SOAP12_CONSTANTS &&
            attributes.getValue(Constants.URI_SOAP12_ENV, Constants.ATTR_ENCODING_STYLE) != null) {

            AxisFault fault = new AxisFault(Constants.FAULT_SOAP12_SENDER,
                null, Messages.getMessage("noEncodingStyleAttrAppear", "Header"), null, null, null);

            throw new SAXException(fault);
        }

        if (!context.isDoneParsing()) {
            if (myElement == null) {
                try {
                    myElement = new SOAPHeader(namespace, localName, prefix,
                                               attributes, context,
                                               envelope.getSOAPConstants());
                } catch (AxisFault axisFault) {
                    throw new SAXException(axisFault);
                }
                envelope.setHeader((SOAPHeader)myElement);
            }
            context.pushNewElement(myElement);
        }
    }

    public SOAPHandler onStartChild(String namespace,
                                    String localName,
                                    String prefix,
                                    Attributes attributes,
                                    DeserializationContext context)
        throws SAXException
    {
        try {
            header = new SOAPHeaderElement(namespace, localName, prefix,
                                           attributes, context);
        } catch (AxisFault axisFault) {
            throw new SAXException(axisFault);
        }

        SOAPHandler handler = new SOAPHandler();
        handler.myElement = header;

        return handler;
    }

    public void onEndChild(String namespace, String localName,
                           DeserializationContext context)
    {
    }
}
