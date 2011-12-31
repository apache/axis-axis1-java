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
import java.io.ByteArrayInputStream;

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
        tester.testSoapFaultBUG();
    }

    /**
     * Constructor TestSOAPFault
     * 
     * @param name 
     */
    public TestSOAPFault(String name) {
        super(name);
    }

    String xmlString =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<soapenv:Body>" +
            "<soapenv:Fault>" +
            "<faultcode>soapenv:13001</faultcode>" +
            "<faultstring>java.lang.Exception: File already exists</faultstring>" +
            "<faultactor>urn:RiskMetricsDirect:1.0:object-service-service:CreateObject</faultactor>" +
            "<detail/>" +
            "</soapenv:Fault>" +
            "</soapenv:Body>" +
            "</soapenv:Envelope>";

    /**
     * Method testSoapFaultBUG
     * 
     * @throws Exception 
     */
    public void testSoapFaultBUG() throws Exception {
        ByteArrayInputStream bis = new ByteArrayInputStream(xmlString.getBytes());
        MessageFactory msgFactory = MessageFactory.newInstance();
        SOAPMessage msg = msgFactory.createMessage(null, bis);
			
        //now attempt to access the fault
        if (msg.getSOAPPart().getEnvelope().getBody().hasFault()) {
            SOAPFault fault =
                    msg.getSOAPPart().getEnvelope().getBody().getFault();
            System.out.println("Fault: " + fault.getFaultString());
        }
    }
}    