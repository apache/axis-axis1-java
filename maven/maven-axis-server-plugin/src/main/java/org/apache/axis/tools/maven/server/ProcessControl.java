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
package org.apache.axis.tools.maven.server;

import org.codehaus.plexus.logging.Logger;

/**
 * Defines the actions to be executed after a given process has been started and when a process is
 * stopped.
 * 
 * @author Andreas Veithen
 */
public interface ProcessControl {
    /**
     * Indicates that the process is expected to be still running after the action is completed.
     */
    int RUNNING = 1;

    /**
     * Indicates that the action has sent a request to the process to initiate a clean shutdown,
     * i.e. that the process is expected to be stopping (but not necessarily to be stopped yet).
     */
    int STOPPING = 2;
    
    /**
     * Initialize the process. This typically involves waiting for the process to be completely
     * started and to configure the server process, e.g. to deploy services.
     * 
     * @param logger
     * @param process
     * @throws Exception
     */
    void initializeProcess(Logger logger, Process process) throws Exception;
    
    /**
     * Prepare the process for shutdown. Typically (but not necessarily) this involves sending a
     * request to initiate a clean shutdown of the process.
     * 
     * @param logger
     * @return
     * @throws Exception
     */
    int shutdownProcess(Logger logger) throws Exception;
}
