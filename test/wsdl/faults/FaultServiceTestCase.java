/**
 * FaultServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 */

package test.wsdl.faults;




public class FaultServiceTestCase extends junit.framework.TestCase {
    public FaultServiceTestCase(String name) {
        super(name);
    }

    /* FIXME: RUNTIME WSDL broken.
    public void testFaultServiceWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new test.wsdl.faults.FaultServiceLocator().getFaultServiceAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new test.wsdl.faults.FaultServiceLocator().getServiceName());
        assertTrue(service != null);
    }
    */

    public void testFaultServiceGetQuote() {
        test.wsdl.faults.FaultServicePortType binding;
        try {
            binding = new FaultServiceLocator().getFaultService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.
                    AssertionFailedError("JAX-RPC ServiceException caught: " + jre);            
        }
        assertTrue("binding is null", binding != null);
        String symbol = new String("MACR");
        try {
            float value = 0;
            value = binding.getQuote(symbol);
            fail("Should raise an InvalidTickerFault"); 
        } 
        catch (InvalidTickerFaultMessage tickerFault) {
            assertEquals("Ticker Symbol in Fault doesn't match original argument", 
                    symbol, tickerFault.getTickerSymbol());
        }
        catch (org.apache.axis.AxisFault e) {
            throw new junit.framework.
                    AssertionFailedError("AxisFault caught: " + e);            
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.
                    AssertionFailedError("Remote Exception caught: " + re );
        }
    }

    public void testFaultServiceThrowFault() throws Exception {
        test.wsdl.faults.FaultServicePortType binding;
        try {
            binding = new FaultServiceLocator().getFaultService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.
                    AssertionFailedError("JAX-RPC ServiceException caught: " + jre);            
        }
        assertTrue("binding is null", binding != null);
        int a = 7;
        String b = "test";
        float c = 3.14F;
            
        try {
            float value = 0;
            value = binding.throwFault(a,b,c);
            fail("Should raise a DerivedFault"); 
        } 
        // We are expecting DerivedFault2 (the operation indicates
        // that it throws a DerivedFault, but we know the impl actually
        // throws DerivedFault2 which extends DerivedFault)
        catch (DerivedFault2 e) {
            assertEquals("Param A in DerivedFault2 doesn't match original",
                    a, e.getA());
            assertEquals("Param B in DerivedFault2 doesn't match original",
                    b, e.getB());
            assertEquals("Param C in DerivedFault2 doesn't match original",
                    c, e.getC(), 0.01F);
        }
        catch (DerivedFault e) {
            throw new junit.framework.
                    AssertionFailedError("DerivedFault caught: " + e);            
        }
        catch (BaseFault e) {
            throw new junit.framework.
                    AssertionFailedError("BaseFault caught: " + e);            
        }
    }

    public void testFaultServiceThrowExtensionFault() {
        test.wsdl.faults.FaultServicePortType binding;
        try {
            binding = new FaultServiceLocator().getFaultService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.
                    AssertionFailedError("JAX-RPC ServiceException caught: " + jre);            
        }
        assertTrue("binding is null", binding != null);
        String description = "test";
            
        try {
            int value = 0;
            value = binding.throwExtensionFault(description);
            fail("Should raise an ExtensionFault"); 
        } 
        catch (ExtensionFault e) {
            try {
                assertEquals("ExtensionFault extension element does not match original",
                        description, e.getExtension().get_any()[0].getAsDOM().getTagName());
            } catch (Exception domError) {
                throw new junit.framework.
                    AssertionFailedError("DOM Exception caught: " + domError);
            }
        }
        catch (org.apache.axis.AxisFault e) {
            throw new junit.framework.
                    AssertionFailedError("AxisFault caught: " + e);            
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.
                    AssertionFailedError("Remote Exception caught: " + re );
        }
    }

}

