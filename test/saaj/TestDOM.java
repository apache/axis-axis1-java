package test.saaj;

import org.apache.axis.utils.XMLUtils;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.RPCElement;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
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
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPBodyElement;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;

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
    
    public void testRPCParams() throws Exception {
        SOAPMessage message = MessageFactory.newInstance().createMessage();
        RPCParam arg1 = new RPCParam("urn:myNamespace", "testParam1",
                "this is a string #1");
        RPCParam arg2 = new RPCParam("urn:myNamespace", "testParam2",
                "this is a string #2");
        RPCElement body = new RPCElement("urn:myNamespace", "method1",
                new Object[]{arg1, arg2});
        SOAPPart sp = message.getSOAPPart();
        SOAPEnvelope se = sp.getEnvelope();
        SOAPBody sb = se.getBody();
        sb.addChildElement(body);
        Iterator it = sb.getChildElements();
        assertTrue(it.hasNext());

        SOAPElement elem = (SOAPElement) it.next();

        Name name2 = se.createName("testParam1", "", "urn:myNamespace");
        Iterator it2 = elem.getChildElements(name2);
        assertTrue(it2.hasNext());
        while (it2.hasNext()) {
            SOAPElement elem2 = (SOAPElement) it2.next();
            System.out.println("child = " + elem2);
        }

        Name name3 = se.createName("testParam2", "", "urn:myNamespace");
        Iterator it3 = elem.getChildElements(name3);
        assertTrue(it3.hasNext());
        while (it3.hasNext()) {
            SOAPElement elem3 = (SOAPElement) it3.next();
            System.out.println("child = " + elem3);
        }
    }
    
    public void testAddDocument() throws Exception {
        String xml = "<bank:getBalance xmlns:bank=\"http://myservice.test.com/banking/\">\n" +
                     "    <gb:getBalanceReq xmlns:gb=\"http://myservice.test.com/banking/getBalance\">\n" +
                     "        <bt:account acctType=\"domestic\" customerId=\"654321\" xmlns:bt=\"http://myservice.test.com/banking/bankTypes\">\n" +
                     "            <bt:accountNumber>1234567890</bt:accountNumber>\n" +
                     "            <bt:currency>USD</bt:currency>\n" +
                     "        </bt:account>\n" +
                     "    </gb:getBalanceReq>\n" +
                     "</bank:getBalance>";
        Document document = XMLUtils.newDocument(new ByteArrayInputStream(xml.getBytes()));
        
        MessageFactory factory = new org.apache.axis.soap.MessageFactoryImpl();
        SOAPMessage msg = factory.createMessage();
        msg.getSOAPBody();
        SOAPBody body = msg.getSOAPBody();
        
        SOAPBodyElement soapBodyElt = body.addDocument(document);
        assertXMLEqual(xml, soapBodyElt.toString());
    }

    private String messageToString(SOAPMessage message) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        message.writeTo(baos);
        return new String(baos.toByteArray());
    }

}
