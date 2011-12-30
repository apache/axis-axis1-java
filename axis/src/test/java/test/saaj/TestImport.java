package test.saaj;

import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import test.AxisTestBase;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import java.io.ByteArrayInputStream;

public class TestImport extends AxisTestBase {

    public TestImport(String name) {
        super(name);
    }

    private static final String SAMPLE_1 =
            "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
            "\n" +
            "<SOAP-ENV:Body> " + "\n" +
            "<m:GetLastTradePrice xmlns:m=\"http://wombat.ztrade.com\">" +
            "\n" +
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
        return message;
    }

    public void testImports() throws Exception {
        //DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        //DocumentBuilder db = dbf.newDocumentBuilder();
        //Document doc1 = db.parse(new ByteArrayInputStream(SAMPLE_1.getBytes()));

        Document doc2 = testImportFromSaajToDom();
        Document body = testImportFromDomToSaaj(doc2);
        XMLUtils.PrettyDocumentToStream(body, System.out);
        //assertXMLEqual(doc1, body);
        //assertXMLEqual(doc2, body);
        //assertXMLEqual(doc1, doc2);
    }

    private Document testImportFromSaajToDom() throws Exception {
        SOAPMessage message = getSOAPMessageFromString(SAMPLE_1);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();
        org.w3c.dom.Node fromNode = message.getSOAPBody().getFirstChild();
        Node n = doc.importNode(fromNode, true);
        doc.appendChild(n);
        return doc;
    }

    private Document testImportFromDomToSaaj(Document doc) throws Exception {
        SOAPMessage sm = MessageFactory.newInstance().createMessage();
        SOAPPart sp = sm.getSOAPPart();
        SOAPBody body = sm.getSOAPBody();
        org.w3c.dom.Node node = sp.importNode(doc.getDocumentElement(), true);
        body.appendChild(node);
        return sp;
    }
}

