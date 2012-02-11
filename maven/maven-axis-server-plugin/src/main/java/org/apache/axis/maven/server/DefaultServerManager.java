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
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axis.client.AdminClient;
import org.apache.axis.client.Call;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.StringUtils;

public class DefaultServerManager implements ServerManager, LogEnabled {
    private final Map servers = new HashMap();
    
    private Logger logger;
    
    public void enableLogging(Logger logger) {
        this.logger = logger;
    }

    private void process(AdminClient adminClient, File[] wsddFiles) throws Exception {
        for (int i=0; i<wsddFiles.length; i++) {
            File wsddFile = wsddFiles[i];
            if (logger.isDebugEnabled()) {
                logger.debug("Starting to process " + wsddFile);
            }
            String result = adminClient.process(wsddFile.getPath());
            if (logger.isDebugEnabled()) {
                logger.debug("AdminClient result: " + result);
            }
            logger.info("Processed " + wsddFile);
        }
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
        servers.put(Integer.valueOf(port), new Server(process, adminClient, undeployments));
        new Thread(new StreamPump(process.getInputStream(), System.out), "axis-server-" + port + "-stdout").start();
        new Thread(new StreamPump(process.getErrorStream(), System.err), "axis-server-" + port + "-stderr").start();
        
        // Wait for server to become ready
        String versionUrl = "http://localhost:" + port + "/axis/services/Version";
        Call call = new Call(new URL(versionUrl));
        call.setOperationName(new QName(versionUrl, "getVersion"));
        long start = System.currentTimeMillis();
        for (int i=0; ; i++) {
            try {
                String result = (String)call.invoke(new Object[0]);
                logger.info("Server ready on port " + port + ": " + result.replace('\n', ' '));
                break;
            } catch (RemoteException ex) {
                if (System.currentTimeMillis() > start + timeout) {
                    throw ex;
                }
            }
            try {
                int exitValue = process.exitValue();
                // TODO: choose a better exception here
                throw new RemoteException("The server process unexpectedly died with exit status " + exitValue);
            } catch (IllegalThreadStateException ex) {
                // This means that the process is still running; continue
            }
            Thread.sleep(200);
        }
        
        // Deploy services
        process(adminClient, deployments);
    }
    
    public void stopServer(int port) throws Exception {
        Server server = (Server)servers.remove(Integer.valueOf(port));
        AdminClient adminClient = server.getAdminClient();
        process(adminClient, server.getUndeployments());
        adminClient.quit();
        server.getProcess().waitFor();
        logger.info("Server on port " + port + " stopped");
    }
}
