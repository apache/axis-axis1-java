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

/**
 *
 * @author Glen Daniels (gdaniels@allaire.com)
 */

import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.enum.Style;

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.components.i18n.Messages;
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
        super.startElement(namespace, localName, prefix, attributes, context);
        if (!context.isDoneParsing()) {
            envelope.setBody((SOAPBody)myElement);
        }
    }

    public MessageElement makeNewElement(String namespace, String localName,
                                         String prefix, Attributes attributes,
                                         DeserializationContext context) {
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
             if(msgContext != null)
                operations = msgContext.getPossibleOperationsByQName(qname);
        } catch (org.apache.axis.AxisFault e) {
            // SAXException is already known to this method, so I
            // don't have an exception-handling propogation explosion.
            throw new SAXException(e);
        }

        Style style = operations == null ? Style.RPC : operations[0].getStyle();

        /** Now we make a plain SOAPBodyElement IF we either:
         * a) have an non-root element, or
         * b) have a non-RPC service
         */
        if (localName.equals(Constants.ELEM_FAULT) &&
            namespace.equals(msgContext.getSOAPConstants().getEnvelopeURI())) {
            element = new SOAPFault(namespace, localName, prefix,
                                           attributes, context);
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

        SOAPConstants soapConstants = context.getMessageContext() == null ?
                                        SOAPConstants.SOAP11_CONSTANTS :
                                        context.getMessageContext().getSOAPConstants();
        if (element == null) {
            if (style == Style.RPC &&
                soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
                throw new SAXException(Messages.getMessage("onlyOneBodyFor12"));
            }
            element = new SOAPBodyElement(namespace, localName, prefix,
                                      attributes, context);
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
