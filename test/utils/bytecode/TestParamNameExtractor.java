package test.utils.bytecode;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.lang.reflect.Method;
import java.util.List;
import java.util.ArrayList;

import org.apache.axis.utils.bytecode.ParamNameExtractor;

/**
 * Description
 * User: pengyu
 * Date: Sep 12, 2003
 * Time: 11:47:48 PM
 * 
 */
public class TestParamNameExtractor extends TestCase {
    TestClass t = null;

    public TestParamNameExtractor(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestParamNameExtractor.class);
    }

    protected void setup() {
        t = this.new TestClass();
    }

    public void testExtractParameter() {
        //now get the nonoverloadmethod
        Method[] methods = TestClass.class.getMethods();
        Method method = null;
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals("nonOverloadMethod")) {
                method = methods[i];
            }
        }
        assertTrue("Find nonOverloadMethod", method != null);
        String[] params = ParamNameExtractor.getParameterNamesFromDebugInfo(method);
        assertTrue("Number of parameter is right", params.length == 2);
        assertTrue("First name of parameter is intValue", params[0].equals("intValue"));
        assertTrue("Second name of parameter is boolValue", params[1].equals("boolValue"));
    }

    public void testExtractOverloadedParameter() {
        Method[] methods = TestClass.class.getMethods();
        List matchMethods = new ArrayList();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals("overloadedMethod")) {
                matchMethods.add(methods[i]);
            }
        }
        assertTrue("Found two overloaded methods", matchMethods.size() == 2);
        boolean foundBoolean = false;
        boolean foundInt = false;
        for (int i = 0; i < 2; i++) {
            Method method = (Method) matchMethods.get(i);
            Class[] paramTypes = method.getParameterTypes();
            assertTrue("only one parameter found", paramTypes.length == 1);
            assertTrue("It has to be either boolean or int",
                    (paramTypes[0] == Integer.TYPE) ||
                    (paramTypes[0] == Boolean.TYPE));
            String[] params = ParamNameExtractor.getParameterNamesFromDebugInfo(method);
            assertTrue("Only parameter found", params.length == 1);
            if (paramTypes[0] == Integer.TYPE) {
                if (foundInt) { //already found such method so something is wrong
                    fail("It is wrong type, should not be int");
                }else {
                    foundInt = true;
                }
                assertTrue("parameter is 'intValue'", params[0].equals("intValue"));
            } else if (paramTypes[0] == Boolean.TYPE) {
                if (foundBoolean) {
                    fail("It is wrong type, should not be boolean");
                }else {
                    foundBoolean = true;
                }
                assertTrue("parameter is 'boolValue'", params[0].equals("boolValue"));
            }
        }
    }

    class TestClass {
        public void nonOverloadMethod(int intValue, boolean boolValue) {
        }

        public void overloadedMethod(int intValue) {
        }

        public void overloadedMethod(boolean boolValue) {
        }
    }
}
