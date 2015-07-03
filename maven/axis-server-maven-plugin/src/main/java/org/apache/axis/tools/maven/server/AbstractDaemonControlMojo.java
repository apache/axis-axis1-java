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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.github.veithen.ulog.PlexusLoggerInjector;

public abstract class AbstractDaemonControlMojo extends AbstractMojo {
    /**
     * @component
     */
    // This is necessary to set up logging such that all messages logged by the Axis
    // libraries through commons logging are redirected to Plexus logs.
    PlexusLoggerInjector loggerInjector;
    
    /**
     * @component
     */
    private DaemonManager daemonManager;

    /**
     * Set this to <code>true</code> to skip running tests, but still compile them. This is the same
     * flag that is also used by the Surefire and Failsafe plugins.
     * 
     * @parameter expression="${skipTests}" default-value="false"
     */
    private boolean skipTests;
    
    public final DaemonManager getDaemonManager() {
        return daemonManager;
    }

    public final void execute() throws MojoExecutionException, MojoFailureException {
        if (skipTests) {
            getLog().info("Tests are skipped.");
        } else {
            doExecute();
        }
    }
    
    protected abstract void doExecute() throws MojoExecutionException, MojoFailureException;
}
