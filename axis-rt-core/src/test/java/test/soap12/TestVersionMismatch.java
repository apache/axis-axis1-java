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

/**
 * @author Andras Avar (andras.avar@nokia.com)
 */

package test.soap12;

import java.lang.reflect.*;
import java.util.*;
import javax.xml.namespace.*;
import junit.framework.*;
import org.apache.axis.*;
import org.apache.axis.encoding.*;
import org.apache.axis.message.*;
import org.apache.axis.server.*;
import org.apache.axis.soap.*;
import org.apache.axis.utils.*;

/**
 * Test VersionMismatch fault generation
 */
public class TestVersionMismatch extends TestCase {
    private AxisServer server = null;


    public TestVersionMismatch(String name) {
        super(name);
        server = new AxisServer();
    }

    private final String SOAP_MESSAGE =
        "<?xml version=\"1.0\"?>\n" +
        "<soap:Envelope " +
          "xmlns:soap=\"http://www.w3.org/2002/wrong-envelope-version\" " +
          "xmlns:soapenc=\"http://www.w3.org/2003/05/soap-encoding\" " +
          "xmlns:this=\"http://encoding.test\" " +
          "xmlns:xsi=\"" + Constants.URI_DEFAULT_SCHEMA_XSI + "\" " +
          "xmlns:xsd=\"" + Constants.URI_DEFAULT_SCHEMA_XSD + "\">\n" +
          "<item xsi:type=\"xsd:string\">abc</item>\n" +
          "<soap:Body>\n" +
            "<methodResult xmlns=\"http://tempuri.org/\">\n" +
            "<hello/>" +
            "</methodResult>\n" +
          "</soap:Body>\n" +
        "</soap:Envelope>\n";


    public void testVersionMismatch() throws Exception {
        Message message = new Message(SOAP_MESSAGE);
        MessageContext context = new MessageContext(server);
        context.setSOAPConstants(SOAPConstants.SOAP12_CONSTANTS);

        message.setMessageContext(context);

        boolean expectedExceptionThrown = false;
        try {
            SOAPEnvelope envelope = message.getSOAPEnvelope();
        } catch (AxisFault af) {
            if (Constants.FAULT_VERSIONMISMATCH.equals(af.getFaultCode()))
                expectedExceptionThrown = true;
        }

        assertTrue(expectedExceptionThrown);

    }
}
