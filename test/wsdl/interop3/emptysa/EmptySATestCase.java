package test.wsdl.interop3.emptysa;

//import test.wsdl.interop3.emptysa.step6.EmptySALocator;

/*
    <!-- SOAP Builder's round III web services          -->
    <!-- interoperability testing:  import1             -->
    <!-- (see http://www.whitemesa.net/r3/plan.html)    -->
    <!-- Step 1.  Start with predefined WSDL            -->
    <!-- Step 2.  Generate client from predefined WSDL  -->
    <!-- Step 3.  Test generated client against         -->
    <!--          pre-built server                      -->
    <!-- Step 4.  Generate server from predefined WSDL  -->
    <!-- Step 5.  Test generated client against         -->
    <!--          generated server                      -->
    <!-- Step 6.  Generate second client from           -->
    <!--          generated server's WSDL (some clients -->
    <!--          can do this dynamically)              -->
    <!-- Step 7.  Test second generated client against  -->
    <!--          generated server                      -->
    <!-- Step 8.  Test second generated client against  -->
    <!--          pre-built server                      -->
*/

public class EmptySATestCase extends junit.framework.TestCase {
    public EmptySATestCase(String name) {
        super(name);
    }

    public void testStep3() {
        SoapInteropEmptySAPortType binding;
        try {
            binding = new EmptySALocator().getSoapInteropEmptySAPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            String value = "Empty SA Echo Test step 3";
            assertEquals("EmptySA step 3 returned something weird!", value, binding.echoString(value));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void testStep5() {
        SoapInteropEmptySAPortType binding;
        try {
            binding = new EmptySALocator().getSoapInteropEmptySAPort(new java.net.URL("http://localhost:8080/axis/services/EmptySA"));
        }
        catch (Throwable t) {
            throw new junit.framework.AssertionFailedError("Throwable caught: " + t);
        }
        assertTrue("binding is null", binding != null);

        try {
            String value = "Empty SA Echo Test step 5";
            assertEquals("EmptySA step 5 returned something weird!", value, binding.echoString(value));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    /*
    public void testStep7() {
        test.wsdl.interop3.emptysa.step6.SoapInteropEmptySAPortType binding;
        try {
            binding = new test.wsdl.interop3.emptysa.step6.EmptySALocator().getSoapInteropEmptySAPort();
        }
        catch (Throwable t) {
            throw new junit.framework.AssertionFailedError("Throwable caught: " + t);
        }
        assertTrue("binding is null", binding != null);

        try {
            String value = null;
            value = binding.echoString(new String());
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

/* doesn't work yet
    public void testStep8() {
       test.wsdl.interop3.import1.step6.definitions.SoapInteropImport1PortType binding;
        try {
            binding = new SoapInteropImport1PortTypeServiceLocator().getSoapInteropImport1Port(new java.net.URL("http://mssoapinterop.org/stkV3/wsdl/import2.wsdl"));
        }
        catch (Throwable t) {
            throw new junit.framework.AssertionFailedError("Throwable caught: " + t);
        }
        assertTrue("binding is null", binding != null);

        try {
            java.lang.String value = null;
            value = binding.echoString(new java.lang.String());
        }
        catch (java.rmi.RemoteException re) {
            re.printStackTrace();
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }
*/

    public static void main(String[] args) {
        junit.textui.TestRunner.run(new junit.framework.TestSuite(EmptySATestCase.class));
    } // main

}

