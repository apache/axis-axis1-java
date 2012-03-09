/**
 * ReportingServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4.1-SNAPSHOT Mar 09, 2012 (11:21:16 GMT) WSDL2Java emitter.
 */

package test.wsdl.wrapped_choice;

import test.HttpTestUtil;

public class ReportingServiceTestCase extends junit.framework.TestCase {
    public ReportingServiceTestCase(java.lang.String name) {
        super(name);
    }

    public void testReportingServiceSoapWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = HttpTestUtil.getTestEndpoint(new test.wsdl.wrapped_choice.ReportingServiceLocator().getReportingServiceSoapAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new test.wsdl.wrapped_choice.ReportingServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1ReportingServiceSoapListSecureMethods() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        java.lang.String[] value = null;
        value = binding.listSecureMethods();
        // TBD - validate results
    }

    public void test2ReportingServiceSoapCreateBatch() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        java.lang.String value = null;
        value = binding.createBatch();
        // TBD - validate results
    }

    public void test3ReportingServiceSoapCancelBatch() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.cancelBatch();
        // TBD - validate results
    }

    public void test4ReportingServiceSoapExecuteBatch() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.executeBatch();
        // TBD - validate results
    }

    public void test5ReportingServiceSoapGetSystemProperties() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        test.wsdl.wrapped_choice.Property[] value = null;
        value = binding.getSystemProperties(new test.wsdl.wrapped_choice.Property[0]);
        // TBD - validate results
    }

    public void test6ReportingServiceSoapSetSystemProperties() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.setSystemProperties(new test.wsdl.wrapped_choice.Property[0]);
        // TBD - validate results
    }

    public void test7ReportingServiceSoapDeleteItem() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.deleteItem(new java.lang.String());
        // TBD - validate results
    }

    public void test8ReportingServiceSoapMoveItem() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.moveItem(new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test9ReportingServiceSoapListChildren() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        test.wsdl.wrapped_choice.CatalogItem[] value = null;
        value = binding.listChildren(new java.lang.String(), true);
        // TBD - validate results
    }

    public void test10ReportingServiceSoapGetProperties() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        test.wsdl.wrapped_choice.Property[] value = null;
        value = binding.getProperties(new java.lang.String(), new test.wsdl.wrapped_choice.Property[0]);
        // TBD - validate results
    }

    public void test11ReportingServiceSoapSetProperties() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.setProperties(new java.lang.String(), new test.wsdl.wrapped_choice.Property[0]);
        // TBD - validate results
    }

    public void test12ReportingServiceSoapGetItemType() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        test.wsdl.wrapped_choice.ItemTypeEnum value = null;
        value = binding.getItemType(new java.lang.String());
        // TBD - validate results
    }

    public void test13ReportingServiceSoapCreateFolder() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.createFolder(new java.lang.String(), new java.lang.String(), new test.wsdl.wrapped_choice.Property[0]);
        // TBD - validate results
    }

    public void test14ReportingServiceSoapCreateReport() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        test.wsdl.wrapped_choice.Warning[] value = null;
        value = binding.createReport(new java.lang.String(), new java.lang.String(), true, new byte[0], new test.wsdl.wrapped_choice.Property[0]);
        // TBD - validate results
    }

    public void test15ReportingServiceSoapGetReportDefinition() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        byte[] value = null;
        value = binding.getReportDefinition(new java.lang.String());
        // TBD - validate results
    }

    public void test16ReportingServiceSoapSetReportDefinition() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        test.wsdl.wrapped_choice.Warning[] value = null;
        value = binding.setReportDefinition(new java.lang.String(), new byte[0]);
        // TBD - validate results
    }

    public void test17ReportingServiceSoapCreateResource() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.createResource(new java.lang.String(), new java.lang.String(), true, new byte[0], new java.lang.String(), new test.wsdl.wrapped_choice.Property[0]);
        // TBD - validate results
    }

    public void test18ReportingServiceSoapSetResourceContents() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.setResourceContents(new java.lang.String(), new byte[0], new java.lang.String());
        // TBD - validate results
    }

    public void test19ReportingServiceSoapGetResourceContents() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.getResourceContents(new java.lang.String(), new javax.xml.rpc.holders.ByteArrayHolder(), new javax.xml.rpc.holders.StringHolder());
        // TBD - validate results
    }

    public void test20ReportingServiceSoapGetReportParameters() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        test.wsdl.wrapped_choice.ReportParameter[] value = null;
        value = binding.getReportParameters(new java.lang.String(), new java.lang.String(), true, new test.wsdl.wrapped_choice.ParameterValue[0], new test.wsdl.wrapped_choice.DataSourceCredentials[0]);
        // TBD - validate results
    }

    public void test21ReportingServiceSoapSetReportParameters() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.setReportParameters(new java.lang.String(), new test.wsdl.wrapped_choice.ReportParameter[0]);
        // TBD - validate results
    }

    public void test22ReportingServiceSoapCreateLinkedReport() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.createLinkedReport(new java.lang.String(), new java.lang.String(), new java.lang.String(), new test.wsdl.wrapped_choice.Property[0]);
        // TBD - validate results
    }

    public void test23ReportingServiceSoapGetReportLink() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        java.lang.String value = null;
        value = binding.getReportLink(new java.lang.String());
        // TBD - validate results
    }

    public void test24ReportingServiceSoapSetReportLink() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.setReportLink(new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test25ReportingServiceSoapListLinkedReports() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        test.wsdl.wrapped_choice.CatalogItem[] value = null;
        value = binding.listLinkedReports(new java.lang.String());
        // TBD - validate results
    }

    public void test26ReportingServiceSoapRender() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.render(new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new test.wsdl.wrapped_choice.ParameterValue[0], new test.wsdl.wrapped_choice.DataSourceCredentials[0], new java.lang.String(), new javax.xml.rpc.holders.ByteArrayHolder(), new javax.xml.rpc.holders.StringHolder(), new javax.xml.rpc.holders.StringHolder(), new test.wsdl.wrapped_choice.holders.ArrayOfParameterValueHolder(), new test.wsdl.wrapped_choice.holders.ArrayOfWarningHolder(), new test.wsdl.wrapped_choice.holders.ArrayOfStringHolder());
        // TBD - validate results
    }

    public void test27ReportingServiceSoapRenderStream() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.renderStream(new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new test.wsdl.wrapped_choice.ParameterValue[0], new javax.xml.rpc.holders.ByteArrayHolder(), new javax.xml.rpc.holders.StringHolder(), new javax.xml.rpc.holders.StringHolder());
        // TBD - validate results
    }

    public void test28ReportingServiceSoapGetRenderResource() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.getRenderResource(new java.lang.String(), new java.lang.String(), new javax.xml.rpc.holders.ByteArrayHolder(), new javax.xml.rpc.holders.StringHolder());
        // TBD - validate results
    }

    public void test29ReportingServiceSoapSetExecutionOptions() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.setExecutionOptions(new java.lang.String(), test.wsdl.wrapped_choice.ExecutionSettingEnum.Live, new test.wsdl.wrapped_choice.NoSchedule(), new test.wsdl.wrapped_choice.ScheduleDefinition(), new test.wsdl.wrapped_choice.ScheduleReference());
        // TBD - validate results
    }

    public void test30ReportingServiceSoapGetExecutionOptions() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.getExecutionOptions(new java.lang.String(), new test.wsdl.wrapped_choice.holders.ExecutionSettingEnumHolder(), new test.wsdl.wrapped_choice.holders.NoScheduleHolder(), new test.wsdl.wrapped_choice.holders.ScheduleDefinitionHolder(), new test.wsdl.wrapped_choice.holders.ScheduleReferenceHolder());
        // TBD - validate results
    }

    public void test31ReportingServiceSoapSetCacheOptions() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.setCacheOptions(new java.lang.String(), true, new test.wsdl.wrapped_choice.ScheduleExpiration(), new test.wsdl.wrapped_choice.TimeExpiration());
        // TBD - validate results
    }

    public void test32ReportingServiceSoapGetCacheOptions() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.getCacheOptions(new java.lang.String(), new javax.xml.rpc.holders.BooleanHolder(), new test.wsdl.wrapped_choice.holders.ScheduleExpirationHolder(), new test.wsdl.wrapped_choice.holders.TimeExpirationHolder());
        // TBD - validate results
    }

    public void test33ReportingServiceSoapUpdateReportExecutionSnapshot() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.updateReportExecutionSnapshot(new java.lang.String());
        // TBD - validate results
    }

    public void test34ReportingServiceSoapFlushCache() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.flushCache(new java.lang.String());
        // TBD - validate results
    }

    public void test35ReportingServiceSoapListJobs() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        test.wsdl.wrapped_choice.Job[] value = null;
        value = binding.listJobs();
        // TBD - validate results
    }

    public void test36ReportingServiceSoapCancelJob() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        boolean value = false;
        value = binding.cancelJob(new java.lang.String());
        // TBD - validate results
    }

    public void test37ReportingServiceSoapCreateDataSource() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.createDataSource(new java.lang.String(), new java.lang.String(), true, new test.wsdl.wrapped_choice.DataSourceDefinition(), new test.wsdl.wrapped_choice.Property[0]);
        // TBD - validate results
    }

    public void test38ReportingServiceSoapGetDataSourceContents() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        test.wsdl.wrapped_choice.DataSourceDefinition value = null;
        value = binding.getDataSourceContents(new java.lang.String());
        // TBD - validate results
    }

    public void test39ReportingServiceSoapSetDataSourceContents() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.setDataSourceContents(new java.lang.String(), new test.wsdl.wrapped_choice.DataSourceDefinition());
        // TBD - validate results
    }

    public void test40ReportingServiceSoapEnableDataSource() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.enableDataSource(new java.lang.String());
        // TBD - validate results
    }

    public void test41ReportingServiceSoapDisableDataSource() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.disableDataSource(new java.lang.String());
        // TBD - validate results
    }

    public void test42ReportingServiceSoapListReportsUsingDataSource() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        test.wsdl.wrapped_choice.CatalogItem[] value = null;
        value = binding.listReportsUsingDataSource(new java.lang.String());
        // TBD - validate results
    }

    public void test43ReportingServiceSoapSetReportDataSources() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.setReportDataSources(new java.lang.String(), new test.wsdl.wrapped_choice.DataSource[0]);
        // TBD - validate results
    }

    public void test44ReportingServiceSoapGetReportDataSources() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        test.wsdl.wrapped_choice.DataSource[] value = null;
        value = binding.getReportDataSources(new java.lang.String());
        // TBD - validate results
    }

    public void test45ReportingServiceSoapGetReportDataSourcePrompts() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        test.wsdl.wrapped_choice.DataSourcePrompt[] value = null;
        value = binding.getReportDataSourcePrompts(new java.lang.String());
        // TBD - validate results
    }

    public void test46ReportingServiceSoapCreateReportHistorySnapshot() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.createReportHistorySnapshot(new java.lang.String(), new javax.xml.rpc.holders.StringHolder(), new test.wsdl.wrapped_choice.holders.ArrayOfWarningHolder());
        // TBD - validate results
    }

    public void test47ReportingServiceSoapSetReportHistoryOptions() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.setReportHistoryOptions(new java.lang.String(), true, true, new test.wsdl.wrapped_choice.NoSchedule(), new test.wsdl.wrapped_choice.ScheduleDefinition(), new test.wsdl.wrapped_choice.ScheduleReference());
        // TBD - validate results
    }

    public void test48ReportingServiceSoapGetReportHistoryOptions() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.getReportHistoryOptions(new java.lang.String(), new javax.xml.rpc.holders.BooleanHolder(), new javax.xml.rpc.holders.BooleanHolder(), new test.wsdl.wrapped_choice.holders.NoScheduleHolder(), new test.wsdl.wrapped_choice.holders.ScheduleDefinitionHolder(), new test.wsdl.wrapped_choice.holders.ScheduleReferenceHolder());
        // TBD - validate results
    }

    public void test49ReportingServiceSoapSetReportHistoryLimit() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.setReportHistoryLimit(new java.lang.String(), true, 0);
        // TBD - validate results
    }

    public void test50ReportingServiceSoapGetReportHistoryLimit() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.getReportHistoryLimit(new java.lang.String(), new javax.xml.rpc.holders.IntHolder(), new javax.xml.rpc.holders.BooleanHolder(), new javax.xml.rpc.holders.IntHolder());
        // TBD - validate results
    }

    public void test51ReportingServiceSoapListReportHistory() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        test.wsdl.wrapped_choice.ReportHistorySnapshot[] value = null;
        value = binding.listReportHistory(new java.lang.String());
        // TBD - validate results
    }

    public void test52ReportingServiceSoapDeleteReportHistorySnapshot() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.deleteReportHistorySnapshot(new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test53ReportingServiceSoapFindItems() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        test.wsdl.wrapped_choice.CatalogItem[] value = null;
        value = binding.findItems(new java.lang.String(), test.wsdl.wrapped_choice.BooleanOperatorEnum.And, new test.wsdl.wrapped_choice.SearchCondition[0]);
        // TBD - validate results
    }

    public void test54ReportingServiceSoapCreateSchedule() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        java.lang.String value = null;
        value = binding.createSchedule(new java.lang.String(), new test.wsdl.wrapped_choice.ScheduleDefinition());
        // TBD - validate results
    }

    public void test55ReportingServiceSoapDeleteSchedule() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.deleteSchedule(new java.lang.String());
        // TBD - validate results
    }

    public void test56ReportingServiceSoapSetScheduleProperties() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.setScheduleProperties(new java.lang.String(), new java.lang.String(), new test.wsdl.wrapped_choice.ScheduleDefinition());
        // TBD - validate results
    }

    public void test57ReportingServiceSoapGetScheduleProperties() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        test.wsdl.wrapped_choice.Schedule value = null;
        value = binding.getScheduleProperties(new java.lang.String());
        // TBD - validate results
    }

    public void test58ReportingServiceSoapListScheduledReports() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        test.wsdl.wrapped_choice.CatalogItem[] value = null;
        value = binding.listScheduledReports(new java.lang.String());
        // TBD - validate results
    }

    public void test59ReportingServiceSoapListSchedules() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        test.wsdl.wrapped_choice.Schedule[] value = null;
        value = binding.listSchedules();
        // TBD - validate results
    }

    public void test60ReportingServiceSoapPauseSchedule() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.pauseSchedule(new java.lang.String());
        // TBD - validate results
    }

    public void test61ReportingServiceSoapResumeSchedule() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.resumeSchedule(new java.lang.String());
        // TBD - validate results
    }

    public void test62ReportingServiceSoapCreateSubscription() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        java.lang.String value = null;
        value = binding.createSubscription(new java.lang.String(), new test.wsdl.wrapped_choice.ExtensionSettings(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new test.wsdl.wrapped_choice.ParameterValue[0]);
        // TBD - validate results
    }

    public void test63ReportingServiceSoapCreateDataDrivenSubscription() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        java.lang.String value = null;
        value = binding.createDataDrivenSubscription(new java.lang.String(), new test.wsdl.wrapped_choice.ExtensionSettings(), new test.wsdl.wrapped_choice.DataRetrievalPlan(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new test.wsdl.wrapped_choice.ParameterValueOrFieldReference[0]);
        // TBD - validate results
    }

    public void test64ReportingServiceSoapSetSubscriptionProperties() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.setSubscriptionProperties(new java.lang.String(), new test.wsdl.wrapped_choice.ExtensionSettings(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new test.wsdl.wrapped_choice.ParameterValue[0]);
        // TBD - validate results
    }

    public void test65ReportingServiceSoapSetDataDrivenSubscriptionProperties() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.setDataDrivenSubscriptionProperties(new java.lang.String(), new test.wsdl.wrapped_choice.ExtensionSettings(), new test.wsdl.wrapped_choice.DataRetrievalPlan(), new java.lang.String(), new java.lang.String(), new java.lang.String(), new test.wsdl.wrapped_choice.ParameterValueOrFieldReference[0]);
        // TBD - validate results
    }

    public void test66ReportingServiceSoapGetSubscriptionProperties() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.getSubscriptionProperties(new java.lang.String(), new javax.xml.rpc.holders.StringHolder(), new test.wsdl.wrapped_choice.holders.ExtensionSettingsHolder(), new javax.xml.rpc.holders.StringHolder(), new test.wsdl.wrapped_choice.holders.ActiveStateHolder(), new javax.xml.rpc.holders.StringHolder(), new javax.xml.rpc.holders.StringHolder(), new javax.xml.rpc.holders.StringHolder(), new test.wsdl.wrapped_choice.holders.ArrayOfParameterValueHolder());
        // TBD - validate results
    }

    public void test67ReportingServiceSoapGetDataDrivenSubscriptionProperties() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.getDataDrivenSubscriptionProperties(new java.lang.String(), new javax.xml.rpc.holders.StringHolder(), new test.wsdl.wrapped_choice.holders.ExtensionSettingsHolder(), new test.wsdl.wrapped_choice.holders.DataRetrievalPlanHolder(), new javax.xml.rpc.holders.StringHolder(), new test.wsdl.wrapped_choice.holders.ActiveStateHolder(), new javax.xml.rpc.holders.StringHolder(), new javax.xml.rpc.holders.StringHolder(), new javax.xml.rpc.holders.StringHolder(), new test.wsdl.wrapped_choice.holders.ArrayOfParameterValueOrFieldReferenceHolder());
        // TBD - validate results
    }

    public void test68ReportingServiceSoapDeleteSubscription() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.deleteSubscription(new java.lang.String());
        // TBD - validate results
    }

    public void test69ReportingServiceSoapPrepareQuery() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.prepareQuery(new test.wsdl.wrapped_choice.DataSource(), new test.wsdl.wrapped_choice.DataSetDefinition(), new test.wsdl.wrapped_choice.holders.DataSetDefinitionHolder(), new javax.xml.rpc.holders.BooleanHolder());
        // TBD - validate results
    }

    public void test70ReportingServiceSoapGetExtensionSettings() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        test.wsdl.wrapped_choice.ExtensionParameter[] value = null;
        value = binding.getExtensionSettings(new java.lang.String());
        // TBD - validate results
    }

    public void test71ReportingServiceSoapValidateExtensionSettings() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        test.wsdl.wrapped_choice.ExtensionParameter[] value = null;
        value = binding.validateExtensionSettings(new java.lang.String(), new test.wsdl.wrapped_choice.ParameterValueOrFieldReference[0]);
        // TBD - validate results
    }

    public void test72ReportingServiceSoapListSubscriptions() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        test.wsdl.wrapped_choice.Subscription[] value = null;
        value = binding.listSubscriptions(new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test73ReportingServiceSoapListSubscriptionsUsingDataSource() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        test.wsdl.wrapped_choice.Subscription[] value = null;
        value = binding.listSubscriptionsUsingDataSource(new java.lang.String());
        // TBD - validate results
    }

    public void test74ReportingServiceSoapListExtensions() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        test.wsdl.wrapped_choice.Extension[] value = null;
        value = binding.listExtensions(test.wsdl.wrapped_choice.ExtensionTypeEnum.Delivery);
        // TBD - validate results
    }

    public void test75ReportingServiceSoapListEvents() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        test.wsdl.wrapped_choice.Event[] value = null;
        value = binding.listEvents();
        // TBD - validate results
    }

    public void test76ReportingServiceSoapFireEvent() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.fireEvent(new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test77ReportingServiceSoapListSystemTasks() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        test.wsdl.wrapped_choice.Task[] value = null;
        value = binding.listSystemTasks();
        // TBD - validate results
    }

    public void test78ReportingServiceSoapListTasks() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        test.wsdl.wrapped_choice.Task[] value = null;
        value = binding.listTasks();
        // TBD - validate results
    }

    public void test79ReportingServiceSoapListSystemRoles() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        test.wsdl.wrapped_choice.Role[] value = null;
        value = binding.listSystemRoles();
        // TBD - validate results
    }

    public void test80ReportingServiceSoapListRoles() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        test.wsdl.wrapped_choice.Role[] value = null;
        value = binding.listRoles();
        // TBD - validate results
    }

    public void test81ReportingServiceSoapCreateRole() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.createRole(new java.lang.String(), new java.lang.String(), new test.wsdl.wrapped_choice.Task[0]);
        // TBD - validate results
    }

    public void test82ReportingServiceSoapDeleteRole() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.deleteRole(new java.lang.String());
        // TBD - validate results
    }

    public void test83ReportingServiceSoapGetRoleProperties() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.getRoleProperties(new java.lang.String(), new test.wsdl.wrapped_choice.holders.ArrayOfTaskHolder(), new javax.xml.rpc.holders.StringHolder());
        // TBD - validate results
    }

    public void test84ReportingServiceSoapSetRoleProperties() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.setRoleProperties(new java.lang.String(), new java.lang.String(), new test.wsdl.wrapped_choice.Task[0]);
        // TBD - validate results
    }

    public void test85ReportingServiceSoapGetSystemPolicies() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        test.wsdl.wrapped_choice.Policy[] value = null;
        value = binding.getSystemPolicies();
        // TBD - validate results
    }

    public void test86ReportingServiceSoapSetSystemPolicies() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.setSystemPolicies(new test.wsdl.wrapped_choice.Policy[0]);
        // TBD - validate results
    }

    public void test87ReportingServiceSoapGetPolicies() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.getPolicies(new java.lang.String(), new test.wsdl.wrapped_choice.holders.ArrayOfPolicyHolder(), new javax.xml.rpc.holders.BooleanHolder());
        // TBD - validate results
    }

    public void test88ReportingServiceSoapSetPolicies() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.setPolicies(new java.lang.String(), new test.wsdl.wrapped_choice.Policy[0]);
        // TBD - validate results
    }

    public void test89ReportingServiceSoapInheritParentSecurity() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.inheritParentSecurity(new java.lang.String());
        // TBD - validate results
    }

    public void test90ReportingServiceSoapGetSystemPermissions() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        java.lang.String[] value = null;
        value = binding.getSystemPermissions();
        // TBD - validate results
    }

    public void test91ReportingServiceSoapGetPermissions() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        java.lang.String[] value = null;
        value = binding.getPermissions(new java.lang.String());
        // TBD - validate results
    }

    public void test92ReportingServiceSoapLogonUser() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.logonUser(new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test93ReportingServiceSoapLogoff() throws Exception {
        test.wsdl.wrapped_choice.ReportingServiceSoapStub binding;
        try {
            ReportingServiceLocator loc = new ReportingServiceLocator();
            binding = (ReportingServiceSoapStub)loc.getReportingServiceSoap(HttpTestUtil.getTestEndpoint(loc.getReportingServiceSoapAddress()));
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
        binding.logoff();
        // TBD - validate results
    }

}
