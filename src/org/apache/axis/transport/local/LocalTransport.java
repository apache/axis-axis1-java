/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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
    
    private AxisServer server;
    
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
    }
}

