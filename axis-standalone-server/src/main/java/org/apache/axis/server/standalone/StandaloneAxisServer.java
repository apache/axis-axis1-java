/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.axis.server.standalone;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.servlet.ServletMapping;
import org.mortbay.jetty.servlet.SessionHandler;
import org.mortbay.resource.Resource;
import org.mortbay.resource.ResourceCollection;

/**
 * Jetty based stand-alone Axis server.
 * 
 * @author Andreas Veithen
 */
public class StandaloneAxisServer {
    public static void main(String[] args) throws Exception {
        Options options = new Options();
        
        {
            Option option = new Option("p", true, "the HTTP port");
            option.setArgName("port");
            option.setRequired(true);
            options.addOption(option);
        }
        
        {
            Option option = new Option("w", true, "the work directory");
            option.setArgName("dir");
            option.setRequired(true);
            options.addOption(option);
        }
        
        {
            Option option = new Option("j", true, "a list of directories to look up JWS files from");
            option.setArgName("dirs");
            options.addOption(option);
        }
        
        if (args.length == 0) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(StandaloneAxisServer.class.getName(), options);
            return;
        }
        
        CommandLineParser parser = new GnuParser();
        CommandLine cmdLine;
        try {
            cmdLine = parser.parse(options, args);
        } catch (ParseException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
            return; // Make compiler happy
        }
        
        int port = Integer.parseInt(cmdLine.getOptionValue("p"));
        
        List resources = new ArrayList();
        
        // Add the work dir as a resource so that Axis can create its server-config.wsdd file there
        File workDir = new File(cmdLine.getOptionValue("w"));
        new File(workDir, "WEB-INF").mkdir();
        resources.add(Resource.newResource(workDir.getAbsolutePath()));
        
        boolean enableJWS;
        if (cmdLine.hasOption("j")) {
            String[] jwsDirs = cmdLine.getOptionValue("j").split(File.pathSeparator);
            for (int i=0; i<jwsDirs.length; i++) {
                resources.add(Resource.newResource(jwsDirs[i]));
            }
            enableJWS = true;
        } else {
            enableJWS = false;
        }
        
        Server server = new Server(port);
        server.setGracefulShutdown(1000);
        Context context = new Context(server, "/axis");
        context.setBaseResource(new ResourceCollection((Resource[])resources.toArray(new Resource[resources.size()])));
        context.setSessionHandler(new SessionHandler());
        QuitListener quitListener = new QuitListener();
        context.setAttribute(QuitHandler.QUIT_LISTENER, quitListener);
        ServletHandler servletHandler = context.getServletHandler();
        ServletHolder axisServlet = new ServletHolder(StandaloneAxisServlet.class);
        axisServlet.setName("AxisServlet");
        servletHandler.addServlet(axisServlet);
        {
            ServletMapping mapping = new ServletMapping();
            mapping.setServletName("AxisServlet");
            mapping.setPathSpec("/services/*");
            servletHandler.addServletMapping(mapping);
        }
        if (enableJWS) {
            ServletMapping mapping = new ServletMapping();
            mapping.setServletName("AxisServlet");
            mapping.setPathSpec("*.jws");
            servletHandler.addServletMapping(mapping);
        }
        server.start();
        try {
            quitListener.awaitQuitRequest();
        } catch (InterruptedException ex) {
            // Just continue and stop the server
        }
        server.stop();
    }
}
