package test.wsdl.interop3.compound1;

import test.wsdl.interop3.compound1.xsd.Document;

import java.net.URL;

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

public class Compound1TestCase extends junit.framework.TestCase {
    static URL url;

    public Compound1TestCase(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
    }

    public void testStep3() {
        SoapInteropCompound1Binding binding;
        try {
            if (url != null) {
                binding = new Compound1Locator().getSoapInteropCompound1Port(url);
            } else {
                binding = new Compound1Locator().getSoapInteropCompound1Port();
            }
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            Document doc = new Document();
            doc.setValue("some value");
            doc.setID("myID");
            Document newDoc = binding.echoDocument(doc);

            assertEquals("Step 3 IDs didn't match!", doc.getID(), newDoc.getID());
            assertEquals("Step 3 values didn't match!", doc.getValue(), newDoc.getValue());
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    /*
    public void testStep5() {
        SoapInteropCompound1Binding binding;
        try {
            binding = new Compound1Locator().getSoapInteropCompound1Port(new java.net.URL("http://localhost:8080/axis/services/SoapInteropImport1Port"));
        }
        catch (Throwable t) {
            throw new junit.framework.AssertionFailedError("Throwable caught: " + t);
        }
        assertTrue("binding is null", binding != null);

        try {
            Document doc = new Document();
            doc.setValue("some value");
            doc.setID("myID");
            Document newDoc = binding.echoDocument(doc);

            assertEquals("Step 5 IDs didn't match!", doc.getID(), newDoc.getID());
            assertEquals("Step 5 values didn't match!", doc.getValue(), newDoc.getValue());
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    /*
    public void testStep7() {
        test.wsdl.interop3.import1.step6.definitions.SoapInteropImport1PortType binding;
        try {
            binding = new SoapInteropImport1PortTypeServiceLocator().getSoapInteropImport1Port();
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
        if (args.length == 1) {
            try {
                url = new URL(args[0]);
            } catch (Exception e) {
            }
        }

        junit.textui.TestRunner.run(new junit.framework.TestSuite(Compound1TestCase.class));
    } // main
}

