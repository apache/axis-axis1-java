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
        suite.addTestSuite(TestNormalizedString.class);
        suite.addTestSuite(TestToken.class);
        suite.addTestSuite(TestUnsignedLong.class);
        suite.addTestSuite(TestUnsignedInt.class);
        suite.addTestSuite(TestUnsignedShort.class);
        suite.addTestSuite(TestUnsignedByte.class);
        suite.addTestSuite(TestYearMonth.class);
        suite.addTestSuite(TestYear.class);
        suite.addTestSuite(TestMonth.class);
        suite.addTestSuite(TestMonthDay.class);
        suite.addTestSuite(TestDay.class);
        suite.addTestSuite(TestName.class);
        suite.addTestSuite(TestNCName.class);
        suite.addTestSuite(TestNMToken.class);
        suite.addTestSuite(TestDuration.class);
        return suite;
    }
}
