package test.encoding;

import junit.framework.TestCase;
import org.apache.axis.MessageContext;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCParam;
import org.apache.axis.server.AxisServer;
import org.xml.sax.InputSource;

import javax.xml.soap.SOAPMessage;
import javax.xml.soap.MessageFactory;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

/** 
 * line feed normalization and character reference processing 
 */
public class TestString3 extends TestCase {

    public static final String myNS = "urn:myNS";
    
    public TestString3(String name) {
        super(name);
    }

    static String xml1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<soapenv:Body>" +
            "<ns1:method1 xmlns:ns1=\"urn:myNamespace\">" +
            "<ns1:testParam xsi:type=\"soapenc:string\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\">";

    static String xml2 =
            "</ns1:testParam>" +
            "</ns1:method1>" +
            "</soapenv:Body>" +
            "</soapenv:Envelope>";

    private void runtest(String value, String expected) throws Exception {
        MessageContext msgContext = new MessageContext(new AxisServer());
        String requestEncoding = "UTF-8";
        msgContext.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, requestEncoding);

        String xml = xml1 + value + xml2;
        ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());

        DeserializationContext dser = new DeserializationContext(
            new InputSource(bais), msgContext, org.apache.axis.Message.REQUEST);
        dser.parse();
        
        org.apache.axis.message.SOAPEnvelope env = dser.getEnvelope();
        RPCElement rpcElem = (RPCElement)env.getFirstBody();
        RPCParam output = rpcElem.getParam("testParam");
        assertNotNull("No <testParam> param", output);

        String nodeValue = (String) output.getObjectValue();
        assertNotNull("No node value for testParam param", nodeValue);
        assertEquals(expected, nodeValue);
    }

    public void testEntitizedCRLF() throws Exception {
        runtest("&#xD;&#xA;Hello&#xD;&#xA;World&#xD;&#xA;", "\r\nHello\r\nWorld\r\n");
    }

    public void testPlainCRLF() throws Exception {
        runtest("\r\nHello\r\nWorld\r\n", "\nHello\nWorld\n");
    }

    public void testEntitizedCR() throws Exception {
        runtest("&#xD;Hello&#xD;World&#xD;", "\rHello\rWorld\r");
    }

    public void testPlainCR() throws Exception {
        runtest("\rHello\rWorld\r", "\nHello\nWorld\n");
    }
}
