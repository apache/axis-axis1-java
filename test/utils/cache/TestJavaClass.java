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

        Method countTkns = jcST.getMethod("countTokens", 0);
        Method nextTkn = jcST.getMethod("nextToken", 1);

        Method add1 = jcVec.getMethod("add", 1);
        Method add2 = jcVec.getMethod("add", 2);

        assertEquals("countTkns name was not 'countTokens', it is: " + countTkns.getName(),
                     "countTokens", countTkns.getName());
        assertEquals("nextTkn name was not 'nextToken', it is: " + nextTkn.getName(),
                     "nextToken", nextTkn.getName());

        assertEquals("Return type was not 'boolean', it was: " + add1.getReturnType().getName(),
                     "boolean", add1.getReturnType().getName());
        assertEquals("Return type was not 'void', it was: " + add2.getReturnType().getName(),
                     "void", add2.getReturnType().getName());
    }

    public void testNoSuchMethod()
    {
        Class v = new java.util.Vector().getClass(); 
        JavaClass jcVec = new JavaClass(v);

        Method add7 = jcVec.getMethod("add", 7);
        assertNull("add7 was not null", add7);
    }

    public void testUnknownNumberOfArgs()
    {
        Class v = new java.util.Vector().getClass(); 
        JavaClass jcVec = new JavaClass(v);

        Method add7 = jcVec.getMethod("add", -1);
        assertNull("add7 was not null", add7);

        Method insertElementAt = jcVec.getMethod("insertElementAt", -1);
        assertEquals("Length was not 2, it was: " + insertElementAt.getParameterTypes().length,
                     2, insertElementAt.getParameterTypes().length);
        assertEquals("Return type was not 'void', it was: " + insertElementAt.getReturnType().getName(),
                     "void", insertElementAt.getReturnType().getName());
    }
}
