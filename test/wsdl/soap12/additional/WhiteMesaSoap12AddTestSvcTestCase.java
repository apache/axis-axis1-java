/**
 * WhiteMesaSoap12AddTestSvcTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.soap12.additional;

public class WhiteMesaSoap12AddTestSvcTestCase extends junit.framework.TestCase {
    public WhiteMesaSoap12AddTestSvcTestCase(java.lang.String name) {
        super(name);
    }
        // getTime is a notification style operation and is unsupported.
    public void test1Soap12AddTestDocPortEchoString() throws Exception {
        test.wsdl.soap12.additional.Soap12AddTestDocBindingStub binding;
        try {
            binding = (test.wsdl.soap12.additional.Soap12AddTestDocBindingStub)
                          new test.wsdl.soap12.additional.WhiteMesaSoap12AddTestSvcLocator().getSoap12AddTestDocPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.echoString(new java.lang.String());
        // TBD - validate results
    }

    public void test2Soap12AddTestDocPortEchoSenderFault() throws Exception {
        test.wsdl.soap12.additional.Soap12AddTestDocBindingStub binding;
        try {
            binding = (test.wsdl.soap12.additional.Soap12AddTestDocBindingStub)
                          new test.wsdl.soap12.additional.WhiteMesaSoap12AddTestSvcLocator().getSoap12AddTestDocPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        // TODO: Fix this...
        // binding.echoSenderFault(new java.lang.String());
        // TBD - validate results
    }

    public void test3Soap12AddTestDocPortEchoReceiverFault() throws Exception {
        test.wsdl.soap12.additional.Soap12AddTestDocBindingStub binding;
        try {
            binding = (test.wsdl.soap12.additional.Soap12AddTestDocBindingStub)
                          new test.wsdl.soap12.additional.WhiteMesaSoap12AddTestSvcLocator().getSoap12AddTestDocPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        // TODO: Fix this...
        // binding.echoReceiverFault(new java.lang.String());
        // TBD - validate results
    }

    public void test4Soap12AddTestRpcPortEchoVoid() throws Exception {
        test.wsdl.soap12.additional.Soap12AddTestRpcBindingStub binding;
        try {
            binding = (test.wsdl.soap12.additional.Soap12AddTestRpcBindingStub)
                          new test.wsdl.soap12.additional.WhiteMesaSoap12AddTestSvcLocator().getSoap12AddTestRpcPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        // TODO: Fix this...
        // binding.echoVoid();
        // TBD - validate results
    }

    public void test5Soap12AddTestRpcPortEchoSimpleTypesAsStruct() throws Exception {
        test.wsdl.soap12.additional.Soap12AddTestRpcBindingStub binding;
        try {
            binding = (test.wsdl.soap12.additional.Soap12AddTestRpcBindingStub)
                          new test.wsdl.soap12.additional.WhiteMesaSoap12AddTestSvcLocator().getSoap12AddTestRpcPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        test.wsdl.soap12.additional.xsd.SOAPStruct value = null;
        value = binding.echoSimpleTypesAsStruct(new java.lang.String(), 0, 0);
        // TBD - validate results
    }

    public void test6Soap12AddTestRpcPortEchoString() throws Exception {
        test.wsdl.soap12.additional.Soap12AddTestRpcBindingStub binding;
        try {
            binding = (test.wsdl.soap12.additional.Soap12AddTestRpcBindingStub)
                          new test.wsdl.soap12.additional.WhiteMesaSoap12AddTestSvcLocator().getSoap12AddTestRpcPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.echoString(new java.lang.String());
        // TBD - validate results
    }

    public void test7Soap12AddTestRpcPortEchoSimpleTypesAsStructOfSchemaTypes() throws Exception {
        test.wsdl.soap12.additional.Soap12AddTestRpcBindingStub binding;
        try {
            binding = (test.wsdl.soap12.additional.Soap12AddTestRpcBindingStub)
                          new test.wsdl.soap12.additional.WhiteMesaSoap12AddTestSvcLocator().getSoap12AddTestRpcPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        test.wsdl.soap12.additional.xsd.SOAPStructTypes value = null;
        // TODO: Fix this...
        // value = binding.echoSimpleTypesAsStructOfSchemaTypes(new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test8Soap12AddTestRpcPortEchoInteger() throws Exception {
        test.wsdl.soap12.additional.Soap12AddTestRpcBindingStub binding;
        try {
            binding = (test.wsdl.soap12.additional.Soap12AddTestRpcBindingStub)
                          new test.wsdl.soap12.additional.WhiteMesaSoap12AddTestSvcLocator().getSoap12AddTestRpcPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        int value = -3;
        value = binding.echoInteger(0);
        // TBD - validate results
    }

        // getTime is a notification style operation and is unsupported.
        // getTime is a notification style operation and is unsupported.
    public void test9Soap12AddTestDocUpperPortEchoString() throws Exception {
        test.wsdl.soap12.additional.Soap12AddTestDocBindingStub binding;
        try {
            binding = (test.wsdl.soap12.additional.Soap12AddTestDocBindingStub)
                          new test.wsdl.soap12.additional.WhiteMesaSoap12AddTestSvcLocator().getSoap12AddTestDocUpperPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.echoString(new java.lang.String());
        // TBD - validate results
    }

    public void test10Soap12AddTestDocUpperPortEchoSenderFault() throws Exception {
        test.wsdl.soap12.additional.Soap12AddTestDocBindingStub binding;
        try {
            binding = (test.wsdl.soap12.additional.Soap12AddTestDocBindingStub)
                          new test.wsdl.soap12.additional.WhiteMesaSoap12AddTestSvcLocator().getSoap12AddTestDocUpperPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        binding.echoSenderFault(new java.lang.String());
        // TBD - validate results
    }

    public void test11Soap12AddTestDocUpperPortEchoReceiverFault() throws Exception {
        test.wsdl.soap12.additional.Soap12AddTestDocBindingStub binding;
        try {
            binding = (test.wsdl.soap12.additional.Soap12AddTestDocBindingStub)
                          new test.wsdl.soap12.additional.WhiteMesaSoap12AddTestSvcLocator().getSoap12AddTestDocUpperPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        binding.echoReceiverFault(new java.lang.String());
        // TBD - validate results
    }

        // getTime is a notification style operation and is unsupported.
    public void test12Soap12AddTestDocIntermediaryPortEchoString() throws Exception {
        test.wsdl.soap12.additional.Soap12AddTestDocBindingStub binding;
        try {
            binding = (test.wsdl.soap12.additional.Soap12AddTestDocBindingStub)
                          new test.wsdl.soap12.additional.WhiteMesaSoap12AddTestSvcLocator().getSoap12AddTestDocIntermediaryPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.echoString(new java.lang.String());
        // TBD - validate results
    }

    public void test13Soap12AddTestDocIntermediaryPortEchoSenderFault() throws Exception {
        test.wsdl.soap12.additional.Soap12AddTestDocBindingStub binding;
        try {
            binding = (test.wsdl.soap12.additional.Soap12AddTestDocBindingStub)
                          new test.wsdl.soap12.additional.WhiteMesaSoap12AddTestSvcLocator().getSoap12AddTestDocIntermediaryPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        binding.echoSenderFault(new java.lang.String());
        // TBD - validate results
    }

    public void test14Soap12AddTestDocIntermediaryPortEchoReceiverFault() throws Exception {
        test.wsdl.soap12.additional.Soap12AddTestDocBindingStub binding;
        try {
            binding = (test.wsdl.soap12.additional.Soap12AddTestDocBindingStub)
                          new test.wsdl.soap12.additional.WhiteMesaSoap12AddTestSvcLocator().getSoap12AddTestDocIntermediaryPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        binding.echoReceiverFault(new java.lang.String());
        // TBD - validate results
    }

}
