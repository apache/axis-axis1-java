/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
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
    public static void main(String[] argv) {
        boolean swing = false;
        if (argv.length > 0) {
            if ("-swing".equals(argv[0])) {
                swing = true;
            }
        }
        if (swing) {
            junit.swingui.TestRunner.main(new String[]{
                "test.message.TestSOAPHeader"});
        } else {
            System.out.println("use '-swing' for the Swing version.");
            junit.textui.TestRunner.main(new String[]{
                "test.message.TestSOAPHeader"});
        }
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
    /*
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
        //    SOAPElement el = (SOAPElement) obj;
        //    Name name = el.getElementName();
        //
        //    System.out.println("HEADER ELEMENT NAME:" + name.getPrefix() + ":"
        //            + name.getLocalName() + " " + name.getURI());
        }
        assertTrue(count==1);
    }
    */
}
