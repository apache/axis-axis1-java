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
import java.net.MalformedURLException;
import java.util.HashMap;

import org.apache.axis.constants.Scope;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

public abstract class AbstractWsdl2JavaMojo extends AbstractMojo {
    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;
    
    /**
     * The WSDL file to process.
     * 
     * @parameter
     */
    private File file;
    
    /**
     * The URL of the WSDL to process. This should only be used for remote WSDLs. For local files,
     * use the <code>file</code> parameter.
     * 
     * @parameter
     */
    private String url;
    
    /**
     * Output directory for emitted files.
     * 
     * @parameter
     * @required
     */
    private File output;

    /**
     * Add scope to deploy.xml: "Application", "Request", "Session".
     * 
     * @parameter
     */
    private String deployScope;

    /**
     * Mappings of namespaces to packages.
     * 
     * @parameter
     */
    private Mapping[] mappings;
    
    /**
     * The default type mapping registry to use. Either 1.1 or 1.2.
     * 
     * @parameter default-value="1.2"
     * @required
     */
    private String typeMappingVersion;
    
    /**
     * emit server-side bindings for web service; default=false
     * 
     * @parameter default-value="false"
     */
    private boolean serverSide;
    
    /**
     * deploy skeleton (true) or implementation (false) in deploy.wsdd.
     * Default is false.  Assumes server-side="true".
     * 
     * @parameter default-value="false"
     */
    private boolean skeleton;
    
    /**
     * flag to generate code for all elements, even unreferenced ones
     * 
     * @parameter default-value="false"
     */
    private boolean all;
    
    /**
     * Set the wrap arrays flag - if true this will make new classes
     * like "ArrayOfString" for literal "wrapped" arrays.  Otherwise it
     * will use "String []" and generate appropriate metadata.
     * 
     * @parameter default-value="false"
     */
    private boolean wrapArrays;
    
    public void execute() throws MojoExecutionException, MojoFailureException {
        String wsdlUrl;
        if (file != null && url != null) {
            throw new MojoFailureException("Invalid plugin configuration: either use file or url, but not both!");
        } else if (file != null) {
            try {
                wsdlUrl = file.toURL().toExternalForm();
            } catch (MalformedURLException ex) {
                throw new MojoExecutionException("Unexpected exception", ex);
            }
        } else if (url != null) {
            wsdlUrl = url;
        } else {
            throw new MojoFailureException("Invalid plugin configuration: file or url must be given!");
        }
        
        // Instantiate the emitter
        Emitter emitter = new Emitter();

        //extract the scope
        Scope scope = Scope.getScope(deployScope, null);
        if (scope != null) {
            emitter.setScope(scope);
        } else if (deployScope != null) {
            getLog().warn("Unrecognized scope:  " + deployScope + ".  Ignoring it.");
        }

        //do the mappings, with namespaces mapped as the key
        if (mappings != null && mappings.length > 0) {
            HashMap namespaceMap = new HashMap();
            for (int i=0; i<mappings.length; i++) {
                namespaceMap.put(mappings[i].getNamespace(), mappings[i].getPackage());
            }
            emitter.setNamespaceMap(namespaceMap);
        }
//        emitter.setTestCaseWanted(testCase);
//        emitter.setHelperWanted(helperGen);
//        if (factory != null) {
//            emitter.setFactory(factory);
//        }
//        emitter.setNamespaceIncludes(nsIncludes);
//        emitter.setNamespaceExcludes(nsExcludes);
//        emitter.setProperties(properties);
//        emitter.setImports(!noImports);
        emitter.setAllWanted(all);
        emitter.setOutputDir(output.getAbsolutePath());
        emitter.setServerSide(serverSide);
        emitter.setSkeletonWanted(skeleton);
//        emitter.setVerbose(verbose);
//        emitter.setDebug(debug);
//        emitter.setQuiet(quiet);
        emitter.setTypeMappingVersion(typeMappingVersion);
//        emitter.setNowrap(noWrapped);
//        emitter.setAllowInvalidURL(allowInvalidURL);
        emitter.setWrapArrays(wrapArrays);
//        if (namespaceMappingFile != null) {
//            emitter.setNStoPkg(namespaceMappingFile.toString());
//        }
//        emitter.setTimeout(timeout);
//        emitter.setImplementationClassName(implementationClassName);

//        Authenticator.setDefault(new DefaultAuthenticator(username, password));
//        if (classpath != null) {
//            AntClassLoader cl = new AntClassLoader(
//                    getClass().getClassLoader(),
//                    getProject(),
//                    classpath,
//                    false);
//            log("Using CLASSPATH " + cl.getClasspath(),
//                    Project.MSG_VERBOSE);
//            ClassUtils.setDefaultClassLoader(cl);
//        }
        
        configureEmitter(emitter);
        
        getLog().info("Processing " + wsdlUrl);
        
        try {
            emitter.run(wsdlUrl);
        } catch (Exception ex) {
            throw new MojoFailureException("wsdl2java failed", ex);
        }
        
        addSourceRoot(project, output.getAbsolutePath());
    }
    
    protected abstract void configureEmitter(Emitter emitter);
    protected abstract void addSourceRoot(MavenProject project, String path);
}
