package test.utils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.axis.utils.JavaUtils;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.Vector;

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
                JavaUtils.xmlNameToJava("other.punct\u00B7chars"));
        assertEquals("answer42", JavaUtils.xmlNameToJava("Answer42"));

        assertEquals("\u2160Foo", JavaUtils.xmlNameToJava("\u2160foo"));
        assertEquals("foo", JavaUtils.xmlNameToJava("2foo"));
        assertEquals("_Foo_", JavaUtils.xmlNameToJava("_foo_"));
        assertEquals("foobar", JavaUtils.xmlNameToJava("--foobar--"));

        assertEquals("foo22Bar", JavaUtils.xmlNameToJava("foo22bar"));
        assertEquals("foo\u2160Bar", JavaUtils.xmlNameToJava("foo\u2160bar"));

        assertEquals("fooBar", JavaUtils.xmlNameToJava("foo-bar"));
        assertEquals("fooBar", JavaUtils.xmlNameToJava("foo.bar"));
        assertEquals("fooBar", JavaUtils.xmlNameToJava("foo:bar"));
        assertEquals("foo_Bar", JavaUtils.xmlNameToJava("foo_bar"));
        assertEquals("fooBar", JavaUtils.xmlNameToJava("foo\u00B7bar"));
        assertEquals("fooBar", JavaUtils.xmlNameToJava("foo\u0387bar"));
        assertEquals("fooBar", JavaUtils.xmlNameToJava("foo\u06DDbar"));
        assertEquals("fooBar", JavaUtils.xmlNameToJava("foo\u06DEbar"));
        assertEquals("fooBar", JavaUtils.xmlNameToJava("FooBar"));
        assertEquals("FOOBar", JavaUtils.xmlNameToJava("FOOBar"));

        assertEquals("a1BBB", JavaUtils.xmlNameToJava("A1-BBB"));
        assertEquals("ABBB", JavaUtils.xmlNameToJava("A-BBB"));
        assertEquals("ACCC", JavaUtils.xmlNameToJava("ACCC"));
        

        // the following cases are ambiguous in JSR-101
        assertEquals("fooBar", JavaUtils.xmlNameToJava("foo bar"));
        assertEquals("_1", JavaUtils.xmlNameToJava("-"));
    }
    
    /**
     * test the convert() function 
     * verify that we can convert to the Collection, List, and Set interfaces
     */ 
    public void testConvert() {
        Integer[] array = new Integer[4];
        array[0] = new Integer(5); array[1] = new Integer(4);
        array[2] = new Integer(3); array[3] = new Integer(2);
        
        Object ret = JavaUtils.convert(array, List.class);
        assertTrue("Converted array not a List", (ret instanceof List));
        List list = (List)ret;
        for (int i = 0; i < array.length; i++) {
            assertEquals(array[i], list.get(i));
        }
        
        ret = JavaUtils.convert(array, Collection.class);
        assertTrue("Converted array is not a Collection", (ret instanceof Collection));
        
        ret = JavaUtils.convert(array, Set.class);
        assertTrue("Converted array not a Set", (ret instanceof Set));
        
        ret = JavaUtils.convert(array, Vector.class);
        assertTrue("Converted array not a Vector", (ret instanceof Vector));
        
        HashMap m = new HashMap();
        m.put("abcKey", "abcVal");
        m.put("defKey", "defVal");
        ret = JavaUtils.convert(m, Hashtable.class);
        assertTrue("Converted HashMap not a Hashtable", (ret instanceof Hashtable));
    }
}
