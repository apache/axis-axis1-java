/**
 * EsrTestServiceTestCase.java
 *
 * Test for bug 12636
 * Uses the Service interface to deal with WSDL instead of stubs.
 */

package test.wsdl.esr;

import org.apache.axis.transport.http.SimpleAxisWorker;
import org.apache.axis.utils.NetworkUtils;

import javax.xml.namespace.QName;

public class EsrTestServiceTestCase extends junit.framework.TestCase {
    public EsrTestServiceTestCase(java.lang.String name) {
        super(name);
    }

    public void testEsrTestWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new test.wsdl.esr.EsrTestServiceLocator().getEsrTestAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new test.wsdl.esr.EsrTestServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1EsrTestEsrInOut() {
        // Using WSDL file to make a SOAP call
        try {
            String thisHost = NetworkUtils.getLocalHostname();
            String thisPort = System.getProperty("test.functional.ServicePort", "8080");

            //load wsdl file
            String wsdlLocation = "http://" + thisHost + ":" + thisPort + "/axis/services/EsrTest?WSDL";
            javax.xml.rpc.Service svc =
                    new org.apache.axis.client.Service(
                            wsdlLocation,
                            new javax.xml.namespace.QName("urn:esr.wsdl.test",
                                    "EsrTestService")
                    );
			
            //setting up the call
            javax.xml.rpc.Call call = svc.createCall(
                    new javax.xml.namespace.QName("urn:esr.wsdl.test",
                            "EsrTest"),
                    new javax.xml.namespace.QName("urn:esr.wsdl.test",
                                    "esrInOut")
            );
			
            //init in params
            Object[] soapInParams = new Object[]{new Short((short) 5)};
			
            //calling soap service
            Object ret = call.invoke(soapInParams);
			
            //printing output params
            java.util.Map outParams = call.getOutputParams();

            // Debug code if you need it
            /*
            java.util.Collection outs = outParams.values();
            java.util.Iterator it = outs.iterator();
            int i = 1;
            while (it.hasNext()) {
                System.out.println(i++ + ". " + it.next().toString());
            }
            */

            // Expecting a short and a double back
            assertEquals("Number of output parameters is wrong", outParams.size(), 2);
            Object s = outParams.get(new QName("echoVal"));
            assertNotNull("echoVal paramter is null", s);
            assertEquals("echoVal parameter is incorrect", (Short)s, new Short((short) 5) );
            Object sq = outParams.get(new QName("sqrtVal"));
            assertNotNull("sqrtVal paramter is null", sq);
            assertEquals("sqrtVal parameter is incorrect", ((Double)sq).doubleValue(), Math.sqrt(5), 0.001D );

        } catch (Exception e) {
            e.printStackTrace(System.out);
            throw new junit.framework.AssertionFailedError("Exception caught: " + e);
        }

    }
    
    public void test1EsrTestEsrInOut2() {
        // Using WSDL file to make a SOAP call
        try {
            String thisHost = NetworkUtils.getLocalHostname();
            String thisPort = System.getProperty("test.functional.ServicePort", "8080");

            //load wsdl file
            String wsdlLocation = "http://" + thisHost + ":" + thisPort + "/axis/services/EsrTest?WSDL";
            javax.xml.rpc.Service svc =
                    new org.apache.axis.client.Service(
                            wsdlLocation,
                            new javax.xml.namespace.QName("urn:esr.wsdl.test",
                                    "EsrTestService")
                    );
			
            //setting up the call
            javax.xml.rpc.Call call = svc.createCall(
                    new javax.xml.namespace.QName("urn:esr.wsdl.test",
                            "EsrTest"),
                    new javax.xml.namespace.QName("urn:esr.wsdl.test",
                                    "esrInOut2")
            );
			
			//init in params
			Object[] soapInParams = new Object[] { 
                                              "token1",
                                              "token2",
                                              new Short((short)5) };
            
            //calling soap service
            Object ret = call.invoke(soapInParams);
			
            //printing output params
            java.util.Map outParams = call.getOutputParams();

            // Debug code if you need it
            /*
            java.util.Collection outs = outParams.values();
            java.util.Iterator it = outs.iterator();
            int i = 1;
            while (it.hasNext()) {
                System.out.println(i++ + ". " + it.next().toString());
            }
            */

            // Expecting a short and a double back
            assertEquals("Number of output parameters is wrong", outParams.size(), 2);
            Object s = outParams.get(new QName("echoVal"));
            assertNotNull("echoVal paramter is null", s);
            assertEquals("echoVal parameter is incorrect", (Short)s, new Short((short) 5) );
            Object sq = outParams.get(new QName("sqrtVal"));
            assertNotNull("sqrtVal paramter is null", sq);
            assertEquals("sqrtVal parameter is incorrect", ((Double)sq).doubleValue(), Math.sqrt(5), 0.001D );

        } catch (Exception e) {
            e.printStackTrace(System.out);
            throw new junit.framework.AssertionFailedError("Exception caught: " + e);
        }

    }
}
