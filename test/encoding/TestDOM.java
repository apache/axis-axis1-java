package test.encoding;



import junit.framework.TestCase;
import org.apache.axis.AxisEngine;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.server.AxisServer;
import org.apache.axis.utils.XMLUtils;

/**

 * Verify that deserialization actually can cause the soap service

 * to be set...

 */

public class TestDOM extends TestCase {

    public TestDOM(String name) {
        super(name);
    }

    private String request =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<SOAP-ENV:Envelope" +
        " SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"" +
        " xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"" +
        " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" +
        " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
        "  <SOAP-ENV:Header>\n" +
        "    <SOAP-SEC:signature SOAP-ENV:actor=\"null\" SOAP-ENV:mustUnderstand=\"1\"" +
        " xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"" +
        " xmlns:SOAP-SEC=\"http://schemas.xmlsoap.org/soap/security/\">\n" +
        "       <Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\">\n" +
        "       </Signature>\n" +
        "    </SOAP-SEC:signature>\n" +
        "  </SOAP-ENV:Header>\n" +
        "  <SOAP-ENV:Body id=\"body\">\n" +
        "    <ns1:getQuote xmlns:ns1=\"urn:xmltoday-delayed-quotes\">\n" +
        "      <symbol xsi:type=\"xsd:string\">IBM</symbol>\n" +
        "    </ns1:getQuote>\n" +
        "  </SOAP-ENV:Body>\n" +
        "</SOAP-ENV:Envelope>";

    public void testDOM() throws Exception {

       // setup
       AxisEngine engine = new AxisServer();
       engine.init();
       MessageContext msgContext = new MessageContext(engine);
       Message message = new Message(request);
       message.setMessageContext(msgContext);

       // Now completely round trip it
       SOAPEnvelope envelope = message.getSOAPPart().getAsSOAPEnvelope();
       // Element dom = message.getAsDOM();
       String result = message.getSOAPPart().getAsString();

       assertEquals("Request is not the same as the result.", request, result);
    }

    public void testEmptyNode() throws Exception
    {
        SOAPBodyElement body = new SOAPBodyElement(XMLUtils.newDocument().createElement("tmp"));
        assertEquals("<tmp/>",body.toString());
    }

    public void testNodeWithAttribute() throws Exception 
    {
        org.w3c.dom.Element element = XMLUtils.newDocument().createElement("tmp");
        element.setAttribute("attrib", "foo");
        SOAPBodyElement body = new SOAPBodyElement(element);
        assertEquals("<tmp attrib=\"foo\"/>",body.toString());
    }

    public static void main(String [] args) throws Exception
    {
        TestDOM tester = new TestDOM("TestDOM");
        tester.testNodeWithAttribute();
        tester.testEmptyNode();
        tester.testDOM();
    }
}

