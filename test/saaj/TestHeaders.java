package test.saaj;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayOutputStream;

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
}
