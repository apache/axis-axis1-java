package test.saaj;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import java.io.ByteArrayOutputStream;

/**
 * Test case for Prefixes
 */ 
public class TestPrefixes extends junit.framework.TestCase {

    public TestPrefixes(String name) {
        super(name);
    }

    /**
     * Test for Bug 18274 - prefix name not set during adding child element
     * @throws Exception
     */ 
    public void testAddingPrefixesForChildElements() throws Exception {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage msg = factory.createMessage();
        SOAPPart sp = msg.getSOAPPart();
        SOAPEnvelope se = sp.getEnvelope();
        SOAPBody sb = se.getBody();
        SOAPElement el1 = sb.addBodyElement(se.createName
                ("element1", "prefix1", "http://www.sun.com"));
        SOAPElement el2 = el1.addChildElement(se.createName
                ("element2", "prefix2", "http://www.apache.org"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        msg.writeTo(baos);
        String xml = new String(baos.toByteArray());
        assertTrue(xml.indexOf("prefix1") != -1);
        assertTrue(xml.indexOf("prefix2") != -1);
        assertTrue(xml.indexOf("http://www.sun.com") != -1);
        assertTrue(xml.indexOf("http://www.apache.org") != -1);
    }
}
