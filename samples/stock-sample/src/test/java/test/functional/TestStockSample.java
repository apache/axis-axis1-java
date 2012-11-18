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
import samples.stock.GetQuote;
import samples.stock.GetQuote2;

/** Test the stock sample code.
 */
public class TestStockSample extends TestCase {
    public void testStockJWS () throws Exception {
        String[] args = { "-p", System.getProperty("test.functional.ServicePort", "8080"),
                "-uuser1", "-wpass1", "XXX", "-saxis/StockQuoteService.jws" };
        float val = new GetQuote().getQuote(args);
        assertEquals("TestStockSample.doTestStockJWS(): stock price should be 66.25", val, 66.25, 0.01);
    }
    
    public void testStockJWSInvalidURL() throws Exception {
        // This should FAIL
        String[] args = { "-p", System.getProperty("test.functional.ServicePort", "8080"),
                "-uuser1", "-wpass1", "XXX", "-sjws/StockQuoteService.jws" };
        try {
            new GetQuote().getQuote(args);
        } catch (AxisFault e) {
            // Don't print stack trace unless there is an error
          // e.printStackTrace();
          return;
        }
        assertNull("-sjws/AltStockQuoteService.jws did not fail as expected.");
    }
    
    public void testStockJava() throws Exception {
        String[] args = { "XXX" };
        float val = new GetQuote2().getQuote(args);
        assertEquals("Stock price is not the expected 55.25 +/- 0.01", val, 55.25, 0.01);
    }
    
    public void testStock () throws Exception {
        String[] args = { "-p", System.getProperty("test.functional.ServicePort", "8080"),
                "-uuser1", "-wpass1", "XXX" };
        float val = new GetQuote().getQuote(args);
        assertEquals("Stock price is not the expected 55.25 +/- 0.01", val, 55.25, 0.01);
    }
    
    public void testStockNoAction () throws Exception {
        String[] args = { "-p", System.getProperty("test.functional.ServicePort", "8080"),
                "-uuser1", "-wpass1", "XXX_noaction" };
        float val = new GetQuote().getQuote(args);
        assertEquals("Stock price is not the expected 55.25 +/- 0.01", val, 55.25, 0.01);
    }
}


