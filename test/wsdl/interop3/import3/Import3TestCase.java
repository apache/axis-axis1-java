package test.wsdl.interop3.import3;

import test.wsdl.interop3.import3.step6.definitions.SoapInteropImport3PortTypeServiceLocator;

import test.wsdl.interop3.import3.xsd.SOAPStruct;

/*
    <!-- SOAP Builder's round III web services          -->
    <!-- interoperability testing:  import3             -->
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

public class Import3TestCase extends junit.framework.TestCase {
    public Import3TestCase(String name) {
        super(name);
    }

    public void testStep3EchoStruct() {
        SoapInteropImport3PortType binding;
        try {
            binding = new Import3Locator().getSoapInteropImport3Port();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            SOAPStruct value = null;
            value = binding.echoStruct(new SOAPStruct());
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void testStep3EchoStructArray() {
        SoapInteropImport3PortType binding;
        try {
            binding = new Import3Locator().getSoapInteropImport3Port();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            SOAPStruct[] value = null;
            value = binding.echoStructArray(new SOAPStruct[0]);
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void testStep5EchoStruct() {
        SoapInteropImport3PortType binding;
        try {
            binding = new Import3Locator().getSoapInteropImport3Port(new java.net.URL("http://localhost:8080/axis/services/SoapInteropImport3Port"));
        }
        catch (Throwable t) {
            throw new junit.framework.AssertionFailedError("Throwable caught: " + t);
        }
        assertTrue("binding is null", binding != null);

        try {
            SOAPStruct value = null;
            value = binding.echoStruct(new SOAPStruct());
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void testStep5EchoStructArray() {
        SoapInteropImport3PortType binding;
        try {
            binding = new Import3Locator().getSoapInteropImport3Port(new java.net.URL("http://localhost:8080/axis/services/SoapInteropImport3Port"));
        }
        catch (Throwable t) {
            throw new junit.framework.AssertionFailedError("Throwable caught: " + t);
        }
        assertTrue("binding is null", binding != null);

        try {
            SOAPStruct[] value = null;
            value = binding.echoStructArray(new SOAPStruct[0]);
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void testStep7EchoStruct() {
        test.wsdl.interop3.import3.step6.definitions.SoapInteropImport3PortType binding;
        try {
            binding = new SoapInteropImport3PortTypeServiceLocator().getSoapInteropImport3Port();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            test.wsdl.interop3.import3.step6.xsd.SOAPStruct value = null;
            value = binding.echoStruct(new test.wsdl.interop3.import3.step6.xsd.SOAPStruct());
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void testStep7EchoStructArray() {
        test.wsdl.interop3.import3.step6.definitions.SoapInteropImport3PortType binding;
        try {
            binding = new SoapInteropImport3PortTypeServiceLocator().getSoapInteropImport3Port();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            test.wsdl.interop3.import3.step6.xsd.SOAPStruct[] value = null;
            value = binding.echoStructArray(new test.wsdl.interop3.import3.step6.xsd.SOAPStruct[0]);
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

/* doesn't work yet
    public void testStep8EchoStruct() {
        test.wsdl.interop3.import3.step6.definitions.SoapInteropImport3PortType binding;
        try {
            binding = new SoapInteropImport3PortTypeServiceLocator().getSoapInteropImport3Port(new java.net.URL("http://mssoapinterop.org/stkV3/wsdl/import3.wsdl"));
        }
        catch (Throwable t) {
            throw new junit.framework.AssertionFailedError("Throwable caught: " + t);
        }
        assertTrue("binding is null", binding != null);

        try {
            test.wsdl.interop3.import3.step6.xsd.SOAPStruct value = null;
            value = binding.echoStruct(new test.wsdl.interop3.import3.step6.xsd.SOAPStruct());
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void testStep8EchoStructArray() {
        test.wsdl.interop3.import3.step6.definitions.SoapInteropImport3PortType binding;
        try {
            binding = new SoapInteropImport3PortTypeServiceLocator().getSoapInteropImport3Port(new java.net.URL("http://mssoapinterop.org/stkV3/wsdl/import3.wsdl"));
        }
        catch (Throwable t) {
            throw new junit.framework.AssertionFailedError("Throwable caught: " + t);
        }
        assertTrue("binding is null", binding != null);

        try {
            test.wsdl.interop3.import3.step6.xsd.SOAPStruct[] value = null;
            value = binding.echoStructArray(new test.wsdl.interop3.import3.step6.xsd.SOAPStruct[0]);
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }
*/

    public static void main(String[] args) {
        junit.textui.TestRunner.run(new junit.framework.TestSuite(Import3TestCase.class));
    } // main

}

