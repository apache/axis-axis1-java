package test.utils.bytecode;
import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.io.IOException;

import org.apache.axis.utils.bytecode.ParamReader;

/**
 * Description
 * User: pengyu
 * Date: Sep 12, 2003
 * Time: 11:51:28 PM
 * 
 */
public class TestParamReader  extends TestCase{
    private TestDerivedClass test =null;
    private ParamReader reader;
    public TestParamReader(String name) {
        super(name);
    }
    public static Test suite() {
        return new TestSuite(TestParamReader.class);
    }

    protected void setup() {
        test = this.new TestDerivedClass(1);
    }

    public void testGetMethodParameters(){
        try {
            reader = new ParamReader(TestDerivedClass.class);
        } catch (IOException e) {
            fail("failed to setup paramreader:" + e.getMessage());
        }
        assertTrue("should not be null", reader != null);
        //first get method1
        try {
            Method method1 = TestDerivedClass.class.getMethod("method1", new Class[] {Boolean.TYPE});
            String [] params = reader.getParameterNames(method1);
            assertTrue("one parameter only",params.length == 1);
            assertTrue("It is 'boolValue'", params[0].equals("boolValue"));

            Method method2 = TestDerivedClass.class.getMethod("method2", new Class[] {Boolean.TYPE});
            params = reader.getParameterNames(method2);
            assertTrue("one parameter only",params.length == 1);
            assertTrue("It is 'boolValue'", params[0].equals("boolValue"));
            method2= TestDerivedClass.class.getMethod("method2", new Class[] {Integer.TYPE});
            params = reader.getParameterNames(method2);
            assertTrue("one parameter only",params.length == 1);
            assertTrue("It is 'intValue'", params[0].equals("intValue"));

            Method method3 = TestDerivedClass.class.getMethod("subClassInherit", new Class[] {Integer.TYPE});
            params = reader.getParameterNames(method3);
            assertTrue("It should not find inherited method", params == null);
        } catch (NoSuchMethodException e) {
            fail(e.toString());
        } catch (SecurityException e) {
            fail(e.toString());
        }

    }

    public void testGetConstructorParameters() {
        try {
            reader = new ParamReader(TestDerivedClass.class);
            assertTrue("should not be null", reader != null);
            Constructor ctor = TestDerivedClass.class.getConstructor(new Class[] {
                TestParamReader.class, Integer.TYPE});

            String [] params = reader.getParameterNames(ctor);
            assertTrue("params is not null" , params.length == 2);
            assertTrue("param name is 'in'", params[1].equals("in"));
        }
        catch (IOException e) {
            fail("failed to setup paramreader:" + e.getMessage());
        }
        catch (NoSuchMethodException e) {
            fail(e.getMessage());
        }

    }

    public static void main(String [] arg) {
        TestParamReader t = new TestParamReader("");
        t.testGetConstructorParameters();
    }
    class TestBaseClass {
        public void subClassInherit(int intValue) {
        }
    }
    class TestDerivedClass extends TestBaseClass{
        public TestDerivedClass() {
        }
        public TestDerivedClass(int in) {
        }
        public void method1(boolean boolValue) {
        }
        public void method2(int intValue) {
        }
        public void method2(boolean boolValue) {
        }
    }
}
