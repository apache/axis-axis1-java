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
import samples.transport.FileTest;


/** Test the stock sample code.
 */
public class TestTransportSample extends TestCase {
    static Log log =
            LogFactory.getLog(TestTransportSample.class.getName());

    public TestTransportSample(String name) {
        super(name);
    }
    
    public void doTestDeploy () throws Exception {
        String[] args = { "-llocal:", "samples/transport/deploy.wsdd" };
        AdminClient.main(args);
    }
    
    /* NOT RECOMMENDED -- this calls out to xmltoday.com which is flaky.
       Verify that it either succeeds, or that it produces a specific
       failure. */
    
    public void doTestIBM () throws Exception {
        String[] args = { "IBM" };
        try {
            FileTest.main(args);
        } catch (AxisFault e) {
            String fault = e.getFaultString();
            if (fault == null) throw e;
            if (fault.indexOf("java.net.UnknownHost")<0) {
                int start = fault.indexOf(": ");
                log.info(fault.substring(start+2));
            } else if (fault.equals("timeout")) {
                log.info("timeout");
            } else {
                throw e;
            }
        }
    }
    
    public void doTestXXX () throws Exception {
        String[] args = { "XXX" };
        FileTest.main(args);
    }
    
    public void testService () throws Exception {
        try {
            log.info("Testing transport sample.");
            log.info("Testing deployment...");
            doTestDeploy();
            log.info("Testing service with symbol IBM...");
            doTestIBM();
            log.info("Testing service with symbol XXX...");
            doTestXXX();
            log.info("Test complete.");
        }
        catch( Exception e ) {
            e.printStackTrace();
            throw new Exception("Fault returned from test: "+e);
        }
    }
    
    /**
     * bogus 'main'
     */
    public static void main (String[] args) throws Exception {
        new TestTransportSample("foo").testService();
    }
}

