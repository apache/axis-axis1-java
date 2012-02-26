/**
 * Echo2ServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC3 Mar 22, 2005 (05:08:41 CET) WSDL2Java emitter.
 */
package test.wsdl.echo2;

import java.util.Iterator;
import javax.xml.namespace.QName;
import org.apache.axis.MessageContext;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.PrefixedQName;
import org.apache.axis.message.SOAPBody;

public class Echo2ServiceTestCase extends junit.framework.TestCase {
    public Echo2ServiceTestCase(java.lang.String name) {
        super(name);
    }

    public void testEcho2WSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory
                .newInstance();
        java.net.URL url = new java.net.URL(
                new test.wsdl.echo2.Echo2ServiceLocator().getEcho2Address()
                        + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url,
                new test.wsdl.echo2.Echo2ServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1Echo2EchoMyBase64Bean() throws Exception {
        test.wsdl.echo2.Echo2SoapBindingStub binding;
        binding = (test.wsdl.echo2.Echo2SoapBindingStub) new test.wsdl.echo2.Echo2ServiceLocator()
                .getEcho2();
        assertNotNull("binding is null", binding);
        // Time out after a minute
        binding.setTimeout(60000);

        // message is more clear without multiref
        //binding._setProperty("sendMultiRefs", Boolean.FALSE);
        // Test operation
        test.wsdl.echo2.MyBase64Bean input = new test.wsdl.echo2.MyBase64Bean();
        fillMyBase64Bean(input);
        test.wsdl.echo2.MyBase64Bean ret = binding.echoMyBase64Bean(input);

        // Body
        //  echoMyBase64BeanResponse
        //   return HREF
        //  multiRef : 1 element, no child

        // Check message format
        binding._getCall().getResponseMessage().writeTo(System.out);
        SOAPBody body = (SOAPBody) binding._getCall().getResponseMessage().getSOAPBody();

        QName responseQName = new QName("urn:echo2.wsdl.test", "echoMyBase64BeanResponse");
        QName returnQName = new QName("", "return");
        QName xsdByteQName = new QName("", "varXsdByte");
        QName soapByteQName = new QName("", "varSoapByte");
        QName xsdBase64BinQName = new QName("", "varXsdBase64Binary");
        QName soapBase64BinQName = new QName("", "varSoapBase64Binary");
        QName xsdHexBinQName = new QName("", "varXsdHexBinary");
        QName soapBase64QName = new QName("", "varSoapBase64");

        MessageElement response = body.getChildElement(responseQName);
        assertNotNull("no <ns1:echoMyBase64BeanResponse> found", response);
        MessageElement return1 = response.getChildElement(returnQName);
        assertNotNull("no <return> found", return1);
        MessageElement realRet = return1.getRealElement();

        Iterator it = realRet.getChildElements(xsdByteQName);
        // only 1 element
        MessageElement one = null;
        while (it.hasNext()) {
            assertNull("only 1 element named " + xsdByteQName, one);
            one = (MessageElement) it.next();
        }
        assertNull(xsdByteQName + " is nil and shouldn't have any children", one.getChildren());

        it = realRet.getChildElements(soapByteQName);
        // only 1 element
        one = null;
        while (it.hasNext()) {
            assertNull("only 1 element named " + soapByteQName, one);
            one = (MessageElement) it.next();
        }
        assertNull(soapByteQName + " is nil and shouldn't have any children", one.getChildren());

        it = realRet.getChildElements(xsdBase64BinQName);
        // only 1 element
        one = null;
        while (it.hasNext()) {
            assertNull("only 1 element named " + xsdBase64BinQName, one);
            one = (MessageElement) it.next();
        }
        assertEquals(xsdBase64BinQName + " have wrong value", "fwCB", one.getFirstChild().getNodeValue());

        it = realRet.getChildElements(soapBase64BinQName);
        // only 1 element
        one = null;
        while (it.hasNext()) {
            assertNull("only 1 element named " + soapBase64BinQName, one);
            one = (MessageElement) it.next();
        }
        assertEquals(soapBase64BinQName + " have wrong value", "fwCB", one.getFirstChild().getNodeValue());

        it = realRet.getChildElements(xsdHexBinQName);
        // only 1 element
        one = null;
        while (it.hasNext()) {
            assertNull("only 1 element named " + xsdHexBinQName, one);
            one = (MessageElement) it.next();
        }
        assertEquals(xsdHexBinQName + " have wrong value", "7f0081", one.getFirstChild().getNodeValue());

        it = realRet.getChildElements(soapBase64QName);
        // only 1 element
        one = null;
        while (it.hasNext()) {
            assertNull("only 1 element named " + soapBase64QName, one);
            one = (MessageElement) it.next();
        }
        assertEquals(soapBase64QName + " have wrong value", "fwCB", one.getFirstChild().getNodeValue());

    }

    public void test1Echo2EchoArrayOfMyBase64Bean() throws Exception {
        test.wsdl.echo2.Echo2SoapBindingStub binding;
        binding = (test.wsdl.echo2.Echo2SoapBindingStub) new test.wsdl.echo2.Echo2ServiceLocator()
                .getEcho2();
        assertNotNull("binding is null", binding);
        // Time out after a minute
        binding.setTimeout(60000);
        // Test operation
        MyBase64Bean[] value = null;
        MyBase64Bean[] array = new MyBase64Bean[2];
        array[0] = new MyBase64Bean();
        fillMyBase64Bean(array[0]);
        array[1] = new MyBase64Bean();
        fillMyBase64Bean(array[1]);
        value = binding.echoArrayOfMyBase64Bean(array);
        // TBD - validate results
    }

    /**
     * @param bean
     */
    private void fillMyBase64Bean(MyBase64Bean bean) {
        bean.setVarSoapBase64(new byte[] {127, 0, -127});
        bean.setVarSoapBase64Binary(new byte[] {127, 0, -127});
        bean.setVarXsdBase64Binary(new byte[] {127, 0, -127});
        bean.setVarXsdHexBinary(new byte[] {127, 0, -127});

    }

    public void test2Echo2EchoArrayOfString_MaxOccursUnbounded()
            throws Exception {
        test.wsdl.echo2.Echo2SoapBindingStub binding;
        Echo2ServiceLocator loc = new test.wsdl.echo2.Echo2ServiceLocator();
        try {
            binding = (test.wsdl.echo2.Echo2SoapBindingStub)loc.getEcho2();
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
        String [] args = new String[] {"one", "two", "", null};
		java.lang.String[] value = null;
        value = binding
                .echoArrayOfString_MaxOccursUnbounded(args);

        // Validate results - NOTE: This checks the XML directly, so if
        // any changes are made to the WSDL/code for this test, equivalent
        // changes must be made in this code.

        SOAPBody body = (SOAPBody)binding._getCall().getResponseMessage().getSOAPBody();
        MessageElement element;
        QName responseQName = new QName("urn:echo2.wsdl.test", "echoArrayOfString_MaxOccursUnboundedResponse");
        QName returnQName = new QName("", "return");
        QName itemQName = new QName("", "varStringArray");
        element = body.getChildElement(responseQName);
        assertNotNull("Couldn't find response element", element);
        element = element.getChildElement(returnQName);
        assertNotNull("Couldn't find return element", element);
        Iterator elements = element.getChildElements(itemQName);
        assertNotNull("Couldn't find items", elements);
        int count = 0;
        while (elements.hasNext()) {
            element = (MessageElement) elements.next();
            count++;
        }
        assertEquals("Wrong # of items", 4, count);

        // OK, now that we know the XML looked right, just for yuks check the values
        for (int i = 0; i < value.length; i++) {
            assertEquals("Item " + i + " didn't match!", args[i], value[i]);
        }
    }

    public void test3Echo2EchoArrayOfString_SoapEncArray() throws Exception {
        test.wsdl.echo2.Echo2SoapBindingStub binding;
        try {
            binding = (test.wsdl.echo2.Echo2SoapBindingStub) new test.wsdl.echo2.Echo2ServiceLocator()
                    .getEcho2();
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
        java.lang.String[] value = null;
        value = binding.echoArrayOfString_SoapEncArray(new java.lang.String[] {
                "one", "two", "three", "", null });
        // TBD - validate results
    }
	public static void main(String[] argv) throws Exception {
		Echo2ServiceTestCase tc = new Echo2ServiceTestCase("somehging;");
		tc.test1Echo2EchoMyBase64Bean();
	}
}
