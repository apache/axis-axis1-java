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

import junit.framework.TestCase;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.server.AxisServer;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.Messages;

import javax.xml.namespace.QName;

/**
 * Test encodingstyle attribute appearance
 */
public class TestEncodingStyle extends TestCase {
    private AxisServer server = null;

    public TestEncodingStyle(String name) {
        super(name);
        server = new AxisServer();
    }


    private final String ENVELOPE =
        "<?xml version=\"1.0\"?>\n" +
        "<soap:Envelope " +
          "xmlns:soap=\"" + Constants.URI_SOAP12_ENV + "\" " +
          "xmlns:xsi=\"" + Constants.URI_DEFAULT_SCHEMA_XSI + "\" " +
          "xmlns:xsd=\"" + Constants.URI_DEFAULT_SCHEMA_XSD + "\" ";

    private final String HEADER =
          ">\n" +
          "<soap:Header ";

    private final String BODY =
          "/>\n" +
          "<soap:Body ";

    private final String FAULT_HEAD =
            ">\n" +
            "<soap:Fault ";

    private final String FAULT_DETAIL =
            ">\n" +
            "<soap:Code>" +
                "<soap:Value>soap:Sender</soap:Value>" +
              "</soap:Code>" +
              "<soap:Detail ";

    private final String FAULT_TAIL =
              ">\n" +
                "<hello/>" +
             "</soap:Detail>" +
            "</soap:Fault>";

    private final String TAIL =
          "</soap:Body>\n" +
        "</soap:Envelope>\n";

    private final String ENCSTYLE_DEF =
          "soap:encodingStyle=\"" + Constants.URI_SOAP12_ENC + "\"";


    private final String MESSAGE_HEAD =
            ">\n" +
             "<methodResult xmlns=\"http://tempuri.org/\" ";

    private final String MESSAGE =
            ">\n";

    private final String MESSAGE_TAIL =
            "</methodResult>\n";

    private final String ITEM =
           "<item xsi:type=\"xsd:string\">abc</item>\n";

    private final String INVALID_ENCSTYLE = "http://invalidencodingstyle.org";
    private final String NO_ENCSTYLE = Constants.URI_SOAP12_NOENC;

    private final String INVALID_ENCSTYLE_DEF =
          "soap:encodingStyle=\"" + INVALID_ENCSTYLE + "\"";

    private final String NO_ENCSTYLE_DEF =
          "soap:encodingStyle=\"" + NO_ENCSTYLE + "\"";


    public boolean deserialize(String req, QName expected_code, String expected_str) throws Exception {
        Message message = new Message(req);
        MessageContext context = new MessageContext(server);
        context.setSOAPConstants(SOAPConstants.SOAP12_CONSTANTS);
        context.setProperty(Constants.MC_NO_OPERATION_OK, Boolean.TRUE);

        message.setMessageContext(context);

        boolean expectedFault = false;
        try {
            SOAPEnvelope envelope = message.getSOAPEnvelope();
        } catch (AxisFault af) {
            return af.getFaultString().indexOf(expected_str) != -1 &&
                   expected_code.equals(af.getFaultCode());
        }

        return expectedFault;
    }

    public void testEncStyleInEnvelope() throws Exception {
        String req = ENVELOPE + ENCSTYLE_DEF + HEADER + BODY + FAULT_HEAD + FAULT_DETAIL + FAULT_TAIL + TAIL;
        assertTrue(deserialize(req, Constants.FAULT_SOAP12_SENDER,
            Messages.getMessage("noEncodingStyleAttrAppear", "Envelope")));
    }

    public void testEncStyleInHeader() throws Exception {
        String req = ENVELOPE + HEADER + ENCSTYLE_DEF + BODY + FAULT_HEAD + FAULT_DETAIL + FAULT_TAIL + TAIL;
        assertTrue(deserialize(req, Constants.FAULT_SOAP12_SENDER,
            Messages.getMessage("noEncodingStyleAttrAppear", "Header")));
    }

    public void testEncStyleInBody() throws Exception {
        String req = ENVELOPE +  HEADER + BODY + ENCSTYLE_DEF + FAULT_HEAD + FAULT_DETAIL + FAULT_TAIL + TAIL;
        assertTrue(deserialize(req, Constants.FAULT_SOAP12_SENDER,
            Messages.getMessage("noEncodingStyleAttrAppear", "Body")));
    }

    public void testEncStyleInFault() throws Exception {
        String req = ENVELOPE +  HEADER + BODY + FAULT_HEAD + ENCSTYLE_DEF + FAULT_DETAIL + FAULT_TAIL + TAIL;
        assertTrue(deserialize(req, Constants.FAULT_SOAP12_SENDER,
            Messages.getMessage("noEncodingStyleAttrAppear", "Fault")));
    }

    public void testEncStyleInDetail() throws Exception {
        String req = ENVELOPE +  HEADER + BODY + FAULT_HEAD + FAULT_DETAIL + ENCSTYLE_DEF + FAULT_TAIL + TAIL;
        assertTrue(deserialize(req, Constants.FAULT_SOAP12_SENDER,

          Messages.getMessage("noEncodingStyleAttrAppear", "Detail")));
    }

    public void testInvalidEncodingStyle() throws Exception {
        String req = ENVELOPE + HEADER + BODY + MESSAGE_HEAD + INVALID_ENCSTYLE_DEF + MESSAGE + ITEM + MESSAGE_TAIL + TAIL;
        assertTrue(deserialize(req, Constants.FAULT_SOAP12_DATAENCODINGUNKNOWN,
            Messages.getMessage("invalidEncodingStyle")));
    }

    public void testAcceptUserEncodingStyle() throws Exception {
        String req = ENVELOPE + HEADER + BODY + MESSAGE_HEAD + INVALID_ENCSTYLE_DEF + MESSAGE + ITEM + MESSAGE_TAIL + TAIL;

        Message message = new Message(req);
        MessageContext context = new MessageContext(server);
        context.setProperty(Constants.MC_NO_OPERATION_OK, Boolean.TRUE);

        // Set the "invalid" encoding style
        TypeMappingRegistry reg = context.getTypeMappingRegistry();
        TypeMapping tm = (TypeMapping) reg.createTypeMapping();
        tm.setSupportedEncodings(new String[] { INVALID_ENCSTYLE });
        reg.register(INVALID_ENCSTYLE, tm);
        context.setSOAPConstants(SOAPConstants.SOAP12_CONSTANTS);

        message.setMessageContext(context);

        SOAPEnvelope envelope = message.getSOAPEnvelope();
        assertTrue(envelope != null);
   }

    public void testNoEncodingStyle() throws Exception {
        String req = ENVELOPE + HEADER + BODY + MESSAGE_HEAD + NO_ENCSTYLE_DEF + MESSAGE + ITEM + MESSAGE_TAIL + TAIL;
        assertTrue(deserialize(req, null, null) == false);
    }

}
