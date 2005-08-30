/**
 * ComplexRpcEncServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.interop4.groupH.complexRPCenc;

import java.net.URL;

public class ComplexRpcEncServiceTestCase extends junit.framework.TestCase {
    
    public static URL url = null;
    
    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            url = new URL(args[0]);
        } else {
            url = new URL(new ComplexRpcEncServiceLocator().getComplexRpcEncPortAddress());
        }
        junit.textui.TestRunner.run(new junit.framework.TestSuite(ComplexRpcEncServiceTestCase.class));
    } // main
    
    
    public ComplexRpcEncServiceTestCase(java.lang.String name) throws Exception {
        super(name);
        if (url == null) {
            url = new URL(new ComplexRpcEncServiceLocator().getComplexRpcEncPortAddress());
        }
    }
    public void test1ComplexRpcEncPortEchoSOAPStructFault() throws Exception {
        test.wsdl.interop4.groupH.complexRPCenc.ComplexRpcEncPortType binding;
        try {
            binding = new test.wsdl.interop4.groupH.complexRPCenc.ComplexRpcEncServiceLocator().getComplexRpcEncPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        SOAPStruct soapStruct = new SOAPStruct();
        soapStruct.setVarFloat(1.1F);
        soapStruct.setVarInt(3);
        soapStruct.setVarString("Fault test");

        // Test operation
        try {
            SOAPStructFault param = new SOAPStructFault(soapStruct);
            binding.echoSOAPStructFault(param);
        }
        catch (test.wsdl.interop4.groupH.complexRPCenc.SOAPStructFault e1) {
            assertEquals("SOAPStruct values not equal",
                         soapStruct, e1.getSoapStruct());
            return;
        }
        fail("Should have caught exception!");
    }

    public void test2ComplexRpcEncPortEchoBaseStructFault() throws Exception {
        test.wsdl.interop4.groupH.complexRPCenc.ComplexRpcEncPortType binding;
        try {
            binding = new test.wsdl.interop4.groupH.complexRPCenc.ComplexRpcEncServiceLocator().getComplexRpcEncPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        short s = 30;
        BaseStruct param = new BaseStruct(1.1F, s);
        // Test operation
        try {
            binding.echoBaseStructFault(param);
        }
        catch (test.wsdl.interop4.groupH.complexRPCenc.BaseStruct e1) {
            assertEquals("BaseStruct values not equal", param, e1);
            return;
        }
        fail("Should have caught exception!");
    }

    public void test3ComplexRpcEncPortEchoExtendedStructFault() throws Exception {
        test.wsdl.interop4.groupH.complexRPCenc.ComplexRpcEncPortType binding;
        try {
            binding = new test.wsdl.interop4.groupH.complexRPCenc.ComplexRpcEncServiceLocator().getComplexRpcEncPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        ExtendedStruct extended = new ExtendedStruct();
        extended.setIntMessage(1);
        extended.setAnotherIntMessage(2);
        extended.setFloatMessage(3.3F);
        extended.setShortMessage((short)5);
        extended.setStringMessage("This is an ExtendedStruct");
        
        // Test operation
        try {
            binding.echoExtendedStructFault(extended);
        }
        catch (test.wsdl.interop4.groupH.complexRPCenc.ExtendedStruct e1) {
            assertEquals("ExtendedStruct values not equal", extended, e1);
            return;
        }

        fail("Should have caught exception!");
    }

    public void test4ComplexRpcEncPortEchoMultipleFaults1() throws Exception {
        test.wsdl.interop4.groupH.complexRPCenc.ComplexRpcEncPortType binding;
        try {
            binding = new test.wsdl.interop4.groupH.complexRPCenc.ComplexRpcEncServiceLocator().getComplexRpcEncPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        BaseStruct base = new BaseStruct();
        base.setFloatMessage(2.2F);
        base.setShortMessage((short)4);
        
        SOAPStruct struct = new SOAPStruct();
        struct.setVarFloat(1.1F);
        struct.setVarInt(5);
        struct.setVarString("Twas a dark and stormy night...");

        for (int i = 1; i < 3; i++) {
            // Test operation
            try {
                binding.echoMultipleFaults1(i, struct, base);
            }
            catch (test.wsdl.interop4.groupH.complexRPCenc.BaseStruct e1) {
                assertEquals("Wrong fault thrown: " + e1.getClass(), 2, i);
                assertEquals("Bad data echoed", base, e1);
                continue;
            }
            catch (test.wsdl.interop4.groupH.complexRPCenc.SOAPStructFault e2) {
                assertEquals("Wrong fault thrown: " + e2.getClass(), 1, i);
                assertEquals("Bad data echoed", struct, e2.getSoapStruct());
                continue;
            }
            fail("Should have caught exception!");
        }
    }

    public void test5ComplexRpcEncPortEchoMultipleFaults2() throws Exception {
        test.wsdl.interop4.groupH.complexRPCenc.ComplexRpcEncPortType binding;
        try {
            binding = new test.wsdl.interop4.groupH.complexRPCenc.ComplexRpcEncServiceLocator().getComplexRpcEncPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        BaseStruct base = new BaseStruct();
        base.setFloatMessage(2.2F);
        base.setShortMessage((short)4);
        
        ExtendedStruct extended = new ExtendedStruct();
        extended.setIntMessage(1);
        extended.setAnotherIntMessage(2);
        extended.setFloatMessage(3.3F);
        extended.setShortMessage((short)5);
        extended.setStringMessage("This is an ExtendedStruct");
        
        MoreExtendedStruct moreExtended = new MoreExtendedStruct();
        moreExtended.setBooleanMessage(true);
        moreExtended.setIntMessage(2);
        moreExtended.setAnotherIntMessage(3);
        moreExtended.setFloatMessage(6.6F);
        moreExtended.setShortMessage((short)9);
        moreExtended.setStringMessage("This is a MoreExtendedStruct");
        
        // Test operation multiple times
        for (int i = 1; i < 4; i++) {
            try {
                binding.echoMultipleFaults2(i, base, extended, moreExtended);
            }
            catch (MoreExtendedStruct e3) {
                assertEquals("Wrong fault thrown: " + e3.getClass(), 3, i);
                assertEquals("Bad data echoed", moreExtended, e3);
                continue;
            }
            catch (ExtendedStruct e2) {
                assertEquals("Wrong fault thrown: " + e2.getClass(), 2, i);
                assertEquals("Bad data echoed", extended, e2);
                continue;
            }
            catch (BaseStruct e1) {
                assertEquals("Wrong fault thrown: " + e1.getClass(), 1, i);
                assertEquals("Bad data echoed", base, e1);
                continue;
            }
        
            fail("Should have caught exception!");
        }
    }

}
