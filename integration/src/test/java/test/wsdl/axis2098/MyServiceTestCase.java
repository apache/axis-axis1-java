/**
 * MyServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Jul 05, 2005 (04:50:41 KST) WSDL2Java emitter.
 */

package test.wsdl.axis2098;

public class MyServiceTestCase extends junit.framework.TestCase {
    public MyServiceTestCase(java.lang.String name) {
        super(name);
    }

    public void testHelloWorldWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new test.wsdl.axis2098.MyServiceLocator().getHelloWorldAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new test.wsdl.axis2098.MyServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1HelloWorldHelloWorld() throws Exception {
        test.wsdl.axis2098.MySOAPBindingStub binding;
        try {
            binding = (test.wsdl.axis2098.MySOAPBindingStub)
                          new test.wsdl.axis2098.MyServiceLocator().getHelloWorld();

        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

		// Thread.sleep(10*1000);

		MyRequestType request = new MyRequestType();

        // Test operation
        MyResponseType value = null;

		XsiTestType test = new XsiTestType();

        test.setString("Hello World --------------------------!");

        //  Will give a validation error: cvc-elt.4.3: Type 'xsd:boolean' is not validly derived from the type definition, 'LogicType', of element 'Logic'.
        // It will add a xsi:type boolean but should not: <Logic xsi:type="xsd:boolean">true</Logic>
        test.setLogic(true);

        RestrictionType r = new RestrictionType();
        // r.setFirstName("Hello"); //we do not set this but  <firstName xsi:nil="true"/> is still sent over the wire
        r.setLastName("World");
        test.setRestriction(r);
        
        test.setStringElem("String Ref");

        request.setHelloworld(test);

        // Invoke webservice
        MyResponseType response = binding.helloWorld(request);

        System.out.println("Response from the webservice:");
        System.out.println("\t"+response.getHelloworld());
        // TBD - validate results
    }

}
