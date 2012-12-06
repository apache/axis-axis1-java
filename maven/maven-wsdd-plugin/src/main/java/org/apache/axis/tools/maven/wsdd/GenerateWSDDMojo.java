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
package org.apache.axis.tools.maven.wsdd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.List;

import org.apache.axis.model.wsdd.Deployment;
import org.apache.axis.model.wsdd.WSDDUtil;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.xml.sax.InputSource;

import com.github.veithen.ulog.PlexusLoggerInjector;

/**
 * 
 * 
 * @goal generate-wsdd
 * @requiresDependencyResolution compile
 */
public class GenerateWSDDMojo extends AbstractMojo {
    /**
     * @component
     */
    // This is necessary to set up logging such that all messages logged by the Axis
    // libraries through commons logging are redirected to Plexus logs.
    PlexusLoggerInjector loggerInjector;

    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * 
     * 
     * @parameter
     * @required
     */
    private String type;
    
    /**
     * 
     * 
     * @parameter
     * @required
     */
    private File[] files;
    
    /**
     * 
     * @parameter
     * @required
     */
    private File output;
    
    public void execute() throws MojoExecutionException, MojoFailureException {
        // TODO: copy & paste from AbstractGenerateWsdlMojo
        List classpath;
        try {
            classpath = project.getCompileClasspathElements();
        } catch (DependencyResolutionRequiredException ex) {
            throw new MojoExecutionException("Unexpected exception", ex);
        }
        URL[] urls = new URL[classpath.size()];
        for (int i=0; i<classpath.size(); i++) {
            try {
                urls[i] = new File((String)classpath.get(i)).toURL();
            } catch (MalformedURLException ex) {
                throw new MojoExecutionException("Unexpected exception", ex);
            }
        }
        ClassLoader cl = new URLClassLoader(urls);
        
        Deployment deployment;
        
        // Note: To locate the different parts of the default configuration, we use the same
        //       algorithm as in DefaultConfiguration.
        
        // Load the base configuration
        String resourceName = "org/apache/axis/" + type + "/" + type + "-config.wsdd";
        InputStream in = cl.getResourceAsStream(resourceName);
        if (in == null) {
            throw new MojoFailureException("Resource " + resourceName + " not found");
        }
        getLog().info("Loading resource " + resourceName);
        try {
            try {
                deployment = WSDDUtil.load(new InputSource(in));
            } finally {
                in.close();
            }
        } catch (IOException ex) {
            throw new MojoFailureException("Failed to process resource " + resourceName, ex);
        }
        
        // Discover and load additional default configuration fragments
        resourceName = "META-INF/axis/default-" + type + "-config.wsdd";
        Enumeration resources;
        try {
            resources = cl.getResources(resourceName);
        } catch (IOException ex) {
            throw new MojoFailureException("Failed to discover resources with name " + resourceName, ex);
        }
        while (resources.hasMoreElements()) {
            URL url = (URL)resources.nextElement();
            getLog().info("Loading " + url);
            try {
                in = url.openStream();
                try {
                    deployment.merge(WSDDUtil.load(new InputSource(in)));
                } finally {
                    in.close();
                }
            } catch (Exception ex) {
                throw new MojoFailureException("Failed to process " + url, ex);
            }
        }
        
        // Load WSDD files from plug-in configuration
        for (int i=0; i<files.length; i++) {
            File file = files[i];
            getLog().info("Loading " + file);
            try {
                deployment.merge(WSDDUtil.load(new InputSource(file.toURL().toString())));
            } catch (Exception ex) {
                throw new MojoFailureException("Failed to process " + file, ex);
            }
        }
        
        getLog().info("Writing " + output);
        output.getParentFile().mkdirs();
        try {
            FileOutputStream out = new FileOutputStream(output);
            try {
                WSDDUtil.save(deployment, out);
            } finally {
                out.close();
            }
        } catch (Exception ex) {
            throw new MojoFailureException("Failed to write " + output, ex);
        }
    }
}
