package test.typedesc;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class PackageTests extends TestCase {

    public PackageTests(String name) {
        super(name);
    }

    public static Test suite() throws Exception {
        TestSuite suite = new TestSuite();

        suite.addTestSuite(TestTypeDescSynch.class);

        return suite;
    }
}
