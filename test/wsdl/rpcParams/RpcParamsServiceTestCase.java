/**
 * RpcParamsServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.rpcParams;

import javax.xml.rpc.ServiceException;

import junit.framework.TestCase;

public class RpcParamsServiceTestCase extends TestCase {
    public RpcParamsServiceTestCase(String name) {
        super(name);
    }

    public void testRpcParamsWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new test.wsdl.rpcParams.RpcParamsServiceLocator().getRpcParamsAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new test.wsdl.rpcParams.RpcParamsServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    /**
     * Send parameters in the order that they are specified in
     * the wsdl. Also omits null parameters.
     */
    public void testEcho() throws Exception {
        RpcParamsBindingStub binding = getBinding();

        EchoStruct result;
        // test sending both
        result = binding.echo("first", "second");
        assertNotNull("returned struct is null", result);
        assertEquals("first parameter marshalled wrong when both sent", "first", result.getFirst());
        assertEquals("second parameter marshalled wrong when both sent", "second", result.getSecond());

        // test ommitting the first, since it's null
        result = binding.echo(null, "second");
        assertNotNull("returned struct is null", result);
        assertNull("first parameter should be null", result.getFirst());
        assertEquals("second parameter marshalled wrong first is null", "second", result.getSecond());

        // test ommitting the second, since it's null
        result = binding.echo("first", null);
        assertNotNull("returned struct is null", result);
        assertEquals("first parameter marshalled wrong when second is null", "first", result.getFirst());
        assertNull("second parameter should be null", result.getSecond());

        // test ommitting both, since they're null
        result = binding.echo(null, null);
        assertNotNull("returned struct is null", result);
        assertNull("first parameter should be null", result.getFirst());
        assertNull("second parameter should be null", result.getSecond());
    }

    /**
     * Send parameters in the reverse order that they are specified in
     * the wsdl. Also omits null parameters.
     */
    public void testEchoReverse() throws Exception {
        RpcParamsBindingStub binding = getBinding();

        EchoStruct result;
        // test sending both
        result = binding.echoReverse("first", "second");
        assertNotNull("returned struct is null", result);
        assertEquals("first parameter marshalled wrong when both sent", "first", result.getFirst());
        assertEquals("second parameter marshalled wrong when both sent", "second", result.getSecond());

        // test ommitting the first, since it's null
        result = binding.echoReverse(null, "second");
        assertNotNull("returned struct is null", result);
        assertNull("first parameter should be null", result.getFirst());
        assertEquals("second parameter marshalled wrong first is null", "second", result.getSecond());

        // test ommitting the second, since it's null
        result = binding.echoReverse("first", null);
        assertNotNull("returned struct is null", result);
        assertEquals("first parameter marshalled wrong when second is null", "first", result.getFirst());
        assertNull("second parameter should be null", result.getSecond());

        // test ommitting both, since they're null
        result = binding.echoReverse(null, null);
        assertNotNull("returned struct is null", result);
        assertNull("first parameter should be null", result.getFirst());
        assertNull("second parameter should be null", result.getSecond());
    }

    private RpcParamsBindingStub getBinding() {
        RpcParamsBindingStub binding = null;
        try {
            binding = (RpcParamsBindingStub) new RpcParamsServiceLocator().getRpcParams();
        }
        catch (ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            fail("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);
        return binding;
    }
}
