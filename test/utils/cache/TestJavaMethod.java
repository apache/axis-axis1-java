package test.utils.cache;

import junit.framework.*;
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
        assertNotNull(jmAdd);
        
        Method methodWithOneParam = jmAdd.getMethod(1);
        assertEquals("add", methodWithOneParam.getName());
        Method methodWithTwoParams = jmAdd.getMethod(2);
        assertEquals("add", methodWithTwoParams.getName());

        assertEquals("boolean", methodWithOneParam.getReturnType().getName());
        assertEquals("void", methodWithTwoParams.getReturnType().getName());
        
        boolean gotError = false;
        try {
            Method nonceMethod = jmAdd.getMethod(0); //should be no add() method with 0 params
            nonceMethod.getName();
        }
        catch (NullPointerException ex) {
            gotError = true;
        }
        assert("Expected NullPointerException", gotError);

        //on the other hand, make sure methods with 0 params work...
        JavaMethod jmCapacity = new JavaMethod(vector, "capacity");
        Method methodWithNoParams = jmCapacity.getMethod(0);
        assertEquals("capacity", methodWithNoParams.getName());
    }
    
    public void testGetMethodWithOverloadedStringValueOf()
    {
        Class str = new String().getClass(); 
        JavaMethod jm = new JavaMethod(str, "valueOf");
        assertNotNull(jm);
        
        Method methodWithOneParam = jm.getMethod(1);
        assertEquals("valueOf",methodWithOneParam.getName());
        Method methodWithThreeParams = jm.getMethod(3);
        assertEquals("valueOf",methodWithThreeParams.getName());

        assertEquals("java.lang.String", methodWithOneParam.getReturnType().getName());
        assertEquals("java.lang.String", methodWithThreeParams.getReturnType().getName());
        
        boolean gotError = false;
        try {
            Method nonceMethod = jm.getMethod(2); //should be no valueOf() method with 2 params
            nonceMethod.getName();
        }
        catch (NullPointerException ex) {
            gotError = true;
        }
        assert("Expected NullPointerException", gotError);
    }
}
