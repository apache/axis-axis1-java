/*
 * The Apache Software License, Version 1.1
 *
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
package test.properties;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.providers.java.RPCProvider;
import org.apache.axis.transport.local.LocalTransport;
import org.apache.axis.server.AxisServer;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.configuration.DefaultEngineConfigurationFactory;
import org.apache.axis.configuration.BasicServerConfig;
import org.apache.axis.configuration.BasicClientConfig;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import junit.framework.TestCase;

/**
 * Test scoped properties.  This test confirms that MessageContext.getProperty
 * will correctly defer to a higher-level property scope (the Call on the
 * client side, the SOAPService on the server side) to obtain values for
 * properties that are not explicitly set in the MessageContext itself.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class TestScopedProperties extends TestCase {
    public static final String PROP_NAME = "test.property";
    public static final String CLIENT_VALUE = "client-side property value!";
    public static final String SERVER_VALUE = "this is the server side value";
    public static final String OVERRIDE_NAME = "override.property";
    public static final String OVERRIDE_VALUE = "The REAL value!";

    private SOAPService service;
    private PropertyHandler serverHandler = new PropertyHandler();
    private SimpleProvider config;
    private AxisServer server;

    public TestScopedProperties(String s) {
        super(s);
    }

    /**
     * Sets up the server side for this test.  We deploy a service with
     * a PropertyHandler as a request handler, and PropertyHandler as the
     * backend class as well.  We set an option on the service to a well-
     * known value (PROP_NAME -> PROP_VALUE), and also set an option on
     * the service (OVERRIDE_NAME) which we expect to be overriden by
     * an explicit setting in the MessageContext.
     */
    protected void setUp() throws Exception {
        config = new BasicServerConfig();
        server = new AxisServer(config);

        // Deploy a service which contains an option that we expect to be
        // available by asking the MessageContext in the service method (see
        // PropertyHandler.java).

        RPCProvider provider = new RPCProvider();
        service = new SOAPService(serverHandler, provider, null);
        service.setOption("className", PropertyHandler.class.getName());
        service.setOption("allowedMethods", "*");

        // Here's the interesting property.
        service.setOption(PROP_NAME, SERVER_VALUE);

        // Also set a property which we expect to be overriden by an explicit
        // value in the MessageContext (see PropertyHandler.invoke()).  We
        // should never see this value.
        service.setOption(OVERRIDE_NAME, SERVER_VALUE);

        config.deployService("service", service);
    }

    /**
     * Basic scoped properties test.  Set up a client side service with a
     * PropertyHandler as the request handler, then set a property on the
     * Call which we expect to be available when the request handler queries
     * the MessageContext.  Call the backend service, and make sure the
     * client handler, the server handler, and the result all agree on what
     * the values should be.
     */
    public void testScopedProperties() throws Exception {
        BasicClientConfig config = new BasicClientConfig();
        PropertyHandler clientHandler = new PropertyHandler();
        SOAPService clientService = new SOAPService(clientHandler, null, null);
        config.deployService("service", clientService);

        Service s = new Service(config);
        Call call = new Call(s);

        // Set a property on the Call which we expect to be available via
        // the MessageContext in the client-side handler.
        call.setScopedProperty(PROP_NAME, CLIENT_VALUE);

        LocalTransport transport = new LocalTransport(server);
        transport.setRemoteService("service");
        call.setTransport(transport);

        // Make the call.
        String result = (String)call.invoke("service",
                                            "testScopedProperty",
                                            new Object [] { });

        assertEquals("Returned scoped property wasn't correct",
                     SERVER_VALUE,
                     result);

        // Confirm that both the client and server side properties were
        // correctly read.
        assertEquals("Client-side scoped property wasn't correct",
                     CLIENT_VALUE,
                     clientHandler.getPropVal());
        assertEquals("Server-side scoped property wasn't correct",
                     SERVER_VALUE,
                     serverHandler.getPropVal());
    }

    /**
     * Test overriding a property that's set in the service with an explicit
     * setting in the MessageContext.  The server-side handler will set the
     * OVERRIDDE_NAME property, and the "testOverrideProperty" method will
     * return the value it sees, which should match.
     */
    public void testMessageContextOverride() throws Exception {
        // Only the server side matters on this one, so don't bother with
        // special client config.
        Call call = new Call(new Service());

        LocalTransport transport = new LocalTransport(server);
        transport.setRemoteService("service");
        call.setTransport(transport);

        // Make the call.
        String result = (String)call.invoke("service",
                                            "testOverrideProperty",
                                            new Object [] { });
        assertEquals("Overriden property value didn't match",
                     OVERRIDE_VALUE,
                     result);
    }

    /**
     * Test of three-level client scopes (MC -> service -> Call).
     *
     * Set a property on the Call, then try the invocation.  The client-side
     * handler should see the Call value.  Then set the same property to a
     * different value in the client-side service object, and confirm that
     * when we invoke again we see the new value.
     */
    public void testFullClientScopes() throws Exception {
        Call call = new Call(new Service());
        PropertyHandler clientHandler = new PropertyHandler();
        SOAPService clientService = new SOAPService(clientHandler, null, null);

        call.setSOAPService(clientService);

        // Set a property on the Call which we expect to be available via
        // the MessageContext in the client-side handler.
        call.setScopedProperty(PROP_NAME, CLIENT_VALUE);

        LocalTransport transport = new LocalTransport(server);
        transport.setRemoteService("service");
        call.setTransport(transport);

        // First call should get the value from the Call object.
        call.invoke("testOverrideProperty", new Object [] { });
        assertEquals("Client-side scoped property from Call wasn't correct",
                     CLIENT_VALUE,
                     clientHandler.getPropVal());

        // Now set the same option on the client service, which should
        // take precedence over the value in the Call.
        clientService.setOption(PROP_NAME, OVERRIDE_VALUE);

        // Second call should now get the value from the client service.
        call.invoke("testOverrideProperty", new Object [] { });
        assertEquals("Client-side scoped property from service wasn't correct",
                     OVERRIDE_VALUE,
                     clientHandler.getPropVal());
    }
}
