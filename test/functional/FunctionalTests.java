package test.functional;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * soapenc's FunctionalTests test client/server interactions.
 */
public class FunctionalTests extends TestCase
{
    public FunctionalTests(String name)
    {
        super(name);
System.out.println("Creating FunctionalTests(name)");
    }

    public static Test suite() throws Exception
    {
System.out.println("Creating FunctionalTests suite.");
        TestSuite suite = new TestSuite();

        // misc (echo) test
        // BROKEN AS OF RIGHT NOW, 11:26 PST 20010531 -- RobJ
        // suite.addTestSuite(TestMiscSample.class);
        
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

        return suite;
    }
}
