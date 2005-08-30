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
import org.apache.axis.Constants;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeader;
import org.apache.axis.message.SOAPHeaderElement;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import java.io.ByteArrayInputStream;
import java.util.Iterator;

/**
 * @author john.gregg@techarch.com
 * @author $Author$
 * @version $Revision$
 */
public class TestSOAPHeader extends TestCase {

    /**
     * Method suite
     *
     * @return
     */
    public static Test suite() {
        return new TestSuite(test.message.TestSOAPHeader.class);
    }

    /**
     * Method main
     *
     * @param argv
     */
    public static void main(String[] argv) throws Exception {
        TestSOAPHeader tester = new TestSOAPHeader("TestSOAPHeader");
        tester.setUp();
        tester.testExamineHeaderElements1();
        tester.testSoapHeadersBUG();
    }

    /** Field ACTOR */
    public static final transient String ACTOR = "http://slashdot.org/";

    /** Field HEADER_NAMESPACE */
    public static final transient String HEADER_NAMESPACE =
            "http://xml.apache.org/";

    /** Field env */
    protected SOAPEnvelope env = null;

    /** Field headerElement1 */
    protected SOAPHeaderElement headerElement1 = null;

    /** Field headerElement2 */
    protected SOAPHeaderElement headerElement2 = null;

    /**
     * Constructor TestSOAPHeader
     *
     * @param name
     */
    public TestSOAPHeader(String name) {
        super(name);
    }

    /**
     * Method setUp
     */
    protected void setUp() {
        env = new org.apache.axis.message.SOAPEnvelope();
        headerElement1 = new SOAPHeaderElement(HEADER_NAMESPACE, "SomeHeader1",
                "SomeValue1");
        headerElement1.setActor(ACTOR);
        env.addHeader(headerElement1);
        headerElement2 = new SOAPHeaderElement(HEADER_NAMESPACE, "SomeHeader2",
                "SomeValue2");
        headerElement2.setActor(Constants.URI_SOAP11_NEXT_ACTOR);
        env.addHeader(headerElement2);
    }

    /**
     * Method tearDown
     */
    protected void tearDown() {
    }

    /**
     * Tests the happy path.
     *
     * @throws Exception
     */
    public void testExamineHeaderElements1() throws Exception {
        SOAPHeader header =
                (org.apache.axis.message.SOAPHeader) env.getHeader();
        Iterator iter = header.examineHeaderElements(ACTOR);

        // This would be a lot simpler if getHeadersByActor() were visible.
        SOAPHeaderElement headerElement = null;
        int expectedHeaders = 2;
        int foundHeaders = 0;

        while (iter.hasNext()) {
            headerElement = (SOAPHeaderElement) iter.next();
            if (Constants.URI_SOAP11_NEXT_ACTOR.equals(headerElement.getActor())
                    || ACTOR.equals(headerElement.getActor())) {
                foundHeaders++;
            }
        }
        assertEquals("Didn't find all the right actors.", expectedHeaders,
                foundHeaders);
    }

    /**
     * Tests when the user submits a null actor.
     *
     * @throws Exception
     */
    public void testExamineHeaderElements2() throws Exception {
        SOAPHeader header =
                (org.apache.axis.message.SOAPHeader) env.getHeader();
        Iterator iter = header.examineHeaderElements(null);

        // This would be a lot simpler if getHeadersByActor() were visible.
        SOAPHeaderElement headerElement = null;
        int expectedHeaders = 1;
        int foundHeaders = 0;

        while (iter.hasNext()) {
            headerElement = (SOAPHeaderElement) iter.next();
            if (Constants.URI_SOAP11_NEXT_ACTOR.equals(
                    headerElement.getActor())) {
                foundHeaders++;
            }
        }
        assertEquals("Didn't find all the right actors.", expectedHeaders,
                foundHeaders);
    }

    /**
     * Tests the happy path.
     *
     * @throws Exception
     */
    public void testExtractHeaderElements1() throws Exception {
        SOAPHeader header =
                (org.apache.axis.message.SOAPHeader) env.getHeader();
        Iterator iter = header.extractHeaderElements(ACTOR);

        // This would be a lot simpler if getHeadersByActor() were visible.
        SOAPHeaderElement headerElement = null;
        int expectedHeaders = 2;
        int foundHeaders = 0;

        while (iter.hasNext()) {
            headerElement = (SOAPHeaderElement) iter.next();
            if (Constants.URI_SOAP11_NEXT_ACTOR.equals(headerElement.getActor())
                    || ACTOR.equals(headerElement.getActor())) {
                foundHeaders++;
            }
        }
        assertEquals("Didn't find all the right actors.", expectedHeaders,
                foundHeaders);
    }

    /**
     * Tests when the user submits a null actor.
     *
     * @throws Exception
     */
    public void testExtractHeaderElements2() throws Exception {
        SOAPHeader header =
                (org.apache.axis.message.SOAPHeader) env.getHeader();
        Iterator iter = header.extractHeaderElements(null);

        // This would be a lot simpler if getHeadersByActor() were visible.
        SOAPHeaderElement headerElement = null;
        int expectedHeaders = 1;
        int foundHeaders = 0;

        while (iter.hasNext()) {
            headerElement = (SOAPHeaderElement) iter.next();
            if (Constants.URI_SOAP11_NEXT_ACTOR.equals(
                    headerElement.getActor())) {
                foundHeaders++;
            }
        }
        assertEquals("Didn't find all the right actors.", expectedHeaders,
                foundHeaders);
    }

    String xmlString =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
        "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n"+
        "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"+
        "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"+
        " <soapenv:Header>\n"+
        "  <shw:Hello xmlns:shw=\"http://www.jcommerce.net/soap/ns/SOAPHelloWorld\">\n"+
        "    <shw:Myname>Tony</shw:Myname>\n"+
        "  </shw:Hello>\n"+
        " </soapenv:Header>\n"+
        " <soapenv:Body>\n"+
        "  <shw:Address xmlns:shw=\"http://www.jcommerce.net/soap/ns/SOAPHelloWorld\">\n"+
        "    <shw:City>GENT</shw:City>\n"+
        "  </shw:Address>\n"+
        " </soapenv:Body>\n"+
        "</soapenv:Envelope>";

    /**
     * Method testSoapHeadersBUG
     *
     * @param filename
     *
     * @throws Exception
     */
    public void testSoapHeadersBUG() throws Exception {
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
        Iterator it = sh.getChildElements();
        int count = 0;

        while (it.hasNext()) {
            SOAPElement el = (SOAPElement) it.next();
            count++;
            Name name = el.getElementName();
            System.out.println("Element:" + el);
            System.out.println("HEADER ELEMENT NAME:" + name.getPrefix() + ":"
                    + name.getLocalName() + " " + name.getURI());
        }
        assertTrue(count==1);
    }
}
