package test.jaxrpc;

/**
 * SAAJ PackageTests tests
 */
public class PackageTests extends junit.framework.TestCase {
    public PackageTests(String name) {
        super(name);
    }

    public static junit.framework.Test suite() throws Exception {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();
        suite.addTestSuite(test.jaxrpc.TestSOAPService.class);
        suite.addTestSuite(test.jaxrpc.TestAxisClient.class);
        return suite;
    }
}

