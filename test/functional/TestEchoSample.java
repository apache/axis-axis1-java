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
import samples.echo.TestClient;

/** Test the stock sample code.
 */
public class TestEchoSample extends TestCase {
    static Log log =
            LogFactory.getLog(TestEchoSample.class.getName());

    public TestEchoSample(String name) {
        super(name);
    }

    public static void main(String args[]) throws Exception {
        TestEchoSample tester = new TestEchoSample("tester");
        tester.testEchoService();
    }

    public void testEchoService () throws Exception {
        log.info("Testing echo interop sample.");

        // deploy the echo service
        String[] args = {"-l",
                         "local:///AdminService",
                         "samples/echo/deploy.wsdd"};
        AdminClient.main(args);

        // define the tests using JUnit assert facilities, and tell client to
        // throw any exceptions it catches.
        TestClient client = new TestClient(true) {
            public void verify(String method, Object sent, Object gotBack) {
                assertTrue("What was sent was not received--" + method + ": " + gotBack, equals(sent, gotBack));
            }
        };

        // run the tests using a local (in process) server
        client.setURL("local:");
        client.executeAll();

        log.info("Test complete.");
    }
}

