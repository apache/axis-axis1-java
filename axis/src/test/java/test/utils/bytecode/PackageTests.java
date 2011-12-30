package test.utils.bytecode;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Description
 * User: pengyu
 * Date: Sep 12, 2003
 * Time: 11:40:05 PM
 * 
 */
public class PackageTests {
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("All axis.utils.cache tests");
        suite.addTest(TestParamNameExtractor.suite());
        suite.addTest(TestParamReader.suite());
        suite.addTest(TestChainedParamReader.suite());
        return suite;
    }
}

