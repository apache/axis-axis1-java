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
import samples.message.TestMsg;


/** Test the message sample code.
 */
public class TestMessageSample extends TestCase {
    static Log log =
            LogFactory.getLog(TestMessageSample.class.getName());

    public TestMessageSample(String name) {
        super(name);
    }
    
    public void doTestDeploy() throws Exception {
        String[] args = { "samples/message/deploy.wsdd" };
        AdminClient.main(args);
    }
    
    public void doTestMessage() throws Exception {
        String[] args = { };
        String res = (new TestMsg()).doit(args);
        String expected="Res elem[0]=<ns1:e1 xmlns:ns1=\"urn:foo\">Hello</ns1:e1>" 
                        +"Res elem[1]=<ns2:e1 xmlns:ns2=\"urn:foo\">World</ns2:e1>"
                        +"Res elem[2]=<ns3:e3 xmlns:ns3=\"urn:foo\">"
                        +"<![CDATA["
                        +"Text with\n\tImportant  <b>  whitespace </b> and tags! "
                        +"]]>"
                        +"</ns3:e3>";
        assertEquals("test result elements", expected, res);
    }
    
    public void doTestUndeploy () throws Exception {
        String[] args = { "samples/message/undeploy.wsdd" };
        AdminClient.main(args);
    }

    public static void main(String args[]) throws Exception {
        TestMessageSample tester = new TestMessageSample("tester");
        tester.testMessageService();
    }

    public void testMessageService () throws Exception {
        try {
            log.info("Testing message sample.");
            log.info("Testing deployment...");
            doTestDeploy();
            log.info("Testing service...");
            doTestMessage();
            doTestUndeploy();
            log.info("Test complete.");
        }
        catch( Exception e ) {
            e.printStackTrace();
            throw new Exception("Fault returned from test: "+e);
        }
    }
    
}


