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

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.server.AxisServer;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.net.HttpURLConnection;

/**
 * The QSWSDLHandler class is a handler which provides an AXIS service's WSDL
 * document when the query string "wsdl" is encountered in an AXIS servlet
 * invocation.
 *
 * @author Curtiss Howard (code mostly from AxisServlet class)
 * @author Doug Davis (dug@us.ibm.com)
 * @author Steve Loughran
 */

public class QSWSDLHandler extends AbstractQueryStringHandler {

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

        try {
            engine.generateWSDL (msgContext);

            Document doc = (Document) msgContext.getProperty ("WSDL");

            if (doc != null) {
                response.setContentType ("text/xml");
                XMLUtils.PrettyDocumentToWriter (doc, writer);
            }

            else {
                if (log.isDebugEnabled()) {
                    log.debug ("processWsdlRequest: failed to create WSDL");
                }

                reportNoWSDL (response, writer, "noWSDL02", null);
            }
        }

        catch (AxisFault axisFault) {
            //the no-service fault is mapped to a no-wsdl error

            if (axisFault.getFaultCode().equals
                    (Constants.QNAME_NO_SERVICE_FAULT_CODE)) {
                //which we log

                processAxisFault (axisFault);

                //then report under a 404 error

                response.setStatus (HttpURLConnection.HTTP_NOT_FOUND);

                reportNoWSDL (response, writer, "noWSDL01", axisFault);
            }

            else {
                //all other faults get thrown

                throw axisFault;
            }
        }
    }

    /**
     * report that we have no WSDL
     * @param res
     * @param writer
     * @param moreDetailCode optional name of a message to provide more detail
     * @param axisFault optional fault string, for extra info at debug time only
     */

    private void reportNoWSDL (HttpServletResponse res, PrintWriter writer,
                               String moreDetailCode, AxisFault axisFault) {
        res.setStatus (HttpURLConnection.HTTP_NOT_FOUND);
        res.setContentType ("text/html");

        writer.println ("<h2>" + Messages.getMessage ("error00") + "</h2>");
        writer.println ("<p>" + Messages.getMessage ("noWSDL00") + "</p>");

        if (moreDetailCode != null) {
            writer.println("<p>" + Messages.getMessage (moreDetailCode)
                    + "</p>");
        }

        if (axisFault != null && isDevelopment()) {
            //dev systems only give fault dumps

            writeFault (writer, axisFault);
        }
    }

}
