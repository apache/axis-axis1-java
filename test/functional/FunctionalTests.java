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
        
        // test of the TCP transport
        // ... should be removed?
        suite.addTestSuite(TestTCPEcho.class);
        
        // transport sample test
        suite.addTestSuite(TestTransportSample.class);

        // stock sample test
        suite.addTestSuite(TestStockSample.class);

        // bid-buy test
        suite.addTestSuite(TestBidBuySample.class);

        return suite;
    }
}
