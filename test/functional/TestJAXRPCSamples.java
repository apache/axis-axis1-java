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

package test.functional;

import junit.framework.TestCase;
import org.apache.axis.client.AdminClient;
import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;
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

//    public void testGetQuote() throws Exception {
//        try {
//            log.info("Testing JAX-RPC GetQuote1 sample.");
//            log.info("Testing deployment...");
//            doTestDeploy();
//            log.info("Testing service...");
//            doTestGetQuoteXXX();
//            doTestGetQuoteMain();
//            log.info("Testing undeployment...");
//            doTestUndeploy();
//            log.info("Test complete.");
//        }
//        catch (Throwable t) {
//            t.printStackTrace();
//            throw new Exception("Fault returned from test: " + t);
//        }
//    } // testGetQuote

    public void testGetInfo() throws Exception {
        try {
            log.info("Testing JAX-RPC GetInfo sample.");
            log.info("Testing deployment...");
            doTestDeploy();
            log.info("Testing service...");
            String[] args = {"-uuser3", "-wpass3", "IBM", "symbol"};
            GetInfo.main(args);
            args = new String[] {"-uuser3", "-wpass3", "MACR", "name"};
            GetInfo.main(args);
            args = new String[] {"-uuser3", "-wpass3", "CSCO", "address"};
            GetInfo.main(args);
            log.info("Testing undeployment...");
            doTestUndeploy();
            log.info("Test complete.");
        }
        catch (Throwable t) {
            t.printStackTrace();
            throw new Exception("Fault returned from test: " + t);
        }
    } // testGetInfo

    public void testHello() throws Exception {
        try {
            log.info("Testing JAX-RPC hello sample.");
            samples.jaxrpc.hello.HelloClient.main(new String[]{});
            log.info("Test complete.");
        }
        catch (Throwable t) {
            t.printStackTrace();
            throw new Exception("Fault returned from test: " + t);
        }
    }

    public void testAddress() throws Exception {
        try {
            log.info("Testing JAX-RPC Address sample.");
            samples.jaxrpc.address.AddressClient.main(new String[]{});
            log.info("Test complete.");
        }
        catch (Throwable t) {
            t.printStackTrace();
            throw new Exception("Fault returned from test: " + t);
        }
    }

    public static void main(String args[]) throws Exception {
        TestJAXRPCSamples tester = new TestJAXRPCSamples("tester");
        tester.testHello();
        tester.testAddress();
    } // main
}


