package test.utils.cache;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 */
public class PackageTests
{
    public static void main (String[] args) {
            junit.textui.TestRunner.run (suite());
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite("All axis.utils.cache tests");

        suite.addTest(TestJavaClass.suite());
        suite.addTest(TestJavaMethod.suite());

        return suite;
    }
}
