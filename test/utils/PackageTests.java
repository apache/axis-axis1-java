package test.utils;

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
        TestSuite suite = new TestSuite("All axis.utils tests");

        // suite.addTest(TestOptions.suite()); //comment out for now - this
        // class is a mess
        suite.addTest(TestQName.suite());
        suite.addTest(TestJavaUtils.suite());
        suite.addTest(TestXMLUtils.suite());
        suite.addTest(TestMessages.suite());
        suite.addTest(TestSrcContent.suite());

        return suite;
    }
}
