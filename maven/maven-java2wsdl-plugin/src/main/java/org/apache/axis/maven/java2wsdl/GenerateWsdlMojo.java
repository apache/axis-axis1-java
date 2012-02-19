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
package org.apache.axis.maven.java2wsdl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.axis.maven.shared.nsmap.Mapping;
import org.apache.axis.maven.shared.nsmap.MappingUtil;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.wsdl.fromJava.Emitter;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.github.veithen.ulog.PlexusLoggerInjector;

/**
 * 
 * 
 * @goal generate-wsdl
 * @phase process-classes
 * @requiresDependencyResolution compile
 */
public class GenerateWsdlMojo extends AbstractMojo {
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
     * The name of the class to generate a WSDL for. The class must be on the classpath.
     * 
     * @parameter
     * @required
     */
    private String className;

    /**
     * The target namespace for the interface.
     * 
     * @parameter
     * @required
     */
    private String namespace;
    
    /**
     * Mappings of packages to namespaces.
     * 
     * @parameter
     */
    private Mapping[] mappings;
    
    /**
     * The style of the WSDL document: RPC, DOCUMENT or WRAPPED.
     * If RPC, a rpc/encoded wsdl is generated. If DOCUMENT, a
     * document/literal wsdl is generated. If WRAPPED, a
     * document/literal wsdl is generated using the wrapped approach.
     *
     * @parameter
     */
    private String style;

    /**
     * Set the use option
     * 
     * @parameter
     */
    private String use;
    
    /**
     * The url of the location of the service. The name after the last slash or
     * backslash is the name of the service port (unless overridden by the -s
     * option). The service port address location attribute is assigned the
     * specified value.
     * 
     * @parameter
     * @required
     */
    private String location;
    
    /**
     * The name of the output WSDL file.
     * 
     * @parameter
     * @required
     */
    private File output;
    
    /**
     * Sets the deploy flag
     * 
     * @parameter default-value="false"
     */
    private boolean deploy;
    
    public void execute() throws MojoExecutionException, MojoFailureException {
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
        // TODO: this will likely make the plugin non thread safe
        ClassUtils.setDefaultClassLoader(new URLClassLoader(urls));
        try {
            Emitter emitter = new Emitter();
            if (mappings != null && mappings.length > 0) {
                emitter.setNamespaceMap(MappingUtil.getPackageToNamespaceMap(mappings));
            }
            try {
                emitter.setCls(className);
            } catch (ClassNotFoundException ex) {
                throw new MojoFailureException("Class " + className + " not found");
            }
            if (style != null) {
                emitter.setStyle(style);
            }
            if (use != null) {
                emitter.setUse(use);
            }
            emitter.setIntfNamespace(namespace);
            emitter.setLocationUrl(location);
            output.getParentFile().mkdirs();
            try {
                emitter.emit(output.getAbsolutePath(), Emitter.MODE_ALL);
                if (deploy) {
                    generateServerSide(emitter, /*(outputImpl != null) ? outputImpl :*/ output.getAbsolutePath());
                }
            } catch (Exception ex) {
                throw new MojoFailureException("java2wsdl failed", ex);
            }
        } finally {
            // TODO: apparently this is a no-op
            ClassUtils.setDefaultClassLoader(null);
        }
    }

    // Copy & paste from Java2WsdlAntTask
    /**
     * Generate the server side artifacts from the generated WSDL
     * 
     * @param j2w the Java2WSDL emitter
     * @param wsdlFileName the generated WSDL file
     * @throws Exception
     */
    protected void generateServerSide(Emitter j2w, String wsdlFileName) throws Exception {
        org.apache.axis.wsdl.toJava.Emitter w2j = new org.apache.axis.wsdl.toJava.Emitter();
        File wsdlFile = new File(wsdlFileName);
        w2j.setServiceDesc(j2w.getServiceDesc());
        w2j.setQName2ClassMap(j2w.getQName2ClassMap());
        w2j.setOutputDir(wsdlFile.getParent());
        w2j.setServerSide(true);   
        w2j.setDeploy(true);
        w2j.setHelperWanted(true);

        // setup namespace-to-package mapping
        String ns = j2w.getIntfNamespace();
        String clsName = j2w.getCls().getName();
        int idx = clsName.lastIndexOf(".");
        String pkg = null;
        if (idx > 0) {
            pkg = clsName.substring(0, idx);            
            w2j.getNamespaceMap().put(ns, pkg);
        }
        
        Map nsmap = j2w.getNamespaceMap();
        if (nsmap != null) {
            for (Iterator i = nsmap.keySet().iterator(); i.hasNext(); ) {
                pkg = (String) i.next();
                ns = (String) nsmap.get(pkg);
                w2j.getNamespaceMap().put(ns, pkg);
            }
        }
        
        // set 'deploy' mode
        w2j.setDeploy(true);
        
        if (j2w.getImplCls() != null) {
            w2j.setImplementationClassName(j2w.getImplCls().getName());
        } else {
            if (!j2w.getCls().isInterface()) {
                w2j.setImplementationClassName(j2w.getCls().getName());
            } else {
                throw new Exception("implementation class is not specified.");
            }
        }
        
        w2j.run(wsdlFileName);
    }
}
