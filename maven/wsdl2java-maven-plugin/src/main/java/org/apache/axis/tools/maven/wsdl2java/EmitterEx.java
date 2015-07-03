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

import org.apache.axis.wsdl.toJava.Emitter;

public class EmitterEx extends Emitter {
    private boolean clientSide;
    private boolean generateImplementation;
    private String clientOutputDirectory;
    private String deployWsdd;
    private String undeployWsdd;
    private String testHttpPortSystemProperty;
    private int testDefaultHttpPort = -1;
    
    public boolean isClientSide() {
        return clientSide;
    }

    public void setClientSide(boolean clientSide) {
        this.clientSide = clientSide;
    }

    public boolean isGenerateImplementation() {
        return generateImplementation;
    }

    public void setGenerateImplementation(boolean generateImplementation) {
        this.generateImplementation = generateImplementation;
    }

    public String getClientOutputDirectory() {
        return clientOutputDirectory;
    }

    public void setClientOutputDirectory(String clientOutputDirectory) {
        this.clientOutputDirectory = clientOutputDirectory;
    }

    public String getDeployWsdd() {
        return deployWsdd;
    }
    
    public void setDeployWsdd(String deployWsdd) {
        this.deployWsdd = deployWsdd;
    }
    
    public String getUndeployWsdd() {
        return undeployWsdd;
    }
    
    public void setUndeployWsdd(String undeployWsdd) {
        this.undeployWsdd = undeployWsdd;
    }

    public String getTestHttpPortSystemProperty() {
        return testHttpPortSystemProperty;
    }

    public void setTestHttpPortSystemProperty(String testHttpPortSystemProperty) {
        this.testHttpPortSystemProperty = testHttpPortSystemProperty;
    }

    public int getTestDefaultHttpPort() {
        return testDefaultHttpPort;
    }

    public void setTestDefaultHttpPort(int testDefaultHttpPort) {
        this.testDefaultHttpPort = testDefaultHttpPort;
    }
}
