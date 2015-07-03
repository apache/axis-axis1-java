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
 * Create Java classes from local or remote WSDL.
 * 
 * @goal generate-sources
 * @phase generate-sources
 */
public class GenerateSourcesMojo extends AbstractWsdl2JavaMojo {
    /**
     * Output directory for generated Java files.
     * 
     * @parameter default-value="${project.build.directory}/generated-sources/wsdl2java"
     */
    private File sourceOutputDirectory;
    
    /**
     * Flag indicating whether the stub and locator should be written to
     * {@link #sourceOutputDirectory} (<code>false</code>) or to {@link #testSourceOutputDirectory}
     * (<code>true</code>). Set this parameter to <code>true</code> if the main artifact of your
     * project should not contain client-side code, but you need it in your test cases. Note that
     * this parameter is only meaningful if <code>generate</code> is set to <code>both</code>.
     * 
     * @parameter default-value="false"
     */
    private boolean writeStubToTestSources;
    
    /**
     * Output directory used for the stub and locator if {@link #writeStubToTestSources} is
     * <code>true</code>.
     * 
     * @parameter default-value="${project.build.directory}/generated-test-sources/wsdl2java"
     */
    private File testSourceOutputDirectory;

    protected void configureEmitter(EmitterEx emitter) {
        emitter.setOutputDir(sourceOutputDirectory.getAbsolutePath());
        if (writeStubToTestSources) {
            emitter.setClientOutputDirectory(testSourceOutputDirectory.getAbsolutePath());
        }
        // In a Maven build, generated sources are always written to a directory other than
        // the source directory. By default, the emitter would generate an empty implementation
        // because it doesn't see the implementation provided by the developer. We don't want this
        // because these two classes would collide. Therefore implementationWanted is hardcoded
        // to false:
        emitter.setGenerateImplementation(false);
    }

    protected void addSourceRoot(MavenProject project) {
        project.addCompileSourceRoot(sourceOutputDirectory.getAbsolutePath());
        if (writeStubToTestSources) {
            project.addTestCompileSourceRoot(testSourceOutputDirectory.getAbsolutePath());
        }
    }
}
