package test.encoding;

import org.apache.axis.Constants;
import org.apache.axis.encoding.Hex;

import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

/** 
 * Test deserialization of SOAP responses
 */
public class TestDeser2001 extends TestDeser {

    public TestDeser2001(String name) {
        super(name, Constants.URI_2001_SCHEMA_XSI, 
                    Constants.URI_2001_SCHEMA_XSD);
    }

    /** 
     * Test deserialization of Date responses
     */
    public void testMinDate() throws Exception {
        Calendar date = Calendar.getInstance();
        date.set(1999,04,31,12,01,30);
        date.setTimeZone(TimeZone.getTimeZone("GMT"));
        date.set(Calendar.MILLISECOND,0);
        deserialize("<result xsi:type=\"xsd:dateTime\">" + 
                       "1999-05-31T12:01:30" + 
                     "</result>",
                     date.getTime());
    }

    public void testDateZ() throws Exception {
        Calendar date = Calendar.getInstance();
        date.set(1999,04,31,12,01,30);
        date.setTimeZone(TimeZone.getTimeZone("GMT"));
        date.set(Calendar.MILLISECOND,150);
        deserialize("<result xsi:type=\"xsd:dateTime\">" + 
                       "1999-05-31T12:01:30.150Z" + 
                     "</result>",
                     date.getTime());
    }

    public void testDateTZ() throws Exception {
        Calendar date = Calendar.getInstance();
        date.set(1999,04,31,12,01,30);
        date.setTimeZone(TimeZone.getTimeZone("GMT-05:00"));
        date.set(Calendar.MILLISECOND,150);
        deserialize("<result xsi:type=\"xsd:dateTime\">" + 
                       "1999-05-31T12:01:30.150-05:00" + 
                     "</result>",
                     date.getTime());
    }

    public void testBase64() throws Exception {
        deserialize("<result xsi:type=\"xsd:base64Binary\">QmFzZTY0</result>",
                    "Base64".getBytes());
    }

    public void testBase64Null() throws Exception {
        deserialize("<result xsi:type=\"xsd:base64Binary\"></result>",
                    new byte[0]);
    }

    public void testHex() throws Exception {
        deserialize("<result xsi:type=\"xsd:hexBinary\">50A9</result>",
                    new Hex("50A9"));
    }

    public void testHexNull() throws Exception {
        deserialize("<result xsi:type=\"xsd:hexBinary\"></result>",
                    new Hex(""));
    }

    public void testMapWithNils() throws Exception {
        HashMap m = new HashMap();
        m.put(null, new Boolean("false"));
        m.put("hi", null);
        deserialize("<result xsi:type=\"xmlsoap:Map\" " +
                    "xmlns:xmlsoap=\"http://xml.apache.org/xml-soap\"> " +
                      "<item>" +
                       "<key xsi:nil=\"true\"/>" +
                       "<value xsi:type=\"xsd:boolean\">false</value>" + 
                      "</item><item>" +
                       "<key xsi:type=\"string\">hi</key>" +
                       "<value xsi:nil=\"true\"/>" +
                      "</item>" +
                    "</result>",
                    m);
    }

}
