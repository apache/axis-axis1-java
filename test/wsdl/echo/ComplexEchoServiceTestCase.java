/**
 * ComplexEchoServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.echo;

public class ComplexEchoServiceTestCase extends junit.framework.TestCase {
    public ComplexEchoServiceTestCase(String name) {
        super(name);
    }

    /* FIXME: RUNTIME WSDL broken.
    public void testComplexEchoServiceWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new test.wsdl.echo.ComplexEchoServiceLocator().getComplexEchoServiceAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new test.wsdl.echo.ComplexEchoServiceLocator().getServiceName());
        assertTrue(service != null);
    }
    */

    public void test1ComplexEchoServiceEcho() {
        test.wsdl.echo.Echo binding;
        try {
            binding = new test.wsdl.echo.ComplexEchoServiceLocator().getComplexEchoService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            test.wsdl.echo.MyComplexType complexType = new test.wsdl.echo.MyComplexType();
            test.wsdl.echo.holders.MyComplexTypeHolder complexTypeHolder = 
                    new test.wsdl.echo.holders.MyComplexTypeHolder(complexType);  
            binding.echo(complexTypeHolder);
            assertTrue(complexTypeHolder.value.getSimpleItem().equals("MY_SIMPLE_ITEM"));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

}
