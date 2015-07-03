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
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.axis.model.wsdd.Deployment;
import org.apache.axis.model.wsdd.WSDDUtil;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.xml.sax.InputSource;

/**
 * Start a {@link org.apache.axis.server.standalone.StandaloneAxisServer} instance in a separate
 * JVM.
 * 
 * @goal start-server
 * @phase pre-integration-test
 * @requiresDependencyResolution test
 */
public class StartServerMojo extends AbstractStartWebServerMojo {
    /**
     * @parameter default-value="${project.build.directory}/axis-server"
     * @required
     * @readonly
     */
    private File workDirBase;
    
    /**
     * The maximum number of concurrently active sessions.
     * 
     * @parameter default-value="100"
     */
    private int maxSessions;
    
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
    
    protected void doStartDaemon(int port) throws MojoExecutionException, MojoFailureException {
        // Need to setup additional dependencies before building the default configuration!
        addAxisDependency("axis-standalone-server");
        if (jwsDirs != null && jwsDirs.length > 0) {
            addAxisDependency("axis-rt-jws");
        }
        
        // Prepare a work directory where we can place the server-config.wsdd file
        File workDir = new File(workDirBase, String.valueOf(port));
        if (workDir.exists()) {
            try {
                FileUtils.deleteDirectory(workDir);
            } catch (IOException ex) {
                throw new MojoFailureException("Failed to clean the work directory", ex);
            }
        }
        File webInfDir = new File(workDir, "WEB-INF");
        webInfDir.mkdirs();
        
        // Start with the default configuration (which depends on the JARs in the classpath)
        Deployment deployment;
        try {
            deployment = WSDDUtil.buildDefaultConfiguration(buildClassLoader(), "server");
        } catch (IOException ex) {
            throw new MojoExecutionException("Failed to build default server configuration", ex);
        }
        
        // Select WSDD files
        if (wsdds != null) {
            for (int i=0; i<wsdds.length; i++) {
                FileSet wsdd = wsdds[i];
                DirectoryScanner scanner = wsdd.createScanner();
                scanner.scan();
                String[] includedFiles = scanner.getIncludedFiles();
                for (int j=0; j<includedFiles.length; j++) {
                    File wsddFile = new File(wsdd.getDirectory(), includedFiles[j]);
                    try {
                        deployment.merge(WSDDUtil.load(new InputSource(wsddFile.toURI().toString())));
                    } catch (IOException ex) {
                        throw new MojoExecutionException("Failed to load " + wsddFile, ex);
                    }
                    getLog().info("Processed " + wsddFile);
                }
            }
        }
        
        // Write the server-config.wsdd file
        File serverConfigWsdd = new File(webInfDir, "server-config.wsdd");
        try {
            FileOutputStream out = new FileOutputStream(serverConfigWsdd);
            try {
                WSDDUtil.save(deployment, out);
            } finally {
                out.close();
            }
        } catch (IOException ex) {
            throw new MojoExecutionException("Failed to write " + serverConfigWsdd, ex);
        }
        
        if (configs != null && configs.length > 0) {
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
        args.add(String.valueOf(port));
        args.add("-w");
        args.add(workDir.getAbsolutePath());
        if (jwsDirs != null && jwsDirs.length > 0) {
            args.add("-j");
            args.add(StringUtils.join(jwsDirs, File.pathSeparator));
        }
        args.add("-m");
        args.add(String.valueOf(maxSessions));
        try {
            startDaemon(
                    "Server on port " + port,
                    "org.apache.axis.server.standalone.daemon.AxisServerDaemon",
                    (String[])args.toArray(new String[args.size()]),
                    workDir);
        } catch (Exception ex) {
            throw new MojoFailureException("Failed to start server", ex);
        }
    }
    
    private ClassLoader buildClassLoader() throws MojoExecutionException {
        List classpath;
        try {
            classpath = getClasspath();
        } catch (Exception ex) {
            throw new MojoExecutionException("Failed to build classpath", ex);
        }
        URL[] urls = new URL[classpath.size()];
        for (int i=0; i<classpath.size(); i++) {
            try {
                urls[i] = ((File)classpath.get(i)).toURI().toURL();
            } catch (MalformedURLException ex) {
                throw new MojoExecutionException("Unexpected exception", ex);
            }
        }
        return new URLClassLoader(urls);
    }
}
