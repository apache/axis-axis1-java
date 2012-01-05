/**
 * MarshallTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC2 Feb 06, 2005 (12:14:42 EST) WSDL2Java emitter.
 */
package test.wsdl.marshall;

import java.math.BigInteger;
import java.util.Iterator;
import javax.xml.namespace.QName;
import org.apache.axis.Message;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.SOAPBody;

public class MarshallTestCase extends junit.framework.TestCase {

    private static final String DIMS = "[3]";

    public MarshallTestCase(java.lang.String name) {
        super(name);
    }

    public void testMarshallPortWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory
                .newInstance();
        java.net.URL url = new java.net.URL(
                new test.wsdl.marshall.MarshallLocator()
                        .getMarshallPortAddress()
                        + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url,
                new test.wsdl.marshall.MarshallLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1MarshallPortMyBeanArray() throws Exception {
        test.wsdl.marshall.MarshallBindingStub binding;
        try {
            binding = (test.wsdl.marshall.MarshallBindingStub) new test.wsdl.marshall.MarshallLocator()
                    .getMarshallPort();
        } catch (javax.xml.rpc.ServiceException jre) {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError(
                    "JAX-RPC ServiceException caught: " + jre);
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
            binding = (test.wsdl.marshall.MarshallBindingStub) new test.wsdl.marshall.MarshallLocator()
                    .getMarshallPort();
        } catch (javax.xml.rpc.ServiceException jre) {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError(
                    "JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);
        // Time out after a minute
        binding.setTimeout(60000);
        // Test operation
        test.wsdl.marshall.types.MyBean[][] value = null;
        value = binding
                .myBeanMultiArray(new test.wsdl.marshall.types.MyBean[0][0]);
        // TBD - validate results
    }

    public void test3MarshallPortMyBean() throws Exception {
        test.wsdl.marshall.MarshallBindingStub binding;
        try {
            binding = (test.wsdl.marshall.MarshallBindingStub) new test.wsdl.marshall.MarshallLocator()
                    .getMarshallPort();
        } catch (javax.xml.rpc.ServiceException jre) {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError(
                    "JAX-RPC ServiceException caught: " + jre);
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
            binding = (test.wsdl.marshall.MarshallBindingStub) new test.wsdl.marshall.MarshallLocator()
                    .getMarshallPort();
        } catch (javax.xml.rpc.ServiceException jre) {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError(
                    "JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);
        // Time out after a minute
        binding.setTimeout(60000);
        // Test operation
        String[] value = new String[] { "1", "2", "", null, "5" };
        String[] ret = null;
        ret = binding.arrayOfSoapEncString(value);
        QName responseQName = new QName("http://marshall.wsdl.test",
                "ArrayOfSoapEncStringResponse");
        QName returnQName = new QName("return");
        Message m = binding._getCall().getResponseMessage();
        SOAPBody body = (SOAPBody) m.getSOAPBody();
        MessageElement response = body.getChildElement(responseQName);
        MessageElement returnE = response.getChildElement(returnQName);
        String arrayType = returnE.getAttributeNS(
                "http://schemas.xmlsoap.org/soap/encoding/", "arrayType");
        assertEquals("wrong array type", "soapenc:string[5]", arrayType);
        // TBD - validate results
    }

    public void test5MarshallPortArrayOfXsdString() throws Exception {
        test.wsdl.marshall.MarshallBindingStub binding;
        try {
            binding = (test.wsdl.marshall.MarshallBindingStub) new test.wsdl.marshall.MarshallLocator()
                    .getMarshallPort();
        } catch (javax.xml.rpc.ServiceException jre) {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError(
                    "JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);
        // Time out after a minute
        binding.setTimeout(60000);
        // Test operation
        String[] value = new String[] { "1", "2", "", null, "5" };
        String[] ret = null;
        ret = binding.arrayOfXsdString(value);
        // TBD - validate results
        QName responseQName = new QName("http://marshall.wsdl.test",
                "ArrayOfXsdStringResponse");
        QName returnQName = new QName("return");
        Message m = binding._getCall().getResponseMessage();
        SOAPBody body = (SOAPBody) m.getSOAPBody();
        MessageElement response = body.getChildElement(responseQName);
        MessageElement returnE = response.getChildElement(returnQName);
        String arrayType = returnE.getAttributeNS(
                "http://schemas.xmlsoap.org/soap/encoding/", "arrayType");
        assertEquals("wrong array type", "xsd:string[5]", arrayType);
    }

    public void test6MarshallPortArrayOfbase64Binary() throws Exception {
        test.wsdl.marshall.MarshallBindingStub binding;
        try {
            binding = (test.wsdl.marshall.MarshallBindingStub) new test.wsdl.marshall.MarshallLocator()
                    .getMarshallPort();
        } catch (javax.xml.rpc.ServiceException jre) {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError(
                    "JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);
        // Time out after a minute
        binding.setTimeout(60000);
        // Test operation
        byte[][] value = null;
        value = binding.arrayOfbase64Binary(getBiDimensionnalByteArray());
        // TBD - validate results
        checkArrayReturnValues(value);
        QName responseQName = new QName("http://marshall.wsdl.test",
                "ArrayOfbase64BinaryResponse");
        String innerTypeString = "xsd:base64Binary";
        String arrayTypeString = innerTypeString + DIMS;
        Message m = binding._getCall().getResponseMessage();
        SOAPBody body = (SOAPBody) m.getSOAPBody();
        checkReturnMessage(body, responseQName, arrayTypeString,
                innerTypeString);
    }

    public void test7MarshallPortArrayOfhexBinary() throws Exception {
        test.wsdl.marshall.MarshallBindingStub binding;
        try {
            binding = (test.wsdl.marshall.MarshallBindingStub) new test.wsdl.marshall.MarshallLocator()
                    .getMarshallPort();
        } catch (javax.xml.rpc.ServiceException jre) {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError(
                    "JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);
        // Time out after a minute
        binding.setTimeout(60000);
        // Test operation
        byte[][] value = null;
        value = binding.arrayOfhexBinary(getBiDimensionnalByteArray());
        // TBD - validate results
        checkArrayReturnValues(value);
        QName responseQName = new QName("http://marshall.wsdl.test",
                "ArrayOfhexBinaryResponse");
        String innerTypeString = "xsd:hexBinary";
        String arrayTypeString = innerTypeString + DIMS;
        Message m = binding._getCall().getResponseMessage();
        SOAPBody body = (SOAPBody) m.getSOAPBody();
        checkReturnMessage(body, responseQName, arrayTypeString,
                innerTypeString);
    }

    public void test8MarshallPortArrayOfsoapencbase64() throws Exception {
        test.wsdl.marshall.MarshallBindingStub binding;
        try {
            binding = (test.wsdl.marshall.MarshallBindingStub) new test.wsdl.marshall.MarshallLocator()
                    .getMarshallPort();
        } catch (javax.xml.rpc.ServiceException jre) {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError(
                    "JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);
        // Time out after a minute
        binding.setTimeout(60000);
        // Test operation
        byte[][] value = null;
        value = binding.arrayOfsoapencbase64(getBiDimensionnalByteArray());
        // TBD - validate results
        checkArrayReturnValues(value);
        QName responseQName = new QName("http://marshall.wsdl.test",
                "ArrayOfsoapencbase64Response");
        String innerTypeString = "soapenc:base64";
        String arrayTypeString = innerTypeString + DIMS;
        Message m = binding._getCall().getResponseMessage();
        SOAPBody body = (SOAPBody) m.getSOAPBody();
        checkReturnMessage(body, responseQName, arrayTypeString,
                innerTypeString);
    }

    public void test9MarshallPortArrayOfbase64BinaryUnbounded()
            throws Exception {
        test.wsdl.marshall.MarshallBindingStub binding;
        try {
            binding = (test.wsdl.marshall.MarshallBindingStub) new test.wsdl.marshall.MarshallLocator()
                    .getMarshallPort();
        } catch (javax.xml.rpc.ServiceException jre) {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError(
                    "JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);
        // Time out after a minute
        binding.setTimeout(60000);
        // Test operation
        byte[][] value = null;
        value = binding
                .arrayOfbase64BinaryUnbounded(getBiDimensionnalByteArray());
        // TBD - validate results
        checkArrayReturnValues(value);

        QName responseQName = new QName("http://marshall.wsdl.test",
                "ArrayOfbase64BinaryUnboundedResponse");
        String innerTypeString = "xsd:base64Binary";
        String arrayTypeString = innerTypeString + DIMS;
        Message m = binding._getCall().getResponseMessage();
        SOAPBody body = (SOAPBody) m.getSOAPBody();
        checkReturnMessage(body, responseQName, arrayTypeString,
                innerTypeString);
    }

    public void test10MarshallPortArrayOfhexBinaryUnbounded() throws Exception {
        test.wsdl.marshall.MarshallBindingStub binding;
        try {
            binding = (test.wsdl.marshall.MarshallBindingStub) new test.wsdl.marshall.MarshallLocator()
                    .getMarshallPort();
        } catch (javax.xml.rpc.ServiceException jre) {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError(
                    "JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);
        // Time out after a minute
        binding.setTimeout(60000);
        // Test operation
        byte[][] value = null;
        value = binding.arrayOfhexBinaryUnbounded(getBiDimensionnalByteArray());
        // TBD - validate results
        checkArrayReturnValues(value);

        QName responseQName = new QName("http://marshall.wsdl.test",
                "ArrayOfhexBinaryUnboundedResponse");
        String innerTypeString = "xsd:hexBinary";
        String arrayTypeString = innerTypeString + DIMS;
        Message m = binding._getCall().getResponseMessage();
        SOAPBody body = (SOAPBody) m.getSOAPBody();
        checkReturnMessage(body, responseQName, arrayTypeString,
                innerTypeString);
    }

    public void test11MarshallPortArrayOfsoapencbase64Unbounded()
            throws Exception {
        test.wsdl.marshall.MarshallBindingStub binding;
        try {
            binding = (test.wsdl.marshall.MarshallBindingStub) new test.wsdl.marshall.MarshallLocator()
                    .getMarshallPort();
        } catch (javax.xml.rpc.ServiceException jre) {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError(
                    "JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);
        // Time out after a minute
        binding.setTimeout(60000);
        // Test operation
        byte[][] value = null;
        value = binding
                .arrayOfsoapencbase64Unbounded(getBiDimensionnalByteArray());
        // TBD - validate results
        checkArrayReturnValues(value);

        QName responseQName = new QName("http://marshall.wsdl.test",
                "ArrayOfsoapencbase64UnboundedResponse");
        String innerTypeString = "soapenc:base64";
        String arrayTypeString = innerTypeString + DIMS;
        Message m = binding._getCall().getResponseMessage();
        SOAPBody body = (SOAPBody) m.getSOAPBody();
        checkReturnMessage(body, responseQName, arrayTypeString,
                innerTypeString);
    }

    public void test12MarshallPortArrayOfArrayOfSoapEncString() throws Exception {
        test.wsdl.marshall.MarshallBindingStub binding;
        try {
            binding = (test.wsdl.marshall.MarshallBindingStub) new test.wsdl.marshall.MarshallLocator()
                    .getMarshallPort();
        } catch (javax.xml.rpc.ServiceException jre) {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError(
                    "JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);
        // Time out after a minute
        binding.setTimeout(60000);
        // Test operation
        String[] v1 = new String[] { "a1", "a2", "", null, "a5", null};
        String[] v2 = new String[] { "b1", "b2", "", null, "b5", null };
        String[] v3 = new String[] { "c1", "c2", "", null, "c5", null };
        String[][] value = new String[][] {v1, v2, v3};
        String[][] ret = null;
        ret = binding.arrayOfArrayOfSoapEncString(value);

        // print Array
        for(int i = 0; i < ret.length; i++) {
            System.out.print("[");
            for(int j = 0; j < ret[i].length; j++) {
                System.out.print("[" + ret[i][j] + "]");
            }
            System.out.println("]");
        }

        assertEquals("array size incorrect", value.length, ret.length);
        for(int i = 0; i < value.length; i++) {
            assertEquals("array size incorrect", value[i].length, ret[i].length);
            for(int j = 0; j < value[i].length; j++) {
                assertEquals("value not equals", value[i][j], ret[i][j]);
            }
        }

        QName responseQName = new QName("http://marshall.wsdl.test",
                "ArrayOfArrayOfSoapEncStringResponse");
        QName returnQName = new QName("return");
        Message m = binding._getCall().getResponseMessage();

        SOAPBody body = (SOAPBody) m.getSOAPBody();
        MessageElement response = body.getChildElement(responseQName);
        MessageElement returnE = response.getChildElement(returnQName);
        String arrayType = returnE.getAttributeNS(
                "http://schemas.xmlsoap.org/soap/encoding/", "arrayType");
        assertEquals("wrong array type", "soapenc:string[][3]", arrayType);


        for (Iterator it = returnE.getChildElements(returnQName); it.hasNext();) {
            returnE = (MessageElement) it.next();
            arrayType = returnE.getAttributeNS(
                    "http://schemas.xmlsoap.org/soap/encoding/", "arrayType");
            assertEquals("wrong array type", "soapenc:string[6]", arrayType);


            for (Iterator it2 = returnE.getChildElements(returnQName); it2.hasNext();) {
                returnE = (MessageElement) it2.next();
                String xsiType = returnE.getAttributeNS(
                        "http://www.w3.org/2001/XMLSchema-instance", "type");
                assertEquals("wrong xsi type", "soapenc:string", xsiType);


            }
        }
        // TBD - validate results
    }

    public void test13MarshallPortArrayOfArrayOfinteger() throws Exception {
        test.wsdl.marshall.MarshallBindingStub binding;
        try {
            binding = (test.wsdl.marshall.MarshallBindingStub) new test.wsdl.marshall.MarshallLocator()
                    .getMarshallPort();
        } catch (javax.xml.rpc.ServiceException jre) {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError(
                    "JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);
        // Time out after a minute
        binding.setTimeout(60000);
        // Test operation
        BigInteger[] v1 = new BigInteger[] { new BigInteger("-3254687"), new BigInteger("0"), new BigInteger("3254687"), null};
        BigInteger[] v2 = new BigInteger[] { new BigInteger("-3254688"), new BigInteger("0"), new BigInteger("3254688"), null};
        BigInteger[] v3 = new BigInteger[] { new BigInteger("-3254689"), new BigInteger("0"), new BigInteger("3254689"), null};
        BigInteger[][] value = new BigInteger[][] {v1, v2, v3};
        BigInteger[][] ret = null;
        ret = binding.arrayOfArrayOfinteger(value);

        // print Array
        for(int i = 0; i < ret.length; i++) {
            System.out.print("[");
            for(int j = 0; j < ret[i].length; j++) {
                System.out.print("[" + ret[i][j] + "]");
            }
            System.out.println("]");
        }

        assertEquals("array size incorrect", value.length, ret.length);
        for(int i = 0; i < value.length; i++) {
            assertEquals("array size incorrect", value[i].length, ret[i].length);
            for(int j = 0; j < value[i].length; j++) {
                assertEquals("value not equals", value[i][j], ret[i][j]);
            }
        }

        QName responseQName = new QName("http://marshall.wsdl.test",
                "ArrayOfArrayOfintegerResponse");
        QName returnQName = new QName("return");
        Message m = binding._getCall().getResponseMessage();

        SOAPBody body = (SOAPBody) m.getSOAPBody();
        MessageElement response = body.getChildElement(responseQName);
        MessageElement returnE = response.getChildElement(returnQName);
        String arrayType = returnE.getAttributeNS(
                "http://schemas.xmlsoap.org/soap/encoding/", "arrayType");
        assertEquals("wrong array type", "xsd:integer[][3]", arrayType);


        for (Iterator it = returnE.getChildElements(returnQName); it.hasNext();) {
            returnE = (MessageElement) it.next();
            arrayType = returnE.getAttributeNS(
                    "http://schemas.xmlsoap.org/soap/encoding/", "arrayType");
            assertEquals("wrong array type", "xsd:integer[4]", arrayType);


            for (Iterator it2 = returnE.getChildElements(returnQName); it2.hasNext();) {
                returnE = (MessageElement) it2.next();
                // we have multiRef to follow here
                MessageElement real = returnE.getRealElement();
                String xsiType = real.getAttributeNS(
                        "http://www.w3.org/2001/XMLSchema-instance", "type");
                assertEquals("wrong xsi type", "xsd:integer", xsiType);


            }
        }
        // TBD - validate results
    }

    /**
     * @param m
     */
    private void printMessage(Message m) throws Exception {
        System.out.println();
        m.writeTo(System.out);
        System.out.println();
    }

    /**
     * @param responseQName
     * @param arrayTypeString
     * @param innerTypeString
     */
    private void checkReturnMessage(SOAPBody body, QName responseQName,
            String arrayTypeString, String innerTypeString) {
        // Message should looks like this :
        // ns:<methodName>Response
        // return @soapenc:arrayType
        // return @xsi:type
        QName returnQName = new QName("return");
        MessageElement response = body.getChildElement(responseQName);
        // check arrayType attribute
        MessageElement returnE = response.getChildElement(returnQName);
        String arrayType = returnE.getAttributeNS(
                "http://schemas.xmlsoap.org/soap/encoding/", "arrayType");
        assertEquals("wrong array type", arrayTypeString, arrayType);
        for (Iterator i = returnE.getChildElements(returnQName); i.hasNext();) {
            MessageElement ret = (MessageElement) i.next();
            String xsiType = ret.getAttributeNS(
                    "http://www.w3.org/2001/XMLSchema-instance", "type");
            assertNotNull("should have an xsi:type attribute", xsiType);
            assertEquals("wrong xsi:type", innerTypeString, xsiType);
        }
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

}
