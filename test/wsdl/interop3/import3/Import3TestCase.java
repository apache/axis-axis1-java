package test.wsdl.interop3.import3;


import test.wsdl.interop3.import3.xsd.SOAPStruct;

import java.net.URL;

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
    public static URL url;

    public Import3TestCase(String name) {
        super(name);
    }

    public void testStep3EchoStruct() {
        SoapInteropImport3PortType binding;
        try {
            if (url == null) {
                binding = new Import3Locator().getSoapInteropImport3Port();
            } else {
                binding = new Import3Locator().getSoapInteropImport3Port(url);
            }
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            SOAPStruct value = new SOAPStruct();
            value.setVarString("import2 string");
            value.setVarInt(5);
            value.setVarFloat(4.5F);
            SOAPStruct result = binding.echoStruct(value);
            assertEquals("String members didn't match", value.getVarString(), result.getVarString());
            assertEquals("int members didn't match", value.getVarInt(), result.getVarInt());
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void testStep3EchoStructArray() {
        SoapInteropImport3PortType binding;
        try {
            if (url == null) {
                binding = new Import3Locator().getSoapInteropImport3Port();
            } else {
                binding = new Import3Locator().getSoapInteropImport3Port(url);
            }
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

    public static void main(String[] args) {
        if (args.length == 1) {
            try {
                url = new URL(args[0]);
            } catch (Exception e) {
            }
        }

        junit.textui.TestRunner.run(new junit.framework.TestSuite(Import3TestCase.class));
    } // main

}

