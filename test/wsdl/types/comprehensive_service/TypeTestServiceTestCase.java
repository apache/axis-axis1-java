/**
 * TypeTestServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2alpha Dec 15, 2003 (12:04:17 EST) WSDL2Java emitter.
 */

package test.wsdl.types.comprehensive_service;

public class TypeTestServiceTestCase extends junit.framework.TestCase {
    public TypeTestServiceTestCase(java.lang.String name) {
        super(name);
    }

    /* FIXME: RUNTIME WSDL broken.
    public void testTypeTestWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTestAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getServiceName());
        assertTrue(service != null);
    }
    */

    public void test1TypeTestAllPrimitivesIn() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        binding.allPrimitivesIn(new java.lang.String(), new java.math.BigInteger("0"), 0, 0, (short)0, new java.math.BigDecimal(0), 0, 0, true, (byte)0, new javax.xml.namespace.QName("http://double-double", "toil-and-trouble"), java.util.Calendar.getInstance(), new byte[0], new byte[0], new java.lang.String(), new java.lang.Boolean(false), new java.lang.Float(0), new java.lang.Double(0), new java.math.BigDecimal(0), new java.lang.Integer(0), new java.lang.Short((short)0), new byte[0], new org.apache.axis.types.Time("15:45:45.275Z"), new org.apache.axis.types.UnsignedLong(0), new org.apache.axis.types.UnsignedInt(0), new org.apache.axis.types.UnsignedShort(0), new org.apache.axis.types.UnsignedByte(0), new org.apache.axis.types.NonNegativeInteger("0"), new org.apache.axis.types.PositiveInteger("1"), new org.apache.axis.types.NonPositiveInteger("0"), new org.apache.axis.types.NegativeInteger("-1"), new org.apache.axis.types.URI("urn:testing"), new org.apache.axis.types.Year(2000), new org.apache.axis.types.Month(1), new org.apache.axis.types.Day(1), new org.apache.axis.types.YearMonth(2000,1), new org.apache.axis.types.MonthDay(1, 1));
        // TBD - validate results
    }

    public void test2TypeTestAllPrimitivesInout() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        binding.allPrimitivesInout(new javax.xml.rpc.holders.StringHolder(new java.lang.String()), new javax.xml.rpc.holders.BigIntegerHolder(new java.math.BigInteger("0")), new javax.xml.rpc.holders.IntHolder(0), new javax.xml.rpc.holders.LongHolder(0), new javax.xml.rpc.holders.ShortHolder((short)0), new javax.xml.rpc.holders.BigDecimalHolder(new java.math.BigDecimal(0)), new javax.xml.rpc.holders.FloatHolder(0), new javax.xml.rpc.holders.DoubleHolder(0), new javax.xml.rpc.holders.BooleanHolder(true), new javax.xml.rpc.holders.ByteHolder((byte)0), new javax.xml.rpc.holders.QNameHolder(new javax.xml.namespace.QName("http://double-double", "toil-and-trouble")), new javax.xml.rpc.holders.CalendarHolder(java.util.Calendar.getInstance()), new javax.xml.rpc.holders.ByteArrayHolder(new byte[0]), new javax.xml.rpc.holders.ByteArrayHolder(new byte[0]), new javax.xml.rpc.holders.StringHolder(new java.lang.String()), new javax.xml.rpc.holders.BooleanWrapperHolder(new java.lang.Boolean(false)), new javax.xml.rpc.holders.FloatWrapperHolder(new java.lang.Float(0)), new javax.xml.rpc.holders.DoubleWrapperHolder(new java.lang.Double(0)), new javax.xml.rpc.holders.BigDecimalHolder(new java.math.BigDecimal(0)), new javax.xml.rpc.holders.IntegerWrapperHolder(new java.lang.Integer(0)), new javax.xml.rpc.holders.ShortWrapperHolder(new java.lang.Short((short)0)), new javax.xml.rpc.holders.ByteArrayHolder(new byte[0]), new org.apache.axis.holders.TimeHolder(new org.apache.axis.types.Time("15:45:45.275Z")), new org.apache.axis.holders.UnsignedLongHolder(new org.apache.axis.types.UnsignedLong(0)), new org.apache.axis.holders.UnsignedIntHolder(new org.apache.axis.types.UnsignedInt(0)), new org.apache.axis.holders.UnsignedShortHolder(new org.apache.axis.types.UnsignedShort(0)), new org.apache.axis.holders.UnsignedByteHolder(new org.apache.axis.types.UnsignedByte(0)), new org.apache.axis.holders.NonNegativeIntegerHolder(new org.apache.axis.types.NonNegativeInteger("0")), new org.apache.axis.holders.PositiveIntegerHolder(new org.apache.axis.types.PositiveInteger("1")), new org.apache.axis.holders.NonPositiveIntegerHolder(new org.apache.axis.types.NonPositiveInteger("0")), new org.apache.axis.holders.NegativeIntegerHolder(new org.apache.axis.types.NegativeInteger("-1")), new org.apache.axis.holders.URIHolder(new org.apache.axis.types.URI("urn:testing")), new org.apache.axis.holders.YearHolder(new org.apache.axis.types.Year(2000)), new org.apache.axis.holders.MonthHolder(new org.apache.axis.types.Month(1)), new org.apache.axis.holders.DayHolder(new org.apache.axis.types.Day(1)), new org.apache.axis.holders.YearMonthHolder(new org.apache.axis.types.YearMonth(2000,1)), new org.apache.axis.holders.MonthDayHolder(new org.apache.axis.types.MonthDay(1, 1)));
        // TBD - validate results
    }

    public void test3TypeTestAllPrimitivesOut() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        binding.allPrimitivesOut(new javax.xml.rpc.holders.StringHolder(), new javax.xml.rpc.holders.BigIntegerHolder(), new javax.xml.rpc.holders.IntHolder(), new javax.xml.rpc.holders.LongHolder(), new javax.xml.rpc.holders.ShortHolder(), new javax.xml.rpc.holders.BigDecimalHolder(), new javax.xml.rpc.holders.FloatHolder(), new javax.xml.rpc.holders.DoubleHolder(), new javax.xml.rpc.holders.BooleanHolder(), new javax.xml.rpc.holders.ByteHolder(), new javax.xml.rpc.holders.QNameHolder(), new javax.xml.rpc.holders.CalendarHolder(), new javax.xml.rpc.holders.ByteArrayHolder(), new javax.xml.rpc.holders.ByteArrayHolder(), new javax.xml.rpc.holders.StringHolder(), new javax.xml.rpc.holders.BooleanWrapperHolder(), new javax.xml.rpc.holders.FloatWrapperHolder(), new javax.xml.rpc.holders.DoubleWrapperHolder(), new javax.xml.rpc.holders.BigDecimalHolder(), new javax.xml.rpc.holders.IntegerWrapperHolder(), new javax.xml.rpc.holders.ShortWrapperHolder(), new javax.xml.rpc.holders.ByteArrayHolder(), new org.apache.axis.holders.TimeHolder(), new org.apache.axis.holders.UnsignedLongHolder(), new org.apache.axis.holders.UnsignedIntHolder(), new org.apache.axis.holders.UnsignedShortHolder(), new org.apache.axis.holders.UnsignedByteHolder(), new org.apache.axis.holders.NonNegativeIntegerHolder(), new org.apache.axis.holders.PositiveIntegerHolder(), new org.apache.axis.holders.NonPositiveIntegerHolder(), new org.apache.axis.holders.NegativeIntegerHolder(), new org.apache.axis.holders.URIHolder(), new org.apache.axis.holders.YearHolder(), new org.apache.axis.holders.MonthHolder(), new org.apache.axis.holders.DayHolder(), new org.apache.axis.holders.YearMonthHolder(), new org.apache.axis.holders.MonthDayHolder());
        // TBD - validate results
    }

    public void test4TypeTestEnumIn() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        binding.enumIn(test.wsdl.types.comprehensive_types.Enum.one);
        // TBD - validate results
    }

    public void test5TypeTestEnumInout() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        binding.enumInout(new test.wsdl.types.comprehensive_types.holders.EnumHolder(test.wsdl.types.comprehensive_types.Enum.one));
        // TBD - validate results
    }

    public void test6TypeTestEnumOut() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        binding.enumOut(new test.wsdl.types.comprehensive_types.holders.EnumHolder());
        // TBD - validate results
    }

    public void test7TypeTestEnumReturn() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        test.wsdl.types.comprehensive_types.Enum value = null;
        value = binding.enumReturn();
        // TBD - validate results
    }

    public void test8TypeTestEnumIntIn() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        binding.enumIntIn(test.wsdl.types.comprehensive_types.EnumInt.value1);
        // TBD - validate results
    }

    public void test9TypeTestEnumIntInout() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        binding.enumIntInout(new test.wsdl.types.comprehensive_types.holders.EnumIntHolder(test.wsdl.types.comprehensive_types.EnumInt.value1));
        // TBD - validate results
    }

    public void test10TypeTestEnumIntOut() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        binding.enumIntOut(new test.wsdl.types.comprehensive_types.holders.EnumIntHolder());
        // TBD - validate results
    }

    public void test11TypeTestEnumIntReturn() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        test.wsdl.types.comprehensive_types.EnumInt value = null;
        value = binding.enumIntReturn();
        // TBD - validate results
    }

    public void test12TypeTestArrayIn() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        binding.arrayIn(new java.lang.String[0]);
        // TBD - validate results
    }

    public void test13TypeTestArrayInout() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        binding.arrayInout(new test.wsdl.types.comprehensive_types.holders.ArrayHolder(new java.lang.String[0]));
        // TBD - validate results
    }

    public void test14TypeTestArrayOut() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        binding.arrayOut(new test.wsdl.types.comprehensive_types.holders.ArrayHolder());
        // TBD - validate results
    }

    public void test15TypeTestArrayReturn() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        value = binding.arrayReturn();
        // TBD - validate results
    }

    public void test16TypeTestArrayMIn() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        binding.arrayMIn(new int[0][0][0]);
        // TBD - validate results
    }

    public void test17TypeTestArrayMInout() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        binding.arrayMInout(new test.wsdl.types.comprehensive_types.holders.ArrayMHolder(new int[0][0][0]));
        // TBD - validate results
    }

    public void test18TypeTestArrayMOut() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        binding.arrayMOut(new test.wsdl.types.comprehensive_types.holders.ArrayMHolder());
        // TBD - validate results
    }

    public void test19TypeTestArrayMReturn() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        int[][][] value = null;
        value = binding.arrayMReturn();
        // TBD - validate results
    }

    public void test20TypeTestComplexAllIn() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        binding.complexAllIn(new test.wsdl.types.comprehensive_types.ComplexAll());
        // TBD - validate results
    }

    public void test21TypeTestComplexAllInout() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        binding.complexAllInout(new test.wsdl.types.comprehensive_types.holders.ComplexAllHolder(new test.wsdl.types.comprehensive_types.ComplexAll()));
        // TBD - validate results
    }

    public void test22TypeTestComplexAllOut() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        binding.complexAllOut(new test.wsdl.types.comprehensive_types.holders.ComplexAllHolder());
        // TBD - validate results
    }

    public void test23TypeTestComplexAllReturn() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        test.wsdl.types.comprehensive_types.ComplexAll value = null;
        value = binding.complexAllReturn();
        // TBD - validate results
    }

    public void test24TypeTestComplexSequenceIn() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        binding.complexSequenceIn(new test.wsdl.types.comprehensive_types.ComplexSequence());
        // TBD - validate results
    }

    public void test25TypeTestComplexSequenceInout() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        binding.complexSequenceInout(new test.wsdl.types.comprehensive_types.holders.ComplexSequenceHolder(new test.wsdl.types.comprehensive_types.ComplexSequence()));
        // TBD - validate results
    }

    public void test26TypeTestComplexSequenceOut() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        binding.complexSequenceOut(new test.wsdl.types.comprehensive_types.holders.ComplexSequenceHolder());
        // TBD - validate results
    }

    public void test27TypeTestComplexSequenceReturn() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        test.wsdl.types.comprehensive_types.ComplexSequence value = null;
        value = binding.complexSequenceReturn();
        // TBD - validate results
    }

    public void test32TypeTestComplexWComplexIn() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        binding.complexWComplexIn(new test.wsdl.types.comprehensive_types.ComplexWComplex());
        // TBD - validate results
    }

    public void test33TypeTestComplexWComplexInout() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        binding.complexWComplexInout(new test.wsdl.types.comprehensive_types.holders.ComplexWComplexHolder(new test.wsdl.types.comprehensive_types.ComplexWComplex()));
        // TBD - validate results
    }

    public void test34TypeTestComplexWComplexOut() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        binding.complexWComplexOut(new test.wsdl.types.comprehensive_types.holders.ComplexWComplexHolder());
        // TBD - validate results
    }

    public void test35TypeTestComplexWComplexReturn() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        test.wsdl.types.comprehensive_types.ComplexWComplex value = null;
        value = binding.complexWComplexReturn();
        // TBD - validate results
    }

    public void test36TypeTestEmptyComplexTypeIn() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        try {
            binding.emptyComplexTypeIn(new test.wsdl.types.comprehensive_types.EmptyComplexType());
        }
        catch (test.wsdl.types.comprehensive_types.EmptyFault e1) {
            throw new junit.framework.AssertionFailedError("emptyFault Exception caught: " + e1);
        }
            // TBD - validate results
    }

    public void test37TypeTestEmptyComplexTypeInout() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        try {
            binding.emptyComplexTypeInout(new test.wsdl.types.comprehensive_types.holders.EmptyComplexTypeHolder(new test.wsdl.types.comprehensive_types.EmptyComplexType()));
        }
        catch (test.wsdl.types.comprehensive_types.EmptyFault e1) {
            throw new junit.framework.AssertionFailedError("emptyFault Exception caught: " + e1);
        }
            // TBD - validate results
    }

    public void test38TypeTestEmptyComplexTypeOut() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        try {
            binding.emptyComplexTypeOut(new test.wsdl.types.comprehensive_types.holders.EmptyComplexTypeHolder());
        }
        catch (test.wsdl.types.comprehensive_types.EmptyFault e1) {
            throw new junit.framework.AssertionFailedError("emptyFault Exception caught: " + e1);
        }
            // TBD - validate results
    }

    public void test39TypeTestEmptyComplexTypeReturn() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        try {
            test.wsdl.types.comprehensive_types.EmptyComplexType value = null;
            value = binding.emptyComplexTypeReturn();
        }
        catch (test.wsdl.types.comprehensive_types.EmptyFault e1) {
            throw new junit.framework.AssertionFailedError("emptyFault Exception caught: " + e1);
        }
            // TBD - validate results
    }

    public void test40TypeTestAnyIn() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        binding.anyIn(new java.lang.String());
        // TBD - validate results
    }

    public void test41TypeTestAnyInout() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        binding.anyInout(new javax.xml.rpc.holders.ObjectHolder(new java.lang.String()));
        // TBD - validate results
    }

    public void test42TypeTestAnyOut() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        binding.anyOut(new javax.xml.rpc.holders.ObjectHolder());
        // TBD - validate results
    }

    public void test43TypeTestAnyReturn() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        java.lang.Object value = null;
        value = binding.anyReturn();
        // TBD - validate results
    }

    public void test44TypeTestAnimalIn() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        binding.animalIn(new test.wsdl.types.comprehensive_types.Animal());
        // TBD - validate results
    }

    public void test45TypeTestAnimalInout() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        binding.animalInout(new test.wsdl.types.comprehensive_types.holders.AnimalHolder(new test.wsdl.types.comprehensive_types.Animal()));
        // TBD - validate results
    }

    public void test46TypeTestAnimalOut() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        binding.animalOut(new test.wsdl.types.comprehensive_types.holders.AnimalHolder());
        // TBD - validate results
    }

    public void test47TypeTestAnimalReturn() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        test.wsdl.types.comprehensive_types.Animal value = null;
        value = binding.animalReturn();
        // TBD - validate results
    }

    public void test48TypeTestCatIn() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        binding.catIn(new test.wsdl.types.comprehensive_types.Cat());
        // TBD - validate results
    }

    public void test49TypeTestCatInout() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        binding.catInout(new test.wsdl.types.comprehensive_types.holders.CatHolder(new test.wsdl.types.comprehensive_types.Cat()));
        // TBD - validate results
    }

    public void test50TypeTestCatOut() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        binding.catOut(new test.wsdl.types.comprehensive_types.holders.CatHolder());
        // TBD - validate results
    }

    public void test51TypeTestCatReturn() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        test.wsdl.types.comprehensive_types.Cat value = null;
        value = binding.catReturn();
        // TBD - validate results
    }

    public void test52TypeTestMethodBoolean() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        value = binding.methodBoolean(true, new javax.xml.rpc.holders.BooleanHolder(true));
        // TBD - validate results
    }

    public void test53TypeTestMethodByte() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        value = binding.methodByte((byte)0, new javax.xml.rpc.holders.ByteHolder((byte)0));
        // TBD - validate results
    }

    public void test54TypeTestMethodShort() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        value = binding.methodShort((short)0, new javax.xml.rpc.holders.ShortHolder((short)0));
        // TBD - validate results
    }

    public void test55TypeTestMethodInt() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        value = binding.methodInt(0, new javax.xml.rpc.holders.IntHolder(0));
        // TBD - validate results
    }

    public void test56TypeTestMethodLong() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        value = binding.methodLong(0, new javax.xml.rpc.holders.LongHolder(0));
        // TBD - validate results
    }

    public void test57TypeTestMethodFloat() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        value = binding.methodFloat(0, new javax.xml.rpc.holders.FloatHolder(0));
        // TBD - validate results
    }

    public void test58TypeTestMethodDouble() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        value = binding.methodDouble(0, new javax.xml.rpc.holders.DoubleHolder(0));
        // TBD - validate results
    }

    public void test59TypeTestMethodString() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        value = binding.methodString(new java.lang.String(), new javax.xml.rpc.holders.StringHolder(new java.lang.String()));
        // TBD - validate results
    }

    public void test60TypeTestMethodInteger() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        value = binding.methodInteger(new java.math.BigInteger("0"), new javax.xml.rpc.holders.BigIntegerHolder(new java.math.BigInteger("0")));
        // TBD - validate results
    }

    public void test61TypeTestMethodDecimal() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        value = binding.methodDecimal(new java.math.BigDecimal(0), new javax.xml.rpc.holders.BigDecimalHolder(new java.math.BigDecimal(0)));
        // TBD - validate results
    }

    public void test62TypeTestMethodDateTime() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        value = binding.methodDateTime(java.util.Calendar.getInstance(), new javax.xml.rpc.holders.CalendarHolder(java.util.Calendar.getInstance()));
        // TBD - validate results
    }

    public void test63TypeTestMethodQName() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        javax.xml.namespace.QName value = null;
        value = binding.methodQName(new javax.xml.namespace.QName("http://double-double", "toil-and-trouble"), new javax.xml.rpc.holders.QNameHolder(new javax.xml.namespace.QName("http://double-double", "toil-and-trouble")));
        // TBD - validate results
    }

    public void test64TypeTestMethodTime() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        org.apache.axis.types.Time value = null;
        value = binding.methodTime(new org.apache.axis.types.Time("15:45:45.275Z"), new org.apache.axis.holders.TimeHolder(new org.apache.axis.types.Time("15:45:45.275Z")));
        // TBD - validate results
    }

    public void test65TypeTestMethodUnsignedLong() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        org.apache.axis.types.UnsignedLong value = null;
        value = binding.methodUnsignedLong(new org.apache.axis.types.UnsignedLong(0), new org.apache.axis.holders.UnsignedLongHolder(new org.apache.axis.types.UnsignedLong(0)));
        // TBD - validate results
    }

    public void test66TypeTestMethodUnsignedInt() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        org.apache.axis.types.UnsignedInt value = null;
        value = binding.methodUnsignedInt(new org.apache.axis.types.UnsignedInt(0), new org.apache.axis.holders.UnsignedIntHolder(new org.apache.axis.types.UnsignedInt(0)));
        // TBD - validate results
    }

    public void test67TypeTestMethodUnsignedShort() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        org.apache.axis.types.UnsignedShort value = null;
        value = binding.methodUnsignedShort(new org.apache.axis.types.UnsignedShort(0), new org.apache.axis.holders.UnsignedShortHolder(new org.apache.axis.types.UnsignedShort(0)));
        // TBD - validate results
    }

    public void test68TypeTestMethodUnsignedByte() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        org.apache.axis.types.UnsignedByte value = null;
        value = binding.methodUnsignedByte(new org.apache.axis.types.UnsignedByte(0), new org.apache.axis.holders.UnsignedByteHolder(new org.apache.axis.types.UnsignedByte(0)));
        // TBD - validate results
    }

    public void test69TypeTestMethodNonNegativeInteger() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        org.apache.axis.types.NonNegativeInteger value = null;
        value = binding.methodNonNegativeInteger(new org.apache.axis.types.NonNegativeInteger("0"), new org.apache.axis.holders.NonNegativeIntegerHolder(new org.apache.axis.types.NonNegativeInteger("0")));
        // TBD - validate results
    }

    public void test70TypeTestMethodPositiveInteger() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        org.apache.axis.types.PositiveInteger value = null;
        value = binding.methodPositiveInteger(new org.apache.axis.types.PositiveInteger("1"), new org.apache.axis.holders.PositiveIntegerHolder(new org.apache.axis.types.PositiveInteger("1")));
        // TBD - validate results
    }

    public void test71TypeTestMethodNonPositiveInteger() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        org.apache.axis.types.NonPositiveInteger value = null;
        value = binding.methodNonPositiveInteger(new org.apache.axis.types.NonPositiveInteger("0"), new org.apache.axis.holders.NonPositiveIntegerHolder(new org.apache.axis.types.NonPositiveInteger("0")));
        // TBD - validate results
    }

    public void test72TypeTestMethodNegativeInteger() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        org.apache.axis.types.NegativeInteger value = null;
        value = binding.methodNegativeInteger(new org.apache.axis.types.NegativeInteger("-1"), new org.apache.axis.holders.NegativeIntegerHolder(new org.apache.axis.types.NegativeInteger("-1")));
        // TBD - validate results
    }

    public void test73TypeTestMethodAnyURI() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        org.apache.axis.types.URI value = null;
        value = binding.methodAnyURI(new org.apache.axis.types.URI("urn:testing"), new org.apache.axis.holders.URIHolder(new org.apache.axis.types.URI("urn:testing")));
        // TBD - validate results
    }

    public void test74TypeTestMethodSimpleAnyURI() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        org.apache.axis.types.URI value = null;
        value = binding.methodSimpleAnyURI(new org.apache.axis.types.URI(), new org.apache.axis.holders.URIHolder(new org.apache.axis.types.URI()));
        // TBD - validate results
    }

    public void test75TypeTestMethodYear() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        org.apache.axis.types.Year value = null;
        value = binding.methodYear(new org.apache.axis.types.Year(2000), new org.apache.axis.holders.YearHolder(new org.apache.axis.types.Year(2000)));
        // TBD - validate results
    }

    public void test76TypeTestMethodMonth() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        org.apache.axis.types.Month value = null;
        value = binding.methodMonth(new org.apache.axis.types.Month(1), new org.apache.axis.holders.MonthHolder(new org.apache.axis.types.Month(1)));
        // TBD - validate results
    }

    public void test77TypeTestMethodDay() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        org.apache.axis.types.Day value = null;
        value = binding.methodDay(new org.apache.axis.types.Day(1), new org.apache.axis.holders.DayHolder(new org.apache.axis.types.Day(1)));
        // TBD - validate results
    }

    public void test78TypeTestMethodYearMonth() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        org.apache.axis.types.YearMonth value = null;
        value = binding.methodYearMonth(new org.apache.axis.types.YearMonth(2000,1), new org.apache.axis.holders.YearMonthHolder(new org.apache.axis.types.YearMonth(2000,1)));
        // TBD - validate results
    }

    public void test79TypeTestMethodMonthDay() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        org.apache.axis.types.MonthDay value = null;
        value = binding.methodMonthDay(new org.apache.axis.types.MonthDay(1, 1), new org.apache.axis.holders.MonthDayHolder(new org.apache.axis.types.MonthDay(1, 1)));
        // TBD - validate results
    }

    public void test80TypeTestMethodSoapString() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        value = binding.methodSoapString(new java.lang.String(), new javax.xml.rpc.holders.StringHolder(new java.lang.String()));
        // TBD - validate results
    }

    public void test81TypeTestMethodSoapBoolean() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        value = binding.methodSoapBoolean(new java.lang.Boolean(false), new javax.xml.rpc.holders.BooleanWrapperHolder(new java.lang.Boolean(false)));
        // TBD - validate results
    }

    public void test82TypeTestMethodSoapFloat() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        value = binding.methodSoapFloat(new java.lang.Float(0), new javax.xml.rpc.holders.FloatWrapperHolder(new java.lang.Float(0)));
        // TBD - validate results
    }

    public void test83TypeTestMethodSoapDouble() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        value = binding.methodSoapDouble(new java.lang.Double(0), new javax.xml.rpc.holders.DoubleWrapperHolder(new java.lang.Double(0)));
        // TBD - validate results
    }

    public void test84TypeTestMethodSoapDecimal() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        value = binding.methodSoapDecimal(new java.math.BigDecimal(0), new javax.xml.rpc.holders.BigDecimalHolder(new java.math.BigDecimal(0)));
        // TBD - validate results
    }

    public void test85TypeTestMethodSoapInt() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        value = binding.methodSoapInt(new java.lang.Integer(0), new javax.xml.rpc.holders.IntegerWrapperHolder(new java.lang.Integer(0)));
        // TBD - validate results
    }

    public void test86TypeTestMethodSoapShort() throws Exception {
        test.wsdl.types.comprehensive_service.TypeTestBindingStub binding;
        try {
            binding = (test.wsdl.types.comprehensive_service.TypeTestBindingStub)
                          new test.wsdl.types.comprehensive_service.TypeTestServiceLocator().getTypeTest();
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
        value = binding.methodSoapShort(new java.lang.Short((short)0), new javax.xml.rpc.holders.ShortWrapperHolder(new java.lang.Short((short)0)));
        // TBD - validate results
    }

}
