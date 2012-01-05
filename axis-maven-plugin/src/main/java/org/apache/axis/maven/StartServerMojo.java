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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.axis.transport.http.SimpleAxisServer;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.toolchain.Toolchain;
import org.apache.maven.toolchain.ToolchainManager;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;

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
    
    /**
     * @parameter default-value="${project.build.directory}/axis-server"
     * @required
     * @readonly
     */
    private File workDirBase;
    
    /**
     * A set of WSDD files for services to deploy.
     * 
     * @parameter
     */
    private WSDD[] wsdds;
    
    public void execute() throws MojoExecutionException, MojoFailureException {
        Log log = getLog();
        
        // Locate java executable to use
        String executable;
        Toolchain tc = toolchainManager.getToolchainFromBuildContext("jdk", session);
        if (tc != null) {
            executable = tc.findTool("java");
        } else {
            executable = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        }
        if (log.isDebugEnabled()) {
            log.debug("Java executable: " + executable);
        }
        
        // Get class path
        List classPathElements;
        try {
            classPathElements = project.getTestClasspathElements();
        } catch (DependencyResolutionRequiredException ex) {
            throw new MojoExecutionException("Unexpected exception", ex);
        }
        if (log.isDebugEnabled()) {
            log.debug("Class path elements: " + classPathElements);
        }
        
        // Select WSDD files
        List wsddFiles;
        if (wsdds != null && wsdds.length > 0) {
            wsddFiles = new ArrayList();
            for (int i=0; i<wsdds.length; i++) {
                WSDD wsdd = wsdds[i];
                DirectoryScanner scanner = new DirectoryScanner();
                scanner.setBasedir(wsdd.getDirectory());
                scanner.setIncludes(wsdd.getIncludes());
                scanner.setExcludes(wsdd.getExcludes());
                scanner.scan();
                String[] includedFiles = scanner.getIncludedFiles();
                for (int j=0; j<includedFiles.length; j++) {
                    wsddFiles.add(new File(wsdd.getDirectory(), includedFiles[j]).getPath());
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("WSDD files: " + wsddFiles);
            }
        } else {
            wsddFiles = null;
        }
        
        // Prepare a work directory where the server can create a server-config.wsdd file
        File workDir = new File(workDirBase, String.valueOf(getPort()));
        if (workDir.exists()) {
            try {
                FileUtils.deleteDirectory(workDir);
            } catch (IOException ex) {
                throw new MojoFailureException("Failed to clean the work directory", ex);
            }
        } else {
            workDir.mkdirs();
        }
        
        // Start the server
        try {
            getServerManager().startServer(executable,
                    (String[])classPathElements.toArray(new String[classPathElements.size()]),
                    getPort(),
                    workDir,
                    wsddFiles == null ? null : (String[])wsddFiles.toArray(new String[wsddFiles.size()]));
        } catch (Exception ex) {
            throw new MojoFailureException("Failed to start server", ex);
        }
    }
}
