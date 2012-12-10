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
package org.apache.axis.tools.maven.wsdl2java;

import java.io.File;
import java.net.MalformedURLException;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceFactory;

import org.apache.axis.constants.Scope;
import org.apache.axis.tools.maven.shared.nsmap.Mapping;
import org.apache.axis.tools.maven.shared.nsmap.MappingUtil;
import org.apache.axis.wsdl.gen.GeneratorFactory;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.xml.resolver.CatalogManager;
import org.apache.xml.resolver.tools.CatalogResolver;

import com.github.veithen.ulog.PlexusLoggerInjector;

public abstract class AbstractWsdl2JavaMojo extends AbstractMojo {
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
     * The catalog file to resolve external entity references. This can be any type of catalog
     * supported by <a
     * href="http://xerces.apache.org/xml-commons/components/resolver/">xml-resolver</a>.
     * 
     * @parameter
     */
    private File catalog;
    
    /**
     * Determines the scope that will be specified for the service in the deployment WSDD. Valid
     * values are <code>application</code>, <code>request</code> and <code>session</code>. This
     * parameter has no effect if {@link #generate} is set to <code>client</code> or if
     * {@link #deployWsdd} is not specified. If this parameter is not specified, then no explicit
     * scope will be configured in the deployment WSDD, in which case the scope defaults to
     * <code>request</code>.
     * <br>
     * Note that these semantics (in particular the default scope <code>request</code>) are
     * compatible with the <code>deployScope</code> parameter of the wsdl2java Ant task. This
     * simplifies the migration of Ant builds to Maven. However, for most services,
     * <code>application</code> is a more reasonable setting.
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
     */
    private String typeMappingVersion;
    
    /**
     * Specifies what artifacts should be generated. Valid values are:
     * <ul>
     * <li><code>client</code>: generate client stubs
     * <li><code>server</code>: generate server side artifacts
     * <li><code>both</code>: generate all artifacts
     * </ul>
     * The <code>server</code> mode can also be used for clients that rely on dynamic proxies
     * created using the JAX-RPC {@link ServiceFactory} API, because they don't need client stubs.
     * <br>
     * Also note that the <code>both</code> mode is only really meaningful if {@link #skeleton} is
     * set to <code>true</code> or if {@link #deployWsdd} is specified. If none of these conditions
     * is satisfied, then <code>client</code> and <code>both</code> will generate the same set of
     * artifacts.
     * 
     * @parameter
     * @required
     */
    private String generate;
    
    /**
     * Set the name of the class implementing the web service. This parameter is ignored if
     * {@link #generate} is set to <code>client</code>. If this parameter is not specified, then a
     * default class name will be chosen if necessary.
     * 
     * @parameter
     */
    private String implementationClassName;
    
    /**
     * Specifies whether a skeleton should be generated. If this parameter is set to
     * <code>false</code>, a skeleton will not be generated. Instead, the generated deployment WSDD
     * will indicate that the implementation class is deployed directly. In such cases, the WSDD
     * contains extra meta data describing the operations and parameters of the implementation
     * class. This parameter is ignored if {@link #generate} is set to <code>client</code>.
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
    
    /**
     * Set the noWrapped flag.
     * 
     * @parameter default-value="false"
     */
    private boolean noWrapped;
    
    /**
     * Turn on/off Helper class generation.
     * 
     * @parameter default-value="false"
     */
    private boolean helperGen;
    
    /**
     * 
     * 
     * @parameter default-value="false"
     */
    private boolean allowInvalidURL;
    
    /**
     * The location of the deployment WSDD file to be generated. This parameter is ignored if
     * {@link #generate} is set to <code>client</code>. If this parameter is not specified, then no
     * deployment WSDD will be generated.
     * 
     * @parameter
     */
    private File deployWsdd;
    
    /**
     * The location of the undeployment WSDD file to be generated. This parameter is ignored if
     * {@link #generate} is set to <code>client</code>. If this parameter is not specified, then no
     * undeployment WSDD will be generated. Note that (in contrast to {@link #deployWsdd}) this
     * parameter is rarely used: in general, no undeployment WSDD is required.
     * 
     * @parameter
     */
    private File undeployWsdd;
    
