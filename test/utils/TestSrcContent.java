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

import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.MalformedPatternException;

import org.apache.axis.utils.Messages;

/**
 * This TestCase verifies that content of the source files adheres
 * to certain coding practices by matching regular expressions
 * (string patterns):
 *
 * - Verify that Log4J logger is not being used directly
 *   ("org.apache.log4j" is not in source files).
 *
 * - Verify that System.out.println is not used except
 *   in wsdl to/from java tooling.
 *
 * - Verify that log.info(), log.warn(), log.error(), and log.fatal()
 *   use Messages.getMessage() (i18n).
 *
 * - Verify that exceptions are created with Messages.getMessage() (i18n).
 *
 * To add new patterns, search for and append to the
 * private attribute 'avoidPatterns'.
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

    private static final String LS = System.getProperty("line.separator");

    private String errors = "";

    /**
     * If this test is run from xml-axis/java, then walk through the source
     * tree (xml-axis/java/src), calling checkFile for each file.
     */
    public void testSourceFiles() {
        String baseDir = System.getProperty("user.dir");
        File   srcDir = new File(baseDir, "src");

        if (srcDir.exists()) {
            walkTree(srcDir);
        }

        if (!errors.equals("")) {
            throw new AssertionFailedError(errors);
        }
    } // testSourceFiles


    /**
     * Walk the source tree
     */
    private void walkTree(File srcDir) {
        File[] files = srcDir.listFiles();
        for (int i = 0; i < files.length; ++i) {
            if (files[i].isDirectory()) {
                walkTree(files[i]);
            }
            else {
                checkFile(files[i]);
            }
        }
    } // walkTree


    static private class FileNameContentPattern
    {
        private PatternCompiler compiler = new Perl5Compiler();
        private PatternMatcher matcher = new Perl5Matcher();

        private Pattern namePattern = null;
        private Pattern contentPattern = null;
        private boolean expectContent = true;

        FileNameContentPattern(String namePattern,
                               String contentPattern,
                               boolean expectContentInFile)
        {
            try {
                this.namePattern = compiler.compile(namePattern);
                this.contentPattern = compiler.compile(contentPattern);
                this.expectContent = expectContentInFile;
            }
            catch (MalformedPatternException e) {
                throw new AssertionFailedError(e.getMessage());
            }
        }

        /**
         * This is not a match IFF
         *  - the name matches, AND
         *  - the content is not as expected
         */
        boolean noMatch(String name, String content)
        {
            return
                matcher.matches(name, namePattern) &&
                matcher.contains(content, contentPattern) != expectContent;
        }

        String getContentPattern() { return contentPattern.getPattern(); }

        boolean getExpectContent() { return expectContent; }
    };

    /**
     * Patterns to be checked. Each pattern has three parameters:
     *   (i) a pattern that matches filenames that are to be checked,
     *  (ii) a pattern to be searched for in the chosen files
     * (iii) whether the pattern is to be allowed (typically false indicating
     *       not allowed)
     * See the Axis Developer's Guide for more information.
     */
    private static final FileNameContentPattern avoidPatterns[] =
        {
            //**
            //** For escape ('\'), remember that Java gets first dibs..
            //** so double-escape for pattern-matcher to see it.
            //**

            // Verify that java files do not use Log4j
            //
            new FileNameContentPattern(".+\\.java",
                                       "org\\.apache\\.log4j", false),

            // Verify that axis java files do not use System.out.println
            // or System.err.println, except:
            //   - utils/tcpmon.java
            //   - providers/BSFProvider.java
            //   - utils/CLArgsParser.java
            //   - Version.java
            //   - tooling in 'org/apache/axis/wsdl'
            //
            new FileNameContentPattern(".+([\\\\/])"
                                       + "java\\1src\\1org\\1apache\\1axis\\1"
                                       + "(?!utils\\1tcpmon\\.java"
                                       + "|providers\\1BSFProvider\\.java"
                                       + "|utils\\1CLArgsParser\\.java"
                                       + "|transport\\1jms\\1SimpleJMSListener\\.java"
                                       + "|Version\\.java"
                                       + "|wsdl\\1)"
                                       + "([a-zA-Z0-9_]+\\1)*"
                                       + "[^\\\\/]+\\.java",
                                       "System\\.(out|err)\\.println", false),

            // Verify that internationalization is being used properly
            // with logger.  Exceptions:
            //   - all log.debug calls
            //   - client/AdminClient.java
            //   - utils/tcpmon.java
            //   - utils/Admin.java
            //   - handlers/LogMessage.java
            //   - tooling in 'org/apache/axis/wsdl'
            //
            new FileNameContentPattern(".+([\\\\/])"
                                       + "java\\1src\\1org\\1apache\\1axis\\1"
                                       + "(?!utils\\1tcpmon\\.java"
                                       + "|client\\1AdminClient\\.java"
                                       + "|utils\\1Admin\\.java"
                                       + "|handlers\\1LogMessage\\.java"
                                       + "|wsdl\\1)"
                                       + "([a-zA-Z0-9_]+\\1)*"
                                       + "[^\\\\/]+\\.java",
                                       "log\\.(info|warn|error|fatal)"
                                       + "[ \\t]*\\("
                                       + "(?![ \\t]*Messages\\.getMessage)",
                                       false),

            // Verify that exceptions are built with messages.

            new FileNameContentPattern(".+([\\\\/])"
                                       + "java\\1src\\1org\\1apache\\1axis\\1"
                                       + "([a-zA-Z0-9_]+\\1)*"
                                       + "[^\\\\/]+\\.java",
                                       "new[ \\t]+[a-zA-Z0-9_]*"
                                       + "Exception\\(\\)",
                                       false),

            // Verify that we don't explicitly create NPEs.

            new FileNameContentPattern(".+([\\\\/])"
                                       + "java\\1src\\1org\\1apache\\1axis\\1"
                                       + "([a-zA-Z0-9_]+\\1)*"
                                       + "[^\\\\/]+\\.java",
                                       "new[ \\t]+"
                                       + "NullPointerException",
                                       false),

        };

    private void checkFile(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            String content = new String(bytes);

            for (int i = 0; i < avoidPatterns.length; i++) {
                if (avoidPatterns[i].noMatch(file.getPath(), content)) {
                //                if (content.indexOf(avoidStrings[i]) >= 0) {
                    errors = errors
                        + "File: " + file.getPath() + ": "
                        + (avoidPatterns[i].getExpectContent()
                           ? "Expected: "
                           : "Unexpected: ")
                        + avoidPatterns[i].getContentPattern()
                        + LS;
                }
            }
        }
        catch (Throwable t) {
            errors = errors
                + "File: " + file.getPath()
                + ": " + t.getMessage()
                + LS;
        }
    } // checkFile
}
