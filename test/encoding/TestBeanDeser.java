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
import org.apache.axis.utils.JavaUtils;

import javax.xml.rpc.namespace.QName;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * Test deserialization of SOAP responses
 */
public class TestBeanDeser extends TestCase {

    private String header;
    private String footer;
    private AxisServer server = new AxisServer();

    public TestBeanDeser(String name) {
        this(name, Constants.URI_DEFAULT_SCHEMA_XSI,
                Constants.URI_DEFAULT_SCHEMA_XSD);
    }

    public TestBeanDeser(String name, String NS_XSI, String NS_XSD) {
        super(name);

        header =
            "<?xml version=\"1.0\"?>\n" +
            "<SOAP-ENV:Envelope\n" +
            "xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
            "xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\"\n" +
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
            "xmlns:xsd-cr=\"http://www.w3.org/2000/10/XMLSchema\"\n" +
            "xmlns:xsd-lc=\"http://www.w3.org/1999/XMLSchema\"\n" +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n"+
            "<SOAP-ENV:Body>\n";
        footer =
            "</SOAP-ENV:Body>\n"+
            "</SOAP-ENV:Envelope>\n";

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

    /**
     * Verify that two objects have the same value, handling arrays...
     */
    private static boolean equals(Object obj1, Object obj2) {
        if ((obj1 == null) || (obj2 == null)) return (obj1 == obj2);
        if (obj1.equals(obj2)) return true;
        return false;
    }

    /**
     * Verify that a given XML deserialized produces the expected result
     */
    protected void deserialize(String data, Object expected)
            throws Exception {
        deserialize(data, expected, false);
    }

    protected void deserialize(String data, Object expected, boolean tryConvert)
            throws Exception {
        Message message = new Message(header + data + footer);
        message.setMessageContext(new MessageContext(server));

        SOAPEnvelope envelope = (SOAPEnvelope) message.getSOAPEnvelope();
        assertNotNull("SOAP envelope should not be null", envelope);

        RPCElement body = (RPCElement) envelope.getFirstBody();
        assertNotNull("SOAP body should not be null", body);

        Vector arglist = body.getParams();
        assertNotNull("arglist", arglist);
        assertTrue("param.size()<=0 {Should be > 0}", arglist.size() > 0);

        RPCParam param = (RPCParam) arglist.get(0);
        assertNotNull("SOAP param should not be null", param);

        Object result = param.getValue();
        if (!equals(result, expected)) {
            // Try to convert to the expected class
            if (tryConvert) {
                Object result2 = JavaUtils.convert(result, expected.getClass());
                if (!equals(result2, expected)) {
                    assertEquals("The result is not what is expected.", expected, result);
                }
            } else {
                assertEquals("The result is not what is expected.", expected, result);
            }
        }
    }

    // Struct Return
    public void testReturn() throws Exception {
        test.encoding.RETURN ret = new test.encoding.RETURN();
        ret.setTYPE("000");
        ret.setID("001");
        ret.setNUMBER("002");
        ret.setMESSAGE("003");
        ret.setLOGNO("004");
        ret.setLOGMSGNO("005");
        ret.setMESSAGEV1("006");
        ret.setMESSAGEV2("007");
        ret.setMESSAGEV3("008");
        ret.setMESSAGEV4("009");
        String response =
                "<ser-root:SrvResponse xmlns:ser-root=\"urn:test.encoding\">\n"+
                "  <ser-root:RETURN xsi:type=\"ser-root:RETURN\">\n"+
                "    <TYPE xsi:type=\"xsd:string\">000</TYPE>\n"+
                "    <ID xsi:type=\"xsd:string\">001</ID>\n"+
                "    <NUMBER xsi:type=\"xsd:string\">002</NUMBER>\n"+
                "    <MESSAGE xsi:type=\"xsd:string\">003</MESSAGE>\n"+
                "    <LOG_NO xsi:type=\"xsd:string\">004</LOG_NO>\n"+
                "    <LOG_MSG_NO xsi:type=\"xsd:string\">005</LOG_MSG_NO>\n"+
                "    <MESSAGE_V1 xsi:type=\"xsd:string\">006</MESSAGE_V1>\n"+
                "    <MESSAGE_V2 xsi:type=\"xsd:string\">007</MESSAGE_V2>\n"+
                "    <MESSAGE_V3 xsi:type=\"xsd:string\">008</MESSAGE_V3>\n"+
                "    <MESSAGE_V4 xsi:type=\"xsd:string\">009</MESSAGE_V4>\n"+
                "  </ser-root:RETURN>\n"+
                "</ser-root:SrvResponse>";
        deserialize(response,ret,true);
    }

    /*
    // Struct Return - variation
    public void testReturn2() throws Exception {
        test.encoding.RETURN ret = new test.encoding.RETURN();
        ret.setTYPE("000");
        ret.setID("001");
        ret.setNUMBER("002");
        ret.setMESSAGE("003");
        ret.setLOGNO("004");
        ret.setLOGMSGNO("005");
        ret.setMESSAGEV1("006");
        ret.setMESSAGEV2("007");
        ret.setMESSAGEV3("008");
        ret.setMESSAGEV4("009");
        String response =
                "<SrvResponse xmlns=\"urn:test.encoding\">\n"+
                "  <RETURN>\n"+
                "    <TYPE xsi:type=\"xsd:string\">000</TYPE>\n"+
                "    <ID xsi:type=\"xsd:string\">001</ID>\n"+
                "    <NUMBER xsi:type=\"xsd:string\">002</NUMBER>\n"+
                "    <MESSAGE xsi:type=\"xsd:string\">003</MESSAGE>\n"+
                "    <LOG_NO xsi:type=\"xsd:string\">004</LOG_NO>\n"+
                "    <LOG_MSG_NO xsi:type=\"xsd:string\">005</LOG_MSG_NO>\n"+
                "    <MESSAGE_V1 xsi:type=\"xsd:string\">006</MESSAGE_V1>\n"+
                "    <MESSAGE_V2 xsi:type=\"xsd:string\">007</MESSAGE_V2>\n"+
                "    <MESSAGE_V3 xsi:type=\"xsd:string\">008</MESSAGE_V3>\n"+
                "    <MESSAGE_V4 xsi:type=\"xsd:string\">009</MESSAGE_V4>\n"+
                "  </RETURN>\n"+
                "</SrvResponse>";
        deserialize(response,ret,true);
    }
    */

    public static void main(String [] args) throws Exception
    {
        TestBeanDeser tester = new TestBeanDeser("test");
        tester.testReturn();
    }
}
