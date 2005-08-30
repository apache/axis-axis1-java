/**
 * DoExampleTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2beta3 Aug 03, 2004 (01:17:01 CEST) WSDL2Java emitter.
 */

package test.wsdl.wrapperHolder;

import javax.xml.rpc.holders.ByteArrayHolder;
import javax.xml.rpc.holders.LongWrapperHolder;

public class ExampleSoapTestCase extends junit.framework.TestCase {
    public ExampleSoapTestCase(java.lang.String name) {
        super(name);
    }

    public void testWrapperHolderWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new test.wsdl.wrapperHolder.DoExample_ServiceLocator().getWrapperHolderAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new test.wsdl.wrapperHolder.DoExample_ServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1WrapperHolderDoExample() throws Exception {
        test.wsdl.wrapperHolder.ExampleSoapStub binding;
        try {
            binding = (test.wsdl.wrapperHolder.ExampleSoapStub)
                          new test.wsdl.wrapperHolder.DoExample_ServiceLocator().getWrapperHolder();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        byte[][]           in1  = new byte[][]  {
            {   (byte) 0xbe,  (byte) 0xef,  (byte) 0xcc   },
            {   (byte) 0xee,  (byte) 0xff,  (byte) 0xaa   },
        };
        Long               in2  = new Long(3);
        ByteArrayHolder    out1 = new ByteArrayHolder();
        LongWrapperHolder  out2 = new LongWrapperHolder();

        // Test operation
        binding.doExample(in1, in2, out1, out2);

        assertEquals("Unexpected value for ByteArrayHolder",
                     byteArrayAsList(in1[0]), byteArrayAsList(out1.value));
        assertEquals("Unexpected value for LongWrapperHolder ",
                     in2, out2.value);
    }

    private static java.util.List  byteArrayAsList(final byte[] a) {
        return new java.util.AbstractList() {
            public Object get(int i) {
                return new Byte(a[i]);
            }
            public int size() {
                return a.length;
            }
            public Object set(int i, Object o) {
                byte oldVal = a[i];
                a[i] = ((Byte) o).byteValue();
                return new Byte(oldVal);
            }
        };
    }
}
