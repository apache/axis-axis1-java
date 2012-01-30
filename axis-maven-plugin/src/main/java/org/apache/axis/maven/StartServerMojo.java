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
import java.util.Arrays;
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
    private FileSet[] wsdds;
    
    /**
     * A set of directories to look up JWS files from.
     * 
     * @parameter
     */
    private File[] jwsDirs;
    
    /**
     * A set of config files to copy to the <tt>WEB-INF</tt> dir. An example of a config file
     * would be <tt>users.lst</tt> used by <code>SimpleSecurityProvider</code>.
     *
     * @parameter
     */
    private FileSet[] configs;
    
    /**
     * If this flag is set to <code>true</code>, then the execution of the goal will block after the
     * server has been started and the services are deployed. This is useful if one wants to
     * manually test some services deployed on the server or if one wants to run the integration
     * tests from an IDE. The flag should only be set using the command line, but not in the POM.
     * <p>
     * Note: this feature is implemented using a flag (instead of a distinct goal) to make sure that
     * the server is configured in exactly the same way as in a normal integration test execution.
     * 
     * @parameter expression="${axis.server.foreground}" default-value="false"
     */
    private boolean foreground;
    
    /**
     * The arguments to pass to the server JVM when debug mode is enabled.
     * 
     * @parameter default-value="-Xdebug -Xrunjdwp:transport=dt_socket,address=8899,server=y,suspend=y"
     */
    private String debugArgs;
    
    /**
     * Indicates whether the server should be started in debug mode. This flag should only be set
     * from the command line.
     * 
     * @parameter expression="${axis.server.debug}" default-value="false"
     */
    private boolean debug;
    
    /**
     * The arguments to pass to the server JVM when JMX is enabled.
     * 
     * @parameter default-value="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9999 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"
     */
    private String jmxArgs;
    
    /**
     * Indicates whether the server should be started with remote JMX enabled. This flag should only
     * be set from the command line.
     * 
     * @parameter expression="${axis.server.jmx}" default-value="false"
     */
    private boolean jmx;
    
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
                FileSet wsdd = wsdds[i];
                DirectoryScanner scanner = wsdd.createScanner();
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
        
        // Compute JVM arguments
        List vmArgs = new ArrayList();
        if (debug) {
            processVMArgs(vmArgs, debugArgs);
        }
        if (jmx) {
            processVMArgs(vmArgs, jmxArgs);
        }
        if (log.isDebugEnabled()) {
            log.debug("Additional VM args: " + vmArgs);
        }
        
        // Prepare a work directory where the server can create a server-config.wsdd file
        File workDir = new File(workDirBase, String.valueOf(getPort()));
        if (workDir.exists()) {
            try {
                FileUtils.deleteDirectory(workDir);
            } catch (IOException ex) {
                throw new MojoFailureException("Failed to clean the work directory", ex);
            }
        }
        workDir.mkdirs();
        
        if (configs != null && configs.length > 0) {
            File webInfDir = new File(workDir, "WEB-INF");
            webInfDir.mkdirs();
            for (int i=0; i<configs.length; i++) {
                FileSet config = configs[i];
                DirectoryScanner scanner = config.createScanner();
                scanner.scan();
                String[] includedFiles = scanner.getIncludedFiles();
                for (int j=0; j<includedFiles.length; j++) {
                    String includedFile = includedFiles[j];
                    File source = new File(config.getDirectory(), includedFile);
                    try {
                        FileUtils.copyFile(source, new File(webInfDir, includedFile));
                    } catch (IOException ex) {
                        throw new MojoFailureException("Unable to copy " + source, ex);
                    }
                }
            }
        }
        
        // Start the server
        try {
            getServerManager().startServer(executable,
                    (String[])classPathElements.toArray(new String[classPathElements.size()]),
                    getPort(),
                    (String[])vmArgs.toArray(new String[vmArgs.size()]),
                    workDir,
                    wsddFiles == null ? null : (String[])wsddFiles.toArray(new String[wsddFiles.size()]),
                    jwsDirs,
                    debug || foreground ? Integer.MAX_VALUE : 20000);
        } catch (Exception ex) {
            throw new MojoFailureException("Failed to start server", ex);
        }
        
        if (foreground) {
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
    
    private static void processVMArgs(List vmArgs, String args) {
        vmArgs.addAll(Arrays.asList(args.split(" ")));
    }
}
