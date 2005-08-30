/**
 * TestServiceServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.document;

import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Element;

public class TestServiceServiceTestCase extends junit.framework.TestCase {
    String xml = "<hello>world</hello>";
    public TestServiceServiceTestCase(java.lang.String name) {
        super(name);
    }

    public void testDocumentTestWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new test.wsdl.document.TestServiceServiceLocator().getDocumentTestAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new test.wsdl.document.TestServiceServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1DocumentTestGetElement() throws Exception {
        test.wsdl.document.DocumentTestSoapBindingStub binding;
        try {
            binding = (test.wsdl.document.DocumentTestSoapBindingStub)
                          new test.wsdl.document.TestServiceServiceLocator().getDocumentTest();
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
        org.w3c.dom.Element value = null;
        value = binding.getElement();
        // TBD - validate results
        assertTrue(value != null);
        assertTrue(XMLUtils.ElementToString(value).indexOf(xml)!=-1);
    }

    public void test2DocumentTestGetDocument() throws Exception {
        test.wsdl.document.DocumentTestSoapBindingStub binding;
        try {
            binding = (test.wsdl.document.DocumentTestSoapBindingStub)
                          new test.wsdl.document.TestServiceServiceLocator().getDocumentTest();
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
        org.w3c.dom.Document value = null;
        value = binding.getDocument();
        // TBD - validate results
        assertTrue(value != null);
        assertTrue(XMLUtils.DocumentToString(value).indexOf(xml)!=-1);
    }
}
