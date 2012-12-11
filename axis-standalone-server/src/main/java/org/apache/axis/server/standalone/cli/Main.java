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
package org.apache.axis.server.standalone.cli;

import java.io.PrintWriter;

import org.apache.axis.server.standalone.ServerException;
import org.apache.axis.server.standalone.StandaloneAxisServer;

/**
 * Main class to run the stand-alone server from the command line.
 * 
 * @author Andreas Veithen
 */
public class Main {
    public static void main(String[] args) {
        StandaloneAxisServer server = new StandaloneAxisServer();
        
        if (args.length == 0) {
            Configurator.INSTANCE.printHelp(new PrintWriter(System.out), Main.class.getName());
            return;
        }
        
        try {
            Configurator.INSTANCE.configure(server, args);
        } catch (ConfiguratorException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
            return; // Make compiler happy
        }
        
        try {
            server.init();
            server.start();
            try {
                server.awaitQuitRequest();
            } catch (InterruptedException ex) {
                // Just continue and stop the server
            }
            server.stop();
        } catch (ServerException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
}
