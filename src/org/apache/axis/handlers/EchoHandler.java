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

package org.apache.axis.handlers ;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.XMLUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.w3c.dom.Document;

/**
 *
 * @author Doug Davis (dug@us.ibm.com)
 */
public class EchoHandler extends BasicHandler {
    protected static Log log =
        LogFactory.getLog(EchoHandler.class.getName());

    public void invoke(MessageContext msgContext) throws AxisFault {
        log.debug(JavaUtils.getMessage("enter00", "EchoHandler::invoke") );
        try {
            Message  msg = msgContext.getRequestMessage();
            SOAPEnvelope env = (SOAPEnvelope) msg.getSOAPEnvelope();
            msgContext.setResponseMessage( new Message( env ) );
        }
        catch( Exception e ) {
            log.error( e );
            throw AxisFault.makeFault(e);
        }
        log.debug(JavaUtils.getMessage("exit00", "EchoHandler::invoke") );
    }

    public String wsdlStart = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" +
            "<definitions xmlns:s=\"http://www.w3.org/2001/XMLSchema\" xmlns:http=\"http://schemas.xmlsoap.org/wsdl/http/\" xmlns:mime=\"http://schemas.xmlsoap.org/wsdl/mime/\" xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:s0=\"http://tempuri.org/EchoService\" targetNamespace=\"http://tempuri.org/EchoService\" xmlns=\"http://schemas.xmlsoap.org/wsdl/\">" +
            "<message name=\"request\">" +
            "<part name=\"content\" type=\"xsd:anyType\" />" +
            "</message>" +
            "<message name=\"response\">" +
            "<part name=\"content\" element=\"xsd:anyType\" />" +
            "</message>" +
            "<portType name=\"EchoSoap\">" +
            "<operation name=\"doIt\">" +
            "<input message=\"s0:request\" /> " +
            "<output message=\"s0:response\" /> " +
            "</operation>" +
            "</portType>" +
            "<binding name=\"EchoSoap\" type=\"s0:EchoSoap\">" +
            "<soap:binding transport=\"http://schemas.xmlsoap.org/soap/http\" style=\"document\" />" +
            "<operation name=\"doIt\">" +
            "<soap:operation soapAction=\"http://tempuri.org/Echo\" style=\"document\" />" +
            "<input>" +
            "<soap:body use=\"literal\" />" +
            "</input>" +
            "<output>" +
            "<soap:body use=\"literal\" />" +
            "</output>" +
            "</operation>" +
            "</binding>" +
            "<service name=\"Echo\">" +
            "<port name=\"EchoSoap\" binding=\"s0:EchoSoap\">" +
            "<soap:address location=\"http://";

    String wsdlEnd = "\" />" +
            "</port>" +
            "</service>" +
            "</definitions>";

    public void generateWSDL(MessageContext msgContext) throws AxisFault {
        String url = msgContext.getStrProp("hostname"); // !!! Get this for real
        String wsdlString = wsdlStart + url + wsdlEnd;
        Document doc = XMLUtils.newDocument(wsdlString);
        msgContext.setProperty("WSDL", doc);
    }
};
