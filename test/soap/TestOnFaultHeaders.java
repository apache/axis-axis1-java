/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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

package test.soap;

import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.providers.java.RPCProvider;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.server.AxisServer;
import org.apache.axis.transport.local.LocalTransport;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.SimpleChain;
import junit.framework.TestCase;

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
