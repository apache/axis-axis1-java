package test.functional;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Axis's FunctionalTests test client/server interactions.
 */
public class FunctionalTests extends TestCase
{
    public FunctionalTests(String name)
    {
        super(name);
    }

    public static Test suite() throws Exception
    {
        TestSuite suite = new TestSuite();

        // Echo test - end to end serialization and deserialization /
        // interop tests.
        suite.addTestSuite(TestEchoSample.class);
        
        // stock sample test
        // run this BEFORE ALL OTHER TESTS to minimize confusion;
        // this will run the JWS test first, and we want to know that
        // nothing else has been deployed
        suite.addTestSuite(TestStockSample.class);

        // TCP transport sample test
        suite.addTestSuite(TestTCPTransportSample.class);
        
        // file transport sample test
        suite.addTestSuite(TestTransportSample.class);

        // bid-buy test
        suite.addTestSuite(TestBidBuySample.class);

        // address book test
        suite.addTestSuite(TestAddressBookSample.class);

        // "Raw" echo service test.
        suite.addTestSuite(TestMiscSample.class);

        // Proxy service test.
        suite.addTestSuite(TestProxySample.class);

        return suite;
    }
}
