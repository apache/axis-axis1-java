/**
 * WhiteMesaSoap12TestSvcTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.soap12.assertion;

import java.util.Arrays;
import java.util.TimeZone;
import java.util.Calendar;

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

        test.wsdl.soap12.assertion.xsd.SOAPStruct input = new test.wsdl.soap12.assertion.xsd.SOAPStruct();
        input.setVarFloat(-5);
        input.setVarInt(10);
        input.setVarString("EchoStruct");
        
        // Test operation
        test.wsdl.soap12.assertion.xsd.SOAPStruct output = null;
        output = binding.echoStruct(input);
        // TBD - validate results
        assertEquals(input, output);
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

        test.wsdl.soap12.assertion.xsd.SOAPStruct[] input = new test.wsdl.soap12.assertion.xsd.SOAPStruct[1];
        input[0] = new test.wsdl.soap12.assertion.xsd.SOAPStruct();
        input[0].setVarFloat(-5);
        input[0].setVarInt(10);
        input[0].setVarString("EchoStruct");
        // Test operation
        test.wsdl.soap12.assertion.xsd.SOAPStruct[] output = null;
        output = binding.echoStructArray(input);
        // TBD - validate results
        assertTrue(Arrays.equals(input,output));
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

        test.wsdl.soap12.assertion.xsd.SOAPStruct input = new test.wsdl.soap12.assertion.xsd.SOAPStruct();
        input.setVarFloat(-5);
        input.setVarInt(10);
        input.setVarString("EchoStructAsSimpleTypes");
        
        javax.xml.rpc.holders.StringHolder out1 = new javax.xml.rpc.holders.StringHolder();
        javax.xml.rpc.holders.IntHolder out2 = new javax.xml.rpc.holders.IntHolder();
        javax.xml.rpc.holders.FloatHolder out3 = new javax.xml.rpc.holders.FloatHolder();

        // Test operation
        binding.echoStructAsSimpleTypes(input, out1, out2, out3);
        // TBD - validate results
        assertEquals(out1.value, input.getVarString());
        assertEquals(out2.value, input.getVarInt());
        assertTrue(out3.value == input.getVarFloat());
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

        String input1 = new String("EchoSimpleTypesAsStruct");
        int    input2 = 50;
        float  input3 = 45.5F;
        // Test operation
        test.wsdl.soap12.assertion.xsd.SOAPStruct output = null;
        output = binding.echoSimpleTypesAsStruct(input1, input2, input3);
        
        // TBD - validate results
        assertEquals(input1, output.getVarString());
        assertEquals(input2, output.getVarInt());
        assertTrue(input3 == output.getVarFloat());
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

        test.wsdl.soap12.assertion.xsd.SOAPStructStruct input = new test.wsdl.soap12.assertion.xsd.SOAPStructStruct();
        input.setVarFloat(-5);
        input.setVarInt(10);
        input.setVarString("EchoNestedStruct1");

        test.wsdl.soap12.assertion.xsd.SOAPStruct inputInner = new test.wsdl.soap12.assertion.xsd.SOAPStruct();
        inputInner.setVarFloat(-5);
        inputInner.setVarInt(10);
        inputInner.setVarString("EchoNestedStruct2");
        
        input.setVarStruct(inputInner);
        
        // Test operation
        test.wsdl.soap12.assertion.xsd.SOAPStructStruct output = null;
        output = binding.echoNestedStruct(input);
        
        // TBD - validate results
        assertEquals(input, output);
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

        test.wsdl.soap12.assertion.xsd.SOAPArrayStruct input = new test.wsdl.soap12.assertion.xsd.SOAPArrayStruct();
        input.setVarFloat(-5);
        input.setVarInt(10);
        input.setVarString("EchoNestedArray1");
        input.setVarArray(new String[] {"EchoNestedArray2","EchoNestedArray3","EchoNestedArray4"});

        // TODO: This does not work :(
        //// Test operation
        //test.wsdl.soap12.assertion.xsd.SOAPArrayStruct output = null;
        //output = binding.echoNestedArray(input);
        //// TBD - validate results
        //assertEquals(input, output);
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

        float[] input = new float[] {
            1.1F,
            1.2F,
            1.3F
        };

        // Test operation
        float[] output = null;
        output = binding.echoFloatArray(input);
        
        // TBD - validate results
        assertTrue(Arrays.equals(input,output));
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

        String[] input = new String[] {
            "1.1F",
            "1.2F",
            "1.3F"
        };
        // Test operation
        java.lang.String[] output = null;
        output = binding.echoStringArray(input);

        // TBD - validate results
        assertTrue(Arrays.equals(input,output));
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

        int[] input = new int[] {
            1,
            2,
            3
        };
        // Test operation
        int[] output = null;
        output = binding.echoIntegerArray(input);

        // TBD - validate results
        assertTrue(Arrays.equals(input,output));
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

        byte[] input = new byte[] {0xC, 0xA, 0xF, 0xE};
        
        // Test operation
        byte[] output = null;
        output = binding.echoBase64(input);
        
        // TBD - validate results
        assertTrue(Arrays.equals(input,output));
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
        assertEquals(true, value);
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

        java.util.Calendar input = java.util.Calendar.getInstance();
        input.setTimeZone(TimeZone.getTimeZone("GMT"));
        input.set(Calendar.MILLISECOND, 0);
        
        java.util.Calendar output = null;
        output = binding.echoDate(input);
        output.setTimeZone(TimeZone.getTimeZone("GMT"));
        assertEquals(input, output);
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

        java.math.BigDecimal input = new java.math.BigDecimal(5000);
        
        // Test operation
        java.math.BigDecimal output = null;
        output = binding.echoDecimal(input);
        
        // TBD - validate results
        assertEquals(input, output);
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

        float input = -334.5F;
        // Test operation
        float output = 0;
        output = binding.echoFloat(input);
        // TBD - validate results
        assertTrue(input == output);
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
        value = binding.echoString(new java.lang.String("EchoString"));
        
        // TBD - validate results
        assertEquals("EchoString", value);
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
        int output = -3;
        output = binding.countItems(new java.lang.String[] {"Life","is","a","box","of","chocolates"});
        // TBD - validate results
        assertEquals(output, 6);
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

        // TODO: This does not work :(
        //// Test operation
        //boolean value = binding.isNil(new java.lang.String("isNil"));
        //
        //// TBD - validate results
        //assertEquals(false, value);
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

        // TODO: This does not work :(
        //// Test operation
        //java.lang.String value = null;
        // value = binding.echoOk(new java.lang.String("EchoOk"));
        //// TBD - validate results
        //assertEquals(value, "EchoOk");
    }

}
