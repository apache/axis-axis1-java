/**
 * WrappedInOutTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.wrapped_inout;

public class WrappedInOutTestCase extends junit.framework.TestCase {
    public WrappedInOutTestCase(java.lang.String name) {
        super(name);
    }
    public void test1WrappedInOutEchoString() throws Exception {
        test.wsdl.wrapped_inout.WrappedInOutInterface binding;
        try {
            binding = new test.wsdl.wrapped_inout.WrappedInOutLocator().getWrappedInOut();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        java.lang.String expected = new String("This is a test");
        java.lang.String value = null;
        value = binding.echoString(expected);
        assertEquals(value, expected);
    }

    public void test2WrappedInOutEchoStringIO() throws Exception {
        test.wsdl.wrapped_inout.WrappedInOutInterface binding;
        try {
            binding = new test.wsdl.wrapped_inout.WrappedInOutLocator().getWrappedInOut();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        java.lang.String expected = new String("This is a test");
        java.lang.String value = null;
        value = binding.echoStringIO(expected);
        assertEquals(value, expected);
    }

    public void test3WrappedInOutEchoStringIOret() throws Exception {
        test.wsdl.wrapped_inout.WrappedInOutInterface binding;
        try {
            binding = new test.wsdl.wrapped_inout.WrappedInOutLocator().getWrappedInOut();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        java.lang.String value = null;
        javax.xml.rpc.holders.StringHolder ioarg = new javax.xml.rpc.holders.StringHolder("in1");
        value = binding.echoStringIOret(ioarg);
        assertEquals(value, "return");
        assertNotNull(ioarg);
        assertEquals(ioarg.value, "out1");
    }

    public void test4WrappedInOutEchoStringInIO() throws Exception {
        test.wsdl.wrapped_inout.WrappedInOutInterface binding;
        try {
            binding = new test.wsdl.wrapped_inout.WrappedInOutLocator().getWrappedInOut();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        javax.xml.rpc.holders.StringHolder holder = new javax.xml.rpc.holders.StringHolder("in2");
        binding.echoStringInIO("in1", holder);
        assertNotNull(holder);
        assertEquals(holder.value, "in1");
    }

    public void test5WrappedInOutEchoStringBig() throws Exception {
        test.wsdl.wrapped_inout.WrappedInOutInterface binding;
        try {
            binding = new test.wsdl.wrapped_inout.WrappedInOutLocator().getWrappedInOut();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        java.lang.String value = null;
        javax.xml.rpc.holders.StringHolder ioarg1 = new javax.xml.rpc.holders.StringHolder("ioarg1");
        javax.xml.rpc.holders.StringHolder ioarg2 = new javax.xml.rpc.holders.StringHolder("ioarg2");
        value = binding.echoStringBig("firstin1", ioarg1, ioarg2);
        assertNotNull(value);
        assertEquals(value, "firstin1");
        assertEquals(ioarg1.value, "out1");
        assertEquals(ioarg2.value, "out2");
    }

    public void test6WrappedInOutHelloInOut() throws Exception {
        test.wsdl.wrapped_inout.WrappedInOutInterface binding;
        try {
            binding = new test.wsdl.wrapped_inout.WrappedInOutLocator().getWrappedInOut();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        javax.xml.rpc.holders.StringHolder ioarg1 = new javax.xml.rpc.holders.StringHolder("ioarg1");
        javax.xml.rpc.holders.StringHolder ioarg2 = new javax.xml.rpc.holders.StringHolder("ioarg2");
        javax.xml.rpc.holders.StringHolder ioarg3 = new javax.xml.rpc.holders.StringHolder("ioarg3");
        javax.xml.rpc.holders.StringHolder ioarg4 = new javax.xml.rpc.holders.StringHolder("ioarg4");
        binding.helloInOut(ioarg1, ioarg2, ioarg3, ioarg4, "onlyInput");
        assertEquals(ioarg1.value, "out1");
        assertEquals(ioarg2.value, "out2");
        assertEquals(ioarg3.value, "out3");
        assertEquals(ioarg4.value, "out4");
    }
    
    public void test7WrappedInOutEchoPhone() throws Exception {
        test.wsdl.wrapped_inout.WrappedInOutInterface binding;
        try {
            binding = new test.wsdl.wrapped_inout.WrappedInOutLocator().getWrappedInOut();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        test.wsdl.wrapped_inout.Phone value = null;
        test.wsdl.wrapped_inout.Phone in = new test.wsdl.wrapped_inout.Phone();
        in.setAreaCode("503");
        in.setPrefix("281");
        in.setNumber("0816");
        value = binding.echoPhone(in);
        assertNotNull(value);
        assertEquals(value, in);
    }

}
