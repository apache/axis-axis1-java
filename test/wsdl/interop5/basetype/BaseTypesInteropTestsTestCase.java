/**
 * BaseTypesInteropTestsTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.interop5.basetype;

import java.net.URL;

public class BaseTypesInteropTestsTestCase extends junit.framework.TestCase {
    public static URL url = null;
    
    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            url = new URL(args[0]);
        } else {
            url = new URL(new test.wsdl.interop5.basetype.BaseTypesInteropTestsLocator().getInteropTestsPortAddress());
        }
        junit.textui.TestRunner.run(new junit.framework.TestSuite(BaseTypesInteropTestsTestCase.class));
    } // main

    public BaseTypesInteropTestsTestCase(java.lang.String name) {
        super(name);
    }
    public void test1InteropTestsPortEchoDouble() throws Exception {
        test.wsdl.interop5.basetype.InteropTestsExpType binding;
        try {
            binding = new test.wsdl.interop5.basetype.BaseTypesInteropTestsLocator().getInteropTestsPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        double value = -3;
        value = binding.echoDouble(0);
        // TBD - validate results
    }

    public void test2InteropTestsPortEchoDuration() throws Exception {
        test.wsdl.interop5.basetype.InteropTestsExpType binding;
        try {
            binding = new test.wsdl.interop5.basetype.BaseTypesInteropTestsLocator().getInteropTestsPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        org.apache.axis.types.Duration value = null;
        value = binding.echoDuration(new org.apache.axis.types.Duration());
        // TBD - validate results
    }

    public void test3InteropTestsPortEchoDateTime() throws Exception {
        test.wsdl.interop5.basetype.InteropTestsExpType binding;
        try {
            binding = new test.wsdl.interop5.basetype.BaseTypesInteropTestsLocator().getInteropTestsPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        java.util.Calendar value = null;
        value = binding.echoDateTime(java.util.Calendar.getInstance());
        // TBD - validate results
    }

    public void test4InteropTestsPortEchoTime() throws Exception {
        test.wsdl.interop5.basetype.InteropTestsExpType binding;
        try {
            binding = new test.wsdl.interop5.basetype.BaseTypesInteropTestsLocator().getInteropTestsPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        org.apache.axis.types.Time value = null;
        value = binding.echoTime(new org.apache.axis.types.Time("15:45:45.275Z"));
        // TBD - validate results
    }

    public void test5InteropTestsPortEchoGYearMonth() throws Exception {
        test.wsdl.interop5.basetype.InteropTestsExpType binding;
        try {
            binding = new test.wsdl.interop5.basetype.BaseTypesInteropTestsLocator().getInteropTestsPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        org.apache.axis.types.YearMonth value = null;
        value = binding.echoGYearMonth(new org.apache.axis.types.YearMonth(2000,1));
        // TBD - validate results
    }

    public void test6InteropTestsPortEchoGYear() throws Exception {
        test.wsdl.interop5.basetype.InteropTestsExpType binding;
        try {
            binding = new test.wsdl.interop5.basetype.BaseTypesInteropTestsLocator().getInteropTestsPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        org.apache.axis.types.Year value = null;
        value = binding.echoGYear(new org.apache.axis.types.Year(2000));
        // TBD - validate results
    }

    public void test7InteropTestsPortEchoGMonthDay() throws Exception {
        test.wsdl.interop5.basetype.InteropTestsExpType binding;
        try {
            binding = new test.wsdl.interop5.basetype.BaseTypesInteropTestsLocator().getInteropTestsPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        org.apache.axis.types.MonthDay value = null;
        value = binding.echoGMonthDay(new org.apache.axis.types.MonthDay(1, 1));
        // TBD - validate results
    }

    public void test8InteropTestsPortEchoGDay() throws Exception {
        test.wsdl.interop5.basetype.InteropTestsExpType binding;
        try {
            binding = new test.wsdl.interop5.basetype.BaseTypesInteropTestsLocator().getInteropTestsPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        org.apache.axis.types.Day value = null;
        value = binding.echoGDay(new org.apache.axis.types.Day(1));
        // TBD - validate results
    }

    public void test9InteropTestsPortEchoGMonth() throws Exception {
        test.wsdl.interop5.basetype.InteropTestsExpType binding;
        try {
            binding = new test.wsdl.interop5.basetype.BaseTypesInteropTestsLocator().getInteropTestsPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        org.apache.axis.types.Month value = null;
        value = binding.echoGMonth(new org.apache.axis.types.Month(1));
        // TBD - validate results
    }

    public void test10InteropTestsPortEchoAnyURI() throws Exception {
        test.wsdl.interop5.basetype.InteropTestsExpType binding;
        try {
            binding = new test.wsdl.interop5.basetype.BaseTypesInteropTestsLocator().getInteropTestsPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        org.apache.axis.types.URI value = null;
        value = binding.echoAnyURI(new org.apache.axis.types.URI("urn:testing"));
        // TBD - validate results
    }

    public void test11InteropTestsPortEchoQName() throws Exception {
        test.wsdl.interop5.basetype.InteropTestsExpType binding;
        try {
            binding = new test.wsdl.interop5.basetype.BaseTypesInteropTestsLocator().getInteropTestsPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        javax.xml.namespace.QName value = null;
        value = binding.echoQName(new javax.xml.namespace.QName("http://double-double", "toil-and-trouble"));
        // TBD - validate results
    }

    public void test12InteropTestsPortEchoNotation() throws Exception {
        test.wsdl.interop5.basetype.InteropTestsExpType binding;
        try {
            binding = new test.wsdl.interop5.basetype.BaseTypesInteropTestsLocator().getInteropTestsPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        org.apache.axis.types.Notation value = null;
        value = binding.echoNotation(new org.apache.axis.types.Notation());
        // TBD - validate results
    }

    public void test13InteropTestsPortEchoLanguage() throws Exception {
        test.wsdl.interop5.basetype.InteropTestsExpType binding;
        try {
            binding = new test.wsdl.interop5.basetype.BaseTypesInteropTestsLocator().getInteropTestsPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        org.apache.axis.types.Language value = null;
        value = binding.echoLanguage(new org.apache.axis.types.Language());
        // TBD - validate results
    }

    public void test14InteropTestsPortEchoNMToken() throws Exception {
        test.wsdl.interop5.basetype.InteropTestsExpType binding;
        try {
            binding = new test.wsdl.interop5.basetype.BaseTypesInteropTestsLocator().getInteropTestsPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        org.apache.axis.types.NMToken value = null;
        value = binding.echoNMToken(new org.apache.axis.types.NMToken());
        // TBD - validate results
    }

    public void test15InteropTestsPortEchoNMTokens() throws Exception {
        test.wsdl.interop5.basetype.InteropTestsExpType binding;
        try {
            binding = new test.wsdl.interop5.basetype.BaseTypesInteropTestsLocator().getInteropTestsPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        org.apache.axis.types.NMTokens value = null;
        value = binding.echoNMTokens(new org.apache.axis.types.NMTokens());
        // TBD - validate results
    }

    public void test16InteropTestsPortEchoName() throws Exception {
        test.wsdl.interop5.basetype.InteropTestsExpType binding;
        try {
            binding = new test.wsdl.interop5.basetype.BaseTypesInteropTestsLocator().getInteropTestsPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        org.apache.axis.types.Name value = null;
        value = binding.echoName(new org.apache.axis.types.Name());
        // TBD - validate results
    }

    public void test17InteropTestsPortEchoNCName() throws Exception {
        test.wsdl.interop5.basetype.InteropTestsExpType binding;
        try {
            binding = new test.wsdl.interop5.basetype.BaseTypesInteropTestsLocator().getInteropTestsPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        org.apache.axis.types.NCName value = null;
        value = binding.echoNCName(new org.apache.axis.types.NCName());
        // TBD - validate results
    }

    public void test18InteropTestsPortEchoID() throws Exception {
        test.wsdl.interop5.basetype.InteropTestsExpType binding;
        try {
            binding = new test.wsdl.interop5.basetype.BaseTypesInteropTestsLocator().getInteropTestsPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        org.apache.axis.types.Id value = null;
        value = binding.echoID(new org.apache.axis.types.Id());
        // TBD - validate results
    }

    public void test19InteropTestsPortEchoIDREF() throws Exception {
        test.wsdl.interop5.basetype.InteropTestsExpType binding;
        try {
            binding = new test.wsdl.interop5.basetype.BaseTypesInteropTestsLocator().getInteropTestsPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        org.apache.axis.types.IDRef value = null;
        value = binding.echoIDREF(new org.apache.axis.types.IDRef());
        // TBD - validate results
    }

    public void test20InteropTestsPortEchoIDREFS() throws Exception {
        test.wsdl.interop5.basetype.InteropTestsExpType binding;
        try {
            binding = new test.wsdl.interop5.basetype.BaseTypesInteropTestsLocator().getInteropTestsPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        org.apache.axis.types.IDRefs value = null;
        value = binding.echoIDREFS(new org.apache.axis.types.IDRefs());
        // TBD - validate results
    }

    public void test21InteropTestsPortEchoEntity() throws Exception {
        test.wsdl.interop5.basetype.InteropTestsExpType binding;
        try {
            binding = new test.wsdl.interop5.basetype.BaseTypesInteropTestsLocator().getInteropTestsPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        org.apache.axis.types.Entity value = null;
        value = binding.echoEntity(new org.apache.axis.types.Entity());
        // TBD - validate results
    }

    public void test22InteropTestsPortEchoEntities() throws Exception {
        test.wsdl.interop5.basetype.InteropTestsExpType binding;
        try {
            binding = new test.wsdl.interop5.basetype.BaseTypesInteropTestsLocator().getInteropTestsPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        org.apache.axis.types.Entities value = null;
        value = binding.echoEntities(new org.apache.axis.types.Entities());
        // TBD - validate results
    }

    public void test23InteropTestsPortEchoNonPositiveInteger() throws Exception {
        test.wsdl.interop5.basetype.InteropTestsExpType binding;
        try {
            binding = new test.wsdl.interop5.basetype.BaseTypesInteropTestsLocator().getInteropTestsPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        org.apache.axis.types.NonPositiveInteger value = null;
        value = binding.echoNonPositiveInteger(new org.apache.axis.types.NonPositiveInteger("0"));
        // TBD - validate results
    }

    public void test24InteropTestsPortEchoNegativeInteger() throws Exception {
        test.wsdl.interop5.basetype.InteropTestsExpType binding;
        try {
            binding = new test.wsdl.interop5.basetype.BaseTypesInteropTestsLocator().getInteropTestsPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        org.apache.axis.types.NegativeInteger value = null;
        value = binding.echoNegativeInteger(new org.apache.axis.types.NegativeInteger("-1"));
        // TBD - validate results
    }

    public void test25InteropTestsPortEchoLong() throws Exception {
        test.wsdl.interop5.basetype.InteropTestsExpType binding;
        try {
            binding = new test.wsdl.interop5.basetype.BaseTypesInteropTestsLocator().getInteropTestsPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        long value = -3;
        value = binding.echoLong(0);
        // TBD - validate results
    }

    public void test26InteropTestsPortEchoInt() throws Exception {
        test.wsdl.interop5.basetype.InteropTestsExpType binding;
        try {
            binding = new test.wsdl.interop5.basetype.BaseTypesInteropTestsLocator().getInteropTestsPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        int value = -3;
        value = binding.echoInt(0);
        // TBD - validate results
    }

    public void test27InteropTestsPortEchoShort() throws Exception {
        test.wsdl.interop5.basetype.InteropTestsExpType binding;
        try {
            binding = new test.wsdl.interop5.basetype.BaseTypesInteropTestsLocator().getInteropTestsPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        short value = -3;
        value = binding.echoShort((short)0);
        // TBD - validate results
    }

    public void test28InteropTestsPortEchoByte() throws Exception {
        test.wsdl.interop5.basetype.InteropTestsExpType binding;
        try {
            binding = new test.wsdl.interop5.basetype.BaseTypesInteropTestsLocator().getInteropTestsPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        byte value = -3;
        value = binding.echoByte((byte)0);
        // TBD - validate results
    }

    public void test29InteropTestsPortEchoNonNegativeInteger() throws Exception {
        test.wsdl.interop5.basetype.InteropTestsExpType binding;
        try {
            binding = new test.wsdl.interop5.basetype.BaseTypesInteropTestsLocator().getInteropTestsPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        org.apache.axis.types.NonNegativeInteger value = null;
        value = binding.echoNonNegativeInteger(new org.apache.axis.types.NonNegativeInteger("0"));
        // TBD - validate results
    }

    public void test30InteropTestsPortEchoUnsignedLong() throws Exception {
        test.wsdl.interop5.basetype.InteropTestsExpType binding;
        try {
            binding = new test.wsdl.interop5.basetype.BaseTypesInteropTestsLocator().getInteropTestsPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        org.apache.axis.types.UnsignedLong value = null;
        value = binding.echoUnsignedLong(new org.apache.axis.types.UnsignedLong(0));
        // TBD - validate results
    }

    public void test31InteropTestsPortEchoUnsignedInt() throws Exception {
        test.wsdl.interop5.basetype.InteropTestsExpType binding;
        try {
            binding = new test.wsdl.interop5.basetype.BaseTypesInteropTestsLocator().getInteropTestsPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        org.apache.axis.types.UnsignedInt value = null;
        value = binding.echoUnsignedInt(new org.apache.axis.types.UnsignedInt(0));
        // TBD - validate results
    }

    public void test32InteropTestsPortEchoUnsignedShort() throws Exception {
        test.wsdl.interop5.basetype.InteropTestsExpType binding;
        try {
            binding = new test.wsdl.interop5.basetype.BaseTypesInteropTestsLocator().getInteropTestsPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        org.apache.axis.types.UnsignedShort value = null;
        value = binding.echoUnsignedShort(new org.apache.axis.types.UnsignedShort(0));
        // TBD - validate results
    }

    public void test33InteropTestsPortEchoUnsignedByte() throws Exception {
        test.wsdl.interop5.basetype.InteropTestsExpType binding;
        try {
            binding = new test.wsdl.interop5.basetype.BaseTypesInteropTestsLocator().getInteropTestsPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        org.apache.axis.types.UnsignedByte value = null;
        value = binding.echoUnsignedByte(new org.apache.axis.types.UnsignedByte(0));
        // TBD - validate results
    }

    public void test34InteropTestsPortEchoPositiveInteger() throws Exception {
        test.wsdl.interop5.basetype.InteropTestsExpType binding;
        try {
            binding = new test.wsdl.interop5.basetype.BaseTypesInteropTestsLocator().getInteropTestsPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        org.apache.axis.types.PositiveInteger value = null;
        value = binding.echoPositiveInteger(new org.apache.axis.types.PositiveInteger("1"));
        // TBD - validate results
    }

}
