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

import javax.xml.namespace.QName;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * Test deserialization of SOAP responses
 */
public class TestBeanDeser2 extends TestCase {

    private String header;
    private String footer;
    private AxisServer server = new AxisServer();

    public TestBeanDeser2(String name) {
        this(name, Constants.URI_DEFAULT_SCHEMA_XSI,
                Constants.URI_DEFAULT_SCHEMA_XSD);
    }

    public TestBeanDeser2(String name, String NS_XSI, String NS_XSD) {
        super(name);
        header =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<SOAP-ENV:Envelope SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"\n" +
            "                   xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
            "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
            "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "                   xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
            "<SOAP-ENV:Body>\n";
        footer =
            "</SOAP-ENV:Body>\n"+
            "</SOAP-ENV:Envelope>\n";

        TypeMappingRegistry tmr = server.getTypeMappingRegistry();
        TypeMapping tm = (TypeMapping) tmr.createTypeMapping();
        tm.setSupportedEncodings(new String[]{Constants.URI_DEFAULT_SOAP_ENC});
        tmr.register(Constants.URI_DEFAULT_SOAP_ENC, tm);
        tm.register(test.encoding.beans.SbTravelRequest.class,
                new QName("http://www.sidestep.com/sbws", "SbTravelRequest"),
                new org.apache.axis.encoding.ser.BeanSerializerFactory(
                        test.encoding.beans.SbTravelRequest.class,
                        new QName("http://www.sidestep.com/sbws", "SbTravelRequest")),
                new org.apache.axis.encoding.ser.BeanDeserializerFactory(
                        test.encoding.beans.SbTravelRequest.class,
                        new QName("http://www.sidestep.com/sbws", "SbTravelRequest")));
        tm.register(test.encoding.beans.SbSupplier.class,
                new QName("http://www.sidestep.com/sbws", "SbSupplier"),
                new org.apache.axis.encoding.ser.BeanSerializerFactory(
                        test.encoding.beans.SbSupplier.class,
                        new QName("http://www.sidestep.com/sbws", "SbSupplier")),
                new org.apache.axis.encoding.ser.BeanDeserializerFactory(
                        test.encoding.beans.SbSupplier.class,
                        new QName("http://www.sidestep.com/sbws", "SbSupplier")));
    }

    protected Object deserialize(String data)
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

        return param.getValue();
    }

    public void testTravelRequest() throws Exception {
        String response =
             "<startSearch>\n"+
             " <arg0 href=\"#id0\"/>\n"+
             "</startSearch>\n"+
             "<multiRef id=\"id0\" SOAP-ENC:root=\"0\"\n"+
             "         encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"\n"+
             "         xsi:type=\"ns2:SbTravelRequest\"\n"+
             "         xmlns:ns1=\"http://schemas.xmlsoap.org/soap/envelope/:encodingStyle\"\n"+
             "         xmlns:ns2=\"http://www.sidestep.com/sbws\">\n"+
             "<requestOr xsi:type=\"xsd:string\">SOAP test 1</requestOr>\n"+
             "<homeCountry xsi:type=\"xsd:string\">US</homeCountry>\n"+
             "<departureLocation xsi:type=\"xsd:string\">SJC</departureLocation>\n"+
             "<destinationLocation xsi:type=\"xsd:string\">ATL</destinationLocation>\n"+
             "<startDate xsi:type=\"xsd:dateTime\">2002-08-10T13:42:24.024Z</startDate>\n"+
             "<endDate xsi:type=\"xsd:dateTime\">2002-06-27T13:42:24.024Z</endDate>\n"+
             "<searchTypes xsi:type=\"xsd:string\">AIR:RTACR</searchTypes>\n"+
             "<searchParams xsi:nil=\"true\"/>\n"+
             "<searchHints xsi:nil=\"true\"/>\n"+
             "<supPliers xsi:type=\"SOAP-ENC:Array\" SOAP-ENC:arrayType=\"ns2:SbSupplier[1]\">\n"+
             " <item href=\"#id1\"/>\n"+
             "</supPliers>\n"+
             "</multiRef>"+
             "<multiRef id=\"id1\" SOAP-ENC:root=\"0\""+
             "                   encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\""+
             "                   xsi:type=\"ns3:SbSupplier\""+
             "                   xmlns:ns3=\"http://www.sidestep.com/sbws\">"+
             " <searchType xsi:type=\"xsd:int\">0</searchType>"+
             " <supplierCode xsi:type=\"xsd:string\">SC**</supplierCode>"+
             " <chanNel xsi:type=\"xsd:string\">CN**</chanNel>"+
             "</multiRef>";

        test.encoding.beans.SbTravelRequest travelRequest = (test.encoding.beans.SbTravelRequest) deserialize(response);
        assertTrue(travelRequest.supPliers.length==1);
        assertTrue(travelRequest.supPliers[0].searchType.intValue()==0);
        assertTrue(travelRequest.supPliers[0].supplierCode.equals("SC**"));
        assertTrue(travelRequest.supPliers[0].chanNel.equals("CN**"));
    }

    public static void main(String [] args) throws Exception
    {
        TestBeanDeser2 tester = new TestBeanDeser2("test");
        tester.testTravelRequest();
    }
}
