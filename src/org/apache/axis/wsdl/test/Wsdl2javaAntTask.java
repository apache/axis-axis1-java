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
package org.apache.axis.wsdl.test;

import org.apache.axis.wsdl.Emitter; 
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.CallTarget;
import org.apache.tools.ant.Project;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

/**
 * Simple Ant task for running Wsdl2java utility. 
 *
 * @author Davanum Srinivas (dims@yahoo.com)
 */
public class Wsdl2javaAntTask extends Task
{
    private boolean verbose = true;
    private boolean skeleton = true ;
    private boolean messageContext = false;
    private boolean testCase = false;
    private boolean noImports = true;
    private String javaPackageName = "";
    private String output = "." ;
    private String deployScope = "";
    private String url = "";

    // The method executing the task
    public void execute() throws BuildException {
        try {
            log("Running Wsdl2javaAntTask with parameters:", Project.MSG_VERBOSE);
            log("\tverbose:" + verbose, Project.MSG_VERBOSE);
            log("\tskeleton:" + skeleton, Project.MSG_VERBOSE);
            log("\tmessageContext:" + messageContext, Project.MSG_VERBOSE);
            log("\ttestCase:" + testCase, Project.MSG_VERBOSE);
            log("\tnoImports:" + noImports, Project.MSG_VERBOSE);
            log("\tpackage:" + javaPackageName, Project.MSG_VERBOSE);
            log("\toutput:" + output, Project.MSG_VERBOSE);
            log("\tdeployScope:" + deployScope, Project.MSG_VERBOSE);
            log("\tURL:" + url, Project.MSG_VERBOSE);
            
            // Instantiate the emitter
            Emitter emitter = new Emitter();
            if ("application".equalsIgnoreCase(deployScope)) {
                emitter.setScope(Emitter.APPLICATION_SCOPE);
            }
            else if ("request".equalsIgnoreCase(deployScope)) {
                emitter.setScope(Emitter.REQUEST_SCOPE);
            }
            else if ("session".equalsIgnoreCase(deployScope)) {
                emitter.setScope(Emitter.SESSION_SCOPE);
            }
            else {
                log("Unrecognized scope:  " + deployScope + ".  Ignoring it.", Project.MSG_VERBOSE);
            }
            if (javaPackageName == null || javaPackageName.length()<=0)
                emitter.generatePackageName(true);
            else
                emitter.setPackageName(javaPackageName);
            emitter.generateTestCase(testCase);
            emitter.generateImports(!noImports);
            emitter.setOutputDir(output);
            emitter.generateMessageContext(messageContext);
            emitter.generateSkeleton(skeleton);
            emitter.verbose(verbose);
            emitter.emit(url);
        } catch (Throwable t) {
            throw new BuildException("Error while running " + getClass().getName(), t); 
        }
    }

    // The setter for the "verbose" attribute
    public void setVerbose(String parameter) {
        this.verbose = Project.toBoolean(parameter);
    }

    // The setter for the "skeleton" attribute
    public void setSkeleton(String parameter) {
        this.skeleton = Project.toBoolean(parameter);
    }

    // The setter for the "messagecontext" attribute
    public void setMessageContext(String parameter) {
        this.messageContext = Project.toBoolean(parameter);
    }

    // The setter for the "testcase" attribute
    public void setTestCase(String parameter) {
        this.testCase = Project.toBoolean(parameter);
    }

    // The setter for the "noimports" attribute
    public void setNoImports(String parameter) {
        this.noImports = Project.toBoolean(parameter);
    }

    // The setter for the "package" attribute
    public void setPackage(String parameter) {
        this.javaPackageName = parameter;
    }

    // The setter for the "output" attribute
    public void setOutput(String parameter) {
        this.output = parameter;
    }

    // The setter for the "deployscope" attribute
    public void setDeployScope(String parameter) {
        this.deployScope = parameter;
    }
    
    // The setter for the "url" attribute
    public void setURL(String parameter) {
        this.url = parameter;
    }
}


