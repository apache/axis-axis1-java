package test.encoding;

import junit.framework.TestCase;
import org.apache.axis.components.encoding.XMLEncoder;
import org.apache.axis.components.encoding.XMLEncoderFactory;

import java.io.UnsupportedEncodingException;

/**
 * Tests for the new XMLEncoder components.
 * Some of the tests are convoluted; that is to make diagnosis of faults easy, even
 * with JUnit's XML reporting intervening in the process.
 */
public class EncodingTest extends TestCase {
    private static final String GERMAN_UMLAUTS = " Some text \u00df with \u00fc special \u00f6 chars \u00e4.";
    private static final String XML_SPECIAL_CHARS = "< > \" &";
    private static final String ENCODED_XML_SPECIAL_CHARS = "&lt; &gt; &quot; &amp;";
    private static final String SUPPORT_CHARS_LESS_HEX_20 = "\t\r\n";
    private static final String ENCODED_SUPPORT_CHARS_LESS_HEX_20 = "&#x9;&#xd;&#xa;";
    private static final String INVALID_XML_STRING = "Invalid XML String \u0000";

    public EncodingTest(String s) {
        super(s);
    }

    public void testEncodingFailure() throws Exception {
        try {
            XMLEncoderFactory.getEncoder("XYZ");
            fail("A UnsupportedEncodingException should have been thrown.");
        } catch (UnsupportedEncodingException expected) {
            // expected
        }
        assertInvalidStringsDetected(INVALID_XML_STRING);
        //run through the first 32 chars
        for(int i=0;i<31;i++) {
            char c=(char)i;
            //ignore legit whitespace
            if ("\t\n\r".indexOf(c) == -1) {
                //verify the others are caught
                String s=(new Character(c)).toString();
                assertInvalidStringsDetected(s);
            }
        }
    }

    /**
     * try a string through the two encoders we have, verify it is invalid
     * @param invalidXmlString string we expect to fail
     * @throws Exception
     */
    private void assertInvalidStringsDetected(String invalidXmlString) throws Exception {
        assertInvalidStringsDetected(XMLEncoderFactory.ENCODING_UTF_16,invalidXmlString);
        assertInvalidStringsDetected(XMLEncoderFactory.ENCODING_UTF_8, invalidXmlString);
    }



    /**
     * try a string through the two encoders we have, verify it is invalid
     * @param encoderChoice name of the encoder to use
     * @param invalidXmlString string we expect to fail
     */
    private void assertInvalidStringsDetected(String encoderChoice, String invalidXmlString) throws Exception {
        try {
            XMLEncoder encoder = XMLEncoderFactory.getEncoder(encoderChoice);
            encoder.encode(invalidXmlString);
            fail("A UnsupportedEncodingException should have been thrown.");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    public void testUTF8() throws Exception {
        XMLEncoder encoder = XMLEncoderFactory.getEncoder(XMLEncoderFactory.ENCODING_UTF_8);
        String encodedUmlauts = encoder.encode(GERMAN_UMLAUTS);

        assertEquals(XMLEncoderFactory.ENCODING_UTF_8, encoder.getEncoding());
        assertEquals(GERMAN_UMLAUTS, new String(encodedUmlauts.getBytes(), XMLEncoderFactory.ENCODING_UTF_8));
        verifyCommonAssertions(encoder);
    }

    public void testUTF16() throws Exception {
        XMLEncoder encoder = XMLEncoderFactory.getEncoder(XMLEncoderFactory.ENCODING_UTF_16);
        String encodedUmlauts = encoder.encode(GERMAN_UMLAUTS);

        assertEquals(XMLEncoderFactory.ENCODING_UTF_16, encoder.getEncoding());
        // java uses UTF-16 internally, should be equal
        assertEquals(GERMAN_UMLAUTS, encodedUmlauts);
        verifyCommonAssertions(encoder);
    }


    /**
     * assertions here hold for either encoder
     * @param encoder
     */
    private void verifyCommonAssertions(XMLEncoder encoder) {
        String encodedXMLChars = encoder.encode(XML_SPECIAL_CHARS);
        assertEquals(ENCODED_XML_SPECIAL_CHARS, encodedXMLChars);
        //assert that the whitespace chars are not touched
        verifyUntouched(encoder, "\t");
        verifyUntouched(encoder, "\n");
        verifyUntouched(encoder, "\r");
    }

    /**
     * verify that the support chars are not touched. This is done on a char by
     * char basis for easier debugging. One debug problem there is that
     * ant's XML logger also encodes the strings, making diagnosing
     * the defect from an error report trickier than normal.
     * @param encoder
     */
    private void verifyUntouched(XMLEncoder encoder, String source) {
        for(int i=0;i<source.length();i++) {
            char c = source.charAt(i);
            Character ch = new Character(c);
            String xmlString = ch.toString();
            String encoded= encoder.encode(xmlString);
            assertEquals("Char " +(int) c + " was encoded as " + hexDump(encoded),
                    xmlString,encoded);
        }
    }

    private String hexDump(String source) {
        StringBuffer out=new StringBuffer(source.length()*5);
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            out.append("0x");
            out.append(Integer.toHexString(c));
            out.append(" ");
        }
        return new String(out);

    }
    public static void main(String[] args) {
        junit.textui.TestRunner.run(EncodingTest.class);
    }
}
