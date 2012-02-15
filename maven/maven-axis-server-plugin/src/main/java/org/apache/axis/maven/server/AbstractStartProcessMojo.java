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
package org.apache.axis.maven.server;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.toolchain.Toolchain;
import org.apache.maven.toolchain.ToolchainManager;
import org.codehaus.plexus.util.StringUtils;

public abstract class AbstractStartProcessMojo extends AbstractServerMojo {
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
     * The arguments to pass to the JVM when debug mode is enabled.
     * 
     * @parameter default-value="-Xdebug -Xrunjdwp:transport=dt_socket,address=8899,server=y,suspend=y"
     */
    private String debugArgs;
    
    /**
     * Indicates whether the Java process should be started in debug mode. This flag should only be
     * set from the command line.
     * 
     * @parameter expression="${axis.server.debug}" default-value="false"
     */
    private boolean debug;
    
    /**
     * The arguments to pass to the JVM when JMX is enabled.
     * 
     * @parameter default-value="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9999 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"
     */
    private String jmxArgs;
    
    /**
     * Indicates whether the Java process should be started with remote JMX enabled. This flag
     * should only be set from the command line.
     * 
     * @parameter expression="${axis.server.jmx}" default-value="false"
     */
    private boolean jmx;
    
    protected boolean isDebug() {
        return debug;
    }

    protected void startJavaProcess(String description, String mainClass, String[] args, File workDir, ProcessStartAction startAction, ProcessStopAction stopAction) throws MojoExecutionException, MojoFailureException {
        Log log = getLog();
        
        // Locate java executable to use
        String jvm;
        Toolchain tc = toolchainManager.getToolchainFromBuildContext("jdk", session);
        if (tc != null) {
            jvm = tc.findTool("java");
        } else {
            jvm = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        }
        if (log.isDebugEnabled()) {
            log.debug("Java executable: " + jvm);
        }
        
        // Get class path
        List classpath;
        try {
            classpath = project.getTestClasspathElements();
        } catch (DependencyResolutionRequiredException ex) {
            throw new MojoExecutionException("Unexpected exception", ex);
        }
        if (log.isDebugEnabled()) {
            log.debug("Class path elements: " + classpath);
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
        
        List cmdline = new ArrayList();
        cmdline.add(jvm);
        cmdline.add("-cp");
        cmdline.add(StringUtils.join(classpath.iterator(), File.pathSeparator));
        cmdline.addAll(vmArgs);
        cmdline.add(mainClass);
        cmdline.addAll(Arrays.asList(args));
        try {
            getProcessManager().startProcess(
                    description,
                    (String[])cmdline.toArray(new String[cmdline.size()]),
                    workDir,
                    startAction,
                    stopAction);
        } catch (Exception ex) {
            throw new MojoFailureException("Failed to start server", ex);
        }
    }
    
    private static void processVMArgs(List vmArgs, String args) {
        vmArgs.addAll(Arrays.asList(args.split(" ")));
    }
}
