package test.encoding;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * soapenc's PackageTests tests multiple floating point
 * deserialization classes (float and double, primitive and object).
 */
public class PackageTests extends TestCase
{
    public PackageTests(String name)
    {
        super(name);
    }

    public static Test suite() throws Exception
    {
        TestSuite suite = new TestSuite();

        suite.addTestSuite(TestDerivatedBeanSerializer.class);
        suite.addTestSuite(TestDeser.class);
        suite.addTestSuite(TestDeser1999.class);
        suite.addTestSuite(TestDeser2000.class);
        suite.addTestSuite(TestDeser2001.class);
        suite.addTestSuite(TestSer.class);
        suite.addTestSuite(TestString.class);
        suite.addTestSuite(TestHrefs.class);
        suite.addTestSuite(TestBody.class);
        suite.addTestSuite(TestDOM.class);
        suite.addTestSuite(TestArrayListConversions.class);
        suite.addTestSuite(TestXsiType.class);
        suite.addTestSuite(TestOutputter.class);
        suite.addTestSuite(TestAttributes.class);
        suite.addTestSuite(TestBeanDeser.class);
        suite.addTestSuite(TestBeanDeser2.class);
        suite.addTestSuite(TestRoundTrip.class);
        suite.addTestSuite(TestOmittedValues.class);
        suite.addTestSuite(TestMultiRefIdentity.class);
        suite.addTestSuite(TestArray.class);
        suite.addTestSuite(TestCircularRefs.class);
        suite.addTestSuite(TestAutoTypes.class);
        suite.addTestSuite(EncodingTest.class);
        return suite;
    }
}
