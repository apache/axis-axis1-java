package test.utils.cache;

import junit.framework.*;
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
        assertNotNull(jc);
        assertTrue(jc.getJavaClass().getName().equals("java.util.Date"));
        assertTrue(!jc.getJavaClass().getName().equals("java.util.D"));
    }

    public void testGetMethod()
    {
        Class v = new java.util.Vector().getClass(); 
        Class st = new java.util.StringTokenizer("some string").getClass();
        JavaClass jcVec = new JavaClass(v);
        JavaClass jcST = new JavaClass(st);

        Method countTkns = jcST.getMethod("countTokens", 0);
        Method nextTkn = jcST.getMethod("nextToken", 1);

        Method add1 = jcVec.getMethod("add", 1);
        Method add2 = jcVec.getMethod("add", 2);

        assertEquals("countTokens", countTkns.getName());
        assertEquals("nextToken", nextTkn.getName());

        assertEquals("boolean", add1.getReturnType().getName());
        assertEquals("void", add2.getReturnType().getName());
    }

    public void testNoSuchMethod()
    {
        Class v = new java.util.Vector().getClass(); 
        JavaClass jcVec = new JavaClass(v);

        Method add7 = jcVec.getMethod("add", 7);
        assertNull(add7);
    }

    public void testUnknownNumberOfArgs()
    {
        Class v = new java.util.Vector().getClass(); 
        JavaClass jcVec = new JavaClass(v);

        Method add7 = jcVec.getMethod("add", -1);
        assertNull(add7);

        Method insertElementAt = jcVec.getMethod("insertElementAt", -1);
        assertEquals(2, insertElementAt.getParameterTypes().length);
        assertEquals("void", insertElementAt.getReturnType().getName());
    }
}
