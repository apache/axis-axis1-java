package test.utils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.axis.utils.JavaUtils;

import javax.xml.rpc.holders.ByteHolder;
import javax.xml.rpc.holders.LongHolder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
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

        /* Begin TABLE 20-2 Illustrative Examples from JAXRPC Spec */
        assertEquals("mixedCaseName", JavaUtils.xmlNameToJava("mixedCaseName"));

        assertEquals("nameWithDashes", JavaUtils.xmlNameToJava("name-with-dashes"));
        
        assertEquals("name_with_underscore", JavaUtils.xmlNameToJava("name_with_underscore"));
        
        assertEquals("other_punctChars", JavaUtils.xmlNameToJava("other_punct.chars"));
        
        assertEquals("answer42", JavaUtils.xmlNameToJava("Answer42"));
        /* End TABLE 20-2 Illustrative Examples from JAXRPC Spec */

        assertEquals("nameWithDashes",
                JavaUtils.xmlNameToJava("name-with-dashes"));

        assertEquals("otherPunctChars",
                JavaUtils.xmlNameToJava("other.punct\u00B7chars"));

        assertEquals("answer42", JavaUtils.xmlNameToJava("Answer42"));

        assertEquals("\u2160Foo", JavaUtils.xmlNameToJava("\u2160foo"));

        assertEquals("foo", JavaUtils.xmlNameToJava("2foo"));

        //assertEquals("_Foo_", JavaUtils.xmlNameToJava("_foo_"));
        assertEquals("_foo_", JavaUtils.xmlNameToJava("_foo_"));

        assertEquals("foobar", JavaUtils.xmlNameToJava("--foobar--"));

        assertEquals("foo22Bar", JavaUtils.xmlNameToJava("foo22bar"));

        assertEquals("foo\u2160Bar", JavaUtils.xmlNameToJava("foo\u2160bar"));

        assertEquals("fooBar", JavaUtils.xmlNameToJava("foo-bar"));

        assertEquals("fooBar", JavaUtils.xmlNameToJava("foo.bar"));

        assertEquals("fooBar", JavaUtils.xmlNameToJava("foo:bar"));

        //assertEquals("foo_Bar", JavaUtils.xmlNameToJava("foo_bar"));
        assertEquals("foo_bar", JavaUtils.xmlNameToJava("foo_bar"));
      
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

        LongHolder holder = new LongHolder(1);
        ret = JavaUtils.convert(holder, Object.class);
        assertTrue(ret != null);
        assertTrue(Long.class.isInstance(ret));

        ByteHolder holder2 = new ByteHolder((byte)0);
        ret = JavaUtils.convert(holder2, Object.class);
        assertTrue(ret != null);
        assertTrue(Byte.class.isInstance(ret));
        
        // Make sure we convert ArrayList to array in 2D cases
        Object[] arrayin = new Object[1];
        ArrayList data = new ArrayList(5);
        data.add("one"); data.add(new Integer(2)); data.add(new Float(4.0));
        data.add(new Double(5.0)); data.add("five");
        arrayin[0] = data;
        ret = JavaUtils.convert(arrayin, Object[][].class);
        assertTrue("Converted 2D array/ArrayList wrong", ret.getClass().equals(Object[][].class));
        Object[][] outer = (Object[][]) ret;
        assertEquals("Outer array of 2D array/ArrayList is wrong length", 1, outer.length);
        Object[] inner = ((Object[][])ret)[0];
        assertEquals("Inner array of 2D array/ArrayLis is wrong length", 5, inner.length);
        
        // check 2D ArrayList of ArrayList
        ArrayList data2D = new ArrayList(2);
        data2D.add(data); data2D.add(data);
        ret = JavaUtils.convert(data2D, Object[][].class);
        assertTrue("Converted 2D ArrayList wrong", ret.getClass().equals(Object[][].class));
        Object[][] outer2 = (Object[][]) ret;
        assertEquals("Outer array of 2D ArrayList is wrong length", 2, outer2.length);
        Object[] inner2 = ((Object[][]) ret)[0];
        assertEquals("Inner array of 2D ArrayList is wrong length", 5, inner2.length);
        
    }

    /**
     * test the isConvertable() function
     */
    public void testIsConvert() {
        assertTrue(JavaUtils.isConvertable(new Long(1),Long.class));
        assertTrue(JavaUtils.isConvertable(new Long(1),long.class));
        assertTrue(JavaUtils.isConvertable(new Long(1),Object.class));
        assertTrue(!JavaUtils.isConvertable(new Long(1),Float.class));
        Class clazz = long.class;
        assertTrue(JavaUtils.isConvertable(clazz,Long.class));
        assertTrue(JavaUtils.isConvertable(clazz,Object.class));
        clazz = byte.class;
        assertTrue(JavaUtils.isConvertable(clazz,Byte.class));
        assertTrue(JavaUtils.isConvertable(clazz,Object.class));
    }
    
    /**
     * Make sure we can't say convert from string[] to Calendar[]
     */
    public void testIsConvert2() 
    {
        String[] strings = new String[]{"hello"};
        Calendar[] calendars = new Calendar[1];
        assertTrue(!JavaUtils.isConvertable(strings, calendars.getClass()));
    }    

    public static void main(String args[]){
        TestJavaUtils tester = new TestJavaUtils("TestJavaUtils");
        tester.testIsConvert();
        tester.testConvert();
    }
}
