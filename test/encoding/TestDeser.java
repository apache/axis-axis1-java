package test.encoding;

import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.message.*;
import org.apache.axis.encoding.*;
import org.apache.axis.utils.QName;
import org.xml.sax.InputSource;
import org.apache.xerces.parsers.SAXParser;
import java.io.*;
import java.util.*;

import junit.framework.TestCase;

/** 
 * Test deserialization of SOAP responses
 */
public class TestDeser extends TestCase {

    private String header;
    private String footer;

    public TestDeser(String name) {
        this(name, Constants.URI_CURRENT_SCHEMA_XSI, 
                   Constants.URI_CURRENT_SCHEMA_XSD);
    }

    public TestDeser(String name, String NS_XSI, String NS_XSD) {
        super(name);

        header =
            "<?xml version=\"1.0\"?>\n" +
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
            "</soap:Envelope>\n";
    }

    private void deserialize(String data, Object expected) {
       Message message = new Message(header + data + footer, "String");

       SOAPEnvelope envelope = (SOAPEnvelope)message.getAs("SOAPEnvelope");
       assertNotNull("envelope", envelope);

       RPCElement body = (RPCElement)envelope.getFirstBody();
       assertNotNull("body", body);

       Vector arglist = body.getParams();
       assertNotNull("arglist", arglist);
       assert("param.size()>0", arglist.size()>0);

       RPCParam param = (RPCParam) arglist.get(0);
       assertNotNull("param", param);

       Object result = param.getValue();
       assertEquals(expected, result);
    }

    public void testInt() {
        deserialize("<result xsi:type=\"xsd:int\">10</result>",
                    new Integer(10));
    }

    public void testUntyped() {
         deserialize("<result>10</result>", "10");
    }
}
