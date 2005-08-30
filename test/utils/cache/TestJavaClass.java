package test.utils.cache;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.axis.utils.cache.JavaClass;

import java.lang.reflect.Method;

public class TestJavaClass extends TestCase
{
    public TestJavaClass (String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestJavaClass.class);
    }
    
    protected void setup() {
    }

    public void testGetJavaClass()
    {
        Class c = new java.util.Date().getClass(); 
        JavaClass jc = new JavaClass(c);
        assertNotNull("The JavaClass was null", jc);
        assertTrue("JavaClass name is not 'java.util.Date', it is: " + jc.getClass().getName(),
                   jc.getJavaClass().getName().equals("java.util.Date"));
        assertTrue("JavaClass cut off the name of the real class.",
                   !jc.getJavaClass().getName().equals("java.util.D"));
    }

    public void testGetMethod()
    {
        Class v = new java.util.Vector().getClass(); 
        Class st = new java.util.StringTokenizer("some string").getClass();
        JavaClass jcVec = new JavaClass(v);
        JavaClass jcST = new JavaClass(st);

        Method countTkns = jcST.getMethod("countTokens")[0];
        Method nextTkn = jcST.getMethod("nextToken")[0];

        Method[] adds = jcVec.getMethod("add");

        assertEquals("countTkns name was not 'countTokens', it is: " + countTkns.getName(),
                     "countTokens", countTkns.getName());
        assertEquals("nextTkn name was not 'nextToken', it is: " + nextTkn.getName(),
                     "nextToken", nextTkn.getName());

        assertEquals("There are not 2 add methods as expected, there are " + adds.length, 2, adds.length);

        for (int i = 0; i < adds.length; ++i) {
            if (adds[i].getReturnType().equals(boolean.class)) {
                assertEquals("Unexpected boolean add signature",
                   "public synchronized boolean java.util.Vector.add(java.lang.Object)",
                   adds[i].toString());
            }
            else {
                assertEquals("Unexpected void add signature",
                    "public void java.util.Vector.add(int,java.lang.Object)",
                    adds[i].toString());
            }
        }
    }

    public void testNoSuchMethod()
    {
        Class v = new java.util.Vector().getClass(); 
        JavaClass jcVec = new JavaClass(v);

        Method[] gorp = jcVec.getMethod("gorp");
        assertNull("gorp was not null", gorp);
    }
}
