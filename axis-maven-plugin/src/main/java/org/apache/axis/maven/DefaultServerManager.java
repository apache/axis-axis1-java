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
package org.apache.axis.maven;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.axis.client.AdminClient;
import org.codehaus.plexus.util.StringUtils;

public class DefaultServerManager implements ServerManager {
    private final Map servers = new HashMap();

    public void startServer(String jvm, String[] classpath, int port, String[] wsddFiles) throws Exception {
        AdminClient adminClient = new AdminClient(true);
        adminClient.setTargetEndpointAddress(new URL("http://localhost:" + port + "/axis/services/AdminService"));
        Process process = Runtime.getRuntime().exec(new String[] {
                jvm,
                "-cp",
                StringUtils.join(classpath, File.pathSeparator),
                "org.apache.axis.transport.http.SimpleAxisServer",
                "-p",
                String.valueOf(port)
        });
        servers.put(Integer.valueOf(port), new Server(process, adminClient));
        // TODO: need to set up stdout/stderr forwarding; otherwise the process will hang
        // TODO: need to ping the server and wait until it becomes ready
        Thread.sleep(5000);
        if (wsddFiles != null) {
            for (int i=0; i<wsddFiles.length; i++) {
                System.out.println(adminClient.process(wsddFiles[i]));
            }
        }
    }
    
    public void stopServer(int port) throws Exception {
        Server server = (Server)servers.remove(Integer.valueOf(port));
        server.getAdminClient().quit();
        server.getProcess().waitFor();
    }
}