    /**
     * A set of Java to XML type mappings that override the default mappings. This can be used to
     * <a href="java-xml-type-mappings.html">change the Java class associated with an XML type</a>.
     * 
     * @parameter
     */
    private JavaXmlTypeMapping[] javaXmlTypeMappings;
    
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
        EmitterEx emitter = new EmitterEx();
        if (generate.equals("client")) {
            emitter.setClientSide(true);
            emitter.setServerSide(false);
        } else if (generate.equals("server")) {
            emitter.setClientSide(false);
            emitter.setServerSide(true);
        } else if (generate.equals("both")) {
            emitter.setClientSide(true);
            emitter.setServerSide(true);
        } else {
            throw new MojoExecutionException("Invalid value '" + generate + "' for the 'generate' parameter");
        }
        if (deployWsdd != null) {
            emitter.setDeployWsdd(deployWsdd.getAbsolutePath());
        }
        if (undeployWsdd != null) {
            emitter.setUndeployWsdd(undeployWsdd.getAbsolutePath());
        }
        emitter.setFactory(new JavaGeneratorFactoryEx(emitter));

        //extract the scope
        Scope scope = Scope.getScope(deployScope, null);
        if (scope != null) {
            emitter.setScope(scope);
        } else if (deployScope != null) {
            getLog().warn("Unrecognized scope:  " + deployScope + ".  Ignoring it.");
        }

        //do the mappings, with namespaces mapped as the key
        if (mappings != null && mappings.length > 0) {
            emitter.setNamespaceMap(MappingUtil.getNamespaceToPackageMap(mappings));
        }
//        emitter.setTestCaseWanted(testCase);
        emitter.setHelperWanted(helperGen);
//        emitter.setNamespaceIncludes(nsIncludes);
//        emitter.setNamespaceExcludes(nsExcludes);
//        emitter.setProperties(properties);
//        emitter.setImports(!noImports);
        emitter.setAllWanted(all);
        emitter.setSkeletonWanted(skeleton);
//        emitter.setVerbose(verbose);
//        emitter.setDebug(debug);
//        emitter.setQuiet(quiet);
        emitter.setTypeMappingVersion(typeMappingVersion);
        emitter.setNowrap(noWrapped);
        emitter.setAllowInvalidURL(allowInvalidURL);
        emitter.setWrapArrays(wrapArrays);
//        if (namespaceMappingFile != null) {
//            emitter.setNStoPkg(namespaceMappingFile.toString());
//        }
//        emitter.setTimeout(timeout);
        emitter.setImplementationClassName(implementationClassName);

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
        
        if (javaXmlTypeMappings != null && javaXmlTypeMappings.length > 0) {
            GeneratorFactory factory = emitter.getFactory();
            CustomizableBaseTypeMapping btm = new CustomizableBaseTypeMapping(factory.getBaseTypeMapping());
            for (int i=0; i<javaXmlTypeMappings.length; i++) {
                String xmlTypeName = javaXmlTypeMappings[i].getXmlType();
                if (xmlTypeName.length() == 0 || xmlTypeName.charAt(0) != '{') {
                    throw new MojoFailureException("Invalid XML type '" + xmlTypeName + "'");
                }
                int idx = xmlTypeName.indexOf('}', 1);
                if (idx == -1) {
                    throw new MojoFailureException("Invalid XML type '" + xmlTypeName + "'");
                }
                btm.addMapping(new QName(xmlTypeName.substring(1, idx), xmlTypeName.substring(idx+1)), javaXmlTypeMappings[i].getJavaType());
            }
            factory.setBaseTypeMapping(btm);
        }
        
        configureEmitter(emitter);
        
        if (catalog != null) {
            CatalogManager catalogManager = new CatalogManager();
            catalogManager.setCatalogFiles(catalog.getAbsolutePath());
            emitter.setEntityResolver(new CatalogResolver(catalogManager));
        }
        
        getLog().info("Processing " + wsdlUrl);
        
        try {
            emitter.run(wsdlUrl);
        } catch (Exception ex) {
            throw new MojoFailureException("wsdl2java failed", ex);
        }
        
        addSourceRoot(project);
    }
    
    protected abstract void configureEmitter(EmitterEx emitter);
    protected abstract void addSourceRoot(MavenProject project);
}
