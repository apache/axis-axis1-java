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
import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.namespace.QName;

import org.apache.axis.client.AdminClient;
import org.apache.axis.client.Call;
import org.codehaus.plexus.logging.Logger;

public class AxisServerProcessControl implements ProcessControl {
    private final int port;
    private final AdminClient adminClient;
    private final File[] deployments;
    private final File[] undeployments;
    private final int timeout;
    
    public AxisServerProcessControl(int port, AdminClient adminClient, File[] deployments, File[] undeployments, int timeout) {
        this.port = port;
        this.adminClient = adminClient;
        this.deployments = deployments;
        this.undeployments = undeployments;
        this.timeout = timeout;
    }

    public void initializeProcess(Logger logger, Process process) throws Exception {
        // Wait for server to become ready
        String versionUrl = "http://localhost:" + port + "/axis/services/Version";
        Call call = new Call(new URL(versionUrl));
        call.setOperationName(new QName(versionUrl, "getVersion"));
        long start = System.currentTimeMillis();
        while (true) {
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
        AdminClientUtils.process(logger, adminClient, deployments);
    }

    public int shutdownProcess(Logger logger) throws Exception {
        AdminClientUtils.process(logger, adminClient, undeployments);
        adminClient.quit();
        return STOPPING;
    }
}
