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
 * Action to be executed after a given process has been started. This is typically used to configure
 * the server process, e.g. to deploy services.
 * 
 * @author Andreas Veithen
 */
public interface ProcessStartAction {
    ProcessStartAction NOP = new ProcessStartAction() {
        public void execute(Logger logger, Process process) throws Exception {
        }
    };
    
    void execute(Logger logger, Process process) throws Exception;
}
