package test.outparams;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 *  Test package for output params
 */
public class PackageTests extends TestCase {

    public PackageTests(String name) {
        super(name);
    }

    public static Test suite() throws Exception {
        TestSuite suite = new TestSuite();

        suite.addTestSuite(TestOutParams.class);

        return suite;
    }
}
