package test.encoding;

import java.lang.reflect.Array;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
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

    /**
     * Verify that two objects have the same value, handling arrays...
     */
    private static boolean equals(Object obj1, Object obj2) {
       if (obj1 == null) return (obj2 == null);
       if (obj1.equals(obj2)) return true;
       if (!obj2.getClass().isArray()) return false;
       if (!obj1.getClass().isArray()) return false;
       if (Array.getLength(obj1) != Array.getLength(obj2)) return false;
       for (int i=0; i<Array.getLength(obj1); i++)
           if (!equals(Array.get(obj1,i),Array.get(obj2,i))) return false;
       return true;
    }

    /**
     * Verify that a given XML deserialized produces the expected result
     */
    private void deserialize(String data, Object expected) {
       Message message = new Message(header + data + footer, "String");
       message.setMessageContext(new MessageContext());

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
       if (!equals(expected,result)) assertEquals(expected, result);
    }

    public void testString() {
        deserialize("<result xsi:type=\"xsd:string\">abc</result>",
                    "abc");
    }

    public void testBoolean() {
        deserialize("<result xsi:type=\"xsd:boolean\">true</result>",
                    new Boolean(true));
    }

    public void testDouble() {
        deserialize("<result xsi:type=\"xsd:double\">3.14</result>",
                    new Double(3.14));
    }

    public void testFloat() {
        deserialize("<result xsi:type=\"xsd:float\">3.14</result>",
                    new Float(3.14F));
    }

    public void testInt() {
        deserialize("<result xsi:type=\"xsd:int\">10</result>",
                    new Integer(10));
    }

    public void testLong() {
        deserialize("<result xsi:type=\"xsd:long\">17</result>",
                    new Long(17));
    }

    public void testShort() {
        deserialize("<result xsi:type=\"xsd:short\">3</result>",
                    new Short((short)3));
    }

    public void testArray() {
        deserialize("<result xsi:type=\"soapenc:Array\" " +
                            "soapenc:arrayType=\"xsd:string[2]\"> " +
                       "<item xsi:type=\"xsd:string\">abc</item>" + 
                       "<item xsi:type=\"xsd:string\">def</item>" +
                    "</result>",
                    new String[] {"abc", "def"});
    }

    public void testUntyped() {
         deserialize("<result>10</result>", "10");
    }
}
