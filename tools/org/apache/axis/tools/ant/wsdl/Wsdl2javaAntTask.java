/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2002 The Apache Software Foundation.  All rights
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
package org.apache.axis.tools.ant.wsdl;

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
 * Ant task for running Wsdl2java utility. It is still not ready for
 * end users, though that is the final intent.
 * This task does no dependency checking; files are generated whether they
 * need to be or not. 
 * As well as the nested parameters, this task uses  the file 
 * <tt>NStoPkg.properties</tt> in the project base directory
 * for namespace mapping
 * @ant.task category="axis"
 * @author steve loughran
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
    private String tm = "1.1";
    private long timeout = 45000;
    
    /**
     * do we print a stack trace when something goes wrong?
     */
    private boolean printStackTraceOnFailure=true;
    /**
     * what action to take when there was a failure and the source was some
     * URL
     */
    private boolean failOnNetworkErrors=false;    

    /**
     * validation code
     * @throws  BuildException  if validation failed
     */ 
    protected void validate() 
            throws BuildException {
        if(url==null || url.length()==0) {
            throw new BuildException("No url specified");
        }
        if(timeout<-1) {
            throw new BuildException("negative timeout supplied");
        }
        File outdir=new File(output);
        if(!outdir.isDirectory() || !outdir.exists()) {
            throw new BuildException("output directory is not valid");
        }
            
    }
    
    /**
     * trace out parameters
     * @param level to log at
     * @see org.apache.tools.ant.Project#log
     */
    public void traceParams(int logLevel) {
        log("Running Wsdl2javaAntTask with parameters:", logLevel);
        log("\tverbose:" + verbose, logLevel);
        log("\tserver-side:" + server, logLevel);
        log("\tskeletonDeploy:" + skeletonDeploy, logLevel);
        log("\thelperGen:" + helperGen, logLevel);
        log("\tfactory:" + factory, logLevel);
        log("\ttestCase:" + testCase, logLevel);
        log("\tnoImports:" + noImports, logLevel);
        log("\tNStoPkg:" + namespaceMap, logLevel);
        log("\toutput:" + output, logLevel);
        log("\tdeployScope:" + deployScope, logLevel);
        log("\tURL:" + url, logLevel);
        log("\tall:" + all, logLevel);
        log("\ttypeMappingVersion:" + tm, logLevel);
        log("\ttimeout:" + timeout, logLevel);
        log("\tfailOnNetworkErrors:" + failOnNetworkErrors, logLevel);
        log("\tprintStackTraceOnFailure:" + printStackTraceOnFailure, logLevel);
    }    

    /**
     * The method executing the task
     * @throws  BuildException  if validation or execution failed
     */ 
    public void execute() throws BuildException {
        traceParams(Project.MSG_VERBOSE);
        validate();
        try {
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
            //TODO: extract this and make it an attribute
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
                    // it impossible to continue with the test, so we stop here
                    if(!failOnNetworkErrors) {
                        // test mode, issue a warning, and return without
                        //reporting a fatal error.
                        log(e.toString(), Project.MSG_WARN);
                        return;
                    } else {
                        //in 'consumer' mode, bail out with the URL
                        throw new BuildException("Could not build "+url,e);
                    }
                } else {
                    throw e;
                }
            }
        } catch (BuildException b) {
            throw b;
        } catch (Throwable t) {
            if(printStackTraceOnFailure) {
                traceParams(Project.MSG_INFO);
                t.printStackTrace();
            }
            throw new BuildException("Error while processing WSDL in Wsdl2javaAntTask for "+url,t); 
        }

    }

    /**
     *  flag for verbose output; default=false
     *
     *@param  verbose  The new verbose value
     */   
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     *  emit server-side bindings for web service; default=false
     */
    public void setServerSide(boolean parameter) {
        this.server = parameter;
    }

    /**
     * deploy skeleton (true) or implementation (false) in deploy.wsdd. 
     * Default is false.  Assumes server-side="true".     
     */
    public void setSkeletonDeploy(boolean parameter) {
        this.skeletonDeploy = parameter;
    }

    /**
     * flag for automatic Junit testcase generation
     * default is false
     */
    public void setTestCase(boolean parameter) {
        this.testCase = parameter;
    }

    /**
     * Turn on/off Helper class generation;
     * default is false
     */
    public void setHelperGen(boolean parameter) {
        this.helperGen = parameter;
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
    public void setNoImports(boolean parameter) {
        this.noImports = parameter;
    }

    /**
     * output directory for emitted files 
     */
    public void setOutput(File parameter) throws BuildException {
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
     * URL to fetch and generate WSDL for.
     * Can be remote or a local file.
     */
    public void setURL(String parameter) {
        this.url = parameter;
    }

    /**
     * flag to generate code for all elements, even unreferenced ones
     * default=false;
     */
    public void setAll(boolean parameter) {
        this.all = parameter;
    }

    /**
     * Set the type mapping version; default is "1.2"
     */
    public void setTypeMappingVersion(String parameter) {
        this.tm = parameter;
    }

    /**
     * timeout in seconds for URL retrieval; default is 45 seconds.
     * Set this to -1 to disable timeouts altogether: other negative values
     * are not allowed)
     * TODO: normally format conversions are failures, but because this method
     * ignored such errors, we have to keep going. Maybe it could be escalated to 
     * a failure in end-user versions.
     */
    public void setTimeout(String parameter) {
        try {
            this.timeout = new Long(parameter).longValue();
        } catch (NumberFormatException e) {
            // Sorry, stick with default.
            log("Could not convert "+parameter+" to a number", Project.MSG_WARN);
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


