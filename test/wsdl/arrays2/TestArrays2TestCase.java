package test.wsdl.arrays2;

import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.server.AxisServer;

import test.wsdl.arrays2.data.*;
import test.wsdl.arrays2.data.inner.*;

import java.util.Vector;

public class TestArrays2TestCase extends junit.framework.TestCase {
    public TestArrays2TestCase(String name) {
        super(name);
    }

    private static AxisServer server = new AxisServer();

    private static String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            " <env:Header/>\n" +
            " <env:Body env:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
            "  <m:getDataOperationResponse xmlns:m=\"http://www.xyz.net/webservices/arraytest/1.0\">\n" +
            "   <dataResponse xmlns:n1=\"http://www.xyz.net/schemas/arraytest/data/1.0\" xsi:type=\"n1:dataType\">\n" +
            "    <innerData soapenc:arrayType=\"n2:innerDataType[10]\" xmlns:n2=\"http://www.xyz.net/schemas/arraytest/innerdata/1.0\">\n" +
            "     <innerDataType xsi:type=\"n2:innerDataType\">\n" +
            "      <trDescr xsi:type=\"n2:trDescrType\">\n" +
            "desc#0 HELLO!      </trDescr>\n" +
            "     </innerDataType>\n" +
            "     <innerDataType xsi:type=\"n2:innerDataType\">\n" +
            "      <trDescr xsi:type=\"n2:trDescrType\">\n" +
            "desc#1 HELLO!      </trDescr>\n" +
            "     </innerDataType>\n" +
            "     <innerDataType xsi:type=\"n2:innerDataType\">\n" +
            "      <trDescr xsi:type=\"n2:trDescrType\">\n" +
            "desc#2 HELLO!      </trDescr>\n" +
            "     </innerDataType>\n" +
            "     <innerDataType xsi:type=\"n2:innerDataType\">\n" +
            "      <trDescr xsi:type=\"n2:trDescrType\">\n" +
            "desc#3 HELLO!      </trDescr>\n" +
            "     </innerDataType>\n" +
            "     <innerDataType xsi:type=\"n2:innerDataType\">\n" +
            "      <trDescr xsi:type=\"n2:trDescrType\">\n" +
            "desc#4 HELLO!      </trDescr>\n" +
            "     </innerDataType>\n" +
            "     <innerDataType xsi:type=\"n2:innerDataType\">\n" +
            "      <trDescr xsi:type=\"n2:trDescrType\">\n" +
            "desc#5 HELLO!      </trDescr>\n" +
            "     </innerDataType>\n" +
            "     <innerDataType xsi:type=\"n2:innerDataType\">\n" +
            "      <trDescr xsi:type=\"n2:trDescrType\">\n" +
            "desc#6 HELLO!      </trDescr>\n" +
            "     </innerDataType>\n" +
            "     <innerDataType xsi:type=\"n2:innerDataType\">\n" +
            "      <trDescr xsi:type=\"n2:trDescrType\">\n" +
            "desc#7 HELLO!      </trDescr>\n" +
            "     </innerDataType>\n" +
            "     <innerDataType xsi:type=\"n2:innerDataType\">\n" +
            "      <trDescr xsi:type=\"n2:trDescrType\">\n" +
            "desc#8 HELLO!      </trDescr>\n" +
            "     </innerDataType>\n" +
            "     <innerDataType xsi:type=\"n2:innerDataType\">\n" +
            "      <trDescr xsi:type=\"n2:trDescrType\">\n" +
            "desc#9 HELLO!      </trDescr>\n" +
            "     </innerDataType>\n" +
            "    </innerData>\n" +
            "   </dataResponse>\n" +
            "  </m:getDataOperationResponse>\n" +
            " </env:Body>\n" +
            "</env:Envelope>";

