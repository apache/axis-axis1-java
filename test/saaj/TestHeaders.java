package test.saaj;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;

public class TestHeaders extends junit.framework.TestCase {

    public TestHeaders(String name) {
        super(name);
    }

    public void testAddingHeaderElements() throws Exception {
        SOAPMessage soapMessage = MessageFactory.newInstance().createMessage();
        SOAPEnvelope soapEnv = soapMessage.getSOAPPart().getEnvelope();
        SOAPHeader header = soapEnv.getHeader();
        header.addChildElement("ebxmlms");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        soapMessage.writeTo(baos);
        String xml = new String(baos.toByteArray());
        assertTrue(xml.indexOf("ebxmlms") != -1);
    }

    
    private final String actor = "ACTOR#1";
    private final String localName = "Local1";
    private final String namespace = "http://ws.apache.org";
    private final String prefix = "P1";
    
    String xmlString =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
            "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
            "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            " <soapenv:Body>\n" +
            "  <shw:Address xmlns:shw=\"http://www.jcommerce.net/soap/ns/SOAPHelloWorld\">\n" +
            "    <shw:City>GENT</shw:City>\n" +
            "  </shw:Address>\n" +
            " </soapenv:Body>\n" +
            "</soapenv:Envelope>";    
    
    public void testAddingHeaderElements2() throws Exception {
        MessageFactory mf = MessageFactory.newInstance();
        SOAPMessage soapMessage = mf.createMessage(new MimeHeaders(), new ByteArrayInputStream(xmlString.getBytes()));
        SOAPEnvelope soapEnv = soapMessage.getSOAPPart().getEnvelope();
        SOAPHeader header = soapEnv.getHeader();

        Name headerName = soapEnv.createName(localName, prefix, prefix);
        SOAPHeaderElement he = header.addHeaderElement(headerName);
        he.setActor(actor);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        soapMessage.writeTo(baos);
        
        String xml = new String(baos.toByteArray());
        assertTrue(xml.indexOf(localName) != -1);
    }
    
    public void testExtractAllHeaders() throws Exception {
        MessageFactory mf = MessageFactory.newInstance();
        SOAPMessage soapMessage = mf.createMessage();
        SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();
        SOAPHeader hdr = envelope.getHeader();
        SOAPHeaderElement she1 = hdr.addHeaderElement(envelope.createName("foo1", "f1", "foo1-URI"));
        she1.setActor("actor-URI");

        Iterator iterator = hdr.extractAllHeaderElements();
        SOAPHeaderElement she = null;
        int cnt = 0;
        while (iterator.hasNext()) {
            cnt++;
            she = (SOAPHeaderElement) iterator.next();
            assertEquals(she, she1);
        }
        assertEquals(1, cnt);
        iterator = hdr.extractAllHeaderElements();
        assertTrue(!iterator.hasNext());
    }

    public void testExamineAllHeaders() throws Exception {
        MessageFactory mf = MessageFactory.newInstance();
        SOAPMessage soapMessage = mf.createMessage();
        SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();
        SOAPHeader hdr = envelope.getHeader();
        SOAPHeaderElement she1 = hdr.addHeaderElement(envelope.createName("foo1", "f1", "foo1-URI"));
        she1.setActor("actor-URI");

        Iterator iterator = hdr.examineAllHeaderElements();
        SOAPHeaderElement she = null;
        int cnt = 0;
        while (iterator.hasNext()) {
            cnt++;
            she = (SOAPHeaderElement) iterator.next();
            assertEquals(she, she1);
        }
        assertEquals(1, cnt);
        iterator = hdr.examineAllHeaderElements();
        assertTrue(iterator.hasNext());
    }
}
