/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
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
 *        Apache Software Foundation (http://www.apache.org/)."
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
package test;

import junit.framework.TestCase;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.configuration.BasicServerConfig;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.enum.Style;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.providers.java.RPCProvider;
import org.apache.axis.server.AxisServer;
import org.apache.axis.transport.local.LocalTransport;
import org.apache.axis.Handler;

/**
 * This is a framework class which handles all the basic stuff necessary
 * to set up a local "roundtrip" test to an AxisServer.
 * 
 * To use it - extend this class with your own test.  Make sure if you
 * override setUp() that you call super.setUp() so that the engine gets
 * initialized correctly.  The method deploy() needs to be called to deploy
 * a target service and set up the transport to talk to it - note that this
 * is done by default in the no-argument setUp().  If you don't want this
 * behavior, or want to tweak names/classes, just call super.setUp(false)
 * instead of super.setUp() and the deploy() call won't happen.  
 * 
 * Then you get a Call object by calling getCall() and you're ready to rock.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class GenericLocalTest extends TestCase {
    protected AxisServer server;
    protected SimpleProvider config;
    protected LocalTransport transport;
    protected SOAPService service = null;

    public GenericLocalTest(String s) {
        super(s);
    }

    /**
     * Default setUp, which automatically deploys the current class
     * as a service named "service".  Override to switch this off.
     * 
     * @throws Exception
     */ 
    protected void setUp() throws Exception {
        setUp(true);
    }

    /**
     * setUp which allows controlling whether or not deploy() is called.
     * 
     * @param deploy indicates whether we should call deploy()
     * @throws Exception
     */ 
    protected void setUp(boolean deploy) throws Exception {
        super.setUp();
        config = new BasicServerConfig();
        server = new AxisServer(config);
        transport = new LocalTransport(server);
        
        if (deploy)
            deploy();
    }

    /**
     * Get an initialized Call, ready to invoke us over the local transport.
     * 
     * @return an initialized Call object.
     */ 
    public Call getCall() {
        Call call = new Call(new Service());
        call.setTransport(transport);
        return call;
    }
    
    /**
     * Convenience method to deploy ourselves as a service
     */ 
    public void deploy() {
        deploy("service", this.getClass(), Style.RPC);
    }

    /**
     * Deploy a service to the local server we've set up, and point the
     * cached local transport object to the desired service name.
     *
     * After calling this method, the "service" field will contain the
     * deployed service, on which you could set other options if
     * desired.
     * 
     * @param serviceName the name under which to deploy the service.
     * @param target class of the service.
     */ 
    public void deploy(String serviceName, Class target, Style style) {
        String className = target.getName();

        service = new SOAPService(new RPCProvider());
        service.setStyle(style);

        service.setOption("className", className);
        service.setOption("allowedMethods", "*");

        config.deployService(serviceName, service);
        transport.setRemoteService(serviceName);
    }

    /**
     * Deploy a service to the local server we've set up, using a
     * Handler we provide as the pivot.
     * 
     * @param serviceName
     * @param handler
     */
    public void deploy(String serviceName, Handler handler) {
        service = new SOAPService(handler);

        config.deployService(serviceName, service);
        transport.setRemoteService(serviceName);
    }
}