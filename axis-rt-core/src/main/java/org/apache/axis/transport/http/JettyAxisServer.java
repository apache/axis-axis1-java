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

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.Options;
import org.apache.commons.logging.Log;
import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpServer;
import org.mortbay.http.SocketListener;
import org.mortbay.http.handler.ResourceHandler;
import org.mortbay.jetty.servlet.ServletHandler;

import java.net.MalformedURLException;

public class JettyAxisServer {
    protected static Log log =
            LogFactory.getLog(JettyAxisServer.class.getName());

    /**
     * Jetty HTTP Server *
     */
    HttpServer server = new HttpServer();

    /**
     * Socket Listener *
     */
    SocketListener listener = new SocketListener();

    /**
     * HTTP Context
     */
    HttpContext context = new HttpContext();

    public JettyAxisServer() {
        // Create a context 
        context.setContextPath("/axis/*");
        server.addContext(context);
      
        // Create a servlet container
        ServletHandler servlets = new ServletHandler();
        context.addHandler(servlets);

        // Map a servlet onto the container
        servlets.addServlet("AdminServlet", "/servlet/AdminServlet",
                "org.apache.axis.transport.http.AdminServlet");
        servlets.addServlet("AxisServlet", "/servlet/AxisServlet",
                "org.apache.axis.transport.http.AxisServlet");
        servlets.addServlet("AxisServlet", "/services/*",
                "org.apache.axis.transport.http.AxisServlet");
        servlets.addServlet("AxisServlet", "*.jws",
                "org.apache.axis.transport.http.AxisServlet");
        context.addHandler(new ResourceHandler());
    }

    /**
     * Set the port
     *
     * @param port
     */
    public void setPort(int port) {
        listener.setPort(port);
        server.addListener(listener);
    }

    /**
     * Set the resource base
     *
     * @param dir
     */
    public void setResourceBase(String dir) {
        context.setResourceBase(dir);
    }

    /**
     * Start the server
     *
     * @throws Exception
     */
    public void start() throws Exception {
        server.start();
        log.info(
                Messages.getMessage("start00", "JettyAxisServer",
                        new Integer(listener.getServerSocket().getLocalPort()).toString()));
    }

    public static void main(String[] args) {
        Options opts = null;
        try {
            opts = new Options(args);
        } catch (MalformedURLException e) {
            log.error(Messages.getMessage("malformedURLException00"), e);
            return;
        }
        JettyAxisServer server = new JettyAxisServer();
        server.setPort(opts.getPort());
        String dir = opts.isValueSet('d');
        if (dir == null) {
            // Serve static content from the context
            dir = System.getProperty("jetty.home", ".") + "/webapps/axis/";
        }
        server.setResourceBase(dir);
        
        // Start the http server
        try {
            server.start();
        } catch (Exception e) {
            log.error(Messages.getMessage("exception00"), e);
        }
    }
}