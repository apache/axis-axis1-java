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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

public abstract class AbstractStartWebServerMojo extends AbstractStartDaemonMojo {
    /**
     * The HTTP port.
     * 
     * @parameter default-value="8080"
     * @required
     */
    private int port;

    /**
     * If this flag is set to <code>true</code>, then the execution of the goal will block after the
     * server has been started. This is useful if one wants to manually test some services deployed
     * on the server or if one wants to run the integration tests from an IDE. The flag should only
     * be set using the command line, but not in the POM.
     * 
     * @parameter expression="${axis.server.foreground}" default-value="false"
     */
    // Note: this feature is implemented using a flag (instead of a distinct goal) to make sure that
    // the server is configured in exactly the same way as in a normal integration test execution.
    private boolean foreground;
    
    /**
     * Specifies an alternate port number that will override {@link #port} if {@link #foreground} is
     * set to <code>true</code>. This parameter should be used if the port number configured with
     * the {@link #port} parameter is allocated dynamically. This makes it easier to run integration
     * tests from an IDE. For more information, see the <a href="usage.html">usage
     * documentation</a>.
     * 
     * @parameter
     */
    private int foregroundPort = -1;
    
    protected final void doStartDaemon() throws MojoExecutionException, MojoFailureException {
        Log log = getLog();
        
        doStartDaemon(foreground && foregroundPort != -1 ? foregroundPort : port);
        
        if (foreground) {
            log.info("Server started in foreground mode. Press CRTL-C to stop.");
            Object lock = new Object();
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException ex) {
                    // Set interrupt flag and continue
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    protected abstract void doStartDaemon(int port) throws MojoExecutionException, MojoFailureException;
}