    public void testBug22213() throws Exception {
        TypeMappingRegistry tmr = server.getTypeMappingRegistry();
        TypeMapping tm = (TypeMapping) tmr.createTypeMapping();
        tm.setSupportedEncodings(new String[]{Constants.URI_DEFAULT_SOAP_ENC});
        tmr.register(Constants.URI_DEFAULT_SOAP_ENC, tm);
        tm.register(test.wsdl.arrays2.data.DataType.class,
                new javax.xml.namespace.QName("http://www.xyz.net/schemas/arraytest/data/1.0", "dataType"),
                new org.apache.axis.encoding.ser.BeanSerializerFactory(
                        test.wsdl.arrays2.data.DataType.class,
                        new javax.xml.namespace.QName("http://www.xyz.net/schemas/arraytest/data/1.0", "dataType")),
                new org.apache.axis.encoding.ser.BeanDeserializerFactory(
                        test.wsdl.arrays2.data.DataType.class,
                        new javax.xml.namespace.QName("http://www.xyz.net/schemas/arraytest/data/1.0", "dataType")));
        tm.register(InnerDataType.class,
                new javax.xml.namespace.QName("http://www.xyz.net/schemas/arraytest/innerdata/1.0", "innerDataType"),
                new org.apache.axis.encoding.ser.BeanSerializerFactory(
                        InnerDataType.class,
                        new javax.xml.namespace.QName("http://www.xyz.net/schemas/arraytest/innerdata/1.0", "innerDataType")),
                new org.apache.axis.encoding.ser.BeanDeserializerFactory(
                        InnerDataType.class,
                        new javax.xml.namespace.QName("http://www.xyz.net/schemas/arraytest/innerdata/1.0", "innerDataType")));

        tm.register(TrDescrType.class,
                new javax.xml.namespace.QName("http://www.xyz.net/schemas/arraytest/innerdata/1.0", "trDescrType"),
                new org.apache.axis.encoding.ser.BeanSerializerFactory(
                        TrDescrType.class,
                        new javax.xml.namespace.QName("http://www.xyz.net/schemas/arraytest/innerdata/1.0", "trDescrType")),
                new org.apache.axis.encoding.ser.BeanDeserializerFactory(
                        TrDescrType.class,
                        new javax.xml.namespace.QName("http://www.xyz.net/schemas/arraytest/innerdata/1.0", "trDescrType")));

        tm.register(DataRequestType.class,
                new javax.xml.namespace.QName("http://www.xyz.net/schemas/arraytest/innerdata/1.0", "dataRequestType"),
                new org.apache.axis.encoding.ser.BeanSerializerFactory(
                        DataRequestType.class,
                        new javax.xml.namespace.QName("http://www.xyz.net/schemas/arraytest/innerdata/1.0", "dataRequestType")),
                new org.apache.axis.encoding.ser.BeanDeserializerFactory(
                        DataRequestType.class,
                        new javax.xml.namespace.QName("http://www.xyz.net/schemas/arraytest/innerdata/1.0", "dataRequestType")));

        tm.register(InnerDataType[].class,
                new javax.xml.namespace.QName("http://www.xyz.net/schemas/arraytest/innerdata/1.0", "innerDataArrType"),
                new org.apache.axis.encoding.ser.ArraySerializerFactory(),
                new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

        Message message = new Message(xml);
        message.setMessageContext(new MessageContext(server));

        SOAPEnvelope envelope = (SOAPEnvelope) message.getSOAPEnvelope();
        RPCElement body = (RPCElement) envelope.getFirstBody();
        Vector arglist = body.getParams();
        RPCParam param = (RPCParam) arglist.get(0);
        DataType result = (DataType) param.getValue();
        System.out.println(result);
        assertTrue(result != null);
        InnerDataType inner[] = result.getInnerData();
        assertTrue(inner != null);
        assertEquals(inner.length, 10);
        assertEquals(inner[9].getTrDescr().getValue().trim(),"desc#9 HELLO!");
    }
}
