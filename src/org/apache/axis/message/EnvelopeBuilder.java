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
package org.apache.axis.message;

import org.apache.axis.Constants;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.Messages;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.apache.axis.AxisFault;
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

        if (namespace.equals(Constants.URI_SOAP11_ENV)) {
            // SOAP 1.1
            soapConstants = SOAPConstants.SOAP11_CONSTANTS;
        } else if (namespace.equals(Constants.URI_SOAP12_ENV)) {
            // SOAP 1.2
            soapConstants = SOAPConstants.SOAP12_CONSTANTS;
        } else {
            soapConstants = Constants.DEFAULT_SOAP_VERSION;

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
                    new QName(soapConstants.getEnvelopeURI(), Constants.ELEM_ENVELOPE));

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
