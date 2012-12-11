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

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.resolver.DebugResolutionListener;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.artifact.InvalidDependencyVersionException;
import org.apache.maven.toolchain.Toolchain;
import org.apache.maven.toolchain.ToolchainManager;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.StringUtils;

public abstract class AbstractStartDaemonMojo extends AbstractDaemonControlMojo implements LogEnabled {
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
    private MavenProjectBuilder projectBuilder;
    
    /**
     * Local maven repository.
     * 
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    private ArtifactRepository localRepository;
    
    /**
     * Remote repositories.
     * 
     * @parameter expression="${project.remoteArtifactRepositories}"
     * @required
     * @readonly
     */
    private List remoteArtifactRepositories;
    
    /**
     * @component
     */
    private ArtifactFactory artifactFactory;
    
    /**
     * @component
     */
    private ArtifactResolver artifactResolver;
    
    /**
     * @component
     */
    private ArtifactCollector artifactCollector;
    
    /**
     * @component
     */
    private ArtifactMetadataSource artifactMetadataSource;
    
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
    
    /**
     * Arbitrary JVM options to set on the command line. Note that this parameter uses the same
     * expression as the Surefire and Failsafe plugins. By setting the <code>argLine</code>
     * property, it is therefore possible to easily pass a common set of JVM options to all
     * processes involved in the tests. Since the JaCoCo Maven plugin also sets this property, code
     * coverage generated on the server-side will be automatically included in the analysis.
     * 
     * @parameter expression="${argLine}"
     */
    private String argLine;
    
    /**
     * @parameter default-value="${plugin.version}"
     * @required
     * @readonly
     */
    private String axisVersion;
    
    private final Set/*<Artifact>*/ additionalDependencies = new HashSet();
    private List/*<File>*/ classpath;
    
    private Logger logger;
    
    public final void enableLogging(Logger logger) {
        this.logger = logger;
    }
    
    protected final void addDependency(String groupId, String artifactId, String version) {
        additionalDependencies.add(artifactFactory.createArtifact(groupId, artifactId, version, Artifact.SCOPE_TEST, "jar"));
        classpath = null;
    }
    
    protected final void addAxisDependency(String artifactId) {
        addDependency("org.apache.axis", artifactId, axisVersion);
    }
    
    protected final List/*<File>*/ getClasspath() throws ProjectBuildingException, InvalidDependencyVersionException, ArtifactResolutionException, ArtifactNotFoundException {
        if (classpath == null) {
            final Log log = getLog();
            
            // We need dependencies in scope test. Since this is the largest scope, we don't need
            // to do any additional filtering based on dependency scope.
            Set projectDependencies = project.getArtifacts();
            
            final Set artifacts = new HashSet(projectDependencies);
            
            if (additionalDependencies != null) {
                for (Iterator it = additionalDependencies.iterator(); it.hasNext(); ) {
                    Artifact a = (Artifact)it.next();
                    if (log.isDebugEnabled()) {
                        log.debug("Resolving artifact to be added to classpath: " + a);
                    }
                    ArtifactFilter filter = new ArtifactFilter() {
                        public boolean include(Artifact artifact) {
                            String id = artifact.getDependencyConflictId();
                            for (Iterator it = artifacts.iterator(); it.hasNext(); ) {
                                if (id.equals(((Artifact)it.next()).getDependencyConflictId())) {
                                    return false;
                                }
                            }
                            return true;
                        }
                    };
                    MavenProject p = projectBuilder.buildFromRepository(a, remoteArtifactRepositories, localRepository);
                    if (filter.include(p.getArtifact())) {
                        Set s = p.createArtifacts(artifactFactory, Artifact.SCOPE_RUNTIME, filter);
                        artifacts.addAll(artifactCollector.collect(s,
                                p.getArtifact(), p.getManagedVersionMap(),
                                localRepository, remoteArtifactRepositories, artifactMetadataSource, filter,
                                Collections.singletonList(new DebugResolutionListener(logger))).getArtifacts());
                        artifacts.add(p.getArtifact());
                    }
                }
            }
            
            classpath = new ArrayList();
            classpath.add(new File(project.getBuild().getTestOutputDirectory()));
            classpath.add(new File(project.getBuild().getOutputDirectory()));
            for (Iterator it = artifacts.iterator(); it.hasNext(); ) {
                Artifact a = (Artifact)it.next();
                if (a.getArtifactHandler().isAddedToClasspath()) {
                    if (a.getFile() == null) {
                        artifactResolver.resolve(a, remoteArtifactRepositories, localRepository);
                    }
                    classpath.add(a.getFile());
                }
            }
        }
        
        return classpath;
    }
    
    private int allocatePort() throws MojoFailureException {
        try {
            ServerSocket ss = new ServerSocket(0);
            int port = ss.getLocalPort();
            ss.close();
            return port;
        } catch (IOException ex) {
            throw new MojoFailureException("Failed to allocate port number", ex);
        }
    }
    
    protected final void startDaemon(String description, String daemonClass, String[] args, File workDir) throws MojoExecutionException, MojoFailureException {
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
        
        int controlPort = allocatePort();
        
        // Get class path
        List classpath;
        try {
            classpath = getClasspath();
        } catch (Exception ex) {
            throw new MojoExecutionException("Failed to build classpath", ex);
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
        if (argLine != null) {
            processVMArgs(vmArgs, argLine);
        }
        if (log.isDebugEnabled()) {
            log.debug("Additional VM args: " + vmArgs);
        }
        
        List cmdline = new ArrayList();
        cmdline.add(jvm);
        cmdline.add("-cp");
        cmdline.add(StringUtils.join(classpath.iterator(), File.pathSeparator));
        cmdline.addAll(vmArgs);
        cmdline.add("org.apache.axis.tools.daemon.Launcher");
        cmdline.add(daemonClass);
        cmdline.add(String.valueOf(controlPort));
        cmdline.addAll(Arrays.asList(args));
        try {
            getDaemonManager().startDaemon(
                    description,
                    (String[])cmdline.toArray(new String[cmdline.size()]),
                    workDir,
                    controlPort);
        } catch (Exception ex) {
            throw new MojoFailureException("Failed to start server", ex);
        }
    }
    
    private static void processVMArgs(List vmArgs, String args) {
        vmArgs.addAll(Arrays.asList(args.split(" ")));
    }

    protected final void doExecute() throws MojoExecutionException, MojoFailureException {
        addAxisDependency("daemon-launcher");
        doStartDaemon();
    }
    
    protected abstract void doStartDaemon() throws MojoExecutionException, MojoFailureException;
}
