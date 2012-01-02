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
import java.util.List;

import org.apache.axis.transport.http.SimpleAxisServer;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.toolchain.Toolchain;
import org.apache.maven.toolchain.ToolchainManager;

/**
 * Start a {@link SimpleAxisServer} instance in a separate JVM.
 * 
 * @goal start-server
 * @phase pre-integration-test
 * @requiresDependencyResolution test
 */
public class StartServerMojo extends AbstractServerMojo {
    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;
    
    /**
     * The current build session instance. This is used for toolchain manager API calls.
     * 
     * @parameter default-value="${session}"
     * @required
     * @readonly
     */
    private MavenSession session;
    
    /**
     * @component
     */
    private ToolchainManager toolchainManager;
    
    public void execute() throws MojoExecutionException, MojoFailureException {
        String executable;
        Toolchain tc = toolchainManager.getToolchainFromBuildContext("jdk", session);
        if (tc != null) {
            executable = tc.findTool("java");
        } else {
            executable = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        }
        getLog().debug("Java executable: " + executable);
        List classPathElements;
        try {
            classPathElements = project.getTestClasspathElements();
        } catch (DependencyResolutionRequiredException ex) {
            throw new MojoExecutionException("Unexpected exception", ex);
        }
        getLog().debug("Class path elements: " + classPathElements);
        try {
            getServerManager().startServer(executable, (String[])classPathElements.toArray(new String[classPathElements.size()]), getPort());
        } catch (Exception ex) {
            throw new MojoFailureException("Failed to start server", ex);
        }
    }
}
