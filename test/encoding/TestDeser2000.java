package test.encoding;

import org.apache.axis.Constants;

/** 
 * Test deserialization of SOAP responses
 */
public class TestDeser2000 extends TestDeser {

    public TestDeser2000(String name) {
        super(name, Constants.NS_URI_2000_SCHEMA_XSI, 
                    Constants.NS_URI_2000_SCHEMA_XSD);
    }
}
