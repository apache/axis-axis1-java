package test.wsdd;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.TestResult;

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
        suite.addTestSuite(TestBadWSDD.class);
        suite.addTestSuite(TestAdminService.class);

        return suite;
    }
    
    public static void main(String[] args) throws Exception {
        PackageTests tester = new PackageTests("test");
        TestResult testResult = new TestResult();
        tester.suite().run(testResult);
    }
}
