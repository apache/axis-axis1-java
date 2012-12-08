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
package org.apache.axis.testutils.daemon;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Launcher {
    public static void main(String[] args) throws Exception {
        String daemonClass = args[0];
        int controlPort = Integer.parseInt(args[1]);
        String[] daemonArgs = new String[args.length-2];
        System.arraycopy(args, 2, daemonArgs, 0, args.length-2);
        ServerSocket controlServerSocket = new ServerSocket();
        controlServerSocket.bind(new InetSocketAddress(InetAddress.getByName("localhost"), controlPort));
        Socket controlSocket = controlServerSocket.accept();
        BufferedReader controlIn = new BufferedReader(new InputStreamReader(controlSocket.getInputStream(), "ASCII"));
        Writer controlOut = new OutputStreamWriter(controlSocket.getOutputStream(), "ASCII");
        Daemon daemon = (Daemon)Class.forName(daemonClass).newInstance();
        daemon.start(daemonArgs);
        controlOut.write("READY\r\n");
        controlOut.flush();
        String request = controlIn.readLine();
        if (request == null) {
            System.err.println("Control connection unexpectedly closed");
        } else if (request.equals("STOP")) {
            daemon.stop();
            controlOut.write("STOPPED\r\n");
            controlOut.flush();
        } else {
            System.err.println("Unexpected request: " + request);
        }
    }
}
