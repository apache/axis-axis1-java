package test.chains;

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
        TestSuite suite = new TestSuite("All axis Chain tests");

        suite.addTest(TestSimpleChain.suite());
        suite.addTest(TestChainFault.suite());
        return suite;
    }
}
