package test.encoding;

import junit.framework.TestCase;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.server.AxisServer;

/**
 * Test deserialization of SOAP responses
 */
public class TestOutputter extends TestCase {

    private String header;
    private String footer;
    private AxisServer server = new AxisServer();

    public TestOutputter(String name) {
        this(name, Constants.NS_URI_CURRENT_SCHEMA_XSI,
                   Constants.NS_URI_CURRENT_SCHEMA_XSD);
    }

    public TestOutputter(String name, String NS_XSI, String NS_XSD) {
        super(name);

        header =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<soap:Envelope " +
              "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
              "xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" " +
              "xmlns:xsi=\"" + NS_XSI + "\" " +
              "xmlns:xsd=\"" + NS_XSD + "\">\n" +
              "<soap:Body>\n" +
                "<methodResult xmlns=\"http://tempuri.org/\">\n";

        footer =
                "</methodResult>\n" +
              "</soap:Body>\n" +
            "</soap:Envelope>";
    }

    /**
     * Verify that a given XML deserialized produces the expected result
     */
    protected void roundtrip(String data)
       throws Exception
    {
       Message message = new Message(header + data + footer);
       message.setMessageContext(new MessageContext(server));

       message.getSOAPEnvelope();

       assertEquals(header+data+footer, message.getSOAPPartAsString());
    }

    public void testString() throws Exception {
        roundtrip("<result xsi:type=\"xsd:string\">abc</result>");
    }

    public void testEscapedText() throws Exception {
        roundtrip("<abc>&lt;&amp;&gt;</abc>");
    }

    public void testEscapedAttributes() throws Exception {
        roundtrip("<abc foo=\"&lt;&amp;&gt;\"/>");
        // roundtrip("<abc foo=\"&lt;&amp;&gt;\"/>");
    }

    public static void main(String [] args) throws Exception
    {
        TestOutputter tester = new TestOutputter("test");
        tester.testString();
    }
}
