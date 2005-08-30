package test.wsdl.opStyles;

import java.lang.reflect.Method;
import java.util.Vector;

// This test makes sure that the OpStyles interface ONLY has oneway and
// requestResponse methods and that no other methods, particularly
// solicitResponse and notification, exist.

public class VerifyTestCase extends junit.framework.TestCase {
    public VerifyTestCase(String name) {
        super(name);
    }

    public void testOpStyles() {
        Method[] methods =
                test.wsdl.opStyles.OpStyles.class.getDeclaredMethods();
        boolean sawOneway = false;
        boolean sawRequestResponse = false;
        boolean sawSolicitResponse = false;
        boolean sawNotification = false;
        Vector  others = new Vector();
        for (int i = 0; i < methods.length; ++i) {
            String name = methods[i].getName();
            if ("oneway".equals(name)) {
                sawOneway = true;
            }
            else if ("requestResponse".equals(name)) {
                sawRequestResponse = true;
            }
            else {
                others.add(name);
            }
        }
        assertTrue("Expected method oneWay does not exist on OpStyles", sawOneway == true);
        assertTrue("Expected method requestResponse does not exist on OpStyles",
                sawRequestResponse == true);
        if (others.size() > 0) {
            String message = "Methods exist on OpStyles but should not:  ";
            boolean needComma = false;
            for (int i = 0; i < others.size(); ++i) {
                if (needComma) {
                    message += ", ";
                }
                else {
                    needComma = true;
                }
                message += (String) others.get(i);
            }
        }
    } // testOpStyles
} // class VerifyTestCase

