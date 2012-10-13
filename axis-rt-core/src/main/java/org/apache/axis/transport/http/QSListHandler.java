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

import java.io.PrintWriter;
import java.net.HttpURLConnection;

import javax.servlet.http.HttpServletResponse;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.server.AxisServer;
import org.apache.axis.utils.Admin;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;

/**
 * The QSListHandler class is a handler which lists the AXIS Server's
 * configuration when the query string "list" is encountered in an AXIS servlet
 * invocation.
 *
 * @author Curtiss Howard (code mostly from AxisServlet class)
 * @author Doug Davis (dug@us.ibm.com)
 * @author Steve Loughran
 */

public class QSListHandler extends AbstractQueryStringHandler  {
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
          
          boolean enableList = ((Boolean) msgContext.getProperty
               (HTTPConstants.PLUGIN_ENABLE_LIST)).booleanValue();
          AxisServer engine = (AxisServer) msgContext.getProperty
               (HTTPConstants.PLUGIN_ENGINE);
          PrintWriter writer = (PrintWriter) msgContext.getProperty
               (HTTPConstants.PLUGIN_WRITER);
          HttpServletResponse response = (HttpServletResponse)
               msgContext.getProperty (HTTPConstants.MC_HTTP_SERVLETRESPONSE);
          
          if (enableList) {
               Document doc = Admin.listConfig (engine);
               
               if (doc != null) {
                    response.setContentType ("text/xml");
                    XMLUtils.DocumentToWriter (doc, writer);
               }
               
               else {
                    //error code is 404
                    
                    response.setStatus (HttpURLConnection.HTTP_NOT_FOUND);
                    response.setContentType ("text/html");
                    
                    writer.println ("<h2>" + Messages.getMessage ("error00") +
                         "</h2>");
                    writer.println ("<p>" + Messages.getMessage ("noDeploy00") +
                         "</p>");
               }
          }
          
          else {
               // list not enable, return error
               //error code is, what, 401
               
               response.setStatus (HttpURLConnection.HTTP_FORBIDDEN);
               response.setContentType ("text/html");
               
               writer.println ("<h2>" + Messages.getMessage ("error00") +
                    "</h2>");
               writer.println ("<p><i>?list</i> " +
                    Messages.getMessage ("disabled00") + "</p>");
          }
     }
}
