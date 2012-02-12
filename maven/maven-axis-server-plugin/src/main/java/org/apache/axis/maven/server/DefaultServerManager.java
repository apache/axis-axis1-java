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
package org.apache.axis.maven.server;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.axis.client.AdminClient;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.StringUtils;

public class DefaultServerManager implements ServerManager, LogEnabled {
    private final List managedProcesses = new ArrayList();
    
    private Logger logger;
    
    public void enableLogging(Logger logger) {
        this.logger = logger;
    }

    public void startServer(String jvm, String[] classpath, int port, String[] vmArgs, File workDir, File[] deployments, File[] undeployments, File[] jwsDirs, int timeout) throws Exception {
        AdminClient adminClient = new AdminClient(true);
        adminClient.setTargetEndpointAddress(new URL("http://localhost:" + port + "/axis/services/AdminService"));
        List cmdline = new ArrayList();
        cmdline.add(jvm);
        cmdline.add("-cp");
        cmdline.add(StringUtils.join(classpath, File.pathSeparator));
        cmdline.addAll(Arrays.asList(vmArgs));
        cmdline.add("org.apache.axis.server.standalone.StandaloneAxisServer");
        cmdline.add("-p");
        cmdline.add(String.valueOf(port));
        cmdline.add("-w");
        cmdline.add(workDir.getAbsolutePath());
        if (jwsDirs != null && jwsDirs.length > 0) {
            cmdline.add("-j");
            cmdline.add(StringUtils.join(jwsDirs, File.pathSeparator));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Starting process with command line: " + cmdline);
        }
        Process process = Runtime.getRuntime().exec((String[])cmdline.toArray(new String[cmdline.size()]), null, workDir);
        managedProcesses.add(new ManagedProcess(process, "Server on port " + port, new AxisServerStopAction(adminClient, undeployments)));
        new Thread(new StreamPump(process.getInputStream(), System.out), "axis-server-" + port + "-stdout").start();
        new Thread(new StreamPump(process.getErrorStream(), System.err), "axis-server-" + port + "-stderr").start();
        
        new AxisServerStartAction(port, adminClient, deployments, timeout).execute(logger, process);
    }
    
    public void stopAll() throws Exception {
        Exception savedException = null;
        for (Iterator it = managedProcesses.iterator(); it.hasNext(); ) {
            ManagedProcess server = (ManagedProcess)it.next();
            int result;
            try {
                result = server.getStopAction().execute(logger);
            } catch (Exception ex) {
                if (savedException == null) {
                    savedException = ex;
                }
                result = -1;
            }
            if (result == ProcessStopAction.STOPPING) {
                server.getProcess().waitFor();
            } else {
                server.getProcess().destroy();
            }
            logger.info(server.getDescription() + " stopped");
        }
        // TODO: need to clear the collection because the same ServerManager instance may be used by multiple projects in a reactor build;
        //       note that this means that the plugin is not thread safe (i.e. doesn't support parallel builds in Maven 3)
        managedProcesses.clear();
        if (savedException != null) {
            throw savedException;
        }
    }
}
