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
import samples.faults.EmployeeClient;

/** Test the faults sample code.
 */
public class TestFaultsSample extends TestCase {
    static Log log =
            LogFactory.getLog(TestFaultsSample.class.getName());

    public void test1 () throws Exception {
        String[] args = { "-p", System.getProperty("test.functional.ServicePort", "8080"), "#001" };
        EmployeeClient.main(args);
    }
    
    public void test2 () throws Exception {
        String[] args = { "-p", System.getProperty("test.functional.ServicePort", "8080"), "#002" };
        try {
            EmployeeClient.main(args);
        } catch (samples.faults.NoSuchEmployeeFault nsef) {
            return;
        }
        fail("Should not reach here");
    }
}

