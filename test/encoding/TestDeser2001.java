package test.encoding;

import org.apache.axis.Constants;

import org.apache.axis.types.HexBinary;
import org.apache.axis.types.NormalizedString;
import org.apache.axis.types.Token;
import org.apache.axis.types.UnsignedLong;
import org.apache.axis.types.UnsignedInt;
import org.apache.axis.types.UnsignedShort;
import org.apache.axis.types.UnsignedByte;
import org.apache.axis.types.Time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import javax.xml.namespace.QName;

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
        date.set(1999, 04, 31, 0, 0, 0);
        date.set(Calendar.MILLISECOND,0);
        deserialize("<result xsi:type=\"xsd:date\">" +
                       "1999-05-31" +
                     "</result>",
                     date.getTime());
    }

    /**
     * Test deserialization of dateTime (Calendar) responses
     */
    public void testMinDateTime() throws Exception {
        Calendar date = Calendar.getInstance();
        date.set(1999,04,31, 12, 01, 30);
        date.setTimeZone(TimeZone.getTimeZone("GMT"));
        date.set(Calendar.MILLISECOND,0);
        deserialize("<result xsi:type=\"xsd:dateTime\">" +
                       "1999-05-31T12:01:30Z" +
                     "</result>",
                     date);
    }

    public void testDateTimeZ() throws Exception {
        Calendar date = Calendar.getInstance();
        date.set(1999,04,31,12,01,30);
        date.setTimeZone(TimeZone.getTimeZone("GMT"));
        date.set(Calendar.MILLISECOND,150);
        deserialize("<result xsi:type=\"xsd:dateTime\">" +
                       "1999-05-31T12:01:30.150Z" +
                     "</result>",
                     date);
    }

    public void testDateTZ() throws Exception {
        Calendar date = Calendar.getInstance();
        date.set(1999, 04, 31, 0, 0, 0);
        date.set(Calendar.MILLISECOND,0);
        deserialize("<result xsi:type=\"xsd:date\">" +
                       "1999-05-31" +
                     "</result>",
                     date.getTime());
    }

    public void testDateTimeTZ() throws Exception {
        Calendar date = Calendar.getInstance();
        date.set(1999,04,31,12,01,30);
        date.set(Calendar.MILLISECOND,150);
        deserialize("<result xsi:type=\"xsd:dateTime\">" +
                       "1999-05-31T12:01:30.150" + calcGMTOffset(date) +
                     "</result>",
                     date);
    }

    /**
     * Test the xsd:Time deserialization
     */ 
    public void testTimeZ() throws Exception {
        Calendar date = Calendar.getInstance();
        date.set(Calendar.HOUR_OF_DAY, 12);
        date.set(Calendar.MINUTE, 01);
        date.set(Calendar.SECOND, 30);
        date.set(Calendar.MILLISECOND,150);
        date.setTimeZone(TimeZone.getTimeZone("GMT"));
        Time time = new Time(date);
        deserialize("<result xsi:type=\"xsd:time\">" +
                       "12:01:30.150Z" +
                     "</result>",
                     time);
    }
    public void testTimeTZ() throws Exception {
        Calendar date = Calendar.getInstance();
        date.set(Calendar.HOUR_OF_DAY, 12);
        date.set(Calendar.MINUTE, 01);
        date.set(Calendar.SECOND, 30);
        date.set(Calendar.MILLISECOND,150);
        date.setTimeZone(TimeZone.getDefault());
        Time time = new Time(date);
        deserialize("<result xsi:type=\"xsd:time\">" +
                       "12:01:30.150" + calcGMTOffset(date) +
                     "</result>",
                     time);
    }
    
    private final int msecsInMinute = 60000;
    private final int msecsInHour = 60 * msecsInMinute;

    private String calcGMTOffset(Calendar cal) {
        int msecOffset = cal.get(Calendar.ZONE_OFFSET) +
                cal.get(Calendar.DST_OFFSET);
        int hourOffset = Math.abs(msecOffset / msecsInHour);
        String offsetString = msecOffset > 0 ? "+" : "-";
        offsetString += hourOffset >= 10 ? "" + hourOffset : "0" + hourOffset;
        offsetString += ":";
        int minOffset = Math.abs(msecOffset % msecsInHour);
        if (minOffset == 0) {
            offsetString += "00";
        }
        else {
            offsetString += minOffset >= 10 ? "" + minOffset : "0" + minOffset;
        }
        return offsetString;
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
                    new HexBinary("50A9"),true);
    }

    public void testHexNull() throws Exception {
        deserialize("<result xsi:type=\"xsd:hexBinary\"></result>",
                    new HexBinary(""),true);
    }

    public void testToken() throws Exception {
        deserialize("<result xsi:type=\"xsd:token\">abcdefg</result>",
                    new Token("abcdefg"),true);
    }

    public void testNormalizedString() throws Exception {
        deserialize("<result xsi:type=\"xsd:normalizedString\">abcdefg</result>",
                    new NormalizedString("abcdefg"),true);
    }

    public void testUnsignedLong() throws Exception {
        deserialize("<result xsi:type=\"xsd:unsignedLong\">100</result>",
                    new UnsignedLong(100),true);
    }

    public void testUnsignedInt() throws Exception {
        deserialize("<result xsi:type=\"xsd:unsignedInt\">101</result>",
                    new UnsignedInt(101),true);
    }

    public void testUnsignedShort() throws Exception {
        deserialize("<result xsi:type=\"xsd:unsignedShort\">102</result>",
                    new UnsignedShort(102),true);
    }

    public void testUnsignedByte() throws Exception {
        deserialize("<result xsi:type=\"xsd:unsignedByte\">103</result>",
                    new UnsignedByte(103),true);
    }

    public void testQName() throws Exception {
        deserialize("<result xsi:type=\"xsd:QName\" xmlns:qns=\"namespace\">qns:localPart</result>", new QName("namespace", "localPart"), true);
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

    public void testArrayWithNilInt() throws Exception {
        ArrayList list = new ArrayList(4);
        list.add(new Integer(1));
        list.add(null);
        list.add(new Integer(3));
        deserialize("<result xsi:type=\"soapenc:Array\" " +
                            "soapenc:arrayType=\"xsd:int[3]\"> " +
                       "<item xsi:type=\"xsd:int\">1</item>" +
                       "<item xsi:nil=\"true\"/>" +
                       "<item xsi:type=\"xsd:int\">3</item>" +
                    "</result>",
                    list, true);
    }

    public void testArrayWithNilString() throws Exception {
        ArrayList list = new ArrayList(4);
        list.add("abc");
        list.add(null);
        list.add("def");
        deserialize("<result xsi:type=\"soapenc:Array\" " +
                            "soapenc:arrayType=\"xsd:string[3]\"> " +
                       "<item xsi:type=\"xsd:string\">abc</item>" +
                       "<item xsi:nil=\"true\"/>" +
                       "<item xsi:type=\"xsd:string\">def</item>" +
                    "</result>",
                    list, true);
    }

    public void testNilSOAPBoolean() throws Exception {
        deserialize("<result xsi:type=\"soapenc:boolean\" xsi:nil=\"true\" />",
                    null);
    }

}
