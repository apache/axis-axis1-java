package test.components;

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
        TestSuite suite = new TestSuite("All axis Components tests");

        suite.addTest(TestUUID.suite());
        return suite;
    }
}
