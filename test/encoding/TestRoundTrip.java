package test.encoding;

import junit.framework.TestCase;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.server.AxisServer;

import javax.xml.rpc.namespace.QName;
import java.util.Vector;

/**
 * Test round-trip serialization/deserialization of SOAP messages
 */
public class TestRoundTrip extends TestCase {
    private AxisServer server = new AxisServer();
    private String header =
            "<?xml version=\"1.0\"?>\n" +
            "<SOAP-ENV:Envelope\n" +
            "xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
            "xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\"\n" +
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
            "xmlns:xsd-cr=\"http://www.w3.org/2000/10/XMLSchema\"\n" +
            "xmlns:xsd-lc=\"http://www.w3.org/1999/XMLSchema\"\n" +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
            "<SOAP-ENV:Body>\n";
    private String footer =
            "</SOAP-ENV:Body>\n" +
            "</SOAP-ENV:Envelope>\n";
    private String response =
            "<ser-root:SrvResponse xmlns:ser-root=\"urn:test.encoding\">\n" +
            "  <ser-root:RETURN xsi:type=\"ser-root:RETURN\">\n" +
            "    <TYPE xsi:type=\"xsd:string\">000</TYPE>\n" +
            "    <ID xsi:type=\"xsd:string\">001</ID>\n" +
            "    <NUMBER xsi:type=\"xsd:string\">002</NUMBER>\n" +
            "    <MESSAGE xsi:type=\"xsd:string\">003</MESSAGE>\n" +
            "    <LOG_NO xsi:type=\"xsd:string\">004</LOG_NO>\n" +
            "    <LOG_MSG_NO xsi:type=\"xsd:string\">005</LOG_MSG_NO>\n" +
            "    <MESSAGE_V1 xsi:type=\"xsd:string\">006</MESSAGE_V1>\n" +
            "    <MESSAGE_V2 xsi:type=\"xsd:string\">007</MESSAGE_V2>\n" +
            "    <MESSAGE_V3 xsi:type=\"xsd:string\">008</MESSAGE_V3>\n" +
            "    <MESSAGE_V4 xsi:type=\"xsd:string\">009</MESSAGE_V4>\n" +
            "  </ser-root:RETURN>\n" +
            "</ser-root:SrvResponse>";

    public TestRoundTrip(String name) {
        this(name, Constants.URI_DEFAULT_SCHEMA_XSI,
                   Constants.URI_DEFAULT_SCHEMA_XSD);
    }

    public TestRoundTrip(String name, String NS_XSI, String NS_XSD) {
        super(name);
        TypeMappingRegistry tmr = server.getTypeMappingRegistry();
        TypeMapping tm = (TypeMapping) tmr.createTypeMapping();
        tm.setSupportedEncodings(new String[]{Constants.URI_DEFAULT_SOAP_ENC});
        tmr.register(Constants.URI_DEFAULT_SOAP_ENC, tm);
        tm.register(test.encoding.RETURN.class,
                new QName("urn:test.encoding", "RETURN"),
                new org.apache.axis.encoding.ser.BeanSerializerFactory(
                        test.encoding.RETURN.class,
                        new QName("urn:test.encoding", "RETURN")),
                new org.apache.axis.encoding.ser.BeanDeserializerFactory(
                        test.encoding.RETURN.class,
                        new QName("urn:test.encoding", "RETURN")));
    }

    // test if objects are equal
    private static boolean equals(Object obj1, Object obj2) {
        if ((obj1 == null) || (obj2 == null)) return (obj1 == obj2);
        if (obj1.equals(obj2)) return true;
        return false;
    }

    // Test RoundTrip
    public void testRoundTrip() throws Exception {
        checkRoundTrip(header + response + footer);
    }

    protected void checkRoundTrip(String xml1) throws Exception {
        Message message = new Message(xml1);
        message.setMessageContext(new MessageContext(server));

        SOAPEnvelope envelope = (SOAPEnvelope) message.getSOAPEnvelope();
        RPCElement body = (RPCElement) envelope.getFirstBody();
        Vector arglist = body.getParams();
        Object ret1 = ((RPCParam) arglist.get(0)).getValue();

        String xml2 = message.getSOAPPartAsString();
        Message message2 = new Message(xml2);
        message2.setMessageContext(new MessageContext(server));

        SOAPEnvelope envelope2 = (SOAPEnvelope) message2.getSOAPEnvelope();
        RPCElement body2 = (RPCElement) envelope2.getFirstBody();
        Vector arglist2 = body2.getParams();
        Object ret2 = ((RPCParam) arglist2.get(0)).getValue();

        if (!equals(ret1, ret2)) {
            assertEquals("The result is not what is expected.", ret1, ret2);
        }
    }

    public static void main(String[] args) throws Exception {
        TestRoundTrip trip = new TestRoundTrip("Test RoundTrip");
        trip.testRoundTrip();
    }
}
