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

import org.apache.axis.components.i18n.Messages;
import org.apache.axis.components.i18n.DefaultMessageBundle;


/**
 * This TestCase verifies:
 *   - the contents of axisNLS.properties for well-formedness, and
 *   - tests calls to Messages.getMessage.
 */
public class TestMessages extends TestCase {
    public TestMessages(String name) {
        super(name);
    } // ctor

    public static Test suite() {
        return new TestSuite(TestMessages.class);
    }

    /**
     * Call getMessage for each key in axisNLS.properties to make sure they are all well formed.
     */
    public void testAllMessages() {
        String arg0 = "arg0";
        String arg1 = "arg1";
        String[] args = {arg0, arg1, "arg2"};
        Enumeration keys = DefaultMessageBundle.getMessageResourceBundle().getKeys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            try {
                String message = Messages.getMessage(key);
                message = Messages.getMessage(key, arg0);
                message = Messages.getMessage(key, arg0, arg1);
                message = Messages.getMessage(key, args);
            }
            catch (IllegalArgumentException iae) {
                throw new AssertionFailedError("Test failure on key = " + key + ":  " + iae.getMessage());
            }
        }
    } // testAllMessages

    /**
     * Make sure the test messages come out as we expect them to.
     */
    public void testTestMessages() {
        try {
            String message = Messages.getMessage("test00");
            String expected = "...";
            assertTrue("expected (" + expected + ") got (" + message + ")", expected.equals(message));
            message = Messages.getMessage("test00", new String[0]);
            assertTrue("expected (" + expected + ") got (" + message + ")", expected.equals(message));
            message = Messages.getMessage("test00", new String[] {"one", "two"});
            assertTrue("expected (" + expected + ") got (" + message + ")", expected.equals(message));
            message = Messages.getMessage("test01");
            expected = ".{0}.";
            assertTrue("expected (" + expected + ") got (" + message + ")", expected.equals(message));
            message = Messages.getMessage("test01", "one");
            expected = ".one.";
            assertTrue("expected (" + expected + ") got (" + message + ")", expected.equals(message));
            message = Messages.getMessage("test01", new String[0]);
            expected = ".{0}.";
            assertTrue("expected (" + expected + ") got (" + message + ")", expected.equals(message));
            message = Messages.getMessage("test01", new String[] {"one"});
            expected = ".one.";
            assertTrue("expected (" + expected + ") got (" + message + ")", expected.equals(message));
            message = Messages.getMessage("test01", new String[] {"one", "two"});
            expected = ".one.";
            assertTrue("expected (" + expected + ") got (" + message + ")", expected.equals(message));
            message = Messages.getMessage("test02");
            expected = "{0}, {1}.";
            assertTrue("expected (" + expected + ") got (" + message + ")", expected.equals(message));
            message = Messages.getMessage("test02", new String[0]);
            expected = "{0}, {1}.";
            assertTrue("expected (" + expected + ") got (" + message + ")", expected.equals(message));
            message = Messages.getMessage("test02", new String[] {"one"});
            expected = "one, {1}.";
            assertTrue("expected (" + expected + ") got (" + message + ")", expected.equals(message));
            message = Messages.getMessage("test02", new String[] {"one", "two"});
            expected = "one, two.";
            assertTrue("expected (" + expected + ") got (" + message + ")", expected.equals(message));
            message = Messages.getMessage("test03", new String[] {"one", "two", "three"});
            expected = ".three, one, two.";
            assertTrue("expected (" + expected + ") got (" + message + ")", expected.equals(message));
            message = Messages.getMessage("test04", new String[] {"one", "two", "three", "four", "five", "six"});
            expected = ".one two three ... four three five six.";
            assertTrue("expected (" + expected + ") got (" + message + ")", expected.equals(message));
        }
        catch (Throwable t) {
            throw new AssertionFailedError("Test failure:  " + t.getMessage());
        }
    } // testTestMessages


    private static final String LS = System.getProperty("line.separator");

    private String errors = "";

    /**
     * If this test is run from xml-axis/java, then walk through the source
     * tree looking for all calls to Messages.getMessage.  For each of these
     * calls:
     * 1.  Make sure the message key exists in axisNLS.properties
     * 2.  Make sure the actual number of parameters (in axisNLS.properties)
     *     matches the excpected number of parameters (in the source code).
     */
    public void testForMissingMessages() {
        String baseDir = System.getProperty("user.dir");
        char sep = File.separatorChar;
        String srcDirStr = baseDir + sep + "src";

        File srcDir = new File(srcDirStr);
        if (srcDir.exists()) {
            walkTree(srcDir);
        }
        if (!errors.equals("")) {
            throw new AssertionFailedError(errors);
        }
    } // testForMissingMessages

    /**
     * Walk the source tree
     */
    private void walkTree(File srcDir) {
        File[] files = srcDir.listFiles();
        for (int i = 0; i < files.length; ++i) {
            if (files[i].isDirectory()) {
                walkTree(files[i]);
            }
            else if (files[i].getName().endsWith(".java")) {
                checkMessages(files[i]);
            }
        }
    } // walkTree

    /**
     * Check all calls to Messages.getMessages:
     * 1.  Make sure the message key exists in axisNLS.properties
     * 2.  Make sure the actual number of parameters (in axisNLS.properties) matches the
     *     excpected number of parameters (in the source code).
     */
    private void checkMessages(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            final String pattern = "Messages.getMessage(";
            String string = new String(bytes);
            while (true) {
                int index = string.indexOf(pattern);
                if (index < 0) break;

                // Bump the string past the pattern-string
                string = string.substring(index + pattern.length());

                // Get the arguments for the getMessage call
                String[] msgArgs = args(string);

                // The first argument is the key.
                // If the key is a literal string, check the usage.
                // If the key is not a literal string, accept the usage.
                if (msgArgs[0].startsWith("\"")) {
                    String key = msgArgs[0].substring(1, msgArgs[0].length() - 1);
                    
                    // Get the raw message
                    String value = null;
                    try {
                        value = Messages.getMessage(key);
                    }
                    catch (Throwable t) {
                        errors = errors + "File:  " + file.getPath() + " " + t.getMessage() + LS;
                    }
                    // The realParms count is the number of strings in the
                    // message of the form: {X} where X is 0..9
                    int realParms = count(value);
                    
                    // The providedParms count is the number of arguments to
                    // getMessage, minus the first argument (key).
                    int providedParms = msgArgs.length - 1;
                    if (realParms != providedParms) {
                        errors = errors + "File:  '" + file.getPath() + "', Key '" + key + "' specifies " + realParms + " {X} parameters, but " + providedParms + " parameter(s) provided." + LS;
                    }
                }
            }
        }
        catch (Throwable t) {
            errors = errors + "File:  " + file.getPath() + " " + t.getMessage() + LS;
        }
    } // checkMessages

    /**
     * For the given method call string, return the parameter strings.
     * This means that everything between the first "(" and the last ")",
     * and each "," encountered at the top level delimits a parameter.
     */
    private String[] args (String string) {
        int innerParens = 0;
        Vector args = new Vector();
        String arg = "";
        while (true) {
            if (string.startsWith("\"")) {

                // Make sure we don't look for the following characters within quotes:
                // , ' " ( )
                String quote = readQuote(string);
                arg = arg + quote;
                string = string.substring(quote.length());
            }
            else if (string.startsWith("'")) {

                // Make sure we ignore a quoted character
                arg = arg + string.substring(0, 2);
                string = string.substring(2);
            }
            else if (string.startsWith(",")) {

                // If we're at the top level (ie., not inside inner parens like:
                // (X, Y, new String(str, 0))), then we are seeing the end of an argument.
                if (innerParens == 0) {
                    args.add(arg);
                    arg = "";
                }
                else {
                    arg = arg + ',';
                }
                string = string.substring(1);
            }
            else if (string.startsWith("(")) {

                // We are stepping within a subexpression delimited by parens
                ++innerParens;
                arg = arg + '(';
                string = string.substring(1);
            }
            else if (string.startsWith(")")) {

                // We are either stepping out of a subexpression delimited by parens, or we
                // have reached the end of the parameter list.
                if (innerParens == 0) {
                    args.add(arg);
                    String[] argsArray = new String[args.size()];
                    args.toArray(argsArray);
                    return argsArray;
                }
                else {
                    --innerParens;
                    arg = arg + ')';
                    string = string.substring(1);
                }
            }
            else {

                // We aren't looking at any special character, just add it to the arg string
                // we're building.
                if (!Character.isWhitespace(string.charAt(0))) {
                    arg = arg + string.charAt(0);
                }
                string = string.substring(1);
            }
        }
    } // args

    /**
     * Collect a quoted string, making sure we really end when the string ends.
     */
    private String readQuote(String string) {
        String quote = "\"";
        string = string.substring(1);
        while (true) {
            int index = string.indexOf('"');
            if (index == 0 || string.charAt(index - 1) != '\\') {
                quote = quote + string.substring(0, index + 1);
                return quote;
            }
            else {
                quote = quote + string.substring(0, index + 1);
                string = string.substring(index);
            }
        }
    } // readQuote

    /**
     * Count the number of strings of the form {X} where X = 0..9.
     */
    private int count(String string) {
        int parms = 0;
        int index = string.indexOf("{");
        while (index >= 0) {
            try {
                char parmNumber = string.charAt(index + 1);
                if (parmNumber >= '0' && parmNumber <= '9' && string.charAt(index + 2) == '}') {
                    ++parms;
                }
                string = string.substring(index + 1);
                index = string.indexOf("{");
            } catch (Throwable t) {
            }
        }
        return parms;
    } // count
}
