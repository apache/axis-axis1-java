package test.RPCDispatch;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * RPCDispatch's PackageTests tests
 */
public class PackageTests extends TestCase {

    public PackageTests(String name) {
        super(name);
    }

    public static Test suite() throws Exception {
        TestSuite suite = new TestSuite();

        suite.addTestSuite(TestRPC.class);
        suite.addTestSuite(TestSerializedRPC.class);

        return suite;
    }
}
