package test.wsdl.wrapperHolder2;

import javax.xml.rpc.holders.ByteArrayHolder;
import javax.xml.rpc.holders.LongWrapperHolder;

public class ExampleSoapTestCase extends junit.framework.TestCase {
    public ExampleSoapTestCase(java.lang.String name) {
        super(name);
    }

    public void testWrapperHolder2WSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new test.wsdl.wrapperHolder2.DoExample_ServiceLocator().getWrapperHolder2Address() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new test.wsdl.wrapperHolder2.DoExample_ServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1WrapperHolder2DoExample() throws Exception {
        test.wsdl.wrapperHolder2.ExampleSoapStub binding;
        try {
            binding = (test.wsdl.wrapperHolder2.ExampleSoapStub)
                          new test.wsdl.wrapperHolder2.DoExample_ServiceLocator().getWrapperHolder2();
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
        Long in2  = new Long(3);
        holders.ByteArrayArrayHolder val1 = new holders.ByteArrayArrayHolder();
        test.wsdl.wrapperHolder2.holders.OutArrayHolder val2 = new test.wsdl.wrapperHolder2.holders.OutArrayHolder();
        holders.IntArrayWrapperHolder val3 = new holders.IntArrayWrapperHolder();
        holders.IntArrayHolder val4 = new holders.IntArrayHolder();
        holders.ByteArrayHolder val5 = new holders.ByteArrayHolder();

        // Test operation
        binding.doExample(in1, in2, val1, val2, val3, val4, val5);

        assertEquals("Unexpected value for holders.ByteArrayArrayHolder",
                     byteArrayAsList(in1[0]), byteArrayAsList(val1.value[0]));
        assertEquals("Unexpected value for test.wsdl.WrapperHolder.holders.OutArrayHolder",
                     val2.value[0], 1);
        assertEquals("Unexpected value for holders.IntArrayWrapperHolder",
                     val3.value[0].intValue(), 4);
        assertEquals("Unexpected value for holders.IntArrayHolder",
                     val4.value[0], 6);
        assertEquals("Unexpected value for holders.ByteArrayHolder",
                     val5.value[0], (byte)0x9);
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
