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

public class TestMessages extends TestCase {
    public TestMessages(String name) {
        super(name);
    } // ctor

    public static Test suite() {
        return new TestSuite(TestMessages.class);
    }

    public void testAllMessages() {
        String arg0 = "arg0";
        String arg1 = "arg1";
        String[] args = {arg0, arg1, "arg2"};
        Enumeration keys = JavaUtils.getMessageResourceBundle().getKeys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            try {
                String message = JavaUtils.getMessage(key);
                message = JavaUtils.getMessage(key, arg0);
                message = JavaUtils.getMessage(key, arg0, arg1);
                message = JavaUtils.getMessage(key, args);
            }
            catch (IllegalArgumentException iae) {
                throw new AssertionFailedError("Test failure on key = " + key + ":  " + iae.getMessage());
            }
        }
    } // testAllMessages

    public void testTestMessages() {
        try {
            String message = JavaUtils.getMessage("test00");
            String expected = "...";
            assertTrue("expected (" + expected + ") got (" + message + ")", expected.equals(message));
            message = JavaUtils.getMessage("test00", new String[0]);
            assertTrue("expected (" + expected + ") got (" + message + ")", expected.equals(message));
            message = JavaUtils.getMessage("test00", new String[] {"one", "two"});
            assertTrue("expected (" + expected + ") got (" + message + ")", expected.equals(message));
            message = JavaUtils.getMessage("test01");
            expected = ".{0}.";
            assertTrue("expected (" + expected + ") got (" + message + ")", expected.equals(message));
            message = JavaUtils.getMessage("test01", "one");
            expected = ".one.";
            assertTrue("expected (" + expected + ") got (" + message + ")", expected.equals(message));
            message = JavaUtils.getMessage("test01", new String[0]);
            expected = ".{0}.";
            assertTrue("expected (" + expected + ") got (" + message + ")", expected.equals(message));
            message = JavaUtils.getMessage("test01", new String[] {"one"});
            expected = ".one.";
            assertTrue("expected (" + expected + ") got (" + message + ")", expected.equals(message));
            message = JavaUtils.getMessage("test01", new String[] {"one", "two"});
            expected = ".one.";
            assertTrue("expected (" + expected + ") got (" + message + ")", expected.equals(message));
            message = JavaUtils.getMessage("test02");
            expected = "{0}, {1}.";
            assertTrue("expected (" + expected + ") got (" + message + ")", expected.equals(message));
            message = JavaUtils.getMessage("test02", new String[0]);
            expected = "{0}, {1}.";
            assertTrue("expected (" + expected + ") got (" + message + ")", expected.equals(message));
            message = JavaUtils.getMessage("test02", new String[] {"one"});
            expected = "one, {1}.";
            assertTrue("expected (" + expected + ") got (" + message + ")", expected.equals(message));
            message = JavaUtils.getMessage("test02", new String[] {"one", "two"});
            expected = "one, two.";
            assertTrue("expected (" + expected + ") got (" + message + ")", expected.equals(message));
            message = JavaUtils.getMessage("test03", new String[] {"one", "two", "three"});
            expected = ".three, one, two.";
            assertTrue("expected (" + expected + ") got (" + message + ")", expected.equals(message));
            message = JavaUtils.getMessage("test04", new String[] {"one", "two", "three", "four", "five", "six"});
            expected = ".one two three ... four three five six.";
            assertTrue("expected (" + expected + ") got (" + message + ")", expected.equals(message));
        }
        catch (Throwable t) {
            throw new AssertionFailedError("Test failure:  " + t.getMessage());
        }
    } // testTestMessages

    private String errors = "";

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

    private void checkMessages(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            String string = new String(bytes);
            int index = string.indexOf("JavaUtils.getMessage(");
            while (index >= 0) {
                string = string.substring(index + 21);
                String[] msgArgs = args(string);
                String key = msgArgs[0].substring(1, msgArgs[0].length() - 1);
                String value = null;
                try {
                    value = JavaUtils.getMessage(key);
                }
                catch (Throwable t) {
                    errors = errors + "File:  " + file.getPath() + " " + t.getMessage() + '\n';
                }
                int realParms = count(value);
                int expectedParms = msgArgs.length - 1;
                if (realParms != expectedParms) {
                    errors = errors + "File:  " + file.getPath() + " " + key + " has " + realParms + " parameters, " + expectedParms + " expected.\n";
                }
                index = string.indexOf("JavaUtils.getMessage(");
            }
        }
        catch (Throwable t) {
            errors = errors + "File:  " + file.getPath() + " " + t.getMessage() + '\n';
        }
    } // checkMessages

    private String[] args (String string) {
        int innerParens = 0;
        Vector args = new Vector();
        String arg = "";
        while (true) {
            if (string.startsWith("\"")) {
                String quote = readQuote(string);
                arg = arg + quote;
                string = string.substring(quote.length());
            }
            else if (string.startsWith("'")) {
                arg = arg + string.substring(0, 2);
                string = string.substring(2);
            }
            else if (string.startsWith(",")) {
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
                ++innerParens;
                arg = arg + '(';
                string = string.substring(1);
            }
            else if (string.startsWith(")")) {
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
                if (!Character.isWhitespace(string.charAt(0))) {
                    arg = arg + string.charAt(0);
                }
                string = string.substring(1);
            }
        }
    } // args

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
