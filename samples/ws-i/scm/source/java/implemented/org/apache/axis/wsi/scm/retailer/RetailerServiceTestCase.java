/**
 * RetailerServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2alpha Jan 15, 2004 (11:28:11 EST) WSDL2Java emitter.
 */

package org.apache.axis.wsi.scm.retailer;

public class RetailerServiceTestCase extends junit.framework.TestCase {
    public RetailerServiceTestCase(java.lang.String name) {
        super(name);
    }

    /* FIXME: RUNTIME WSDL broken.
    public void testRetailerPortWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new org.apache.axis.wsi.scm.retailer.RetailerServiceLocator().getRetailerPortAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new org.apache.axis.wsi.scm.retailer.RetailerServiceLocator().getServiceName());
        assertTrue(service != null);
    }
    */

    public void test1RetailerPortGetCatalog() throws Exception {
        org.apache.axis.wsi.scm.retailer.RetailerSoapBindingStub binding;
        try {
            binding = (org.apache.axis.wsi.scm.retailer.RetailerSoapBindingStub)
                          new org.apache.axis.wsi.scm.retailer.RetailerServiceLocator().getRetailerPort();
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
        org.apache.axis.wsi.scm.retailer.catalog.CatalogType catalog = null;
        catalog = binding.getCatalog();
        // TBD - validate results

        assertNotNull("catalog is null", catalog);

        org.apache.axis.wsi.scm.retailer.catalog.CatalogItem[] items = catalog.getItem();

        assertTrue(items.length > 0);

        for (int i = 0; i < items.length; i++) {
            System.out.println("------------------");
            System.out.println(items[i].getName());
            System.out.println(items[i].getBrand());
        }
    }

    public void test2RetailerPortSubmitOrder() throws Exception {
        org.apache.axis.wsi.scm.retailer.RetailerSoapBindingStub binding;
        try {
            binding = (org.apache.axis.wsi.scm.retailer.RetailerSoapBindingStub)
                          new org.apache.axis.wsi.scm.retailer.RetailerServiceLocator().getRetailerPort();
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
            org.apache.axis.wsi.scm.retailer.order.PartsOrderResponseType value = null;
            value = binding.submitOrder(new org.apache.axis.wsi.scm.retailer.order.PartsOrderType(), new org.apache.axis.wsi.scm.retailer.order.CustomerDetailsType(), new org.apache.axis.wsi.scm.configuration.ConfigurationType());
        }
        catch (org.apache.axis.wsi.scm.retailer.order.InvalidProductCodeType e1) {
            throw new junit.framework.AssertionFailedError("InvalidProductCode Exception caught: " + e1);
        }
        catch (org.apache.axis.wsi.scm.retailer.BadOrderFault e2) {
            throw new junit.framework.AssertionFailedError("BadOrder Exception caught: " + e2);
        }
        catch (org.apache.axis.wsi.scm.configuration.ConfigurationFaultType e3) {
            throw new junit.framework.AssertionFailedError("ConfigurationFault Exception caught: " + e3);
        }
            // TBD - validate results
    }

}
