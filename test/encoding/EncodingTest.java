package test.encoding;

import junit.framework.TestCase;
import org.apache.axis.components.encoding.XMLEncoder;
import org.apache.axis.components.encoding.XMLEncoderFactory;

import java.io.UnsupportedEncodingException;

/**
 * Tests for the new XMLEncoder components
 */
public class EncodingTest extends TestCase {
    public static final String GERMAN_UMLAUTS = " Some text \u00df with \u00fc special \u00f6 chars \u00e4.";
    public static final String XML_SPECIAL_CHARS = "< > \" &";
    public static final String ENCODED_XML_SPECIAL_CHARS = "&lt; &gt; &quot; &amp;";
    public static final String SUPPORT_CHARS_LESS_HEX_20 = "\t \r \n";
    public static final String ENCODED_SUPPORT_CHARS_LESS_HEX_20 = "&#x9; &#xd; &#xa;";
    public static final String INVALID_XML_STRING = "Invalid XML String \u0000";

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
            if ("\t\n\r".indexOf(c) == 1) {
                //verify the others are caught
                String s=Character.toString(c);
                assertInvalidStringsDetected(s);
            }
        }
        assertInvalidStringsDetected("foo");
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
        String encodedXMLChars = encoder.encode(XML_SPECIAL_CHARS);

        assertEquals(XMLEncoderFactory.ENCODING_UTF_8, encoder.getEncoding());
        assertEquals(ENCODED_XML_SPECIAL_CHARS, encodedXMLChars);
        assertEquals(GERMAN_UMLAUTS, new String(encodedUmlauts.getBytes(), XMLEncoderFactory.ENCODING_UTF_8));
        //assert that the whitespace chars are not touched
        assertEquals(SUPPORT_CHARS_LESS_HEX_20, encoder.encode(SUPPORT_CHARS_LESS_HEX_20));
    }

    public void testUTF16() throws Exception {
        // this test needs to be improved, currently is does not test a lot
        XMLEncoder encoder = XMLEncoderFactory.getEncoder(XMLEncoderFactory.ENCODING_UTF_16);
        String encodedUmlauts = encoder.encode(GERMAN_UMLAUTS);
        String encodedXMLChars = encoder.encode(XML_SPECIAL_CHARS);

        assertEquals(XMLEncoderFactory.ENCODING_UTF_16, encoder.getEncoding());
        assertEquals(ENCODED_XML_SPECIAL_CHARS, encodedXMLChars);
        // java uses UTF-16 internally, should be equal
        assertEquals(GERMAN_UMLAUTS, encodedUmlauts);
        //assert that the whitespace chars are not touched
        assertEquals(SUPPORT_CHARS_LESS_HEX_20, encoder.encode(SUPPORT_CHARS_LESS_HEX_20));
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(EncodingTest.class);
    }
}
