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

import junit.framework.TestCase;

import javax.xml.soap.Detail;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import java.io.InputStream;
import java.util.Iterator;

/**
 * @author steve.johnson@riskmetrics.com (Steve Johnson)
 * @author Davanum Srinivas (dims@yahoo.com)
 * @author Andreas Veithen
 * 
 * @version $Revision$
 */
public class TestSOAPFault extends TestCase {
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
    
    /**
     * Regression test for AXIS-2705. The issue occurs when a SOAP fault has a detail element
     * containing text (and not elements). Note that such a SOAP fault violates the SOAP spec, but
     * Axis should nevertheless be able to process it.
     * 
     * @throws Exception
     */
    public void _testAxis2705() throws Exception {
        InputStream in = TestSOAPFault.class.getResourceAsStream("AXIS-2705.xml");
        try {
            MessageFactory msgFactory = MessageFactory.newInstance();
            SOAPMessage msg = msgFactory.createMessage(null, in);
            SOAPBody body = msg.getSOAPPart().getEnvelope().getBody();
            assertTrue(body.hasFault());
            SOAPFault fault = body.getFault();
            Detail detail = fault.getDetail();
            assertNotNull(detail);
            Iterator it = detail.getChildElements();
            assertTrue(it.hasNext());
            SOAPElement detailElement = (SOAPElement)it.next();
            assertNull(detailElement.getNamespaceURI());
            assertEquals("text", detailElement.getLocalName());
        } finally {
            in.close();
        }
    }
}    