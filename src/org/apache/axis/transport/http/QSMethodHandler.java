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
