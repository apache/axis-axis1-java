/**
 * Schema2ServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC2 Jan 14, 2005 (05:38:50 EST) WSDL2Java emitter.
 */

package test.wsdl.schema2;

public class Schema2ServiceTestCase extends junit.framework.TestCase {
    public Schema2ServiceTestCase(java.lang.String name) {
        super(name);
    }

    public void testSchema2ServiceWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new test.wsdl.schema2.Schema2ServiceLocator().getSchema2ServiceAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new test.wsdl.schema2.Schema2ServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1Schema2ServiceEchoLanguageTypeTest() throws Exception {
        test.wsdl.schema2.Schema2ServiceSoapBindingStub binding;
        try {
            binding = (test.wsdl.schema2.Schema2ServiceSoapBindingStub)
                          new test.wsdl.schema2.Schema2ServiceLocator().getSchema2Service();
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
        value = binding.echoLanguageTypeTest(new java.lang.String("ABC"));
        // TBD - validate results
    }

    public void test2Schema2ServiceEchoTokenTypeTest() throws Exception {
        test.wsdl.schema2.Schema2ServiceSoapBindingStub binding;
        try {
            binding = (test.wsdl.schema2.Schema2ServiceSoapBindingStub)
                          new test.wsdl.schema2.Schema2ServiceLocator().getSchema2Service();
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
        value = binding.echoTokenTypeTest(new java.lang.String("ABC"));
        // TBD - validate results
    }

    public void test3Schema2ServiceEchoNameTypeTest() throws Exception {
        test.wsdl.schema2.Schema2ServiceSoapBindingStub binding;
        try {
            binding = (test.wsdl.schema2.Schema2ServiceSoapBindingStub)
                          new test.wsdl.schema2.Schema2ServiceLocator().getSchema2Service();
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
        value = binding.echoNameTypeTest(new java.lang.String("ABC"));
        // TBD - validate results
    }

    public void test4Schema2ServiceEchoNCNameTypeTest() throws Exception {
        test.wsdl.schema2.Schema2ServiceSoapBindingStub binding;
        try {
            binding = (test.wsdl.schema2.Schema2ServiceSoapBindingStub)
                          new test.wsdl.schema2.Schema2ServiceLocator().getSchema2Service();
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
        value = binding.echoNCNameTypeTest(new java.lang.String("ABC"));
        // TBD - validate results
    }

    public void test5Schema2ServiceEchoIDTypeTest() throws Exception {
        test.wsdl.schema2.Schema2ServiceSoapBindingStub binding;
        try {
            binding = (test.wsdl.schema2.Schema2ServiceSoapBindingStub)
                          new test.wsdl.schema2.Schema2ServiceLocator().getSchema2Service();
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
        value = binding.echoIDTypeTest(new java.lang.String("ABC"));
        // TBD - validate results
    }

    public void test6Schema2ServiceEchoUnsignedShortTest() throws Exception {
        test.wsdl.schema2.Schema2ServiceSoapBindingStub binding;
        try {
            binding = (test.wsdl.schema2.Schema2ServiceSoapBindingStub)
                          new test.wsdl.schema2.Schema2ServiceLocator().getSchema2Service();
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
        int value = -3;
        value = binding.echoUnsignedShortTest(123);
        // TBD - validate results
    }

    public void test7Schema2ServiceEchoUnsignedIntTest() throws Exception {
        test.wsdl.schema2.Schema2ServiceSoapBindingStub binding;
        try {
            binding = (test.wsdl.schema2.Schema2ServiceSoapBindingStub)
                          new test.wsdl.schema2.Schema2ServiceLocator().getSchema2Service();
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
        long value = -3;
        value = binding.echoUnsignedIntTest(234);
        // TBD - validate results
    }

    public void test8Schema2ServiceEchoUnsignedByteTest() throws Exception {
        test.wsdl.schema2.Schema2ServiceSoapBindingStub binding;
        try {
            binding = (test.wsdl.schema2.Schema2ServiceSoapBindingStub)
                          new test.wsdl.schema2.Schema2ServiceLocator().getSchema2Service();
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
        short value = -3;
        value = binding.echoUnsignedByteTest((short)456);
        // TBD - validate results
    }

    public void test9Schema2ServiceEchoUnsignedLongTest() throws Exception {
        test.wsdl.schema2.Schema2ServiceSoapBindingStub binding;
        try {
            binding = (test.wsdl.schema2.Schema2ServiceSoapBindingStub)
                          new test.wsdl.schema2.Schema2ServiceLocator().getSchema2Service();
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
        java.math.BigInteger value = null;
        value = binding.echoUnsignedLongTest(new java.math.BigInteger("567"));
        // TBD - validate results
    }

    public void test10Schema2ServiceEchoNonPositiveIntegerTest() throws Exception {
        test.wsdl.schema2.Schema2ServiceSoapBindingStub binding;
        try {
            binding = (test.wsdl.schema2.Schema2ServiceSoapBindingStub)
                          new test.wsdl.schema2.Schema2ServiceLocator().getSchema2Service();
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
        java.math.BigInteger value = null;
        value = binding.echoNonPositiveIntegerTest(new java.math.BigInteger("678"));
        // TBD - validate results
    }

    public void test11Schema2ServiceEchoNonNegativeIntegerTest() throws Exception {
        test.wsdl.schema2.Schema2ServiceSoapBindingStub binding;
        try {
            binding = (test.wsdl.schema2.Schema2ServiceSoapBindingStub)
                          new test.wsdl.schema2.Schema2ServiceLocator().getSchema2Service();
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
        java.math.BigInteger value = null;
        value = binding.echoNonNegativeIntegerTest(new java.math.BigInteger("8910"));
        // TBD - validate results
    }

    public void test12Schema2ServiceEchoPositiveIntegerTest() throws Exception {
        test.wsdl.schema2.Schema2ServiceSoapBindingStub binding;
        try {
            binding = (test.wsdl.schema2.Schema2ServiceSoapBindingStub)
                          new test.wsdl.schema2.Schema2ServiceLocator().getSchema2Service();
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
        java.math.BigInteger value = null;
        value = binding.echoPositiveIntegerTest(new java.math.BigInteger("91011"));
        // TBD - validate results
    }

    public void test13Schema2ServiceEchoNegativeIntegerTest() throws Exception {
        test.wsdl.schema2.Schema2ServiceSoapBindingStub binding;
        try {
            binding = (test.wsdl.schema2.Schema2ServiceSoapBindingStub)
                          new test.wsdl.schema2.Schema2ServiceLocator().getSchema2Service();
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
        java.math.BigInteger value = null;
        value = binding.echoNegativeIntegerTest(new java.math.BigInteger("111213"));
        // TBD - validate results
    }

    public void test14Schema2ServiceEchoTimeTest() throws Exception {
        test.wsdl.schema2.Schema2ServiceSoapBindingStub binding;
        try {
            binding = (test.wsdl.schema2.Schema2ServiceSoapBindingStub)
                          new test.wsdl.schema2.Schema2ServiceLocator().getSchema2Service();
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
        java.util.Calendar value = null;
        value = binding.echoTimeTest(java.util.Calendar.getInstance());
        // TBD - validate results
    }

    public void test15Schema2ServiceEchoDateTest() throws Exception {
        test.wsdl.schema2.Schema2ServiceSoapBindingStub binding;
        try {
            binding = (test.wsdl.schema2.Schema2ServiceSoapBindingStub)
                          new test.wsdl.schema2.Schema2ServiceLocator().getSchema2Service();
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
        java.util.Calendar value = null;
        value = binding.echoDateTest(java.util.Calendar.getInstance());
        // TBD - validate results
    }

    public void test16Schema2ServiceEchoDocumentTest() throws Exception {
        test.wsdl.schema2.Schema2ServiceSoapBindingStub binding;
        try {
            binding = (test.wsdl.schema2.Schema2ServiceSoapBindingStub)
                          new test.wsdl.schema2.Schema2ServiceLocator().getSchema2Service();
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
        Document value = new Document();
        value.set_value("XYZ");
        value.setID("ID#1");
        value = binding.echoDocument(value);
        // TBD - validate results
        assertEquals("ID#1",value.getID());
        assertEquals("XYZ",value.get_value());
    }
}
