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

package org.apache.axis.transport.http;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.server.AxisServer;
import org.apache.axis.utils.Messages;

/**
 * The QSMethodHandler class is a handler which executes a given method from an
 * an AXIS service's WSDL definition when the query string "method" is
 * encountered in an AXIS servlet invocation.
 *
 * @author Curtiss Howard (code mostly from AxisServlet class)
 * @author Doug Davis (dug@us.ibm.com)
 * @author Steve Loughran
 */

public class QSMethodHandler extends AbstractQueryStringHandler  {

    /**
     * Performs the action associated with this particular query string
     * handler.
     *
     * @param msgContext a MessageContext object containing message context
     *        information for this query string handler.
     * @throws AxisFault if an error occurs.
     */

    public void invoke (MessageContext msgContext) throws AxisFault {
        // Obtain objects relevant to the task at hand from the provided
        // MessageContext's bag.

        configureFromContext(msgContext);
        AxisServer engine = (AxisServer) msgContext.getProperty
                (HTTPConstants.PLUGIN_ENGINE);
        PrintWriter writer = (PrintWriter) msgContext.getProperty
                (HTTPConstants.PLUGIN_WRITER);
        HttpServletRequest request = (HttpServletRequest)
                msgContext.getProperty (HTTPConstants.MC_HTTP_SERVLETREQUEST);
        HttpServletResponse response = (HttpServletResponse)
                msgContext.getProperty (HTTPConstants.MC_HTTP_SERVLETRESPONSE);


        String method = null;
        String args = "";
        Enumeration enum = request.getParameterNames();

        while (enum.hasMoreElements()) {
            String param = (String) enum.nextElement();
            if (param.equalsIgnoreCase ("method")) {
                method = request.getParameter (param);
            }

            else {
                args += "<" + param + ">" + request.getParameter (param) +
                        "</" + param + ">";
            }
        }

        if (method == null) {
            response.setContentType ("text/html");
            response.setStatus (HttpServletResponse.SC_BAD_REQUEST);

            writer.println ("<h2>" + Messages.getMessage ("error00") +
                    ":  " + Messages.getMessage ("invokeGet00") + "</h2>");
            writer.println ("<p>" + Messages.getMessage ("noMethod01") +
                    "</p>");
        }

        else {
            invokeEndpointFromGet (msgContext, response, writer, method, args);
        }
    }

    /**
     * invoke an endpoint from a get request by building an XML request and
     * handing it down. If anything goes wrong, we generate an XML formatted
     * axis fault
     * @param msgContext current message
     * @param response to return data
     * @param writer output stream
     * @param method method to invoke (may be null)
     * @param args argument list in XML form
     * @throws AxisFault iff something goes wrong when turning the response message
     * into a SOAP string.
     */

    private void invokeEndpointFromGet (MessageContext msgContext,
                                        HttpServletResponse response, PrintWriter writer, String method,
                                        String args) throws AxisFault {
        String body = "<" + method + ">" + args + "</" + method + ">";
        String msgtxt = "<SOAP-ENV:Envelope" +
                " xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<SOAP-ENV:Body>" + body + "</SOAP-ENV:Body>" +
                "</SOAP-ENV:Envelope>";
        ByteArrayInputStream istream =
                new ByteArrayInputStream (msgtxt.getBytes());
        Message responseMsg = null;

        try {
            AxisServer engine = (AxisServer) msgContext.getProperty
                    (HTTPConstants.PLUGIN_ENGINE);
            Message msg = new Message (istream, false);

            msgContext.setRequestMessage (msg);
            engine.invoke (msgContext);

            responseMsg = msgContext.getResponseMessage();

            //turn off caching for GET requests

            response.setHeader ("Cache-Control", "no-cache");
            response.setHeader ("Pragma", "no-cache");

            if (responseMsg == null) {
                //tell everyone that something is wrong

                throw new Exception (Messages.getMessage ("noResponse01"));
            }

        }

        catch (AxisFault fault) {
            processAxisFault (fault);

            configureResponseFromAxisFault (response, fault);

            if (responseMsg == null) {
                responseMsg = new Message (fault);
            }
        }

        catch (Exception e) {
            response.setStatus (HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseMsg = convertExceptionToAxisFault (e, responseMsg);
        }

        //this call could throw an AxisFault. We delegate it up, because
        //if we cant write the message there is not a lot we can do in pure SOAP terms.

        response.setContentType ("text/xml");

        writer.println (responseMsg.getSOAPPartAsString());
    }

}
