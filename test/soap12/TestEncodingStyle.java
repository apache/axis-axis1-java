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
