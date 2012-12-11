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
package org.apache.axis.server.standalone.daemon;

import org.apache.axis.server.standalone.StandaloneAxisServer;
import org.apache.axis.server.standalone.cli.Configurator;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;

/**
 * {@link Daemon} implementation that runs the stand-alone Axis server.
 * 
 * @author Andreas Veithen
 */
public class AxisServerDaemon implements Daemon {
    private StandaloneAxisServer server;
    
    public void init(DaemonContext context) throws DaemonInitException, Exception {
        server = new StandaloneAxisServer();
        Configurator.INSTANCE.configure(server, context.getArguments());
        server.init();
    }

    public void start() throws Exception {
        server.start();
    }

    public void stop() throws Exception {
        server.stop();
    }

    public void destroy() {
        server = null;
    }
}
