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
        } catch (UnsupportedEncodingException e) {
            // expected
        }
        try {
            XMLEncoder encoder = XMLEncoderFactory.getEncoder(XMLEncoderFactory.ENCODING_UTF_8);
            encoder.encode(INVALID_XML_STRING);
            fail("A UnsupportedEncodingException should have been thrown.");
        } catch (IllegalArgumentException e) {
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
        assertEquals(ENCODED_SUPPORT_CHARS_LESS_HEX_20, encoder.encode(SUPPORT_CHARS_LESS_HEX_20));
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
        assertEquals(ENCODED_SUPPORT_CHARS_LESS_HEX_20, encoder.encode(SUPPORT_CHARS_LESS_HEX_20));
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(EncodingTest.class);
    }
}
