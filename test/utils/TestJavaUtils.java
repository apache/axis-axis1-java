package test.utils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.axis.utils.JavaUtils;

public class TestJavaUtils extends TestCase
{

    public TestJavaUtils (String name) {
        super(name);
    }
    public static Test suite() {
        return new TestSuite(TestJavaUtils.class);
    }

    public void setup() {
    }

    /** 
     * See JSR-101: JAX-RPC, Appendix: Mapping of XML Names
     */
    public void testXmlNameToJava() {
        assertEquals("mixedCaseName", JavaUtils.xmlNameToJava("mixedCaseName"));
        assertEquals("nameWithDashes",
                JavaUtils.xmlNameToJava("name-with-dashes"));
        assertEquals("otherPunctChars",
                JavaUtils.xmlNameToJava("other_punct\u00B7chars"));
        assertEquals("answer42", JavaUtils.xmlNameToJava("Answer42"));

        assertEquals("\u2160Foo", JavaUtils.xmlNameToJava("\u2160foo"));
        assertEquals("foo", JavaUtils.xmlNameToJava("2foo"));
        assertEquals("foo", JavaUtils.xmlNameToJava("_foo_"));
        assertEquals("foobar", JavaUtils.xmlNameToJava("--foobar--"));

        assertEquals("foo22Bar", JavaUtils.xmlNameToJava("foo22bar"));
        assertEquals("foo\u2160Bar", JavaUtils.xmlNameToJava("foo\u2160bar"));

        assertEquals("fooBar", JavaUtils.xmlNameToJava("foo-bar"));
        assertEquals("fooBar", JavaUtils.xmlNameToJava("foo.bar"));
        assertEquals("fooBar", JavaUtils.xmlNameToJava("foo:bar"));
        assertEquals("fooBar", JavaUtils.xmlNameToJava("foo_bar"));
        assertEquals("fooBar", JavaUtils.xmlNameToJava("foo\u00B7bar"));
        assertEquals("fooBar", JavaUtils.xmlNameToJava("foo\u0387bar"));
        assertEquals("fooBar", JavaUtils.xmlNameToJava("foo\u06DDbar"));
        assertEquals("fooBar", JavaUtils.xmlNameToJava("foo\u06DEbar"));
        assertEquals("fooBar", JavaUtils.xmlNameToJava("FooBar"));
        assertEquals("FOOBar", JavaUtils.xmlNameToJava("FOOBar"));

        // the following cases are ambiguous in JSR-101
        assertEquals("fooBar", JavaUtils.xmlNameToJava("foo bar"));
        assertEquals("_1", JavaUtils.xmlNameToJava("-"));
        assertEquals("__", JavaUtils.xmlNameToJava("_"));
    }
}
