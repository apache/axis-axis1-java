/**
 * ComplexDocLitServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.interop4.groupH.complexDocLit;

import java.net.URL;

public class ComplexDocLitServiceTestCase extends junit.framework.TestCase {

    public static URL url = null;
    
    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            url = new URL(args[0]);
        } else {
            url = new URL(new ComplexDocLitServiceLocator().getComplexDocLitPortAddress());
        }
        junit.textui.TestRunner.run(new junit.framework.TestSuite(ComplexDocLitServiceTestCase.class));
    } // main
    

    public ComplexDocLitServiceTestCase(java.lang.String name) throws Exception {
        super(name);
        if (url == null) {
            url = new URL(new ComplexDocLitServiceLocator().getComplexDocLitPortAddress());
        }
    }

    /* FIXME: RUNTIME WSDL broken.
    public void testComplexDocLitPortWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new test.wsdl.interop4.groupH.complexDocLit.ComplexDocLitServiceLocator().getComplexDocLitPortAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new test.wsdl.interop4.groupH.complexDocLit.ComplexDocLitServiceLocator().getServiceName());
        assertTrue(service != null);
    }
    */

    public void test1ComplexDocLitPortEchoSOAPStructFault() throws Exception {
        ComplexDocLitPortType binding;
        try {
            binding = new ComplexDocLitServiceLocator().getComplexDocLitPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        SOAPStruct soapStruct = new SOAPStruct();
        soapStruct.setVarFloat(1.1F);
        soapStruct.setVarInt(3);
        soapStruct.setVarString("Fault test");
        
        try {
            binding.echoSOAPStructFault(soapStruct);
        }
        catch (SOAPStructFault e1) {
            assertEquals("SOAPStruct values not equal",
                         soapStruct, e1.getSoapStruct());
            return;
        }
        fail("Should have caught exception!");
    }

    public void test2ComplexDocLitPortEchoBaseStructFault() throws Exception {
        ComplexDocLitPortType binding;
        try {
            binding = new ComplexDocLitServiceLocator().getComplexDocLitPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        short s = 30;
        SOAPStruct soapStruct = new SOAPStruct();
        soapStruct.setVarFloat(1.1F);
        soapStruct.setVarInt(3);
        soapStruct.setVarString("Fault test");
        BaseStruct param = new BaseStruct(soapStruct, s);
        try {
            binding.echoBaseStructFault(param);
        }
        catch (BaseStruct e1) {
            assertEquals("BaseStruct values not equal", param, e1);
            return;
        }
        fail("Should have caught exception!");
    }

    public void test3ComplexDocLitPortEchoExtendedStructFault() throws Exception {
        ComplexDocLitPortType binding;
        try {
            binding = new ComplexDocLitServiceLocator().getComplexDocLitPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        ExtendedStruct extended = new ExtendedStruct();
        SOAPStruct soapStruct = new SOAPStruct();
        soapStruct.setVarFloat(1.1F);
        soapStruct.setVarInt(3);
        soapStruct.setVarString("Fault test");
        
        extended.setIntMessage(1);
        extended.setAnotherIntMessage(2);
        extended.setStructMessage(soapStruct);
        extended.setShortMessage((short)5);
        extended.setStringMessage("This is an ExtendedStruct");
        
        
        try {
            binding.echoExtendedStructFault(extended);
        }
        catch (ExtendedStruct e1) {
            assertEquals("ExtendedStruct values not equal", extended, e1);
            return;
        }

        fail("Should have caught exception!");
    }

    public void test4ComplexDocLitPortEchoMultipleFaults1() throws Exception {
        ComplexDocLitPortType binding;
        try {
            binding = new ComplexDocLitServiceLocator().getComplexDocLitPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        BaseStruct base = new BaseStruct();
        base.setShortMessage((short)4);
        
        SOAPStruct soapStruct = new SOAPStruct();
        soapStruct.setVarFloat(1.1F);
        soapStruct.setVarInt(3);
        soapStruct.setVarString("Fault test");
        
        base.setStructMessage(soapStruct);
        
        SOAPStruct struct = new SOAPStruct();
        struct.setVarFloat(1.1F);
        struct.setVarInt(5);
        struct.setVarString("Twas a dark and stormy night...");

        for (int i = 1; i < 3; i++) {
            try {
                _echoMultipleFaults1Request param = 
                        new _echoMultipleFaults1Request();
                param.setWhichFault(i);
                param.setParam1(struct);
                param.setParam2(base);
                binding.echoMultipleFaults1(param);
            }
            catch (BaseStruct e1) {
                assertEquals("Wrong fault thrown: " + e1.getClass(), 2, i);
                assertEquals("Bad data echoed", base, e1);
                continue;
            }
            catch (SOAPStructFault e2) {
                assertEquals("Wrong fault thrown: " + e2.getClass(), 1, i);
                assertEquals("Bad data echoed", struct, e2.getSoapStruct());
                continue;
            }
            
            fail("Should have caught exception!");
        }
    }

    public void test5ComplexDocLitPortEchoMultipleFaults2() throws Exception {
        ComplexDocLitPortType binding;
        try {
            binding = new ComplexDocLitServiceLocator().getComplexDocLitPort(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        SOAPStruct struct = new SOAPStruct();
        struct.setVarFloat(1.1F);
        struct.setVarInt(5);
        struct.setVarString("Twas a dark and stormy night...");
        
        // Test operation
        BaseStruct base = new BaseStruct();
        base.setShortMessage((short)4);
        base.setStructMessage(struct);
        
        ExtendedStruct extended = new ExtendedStruct();
        extended.setIntMessage(1);
        extended.setAnotherIntMessage(2);
        extended.setShortMessage((short)5);
        extended.setStringMessage("This is an ExtendedStruct");
        extended.setStructMessage(struct);
        
        MoreExtendedStruct moreExtended = new MoreExtendedStruct();
        moreExtended.setBooleanMessage(true);
        moreExtended.setIntMessage(2);
        moreExtended.setAnotherIntMessage(3);
        moreExtended.setShortMessage((short)9);
        moreExtended.setStringMessage("This is a MoreExtendedStruct");
        moreExtended.setStructMessage(struct);
        
        // Test operation multiple times
        for (int i = 1; i < 4; i++) {
            try {
                _echoMultipleFaults2Request param = 
                        new _echoMultipleFaults2Request();
                param.setWhichFault(i);
                param.setParam1(base);
                param.setParam2(extended);
                param.setParam3(moreExtended);
                binding.echoMultipleFaults2(param);
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
