package test.saaj;

import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Element;
import org.custommonkey.xmlunit.XMLUnit;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import test.AxisTestBase;

public class TestDOM extends AxisTestBase {
    public TestDOM(String name) {
        super(name);
    }

    public void testOwnerDocument() throws Exception {
        final SOAPMessage message = MessageFactory.newInstance().createMessage();
        SOAPPart soapPart = message.getSOAPPart();
        assertNotNull("envelope should have an owner document",
                message.getSOAPPart().getEnvelope().getOwnerDocument());
        assertNotNull("soap part must have a document element",
                soapPart.getDocumentElement());
        assertNotNull(
                "soap part's document element's owner document should not be null",
                soapPart.getDocumentElement().getOwnerDocument());
    }

    private static final String SAMPLE_1 = 
            "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" + "\n" +
                "<SOAP-ENV:Body> " + "\n" +
                    "<m:GetLastTradePrice xmlns:m=\"http://wombat.ztrade.com\">" + "\n" +
                        "<symbol>SUNW</symbol> " + "\n" +
                    "</m:GetLastTradePrice> " + "\n" +
                "</SOAP-ENV:Body> " + "\n" +
            "</SOAP-ENV:Envelope>";

    private SOAPMessage getSOAPMessageFromString(String str) throws Exception {
        MimeHeaders mimeHeaders = new MimeHeaders();
        mimeHeaders.addHeader("content-type", "text/xml");
        SOAPMessage message = MessageFactory.newInstance().createMessage(
                mimeHeaders,
                new ByteArrayInputStream(str.getBytes()));
        SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
        SOAPHeader header = message.getSOAPHeader();
        if (header == null) {
            header = envelope.addHeader();
        }
        return message;
    }
    
    public void testSAAJSerialization() throws Exception {
        SOAPMessage message1 = this.getSOAPMessageFromString(SAMPLE_1);
        SOAPHeader header1 = message1.getSOAPHeader();

        boolean oldIgnore = XMLUnit.getIgnoreWhitespace();
        XMLUnit.setIgnoreWhitespace(true);
        try {
            //this is how header element is added in sun's example
            SOAPFactory soapFactory = SOAPFactory.newInstance();
            Name headerName = soapFactory.createName("Claim",
                    "wsi", "http://ws-i.org/schemas/conformanceClaim/");
            SOAPHeaderElement headerElement =
                    header1.addHeaderElement(headerName);
            headerElement.addAttribute(soapFactory.createName("conformsTo"), "http://ws-i.org/profiles/basic1.0/");
            final String domToString1 = XMLUtils.PrettyDocumentToString(
                    message1.getSOAPPart());
            final String messageToString1 = messageToString(message1);
    
            assertXMLEqual(domToString1, messageToString1);
        } finally {
            XMLUnit.setIgnoreWhitespace(oldIgnore);            
        }
    }

    public void testSAAJSerialization2() throws Exception {
        SOAPMessage message2 = this.getSOAPMessageFromString(SAMPLE_1);
        SOAPHeader header2 = message2.getSOAPHeader();

        boolean oldIgnore = XMLUnit.getIgnoreWhitespace();
        XMLUnit.setIgnoreWhitespace(true);
        try {
            Element header2Element = header2.getOwnerDocument().createElementNS(
                    "http://ws-i.org/schemas/conformanceClaim/", "wsi:Claim");
            header2Element.setAttributeNS(
                    "http://ws-i.org/schemas/conformanceClaim/",
                    "wsi:conformsTo", "http://ws-i.org/profiles/basic1.0/");
            header2.appendChild(header2Element);
            final String domToString2 = XMLUtils.PrettyDocumentToString(
                    message2.getSOAPPart());
            final String messageToString2 = messageToString(message2);

            assertXMLEqual(domToString2, messageToString2);
        } finally {
            XMLUnit.setIgnoreWhitespace(oldIgnore);            
        }
    }

    private String messageToString(SOAPMessage message) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        message.writeTo(baos);
        return new String(baos.toByteArray());
    }

}
