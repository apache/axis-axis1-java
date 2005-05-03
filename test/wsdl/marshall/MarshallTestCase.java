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
}
