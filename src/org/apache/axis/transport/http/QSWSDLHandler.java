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
                response.setContentType ("text/xml; charset="+XMLUtils.getEncoding().toLowerCase());
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
