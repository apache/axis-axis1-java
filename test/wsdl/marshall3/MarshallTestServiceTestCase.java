/**
 * MarshallTestServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC3 Apr 29, 2005 (10:05:23 EDT) WSDL2Java emitter.
 */

package test.wsdl.marshall3;

import javax.xml.namespace.QName;
import test.wsdl.marshall3.types.QNameArrayTest;
import test.wsdl.marshall3.types.QNameArrayTestResponse;
import test.wsdl.marshall3.types.ShortArrayTest;
import test.wsdl.marshall3.types.ShortArrayTestResponse;
import test.wsdl.marshall3.types.StringArrayTest;
import test.wsdl.marshall3.types.StringArrayTestResponse;

public class MarshallTestServiceTestCase extends junit.framework.TestCase {
    public MarshallTestServiceTestCase(java.lang.String name) {
        super(name);
    }

    public void test1Marshall3TestPort1ShortArrayTest() throws Exception {
        test.wsdl.marshall3.Marshall3TestPort1SoapBindingStub binding;
        try {
            binding = (test.wsdl.marshall3.Marshall3TestPort1SoapBindingStub)
                          new test.wsdl.marshall3.MarshallTestServiceLocator().getMarshall3TestPort1();
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
        ShortArrayTestResponse value = null;
        value = binding.shortArrayTest(new ShortArrayTest(new short[]{1,2,3}));
        assertEquals(3,value.getShortArray().length);
    }

    public void test2Marshall3TestPort1StringArrayTest() throws Exception {
        test.wsdl.marshall3.Marshall3TestPort1SoapBindingStub binding;
        try {
            binding = (test.wsdl.marshall3.Marshall3TestPort1SoapBindingStub)
                          new test.wsdl.marshall3.MarshallTestServiceLocator().getMarshall3TestPort1();
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
        StringArrayTestResponse value = null;
        value = binding.stringArrayTest(new StringArrayTest(new String[]{"1","2","","4",null,"6"}));
        // TBD - validate results
        assertEquals(6,value.getStringArray().length);
    }

    public void test2Marshall3TestPort1QnameArrayTest() throws Exception {
        test.wsdl.marshall3.Marshall3TestPort1SoapBindingStub binding;
        try {
            binding = (test.wsdl.marshall3.Marshall3TestPort1SoapBindingStub)
                          new test.wsdl.marshall3.MarshallTestServiceLocator().getMarshall3TestPort1();
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
        QNameArrayTestResponse value = null;
        QName[] qnames= new QName[3];
        qnames[0] = new QName("urn:someNamespaceURI", "localPart");
        qnames[1] = new QName("localPartWithoutNS");
        qnames[2] = null;
        value = binding.qnameArrayTest(new QNameArrayTest(qnames));
        // TBD - validate results
        assertEquals("wrong array size", 3, value.getQnameArray().length);
        assertEquals("qnames[0] not equals", new QName("urn:someNamespaceURI", "localPart"), value.getQnameArray()[0]);
        assertEquals("qnames[1] not equals", new QName("localPartWithoutNS"), value.getQnameArray()[1]);
        assertEquals("qnames[2] not equals", null, value.getQnameArray()[2]);
    }

    public void test3Marshall3TestPort1EchoShortListTypeTest() throws Exception {
        test.wsdl.marshall3.Marshall3TestPort1SoapBindingStub binding;
        try {
            binding = (test.wsdl.marshall3.Marshall3TestPort1SoapBindingStub)
                          new test.wsdl.marshall3.MarshallTestServiceLocator().getMarshall3TestPort1();
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
        short[] value = null;
        value = binding.echoShortListTypeTest(new short[]{1,2,3});
        // TBD - validate results
        assertEquals(3,value.length);
    }

}
