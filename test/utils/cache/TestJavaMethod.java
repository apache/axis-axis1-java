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
        
        Method methodWithOneParam = jmAdd.getMethod(1);
        assertEquals("Method with one param was not 'add'", "add", methodWithOneParam.getName());
        Method methodWithTwoParams = jmAdd.getMethod(2);
        assertEquals("Method with two params was not 'add'", "add", methodWithTwoParams.getName());

        assertEquals("Method with one param return type was not 'boolean'", "boolean", methodWithOneParam.getReturnType().getName());
        assertEquals("Method with two params return type was not 'void'", "void", methodWithTwoParams.getReturnType().getName());
        
        boolean gotError = false;
        try {
            Method nonceMethod = jmAdd.getMethod(0); //should be no add() method with 0 params
            nonceMethod.getName();
        }
        catch (NullPointerException ex) {
            gotError = true;
        }
        assertTrue("Expected NullPointerException", gotError);

        //on the other hand, make sure methods with 0 params work...
        JavaMethod jmCapacity = new JavaMethod(vector, "capacity");
        Method methodWithNoParams = jmCapacity.getMethod(0);
        assertEquals("Method with no params was not 'capacity'", "capacity", methodWithNoParams.getName());
    }
    
    public void testGetMethodWithOverloadedStringValueOf()
    {
        Class str = new String().getClass(); 
        JavaMethod jm = new JavaMethod(str, "valueOf");
        assertNotNull("JavaMethod is null", jm);
        
        Method methodWithOneParam = jm.getMethod(1);
        assertEquals("Method with one param is not 'valueOf'", "valueOf",methodWithOneParam.getName());
        Method methodWithThreeParams = jm.getMethod(3);
        assertEquals("Method with two params is not 'valueOf'", "valueOf",methodWithThreeParams.getName());

        assertEquals("Method with one param return type is not 'java.lang.String'", "java.lang.String", methodWithOneParam.getReturnType().getName());
        assertEquals("Method with two parama return type is not 'java.lang.String'", "java.lang.String", methodWithThreeParams.getReturnType().getName());
        
        boolean gotError = false;
        try {
            Method nonceMethod = jm.getMethod(2); //should be no valueOf() method with 2 params
            nonceMethod.getName();
        }
        catch (NullPointerException ex) {
            gotError = true;
        }
        assertTrue("Expected NullPointerException", gotError);
    }
}
