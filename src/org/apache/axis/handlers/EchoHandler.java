/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;

/**
 *
 * @author Doug Davis (dug@us.ibm.com)
 */
public class EchoHandler extends BasicHandler {
    protected static Log log =
        LogFactory.getLog(EchoHandler.class.getName());

    public void invoke(MessageContext msgContext) throws AxisFault {
        log.debug("Enter: EchoHandler::invoke");
        try {
            Message  msg = msgContext.getRequestMessage();
            SOAPEnvelope env = (SOAPEnvelope) msg.getSOAPEnvelope();
            msgContext.setResponseMessage( new Message( env ) );
        }
        catch( Exception e ) {
            log.error( Messages.getMessage("exception00"), e );
            throw AxisFault.makeFault(e);
        }
        log.debug("Exit: EchoHandler::invoke");
    }

    public String wsdlStart1 = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" +
            "<definitions xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" \n" +
            "xmlns:http=\"http://schemas.xmlsoap.org/wsdl/http/\" \n" +
            "xmlns:mime=\"http://schemas.xmlsoap.org/wsdl/mime/\" \n" +
            "xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\" \n" +
            "xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" \n" +
            "xmlns:s0=\"http://tempuri.org/EchoService\" \n"+
            "targetNamespace=\"http://tempuri.org/EchoService\" \n" +
            "xmlns=\"http://schemas.xmlsoap.org/wsdl/\">" +
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


    public String wsdlStart = 
    "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n"+
    "<wsdl:definitions targetNamespace=\"http://handlers.apache.org/EchoService\" \n"+ 
    "xmlns=\"http://schemas.xmlsoap.org/wsdl/\" \n"+
    "xmlns:apachesoap=\"http://xml.apache.org/xml-soap\"  \n"+
    "xmlns:impl=\"http://handlers.apache.org/EchoService\"  \n"+
    "xmlns:intf=\"http://handlers.apache.org/EchoService\"  \n"+
    "xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\"  \n"+
    "xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\"  \n"+
    "xmlns:wsdlsoap=\"http://schemas.xmlsoap.org/wsdl/soap/\"  \n"+
    "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"> \n"+
    "<wsdl:types> \n"+
    "<schema targetNamespace=\"http://handlers.apache.org/EchoService\" \n" +
    "xmlns=\"http://www.w3.org/2001/XMLSchema\"> \n"+
    "<xsd:import namespace=\"http://schemas.xmlsoap.org/soap/encoding/\"/> \n"+
    "<xsd:complexType name=\"echoElements\"> \n" +
    " <xsd:sequence> \n" + 
    "   <xsd:element name=\"content\" type=\"xsd:anyType\"/> \n"+
    " </xsd:sequence>\n"+
    "</xsd:complexType> \n" +
    "<xsd:complexType name=\"echoElementsReturn\"> \n" +
    " <xsd:sequence> \n" + 
    "   <xsd:element name=\"content\" type=\"xsd:anyType\"/> \n"+
    " </xsd:sequence> \n" +
    "</xsd:complexType> \n" +
    "</schema> \n"+
    "</wsdl:types> \n"+
    "  <wsdl:message name=\"echoElementsResponse\"> \n"+
    "    <wsdl:part type=\"impl:echoElementsReturn\" name=\"echoElementsReturn\"/> \n"+
    "  </wsdl:message> \n"+
    "  <wsdl:message name=\"echoElementsRequest\"> \n"+
    "    <wsdl:part type=\"impl:echoElements\" name=\"part\"/> \n"+
    "  </wsdl:message> \n"+
    "  <wsdl:portType name=\"EchoService\"> \n"+
    "    <wsdl:operation name=\"doIt\"> \n"+
    "      <wsdl:input message=\"impl:echoElementsRequest\" name=\"echoElementsRequest\"/> \n"+
    "      <wsdl:output message=\"impl:echoElementsResponse\" name=\"echoElementsResponse\"/> \n"+
    "    </wsdl:operation> \n"+
    "  </wsdl:portType> \n"+
    "  <wsdl:binding name=\"EchoServiceSoapBinding\" type=\"impl:EchoService\"> \n"+
    "    <wsdlsoap:binding style=\"document\" transport=\"http://schemas.xmlsoap.org/soap/http\"/> \n"+
    "    <wsdl:operation name=\"doIt\"> \n"+
    "      <wsdlsoap:operation soapAction=\"\"/> \n"+
    "      <wsdl:input name=\"echoElementsRequest\"> \n"+
    "        <wsdlsoap:body namespace=\"http://handlers.apache.org/EchoService\" use=\"literal\"/> \n"+
    "      </wsdl:input> \n"+
    "      <wsdl:output name=\"echoElementsResponse\"> \n"+
    "        <wsdlsoap:body namespace=\"http://handlers.apache.org/EchoService\" use=\"literal\"/> \n"+
    "      </wsdl:output> \n"+
    "    </wsdl:operation> \n"+
    "  </wsdl:binding> \n"+
    "  <wsdl:service name=\"EchoService\"> \n"+
    "    <wsdl:port binding=\"impl:EchoServiceSoapBinding\" name=\"EchoService\"> \n"+
    "      <wsdlsoap:address location=\"";
          
        String wsdlEnd =  " \"/></wsdl:port>\n" +
                          "</wsdl:service>\n" +
                          "</wsdl:definitions>\n";


    public void generateWSDL(MessageContext msgContext) throws AxisFault {
        try {
            String url = msgContext.getStrProp(MessageContext.TRANS_URL);
            String wsdlString = wsdlStart + url + wsdlEnd;
            Document doc = XMLUtils.newDocument(new ByteArrayInputStream(wsdlString.getBytes("UTF-8")));
            msgContext.setProperty("WSDL", doc);
        } catch (Exception e) {
            throw AxisFault.makeFault(e);
        }
    }
};
