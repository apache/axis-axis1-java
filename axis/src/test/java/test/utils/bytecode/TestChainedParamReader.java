package test.utils.bytecode;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

import org.apache.axis.utils.bytecode.ChainedParamReader;

/**
 * Description
 * User: pengyu
 * Date: Sep 13, 2003
 * Time: 10:59:00 PM
 * 
 */
public class TestChainedParamReader extends TestCase{
    private TestDerivedClass test =null;
    private ChainedParamReader reader;
    public TestChainedParamReader(String name) {
        super(name);
    }
    public static Test suite() {
        return new TestSuite(TestChainedParamReader.class);
    }

    protected void setup() {
        test = this.new TestDerivedClass(1);
    }

    public void testGetMethodParameters(){
        try {
            reader = new ChainedParamReader(TestDerivedClass.class);
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

        } catch (NoSuchMethodException e) {
            fail(e.toString());
        } catch (SecurityException e) {
            fail(e.toString());
        }

    }

    public void testGetConstructorParameters() {
        try {
            reader = new ChainedParamReader(TestDerivedClass.class);
            assertTrue("should not be null", reader != null);
            Constructor ctor = TestDerivedClass.class.getConstructor(new Class[] {
                TestChainedParamReader.class, Integer.TYPE});

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

    public void testGetInheritedMethodParameters() {
        try {
            reader = new ChainedParamReader(TestDerivedClass.class);
            Method method3 = TestDerivedClass.class.getMethod("subClassInherit", new Class[] {Integer.TYPE});
            String [] params = reader.getParameterNames(method3);
            assertTrue("It should find inherited method", params != null);
        } catch (IOException e) {
            fail("failed to setup paramreader:" + e.getMessage());
        } catch (NoSuchMethodException e) {
            fail(e.toString());
        }
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

