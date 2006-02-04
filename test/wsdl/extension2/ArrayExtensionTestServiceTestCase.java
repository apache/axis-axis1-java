/**
 * ArrayExtensionTestServiceTestCase.java
 *
 * This is the test case for AXIS-2280
 * https://issues.apache.org/jira/browse/AXIS-2280
 */

package test.wsdl.extension2;

public class ArrayExtensionTestServiceTestCase extends junit.framework.TestCase {
    public ArrayExtensionTestServiceTestCase(java.lang.String name) {
        super(name);
    }

    public void testArrayExtensionTestPortWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new test.wsdl.extension2.ArrayExtensionTestServiceLocator().getArrayExtensionTestPortAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new test.wsdl.extension2.ArrayExtensionTestServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1ArrayExtensionTestPortEchoData() throws Exception {
        test.wsdl.extension2.ArrayExtensionTestBindingStub binding;

		java.net.URL url = new java.net.URL("http://localhost:8080/axis/services/ArrayExtensionTestPort");
        try {
            binding = (test.wsdl.extension2.ArrayExtensionTestBindingStub)
                          new test.wsdl.extension2.ArrayExtensionTestServiceLocator().getArrayExtensionTestPort(url);
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
		//
	    ManagedObject mo = new ManagedObject("aHandle");
        mo.setType("DataContainer");

		// test1. echo Data[]
        Data data = new Data();
        data.setName("data");
        Data[] result = binding.echoData(mo, new Data[] { data });
        printResult(result);

		assertTrue("result length != 1", result.length == 1);
		assertTrue("result[0] is not instance of Data type", result[0] instanceof Data);

		// test2. echo MoreData[] as Data[]
        MoreData moreData = new MoreData();
        moreData.setName("moreData");
        moreData.setSize(11);

        result = binding.echoData(mo, new Data[] { moreData });
        printResult(result);
		assertTrue("result length != 1", result.length == 1);
		assertTrue("result[0] is not instance of Data type", result[0] instanceof MoreData);
		assertTrue("result[0].getSize() != 11", ((MoreData)result[0]).getSize() == 11);

		// test2. echo MoreData[] as MoreData[]
        result = binding.echoData(mo, new MoreData[] { moreData });
        printResult(result);
		assertTrue("result length != 1", result.length == 1);
		assertTrue("result[0] is not instance of Data type", result[0] instanceof MoreData);
		assertTrue("result[0].getSize() != 11", ((MoreData)result[0]).getSize() == 11);
    }

    public static void printResult(Data[] data) {
        if (data == null) System.out.println("data was null");
        else {
            System.out.println("data.length: " + data.length);
            for (int i = 0;i < data.length;i++) {

                if (data[i] == null) System.out.println("data[" + i + "]: null");
                else if (data[i] instanceof MoreData) System.out.println("data[" + i + "].name: " + ((MoreData)data[i]).getName() + ", size: " + ((MoreData)data[i]).getSize());
                else System.out.println("data[" + i + "].name: " + data[i].getName());
            }
        }
    }


}
