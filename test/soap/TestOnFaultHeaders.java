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

package test.soap;

import junit.framework.TestCase;
import org.apache.axis.SimpleChain;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.providers.java.RPCProvider;
import org.apache.axis.server.AxisServer;
import org.apache.axis.transport.local.LocalTransport;

import java.util.Vector;

/**
 * Confirm OnFault() header processing + additions work right.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class TestOnFaultHeaders extends TestCase {
    public static String TRIGGER_NS = "http://trigger-fault";
    public static String TRIGGER_NAME = "faultPlease";
    public static String RESP_NAME = "okHeresYourFault";

    private SimpleProvider provider = new SimpleProvider();
    private AxisServer engine = new AxisServer(provider);
    private LocalTransport localTransport = new LocalTransport(engine);

    static final String localURL = "local:///testService";

    public TestOnFaultHeaders(String s) {
        super(s);
    }

    public void setUp() throws Exception {
        engine.init();
        localTransport.setUrl(localURL);
        SimpleChain chain = new SimpleChain();
        chain.addHandler(new TestFaultHandler());
        chain.addHandler(new TestHandler());
        SOAPService service = new SOAPService(chain,
                                              new RPCProvider(),
                                              null);
        
        service.setOption("className", TestService.class.getName());
        service.setOption("allowedMethods", "*");
        
        provider.deployService("testService", service);
    }
    
    /**
     * Add a header which will trigger a fault in the TestHandler, and
     * therefore trigger the onFault() in the TestFaultHandler.  That should
     * put a header in the outgoing message, which we check for when we get
     * the fault.
     * 
     * @throws Exception
     */ 
    public void testOnFaultHeaders() throws Exception {
        Call call = new Call(new Service());
        call.setTransport(localTransport);
        
        SOAPHeaderElement header = new SOAPHeaderElement(TRIGGER_NS,
                                                         TRIGGER_NAME,
                                                         "do it");
        
        call.addHeader(header);
        
        try {
            call.invoke("countChars", new Object [] { "foo" });
        } catch (Exception e) {            
            SOAPEnvelope env = call.getResponseMessage().getSOAPEnvelope();
            Vector headers = env.getHeaders();
            assertEquals("Wrong # of headers in fault!", 1, headers.size());
            SOAPHeaderElement respHeader = (SOAPHeaderElement)headers.get(0);
            assertEquals("Wrong namespace for header", TRIGGER_NS,
                         respHeader.getNamespaceURI());
            assertEquals("Wrong localName for response header", RESP_NAME,
                         respHeader.getName());
            return;
        }
        
        fail("We should have gotten a fault!");
    }
}
