package test.wsdd;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Session tests
 */
public class PackageTests extends TestCase {

    public PackageTests(String name) {
        super(name);
    }

    public static Test suite() throws Exception {
        TestSuite suite = new TestSuite();

        suite.addTestSuite(TestGlobalConfiguration.class);
        suite.addTestSuite(TestScopeOption.class);
        suite.addTestSuite(TestOptions.class);
        suite.addTestSuite(TestUndeployment.class);
        suite.addTestSuite(TestStructure.class);

        return suite;
    }
}
