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
package org.apache.axis.tools.daemon;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.LinkedList;

import org.apache.commons.daemon.Daemon;

/**
 * Reads messages from the control connection. The main purpose of this is to detect as soon as
 * possible when the control connection is closed by the parent process and to terminate the child
 * process if that is unexpected. To achieve this we need to read the messages eagerly and place
 * them into a queue. In particular this covers the case where the child process has received a
 * <tt>STOP</tt> message and {@link Daemon#stop()} or {@link Daemon#destroy()} hangs. In this case,
 * if the parent process is terminated (or stops waiting for the <tt>STOPPED</tt> message and closes
 * the control connection), we can stop the child process immediately.
 * 
 * @author Andreas Veithen
 */
final class ControlConnectionReader implements Runnable {
    private final BufferedReader in;
    private final LinkedList queue = new LinkedList();
    private boolean expectClose;
    
    ControlConnectionReader(Reader in) {
        this.in = new BufferedReader(in);
    }
    
    synchronized String awaitMessage() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        return (String)queue.removeFirst();
    }
    
    synchronized void expectClose() {
        this.expectClose = true;
    }
    
    public void run() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                synchronized (this) {
                    queue.add(message);
                    notify();
                }
            }
            if (!expectClose) {
                System.err.println("Control connection unexpectedly closed; terminating.");
                System.exit(1);
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
}
