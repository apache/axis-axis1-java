/**
 * StubHeaderServiceTestCase.java
 *
 * Test case. Add a SOAP header using the Stub APIs and make sure the
 * service returns a SOAP header that we can read.
 */

package test.wsdl.stubheaders;

import org.apache.axis.message.SOAPHeaderElement;

public class StubHeaderServiceTestCase extends junit.framework.TestCase {
    public StubHeaderServiceTestCase(java.lang.String name) {
        super(name);
    }
    public void test1StubHeaderServiceEcho() throws Exception {
        StubHeaderStub binding;
        try {
            binding = (test.wsdl.stubheaders.StubHeaderStub)
                          new StubHeaderServiceLocator().getStubHeaderService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Set header value via the Stub API
        binding.setHeader("http://test.org/inputheader", "headerin", "inputvalue");

        // Test operation
        java.lang.String value = null;
        value = binding.echo(new java.lang.String());

        // validate input header was echoed back
        assertEquals("Request header did not reach service", "inputvalue", value);

        // Check that getting the list of response headers works
        SOAPHeaderElement[] hdrs = binding.getResponseHeaders();
        assertEquals("List of response headers has the wrong number", 1, hdrs.length );

        // Get response header using ONLY the Stub API
        SOAPHeaderElement hdr = binding.getResponseHeader("http://test.org/outputheader", "headerout");
        assertNotNull("Cant find header 'headerout' in response", hdr);
        assertNotNull("Header object value is NULL", hdr.getObjectValue());
        assertEquals("Response header did not arrive as expected", "outputvalue", hdr.getObjectValue());

        // Everything is OK
    }

}
