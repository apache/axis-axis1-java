/**
 * WhiteMesaSoap12AddTestSvcTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.soap12.additional;

import org.apache.axis.Constants;
import org.apache.axis.AxisFault;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.soap.SOAP12Constants;
import org.apache.axis.enum.Style;
import org.apache.axis.enum.Use;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

/**
 * Additional SOAP 1.2 tests.
 * 
 * For details, see:
 *  
 * http://www.w3.org/2000/xp/Group/2/03/soap1.2implementation.html#addtests
 * 
 * Auto-generated from WhiteMesa's WSDL, with additional coding by:
 * @author Davanum Srinivas (dims@apache.org)
 * @author Glen Daniels (gdaniels@apache.org)
 */ 
public class WhiteMesaSoap12AddTestSvcTestCase extends junit.framework.TestCase {
    public static final String STRING_VAL = "SOAP 1.2 is cool!";
    public static final float FLOAT_VAL = 3.14F;
    public static final int INT_VAL = 69;
    
    public WhiteMesaSoap12AddTestSvcTestCase(java.lang.String name) {
        super(name);
    }
    
    /**
     * Test xmlp-2, using the GET webmethod.
     * 
     * @throws Exception
     */ 
    public void testXMLP2() throws Exception {
        Call call = new Call("http://www.whitemesa.net/soap12/add-test-doc/getTime");
        call.setSOAPVersion(SOAPConstants.SOAP12_CONSTANTS);
        call.setProperty(SOAP12Constants.PROP_WEBMETHOD, "GET");
        call.setOperationStyle(Style.DOCUMENT);
        call.setOperationUse(Use.LITERAL);
        call.invoke();
        SOAPEnvelope env = call.getMessageContext().getResponseMessage().getSOAPEnvelope();
        Object result = env.getFirstBody().getValueAsType(Constants.XSD_TIME);
        assertEquals(org.apache.axis.types.Time.class, result.getClass());
        // Suppose we could check the actual time here too, but we aren't
        // gonna for now.
    }

    /**
     * Test xmlp-3, using the GET webmethod and RPC mode (i.e. deal with
     * the rpc:result element).
     * 
     * @throws Exception
     */ 
    public void testXMLP3() throws Exception {
        Call call = new Call("http://www.whitemesa.net/soap12/add-test-rpc/getTime");
        call.setSOAPVersion(SOAPConstants.SOAP12_CONSTANTS);
        call.setProperty(SOAP12Constants.PROP_WEBMETHOD, "GET");
        call.setOperationStyle(Style.RPC);
        call.setReturnType(Constants.XSD_TIME);
        Object ret = call.invoke("", new Object [] {});
        assertEquals(org.apache.axis.types.Time.class, ret.getClass());
        // Suppose we could check the actual time here too, but we aren't
        // gonna for now.
    }
    
