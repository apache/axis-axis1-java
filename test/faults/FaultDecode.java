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
package test.faults;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPFault;
import org.apache.axis.server.AxisServer;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.namespace.QName;

/**
 * This class tests Fault deserialization.
 *
 * @author Mark Roder <mroder@wamnet.com>
 * @author Glen Daniels (gdaniels@apache.org)
 */

public class FaultDecode extends TestCase {
    public static final String FAULT_CODE = "Some.FaultCode";
    public static final String FAULT_STRING = "This caused a fault";
    public static final String DETAIL_ENTRY_TEXT =
        "This was a really bad thing";
    
    public FaultDecode(String name) {
        super(name);
    } // ctor
    
    public static Test suite() {
        return new TestSuite(FaultDecode.class);
    }

    public void testFault() throws Exception {
        String messageText = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" soap:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"
            + "<soap:Header>"
            + ""
            + "</soap:Header> "
            + "<soap:Body>  "
            + "     <soap:Fault>"
            + "          <faultcode>" + FAULT_CODE + "</faultcode>"
            + "          <faultstring>" + FAULT_STRING + "</faultstring>"
            + "          <detail><d1>" + DETAIL_ENTRY_TEXT + "</d1></detail>"
            + "     </soap:Fault>"
            + "</soap:Body>"
            + "</soap:Envelope>";

        AxisServer server = new AxisServer();
        Message message = new Message(messageText);
        message.setMessageContext(new MessageContext(server));

        SOAPEnvelope envelope = (SOAPEnvelope) message.getSOAPEnvelope();
        assertNotNull("envelope", envelope);

        SOAPBodyElement respBody = envelope.getFirstBody();
        assertTrue("respBody should be a SOAPFaultElement", respBody
                        instanceof SOAPFault);
        AxisFault aFault = ((SOAPFault) respBody).getFault();

        assertNotNull("Fault should not be null", aFault);
        
        QName faultCode = aFault.getFaultCode();
        assertNotNull("faultCode should not be null", faultCode);
        assertEquals("faultCode should match",
                     faultCode.getLocalPart(), 
                     "Some.FaultCode");
        
        String faultString = aFault.getFaultString();
        assertNotNull("faultString should not be null", faultString);
        assertEquals("faultString should match", faultString,
                     FAULT_STRING);
        
        Element [] details = aFault.getFaultDetails();
        assertNotNull("faultDetails should not be null", details);
        assertEquals("details should have exactly one element", details.length, 
                     1);
        
        Element el = details[0];
        assertEquals("detail entry tag name should match",
                     el.getLocalName(), "d1");
        
        Text text = (Text)el.getFirstChild();
        assertEquals("detail entry string should match",
                     text.getData(), DETAIL_ENTRY_TEXT);
        
    } // testFault
    
    public static void main(String[] args) throws Exception {
        FaultDecode tester = new FaultDecode("test");
        TestRunner runner = new TestRunner();
        runner.doRun(tester.suite(), false);
    }
}
