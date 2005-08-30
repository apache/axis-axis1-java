/**
 * LoggingFacilityServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2alpha Jan 15, 2004 (11:28:11 EST) WSDL2Java emitter.
 */

package org.apache.axis.wsi.scm.logging;

public class LoggingFacilityServiceTestCase extends junit.framework.TestCase {
    public LoggingFacilityServiceTestCase(java.lang.String name) {
        super(name);
    }

    /* FIXME: RUNTIME WSDL broken.
    public void testLoggingFacilityPortWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new org.apache.axis.wsi.scm.logging.LoggingFacilityServiceLocator().getLoggingFacilityPortAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new org.apache.axis.wsi.scm.logging.LoggingFacilityServiceLocator().getServiceName());
        assertTrue(service != null);
    }
    */

    public void test1LoggingFacilityPortLogEvent() throws Exception {
        org.apache.axis.wsi.scm.logging.LoggingFacilitySoapBindingStub binding;
        try {
            binding = (org.apache.axis.wsi.scm.logging.LoggingFacilitySoapBindingStub)
                          new org.apache.axis.wsi.scm.logging.LoggingFacilityServiceLocator().getLoggingFacilityPort();
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
        binding.logEvent(new org.apache.axis.wsi.scm.logging.LogEventRequestType());
        // TBD - validate results
    }

    public void test2LoggingFacilityPortGetEvents() throws Exception {
        org.apache.axis.wsi.scm.logging.LoggingFacilitySoapBindingStub binding;
        try {
            binding = (org.apache.axis.wsi.scm.logging.LoggingFacilitySoapBindingStub)
                          new org.apache.axis.wsi.scm.logging.LoggingFacilityServiceLocator().getLoggingFacilityPort();
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
        try {
            org.apache.axis.wsi.scm.logging.GetEventsResponseType value = null;
            value = binding.getEvents(new org.apache.axis.wsi.scm.logging.GetEventsRequestType());
        }
        catch (org.apache.axis.wsi.scm.logging.GetEventsFaultType e1) {
            throw new junit.framework.AssertionFailedError("RepositoryMissingFault Exception caught: " + e1);
        }
            // TBD - validate results
    }

}
