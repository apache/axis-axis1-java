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

package test.concurrency;

import junit.framework.TestCase;
import org.apache.axis.configuration.BasicServerConfig;
import org.apache.axis.server.AxisServer;
import org.apache.axis.providers.java.RPCProvider;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.transport.local.LocalTransport;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
                        String respStr = msgContext.getResponseMessage().getSOAPPart().getAsString();

                        String reqStr = msgContext.getRequestMessage().getSOAPPart().getAsString();
                        log.fatal("Got null response! Request message:\r\n" + reqStr + "\r\n\r\n" +
                                  "Response message:\r\n" + respStr);
                    }
                    if (!ret.equals(TestService.MESSAGE)) {
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
