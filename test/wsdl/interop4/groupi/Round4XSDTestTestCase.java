/**
 * Round4XSDTestTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.interop4.groupi;

import java.net.URL;
import java.util.Arrays;
import java.util.TimeZone;

public class Round4XSDTestTestCase extends junit.framework.TestCase {
    
    public static URL url = null;
    
    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            url = new URL(args[0]);
        } else {
            url = new URL(new test.wsdl.interop4.groupi.Round4XSDTestLocator().getRound4XSDTestSoapAddress());
        }
        junit.textui.TestRunner.run(new junit.framework.TestSuite(Round4XSDTestTestCase.class));
    } // main
    
    
    public Round4XSDTestTestCase(java.lang.String name) throws Exception {
        super(name);
        if (url == null) {
            url = new URL(new test.wsdl.interop4.groupi.Round4XSDTestLocator().getRound4XSDTestSoapAddress());
        }
    }
    public void test1Round4XSDTestSoapEchoVoid() {
        test.wsdl.interop4.groupi.Round4XSDTestSoap binding;
        try {
            binding = new test.wsdl.interop4.groupi.Round4XSDTestLocator().getRound4XSDTestSoap(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            jre.printStackTrace();
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            binding.echoVoid();
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test2Round4XSDTestSoapEchoInteger() {
        test.wsdl.interop4.groupi.Round4XSDTestSoap binding;
        try {
            binding = new test.wsdl.interop4.groupi.Round4XSDTestLocator().getRound4XSDTestSoap(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            int value = binding.echoInteger(1500);
            assertEquals(value, 1500);
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test3Round4XSDTestSoapEchoFloat() {
        test.wsdl.interop4.groupi.Round4XSDTestSoap binding;
        try {
            binding = new test.wsdl.interop4.groupi.Round4XSDTestLocator().getRound4XSDTestSoap(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            float value = binding.echoFloat(1500);
            assertEquals(1500, value, 0.001F);
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test4Round4XSDTestSoapEchoString() {
        test.wsdl.interop4.groupi.Round4XSDTestSoap binding;
        try {
            binding = new test.wsdl.interop4.groupi.Round4XSDTestLocator().getRound4XSDTestSoap(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            java.lang.String value = null;
            value = binding.echoString(new java.lang.String("Hello World"));
            assertEquals("Hello World", value);
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test5Round4XSDTestSoapEchoBase64() {
        test.wsdl.interop4.groupi.Round4XSDTestSoap binding;
        try {
            binding = new test.wsdl.interop4.groupi.Round4XSDTestLocator().getRound4XSDTestSoap(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            byte[] input = new byte[] {0xC, 0xA, 0xF, 0xE, 0xB, 0xA, 0xB, 0xE};
            byte[] value = null;
            value = binding.echoBase64(input);
            assertTrue("Array values do not match", Arrays.equals(input, value));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test6Round4XSDTestSoapEchoDate() {
        test.wsdl.interop4.groupi.Round4XSDTestSoap binding;
        try {
            binding = new test.wsdl.interop4.groupi.Round4XSDTestLocator().getRound4XSDTestSoap(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            java.util.Calendar input = java.util.Calendar.getInstance();
            input.setTimeZone(TimeZone.getTimeZone("GMT"));
            java.util.Calendar value = null;
            value = binding.echoDate(input);
            value.setTimeZone(TimeZone.getTimeZone("GMT"));
            assertEquals(input, value);
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test7Round4XSDTestSoapEchoComplexType() {
        test.wsdl.interop4.groupi.Round4XSDTestSoap binding;
        try {
            binding = new test.wsdl.interop4.groupi.Round4XSDTestLocator().getRound4XSDTestSoap(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            test.wsdl.interop4.groupi.xsd.SOAPComplexType input = new test.wsdl.interop4.groupi.xsd.SOAPComplexType();
            input.setVarFloat(1024);
            input.setVarInt(2048);
            input.setVarString("Hello World");
            test.wsdl.interop4.groupi.xsd.SOAPComplexType value = binding.echoComplexType(input);
            assertEquals(input, value);
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test8Round4XSDTestSoapEchoIntegerMultiOccurs() {
        test.wsdl.interop4.groupi.Round4XSDTestSoap binding;
        try {
            binding = new test.wsdl.interop4.groupi.Round4XSDTestLocator().getRound4XSDTestSoap(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            test.wsdl.interop4.groupi.ArrayOfInt input = new test.wsdl.interop4.groupi.ArrayOfInt();
            int[] input2 = new int[3];
            input2[0] = 512;
            input2[1] = 1024;
            input2[2] = 2048;
            input.set_int(input2);
            int[] value = null;
            value = binding.echoIntegerMultiOccurs(input);
            assertEquals(input2.length, value.length);
            for(int i=0;i<input2.length;i++) {
                assertEquals("EchoIntegerMultiOccurs: Array values don't match", input2[i], value[i]);
            }
            assertTrue("Array values don't match", Arrays.equals(input2, value));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test9Round4XSDTestSoapEchoFloatMultiOccurs() {
        test.wsdl.interop4.groupi.Round4XSDTestSoap binding;
        try {
            binding = new test.wsdl.interop4.groupi.Round4XSDTestLocator().getRound4XSDTestSoap(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            test.wsdl.interop4.groupi.ArrayOfFloat input = new test.wsdl.interop4.groupi.ArrayOfFloat();
            float[] input2 = new float[3];
            input2[0] = 512;
            input2[1] = 1024;
            input2[2] = 2048;
            input.set_float(input2);
            float[] value = null;
            value = binding.echoFloatMultiOccurs(input);
            assertTrue("Array values don't match", Arrays.equals(input2, value));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test10Round4XSDTestSoapEchoStringMultiOccurs() {
        test.wsdl.interop4.groupi.Round4XSDTestSoap binding;
        try {
            binding = new test.wsdl.interop4.groupi.Round4XSDTestLocator().getRound4XSDTestSoap(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            test.wsdl.interop4.groupi.ArrayOfString input = new test.wsdl.interop4.groupi.ArrayOfString();
            String[] input2 = new String[3];
            input2[0] = "512";
            input2[1] = "1024";
            input2[2] = "2048";
            input.setString(input2);
            java.lang.String[] value = null;
            value = binding.echoStringMultiOccurs(input);
            assertTrue("Array values don't match", Arrays.equals(input2, value));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test11Round4XSDTestSoapEchoComplexTypeMultiOccurs() {
        test.wsdl.interop4.groupi.Round4XSDTestSoap binding;
        try {
            binding = new test.wsdl.interop4.groupi.Round4XSDTestLocator().getRound4XSDTestSoap(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            test.wsdl.interop4.groupi.xsd.ArrayOfSOAPComplexType input = new test.wsdl.interop4.groupi.xsd.ArrayOfSOAPComplexType();
            input.setSOAPComplexType(new test.wsdl.interop4.groupi.xsd.SOAPComplexType[1]);            
            test.wsdl.interop4.groupi.xsd.SOAPComplexType input2 = new test.wsdl.interop4.groupi.xsd.SOAPComplexType();
            input2.setVarFloat(1024);
            input2.setVarInt(2048);
            input2.setVarString("Hello World");
            input.setSOAPComplexType(0,input2);
            test.wsdl.interop4.groupi.xsd.SOAPComplexType[] value = null;
            value = binding.echoComplexTypeMultiOccurs(input);
            assertTrue("Array values don't match", Arrays.equals(input.getSOAPComplexType(), value));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test12Round4XSDTestSoapEchoDecimal() {
        test.wsdl.interop4.groupi.Round4XSDTestSoap binding;
        try {
            binding = new test.wsdl.interop4.groupi.Round4XSDTestLocator().getRound4XSDTestSoap(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            java.math.BigDecimal input = new java.math.BigDecimal(512);
            java.math.BigDecimal value = null;
            value = binding.echoDecimal(input);
            assertEquals(input, value);
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test13Round4XSDTestSoapEchoBoolean() {
        test.wsdl.interop4.groupi.Round4XSDTestSoap binding;
        try {
            binding = new test.wsdl.interop4.groupi.Round4XSDTestLocator().getRound4XSDTestSoap(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            boolean value = false;
            value = binding.echoBoolean(true);
            assertTrue("boolean value is not TRUE", value == true);
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test14Round4XSDTestSoapEchoHexBinary() {
        test.wsdl.interop4.groupi.Round4XSDTestSoap binding;
        try {
            binding = new test.wsdl.interop4.groupi.Round4XSDTestLocator().getRound4XSDTestSoap(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            byte[] input = new byte[] {0xC, 0xA, 0xF, 0xE, 0xB, 0xA, 0xB, 0xE};
            byte[] value = null;
            value = binding.echoHexBinary(input);
            assertTrue("Array values don't match", Arrays.equals(input, value));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test15Round4XSDTestSoapEchoComplexTypeAsSimpleTypes() {
        test.wsdl.interop4.groupi.Round4XSDTestSoap binding;
        try {
            binding = new test.wsdl.interop4.groupi.Round4XSDTestLocator().getRound4XSDTestSoap(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            test.wsdl.interop4.groupi.xsd.SOAPComplexType input = new test.wsdl.interop4.groupi.xsd.SOAPComplexType();
            input.setVarFloat(1024);
            input.setVarInt(2048);
            input.setVarString("Hello World");
            javax.xml.rpc.holders.StringHolder value1 = new javax.xml.rpc.holders.StringHolder();
            javax.xml.rpc.holders.IntHolder value2 = new javax.xml.rpc.holders.IntHolder();
            javax.xml.rpc.holders.FloatHolder value3 = new javax.xml.rpc.holders.FloatHolder();            
            binding.echoComplexTypeAsSimpleTypes(input, value1, value2, value3);
            assertEquals(input.getVarString(), value1.value);
            assertEquals(input.getVarInt(), value2.value);
            assertEquals(input.getVarFloat(), value3.value, 0.01F);
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test16Round4XSDTestSoapEchoSimpleTypesAsComplexType() {
        test.wsdl.interop4.groupi.Round4XSDTestSoap binding;
        try {
            binding = new test.wsdl.interop4.groupi.Round4XSDTestLocator().getRound4XSDTestSoap(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            test.wsdl.interop4.groupi.xsd.SOAPComplexType value = null;
            value = binding.echoSimpleTypesAsComplexType(new java.lang.String("Hello World"), 1024, 2048);
            assertEquals(value.getVarString(), "Hello World");
            assertEquals(value.getVarInt(), 1024);
            assertEquals(value.getVarFloat(),  2048, 0.01F);
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test17Round4XSDTestSoapEchoNestedComplexType() {
        test.wsdl.interop4.groupi.Round4XSDTestSoap binding;
        try {
            binding = new test.wsdl.interop4.groupi.Round4XSDTestLocator().getRound4XSDTestSoap(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            test.wsdl.interop4.groupi.xsd.SOAPComplexTypeComplexType input = new test.wsdl.interop4.groupi.xsd.SOAPComplexTypeComplexType();
            input.setVarFloat(512);
            input.setVarInt(1024);
            input.setVarString("Hello World");
            test.wsdl.interop4.groupi.xsd.SOAPComplexType input2 = new test.wsdl.interop4.groupi.xsd.SOAPComplexType();
            input2.setVarFloat(1024);
            input2.setVarInt(2048);
            input2.setVarString("Hello World");
            input.setVarComplexType(input2);
            test.wsdl.interop4.groupi.xsd.SOAPComplexTypeComplexType value = null;
            value = binding.echoNestedComplexType(input);
            assertEquals(input, value);
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test18Round4XSDTestSoapEchoNestedMultiOccurs() {
        test.wsdl.interop4.groupi.Round4XSDTestSoap binding;
        try {
            binding = new test.wsdl.interop4.groupi.Round4XSDTestLocator().getRound4XSDTestSoap(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            test.wsdl.interop4.groupi.xsd.SOAPMultiOccursComplexType input = new test.wsdl.interop4.groupi.xsd.SOAPMultiOccursComplexType();
            input.setVarFloat(512); 
            input.setVarInt(1024);
            input.setVarString("H W");
            test.wsdl.interop4.groupi.xsd.ArrayOfString varMultiOccurs = new test.wsdl.interop4.groupi.xsd.ArrayOfString(); 
            varMultiOccurs.setString(new String[]{"A","B","C"});
            input.setVarMultiOccurs(varMultiOccurs);
            test.wsdl.interop4.groupi.xsd.SOAPMultiOccursComplexType value = null;
            value = binding.echoNestedMultiOccurs(input);
            assertEquals(input, value);
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test19Round4XSDTestSoapEchoChoice() {
        test.wsdl.interop4.groupi.Round4XSDTestSoap binding;
        try {
            binding = new test.wsdl.interop4.groupi.Round4XSDTestLocator().getRound4XSDTestSoap(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            //TODO: What does this do?
            test.wsdl.interop4.groupi.xsd.ChoiceComplexType input = new test.wsdl.interop4.groupi.xsd.ChoiceComplexType();
            input.setName0("Hello");
            //input.setName1("World");
            test.wsdl.interop4.groupi.xsd.ChoiceComplexType value = null;
            value = binding.echoChoice(input);
            assertEquals(input, value);
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test20Round4XSDTestSoapEchoEnum() {
        test.wsdl.interop4.groupi.Round4XSDTestSoap binding;
        try {
            binding = new test.wsdl.interop4.groupi.Round4XSDTestLocator().getRound4XSDTestSoap(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            test.wsdl.interop4.groupi.xsd.Enum input = test.wsdl.interop4.groupi.xsd.Enum.BitOne;
            test.wsdl.interop4.groupi.xsd.Enum value = null;
            value = binding.echoEnum(input);
            assertEquals(input, value);
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test21Round4XSDTestSoapRetAnyType() {
        test.wsdl.interop4.groupi.Round4XSDTestSoap binding;
        try {
            binding = new test.wsdl.interop4.groupi.Round4XSDTestLocator().getRound4XSDTestSoap(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            java.lang.Object input = new java.lang.String("Hello World");
            java.lang.Object value = null;
            value = binding.echoAnyType(input);
            assertEquals(value, input);
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test22Round4XSDTestSoapRetAny() {
        test.wsdl.interop4.groupi.Round4XSDTestSoap binding;
        try {
            binding = new test.wsdl.interop4.groupi.Round4XSDTestLocator().getRound4XSDTestSoap(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            test.wsdl.interop4.groupi.__echoAnyElement_inputAny input = new test.wsdl.interop4.groupi.__echoAnyElement_inputAny();
            org.apache.axis.message.MessageElement [] _any = new org.apache.axis.message.MessageElement [1];
            _any[0] = new org.apache.axis.message.MessageElement("hello", "world");
            input.set_any(_any);
            test.wsdl.interop4.groupi.__echoAnyElementResponse_return value = null;
            value = binding.echoAnyElement(input);
            assertEquals(value.get_any()[0].toString(), _any[0].toString());
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test23Round4XSDTestSoapEchoVoidSoapHeader() {
        test.wsdl.interop4.groupi.Round4XSDTestSoap binding;
        try {
            binding = new test.wsdl.interop4.groupi.Round4XSDTestLocator().getRound4XSDTestSoap(url);
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            Round4XSDTestSoapStub stub = (Round4XSDTestSoapStub) binding;
            String namespace = url.toString();
            stub.setHeader(namespace, "test1", "header1");
            stub.setHeader(namespace, "test2", "header2");
            binding.echoVoidSoapHeader();
            assertEquals(stub.getHeader(namespace, "test1").getObjectValue(), "header1");
            assertEquals(stub.getHeader(namespace, "test2").getObjectValue(), "header2");
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

}
