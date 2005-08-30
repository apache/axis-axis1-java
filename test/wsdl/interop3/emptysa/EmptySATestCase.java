/**
 * EmptySATestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.interop3.emptysa;

import java.net.URL;

public class EmptySATestCase extends junit.framework.TestCase {
    
    
    public static URL url = null;
    
    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            url = new URL(args[0]);
        }
        junit.textui.TestRunner.run(new junit.framework.TestSuite(EmptySATestCase.class));
    } // main
    
    
    public EmptySATestCase(java.lang.String name) throws Exception {
        super(name);
        if (url == null) {
            url = new URL(new EmptySALocator().getSoapInteropEmptySAPortAddress());
        }
        
    }
    public void test1SoapInteropEmptySAPortEchoString() throws Exception {
        test.wsdl.interop3.emptysa.SoapInteropEmptySAPortType binding;
        try {
            binding = new test.wsdl.interop3.emptysa.EmptySALocator().getSoapInteropEmptySAPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        java.lang.String value = null;
        String expected = "empty SOAP Action";
        value = binding.echoString(expected);
        
        // validate results
        assertEquals(expected, value);
    }

}
