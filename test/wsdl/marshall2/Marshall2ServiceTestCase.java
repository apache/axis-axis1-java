/**
 * Marshall2ServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC3 Feb 14, 2005 (08:56:33 EST) WSDL2Java emitter.
 */

package test.wsdl.marshall2;

import org.apache.axis.Constants;

import javax.xml.namespace.QName;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.net.URI;

import test.wsdl.marshall2.types.JavaBean;
import test.wsdl.marshall2.types.JavaBean2;

public class Marshall2ServiceTestCase extends junit.framework.TestCase {
    public Marshall2ServiceTestCase(java.lang.String name) {
        super(name);
    }

    public void testMarshall2PortWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new test.wsdl.marshall2.Marshall2ServiceLocator().getMarshall2PortAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new test.wsdl.marshall2.Marshall2ServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1Marshall2PortBigDecimalArrayTest() throws Exception {
        test.wsdl.marshall2.MarshallTestSoapBindingStub binding;
        try {
            binding = (test.wsdl.marshall2.MarshallTestSoapBindingStub)
                          new test.wsdl.marshall2.Marshall2ServiceLocator().getMarshall2Port();
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
        test.wsdl.marshall2.types.BigDecimalArrayTestResponse value = null;
        value = binding.bigDecimalArrayTest(new test.wsdl.marshall2.types.BigDecimalArrayTest(new BigDecimal[]{new BigDecimal("5.0"),new BigDecimal("5.0")}));
        // TBD - validate results
    }

    public void test2Marshall2PortBigDecimalTest() throws Exception {
        test.wsdl.marshall2.MarshallTestSoapBindingStub binding;
        try {
            binding = (test.wsdl.marshall2.MarshallTestSoapBindingStub)
                          new test.wsdl.marshall2.Marshall2ServiceLocator().getMarshall2Port();
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
        test.wsdl.marshall2.types.BigDecimalTestResponse value = null;
        value = binding.bigDecimalTest(new test.wsdl.marshall2.types.BigDecimalTest(new BigDecimal("5.0")));
        // TBD - validate results
    }

    public void test3Marshall2PortBigIntegerArrayTest() throws Exception {
        test.wsdl.marshall2.MarshallTestSoapBindingStub binding;
        try {
            binding = (test.wsdl.marshall2.MarshallTestSoapBindingStub)
                          new test.wsdl.marshall2.Marshall2ServiceLocator().getMarshall2Port();
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
        test.wsdl.marshall2.types.BigIntegerArrayTestResponse value = null;
        value = binding.bigIntegerArrayTest(new test.wsdl.marshall2.types.BigIntegerArrayTest(new BigInteger[]{new BigInteger("5"),new BigInteger("6")}));
        // TBD - validate results
    }

    public void test4Marshall2PortBigIntegerTest() throws Exception {
        test.wsdl.marshall2.MarshallTestSoapBindingStub binding;
        try {
            binding = (test.wsdl.marshall2.MarshallTestSoapBindingStub)
                          new test.wsdl.marshall2.Marshall2ServiceLocator().getMarshall2Port();
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
        test.wsdl.marshall2.types.BigIntegerTestResponse value = null;
        value = binding.bigIntegerTest(new test.wsdl.marshall2.types.BigIntegerTest(new BigInteger("8")));
        // TBD - validate results
    }

    public void test5Marshall2PortBooleanArrayTest() throws Exception {
        test.wsdl.marshall2.MarshallTestSoapBindingStub binding;
        try {
            binding = (test.wsdl.marshall2.MarshallTestSoapBindingStub)
                          new test.wsdl.marshall2.Marshall2ServiceLocator().getMarshall2Port();
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
        test.wsdl.marshall2.types.BooleanArrayTestResponse value = null;
        value = binding.booleanArrayTest(new test.wsdl.marshall2.types.BooleanArrayTest(new boolean[]{true,false}));
        // TBD - validate results
    }

    public void test6Marshall2PortBooleanTest() throws Exception {
        test.wsdl.marshall2.MarshallTestSoapBindingStub binding;
        try {
            binding = (test.wsdl.marshall2.MarshallTestSoapBindingStub)
                          new test.wsdl.marshall2.Marshall2ServiceLocator().getMarshall2Port();
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
        test.wsdl.marshall2.types.BooleanTestResponse value = null;
        value = binding.booleanTest(new test.wsdl.marshall2.types.BooleanTest(true));
        // TBD - validate results
    }

    public void test7Marshall2PortByteArrayTest() throws Exception {
        test.wsdl.marshall2.MarshallTestSoapBindingStub binding;
        try {
            binding = (test.wsdl.marshall2.MarshallTestSoapBindingStub)
                          new test.wsdl.marshall2.Marshall2ServiceLocator().getMarshall2Port();
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
        test.wsdl.marshall2.types.ByteArrayTestResponse value = null;
        value = binding.byteArrayTest(new test.wsdl.marshall2.types.ByteArrayTest("hello".getBytes()));
        // TBD - validate results
    }

    public void test8Marshall2PortByteTest() throws Exception {
        test.wsdl.marshall2.MarshallTestSoapBindingStub binding;
        try {
            binding = (test.wsdl.marshall2.MarshallTestSoapBindingStub)
                          new test.wsdl.marshall2.Marshall2ServiceLocator().getMarshall2Port();
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
        test.wsdl.marshall2.types.ByteTestResponse value = null;
        value = binding.byteTest(new test.wsdl.marshall2.types.ByteTest("x".getBytes()[0]));
        // TBD - validate results
    }

    public void test9Marshall2PortDoubleArrayTest() throws Exception {
        test.wsdl.marshall2.MarshallTestSoapBindingStub binding;
        try {
            binding = (test.wsdl.marshall2.MarshallTestSoapBindingStub)
                          new test.wsdl.marshall2.Marshall2ServiceLocator().getMarshall2Port();
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
        test.wsdl.marshall2.types.DoubleArrayTestResponse value = null;
        value = binding.doubleArrayTest(new test.wsdl.marshall2.types.DoubleArrayTest(new double[]{4,5}));
        // TBD - validate results
    }

    public void test10Marshall2PortDoubleTest() throws Exception {
        test.wsdl.marshall2.MarshallTestSoapBindingStub binding;
        try {
            binding = (test.wsdl.marshall2.MarshallTestSoapBindingStub)
                          new test.wsdl.marshall2.Marshall2ServiceLocator().getMarshall2Port();
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
        test.wsdl.marshall2.types.DoubleTestResponse value = null;
        value = binding.doubleTest(new test.wsdl.marshall2.types.DoubleTest(4));
        // TBD - validate results
    }

    public void test11Marshall2PortFloatArrayTest() throws Exception {
        test.wsdl.marshall2.MarshallTestSoapBindingStub binding;
        try {
            binding = (test.wsdl.marshall2.MarshallTestSoapBindingStub)
                          new test.wsdl.marshall2.Marshall2ServiceLocator().getMarshall2Port();
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
        test.wsdl.marshall2.types.FloatArrayTestResponse value = null;
        value = binding.floatArrayTest(new test.wsdl.marshall2.types.FloatArrayTest(new float[]{67,75}));
        // TBD - validate results
    }

    public void test12Marshall2PortFloatTest() throws Exception {
        test.wsdl.marshall2.MarshallTestSoapBindingStub binding;
        try {
            binding = (test.wsdl.marshall2.MarshallTestSoapBindingStub)
                          new test.wsdl.marshall2.Marshall2ServiceLocator().getMarshall2Port();
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
        test.wsdl.marshall2.types.FloatTestResponse value = null;
        value = binding.floatTest(new test.wsdl.marshall2.types.FloatTest(56));
        // TBD - validate results
    }

    public void test13Marshall2PortIntArrayTest() throws Exception {
        test.wsdl.marshall2.MarshallTestSoapBindingStub binding;
        try {
            binding = (test.wsdl.marshall2.MarshallTestSoapBindingStub)
                          new test.wsdl.marshall2.Marshall2ServiceLocator().getMarshall2Port();
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
        test.wsdl.marshall2.types.IntArrayTestResponse value = null;
        value = binding.intArrayTest(new test.wsdl.marshall2.types.IntArrayTest(new int[]{3,4}));
        // TBD - validate results
    }

    public void test14Marshall2PortIntTest() throws Exception {
        test.wsdl.marshall2.MarshallTestSoapBindingStub binding;
        try {
            binding = (test.wsdl.marshall2.MarshallTestSoapBindingStub)
                          new test.wsdl.marshall2.Marshall2ServiceLocator().getMarshall2Port();
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
        test.wsdl.marshall2.types.IntTestResponse value = null;
        value = binding.intTest(new test.wsdl.marshall2.types.IntTest(3));
        // TBD - validate results
    }

    public void test15Marshall2PortLongArrayTest() throws Exception {
        test.wsdl.marshall2.MarshallTestSoapBindingStub binding;
        try {
            binding = (test.wsdl.marshall2.MarshallTestSoapBindingStub)
                          new test.wsdl.marshall2.Marshall2ServiceLocator().getMarshall2Port();
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
        test.wsdl.marshall2.types.LongArrayTestResponse value = null;
        value = binding.longArrayTest(new test.wsdl.marshall2.types.LongArrayTest(new long[]{3,4}));
        // TBD - validate results
    }

    public void test16Marshall2PortLongTest() throws Exception {
        test.wsdl.marshall2.MarshallTestSoapBindingStub binding;
        try {
            binding = (test.wsdl.marshall2.MarshallTestSoapBindingStub)
                          new test.wsdl.marshall2.Marshall2ServiceLocator().getMarshall2Port();
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
        test.wsdl.marshall2.types.LongTestResponse value = null;
        value = binding.longTest(new test.wsdl.marshall2.types.LongTest(5467));
        // TBD - validate results
    }

    public void test17Marshall2PortShortArrayTest() throws Exception {
        test.wsdl.marshall2.MarshallTestSoapBindingStub binding;
        try {
            binding = (test.wsdl.marshall2.MarshallTestSoapBindingStub)
                          new test.wsdl.marshall2.Marshall2ServiceLocator().getMarshall2Port();
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
        test.wsdl.marshall2.types.ShortArrayTestResponse value = null;
        value = binding.shortArrayTest(new test.wsdl.marshall2.types.ShortArrayTest(new short[]{(short)3,(short)4}));
        // TBD - validate results
    }

    public void test18Marshall2PortShortTest() throws Exception {
        test.wsdl.marshall2.MarshallTestSoapBindingStub binding;
        try {
            binding = (test.wsdl.marshall2.MarshallTestSoapBindingStub)
                          new test.wsdl.marshall2.Marshall2ServiceLocator().getMarshall2Port();
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
        test.wsdl.marshall2.types.ShortTestResponse value = null;
        value = binding.shortTest(new test.wsdl.marshall2.types.ShortTest((short)4));
        // TBD - validate results
    }

    public void test19Marshall2PortStringArrayTest() throws Exception {
        test.wsdl.marshall2.MarshallTestSoapBindingStub binding;
        try {
            binding = (test.wsdl.marshall2.MarshallTestSoapBindingStub)
                          new test.wsdl.marshall2.Marshall2ServiceLocator().getMarshall2Port();
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
        test.wsdl.marshall2.types.StringArrayTestResponse value = null;
        value = binding.stringArrayTest(new test.wsdl.marshall2.types.StringArrayTest(new String[]{"foo","bar"}));
        // TBD - validate results
    }

    public void test20Marshall2PortStringTest() throws Exception {
        test.wsdl.marshall2.MarshallTestSoapBindingStub binding;
        try {
            binding = (test.wsdl.marshall2.MarshallTestSoapBindingStub)
                          new test.wsdl.marshall2.Marshall2ServiceLocator().getMarshall2Port();
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
        test.wsdl.marshall2.types.StringTestResponse value = null;
        value = binding.stringTest(new test.wsdl.marshall2.types.StringTest("foo"));
        // TBD - validate results
    }

    public void test21Marshall2PortQnameTest() throws Exception {
        test.wsdl.marshall2.MarshallTestSoapBindingStub binding;
        try {
            binding = (test.wsdl.marshall2.MarshallTestSoapBindingStub)
                          new test.wsdl.marshall2.Marshall2ServiceLocator().getMarshall2Port();
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
        test.wsdl.marshall2.types.QNameTestResponse value = null;
        value = binding.qnameTest(new test.wsdl.marshall2.types.QNameTest(Constants.QNAME_FAULTDETAIL_HOSTNAME));
        // TBD - validate results
    }

    public void test22Marshall2PortQnameArrayTest() throws Exception {
        test.wsdl.marshall2.MarshallTestSoapBindingStub binding;
        try {
            binding = (test.wsdl.marshall2.MarshallTestSoapBindingStub)
                          new test.wsdl.marshall2.Marshall2ServiceLocator().getMarshall2Port();
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
        test.wsdl.marshall2.types.QNameArrayTestResponse value = null;
        value = binding.qnameArrayTest(new test.wsdl.marshall2.types.QNameArrayTest(new QName[]{Constants.QNAME_FAULTDETAIL_HOSTNAME,Constants.QNAME_FAULTDETAIL_EXCEPTIONNAME}));
        // TBD - validate results
    }

    public void test23Marshall2PortCalendarArrayTest() throws Exception {
        test.wsdl.marshall2.MarshallTestSoapBindingStub binding;
        try {
            binding = (test.wsdl.marshall2.MarshallTestSoapBindingStub)
                          new test.wsdl.marshall2.Marshall2ServiceLocator().getMarshall2Port();
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
        test.wsdl.marshall2.types.CalendarArrayTestResponse value = null;
        value = binding.calendarArrayTest(new test.wsdl.marshall2.types.CalendarArrayTest(new Calendar[]{Calendar.getInstance(),Calendar.getInstance()}));
        // TBD - validate results
    }

    public void test24Marshall2PortCalendarTest() throws Exception {
        test.wsdl.marshall2.MarshallTestSoapBindingStub binding;
        try {
            binding = (test.wsdl.marshall2.MarshallTestSoapBindingStub)
                          new test.wsdl.marshall2.Marshall2ServiceLocator().getMarshall2Port();
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
        test.wsdl.marshall2.types.CalendarTestResponse value = null;
        value = binding.calendarTest(new test.wsdl.marshall2.types.CalendarTest(Calendar.getInstance()));
        // TBD - validate results
    }

    public void test25Marshall2PortJavaBeanArrayTest() throws Exception {
        test.wsdl.marshall2.MarshallTestSoapBindingStub binding;
        try {
            binding = (test.wsdl.marshall2.MarshallTestSoapBindingStub)
                          new test.wsdl.marshall2.Marshall2ServiceLocator().getMarshall2Port();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        JavaBean b = new JavaBean();
        b.setMyBigDecimal(new BigDecimal(4));
        b.setMyBigInteger(new BigInteger("5"));
        b.setMyBoolean(false);
        b.setMyByte((byte)0x1);
        b.setMyCalendar(Calendar.getInstance());
        b.setMyDouble(4);
        b.setMyFloat(5);
        b.setMyInt(6);
        b.setMyLong(3);
        b.setMyString("sdfsdf");
        b.setMyJavaBean(new JavaBean2(new BigDecimal(5),new BigInteger("4"),true,(byte)0x1,Calendar.getInstance(),(double)5,(float)3,(int)1,(long)2,(short)1,"xxx"));

        JavaBean b2 = new JavaBean();
        b2.setMyBigDecimal(new BigDecimal(4));
        b2.setMyBigInteger(new BigInteger("5"));
        b2.setMyBoolean(false);
        b2.setMyByte((byte)0x1);
        b2.setMyCalendar(Calendar.getInstance());
        b2.setMyDouble(4);
        b2.setMyFloat(5);
        b2.setMyInt(6);
        b2.setMyLong(3);
        b2.setMyString("sdfsdf");
        b2.setMyJavaBean(new JavaBean2(new BigDecimal(5),new BigInteger("4"),true,(byte)0x1,Calendar.getInstance(),(double)5,(float)3,(int)1,(long)2,(short)1,"xxx"));
        
        
        // Test operation
        test.wsdl.marshall2.types.JavaBeanArrayTestResponse value = null;
        value = binding.javaBeanArrayTest(new test.wsdl.marshall2.types.JavaBeanArrayTest(new JavaBean[]{b,b2}));
        // TBD - validate results
    }

    public void test26Marshall2PortJavaBeanTest() throws Exception {
        test.wsdl.marshall2.MarshallTestSoapBindingStub binding;
        try {
            binding = (test.wsdl.marshall2.MarshallTestSoapBindingStub)
                          new test.wsdl.marshall2.Marshall2ServiceLocator().getMarshall2Port();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        JavaBean b = new JavaBean();
        b.setMyBigDecimal(new BigDecimal(4));
        b.setMyBigInteger(new BigInteger("5"));
        b.setMyBoolean(false);
        b.setMyByte((byte)0x1);
        b.setMyCalendar(Calendar.getInstance());
        b.setMyDouble(4);
        b.setMyFloat(5);
        b.setMyInt(6);
        b.setMyLong(3);
        b.setMyString("sdfsdf");
        b.setMyJavaBean(new JavaBean2(new BigDecimal(5),new BigInteger("4"),true,(byte)0x1,Calendar.getInstance(),(double)5,(float)3,(int)1,(long)2,(short)1,"xxx"));
        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        test.wsdl.marshall2.types.JavaBeanTestResponse value = null;
        value = binding.javaBeanTest(new test.wsdl.marshall2.types.JavaBeanTest(b));
        // TBD - validate results
    }

    public void test27Marshall2PortAnyURITest() throws Exception {
        test.wsdl.marshall2.MarshallTestSoapBindingStub binding;
        try {
            binding = (test.wsdl.marshall2.MarshallTestSoapBindingStub)
                          new test.wsdl.marshall2.Marshall2ServiceLocator().getMarshall2Port();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        URI b = new URI("urn:something");
        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        test.wsdl.marshall2.types.FooAnyURITypeResponse value = null;
        value = binding.fooAnyURITest(new test.wsdl.marshall2.types.FooAnyURIType(b));
        // TBD - validate results
    }
}
