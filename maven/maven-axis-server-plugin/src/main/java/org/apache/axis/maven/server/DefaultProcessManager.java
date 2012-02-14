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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;

public class DefaultProcessManager implements ProcessManager, LogEnabled {
    private final List managedProcesses = new ArrayList();
    
    private Logger logger;
    
    public void enableLogging(Logger logger) {
        this.logger = logger;
    }

    public void startProcess(String description, String[] cmdline, File workDir, ProcessStartAction startAction, ProcessStopAction stopAction) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Starting process with command line: " + Arrays.asList(cmdline));
        }
        Process process = Runtime.getRuntime().exec(cmdline, null, workDir);
        managedProcesses.add(new ManagedProcess(process, description, stopAction));
        new Thread(new StreamPump(process.getInputStream(), System.out)).start();
        new Thread(new StreamPump(process.getErrorStream(), System.err)).start();
        startAction.execute(logger, process);
    }
    
    public void stopAll() throws Exception {
        Exception savedException = null;
        for (Iterator it = managedProcesses.iterator(); it.hasNext(); ) {
            ManagedProcess server = (ManagedProcess)it.next();
            int result;
            try {
                result = server.getStopAction().execute(logger);
            } catch (Exception ex) {
                if (savedException == null) {
                    savedException = ex;
                }
                result = -1;
            }
            if (result == ProcessStopAction.STOPPING) {
                server.getProcess().waitFor();
            } else {
                server.getProcess().destroy();
            }
            logger.info(server.getDescription() + " stopped");
        }
        // TODO: need to clear the collection because the same ServerManager instance may be used by multiple projects in a reactor build;
        //       note that this means that the plugin is not thread safe (i.e. doesn't support parallel builds in Maven 3)
        managedProcesses.clear();
        if (savedException != null) {
            throw savedException;
        }
    }
}
