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
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import java.io.ByteArrayInputStream;
import java.util.Iterator;

/**
 * @author john.gregg@techarch.com
 * @author $Author$
 * @version $Revision$
 */
public class TestSOAPBody extends TestCase {

    /**
     * Method suite
     *
     * @return
     */
    public static Test suite() {
        return new TestSuite(test.message.TestSOAPBody.class);
    }

    /**
     * Method main
     *
     * @param argv
     */
    public static void main(String[] argv) throws Exception {
        TestSOAPBody tester = new TestSOAPBody("TestSOAPBody");
        tester.testSoapBodyBUG();
    }

    /**
     * Constructor TestSOAPBody
     *
     * @param name
     */
    public TestSOAPBody(String name) {
        super(name);
    }

    String xmlString =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
            "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
            "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            " <soapenv:Header>\n" +
            "  <shw:Hello xmlns:shw=\"http://www.jcommerce.net/soap/ns/SOAPHelloWorld\">\n" +
            "    <shw:Myname>Tony</shw:Myname>\n" +
            "  </shw:Hello>\n" +
            " </soapenv:Header>\n" +
            " <soapenv:Body>\n" +
            "  <shw:Address xmlns:shw=\"http://www.jcommerce.net/soap/ns/SOAPHelloWorld\">\n" +
            "    <shw:City>GENT</shw:City>\n" +
            "  </shw:Address>\n" +
            " </soapenv:Body>\n" +
            "</soapenv:Envelope>";

    /**
     * Method testSoapBodyBUG
     *
     * @throws Exception
     */
    public void testSoapBodyBUG() throws Exception {
        MimeHeaders mimeheaders = new MimeHeaders();

        mimeheaders.addHeader("Content-Type", "text/xml");
        ByteArrayInputStream instream = new ByteArrayInputStream(xmlString.getBytes());
        MessageFactory factory =
                MessageFactory.newInstance();
        SOAPMessage msg =
                factory.createMessage(mimeheaders, instream);
        org.apache.axis.client.AxisClient axisengine =
                new org.apache.axis.client.AxisClient();

        // need to set it not null , if not nullpointer in sp.getEnvelope()
        ((org.apache.axis.Message) msg).setMessageContext(
                new org.apache.axis.MessageContext(axisengine));
        SOAPPart sp = msg.getSOAPPart();
        javax.xml.soap.SOAPEnvelope se = sp.getEnvelope();
        javax.xml.soap.SOAPHeader sh = se.getHeader();
        SOAPBody sb = se.getBody();
        Iterator it = sb.getChildElements();
        int count = 0;

        while (it.hasNext()) {
            SOAPBodyElement el = (SOAPBodyElement) it.next();
            count++;
            Name name = el.getElementName();
            System.out.println("Element:" + el);
            System.out.println("BODY ELEMENT NAME:" + name.getPrefix() + ":"
                    + name.getLocalName() + " " + name.getURI());
        }
        assertTrue(count == 1);
    }
}
