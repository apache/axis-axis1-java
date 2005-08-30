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
import org.apache.axis.transport.http.SimpleAxisWorker;
import org.apache.axis.utils.NetworkUtils;
import org.apache.commons.logging.Log;
import samples.encoding.TestElem;


/** Test the ElementService sample code.
 */
public class TestElementSample extends TestCase {
    static Log log =
            LogFactory.getLog(TestElementSample.class.getName());

    public TestElementSample(String name) {
        super(name);
    }
    
    public void doTestElement () throws Exception {
        String thisHost = NetworkUtils.getLocalHostname();
        String thisPort = System.getProperty("test.functional.ServicePort","8080");

        String[] args = {thisHost,thisPort};
        String   xml = "<x:hello xmlns:x=\"urn:foo\">a string</x:hello>";
        System.out.println("Sending : " + xml );
        String res = TestElem.doit(args, xml);
        System.out.println("Received: " + res );
        assertEquals("TestElementSample.doit(): xml must match", res, xml);
    }
    
    public void doTestDeploy () throws Exception {
        String[] args = { "samples/encoding/deploy.wsdd" };
        AdminClient.main(args);
    }
    
    public void doTestUndeploy () throws Exception {
        String[] args = { "samples/encoding/undeploy.wsdd" };
        AdminClient.main(args);
    }

    public static void main(String args[]) throws Exception {
        TestElementSample tester = new TestElementSample("tester");
        tester.testElementService();
    }

    public void testElementService () throws Exception {
        log.info("Testing element sample.");
        log.info("Testing deployment...");
        doTestDeploy();
        log.info("Testing service...");
        doTestElement();
        log.info("Testing undeployment...");
        doTestUndeploy();
        log.info("Test complete.");
    }
}


