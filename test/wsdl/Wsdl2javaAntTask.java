/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Ant", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package test.wsdl;

import org.apache.axis.enum.Scope;
import org.apache.axis.utils.DefaultAuthenticator;
import org.apache.axis.wsdl.toJava.Emitter;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import java.io.File;
import java.io.IOException;
import java.net.Authenticator;
import java.util.HashMap;

/**
 * Simple Ant task for running Wsdl2java utility. 
 *
 * @author Davanum Srinivas (dims@yahoo.com)
 */
public class Wsdl2javaAntTask extends Task
{
    private boolean verbose = false;
    private boolean server = false;
    private boolean skeletonDeploy = false;
    private boolean testCase = false;
    private boolean noImports = false;
    private boolean all = false;
    private boolean helperGen = false;
    private String factory = null;
    private HashMap namespaceMap = new HashMap();
    private String output = "." ;
    private String deployScope = "";
    private String url = "";
    private String tm = "1.2";
    private long timeout = 45000;

    /**
     * The method executing the task
     * @throws  BuildException  if validation or execution failed
     */
    public void execute() throws BuildException {
        try {
            log("Running Wsdl2javaAntTask with parameters:", Project.MSG_VERBOSE);
            log("\tverbose:" + verbose, Project.MSG_VERBOSE);
            log("\tserver-side:" + server, Project.MSG_VERBOSE);
            log("\tskeletonDeploy:" + skeletonDeploy, Project.MSG_VERBOSE);
            log("\thelperGen:" + helperGen, Project.MSG_VERBOSE);
            log("\tfactory:" + factory, Project.MSG_VERBOSE);
            log("\ttestCase:" + testCase, Project.MSG_VERBOSE);
            log("\tnoImports:" + noImports, Project.MSG_VERBOSE);
            log("\tNStoPkg:" + namespaceMap, Project.MSG_VERBOSE);
            log("\toutput:" + output, Project.MSG_VERBOSE);
            log("\tdeployScope:" + deployScope, Project.MSG_VERBOSE);
            log("\tURL:" + url, Project.MSG_VERBOSE);
            log("\tall:" + all, Project.MSG_VERBOSE);
            log("\ttypeMappingVersion:" + tm, Project.MSG_VERBOSE);
            log("\ttimeout:" + timeout, Project.MSG_VERBOSE);

            // Instantiate the emitter
            Emitter emitter = new Emitter();

            Scope scope = Scope.getScope(deployScope, null);
            if (scope != null) {
                emitter.setScope(scope);
            } else if ("none".equalsIgnoreCase(deployScope)) {
                /* leave default (null, or not-explicit) */;
            } else {
                log("Unrecognized scope:  " + deployScope + ".  Ignoring it.", Project.MSG_VERBOSE);
            }
            
            if (!namespaceMap.isEmpty()) {
                emitter.setNamespaceMap(namespaceMap);
            }
            emitter.setTestCaseWanted(testCase);
            emitter.setHelperWanted(helperGen);    
            if (factory != null) {
                emitter.setFactory(factory);
            }   
            emitter.setImports(!noImports);
            emitter.setAllWanted(all);
            emitter.setOutputDir(output);
            emitter.setServerSide(server);
            emitter.setSkeletonWanted(skeletonDeploy);
            emitter.setVerbose(verbose);
            emitter.setTypeMappingVersion(tm);
            emitter.setNStoPkg(project.resolveFile("NStoPkg.properties"));
            emitter.setTimeout(timeout);

            Authenticator.setDefault(new DefaultAuthenticator(null,null));

            log("WSDL2Java " + url, Project.MSG_INFO);
            try {
                emitter.run(url);
            } catch (Throwable e) {
                if (url.startsWith("http://")) {
                    // What we have is either a network error or invalid XML -
                    // the latter most likely an HTML error page.  This makes
                    // it impossible to continue with the test, so issue
                    // a warning, and return without reporting a fatal error.
                    log(e.toString(), Project.MSG_WARN);
                    return;
                }
                throw e;
            }
        } catch (Throwable t) {
            t.printStackTrace();
            throw new BuildException("Error while running " + getClass().getName(), t); 
        }
    }

    /**
     *  flag for verbose output; default=false
     *
     *@param  verbose  The new verbose value
     */   
    public void setVerbose(String parameter) {
        this.verbose = Project.toBoolean(parameter);
    }

    /**
     *  emit server-side bindings for web service; default=false
     */
    public void setServerSide(String parameter) {
        this.server = Project.toBoolean(parameter);
    }

    /**
     * deploy skeleton (true) or implementation (false) in deploy.wsdd. 
     * Default is false.  Assumes server-side="true".     
     */
    public void setSkeletonDeploy(String parameter) {
        this.skeletonDeploy = Project.toBoolean(parameter);
    }

    /**
     * flag for automatic Junit testcase generation
     * default is false
     */
    public void setTestCase(String parameter) {
        this.testCase = Project.toBoolean(parameter);
    }

    /**
     * Turn on/off Helper class generation;
     * default is false
     */
    public void setHelperGen(String parameter) {
        this.helperGen = Project.toBoolean(parameter);
    }

    /**
     * name of the Java2WSDLFactory class for 
     * extending WSDL generation functions
     */
    public void setFactory(String parameter) {
        this.factory = parameter;
    }

    /**
     * only generate code for the immediate WSDL document,
     * and not imports; default=false;
     */
    public void setNoImports(String parameter) {
        this.noImports = Project.toBoolean(parameter);
    }

    /**
     * output directory for emitted files 
     */
    public void setOutput(File parameter) {
        try {
            this.output = parameter.getCanonicalPath();
        } catch (IOException ioe) {
            throw new BuildException(ioe);
        }
    }

    /**
     * add scope to deploy.xml: "Application", "Request", "Session" 
     * optional; 
     */
    public void setDeployScope(String parameter) {
        this.deployScope = parameter;
    }
    
    /**
     * url to fetch and generate WSDL for. Can be remote or a local file.
     * required.
     */
    public void setURL(String parameter) {
        this.url = parameter;
    }

    /**
     * flag to generate code for all elements, even unreferenced ones
     * default=false;
     */
    public void setAll(String parameter) {
        this.all = Project.toBoolean(parameter);
    }

    /**
     * set the type mapping version. default is "1.2"
     */
    public void setTypeMappingVersion(String parameter) {
        this.tm = parameter;
    }

    /**
     * timeout in seconds for URL retrieval; default is 45 seconds.
     * Set this to -1 to disable timeouts altogether: other negative values
     * are not allowed)
     */
    public void setTimeout(String parameter) {
        try {
            this.timeout = new Long(parameter).longValue();
        } catch (NumberFormatException e) {
            // Sorry, stick with default.
        }
    }

    /** the command arguments */
    public Mapping createMapping() {
        Mapping pkg = new Mapping();
        return pkg;
    }

    /**
     * Used for nested package definitions.
     */
    public class Mapping {
        private String namespace;
        private String packageName;

        /**
         * namespace to map to a package
         */        
        public void setNamespace(String value) {
            namespace = value;
            if(namespace != null && packageName != null)
                namespaceMap.put(namespace, packageName);
        }
        
        /**
         * java package to generate for the namespace's classes
         */
        public void setPackage(String value) {
            packageName = value;
            if(namespace != null && packageName != null)
                namespaceMap.put(namespace, packageName);
        }
    }

}