    public void test1Soap12AddTestDocPortEchoString() throws Exception {
        test.wsdl.soap12.additional.Soap12AddTestDocBindingStub binding;
        try {
            binding = (test.wsdl.soap12.additional.Soap12AddTestDocBindingStub)
                          new test.wsdl.soap12.additional.WhiteMesaSoap12AddTestSvcLocator().getSoap12AddTestDocPort();
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
        value = binding.echoString(STRING_VAL);
        assertEquals(STRING_VAL, value);
    }

    public void test2Soap12AddTestDocPortEchoSenderFault() throws Exception {
        test.wsdl.soap12.additional.Soap12AddTestDocBindingStub binding;
        try {
            binding = (test.wsdl.soap12.additional.Soap12AddTestDocBindingStub)
                          new test.wsdl.soap12.additional.WhiteMesaSoap12AddTestSvcLocator().getSoap12AddTestDocPort();
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
            binding.echoSenderFault(STRING_VAL);
        } catch (java.rmi.RemoteException e) {
            if (e instanceof AxisFault) {
                AxisFault af = (AxisFault)e;
                assertEquals(Constants.FAULT_SOAP12_SENDER,
                             af.getFaultCode());
                return; // success
            }
        }
        
        fail("Should have received sender fault!");
    }

    public void test3Soap12AddTestDocPortEchoReceiverFault() throws Exception {
        test.wsdl.soap12.additional.Soap12AddTestDocBindingStub binding;
        try {
            binding = (test.wsdl.soap12.additional.Soap12AddTestDocBindingStub)
                          new test.wsdl.soap12.additional.WhiteMesaSoap12AddTestSvcLocator().getSoap12AddTestDocPort();
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
            binding.echoReceiverFault(STRING_VAL);
        } catch (java.rmi.RemoteException e) {
            if (e instanceof AxisFault) {
                AxisFault af = (AxisFault)e;
                assertEquals(Constants.FAULT_SOAP12_RECEIVER,
                             af.getFaultCode());
                return; // success
            }
        }
        
        fail("Should have received receiver fault!");
    }

    public void test4Soap12AddTestRpcPortEchoVoid() throws Exception {
        test.wsdl.soap12.additional.Soap12AddTestRpcBindingStub binding;
        try {
            binding = (test.wsdl.soap12.additional.Soap12AddTestRpcBindingStub)
                          new test.wsdl.soap12.additional.WhiteMesaSoap12AddTestSvcLocator().getSoap12AddTestRpcPort();
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
        binding.echoVoid();
    }

    public void test5Soap12AddTestRpcPortEchoSimpleTypesAsStruct() throws Exception {
        test.wsdl.soap12.additional.Soap12AddTestRpcBindingStub binding;
        try {
            binding = (test.wsdl.soap12.additional.Soap12AddTestRpcBindingStub)
                          new test.wsdl.soap12.additional.WhiteMesaSoap12AddTestSvcLocator().getSoap12AddTestRpcPort();
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
        test.wsdl.soap12.additional.xsd.SOAPStruct value = null;
        value = binding.echoSimpleTypesAsStruct(STRING_VAL, INT_VAL, FLOAT_VAL);
        assertEquals("Float values differ", FLOAT_VAL, value.getVarFloat(), 0.000001F);
        assertEquals("Int values differ", INT_VAL, value.getVarInt());
        assertEquals("String values differ", STRING_VAL, value.getVarString());
    }

    public void test6Soap12AddTestRpcPortEchoString() throws Exception {
        test.wsdl.soap12.additional.Soap12AddTestRpcBindingStub binding;
        try {
            binding = (test.wsdl.soap12.additional.Soap12AddTestRpcBindingStub)
                          new test.wsdl.soap12.additional.WhiteMesaSoap12AddTestSvcLocator().getSoap12AddTestRpcPort();
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
        value = binding.echoString(STRING_VAL);
        assertEquals(STRING_VAL, value);
    }

    public void test7Soap12AddTestRpcPortEchoSimpleTypesAsStructOfSchemaTypes() throws Exception {
        test.wsdl.soap12.additional.Soap12AddTestRpcBindingStub binding;
        try {
            binding = (test.wsdl.soap12.additional.Soap12AddTestRpcBindingStub)
                          new test.wsdl.soap12.additional.WhiteMesaSoap12AddTestSvcLocator().getSoap12AddTestRpcPort();
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
        test.wsdl.soap12.additional.xsd.SOAPStructTypes value = null;
        value = binding.
                  echoSimpleTypesAsStructOfSchemaTypes(STRING_VAL,
                                                       new Integer(INT_VAL),
                                                       new Float(FLOAT_VAL),
                                                       "another string");
        assertEquals(Constants.XSD_STRING, value.getType1());
        assertEquals(Constants.XSD_INT, value.getType2());
        assertEquals(Constants.XSD_FLOAT, value.getType3());
        assertEquals(Constants.XSD_ANYTYPE, value.getType4());
    }

    public void test8Soap12AddTestRpcPortEchoInteger() throws Exception {
        test.wsdl.soap12.additional.Soap12AddTestRpcBindingStub binding;
        try {
            binding = (test.wsdl.soap12.additional.Soap12AddTestRpcBindingStub)
                          new test.wsdl.soap12.additional.WhiteMesaSoap12AddTestSvcLocator().getSoap12AddTestRpcPort();
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
        int value;
        value = binding.echoInteger(INT_VAL);
        assertEquals(INT_VAL, value);
    }
    
    // getTime is a notification style operation and is unsupported.
    // getTime is a notification style operation and is unsupported.
    public void test9Soap12AddTestDocUpperPortEchoString() throws Exception {
        test.wsdl.soap12.additional.Soap12AddTestDocBindingStub binding;
        try {
            binding = (test.wsdl.soap12.additional.Soap12AddTestDocBindingStub)
                    new test.wsdl.soap12.additional.WhiteMesaSoap12AddTestSvcLocator().getSoap12AddTestDocUpperPort();
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
        value = binding.echoString(STRING_VAL);
        assertEquals(STRING_VAL.toUpperCase(), value);
    }

    public void test10Soap12AddTestDocUpperPortEchoSenderFault() throws Exception {
        test.wsdl.soap12.additional.Soap12AddTestDocBindingStub binding;
        try {
            binding = (test.wsdl.soap12.additional.Soap12AddTestDocBindingStub)
                          new test.wsdl.soap12.additional.WhiteMesaSoap12AddTestSvcLocator().getSoap12AddTestDocUpperPort();
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
        binding.echoSenderFault(new java.lang.String());
        // TBD - validate results
    }

    public void test11Soap12AddTestDocUpperPortEchoReceiverFault() throws Exception {
        test.wsdl.soap12.additional.Soap12AddTestDocBindingStub binding;
        try {
            binding = (test.wsdl.soap12.additional.Soap12AddTestDocBindingStub)
                          new test.wsdl.soap12.additional.WhiteMesaSoap12AddTestSvcLocator().getSoap12AddTestDocUpperPort();
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
        binding.echoReceiverFault(new java.lang.String());
        // TBD - validate results
    }
    
    // getTime is a notification style operation and is unsupported.
    public void test12Soap12AddTestDocIntermediaryPortEchoString() throws Exception {
        test.wsdl.soap12.additional.Soap12AddTestDocBindingStub binding;
        try {
            binding = (test.wsdl.soap12.additional.Soap12AddTestDocBindingStub)
                    new test.wsdl.soap12.additional.WhiteMesaSoap12AddTestSvcLocator().getSoap12AddTestDocIntermediaryPort();
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
        value = binding.echoString(STRING_VAL);
        assertEquals(STRING_VAL, value);
    }

    public void test13Soap12AddTestDocIntermediaryPortEchoSenderFault() throws Exception {
        test.wsdl.soap12.additional.Soap12AddTestDocBindingStub binding;
        try {
            binding = (test.wsdl.soap12.additional.Soap12AddTestDocBindingStub)
                          new test.wsdl.soap12.additional.WhiteMesaSoap12AddTestSvcLocator().getSoap12AddTestDocIntermediaryPort();
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
        binding.echoSenderFault(new java.lang.String());
        // TBD - validate results
    }

    public void test14Soap12AddTestDocIntermediaryPortEchoReceiverFault() throws Exception {
        test.wsdl.soap12.additional.Soap12AddTestDocBindingStub binding;
        try {
            binding = (test.wsdl.soap12.additional.Soap12AddTestDocBindingStub)
                          new test.wsdl.soap12.additional.WhiteMesaSoap12AddTestSvcLocator().getSoap12AddTestDocIntermediaryPort();
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
        binding.echoReceiverFault(new java.lang.String());
        // TBD - validate results
    }

}
