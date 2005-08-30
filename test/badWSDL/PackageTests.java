package test.badWSDL;

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
        TestSuite suite = new TestSuite("All bad WSDL tests");

        suite.addTest(WSDL2JavaFailuresTestCase.suite());
        return suite;
    }
}
