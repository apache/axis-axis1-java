package test.utils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
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
	AxisClassLoader actual = AxisClassLoader.getClassLoader();
        assertTrue("Did not get AxisClassLoader", expect instanceof AxisClassLoader);
        assertEquals("AxisClassLoader returned two different instances.", expect, actual);
    }

    public void testRegisterClassNameClass()
    {
	AxisClassLoader acl = AxisClassLoader.getClassLoader();
        java.util.Stack stack = new java.util.Stack(); //Stack was chosen arbitrarily
        Class clazz = stack.getClass();
        acl.registerClass("myStack",clazz);
        assertTrue("myStack is not registered properly", acl.isClassRegistered("myStack"));
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
        assertTrue("anotherStack was not properly registered.", acl.isClassRegistered("anotherStack"));
    }

    public void testDeregisterClass()
    {
	AxisClassLoader acl = AxisClassLoader.getClassLoader();
        java.util.Stack stack = new java.util.Stack(); //Stack was chosen arbitrarily
        Class clazz = stack.getClass();
        acl.registerClass("myStack",clazz);
        assertTrue("myStack was not properly registered.", acl.isClassRegistered("myStack"));
        acl.deregisterClass("myStack");
        if (acl.isClassRegistered("myStack"))
        {
            fail("myStack class should have been deregistered.");
        } 
    }

    public void testLoadClass() throws ClassNotFoundException
    {
	AxisClassLoader acl = AxisClassLoader.getClassLoader();
        Class clazz = acl.loadClass("java.util.BitSet"); //BitSet was chosen arbitrarily 
        assertTrue("Could not properly load the class \"java.util.BitSet\"", clazz.getName().equals("java.util.BitSet"));

        java.util.Stack stack = new java.util.Stack(); //Stack was chosen arbitrarily
        Class clazz1 = stack.getClass();
        acl.registerClass("myStack",clazz1);
        Class klass = acl.loadClass("myStack");
        assertTrue("The myStack class was not \"java.util.Stack\"", klass.getName().equals("java.util.Stack"));
    }

    public void testLookup() throws ClassNotFoundException
    {
	AxisClassLoader acl = AxisClassLoader.getClassLoader();
        JavaClass jc = acl.lookup("java.util.BitSet"); //BitSet was chosen arbitrarily 
        Class c = jc.getJavaClass(); 
        assertTrue("The class is not the expected \"java.util.BitSet\", instead it was \"" + c.getName() + "\"",
                   c.getName().equals("java.util.BitSet"));
    }
}
