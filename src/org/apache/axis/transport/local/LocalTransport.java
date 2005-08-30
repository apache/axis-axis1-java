/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.axis.transport.local;

import org.apache.axis.AxisEngine;
import org.apache.axis.MessageContext;
import org.apache.axis.client.Call;
import org.apache.axis.client.Transport;
import org.apache.axis.server.AxisServer;

/**
 * A Transport which will cause an invocation via a "local" AxisServer.
 * 
 * Serialization will still be tested, as the requests and responses
 * pass through a String conversion (see LocalSender.java) - this is
 * primarily for testing and debugging.
 * 
 * This transport will either allow the LocalSender to create its own
 * AxisServer, or if you have one you've configured and wish to use,
 * you may pass it in to the constructor here.
 *
 * @author Rob Jellinghaus (robj@unrealities.com)
 * @author Doug Davis (dug@us.ibm.com)
 * @author Glen Daniels (gdaniels@allaire.com)
 */
public class LocalTransport extends Transport
{
    public static final String LOCAL_SERVER = "LocalTransport.AxisServer";
    public static final String REMOTE_SERVICE = "LocalTransport.RemoteService";

    private AxisServer server;

    /** The name of a particular remote service to invoke.  */
    private String remoteServiceName;

    /** No-arg constructor, which will use an AxisServer constructed
     * by the LocalSender (see LocalSender.java).
     * 
     */
    public LocalTransport()
    {
        transportName = "local";
    }
    
    /** Use this constructor if you have a particular server kicking
     * around (perhaps which you've already deployed useful stuff into)
     * which you'd like to use.
     * 
     * @param server an AxisServer which will bubble down to the LocalSender
     */
    public LocalTransport(AxisServer server)
    {
        transportName = "local";
        this.server = server;
    }

    /**
     * Use this to indicate a particular "remote" service which should be
     * invoked on the target AxisServer.  This can be used programatically
     * in place of a service-specific URL.
     *
     * @param remoteServiceName the name of the remote service to invoke
     */
    public void setRemoteService(String remoteServiceName) {
        this.remoteServiceName = remoteServiceName;
    }

    /**
     * Set up any transport-specific derived properties in the message context.
     * @param context the context to set up
     * @param message the client service instance
     * @param engine the engine containing the registries
     */
    public void setupMessageContextImpl(MessageContext mc,
                                        Call call,
                                        AxisEngine engine)
    {
        if (server != null)
            mc.setProperty(LOCAL_SERVER, server);
        if (remoteServiceName != null)
            mc.setProperty(REMOTE_SERVICE, remoteServiceName);
    }
}

