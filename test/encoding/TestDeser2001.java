package test.encoding;

import org.apache.axis.Constants;
import junit.framework.TestCase;
import java.util.Calendar;
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
    public void testMinDate() {
        Calendar date = Calendar.getInstance();
        date.set(1999,04,31,12,01,30);
        date.setTimeZone(TimeZone.getTimeZone("GMT"));
        date.set(Calendar.MILLISECOND,0);
        deserialize("<result xsi:type=\"xsd:dateTime\">" + 
                       "1999-05-31T12:01:30" + 
                     "</result>",
                     date.getTime());
    }

    public void testDateZ() {
        Calendar date = Calendar.getInstance();
        date.set(1999,04,31,12,01,30);
        date.setTimeZone(TimeZone.getTimeZone("GMT"));
        date.set(Calendar.MILLISECOND,150);
        deserialize("<result xsi:type=\"xsd:dateTime\">" + 
                       "1999-05-31T12:01:30.150Z" + 
                     "</result>",
                     date.getTime());
    }

    public void testDateTZ() {
        Calendar date = Calendar.getInstance();
        date.set(1999,04,31,12,01,30);
        date.setTimeZone(TimeZone.getTimeZone("GMT-05:00"));
        date.set(Calendar.MILLISECOND,150);
        deserialize("<result xsi:type=\"xsd:dateTime\">" + 
                       "1999-05-31T12:01:30.150-05:00" + 
                     "</result>",
                     date.getTime());
    }

}
