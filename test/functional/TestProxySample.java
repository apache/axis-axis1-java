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
import samples.misc.TestClient;


/** Test the proxy sample code.
 */
public class TestProxySample extends TestCase {
    static Log log =
            LogFactory.getLog(TestProxySample.class.getName());

    public TestProxySample(String name) {
        super(name);
    }
    
    public void doTest () throws Exception {
        String[] args = { "-d" };
        TestClient.mainWithService(args, "ProxyService");
    }
    
    // temp for debugging
    public static void main (String[] args) throws Exception {
        new TestProxySample("foo").doTest();
    }
    
    public void testService () throws Exception {
        try {
            log.info("Testing proxy sample.");
            
            log.info("Testing deployment...");
            
            // deploy the proxy service
            String[] args = { "samples/proxy/deploy.wsdd" };
            AdminClient.main(args);
            
            log.info("Testing server-side client deployment...");
            
            // deploy the proxy service
            String[] args2 = { "samples/proxy/client_deploy.xml" };
            AdminClient.main(args2);
            
            log.info("Testing service...");
            doTest();
            log.info("Test complete.");
        }
        catch( Exception e ) {
            e.printStackTrace();
            throw new Exception("Fault returned from test: "+e);
        }
    }
}

