package test.client;

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
        TestSuite suite = new TestSuite("All Axis Call tests");

        suite.addTest(TestCall.suite());
        suite.addTest(TestAsyncCall.suite());
        return suite;
    }
}
