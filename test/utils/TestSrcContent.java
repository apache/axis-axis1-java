package test.utils;

import java.io.File;
import java.io.FileInputStream;

import java.text.MessageFormat;

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.axis.utils.JavaUtils;

/**
 * This TestCase verifies that content of the source files adheres
 * to certain coding practices by looking for "illegal" strings:
 *
 * - Verify that Log4J logger is not being used directly
 *   ("org.apache.log4j" is not in source files).
 *
 * - !! Someday: look for System.out.println .... !!
 *
 *
 * To add new strings to "avoid", search for and append to the
 * private attribute 'avoidStrings'.
 *
 * Based on code in TestMessages.java.
 */
public class TestSrcContent extends TestCase {
    public TestSrcContent(String name) {
        super(name);
    } // ctor

    public static Test suite() {
        return new TestSuite(TestSrcContent.class);
    }

    /**
     * If this test is run from xml-axis/java, then walk through the source
     * tree (xml-axis/java/src), calling checkFile for each file.
     */
    public void testSourceFiles() {
        String baseDir = System.getProperty("user.dir");
        File   srcDir = new File(baseDir, "src");

        if (srcDir.exists() && !walkTree(srcDir)) {
            throw new AssertionFailedError("Unexpected source file content");
        }
    } // testSourceFiles

    /**
     * Walk the source tree
     */
    private boolean walkTree(File srcDir) {
        boolean cleanWalk = true;

        File[] files = srcDir.listFiles();
        for (int i = 0; i < files.length; ++i) {

            // beware 'shortcuts' in logic operations...
            if (files[i].isDirectory()) {
                cleanWalk = walkTree(files[i]) && cleanWalk;
            }
            else if (files[i].getName().endsWith(".java")) {
                cleanWalk = checkFile(files[i]) && cleanWalk;
            }
        }

        return cleanWalk;
    } // walkTree

    /**
     * Check for the following in the input file:
     *     string "org.apache.log4j.Category" in file.
     */
    private static final String avoidStrings[] =
        {
            "org.apache.log4j"
        };

    private boolean checkFile(File file) {
        boolean cleanFile = true;

        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            String string = new String(bytes);

            for (int i = 0; i < avoidStrings.length; i++) {
                if (string.indexOf(avoidStrings[i]) >= 0) {
                    System.out.println(file.getPath() + ": Unexpected '" + avoidStrings[i]);
                    cleanFile = false;
                }
            }
        }
        catch (Throwable t) {
            System.out.println(file.getPath() + ": " + t.getMessage());
            cleanFile = false;
        }

        return cleanFile;
    } // checkFile
}
