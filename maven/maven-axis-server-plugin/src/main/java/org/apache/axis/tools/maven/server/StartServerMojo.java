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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.axis.client.AdminClient;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.w3c.dom.Element;

/**
 * Start a {@link org.apache.axis.server.standalone.StandaloneAxisServer} instance in a separate
 * JVM.
 * 
 * @goal start-server
 * @phase pre-integration-test
 * @requiresDependencyResolution test
 */
public class StartServerMojo extends AbstractStartProcessMojo {
    /**
     * @parameter default-value="${plugin.version}"
     * @required
     * @readonly
     */
    private String axisVersion;
    
    /**
     * @parameter default-value="${project.build.directory}/axis-server"
     * @required
     * @readonly
     */
    private File workDirBase;
    
    /**
     * The port of the Axis server.
     * 
     * @parameter default-value="8080"
     * @required
     */
    private int port;
    
    /**
     * The maximum number of concurrently active sessions.
     * 
     * @parameter default-value="100"
     */
    private int maxSessions;
    
    /**
     * A set of WSDD files for services to deploy. The WSDD files may be deployment or undeployment
     * requests. Undeployment requests will be processed when the server is stopped. The primary use
     * case for this is to test undeployment.
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
    
    protected void doExecute() throws MojoExecutionException, MojoFailureException {
        Log log = getLog();
        
        // Determine the port to be used
        int actualPort = foreground && foregroundPort != -1 ? foregroundPort : port;
        
        // Select WSDD files
        List deployments = new ArrayList();
        List undeployments = new ArrayList();
        if (wsdds != null && wsdds.length > 0) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder documentBuilder;
            try {
                documentBuilder = dbf.newDocumentBuilder();
            } catch (ParserConfigurationException ex) {
                throw new MojoFailureException("Unable to initialize DOM parser", ex);
            }
            for (int i=0; i<wsdds.length; i++) {
                FileSet wsdd = wsdds[i];
                DirectoryScanner scanner = wsdd.createScanner();
                scanner.scan();
                String[] includedFiles = scanner.getIncludedFiles();
                for (int j=0; j<includedFiles.length; j++) {
                    File wsddFile = new File(wsdd.getDirectory(), includedFiles[j]);
                    Element wsddElement;
                    try {
                        wsddElement = documentBuilder.parse(wsddFile).getDocumentElement();
                    } catch (Exception ex) {
                        log.warn("Skipping " + wsddFile + ": not an XML file", ex);
                        continue;
                    }
                    if (WSDDConstants.URI_WSDD.equals(wsddElement.getNamespaceURI())) {
                        String type = wsddElement.getLocalName();
                        if (type.equals(WSDDConstants.ELEM_WSDD_DEPLOY)) {
                            deployments.add(wsddFile);
                        } else if (type.equals(WSDDConstants.ELEM_WSDD_UNDEPLOY)) {
                            undeployments.add(wsddFile);
                        } else {
                            log.warn("Skipping " + wsddFile + ": unexpected WSDD type");
                        }
                    } else {
                        log.warn("Skipping " + wsddFile + ": not a WSDD file");
                    }
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("Deployments: " + deployments);
                log.debug("Undeployments: " + undeployments);
            }
        }
        
        // Prepare a work directory where the server can create a server-config.wsdd file
        File workDir = new File(workDirBase, String.valueOf(actualPort));
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
        List args = new ArrayList();
        args.add("-p");
        args.add(String.valueOf(actualPort));
        args.add("-w");
        args.add(workDir.getAbsolutePath());
        if (jwsDirs != null && jwsDirs.length > 0) {
            args.add("-j");
            args.add(StringUtils.join(jwsDirs, File.pathSeparator));
        }
        args.add("-m");
        args.add(String.valueOf(maxSessions));
        try {
            AdminClient adminClient = new AdminClient(true);
            adminClient.setTargetEndpointAddress(new URL("http://localhost:" + actualPort + "/axis/services/AdminService"));
            Set additionalDependencies = new HashSet();
            additionalDependencies.add(artifactFactory.createArtifact("org.apache.axis", "axis-standalone-server", axisVersion, Artifact.SCOPE_TEST, "jar"));
            startJavaProcess(
                    "Server on port " + actualPort,
                    "org.apache.axis.server.standalone.StandaloneAxisServer",
                    additionalDependencies,
                    (String[])args.toArray(new String[args.size()]),
                    workDir,
                    new AxisServerProcessControl(actualPort, adminClient,
                            (File[])deployments.toArray(new File[deployments.size()]),
                            (File[])undeployments.toArray(new File[undeployments.size()]),
                            isDebug() || foreground ? Integer.MAX_VALUE : 20000));
        } catch (Exception ex) {
            throw new MojoFailureException("Failed to start server", ex);
        }
        
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
}
