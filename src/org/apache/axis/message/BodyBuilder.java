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

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.enum.Style;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;

public class BodyBuilder extends SOAPHandler
{
    protected static Log log =
        LogFactory.getLog(BodyBuilder.class.getName());

    boolean gotRPCElement = false;

    private SOAPEnvelope envelope;

    BodyBuilder(SOAPEnvelope envelope)
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
                null, Messages.getMessage("noEncodingStyleAttrAppear", "Body"), null, null, null);

            throw new SAXException(fault);
        }

        // make a new body element
        if (!context.isDoneParsing()) {
            if (!context.isProcessingRef()) {
                if (myElement == null) {
                    try {
                        myElement = new SOAPBody(namespace, localName, prefix,
                                            attributes, context, envelope.getSOAPConstants());
                    } catch (AxisFault axisFault) {
                        throw new SAXException(axisFault);
                    }
                }
                context.pushNewElement(myElement);
            }
            envelope.setBody((SOAPBody)myElement);
        }
    }

    // FIX: do we need this method ?
    public MessageElement makeNewElement(String namespace, String localName,
                                         String prefix, Attributes attributes,
                                         DeserializationContext context)
        throws AxisFault {
        SOAPConstants soapConstants = context.getMessageContext() == null ?
                                        SOAPConstants.SOAP11_CONSTANTS :
                                        context.getMessageContext().getSOAPConstants();
        return new SOAPBody(namespace,
                            localName,
                            prefix,
                            attributes,
                            context,
                            soapConstants);
    }

    public SOAPHandler onStartChild(String namespace,
                                     String localName,
                                     String prefix,
                                     Attributes attributes,
                                     DeserializationContext context)
        throws SAXException
    {
        SOAPBodyElement element = null;
        if (log.isDebugEnabled()) {
            log.debug("Enter: BodyBuilder::onStartChild()");
        }

        QName qname = new QName(namespace, localName);
        SOAPHandler handler = null;

        /** We're about to create a body element.  So we really need
         * to know at this point if this is an RPC service or not.  It's
         * possible that no one has set the service up until this point,
         * so if that's the case we should attempt to set it based on the
         * namespace of the first root body element.  Setting the
         * service may (should?) result in setting the service
         * description, which can then tell us what to create.
         */
        boolean isRoot = true;
        String root = attributes.getValue(Constants.URI_DEFAULT_SOAP_ENC,
                                          Constants.ATTR_ROOT);
        if ((root != null) && root.equals("0")) isRoot = false;

        MessageContext msgContext = context.getMessageContext();
        OperationDesc [] operations = null;
        try {
            if(msgContext != null) {
                 operations = msgContext.getPossibleOperationsByQName(qname);
            }

            // If there's only one match, set it in the MC now
            if ((operations != null) && (operations.length == 1))
                msgContext.setOperation(operations[0]);
        } catch (org.apache.axis.AxisFault e) {
            // SAXException is already known to this method, so I
            // don't have an exception-handling propogation explosion.
            throw new SAXException(e);
        }

        Style style = operations == null ? Style.RPC : operations[0].getStyle();
        SOAPConstants soapConstants = context.getMessageContext() == null ?
                                        SOAPConstants.SOAP11_CONSTANTS :
                                        context.getMessageContext().getSOAPConstants();

        /** Now we make a plain SOAPBodyElement IF we either:
         * a) have an non-root element, or
         * b) have a non-RPC service
         */
        if (localName.equals(Constants.ELEM_FAULT) &&
            namespace.equals(soapConstants.getEnvelopeURI())) {
            try {
                element = new SOAPFault(namespace, localName, prefix,
                                               attributes, context);
            } catch (AxisFault axisFault) {
                throw new SAXException(axisFault);
            }
            element.setEnvelope(context.getEnvelope());
            handler = new SOAPFaultBuilder((SOAPFault)element,
                                           context);
        } else if (!gotRPCElement) {
            if (isRoot && (style != Style.MESSAGE)) {
                gotRPCElement = true;

                try {

                    element = new RPCElement(namespace, localName, prefix,
                            attributes, context, operations);

                } catch (org.apache.axis.AxisFault e) {
                    // SAXException is already known to this method, so I
                    // don't have an exception-handling propogation explosion.
                    //
                    throw new SAXException(e);
                }

                // Only deserialize this way if there is a unique operation
                // for this QName.  If there are overloads,
                // we'll need to start recording.  If we're making a high-
                // fidelity recording anyway, don't bother (for now).
                if (msgContext != null && !msgContext.isHighFidelity() &&
                        (operations == null || operations.length == 1)) {
                    ((RPCElement)element).setNeedDeser(false);
                    handler = new RPCHandler((RPCElement)element, false);
                    if (operations != null) {
                        ((RPCHandler)handler).setOperation(operations[0]);
                        msgContext.setOperation(operations[0]);
                    }
                }
            }
        }

        if (element == null) {
            if ((style == Style.RPC) &&
                soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
                throw new SAXException(Messages.getMessage("onlyOneBodyFor12"));
            }
            try {
                element = new SOAPBodyElement(namespace, localName, prefix,
                                          attributes, context);
            } catch (AxisFault axisFault) {
                throw new SAXException(axisFault);
            }
            if (element.getFixupDeserializer() != null)
                handler = (SOAPHandler)element.getFixupDeserializer();
        }

        if (handler == null)
            handler = new SOAPHandler();

        handler.myElement = element;

        //context.pushNewElement(element);

        if (log.isDebugEnabled()) {
            log.debug("Exit: BodyBuilder::onStartChild()");
        }
        return handler;
    }

    public void onEndChild(String namespace, String localName,
                           DeserializationContext context)
    {
    }
}
