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
import org.apache.axis.AxisFault;
import org.apache.axis.client.AdminClient;
import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;
import samples.stock.GetQuote;
import samples.stock.GetQuote2;


/** Test the stock sample code.
 */
public class TestStockSample extends TestCase {
    static Log log =
            LogFactory.getLog(TestStockSample.class.getName());

    public TestStockSample(String name) {
        super(name);
    }
    
    public void doTestStockJWS () throws Exception {
        String[] args = { "-uuser1", "-wpass1", "XXX", "-sjws/StockQuoteService.jws" };
        float val = new GetQuote().getQuote(args);
        assertEquals("TestStockSample.doTestStockJWS(): stock price should be 66.25", val, 66.25, 0.01);
        
        // This should FAIL
        args[3] = "-sjws/AltStockQuoteService.jws";
        try {
          val = new GetQuote().getQuote(args);
        } catch (AxisFault e) {
            // Don't print stack trace unless there is an error
          // e.printStackTrace();
          return;
        }
        assertNull("-sjws/AltStockQuoteService.jws did not fail as expected.");
    }
    
    public void doTestDeploy () throws Exception {
        String[] args = { "samples/stock/deploy.wsdd" };
        AdminClient.main(args);
    }
    
    public void doTestStockJava() throws Exception {
        String[] args = { "XXX" };
        float val = new GetQuote2().getQuote(args);
        assertEquals("Stock price is not the expected 55.25 +/- 0.01", val, 55.25, 0.01);
    }
    
    public void doTestStock () throws Exception {
        String[] args = { "-uuser1", "-wpass1", "XXX" };
        float val = new GetQuote().getQuote(args);
        assertEquals("Stock price is not the expected 55.25 +/- 0.01", val, 55.25, 0.01);
    }
    
    public void doTestStockNoAction () throws Exception {
        String[] args = { "-uuser1", "-wpass1", "XXX_noaction" };
        float val = new GetQuote().getQuote(args);
        assertEquals("Stock price is not the expected 55.25 +/- 0.01", val, 55.25, 0.01);
    }
    
    public void doTestUndeploy () throws Exception {
        String[] args = { "samples/stock/undeploy.wsdd" };
        AdminClient.main(args);
    }

    public static void main(String args[]) throws Exception {
        TestStockSample tester = new TestStockSample("tester");
        tester.testStockService();
    }

    public void testStockService () throws Exception {
        try {
            log.info("Testing stock sample.");
            log.info("Testing JWS...");
            doTestStockJWS();
            log.info("Testing Java Binding...");
            doTestStockJava();
            log.info("Testing deployment...");
            doTestDeploy();
            log.info("Testing service...");
            doTestStock();
            log.info("Testing service with SOAPAction: \"\"...");
            doTestStockNoAction();
            log.info("Testing undeployment...");
            doTestUndeploy();
            log.info("Test complete.");
        }
        catch( Exception e ) {
            e.printStackTrace();
            throw new Exception("Fault returned from test: "+e);
        }
    }
    
}


