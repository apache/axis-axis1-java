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
import org.apache.axis.AxisFault;
import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;
import samples.jaxm.DelayedStockQuote;
import samples.jaxm.SOAPFaultTest;
import samples.jaxm.UddiPing;

import javax.xml.messaging.URLEndpoint;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import java.net.SocketException;


/**
 * Test the JAX-RPC compliance samples.
 */
public class TestJAXMSamples extends TestCase {
    static Log log = LogFactory.getLog(TestJAXMSamples.class.getName());

    public TestJAXMSamples(String name) {
        super(name);
    } // ctor

    public void testSOAPFaultTest () throws Exception {
        try {
            SOAPFaultTest.main(new String[0]);
        } catch (Throwable t) {
            t.printStackTrace();
            throw new Exception("Fault returned from test: " + t);
        }
    }

//    // This is timing out for some reason - removed for the nonce.
//    // -- gdaniels, 4/21/2003
//    public void testUddiPing() throws Exception {
//        try {
//            log.info("Testing JAXM UddiPing sample.");
//            UddiPing.searchUDDI("IBM", "http://www-3.ibm.com/services/uddi/testregistry/inquiryapi");
//            log.info("Test complete.");
//        } catch (javax.xml.soap.SOAPException e) {
//            Throwable t = e.getCause();
//            if (t != null) {
//                t.printStackTrace();
//                if (t instanceof AxisFault) {
//                    AxisFault af = (AxisFault) t;
//                    if ((af.detail instanceof SocketException) ||
//                        (af.getFaultCode().getLocalPart().equals("HTTP")) ) {
//                        System.out.println("Connect failure caused JAXM UddiPing to be skipped.");
//                        return;
//                    }
//                }
//                throw new Exception("Fault returned from test: " + t);
//            } else {
//                e.printStackTrace();
//                throw new Exception("Exception returned from test: " + e);
//            }
//        } catch (Throwable t) {
//            t.printStackTrace();
//            throw new Exception("Fault returned from test: " + t);
//        }
//    } // testGetQuote

    public void testDelayedStockQuote() throws Exception {
        try {
            log.info("Testing JAXM DelayedStockQuote sample.");
            DelayedStockQuote stockQuote = new DelayedStockQuote();
            System.out.print("The last price for SUNW is " + stockQuote.getStockQuote("SUNW"));
            log.info("Test complete.");
        } catch (javax.xml.soap.SOAPException e) {
            Throwable t = e.getCause();
            if (t != null) {
                t.printStackTrace();
                if (t instanceof AxisFault) {
                    if (((AxisFault) t).detail instanceof SocketException) {
                        System.out.println("Connect failure caused JAXM DelayedStockQuote to be skipped.");
                        return;
                    }
                }
                throw new Exception("Fault returned from test: " + t);
            } else {
                e.printStackTrace();
                throw new Exception("Exception returned from test: " + e);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            throw new Exception("Fault returned from test: " + t);
        }
    } // testGetQuote
    
    public void testJWSFault() throws Exception {
        SOAPConnectionFactory scFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection con = scFactory.createConnection();

        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage message = factory.createMessage();

        SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
        SOAPBody body = envelope.getBody();

        Name bodyName = envelope.createName("echo");
        SOAPBodyElement bodyElement = body.addBodyElement(bodyName);

        Name name = envelope.createName("arg0");
        SOAPElement symbol = bodyElement.addChildElement(name);
        symbol.addTextNode("Hello");

        URLEndpoint endpoint = new URLEndpoint("http://localhost:8080/jws/FaultTest.jws");
        SOAPMessage response = con.call(message, endpoint);
        SOAPBody respBody = response.getSOAPPart().getEnvelope().getBody();
        assertTrue(respBody.hasFault());
    }

    public static void main(String args[]) throws Exception {
        TestJAXMSamples tester = new TestJAXMSamples("tester");
        //tester.testUddiPing();
    } // main
}


