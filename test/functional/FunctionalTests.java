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

        //suite.addTestSuite(TestTCPEcho.class);
        suite.addTestSuite(TestHTTPEcho.class);

        return suite;
    }
}
