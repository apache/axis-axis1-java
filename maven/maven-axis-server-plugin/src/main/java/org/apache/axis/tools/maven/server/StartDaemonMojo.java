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
package org.apache.axis.tools.maven.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Start a daemon.
 * 
 * @goal start-daemon
 * @phase pre-integration-test
 * @requiresDependencyResolution test
 */
public class StartDaemonMojo extends AbstractStartProcessMojo {
    /**
     * The daemon class.
     * 
     * @parameter
     * @required
     */
    private String daemonClass;
    
    /**
     * The arguments to be passed to the main class.
     * 
     * @parameter
     */
    private String[] args;
    
    /**
     * The working directory for the process.
     * 
     * @parameter default-value="${project.build.directory}/work"
     * @required
     */
    private File workDir;

    protected void doExecute() throws MojoExecutionException, MojoFailureException {
        int controlPort;
        try {
            ServerSocket ss = new ServerSocket(0);
            controlPort = ss.getLocalPort();
            ss.close();
        } catch (IOException ex) {
            throw new MojoFailureException("Failed to allocate port number", ex);
        }
        workDir.mkdirs();
        String[] vmArgs = new String[args != null ? args.length + 2 : 2];
        vmArgs[0] = daemonClass;
        vmArgs[1] = String.valueOf(controlPort);
        if (args != null) {
            System.arraycopy(args, 0, vmArgs, 2, args.length);
        }
        startJavaProcess(daemonClass, "org.apache.axis.testutils.daemon.Launcher", vmArgs, workDir, new DaemonProcessControl(controlPort));
    }
}
