/*
 * Copyright 2002-2004 The Apache Software Foundation.
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