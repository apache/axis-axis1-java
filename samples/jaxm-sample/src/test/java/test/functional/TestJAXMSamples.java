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
import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;
import samples.jaxm.DelayedStockQuote;
import samples.jaxm.UddiPing;

/**
 * Test the JAX-RPC compliance samples.
 */
public class TestJAXMSamples extends TestCase {
    static Log log = LogFactory.getLog(TestJAXMSamples.class.getName());

    public TestJAXMSamples(String name) {
        super(name);
    } // ctor

    public void testUddiPing() throws Exception {
        UddiPing.searchUDDI("Microsoft", "http://localhost:" + System.getProperty("jetty.httpPort") + "/uddi_v1");
    } // testGetQuote

    public void testDelayedStockQuote() throws Exception {
        DelayedStockQuote stockQuote = new DelayedStockQuote("http://localhost:" + System.getProperty("jetty.httpPort") + "/xmethods/delayed-quotes");
        assertEquals("3.67", stockQuote.getStockQuote("SUNW"));
    } // testGetQuote
    
    public static void main(String args[]) throws Exception {
        TestJAXMSamples tester = new TestJAXMSamples("tester");
        //tester.testUddiPing();
    } // main
}


