package test.encoding;

import org.apache.axis.Constants;

/** 
 * Test deserialization of SOAP responses
 */
public class TestDeser1999 extends TestDeser {

    public TestDeser1999(String name) {
        super(name, Constants.NS_URI_1999_SCHEMA_XSI, 
                    Constants.NS_URI_1999_SCHEMA_XSD);
    }
}
