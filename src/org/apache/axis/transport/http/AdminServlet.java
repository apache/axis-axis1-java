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

package org.apache.axis.transport.http ;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.axis.server.AxisServer;
import org.apache.axis.utils.JavaUtils;

import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

/**
 * Proof-of-concept "management" servlet for Axis.
 * 
 * Point a browser here to administer the Axis installation.
 * 
 * Right now just starts and stops the server.
 * 
 * @author Glen Daniels (gdaniels@macromedia.com)
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
            if (!isProduction()) {
                //only in dev mode do these command work
                if (cmd.equals("start")) {
                    log.info(JavaUtils.getMessage("adminServiceStart", callerIP));
                    server.start();
                }
                else if (cmd.equals("stop")) {
                    log.info(JavaUtils.getMessage("adminServiceStop", callerIP));
                    server.stop();
                }
            } else {
                //in production we log a hostile probe. Remember: logs can be
                //used for DoS attacks themselves.
                log.info(JavaUtils.getMessage("adminServiceDeny", callerIP));
            }
        }

        // display status
        if (server.isRunning()) {
            buffer.append(JavaUtils.getMessage("serverRun00"));
        }
        else {
            buffer.append(JavaUtils.getMessage("serverStop00"));
        }
        //add commands
        if(!isProduction()) {
            buffer.append("<p><a href=\"AdminServlet?cmd=start\">start server</a>\n");
            buffer.append("<p><a href=\"AdminServlet?cmd=stop\">stop server</a>\n");
        }
        //print load
        buffer.append("<p>");
        buffer.append(JavaUtils.getMessage("adminServiceLoad",
                Integer.toString(getLoadCounter())));
        buffer.append("\n</body></html>\n");
        response.getWriter().print( new String(buffer) );
    }
}
