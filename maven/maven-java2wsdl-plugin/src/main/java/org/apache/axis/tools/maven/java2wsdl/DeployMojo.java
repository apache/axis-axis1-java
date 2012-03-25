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
package org.apache.axis.tools.maven.java2wsdl;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.apache.axis.wsdl.fromJava.Emitter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.compiler.Compiler;
import org.codehaus.plexus.compiler.CompilerConfiguration;
import org.codehaus.plexus.compiler.CompilerException;
import org.codehaus.plexus.compiler.manager.CompilerManager;
import org.codehaus.plexus.compiler.manager.NoSuchCompilerException;

/**
 * Generate deployment artifacts (WSDD files and helper classes) for a code first Web service. The
 * goal will also compile the generated classes so that it can be used on a service that is built in
 * the same project.
 * 
 * @goal deploy
 * @phase process-classes
 * @requiresDependencyResolution compile
 */
public class DeployMojo extends AbstractGenerateWsdlMojo {
    /**
     * @component
     */
    private CompilerManager compilerManager;
    
    /**
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     * @readonly
     */
    private File outputDirectory;
    
    /**
     * The directory where the source code of the generated Java artifacts (helper classes) is
     * placed.
     * 
     * @parameter default-value="${project.build.directory}/generated-sources/deploy"
     * @required
     */
    private File sourceOutputDirectory;
    
    protected void postProcess(Emitter j2w, File wsdlFile) throws MojoExecutionException, MojoFailureException {
        // Generate the server side artifacts from the generated WSDL
        org.apache.axis.wsdl.toJava.Emitter w2j = new org.apache.axis.wsdl.toJava.Emitter();
        w2j.setServiceDesc(j2w.getServiceDesc());
        w2j.setQName2ClassMap(j2w.getQName2ClassMap());
        w2j.setOutputDir(sourceOutputDirectory.getAbsolutePath());
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
                throw new MojoFailureException("implementation class is not specified.");
            }
        }
        
        try {
            w2j.run(wsdlFile.getAbsolutePath());
        } catch (Exception ex) {
            throw new MojoFailureException("Failed to generate deployment code", ex);
        }
        
        // We add the directory with the generated sources to the compile source roots even
        // if we compile the code ourselves. That is important when using eclipse:eclipse.
        getProject().addCompileSourceRoot(sourceOutputDirectory.getPath());
        
        CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
        compilerConfiguration.setOutputLocation(outputDirectory.getAbsolutePath());
        compilerConfiguration.setSourceLocations(Collections.singletonList(sourceOutputDirectory.getAbsolutePath()));
        compilerConfiguration.setSourceVersion("1.4");
        compilerConfiguration.setTargetVersion("1.4");
        
        Compiler compiler;
        try {
            compiler = compilerManager.getCompiler("javac");
        } catch (NoSuchCompilerException ex) {
            throw new MojoExecutionException("No such compiler '" + ex.getCompilerId() + "'.");
        }
        
        try {
            compiler.compile(compilerConfiguration);
        } catch (CompilerException ex) {
            throw new MojoExecutionException("Compilation failed.", ex);
        }
    }
}
