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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;

import org.codehaus.plexus.logging.Logger;

public class RemoteDaemon {
    private final Process process;
    private final String description;
    private final int controlPort;
    private BufferedReader controlIn;
    private Writer controlOut;

    public RemoteDaemon(Process process, String description, int controlPort) {
        this.process = process;
        this.description = description;
        this.controlPort = controlPort;
    }

    public Process getProcess() {
        return process;
    }

    public String getDescription() {
        return description;
    }

    public void startDaemon(Logger logger) throws Exception {
        logger.debug("Attempting to establish control connection on port " + controlPort);
        Socket controlSocket;
        while (true) {
            try {
                controlSocket = new Socket(InetAddress.getByName("localhost"), controlPort);
                break;
            } catch (IOException ex) {
                try {
                    int exitValue = process.exitValue();
                    throw new IllegalStateException("Process terminated prematurely with exit code " + exitValue);
                } catch (IllegalThreadStateException ex2) {
                    // Process is still running; continue
                }
                Thread.sleep(100);
            }
        }
        logger.debug("Control connection established");
        controlIn = new BufferedReader(new InputStreamReader(controlSocket.getInputStream(), "ASCII"));
        controlOut = new OutputStreamWriter(controlSocket.getOutputStream(), "ASCII");
        logger.debug("Waiting for daemon to become ready");
        expectStatus("READY");
        logger.debug("Daemon is ready");
    }

    public void stopDaemon(Logger logger) throws Exception {
        controlOut.write("STOP\r\n");
        controlOut.flush();
        expectStatus("STOPPED");
    }
    
    private void expectStatus(String expectedStatus) throws IOException {
        String status = controlIn.readLine();
        if (status == null) {
            throw new IllegalStateException("Control connection unexpectedly closed");
        } else if (!status.equals(expectedStatus)) {
            throw new IllegalStateException("Unexpected status: " + status);
        }
    }
}
