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
package org.apache.axis.maven.server;

import org.codehaus.plexus.logging.Logger;

/**
 * Action to be executed when a process is stopped. Typically (but not necessarily) this involves
 * sending a request to initiate a clean shutdown of the process.
 * 
 * @author Andreas Veithen
 */
public interface ProcessStopAction {
    /**
     * Indicates that the process is expected to be still running after the action is completed.
     */
    int RUNNING = 1;

    /**
     * Indicates that the action has sent a request to the process to initiate a clean shutdown,
     * i.e. that the process is expected to be stopping (but not necessarily to be stopped yet).
     */
    int STOPPING = 2;
    
    int execute(Logger logger) throws Exception;
}
