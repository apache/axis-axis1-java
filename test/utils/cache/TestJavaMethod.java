package test.utils.cache;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.axis.utils.cache.JavaMethod;

import java.lang.reflect.Method;

public class TestJavaMethod extends TestCase
{
    public TestJavaMethod (String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestJavaMethod.class);
    }

    protected void setup() {
    }

    public void testGetMethodWithVectorMethods()
    {
        Class vector = new java.util.Vector().getClass(); 
        JavaMethod jmAdd = new JavaMethod(vector, "add");
        assertNotNull("jmAdd was null", jmAdd);

        Method[] adds = jmAdd.getMethod();
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
    
    public void testGetMethodWithOverloadedStringValueOf()
    {
/* RJB - now that I've removed the numArgs parameter, is this test really testing anything?
        Class str = new String().getClass(); 
        JavaMethod jm = new JavaMethod(str, "valueOf");
        assertNotNull("JavaMethod is null", jm);
        
        Method methodWithOneParam = jm.getMethod()[0];
        assertEquals("Method with one param is not 'valueOf'", "valueOf",methodWithOneParam.getName());
        Method methodWithThreeParams = jm.getMethod()[0];
        assertEquals("Method with two params is not 'valueOf'", "valueOf",methodWithThreeParams.getName());

        assertEquals("Method with one param return type is not 'java.lang.String'", "java.lang.String", methodWithOneParam.getReturnType().getName());
        assertEquals("Method with two parama return type is not 'java.lang.String'", "java.lang.String", methodWithThreeParams.getReturnType().getName());
        
        boolean gotError = false;
        try {
            Method nonceMethod = jm.getMethod()[0]; //should be no valueOf() method with 2 params
            nonceMethod.getName();
        }
        catch (NullPointerException ex) {
            gotError = true;
        }
        assertTrue("Expected NullPointerException", gotError);
*/
    }
}
