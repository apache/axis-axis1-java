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

import org.apache.axis.Constants;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.Messages;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;

import javax.xml.namespace.QName;

/**
 * The EnvelopeBuilder is responsible for parsing the top-level
 * SOAP envelope stuff (Envelope, Body, Header), and spawning off
 * HeaderBuilder and BodyBuilders.
 *
 * @author Glen Daniels (gdaniels@allaire.com)
 * @author Andras Avar (andras.avar@nokia.com)
 */
public class EnvelopeBuilder extends SOAPHandler
{
    private SOAPEnvelope envelope;
    private SOAPConstants soapConstants = SOAPConstants.SOAP11_CONSTANTS;

    private boolean gotHeader = false;
    private boolean gotBody = false;

    public EnvelopeBuilder(String messageType, SOAPConstants soapConstants)
    {
        envelope = new SOAPEnvelope(false, soapConstants);
        envelope.setMessageType(messageType);
        myElement = envelope;
    }

    public EnvelopeBuilder(SOAPEnvelope env, String messageType)
    {
        envelope = env ;
        envelope.setMessageType(messageType);
        myElement = envelope;
    }

    public SOAPEnvelope getEnvelope()
    {
        return envelope;
    }

    public void startElement(String namespace, String localName,
                             String prefix, Attributes attributes,
                             DeserializationContext context)
        throws SAXException
    {
        if (!localName.equals(Constants.ELEM_ENVELOPE))
            throw new SAXException(
                    Messages.getMessage("badTag00", localName));
        
        // See if we're only supporting a single SOAP version at this endpoint
        MessageContext msgContext = context.getMessageContext();
        SOAPConstants singleVersion = null;
        if (msgContext != null) {
            singleVersion = (SOAPConstants)msgContext.getProperty(
                                            Constants.MC_SINGLE_SOAP_VERSION); 
        }

        if (namespace.equals(Constants.URI_SOAP11_ENV)) {
            // SOAP 1.1
            soapConstants = SOAPConstants.SOAP11_CONSTANTS;
        } else if (namespace.equals(Constants.URI_SOAP12_ENV)) {
            // SOAP 1.2
            soapConstants = SOAPConstants.SOAP12_CONSTANTS;
        } else {
            soapConstants = null;
        }
        
        if ((soapConstants == null) ||
                (singleVersion != null && soapConstants != singleVersion)) {
            // Mismatch of some sort, either an unknown namespace or not
            // the one we want.  Send back an appropriate fault.
            
            // Right now we only send back SOAP 1.1 faults for this case.  Do
            // we want to send SOAP 1.2 faults back to SOAP 1.2 endpoints?
            soapConstants = SOAPConstants.SOAP11_CONSTANTS;
            if (singleVersion == null) singleVersion = soapConstants;
            
            try {
                AxisFault fault = new AxisFault(soapConstants.getVerMismatchFaultCodeQName(),
                    null, Messages.getMessage("versionMissmatch00"), null, null, null);

                SOAPHeaderElement newHeader = new
                                SOAPHeaderElement(soapConstants.getEnvelopeURI(),
                                                  Constants.ELEM_UPGRADE);

                // TODO: insert soap 1.1 upgrade header in case of soap 1.2 response if
                // axis supports both simultaneously
                MessageElement innerHeader = new
                                MessageElement(soapConstants.getEnvelopeURI(),
                                                  Constants.ELEM_SUPPORTEDENVELOPE);
                innerHeader.addAttribute(null, Constants.ATTR_QNAME,
                    new QName(singleVersion.getEnvelopeURI(), Constants.ELEM_ENVELOPE));

                newHeader.addChildElement(innerHeader);
                fault.addHeader(newHeader);

                throw new SAXException(fault);

            } catch (javax.xml.soap.SOAPException e) {
                throw new SAXException(e);
            }
        }

        // Indicate what version of SOAP we're using to anyone else involved
        // in processing this message.
        if(context.getMessageContext() != null)
            context.getMessageContext().setSOAPConstants(soapConstants);

        if (soapConstants == SOAPConstants.SOAP12_CONSTANTS &&
            attributes.getValue(Constants.URI_SOAP12_ENV, Constants.ATTR_ENCODING_STYLE) != null) {

            AxisFault fault = new AxisFault(Constants.FAULT_SOAP12_SENDER,
                null, Messages.getMessage("noEncodingStyleAttrAppear", "Envelope"), null, null, null);

            throw new SAXException(fault);
        }

        envelope.setPrefix(prefix);
        envelope.setNamespaceURI(namespace);
        envelope.setNSMappings(context.getCurrentNSMappings());
        envelope.setSoapConstants(soapConstants);
        context.pushNewElement(envelope);
    }

    public SOAPHandler onStartChild(String namespace,
                                    String localName,
                                    String prefix,
                                    Attributes attributes,
                                    DeserializationContext context)
        throws SAXException
    {
        QName thisQName = new QName(namespace, localName);
        if (thisQName.equals(soapConstants.getHeaderQName())) {
            if (gotHeader)
                throw new SAXException(Messages.getMessage("only1Header00"));

            gotHeader = true;
            return new HeaderBuilder(envelope);
        }

        if (thisQName.equals(soapConstants.getBodyQName())) {
            if (gotBody)
                throw new SAXException(Messages.getMessage("only1Body00"));

            gotBody = true;
            return new BodyBuilder(envelope);
        }

        if (!gotBody)
            throw new SAXException(Messages.getMessage("noCustomElems00"));

        if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
            throw new SAXException(Messages.getMessage("noElemAfterBody12"));
        }

        try {
            MessageElement element = new MessageElement(namespace, localName, prefix,
                                         attributes, context);

            if (element.getFixupDeserializer() != null)
                return (SOAPHandler)element.getFixupDeserializer();
        } catch (AxisFault axisFault) {
            throw new SAXException(axisFault);
        }

        return null;
    }

    public void onEndChild(String namespace, String localName,
                           DeserializationContext context)
    {
    }

    public void endElement(String namespace, String localName,
                           DeserializationContext context)
        throws SAXException
    {
        // Envelope isn't dirty yet by default...
        envelope.setDirty(false);
    }
}
