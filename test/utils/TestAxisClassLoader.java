package test.utils;

import junit.framework.*;
import org.apache.axis.utils.AxisClassLoader;
import org.apache.axis.utils.cache.JavaClass;

public class TestAxisClassLoader extends TestCase
{
    protected AxisClassLoader acl;

    public TestAxisClassLoader (String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestAxisClassLoader.class);
    }

    protected void setup() {
    }
    
    public void testGetClassLoaderNoArg()
    {
	AxisClassLoader expect = AxisClassLoader.getClassLoader();
	AxisClassLoader actual = AxisClassLoader.getClassLoader("<default_class_loader>");
        assert(expect instanceof AxisClassLoader);
        assertEquals(expect, actual);
    }
    
    public void testGetClassLoaderStringArg()
    {
        AxisClassLoader acl = AxisClassLoader.getClassLoader("newClassLoader");
        AxisClassLoader result = AxisClassLoader.getClassLoader("newClassLoader");
        if (!acl.equals(result))
        {
            fail("Test failure: these 2 AxisClassLoader references should refer to same instance.");
        }
    }

    public void testRemoveClassLoader()
    {
        AxisClassLoader acl = AxisClassLoader.getClassLoader("newClassLoader");
        AxisClassLoader.removeClassLoader("newClassLoader");
        AxisClassLoader result = acl.getClassLoader("newClassLoader");
        if (acl.equals(result))
        {
            fail("These 2 AxisClassLoader references should not refer to same instance.");
        }
    }

    public void testRegisterClassNameClass()
    {
	AxisClassLoader acl = AxisClassLoader.getClassLoader();
        java.util.Stack stack = new java.util.Stack(); //Stack was chosen arbitrarily
        Class clazz = stack.getClass();
        acl.registerClass("myStack",clazz);
        assert(acl.isClassRegistered("myStack"));
    }

    public void testIsClassRegistered()
    {
	AxisClassLoader acl = AxisClassLoader.getClassLoader();
        java.util.Stack stack = new java.util.Stack(); //Stack was chosen arbitrarily
        Class clazz = stack.getClass();
        acl.registerClass("anotherStack",clazz);
        if (acl.isClassRegistered("noStack"))
        {
            fail("Nonce class name should not be registered.");
        }
        assert(acl.isClassRegistered("anotherStack"));
    }

    public void testDeregisterClass()
    {
	AxisClassLoader acl = AxisClassLoader.getClassLoader();
        java.util.Stack stack = new java.util.Stack(); //Stack was chosen arbitrarily
        Class clazz = stack.getClass();
        acl.registerClass("myStack",clazz);
        assert(acl.isClassRegistered("myStack"));
        acl.deregisterClass("myStack");
        if (acl.isClassRegistered("myStack"))
        {
            fail("Class should have been deregistered.");
        } 
    }

    public void testLoadClass() throws ClassNotFoundException
    {
	AxisClassLoader acl = AxisClassLoader.getClassLoader();
        Class clazz = acl.loadClass("java.util.BitSet"); //BitSet was chosen arbitrarily 
        assert(clazz.getName().equals("java.util.BitSet"));

        java.util.Stack stack = new java.util.Stack(); //Stack was chosen arbitrarily
        Class clazz1 = stack.getClass();
        acl.registerClass("myStack",clazz1);
        Class klass = acl.loadClass("myStack");
        assert(klass.getName().equals("java.util.Stack"));
    }

    public void testLookup() throws ClassNotFoundException
    {
	AxisClassLoader acl = AxisClassLoader.getClassLoader();
        JavaClass jc = acl.lookup("java.util.BitSet"); //BitSet was chosen arbitrarily 
        Class c = jc.getJavaClass(); 
        assert(c.getName().equals("java.util.BitSet"));
    }
}
