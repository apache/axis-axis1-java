/**
 * WhiteMesaSoap12TestSvcTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.soap12.assertion;

public class WhiteMesaSoap12TestSvcTestCase extends junit.framework.TestCase {
    public WhiteMesaSoap12TestSvcTestCase(java.lang.String name) {
        super(name);
    }
    public void test1Soap12TestRpcPortReturnVoid() throws Exception {
        test.wsdl.soap12.assertion.Soap12TestRpcBindingStub binding;
        try {
            binding = (test.wsdl.soap12.assertion.Soap12TestRpcBindingStub)
                          new test.wsdl.soap12.assertion.WhiteMesaSoap12TestSvcLocator().getSoap12TestRpcPort();
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
        binding.returnVoid();
        // TBD - validate results
    }

    public void test2Soap12TestRpcPortEchoStruct() throws Exception {
        test.wsdl.soap12.assertion.Soap12TestRpcBindingStub binding;
        try {
            binding = (test.wsdl.soap12.assertion.Soap12TestRpcBindingStub)
                          new test.wsdl.soap12.assertion.WhiteMesaSoap12TestSvcLocator().getSoap12TestRpcPort();
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
        test.wsdl.soap12.assertion.xsd.SOAPStruct value = null;
        value = binding.echoStruct(new test.wsdl.soap12.assertion.xsd.SOAPStruct());
        // TBD - validate results
    }

    public void test3Soap12TestRpcPortEchoStructArray() throws Exception {
        test.wsdl.soap12.assertion.Soap12TestRpcBindingStub binding;
        try {
            binding = (test.wsdl.soap12.assertion.Soap12TestRpcBindingStub)
                          new test.wsdl.soap12.assertion.WhiteMesaSoap12TestSvcLocator().getSoap12TestRpcPort();
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
        test.wsdl.soap12.assertion.xsd.SOAPStruct[] value = null;
        value = binding.echoStructArray(new test.wsdl.soap12.assertion.xsd.SOAPStruct[0]);
        // TBD - validate results
    }

    public void test4Soap12TestRpcPortEchoStructAsSimpleTypes() throws Exception {
        test.wsdl.soap12.assertion.Soap12TestRpcBindingStub binding;
        try {
            binding = (test.wsdl.soap12.assertion.Soap12TestRpcBindingStub)
                          new test.wsdl.soap12.assertion.WhiteMesaSoap12TestSvcLocator().getSoap12TestRpcPort();
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
        //TODO: Why does this not work?
        //binding.echoStructAsSimpleTypes(new test.wsdl.soap12.assertion.xsd.SOAPStruct(), new javax.xml.rpc.holders.StringHolder(), new javax.xml.rpc.holders.IntHolder(), new javax.xml.rpc.holders.FloatHolder());
        // TBD - validate results
    }

    public void test5Soap12TestRpcPortEchoSimpleTypesAsStruct() throws Exception {
        test.wsdl.soap12.assertion.Soap12TestRpcBindingStub binding;
        try {
            binding = (test.wsdl.soap12.assertion.Soap12TestRpcBindingStub)
                          new test.wsdl.soap12.assertion.WhiteMesaSoap12TestSvcLocator().getSoap12TestRpcPort();
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
        test.wsdl.soap12.assertion.xsd.SOAPStruct value = null;
        value = binding.echoSimpleTypesAsStruct(new java.lang.String(), 0, 0);
        // TBD - validate results
    }

    public void test6Soap12TestRpcPortEchoNestedStruct() throws Exception {
        test.wsdl.soap12.assertion.Soap12TestRpcBindingStub binding;
        try {
            binding = (test.wsdl.soap12.assertion.Soap12TestRpcBindingStub)
                          new test.wsdl.soap12.assertion.WhiteMesaSoap12TestSvcLocator().getSoap12TestRpcPort();
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
        test.wsdl.soap12.assertion.xsd.SOAPStructStruct value = null;
        value = binding.echoNestedStruct(new test.wsdl.soap12.assertion.xsd.SOAPStructStruct());
        // TBD - validate results
    }

    public void test7Soap12TestRpcPortEchoNestedArray() throws Exception {
        test.wsdl.soap12.assertion.Soap12TestRpcBindingStub binding;
        try {
            binding = (test.wsdl.soap12.assertion.Soap12TestRpcBindingStub)
                          new test.wsdl.soap12.assertion.WhiteMesaSoap12TestSvcLocator().getSoap12TestRpcPort();
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
        test.wsdl.soap12.assertion.xsd.SOAPArrayStruct value = null;
        value = binding.echoNestedArray(new test.wsdl.soap12.assertion.xsd.SOAPArrayStruct());
        // TBD - validate results
    }

    public void test8Soap12TestRpcPortEchoFloatArray() throws Exception {
        test.wsdl.soap12.assertion.Soap12TestRpcBindingStub binding;
        try {
            binding = (test.wsdl.soap12.assertion.Soap12TestRpcBindingStub)
                          new test.wsdl.soap12.assertion.WhiteMesaSoap12TestSvcLocator().getSoap12TestRpcPort();
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
        float[] value = null;
        value = binding.echoFloatArray(new float[0]);
        // TBD - validate results
    }

    public void test9Soap12TestRpcPortEchoStringArray() throws Exception {
        test.wsdl.soap12.assertion.Soap12TestRpcBindingStub binding;
        try {
            binding = (test.wsdl.soap12.assertion.Soap12TestRpcBindingStub)
                          new test.wsdl.soap12.assertion.WhiteMesaSoap12TestSvcLocator().getSoap12TestRpcPort();
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
        java.lang.String[] value = null;
        value = binding.echoStringArray(new java.lang.String[0]);
        // TBD - validate results
    }

    public void test10Soap12TestRpcPortEchoIntegerArray() throws Exception {
        test.wsdl.soap12.assertion.Soap12TestRpcBindingStub binding;
        try {
            binding = (test.wsdl.soap12.assertion.Soap12TestRpcBindingStub)
                          new test.wsdl.soap12.assertion.WhiteMesaSoap12TestSvcLocator().getSoap12TestRpcPort();
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
        int[] value = null;
        value = binding.echoIntegerArray(new int[0]);
        // TBD - validate results
    }

    public void test11Soap12TestRpcPortEchoBase64() throws Exception {
        test.wsdl.soap12.assertion.Soap12TestRpcBindingStub binding;
        try {
            binding = (test.wsdl.soap12.assertion.Soap12TestRpcBindingStub)
                          new test.wsdl.soap12.assertion.WhiteMesaSoap12TestSvcLocator().getSoap12TestRpcPort();
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
        byte[] value = null;
        value = binding.echoBase64(new byte[0]);
        // TBD - validate results
    }

    public void test12Soap12TestRpcPortEchoBoolean() throws Exception {
        test.wsdl.soap12.assertion.Soap12TestRpcBindingStub binding;
        try {
            binding = (test.wsdl.soap12.assertion.Soap12TestRpcBindingStub)
                          new test.wsdl.soap12.assertion.WhiteMesaSoap12TestSvcLocator().getSoap12TestRpcPort();
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
        boolean value = false;
        value = binding.echoBoolean(true);
        // TBD - validate results
    }

    public void test13Soap12TestRpcPortEchoDate() throws Exception {
        test.wsdl.soap12.assertion.Soap12TestRpcBindingStub binding;
        try {
            binding = (test.wsdl.soap12.assertion.Soap12TestRpcBindingStub)
                          new test.wsdl.soap12.assertion.WhiteMesaSoap12TestSvcLocator().getSoap12TestRpcPort();
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
        java.util.Calendar value = null;
        value = binding.echoDate(java.util.Calendar.getInstance());
        // TBD - validate results
    }

    public void test14Soap12TestRpcPortEchoDecimal() throws Exception {
        test.wsdl.soap12.assertion.Soap12TestRpcBindingStub binding;
        try {
            binding = (test.wsdl.soap12.assertion.Soap12TestRpcBindingStub)
                          new test.wsdl.soap12.assertion.WhiteMesaSoap12TestSvcLocator().getSoap12TestRpcPort();
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
        java.math.BigDecimal value = null;
        value = binding.echoDecimal(new java.math.BigDecimal(0));
        // TBD - validate results
    }

    public void test15Soap12TestRpcPortEchoFloat() throws Exception {
        test.wsdl.soap12.assertion.Soap12TestRpcBindingStub binding;
        try {
            binding = (test.wsdl.soap12.assertion.Soap12TestRpcBindingStub)
                          new test.wsdl.soap12.assertion.WhiteMesaSoap12TestSvcLocator().getSoap12TestRpcPort();
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
        float value = -3;
        value = binding.echoFloat(0);
        // TBD - validate results
    }

    public void test16Soap12TestRpcPortEchoString() throws Exception {
        test.wsdl.soap12.assertion.Soap12TestRpcBindingStub binding;
        try {
            binding = (test.wsdl.soap12.assertion.Soap12TestRpcBindingStub)
                          new test.wsdl.soap12.assertion.WhiteMesaSoap12TestSvcLocator().getSoap12TestRpcPort();
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

    public void test17Soap12TestRpcPortCountItems() throws Exception {
        test.wsdl.soap12.assertion.Soap12TestRpcBindingStub binding;
        try {
            binding = (test.wsdl.soap12.assertion.Soap12TestRpcBindingStub)
                          new test.wsdl.soap12.assertion.WhiteMesaSoap12TestSvcLocator().getSoap12TestRpcPort();
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
        value = binding.countItems(new java.lang.String[0]);
        // TBD - validate results
    }

    public void test18Soap12TestRpcPortIsNil() throws Exception {
        test.wsdl.soap12.assertion.Soap12TestRpcBindingStub binding;
        try {
            binding = (test.wsdl.soap12.assertion.Soap12TestRpcBindingStub)
                          new test.wsdl.soap12.assertion.WhiteMesaSoap12TestSvcLocator().getSoap12TestRpcPort();
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
        boolean value = false;
        //TODO: Why does this not work?
        //value = binding.isNil(new java.lang.String());
        // TBD - validate results
    }

    public void test19Soap12TestDocPortEmptyBody() throws Exception {
        test.wsdl.soap12.assertion.Soap12TestDocBindingStub binding;
        try {
            binding = (test.wsdl.soap12.assertion.Soap12TestDocBindingStub)
                          new test.wsdl.soap12.assertion.WhiteMesaSoap12TestSvcLocator().getSoap12TestDocPort();
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
        binding.emptyBody();
        // TBD - validate results
    }

    public void test20Soap12TestDocPortEchoOk() throws Exception {
        test.wsdl.soap12.assertion.Soap12TestDocBindingStub binding;
        try {
            binding = (test.wsdl.soap12.assertion.Soap12TestDocBindingStub)
                          new test.wsdl.soap12.assertion.WhiteMesaSoap12TestSvcLocator().getSoap12TestDocPort();
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
        value = binding.echoOk(new java.lang.String());
        // TBD - validate results
    }

}
