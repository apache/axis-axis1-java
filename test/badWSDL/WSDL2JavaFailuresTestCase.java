package test.badWSDL;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.axis.wsdl.toJava.Emitter;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * This test grabs each WSDL file in the directory and runs WSDL2Java against them.
 * They should all fail.  If one does not, this test fails.
*/

public class WSDL2JavaFailuresTestCase extends TestCase {
    private static final String badWSDL = "test" + File.separatorChar +
            "badWSDL";
    private String wsdl;

    public WSDL2JavaFailuresTestCase(String wsdl) {
        super("testWSDLFailures");
        this.wsdl = wsdl;
    }

    /**
     * Create a test suite with a single test for each WSDL file in this
     * directory.
     */
    public static Test suite() {
        TestSuite tests = new TestSuite();
        String[] wsdls = getWSDLs();
        for (int i = 0; i < wsdls.length; ++i) {
            tests.addTest(new WSDL2JavaFailuresTestCase(badWSDL +
                    File.separatorChar + wsdls[i]));
        }
        return tests;
    } // suite

    /**
     * Get a list of all WSDL files in this directory.
     */
    private static String[] getWSDLs() {
        String[] wsdls = null;
        try {
            File failuresDir = new File(badWSDL);
            FilenameFilter fnf = new FilenameFilter()
            {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".wsdl");
                }
            };
            wsdls = failuresDir.list(fnf);
        }
        catch (Throwable t) {
            wsdls = null;
        }
        if (wsdls == null) {
            wsdls = new String[0];
        }
        return wsdls;
    } // getWSDLs

    /**
     * Call WSDL2Java on this WSDL file, failing if WSDL2Java succeeds.
     */
    public void testWSDLFailures() {
        boolean failed  = false;
        Emitter emitter = new Emitter();

        emitter.setTestCaseWanted(true);
        emitter.setHelperWanted(true);    
        emitter.setImports(true);
        emitter.setAllWanted(true);
        emitter.setServerSide(true);
        emitter.setSkeletonWanted(true);
        try {
            emitter.run(wsdl);
            failed = true;
        }
        catch (Throwable e) {
        }
        if (failed) {
            fail("WSDL2Java " + wsdl + " should have failed.");
        }
    } // testWSDLFailures
} // class WSDL2JavaFailuresTestCase

