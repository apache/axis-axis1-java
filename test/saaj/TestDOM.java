package test.saaj;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

public class TestDOM extends junit.framework.TestCase {
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
}
