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

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.providers.java.RPCProvider;
import org.apache.axis.transport.local.LocalTransport;
import org.apache.axis.transport.local.LocalResponder;
import org.apache.axis.client.Call;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.server.AxisServer;

import java.util.Random;

import test.RPCDispatch.Data;

import org.apache.axis.client.Service;

/**
 * A fairly comprehensive test of MustUnderstand/Actor combinations.
 *
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class TestHeaderAttrs extends TestCase {
    static final String PROP_DOUBLEIT = "double_result";
    
    static final String GOOD_HEADER_NS = "http://testMU/";
    static final String GOOD_HEADER_NAME = "doubleIt";
    
    static final String BAD_HEADER_NS = "http://incorrect-ns/";
    static final String BAD_HEADER_NAME = "startThermonuclearWar";
    
    static final String ACTOR = "http://some.actor/";
    
    static SOAPHeaderElement goodHeader = 
                                       new SOAPHeaderElement(GOOD_HEADER_NS, 
                                                             GOOD_HEADER_NAME);
    static SOAPHeaderElement badHeader = 
                                       new SOAPHeaderElement(BAD_HEADER_NS, 
                                                             BAD_HEADER_NAME);

    private SimpleProvider provider = new SimpleProvider();
    private AxisServer engine = new AxisServer(provider);
    private LocalTransport localTransport = new LocalTransport(engine);

    static final String localURL = "local:///testService";

    // Which SOAP version are we using?  Default to SOAP 1.1
    protected SOAPConstants soapVersion = SOAPConstants.SOAP11_CONSTANTS;

    public TestHeaderAttrs(String name) {
        super(name);
    }
    
    /**
     * Prep work.  Make a server, tie a LocalTransport to it, and deploy
     * our little test service therein.
     */ 
    public void setUp() throws Exception {
        engine.init();
        localTransport.setUrl(localURL);
        
        SOAPService service = new SOAPService(new TestHandler(),
                                              new RPCProvider(),
                                              null);
        
        service.setOption("className", TestService.class.getName());
        service.setOption("allowedMethods", "*");
        
        provider.deployService("testService", service);

        SimpleTargetedChain serverTransport =
                new SimpleTargetedChain(null, null, new LocalResponder());
        provider.deployTransport("local", serverTransport);
    }
    
    /**
     * Test an unrecognized header with MustUnderstand="true"
     */ 
    public void testMUBadHeader() throws Exception
    {
        // 1. MU header to unrecognized actor -> should work fine
        badHeader.setActor(ACTOR);
        badHeader.setMustUnderstand(true);
        
        assertTrue("Bad result from test", runTest(badHeader, false));
        
        // 2. MU header to NEXT -> should fail
        badHeader.setActor(Constants.URI_SOAP11_NEXT_ACTOR);
        badHeader.setMustUnderstand(true);
        
        // Test (should produce MU failure)
        try {
            runTest(badHeader, false);
        } catch (Exception e) {
            assertTrue("Non AxisFault Exception : " + e, 
                       e instanceof AxisFault);
            AxisFault fault = (AxisFault)e;
            assertEquals("Bad fault code!", Constants.FAULT_MUSTUNDERSTAND,
                         fault.getFaultCode());
            return;
        }
        
        fail("Should have gotten mustUnderstand fault!");
    }
    
    /**
     * Test an unrecognized header with MustUnderstand="false"
     */ 
    public void testNonMUBadHeader() throws Exception
    {
        badHeader.setActor(Constants.URI_SOAP11_NEXT_ACTOR);
        badHeader.setMustUnderstand(false);

        assertTrue("Non-MU bad header to next actor returned bad result!",
                   runTest(badHeader, false));

        badHeader.setActor(ACTOR);
        
        assertTrue("Non-MU bad header to unrecognized actor returned bad result!", 
                   runTest(badHeader, false));
    }
    
    /**
     * Test a recognized header (make sure it has the desired result)
     */ 
    public void testGoodHeader() throws Exception
    {
        goodHeader.setActor(Constants.URI_SOAP11_NEXT_ACTOR);
        assertTrue("Good header with next actor returned bad result!",
                   runTest(goodHeader, true));
    }
    
    /**
     * Test a recognized header with a particular actor attribute
     */ 
    public void testGoodHeaderWithActors() throws Exception
    {
        // 1. Good header to unrecognized actor -> should be ignored, and
        //    we should get a non-doubled result
        goodHeader.setActor(ACTOR);
        assertTrue("Good header with unrecognized actor returned bad result!",
                   runTest(goodHeader, false));
        
        // Now tell the engine to recognize the ACTOR value
        engine.addActorURI(ACTOR);
        
        // 2. Good header should now be processed and return doubled result
        assertTrue("Good header with recognized actor returned bad result!",
                   runTest(goodHeader, true));
        
        engine.removeActorURI(ACTOR);
    }
    
    /**
     * Call the service with a random string.  Returns true if the result
     * is the length of the string (doubled if the doubled arg is true).
     */ 
    public boolean runTest(SOAPHeaderElement header,
                           boolean doubled) throws Exception
    {
        Call call = new Call(new Service());
        call.setSOAPVersion(soapVersion);
        call.setTransport(localTransport);
        
        call.addHeader(header);
        
        String str = "a";
        int maxChars = new Random().nextInt(50);
        for (int i = 0; i < maxChars; i++) {
            str += "a";
        }

        Integer i = (Integer)call.invoke("countChars", new Object [] { str });
        
        int desiredResult = str.length();
        if (doubled) desiredResult = desiredResult * 2;
        
        return (i.intValue() == desiredResult);
    }
}
