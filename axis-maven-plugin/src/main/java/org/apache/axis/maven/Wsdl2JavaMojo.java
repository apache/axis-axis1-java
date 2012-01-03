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

import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.maven.project.MavenProject;

/**
 * Create Java classes from local or remote WSDL.
 * 
 * @goal wsdl2java
 * @phase generate-sources
 */
public class Wsdl2JavaMojo extends AbstractWsdl2JavaMojo {
    protected void configureEmitter(Emitter emitter) {
        // In a Maven build, generated sources are always written to a directory other than
        // the source directory. By default, the emitter would generate an empty implementation
        // because it doesn't see the implementation provided by the developer. We don't want this
        // because these two classes would collide. Therefore implementationWanted is hardcoded
        // to false:
        emitter.setImplementationWanted(false);
    }

    protected void addSourceRoot(MavenProject project, String path) {
        project.addCompileSourceRoot(path);
    }
}
