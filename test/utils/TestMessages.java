package test.utils;

import java.text.ParseException;

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

    public void testMessages() {
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
    } // testMessages
}
