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
import samples.faults.EmployeeClient;



/** Test the faults sample code.
 */
public class TestFaultsSample extends TestCase {
    static Log log =
            LogFactory.getLog(TestFaultsSample.class.getName());

    public TestFaultsSample(String name) {
        super(name);
    }
    
    public void doTestDeploy () throws Exception {
        String[] args = { "samples/faults/deploy.wsdd" };
        AdminClient.main(args);
    }
    
    public void doTest1 () throws Exception {
        String[] args = { "#001" };
        EmployeeClient.main(args);
    }
    
    public void doTest2 () throws Exception {
        String[] args = { "#002" };
        try {
            EmployeeClient.main(args);
        } catch (samples.faults.NoSuchEmployeeFault nsef) {
            return;
        }
        fail("Should not reach here");
    }

    public void testFaultsService () throws Exception {
        try {
            log.info("Testing faults sample.");
            log.info("Testing deployment...");
            doTestDeploy();
            log.info("Testing service...");
            doTest1();
            doTest2();
            log.info("Test complete.");
        }
        catch( Exception e ) {
            e.printStackTrace();
            throw new Exception("Fault returned from test: "+e);
        }
    }

    public static void main(String[] args) throws Exception {
        TestFaultsSample tester = new TestFaultsSample("test");
        tester.testFaultsService();
    }
}

