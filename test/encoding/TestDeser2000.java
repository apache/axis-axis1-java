package test.encoding;

import org.apache.axis.Constants;
import junit.framework.TestCase;

/** 
 * Test deserialization of SOAP responses
 */
public class TestDeser2000 extends TestDeser {

    public TestDeser2000(String name) {
        super(name, Constants.URI_2000_SCHEMA_XSI, 
                    Constants.URI_2000_SCHEMA_XSD);
    }
}
