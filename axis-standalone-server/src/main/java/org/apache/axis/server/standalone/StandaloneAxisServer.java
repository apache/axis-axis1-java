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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.SessionHandler;

/**
 * Jetty based stand-alone Axis server.
 * 
 * @author Andreas Veithen
 */
public class StandaloneAxisServer {
    public static void main(String[] args) throws Exception {
        Options options = new Options();
        Option portOption = new Option("p", true, "the HTTP port");
        portOption.setArgName("port");
        portOption.setRequired(true);
        options.addOption(portOption);
        
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
        
        Server server = new Server(port);
        Context context = new Context(server, "/axis");
        context.setSessionHandler(new SessionHandler());
        QuitListener quitListener = new QuitListener();
        context.setAttribute(QuitHandler.QUIT_LISTENER, quitListener);
        context.addServlet(StandaloneAxisServlet.class, "/services/*");
        server.start();
        try {
            quitListener.awaitQuitRequest();
        } catch (InterruptedException ex) {
            // Just continue and stop the server
        }
        server.stop();
    }
}
