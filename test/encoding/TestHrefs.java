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
 * Test deserialization of SOAP messages with references, by putting the
 * actual value in various places in the message.
 * 
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class TestHrefs extends TestCase {

    private String header;
    private String [] messageParts;
    
    public TestHrefs(String name) {
        this(name, Constants.URI_CURRENT_SCHEMA_XSI, 
                   Constants.URI_CURRENT_SCHEMA_XSD);
    }

    public static void main(String [] args)
    {
        TestHrefs tester = new TestHrefs("me");
        try {
            tester.testStringReference();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TestHrefs(String name, String NS_XSI, String NS_XSD) {
        super(name);

        header = 
            "<?xml version=\"1.0\"?>\n" +
            "<soap:Envelope " +
              "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
              "xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" " +
              "xmlns:xsi=\"" + NS_XSI + "\" " +
              "xmlns:xsd=\"" + NS_XSD + "\">\n";
        
        messageParts = new String [] {
              "<soap:Body>\n" +
                "<methodResult xmlns=\"http://tempuri.org/\">\n" +
                 "<reference href=\"#1\"/>\n" +
                "</methodResult>\n",

              "</soap:Body>\n",
        
             "</soap:Envelope>\n" };
    }

    private void deserialize(String data, Object expected, int pos) {
       String msgString = header;
       
       for (int i = 0; i < messageParts.length; i++) {
           if (pos == i)
               msgString += data;
           msgString += messageParts[i];
       }
       
       Message message = new Message(msgString, "String");

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
       assertEquals("case " + pos, expected, result);
    }

    public void testStringReference() {
        deserialize("<result id=\"1\" xsi:type=\"xsd:string\">abc</result>",
                    "abc", 0);
        deserialize("<result id=\"1\" xsi:type=\"xsd:string\">abc</result>",
                    "abc", 1);
        deserialize("<result id=\"1\" xsi:type=\"xsd:string\">abc</result>",
                    "abc", 2);
    }

    /*
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

    public void testUntyped() {
         deserialize("<result>10</result>", "10");
    }
    */
}
