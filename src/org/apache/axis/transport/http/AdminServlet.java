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

package org.apache.axis.transport.http ;

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.server.AxisServer;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Proof-of-concept "management" servlet for Axis.
 * 
 * Point a browser here to administer the Axis installation.
 * 
 * Right now just starts and stops the server.
 * 
 * @author Glen Daniels (gdaniels@apache.org)
 * @author Steve Loughran
 * xdoclet tags are not active yet; keep web.xml in sync
 * @web.servlet name="AdminServlet"  display-name="Axis Admin Servlet"  load-on-startup="100"
 * @web.servlet-mapping url-pattern="/servlet/AdminServlet"
 */
public class AdminServlet extends AxisServletBase {

    private static Log log =
            LogFactory.getLog(AxisServlet.class.getName());


    /**
     * handle a GET request. Commands are only valid when not in production mode
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        response.setContentType("text/html");
        StringBuffer buffer=new StringBuffer(512);
        buffer.append("<html><head><title>Axis</title></head><body>\n");
        //REVISIT: what happens if there is no engine?
        AxisServer server = getEngine();

        //process command
        String cmd = request.getParameter("cmd");
        if (cmd != null) {
            //who called?
            String callerIP=request.getRemoteAddr();
            if (isDevelopment()) {
                //only in dev mode do these command work
                if (cmd.equals("start")) {
                    log.info(Messages.getMessage("adminServiceStart", callerIP));
                    server.start();
                }
                else if (cmd.equals("stop")) {
                    log.info(Messages.getMessage("adminServiceStop", callerIP));
                    server.stop();
                }
            } else {
                //in production we log a hostile probe. Remember: logs can be
                //used for DoS attacks themselves.
                log.info(Messages.getMessage("adminServiceDeny", callerIP));
            }
        }

        // display status
        if (server.isRunning()) {
            buffer.append(Messages.getMessage("serverRun00"));
        }
        else {
            buffer.append(Messages.getMessage("serverStop00"));
        }
        //add commands
        if(isDevelopment()) {
            buffer.append("<p><a href=\"AdminServlet?cmd=start\">start server</a>\n");
            buffer.append("<p><a href=\"AdminServlet?cmd=stop\">stop server</a>\n");
        }
        //print load
        buffer.append("<p>");
        buffer.append(Messages.getMessage("adminServiceLoad",
                Integer.toString(getLoadCounter())));
        buffer.append("\n</body></html>\n");
        response.getWriter().print( new String(buffer) );
    }
}
