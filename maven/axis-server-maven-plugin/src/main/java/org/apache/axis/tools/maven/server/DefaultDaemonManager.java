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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;

public class DefaultDaemonManager implements DaemonManager, LogEnabled {
    private final List daemons = new ArrayList();
    
    private Logger logger;
    
    public void enableLogging(Logger logger) {
        this.logger = logger;
    }

    public void startDaemon(String description, String[] cmdline, File workDir, int controlPort) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Starting process with command line: " + Arrays.asList(cmdline));
        }
        Process process = Runtime.getRuntime().exec(cmdline, null, workDir);
        RemoteDaemon daemon = new RemoteDaemon(process, description, controlPort);
        daemons.add(daemon);
        new Thread(new StreamPump(process.getInputStream(), System.out)).start();
        new Thread(new StreamPump(process.getErrorStream(), System.err)).start();
        daemon.startDaemon(logger);
    }
    
    public void stopAll() throws Exception {
        Exception savedException = null;
        for (Iterator it = daemons.iterator(); it.hasNext(); ) {
            RemoteDaemon daemon = (RemoteDaemon)it.next();
            if (logger.isDebugEnabled()) {
                logger.debug("Stopping " + daemon.getDescription());
            }
            boolean success;
            try {
                daemon.stopDaemon(logger);
                success = true;
            } catch (Exception ex) {
                if (savedException == null) {
                    savedException = ex;
                }
                success = false;
            }
            if (logger.isDebugEnabled()) {
                logger.debug("success = " + success);
            }
            if (success) {
                daemon.getProcess().waitFor();
            } else {
                daemon.getProcess().destroy();
            }
            logger.info(daemon.getDescription() + " stopped");
        }
        // TODO: need to clear the collection because the same ServerManager instance may be used by multiple projects in a reactor build;
        //       note that this means that the plugin is not thread safe (i.e. doesn't support parallel builds in Maven 3)
        daemons.clear();
        if (savedException != null) {
            throw savedException;
        }
    }
}
