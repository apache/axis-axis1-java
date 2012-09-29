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

import org.apache.maven.project.MavenProject;

/**
 * Create Java classes from local or remote WSDL for usage in test cases.
 * 
 * @goal generate-test-sources
 * @phase generate-test-sources
 */
public class GenerateTestSourcesMojo extends AbstractWsdl2JavaMojo {
    /**
     * Output directory for generated Java files.
     * 
     * @parameter default-value="${project.build.directory}/generated-test-sources/wsdl2java"
     */
    private File testSourceOutputDirectory;

    /**
     * Flag indicating whether a default (empty) implementation should be generated.
     * 
     * @parameter default-value="false"
     */
    private boolean implementation;
    
    /**
     * Flag indicating whether a basic unit test should be generated.
     * 
     * @parameter default-value="false"
     */
    private boolean testCase;
    
    /**
     * 
     * @parameter
     */
    private String testHttpPortSystemProperty;
    
    /**
     * 
     * @parameter
     */
    private int testDefaultHttpPort = -1;
    
    protected void configureEmitter(EmitterEx emitter) {
        emitter.setOutputDir(testSourceOutputDirectory.getAbsolutePath());
        emitter.setGenerateImplementation(implementation);
        emitter.setTestCaseWanted(testCase);
        emitter.setTestHttpPortSystemProperty(testHttpPortSystemProperty);
        emitter.setTestDefaultHttpPort(testDefaultHttpPort);
    }

    protected void addSourceRoot(MavenProject project) {
        project.addTestCompileSourceRoot(testSourceOutputDirectory.getAbsolutePath());
    }
}
