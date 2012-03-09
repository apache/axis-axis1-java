/**
 * AnyServiceServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.anytype;

import test.HttpTestUtil;

public class AnyServiceServiceTestCase extends junit.framework.TestCase {
    public AnyServiceServiceTestCase(java.lang.String name) {
        super(name);
    }
    public void test1AnyServiceRun() throws Exception {
        test.wsdl.anytype.AnyService binding;
        try {
            AnyServiceServiceLocator loc = new AnyServiceServiceLocator();
            binding = loc.getAnyService(HttpTestUtil.getTestEndpoint(loc.getAnyServiceAddress()));
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // Test operation
        java.lang.Object value = null;
        value = binding.run();
        System.out.println("Got:" + value);
    }

}
