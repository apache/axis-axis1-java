/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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

package org.apache.axis.client ;

import java.net.*;
import java.io.*;
import java.util.*;

import org.apache.axis.*;
import org.apache.axis.server.SimpleAxisEngine;
import org.apache.axis.transport.http.HTTPConstants;

/** This is a quick in-memory client to demonstrate how transport-dependent
 * routing works.  It pretends to be the AxisServlet with a SOAPAction header
 * of "EchoService".  This ends up calling the AxisServlet chain, which
 * sets the new TARGET_SERVICE to be the value of the SOAPAction header, and then
 * uses the Router handler to dispatch to the service.
 *
 * @author Glen Daniels (gdaniels@allaire.com)
 */
public class TransportRoutingClient {

    public static void main(String args[]) {

        String msg = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/1999/XMLSchema\">\n" +
                     "<SOAP-ENV:Header>\n" +
                     "<t:Transaction xmlns:t=\"some-URI\" " +
                     "SOAP-ENV:mustUnderstand=\"1\"> 5 </t:Transaction>" +
                     "</SOAP-ENV:Header>\n" +
                     "<SOAP-ENV:Body>\n" +
                     "<m:GetLastTradePrice xmlns:m=\"Some-URI\">" +
                     "<symbol>IBM</symbol>" +
                     "</m:GetLastTradePrice>" +
                     "</SOAP-ENV:Body>\n" +
                     "</SOAP-ENV:Envelope>" ;

        try {
            org.apache.axis.utils.Debug.setDebugLevel(10);
            SimpleAxisEngine engine = new SimpleAxisEngine();
            MessageContext msgContext = new MessageContext();
            Message message = new Message(msg, "String");
            msgContext.setIncomingMessage(message);
            
            /** The transport is http.
             */
            msgContext.setProperty(MessageContext.TRANS_ID, HTTPConstants.TRANSPORT_ID);
            
            /** If we were a real servlet, we might have made the SOAPAction
             * HTTP header available like this...
             */
            msgContext.setProperty(HTTPConstants.MC_HTTP_SOAPACTION, "EchoService");

            engine.init();
            engine.invoke(msgContext);
            
            System.out.println(msgContext.getOutgoingMessage().getAs("String"));
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    };

};
