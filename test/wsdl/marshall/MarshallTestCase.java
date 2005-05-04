/**
 * MarshallTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC2 Feb 06, 2005 (12:14:42 EST) WSDL2Java emitter.
 */

package test.wsdl.marshall;

import javax.xml.namespace.QName;
import org.apache.axis.Message;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.SOAPBody;

public class MarshallTestCase extends junit.framework.TestCase {
    public MarshallTestCase(java.lang.String name) {
        super(name);
    }

    public void testMarshallPortWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new test.wsdl.marshall.MarshallLocator().getMarshallPortAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new test.wsdl.marshall.MarshallLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1MarshallPortMyBeanArray() throws Exception {
        test.wsdl.marshall.MarshallBindingStub binding;
        try {
            binding = (test.wsdl.marshall.MarshallBindingStub)
                          new test.wsdl.marshall.MarshallLocator().getMarshallPort();
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
        test.wsdl.marshall.types.MyBean[] value = null;
        value = binding.myBeanArray(new test.wsdl.marshall.types.MyBean[0]);
        // TBD - validate results
    }

    public void test2MarshallPortMyBeanMultiArray() throws Exception {
        test.wsdl.marshall.MarshallBindingStub binding;
        try {
            binding = (test.wsdl.marshall.MarshallBindingStub)
                          new test.wsdl.marshall.MarshallLocator().getMarshallPort();
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
        test.wsdl.marshall.types.MyBean[][] value = null;
        value = binding.myBeanMultiArray(new test.wsdl.marshall.types.MyBean[0][0]);
        // TBD - validate results
    }

    public void test3MarshallPortMyBean() throws Exception {
        test.wsdl.marshall.MarshallBindingStub binding;
        try {
            binding = (test.wsdl.marshall.MarshallBindingStub)
                          new test.wsdl.marshall.MarshallLocator().getMarshallPort();
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
        test.wsdl.marshall.types.MyBean value = null;
        value = binding.myBean(new test.wsdl.marshall.types.MyBean());
        // TBD - validate results
    }

    public void test4MarshallPortArrayOfSoapEncString() throws Exception {
        test.wsdl.marshall.MarshallBindingStub binding;
        try {
            binding = (test.wsdl.marshall.MarshallBindingStub)
                    new test.wsdl.marshall.MarshallLocator().getMarshallPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }

        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        String[] value = new String[]{"1", "2", "", null, "5"};
        String[] ret = null;
        ret = binding.arrayOfSoapEncString(value);

        QName responseQName = new QName("http://marshall.wsdl.test", "ArrayOfSoapEncStringResponse");
        QName returnQName = new QName("return");
        Message m = binding._getCall().getResponseMessage();
        SOAPBody body = (SOAPBody) m.getSOAPBody();
        MessageElement response = body.getChildElement(responseQName);
        MessageElement returnE = response.getChildElement(returnQName);
        String arrayType = returnE.getAttributeNS("http://schemas.xmlsoap.org/soap/encoding/", "arrayType");
        assertEquals("wrong array type", "soapenc:string[5]", arrayType);
        // TBD - validate results
    }

    public void test5MarshallPortArrayOfXsdString() throws Exception {
        test.wsdl.marshall.MarshallBindingStub binding;
        try {
            binding = (test.wsdl.marshall.MarshallBindingStub)
                    new test.wsdl.marshall.MarshallLocator().getMarshallPort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }

        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        String[] value = new String[]{"1", "2", "", null, "5"};
        String[] ret = null;
        ret = binding.arrayOfXsdString(value);

        // TBD - validate results
        QName responseQName = new QName("http://marshall.wsdl.test", "ArrayOfXsdStringResponse");
        QName returnQName = new QName("return");
        Message m = binding._getCall().getResponseMessage();
        SOAPBody body = (SOAPBody) m.getSOAPBody();
        MessageElement response = body.getChildElement(responseQName);
        MessageElement returnE = response.getChildElement(returnQName);
        String arrayType = returnE.getAttributeNS("http://schemas.xmlsoap.org/soap/encoding/", "arrayType");
        assertEquals("wrong array type", "xsd:string[5]", arrayType);
    }

    public void test6MarshallPortArrayOfbase64Binary() throws Exception {
        test.wsdl.marshall.MarshallBindingStub binding;
        try {
            binding = (test.wsdl.marshall.MarshallBindingStub)
                          new test.wsdl.marshall.MarshallLocator().getMarshallPort();
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
        value = binding.arrayOfbase64Binary(getBiDimensionnalByteArray());
        // TBD - validate results
        checkArrayReturnValues(value);
    }

    /**
     * @param value
     */
    private void checkArrayReturnValues(byte[][] value) {
        assertEquals("wrong array length", 3, value.length);
        assertEquals("wrong subarray length", 3, value[0].length);
        assertEquals("wrong subarray length", 3, value[1].length);
        assertEquals("wrong subarray length", 3, value[2].length);
        assertEquals("wrong value[0][0]", -127, value[0][0]);
        assertEquals("wrong value[0][1]", 0, value[0][1]);
        assertEquals("wrong value[0][2]", 127, value[0][2]);

        assertEquals("wrong value[1][0]", -127, value[1][0]);
        assertEquals("wrong value[1][1]", 0, value[1][1]);
        assertEquals("wrong value[1][2]", 127, value[1][2]);

        assertEquals("wrong value[2][0]", -127, value[2][0]);
        assertEquals("wrong value[2][1]", 0, value[2][1]);
        assertEquals("wrong value[2][2]", 127, value[2][2]);
    }

    /**
     * @return
     */
    private byte[][] getBiDimensionnalByteArray() {
        byte[][] array = new byte[3][];
        array[0] = new byte[3];
        array[1] = new byte[3];
        array[2] = new byte[3];
        array[0][0] = -127;
        array[0][1] = 0;
        array[0][2] = 127;
        array[1][0] = -127;
        array[1][1] = 0;
        array[1][2] = 127;
        array[2][0] = -127;
        array[2][1] = 0;
        array[2][2] = 127;
        return array;
    }

    public void test7MarshallPortArrayOfhexBinary() throws Exception {
        test.wsdl.marshall.MarshallBindingStub binding;
        try {
            binding = (test.wsdl.marshall.MarshallBindingStub)
                          new test.wsdl.marshall.MarshallLocator().getMarshallPort();
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
        value = binding.arrayOfhexBinary(getBiDimensionnalByteArray());
        // TBD - validate results
        checkArrayReturnValues(value);
    }

    public void test8MarshallPortArrayOfsoapencbase64() throws Exception {
        test.wsdl.marshall.MarshallBindingStub binding;
        try {
            binding = (test.wsdl.marshall.MarshallBindingStub)
                          new test.wsdl.marshall.MarshallLocator().getMarshallPort();
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
        value = binding.arrayOfsoapencbase64(getBiDimensionnalByteArray());
        // TBD - validate results
        checkArrayReturnValues(value);
    }

    public void test9MarshallPortArrayOfbase64BinaryUnbounded() throws Exception {
        test.wsdl.marshall.MarshallBindingStub binding;
        try {
            binding = (test.wsdl.marshall.MarshallBindingStub)
                          new test.wsdl.marshall.MarshallLocator().getMarshallPort();
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
        value = binding.arrayOfbase64BinaryUnbounded(getBiDimensionnalByteArray());
        // TBD - validate results
        checkArrayReturnValues(value);
    }

    public void test10MarshallPortArrayOfhexBinaryUnbounded() throws Exception {
        test.wsdl.marshall.MarshallBindingStub binding;
        try {
            binding = (test.wsdl.marshall.MarshallBindingStub)
                          new test.wsdl.marshall.MarshallLocator().getMarshallPort();
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
        value = binding.arrayOfhexBinaryUnbounded(getBiDimensionnalByteArray());
        // TBD - validate results
        checkArrayReturnValues(value);
    }

    public void test11MarshallPortArrayOfsoapencbase64Unbounded() throws Exception {
        test.wsdl.marshall.MarshallBindingStub binding;
        try {
            binding = (test.wsdl.marshall.MarshallBindingStub)
                          new test.wsdl.marshall.MarshallLocator().getMarshallPort();
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
        value = binding.arrayOfsoapencbase64Unbounded(getBiDimensionnalByteArray());
        // TBD - validate results
        checkArrayReturnValues(value);
    }


}
