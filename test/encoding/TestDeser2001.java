package test.encoding;

import org.apache.axis.Constants;
import junit.framework.TestCase;

/** 
 * Test deserialization of SOAP responses
 */
public class TestDeser2001 extends TestDeser {

    public TestDeser2001(String name) {
        super(name, Constants.URI_2001_SCHEMA_XSI, 
                    Constants.URI_2001_SCHEMA_XSD);
    }
}
