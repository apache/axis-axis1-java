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

package test.functional;

import junit.framework.TestCase;

import org.apache.axis.AxisFault;

import org.apache.axis.client.AdminClient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import samples.jaxrpc.GetInfo;
import samples.jaxrpc.GetQuote1;

/**
 * Test the JAX-RPC compliance samples.
 */
public class TestJAXRPCSamples extends TestCase {
    static Log log = LogFactory.getLog(TestJAXRPCSamples.class.getName());

    public TestJAXRPCSamples(String name) {
        super(name);
    } // ctor

    public void doTestDeploy() throws Exception {
        String[] args = {"samples/stock/deploy.wsdd"};
        AdminClient.main(args);
    } // doTestDeploy

    public void doTestGetQuoteXXX() throws Exception {
        String[] args = {"-uuser1", "-wpass1", "XXX"};
        float val = new GetQuote1().getQuote1(args);
        assertEquals("Stock price is not the expected 55.25 +/- 0.01", val,
                55.25, 0.01);
    } // doTestGetQuoteXXX
    
    public void doTestGetQuoteMain() throws Exception {
        String[] args = {"-uuser1", "-wpass1", "XXX"};
        GetQuote1.main(args);
    } // doTestGetQuoteMain

    public void doTestUndeploy() throws Exception {
        String[] args = {"samples/stock/undeploy.wsdd"};
        AdminClient.main(args);
    } // doTestStockNoAction

    public void testGetQuote() throws Exception {
        try {
            log.info("Testing JAX-RPC GetQuote1 sample.");
            log.info("Testing deployment...");
            doTestDeploy();
            log.info("Testing service...");
            doTestGetQuoteXXX();
            doTestGetQuoteMain();
            log.info("Testing undeployment...");
            doTestUndeploy();
            log.info("Test complete.");
        }
        catch (Throwable t) {
            if (t instanceof AxisFault) ((AxisFault)t).dump();
            t.printStackTrace();
            throw new Exception("Fault returned from test: " + t);
        }
    } // testGetQuote

    public void testGetInfo() throws Exception {
        try {
            log.info("Testing JAX-RPC GetInfo sample.");
            log.info("Testing deployment...");
            doTestDeploy();
            log.info("Testing service...");
            String[] args = {"IBM", "symbol"};
            GetInfo.main(args);
            args = new String[] {"ALLR", "name"};
            GetInfo.main(args);
            args = new String[] {"CSCO", "address"};
            GetInfo.main(args);
            log.info("Testing undeployment...");
            doTestUndeploy();
            log.info("Test complete.");
        }
        catch (Throwable t) {
            if (t instanceof AxisFault) ((AxisFault)t).dump();
            t.printStackTrace();
            throw new Exception("Fault returned from test: " + t);
        }
    } // testGetInfo

    public static void main(String args[]) throws Exception {
        TestJAXRPCSamples tester = new TestJAXRPCSamples("tester");
        tester.testGetQuote();
        tester.testGetInfo();
    } // main
}


