package test.wsdl.interop3.import2;

import test.wsdl.interop3.import2.definitions.SoapInteropImport2PortType;
import test.wsdl.interop3.import2.step6.definitions.SoapInteropImport2PortTypeServiceLocator;
import test.wsdl.interop3.import2.xsd.SOAPStruct;

/*
    <!-- SOAP Builder's round III web services          -->
    <!-- interoperability testing:  import2             -->
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

public class Import2TestCase extends junit.framework.TestCase {
    public Import2TestCase(String name) {
        super(name);
    }

    public void testStep3() {
        SoapInteropImport2PortType binding;
        try {
            binding = new Import2Locator().getSoapInteropImport2Port();
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

    public void testStep5() {
        SoapInteropImport2PortType binding;
        try {
            binding = new Import2Locator().getSoapInteropImport2Port(new java.net.URL("http://localhost:8080/axis/services/SoapInteropImport2Port"));
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

    public void testStep7() {
        test.wsdl.interop3.import2.step6.definitions.SoapInteropImport2PortType binding;
        try {
            binding = new SoapInteropImport2PortTypeServiceLocator().getSoapInteropImport2Port();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            test.wsdl.interop3.import2.step6.xsd.SOAPStruct value = null;
            value = binding.echoStruct(new test.wsdl.interop3.import2.step6.xsd.SOAPStruct());
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

/* doesn't work yet
    public void testStep8() {
        SoapInteropImport2PortType binding;
        try {
            binding = new SoapInteropImport2PortTypeServiceLocator().getSoapInteropImport2Port(new java.net.URL("http://mssoapinterop.org/stkV3/wsdl/import2.wsdl"));
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
*/

/* 
   Not working right now.
    public void testAbsoluteStep3() {
        test.wsdl.interop3.absimport2.definitions.SoapInteropImport2PortType binding;
        try {
            binding = new test.wsdl.interop3.absimport2.Import2Locator().getSoapInteropImport2Port();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            test.wsdl.interop3.absimport2.xsd.SOAPStruct value = null;
            value = binding.echoStruct(new test.wsdl.interop3.absimport2.xsd.SOAPStruct());
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void testAbsoluteStep5() {
        test.wsdl.interop3.absimport2.definitions.SoapInteropImport2PortType binding;
        try {
            binding = new test.wsdl.interop3.absimport2.Import2Locator().getSoapInteropImport2Port(new java.net.URL("http://localhost:8080/axis/services/SoapInteropImport2Port"));
        }
        catch (Throwable t) {
            throw new junit.framework.AssertionFailedError("Throwable caught: " + t);
        }
        assertTrue("binding is null", binding != null);

        try {
            test.wsdl.interop3.absimport2.xsd.SOAPStruct value = null;
            value = binding.echoStruct(new test.wsdl.interop3.absimport2.xsd.SOAPStruct());
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void testAbsoluteStep7() {
        test.wsdl.interop3.absimport2.step6.definitions.SoapInteropImport2PortType binding;
        try {
            binding = new test.wsdl.interop3.absimport2.step6.definitions.SoapInteropImport2PortTypeServiceLocator().getSoapInteropImport2Port();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            test.wsdl.interop3.absimport2.step6.xsd.SOAPStruct value = null;
            value = binding.echoStruct(new test.wsdl.interop3.absimport2.step6.xsd.SOAPStruct());
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }
*/
/* doesn't work yet
    public void testAbsoluteStep8() {
        SoapInteropImport2PortType binding;
        try {
            binding = new SoapInteropImport2PortTypeServiceLocator().getSoapInteropImport2Port(new java.net.URL("http://mssoapinterop.org/stkV3/wsdl/import2.wsdl"));
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
*/

    public static void main(String[] args) {
        junit.textui.TestRunner.run(new junit.framework.TestSuite(Import2TestCase.class));
    } // main

}

