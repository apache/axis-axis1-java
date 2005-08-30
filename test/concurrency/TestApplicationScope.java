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

package test.concurrency;

import junit.framework.TestCase;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.configuration.BasicServerConfig;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.providers.java.RPCProvider;
import org.apache.axis.server.AxisServer;
import org.apache.axis.transport.local.LocalTransport;
import org.apache.commons.logging.Log;


/**
 * Test the "application" scope option - lots of threads call the same service
 * multiple times, and we confirm that only a single instance of the service
 * object was created.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class TestApplicationScope extends TestCase {
    protected static Log log =
        LogFactory.getLog(TestApplicationScope.class.getName());

    private BasicServerConfig config;
    private AxisServer server;
    private String SERVICE_NAME = "TestService";

    public TestApplicationScope(String s) {
        super(s);
    }

    protected void setUp() throws Exception {
        config = new BasicServerConfig();
        server = new AxisServer(config);

        // Deploy a service which contains an option that we expect to be
        // available by asking the MessageContext in the service method (see
        // PropertyHandler.java).

        RPCProvider provider = new RPCProvider();
        SOAPService service = new SOAPService(provider);
        service.setName(SERVICE_NAME);
        service.setOption("className", TestService.class.getName());
        service.setOption("scope", "application");
        service.setOption("allowedMethods", "*");
        config.deployService(SERVICE_NAME, service);
    }

    public class TestRunnable implements Runnable {
        private int reps;

        public TestRunnable(int reps) {
            this.reps = reps;
        }

        public void run() {
            LocalTransport transport = new LocalTransport(server);
            transport.setRemoteService(SERVICE_NAME);
            Call call = new Call(new Service());
            call.setTransport(transport);

            for (int i = 0; i < reps; i++) {
                try {
                    String ret = (String)call.invoke("hello", null);
                    if (ret == null) {
                        MessageContext msgContext = call.getMessageContext();
                        String respStr = msgContext.getResponseMessage().getSOAPPartAsString();

                        String reqStr = msgContext.getRequestMessage().getSOAPPartAsString();
                        String nullStr = "Got null response! Request message:\r\n" + reqStr + "\r\n\r\n" +
                                  "Response message:\r\n" + respStr;
                        log.fatal(nullStr);
                        setError(new Exception(nullStr));
                    } else if (!ret.equals(TestService.MESSAGE)) {
                        setError(new Exception("Messages didn't match (got '" +
                                               ret +
                                               "' wanted '" +
                                               TestService.MESSAGE +
                                               "'!"));
                        return;
                    }
                } catch (AxisFault axisFault) {
                    setError(axisFault);
                    return;
                }
            }
        }
    }

    private Exception error = null;
    synchronized void setError(Exception e) {
        if (error == null) {
            error = e;
        }
    }

    public void testApplicationScope() throws Exception {
        int threads = 50;
        int reps = 10;

        ThreadGroup group = new ThreadGroup("TestThreads");

        for (int i = 0; i < threads; i++) {
            TestRunnable tr = new TestRunnable(reps);
            Thread thread = new Thread(group, tr, "TestThread #" + i);
            thread.start();
        }

        while (group.activeCount() > 0 && error == null) {
            Thread.sleep(100);
        }

        if (error != null) {
            throw error;
        }
    }
}
