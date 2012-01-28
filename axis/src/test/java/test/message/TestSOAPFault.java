/*
 * Copyright 2002-2004 The Apache Software Foundation.
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
package test.message;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import java.io.InputStream;

/**
 * @author steve.johnson@riskmetrics.com (Steve Johnson)
 * @author Davanum Srinivas (dims@yahoo.com)
 * @version $Revision$
 */
public class TestSOAPFault extends TestCase {

    /**
     * Method suite
     * 
     * @return 
     */
    public static Test suite() {
        return new TestSuite(TestSOAPFault.class);
    }

    /**
     * Method main
     * 
     * @param argv 
     */
    public static void main(String[] argv) throws Exception {
        TestSOAPFault tester = new TestSOAPFault("TestSOAPFault");
        tester.testAxis1008();
    }

    /**
     * Constructor TestSOAPFault
     * 
     * @param name 
     */
    public TestSOAPFault(String name) {
        super(name);
    }

    /**
     * Regression test for AXIS-1008.
     * 
     * @throws Exception 
     */
    public void testAxis1008() throws Exception {
        InputStream in = TestSOAPFault.class.getResourceAsStream("AXIS-1008.xml");
        try {
            MessageFactory msgFactory = MessageFactory.newInstance();
            SOAPMessage msg = msgFactory.createMessage(null, in);
    			
            //now attempt to access the fault
            if (msg.getSOAPPart().getEnvelope().getBody().hasFault()) {
                SOAPFault fault =
                        msg.getSOAPPart().getEnvelope().getBody().getFault();
                System.out.println("Fault: " + fault.getFaultString());
            }
        } finally {
            in.close();
        }
    }
}    