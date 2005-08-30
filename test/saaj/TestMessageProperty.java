package test.saaj;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.Text;

public class TestMessageProperty extends junit.framework.TestCase {
    public TestMessageProperty(String name) {
        super(name);
    }

    private static final String textValue = "\uc548\ub155\ud558\uc138\uc694";
    
    private SOAPMessage createTestMessage() throws Exception {
        MessageFactory mf = MessageFactory.newInstance();
        SOAPMessage msg = mf.createMessage();
        SOAPBody sb = msg.getSOAPBody();
        SOAPElement se1 = sb.addChildElement("echoString", "ns1", "http://tempuri.org");
        SOAPElement se2 = se1.addChildElement("string");
        se2.addTextNode(textValue);
        
        return msg;
    }
    
    
    public void testWriteXmlDeclPropertyTrue() throws Exception {
        testXmlDecl("true", "<?xml");        
    }
    
    public void testWriteXmlDeclPropertyFalse() throws Exception {
        testXmlDecl("false", "<soapenv:Envelope");
    }    
    
    public void testEncodingPropertyUTF16() throws Exception {
        testEncoding("UTF-16");               
    }        
        
    public void testEncodingPropertyUTF8() throws Exception {
        testEncoding("UTF-8");                
    }        
    
    private void testXmlDecl(String xmlDecl, String expected) throws Exception {
        SOAPMessage msg = createTestMessage();
        
        msg.setProperty(SOAPMessage.WRITE_XML_DECLARATION, xmlDecl);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        msg.writeTo(baos);
        String msgString = new String(baos.toByteArray(), "UTF-8");
        System.out.println("msgString =" + msgString);
        assertTrue(msgString.startsWith(expected));       
    }
    
    private void testEncoding(String encoding) throws Exception {
        SOAPMessage msg = createTestMessage();
        
        msg.setProperty(SOAPMessage.WRITE_XML_DECLARATION, "true");
        msg.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, encoding);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        msg.writeTo(baos);
        
        String msgString = new String(baos.toByteArray(), encoding);
        System.out.println("msgString (" + encoding + ")=" + msgString);
        assertTrue(msgString.startsWith("<?xml version=\"1.0\" encoding=\"" + encoding + "\""));

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        SOAPMessage msg1 = createMessageFromInputStream(bais);
        SOAPElement se1 = (SOAPElement) msg1.getSOAPBody().getChildElements().next();
        SOAPElement se2 = (SOAPElement) se1.getChildElements().next();
        Text text = (Text)se2.getChildElements().next();
        
	assertEquals(textValue, text.getValue());
    }

    private SOAPMessage createMessageFromInputStream(InputStream is) throws Exception {
        MessageFactory mf = MessageFactory.newInstance();
        return mf.createMessage(new MimeHeaders(), is);
    }
}
