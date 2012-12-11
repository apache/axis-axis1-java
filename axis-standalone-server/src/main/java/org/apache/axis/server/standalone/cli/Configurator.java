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

import java.io.File;
import java.io.PrintWriter;

import org.apache.axis.server.standalone.StandaloneAxisServer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public final class Configurator {
    public static final Configurator INSTANCE = new Configurator();
    
    private final Options options;
    
    private Configurator() {
        options = new Options();
        
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
        
        {
            Option option = new Option("m", true, "the maximum number of concurrently active sessions");
            option.setArgName("count");
            options.addOption(option);
        }
    }
    
    public void printHelp(PrintWriter pw, String app) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printUsage(pw, 100, app, options);
        formatter.printOptions(pw, 100, options, 1, 2);
        pw.flush();
    }
    
    public void configure(StandaloneAxisServer server, String[] args) throws ConfiguratorException {
        CommandLineParser parser = new GnuParser();
        CommandLine cmdLine;
        try {
            cmdLine = parser.parse(options, args);
        } catch (ParseException ex) {
            throw new ConfiguratorException(ex.getMessage());
        }
        
        server.setPort(Integer.parseInt(cmdLine.getOptionValue("p")));
        server.setWorkDir(new File(cmdLine.getOptionValue("w")));
        if (cmdLine.hasOption("m")) {
            server.setMaxSessions(Integer.parseInt(cmdLine.getOptionValue("m")));
        }
        if (cmdLine.hasOption("j")) {
            String[] jwsDirStrings = cmdLine.getOptionValue("j").split(File.pathSeparator);
            File[] jwsDirs = new File[jwsDirStrings.length];
            for (int i=0; i<jwsDirStrings.length; i++) {
                jwsDirs[i] = new File(jwsDirStrings[i]);
            }
            server.setJwsDirs(jwsDirs);
        }
    }
}
