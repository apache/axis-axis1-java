/**
 * ComplexTypeExtensionsServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.interop5.complextype;

import java.net.URL;

public class ComplexTypeExtensionsServiceTestCase extends junit.framework.TestCase {
    public static URL url = null;
    
    protected void setUp() throws Exception {
        if(url == null) {
            url = new URL(new test.wsdl.interop4.groupG.dime.doc.DimeDOCInteropLocator().getDimeDOCSoapPortAddress());
        }
    }    

    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            url = new URL(args[0]);
        } else {
            url = new URL(new test.wsdl.interop5.complextype.ComplexTypeExtensionsServiceLocator().getComplexTypeExtensionsPortAddress());
        }
        junit.textui.TestRunner.run(new junit.framework.TestSuite(ComplexTypeExtensionsServiceTestCase.class));
    } // main

    public ComplexTypeExtensionsServiceTestCase(java.lang.String name) {
        super(name);
    }
    public void test1ComplexTypeExtensionsPortEchoBaseType_1() throws Exception {
        test.wsdl.interop5.complextype.ComplexTypeExtensionsPortType binding;
        try {
            binding = new test.wsdl.interop5.complextype.ComplexTypeExtensionsServiceLocator().getComplexTypeExtensionsPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        test.wsdl.interop5.complextype.types.BaseType input = new test.wsdl.interop5.complextype.types.BaseType();
        input.setBaseTypeMember1("echoBaseType_1");
        input.setBaseTypeMember2(1);
        test.wsdl.interop5.complextype.types.holders.BaseTypeHolder inputHolder = new test.wsdl.interop5.complextype.types.holders.BaseTypeHolder(input);
        binding.echoBaseType_1(inputHolder);
        test.wsdl.interop5.complextype.types.BaseType output = inputHolder.value;
        // TBD - validate results
        assertEquals(output.getBaseTypeMember1(),input.getBaseTypeMember1());
        assertEquals(output.getBaseTypeMember2(),input.getBaseTypeMember2());
    }

    public void test2ComplexTypeExtensionsPortEchoBaseType_2() throws Exception {
        test.wsdl.interop5.complextype.ComplexTypeExtensionsPortType binding;
        try {
            binding = new test.wsdl.interop5.complextype.ComplexTypeExtensionsServiceLocator().getComplexTypeExtensionsPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        test.wsdl.interop5.complextype.types.L1DerivedType input = new test.wsdl.interop5.complextype.types.L1DerivedType();
        input.setBaseTypeMember1("echoBaseType_2");
        input.setBaseTypeMember2(2);
        input.setL1DerivedTypeMember("L1DerivedType");
        test.wsdl.interop5.complextype.types.holders.BaseTypeHolder inputHolder = new test.wsdl.interop5.complextype.types.holders.BaseTypeHolder(input);
        binding.echoBaseType_2(inputHolder);
        test.wsdl.interop5.complextype.types.L1DerivedType output = (test.wsdl.interop5.complextype.types.L1DerivedType)inputHolder.value;
        // TBD - validate results
        assertEquals(output.getBaseTypeMember1(),input.getBaseTypeMember1());
        assertEquals(output.getBaseTypeMember2(),input.getBaseTypeMember2());
        assertEquals(output.getL1DerivedTypeMember(),input.getL1DerivedTypeMember());
    }

    public void test3ComplexTypeExtensionsPortEchoBaseType_3() throws Exception {
        test.wsdl.interop5.complextype.ComplexTypeExtensionsPortType binding;
        try {
            binding = new test.wsdl.interop5.complextype.ComplexTypeExtensionsServiceLocator().getComplexTypeExtensionsPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        test.wsdl.interop5.complextype.types.L2DerivedType1 input = new test.wsdl.interop5.complextype.types.L2DerivedType1();
        input.setBaseTypeMember1("echoBaseType_3");
        input.setBaseTypeMember2(2);
        input.setL1DerivedTypeMember("L2DerivedType1");
        input.setL2DerivedType1Member(3);
        test.wsdl.interop5.complextype.types.holders.BaseTypeHolder inputHolder = new test.wsdl.interop5.complextype.types.holders.BaseTypeHolder(input);
        binding.echoBaseType_3(inputHolder);
        test.wsdl.interop5.complextype.types.L2DerivedType1 output = (test.wsdl.interop5.complextype.types.L2DerivedType1)inputHolder.value;
        // TBD - validate results
        assertEquals(output.getBaseTypeMember1(),input.getBaseTypeMember1());
        assertEquals(output.getBaseTypeMember2(),input.getBaseTypeMember2());
        assertEquals(output.getL1DerivedTypeMember(),input.getL1DerivedTypeMember());
        assertEquals(output.getL2DerivedType1Member(),input.getL2DerivedType1Member());
    }

    public void test4ComplexTypeExtensionsPortEchoBaseType_4() throws Exception {
        test.wsdl.interop5.complextype.ComplexTypeExtensionsPortType binding;
        try {
            binding = new test.wsdl.interop5.complextype.ComplexTypeExtensionsServiceLocator().getComplexTypeExtensionsPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        test.wsdl.interop5.complextype.types.L2DerivedType2 input = new test.wsdl.interop5.complextype.types.L2DerivedType2();
        input.setBaseTypeMember1("echoBaseType_4");
        input.setBaseTypeMember2(2);
        input.setL1DerivedTypeMember("L2DerivedType2");
        input.setL2DerivedType2Member(new java.math.BigDecimal(100.00));
        test.wsdl.interop5.complextype.types.holders.BaseTypeHolder inputHolder = new test.wsdl.interop5.complextype.types.holders.BaseTypeHolder(input);
        binding.echoBaseType_4(inputHolder);
        test.wsdl.interop5.complextype.types.L2DerivedType2 output = (test.wsdl.interop5.complextype.types.L2DerivedType2)inputHolder.value;
        // TBD - validate results
        assertEquals(output.getBaseTypeMember1(),input.getBaseTypeMember1());
        assertEquals(output.getBaseTypeMember2(),input.getBaseTypeMember2());
        assertEquals(output.getL1DerivedTypeMember(),input.getL1DerivedTypeMember());
        assertEquals(output.getL2DerivedType2Member(),input.getL2DerivedType2Member());
    }

    public void test5ComplexTypeExtensionsPortEchoBaseType_5() throws Exception {
        test.wsdl.interop5.complextype.ComplexTypeExtensionsPortType binding;
        try {
            binding = new test.wsdl.interop5.complextype.ComplexTypeExtensionsServiceLocator().getComplexTypeExtensionsPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        test.wsdl.interop5.complextype.types.L3DerivedType input = new test.wsdl.interop5.complextype.types.L3DerivedType();
        input.setBaseTypeMember1("echoBaseType_5");
        input.setBaseTypeMember2(2);
        input.setL1DerivedTypeMember("L3DerivedType");
        input.setL2DerivedType2Member(new java.math.BigDecimal(100.00));
        input.setL3DerivedTypeMember((short)5);
        test.wsdl.interop5.complextype.types.holders.BaseTypeHolder inputHolder = new test.wsdl.interop5.complextype.types.holders.BaseTypeHolder(input);
        binding.echoBaseType_5(inputHolder);
        test.wsdl.interop5.complextype.types.L3DerivedType output = (test.wsdl.interop5.complextype.types.L3DerivedType)inputHolder.value;
        // TBD - validate results
        assertEquals(output.getBaseTypeMember1(),input.getBaseTypeMember1());
        assertEquals(output.getBaseTypeMember2(),input.getBaseTypeMember2());
        assertEquals(output.getL1DerivedTypeMember(),input.getL1DerivedTypeMember());
        assertEquals(output.getL2DerivedType2Member(),input.getL2DerivedType2Member());
        assertEquals(output.getL3DerivedTypeMember(),input.getL3DerivedTypeMember());
    }

    public void test6ComplexTypeExtensionsPortEchoL1DerivedType_1() throws Exception {
        test.wsdl.interop5.complextype.ComplexTypeExtensionsPortType binding;
        try {
            binding = new test.wsdl.interop5.complextype.ComplexTypeExtensionsServiceLocator().getComplexTypeExtensionsPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        test.wsdl.interop5.complextype.types.L1DerivedType input = new test.wsdl.interop5.complextype.types.L1DerivedType();
        input.setBaseTypeMember1("echoL1DerivedType_1");
        input.setBaseTypeMember2(2);
        input.setL1DerivedTypeMember("L1DerivedType");
        test.wsdl.interop5.complextype.types.holders.L1DerivedTypeHolder inputHolder = new test.wsdl.interop5.complextype.types.holders.L1DerivedTypeHolder(input);
        binding.echoL1DerivedType_1(inputHolder);
        test.wsdl.interop5.complextype.types.L1DerivedType output = inputHolder.value;
        // TBD - validate results
        assertEquals(output.getBaseTypeMember1(),input.getBaseTypeMember1());
        assertEquals(output.getBaseTypeMember2(),input.getBaseTypeMember2());
        assertEquals(output.getL1DerivedTypeMember(),input.getL1DerivedTypeMember());
    }

    public void test7ComplexTypeExtensionsPortEchoL1DerivedType_2() throws Exception {
        test.wsdl.interop5.complextype.ComplexTypeExtensionsPortType binding;
        try {
            binding = new test.wsdl.interop5.complextype.ComplexTypeExtensionsServiceLocator().getComplexTypeExtensionsPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        test.wsdl.interop5.complextype.types.L2DerivedType1 input = new test.wsdl.interop5.complextype.types.L2DerivedType1();
        input.setBaseTypeMember1("echoL1DerivedType_1");
        input.setBaseTypeMember2(2);
        input.setL1DerivedTypeMember("L2DerivedType1");
        input.setL2DerivedType1Member(5);
        test.wsdl.interop5.complextype.types.holders.L1DerivedTypeHolder inputHolder = new test.wsdl.interop5.complextype.types.holders.L1DerivedTypeHolder(input);
        binding.echoL1DerivedType_2(inputHolder);
        test.wsdl.interop5.complextype.types.L2DerivedType1 output = (test.wsdl.interop5.complextype.types.L2DerivedType1)inputHolder.value;
        // TBD - validate results
        assertEquals(output.getBaseTypeMember1(),input.getBaseTypeMember1());
        assertEquals(output.getBaseTypeMember2(),input.getBaseTypeMember2());
        assertEquals(output.getL1DerivedTypeMember(),input.getL1DerivedTypeMember());
        assertEquals(output.getL2DerivedType1Member(),input.getL2DerivedType1Member());
    }

    public void test8ComplexTypeExtensionsPortEchoL2DerivedType1_1() throws Exception {
        test.wsdl.interop5.complextype.ComplexTypeExtensionsPortType binding;
        try {
            binding = new test.wsdl.interop5.complextype.ComplexTypeExtensionsServiceLocator().getComplexTypeExtensionsPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        test.wsdl.interop5.complextype.types.L2DerivedType1 input = new test.wsdl.interop5.complextype.types.L2DerivedType1();
        input.setBaseTypeMember1("echoL2DerivedType1_1");
        input.setBaseTypeMember2(2);
        input.setL1DerivedTypeMember("L2DerivedType1");
        input.setL2DerivedType1Member(5);
        test.wsdl.interop5.complextype.types.holders.L2DerivedType1Holder inputHolder = new test.wsdl.interop5.complextype.types.holders.L2DerivedType1Holder(input);
        binding.echoL2DerivedType1_1(inputHolder);
        test.wsdl.interop5.complextype.types.L2DerivedType1 output = inputHolder.value;
        // TBD - validate results
        assertEquals(output.getBaseTypeMember1(),input.getBaseTypeMember1());
        assertEquals(output.getBaseTypeMember2(),input.getBaseTypeMember2());
        assertEquals(output.getL1DerivedTypeMember(),input.getL1DerivedTypeMember());
        assertEquals(output.getL2DerivedType1Member(),input.getL2DerivedType1Member());
    }

    public void test9ComplexTypeExtensionsPortEchoL1DerivedTypeAsBaseType() throws Exception {
        test.wsdl.interop5.complextype.ComplexTypeExtensionsPortType binding;
        try {
            binding = new test.wsdl.interop5.complextype.ComplexTypeExtensionsServiceLocator().getComplexTypeExtensionsPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        test.wsdl.interop5.complextype.types.L1DerivedType input = new test.wsdl.interop5.complextype.types.L1DerivedType();
        input.setBaseTypeMember1("echoBaseType_2");
        input.setBaseTypeMember2(2);
        input.setL1DerivedTypeMember("L1DerivedType");
        // Test operation
        test.wsdl.interop5.complextype.types.BaseType output = null;
        output = binding.echoL1DerivedTypeAsBaseType(input);
        // TBD - validate results
        assertEquals(output.getBaseTypeMember1(),input.getBaseTypeMember1());
        assertEquals(output.getBaseTypeMember2(),input.getBaseTypeMember2());
    }

    public void test10ComplexTypeExtensionsPortEchoL2DerivedType1AsBaseType() throws Exception {
        test.wsdl.interop5.complextype.ComplexTypeExtensionsPortType binding;
        try {
            binding = new test.wsdl.interop5.complextype.ComplexTypeExtensionsServiceLocator().getComplexTypeExtensionsPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        test.wsdl.interop5.complextype.types.L2DerivedType1 input = new test.wsdl.interop5.complextype.types.L2DerivedType1();
        input.setBaseTypeMember1("echoBaseType_3");
        input.setBaseTypeMember2(2);
        input.setL1DerivedTypeMember("L2DerivedType1");
        input.setL2DerivedType1Member(3);
        // Test operation
        test.wsdl.interop5.complextype.types.BaseType output = null;
        output = binding.echoL2DerivedType1AsBaseType(input);
        // TBD - validate results
        assertEquals(output.getBaseTypeMember1(),input.getBaseTypeMember1());
        assertEquals(output.getBaseTypeMember2(),input.getBaseTypeMember2());
    }

    public void test11ComplexTypeExtensionsPortEchoBaseTypeAsL1DerivedType() throws Exception {
        test.wsdl.interop5.complextype.ComplexTypeExtensionsPortType binding;
        try {
            binding = new test.wsdl.interop5.complextype.ComplexTypeExtensionsServiceLocator().getComplexTypeExtensionsPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        test.wsdl.interop5.complextype.types.L1DerivedType input = new test.wsdl.interop5.complextype.types.L1DerivedType();
        input.setBaseTypeMember1("echoBaseType_2");
        input.setBaseTypeMember2(2);
        input.setL1DerivedTypeMember("L1DerivedType");
        // Test operation
        test.wsdl.interop5.complextype.types.L1DerivedType output = null;
        output = binding.echoBaseTypeAsL1DerivedType(input);
        // TBD - validate results
        assertEquals(output.getBaseTypeMember1(),input.getBaseTypeMember1());
        assertEquals(output.getBaseTypeMember2(),input.getBaseTypeMember2());
        assertEquals(output.getL1DerivedTypeMember(),input.getL1DerivedTypeMember());
    }

    public void test12ComplexTypeExtensionsPortEchoBaseTypeAsL2DerivedType1() throws Exception {
        test.wsdl.interop5.complextype.ComplexTypeExtensionsPortType binding;
        try {
            binding = new test.wsdl.interop5.complextype.ComplexTypeExtensionsServiceLocator().getComplexTypeExtensionsPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        test.wsdl.interop5.complextype.types.L2DerivedType1 input = new test.wsdl.interop5.complextype.types.L2DerivedType1();
        input.setBaseTypeMember1("echoBaseType_3");
        input.setBaseTypeMember2(2);
        input.setL1DerivedTypeMember("L2DerivedType1");
        input.setL2DerivedType1Member(3);
        // Test operation
        test.wsdl.interop5.complextype.types.L2DerivedType1 output = null;
        output = binding.echoBaseTypeAsL2DerivedType1(input);
        // TBD - validate results
        assertEquals(output.getBaseTypeMember1(),input.getBaseTypeMember1());
        assertEquals(output.getBaseTypeMember2(),input.getBaseTypeMember2());
        assertEquals(output.getL1DerivedTypeMember(),input.getL1DerivedTypeMember());
        assertEquals(output.getL2DerivedType1Member(),input.getL2DerivedType1Member());
    }
}
