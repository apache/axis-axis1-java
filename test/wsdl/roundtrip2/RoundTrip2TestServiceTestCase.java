/**
 * RoundTrip2TestServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC3 Feb 19, 2005 (03:03:59 EST) WSDL2Java emitter.
 */

package test.wsdl.roundtrip2;

public class RoundTrip2TestServiceTestCase extends junit.framework.TestCase {
    public RoundTrip2TestServiceTestCase(java.lang.String name) {
        super(name);
    }

    public void testRoundTrip2TestWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2TestAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1RoundTrip2TestBooleanTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        value = binding.booleanTest(true);
        // TBD - validate results
    }

    public void test2RoundTrip2TestWrapperBooleanTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        java.lang.Boolean value = null;
        value = binding.wrapperBooleanTest(new java.lang.Boolean(false));
        // TBD - validate results
    }

    public void test3RoundTrip2TestByteTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        byte value = -3;
        value = binding.byteTest((byte)0);
        // TBD - validate results
    }

    public void test4RoundTrip2TestWrapperByteTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        java.lang.Byte value = null;
        value = binding.wrapperByteTest(new java.lang.Byte((byte)0));
        // TBD - validate results
    }

    public void test5RoundTrip2TestShortTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        short value = -3;
        value = binding.shortTest((short)0);
        // TBD - validate results
    }

    public void test6RoundTrip2TestWrapperShortTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        java.lang.Short value = null;
        value = binding.wrapperShortTest(new java.lang.Short((short)0));
        // TBD - validate results
    }

    public void test7RoundTrip2TestIntTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        value = binding.intTest(0);
        // TBD - validate results
    }

    public void test8RoundTrip2TestWrapperIntegerTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        java.lang.Integer value = null;
        value = binding.wrapperIntegerTest(new java.lang.Integer(0));
        // TBD - validate results
    }

    public void test9RoundTrip2TestLongTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        long value = -3;
        value = binding.longTest(0);
        // TBD - validate results
    }

    public void test10RoundTrip2TestWrapperLongTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        java.lang.Long value = null;
        value = binding.wrapperLongTest(new java.lang.Long(0));
        // TBD - validate results
    }

    public void test11RoundTrip2TestFloatTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        value = binding.floatTest(0);
        // TBD - validate results
    }

    public void test12RoundTrip2TestWrapperFloatTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        java.lang.Float value = null;
        value = binding.wrapperFloatTest(new java.lang.Float(0));
        // TBD - validate results
    }

    public void test13RoundTrip2TestDoubleTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        double value = -3;
        value = binding.doubleTest(0);
        // TBD - validate results
    }

    public void test14RoundTrip2TestWrapperDoubleTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        java.lang.Double value = null;
        value = binding.wrapperDoubleTest(new java.lang.Double(0));
        // TBD - validate results
    }

    public void test15RoundTrip2TestBooleanArrayTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        boolean[] value = null;
        value = binding.booleanArrayTest(new boolean[]{true,false});
        // TBD - validate results
    }

    public void test16RoundTrip2TestByteArrayTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        value = binding.byteArrayTest(new byte[]{0xD,0xE});
        // TBD - validate results
    }

    public void test17RoundTrip2TestShortArrayTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        short[] value = null;
        value = binding.shortArrayTest(new short[]{3,4});
        // TBD - validate results
    }

    public void test18RoundTrip2TestIntArrayTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        value = binding.intArrayTest(new int[]{1,2});
        // TBD - validate results
    }

    public void test19RoundTrip2TestLongArrayTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        long[] value = null;
        value = binding.longArrayTest(new long[]{45,64});
        // TBD - validate results
    }

    public void test20RoundTrip2TestFloatArrayTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        value = binding.floatArrayTest(new float[]{12,34});
        // TBD - validate results
    }

    public void test21RoundTrip2TestDoubleArrayTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        double[] value = null;
        value = binding.doubleArrayTest(new double[]{435,647});
        // TBD - validate results
    }

    public void test22RoundTrip2TestWrapperBooleanArrayTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        java.lang.Boolean[] value = null;
        value = binding.wrapperBooleanArrayTest(new java.lang.Boolean[]{Boolean.TRUE, Boolean.FALSE});
        // TBD - validate results
    }

    public void test23RoundTrip2TestWrapperByteArrayTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        java.lang.Byte[] value = null;
        value = binding.wrapperByteArrayTest(new java.lang.Byte[]{new Byte((byte)0x3)});
        // TBD - validate results
    }

    public void test24RoundTrip2TestWrapperShortArrayTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        java.lang.Short[] value = null;
        value = binding.wrapperShortArrayTest(new java.lang.Short[]{new Short((short)3)});
        // TBD - validate results
    }

    public void test25RoundTrip2TestWrapperIntArrayTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        java.lang.Integer[] value = null;
        value = binding.wrapperIntArrayTest(new java.lang.Integer[]{new Integer(4)});
        // TBD - validate results
    }

    public void test26RoundTrip2TestWrapperLongArrayTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        java.lang.Long[] value = null;
        value = binding.wrapperLongArrayTest(new java.lang.Long[0]);
        // TBD - validate results
    }

    public void test27RoundTrip2TestWrapperFloatArrayTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        java.lang.Float[] value = null;
        value = binding.wrapperFloatArrayTest(new java.lang.Float[0]);
        // TBD - validate results
    }

    public void test28RoundTrip2TestWrapperDoubleArrayTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        java.lang.Double[] value = null;
        value = binding.wrapperDoubleArrayTest(new java.lang.Double[0]);
        // TBD - validate results
    }

    public void test29RoundTrip2TestBooleanMultiArrayTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        boolean[][] value = null;
        value = binding.booleanMultiArrayTest(new boolean[0][0]);
        // TBD - validate results
    }

    public void test30RoundTrip2TestByteMultiArrayTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        byte[][] value = null;
        value = binding.byteMultiArrayTest(new byte[0][0]);
        // TBD - validate results
    }

    public void test31RoundTrip2TestShortMultiArrayTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        short[][] value = null;
        value = binding.shortMultiArrayTest(new short[0][0]);
        // TBD - validate results
    }

    public void test32RoundTrip2TestIntMultiArrayTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        int[][] value = null;
        value = binding.intMultiArrayTest(new int[0][0]);
        // TBD - validate results
    }

    public void test33RoundTrip2TestLongMultiArrayTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        long[][] value = null;
        value = binding.longMultiArrayTest(new long[0][0]);
        // TBD - validate results
    }

    public void test34RoundTrip2TestFloatMultiArrayTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        float[][] value = null;
        value = binding.floatMultiArrayTest(new float[0][0]);
        // TBD - validate results
    }

    public void test35RoundTrip2TestDoubleMultiArrayTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        double[][] value = null;
        value = binding.doubleMultiArrayTest(new double[0][0]);
        // TBD - validate results
    }

    public void test36RoundTrip2TestStringTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        value = binding.stringTest(new java.lang.String());
        // TBD - validate results
    }

    public void test37RoundTrip2TestStringArrayTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        value = binding.stringArrayTest(new java.lang.String[0]);
        // TBD - validate results
    }

    public void test38RoundTrip2TestStringMultiArrayTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        java.lang.String[][] value = null;
        value = binding.stringMultiArrayTest(new java.lang.String[0][0]);
        // TBD - validate results
    }

    public void test39RoundTrip2TestCalendarTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        value = binding.calendarTest(java.util.Calendar.getInstance());
        // TBD - validate results
    }

    public void test40RoundTrip2TestCalendarArrayTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        java.util.Calendar[] value = null;
        value = binding.calendarArrayTest(new java.util.Calendar[0]);
        // TBD - validate results
    }

    public void test41RoundTrip2TestCalendarMultiArrayTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        java.util.Calendar[][] value = null;
        value = binding.calendarMultiArrayTest(new java.util.Calendar[0][0]);
        // TBD - validate results
    }

    public void test42RoundTrip2TestBigIntegerTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        java.math.BigInteger value = null;
        value = binding.bigIntegerTest(new java.math.BigInteger("0"));
        // TBD - validate results
    }

    public void test43RoundTrip2TestBigIntegerArrayTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        java.math.BigInteger[] value = null;
        value = binding.bigIntegerArrayTest(new java.math.BigInteger[0]);
        // TBD - validate results
    }

    public void test44RoundTrip2TestBigIntegerMultiArrayTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        java.math.BigInteger[][] value = null;
        value = binding.bigIntegerMultiArrayTest(new java.math.BigInteger[0][0]);
        // TBD - validate results
    }

    public void test45RoundTrip2TestBigDecimalTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        value = binding.bigDecimalTest(new java.math.BigDecimal(0));
        // TBD - validate results
    }

    public void test46RoundTrip2TestBigDecimalArrayTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        java.math.BigDecimal[] value = null;
        value = binding.bigDecimalArrayTest(new java.math.BigDecimal[0]);
        // TBD - validate results
    }

    public void test47RoundTrip2TestBigDecimalMultiArrayTest() throws Exception {
        test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub binding;
        try {
            binding = (test.wsdl.roundtrip2.RoundTrip2TestSoapBindingStub)
                          new test.wsdl.roundtrip2.RoundTrip2TestServiceLocator().getRoundTrip2Test();
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
        java.math.BigDecimal[][] value = null;
        value = binding.bigDecimalMultiArrayTest(new java.math.BigDecimal[0][0]);
        // TBD - validate results
    }

}
