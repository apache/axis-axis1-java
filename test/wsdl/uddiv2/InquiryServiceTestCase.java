/**
 * InquiryServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2dev Nov 14, 2003 (04:44:28 EST) WSDL2Java emitter.
 */

package test.wsdl.uddiv2;

public class InquiryServiceTestCase extends junit.framework.TestCase {
    public InquiryServiceTestCase(java.lang.String name) {
        super(name);
    }

    public void test2InquiryService1Find_business() throws Exception {
        test.wsdl.uddiv2.inquiry_v2.InquireSoapStub binding;
        try {
            binding = (test.wsdl.uddiv2.inquiry_v2.InquireSoapStub)
                          new test.wsdl.uddiv2.InquiryServiceLocator().getInquiryService1();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        test.wsdl.uddiv2.api_v2.Find_business find = new test.wsdl.uddiv2.api_v2.Find_business();
        find.setGeneric("2.0");
        find.setMaxRows(100);
        test.wsdl.uddiv2.api_v2.Name[] names = new test.wsdl.uddiv2.api_v2.Name[1];
        names[0] = new test.wsdl.uddiv2.api_v2.Name();
        names[0].setValue("IBM");
        find.setName(names);
        
        // Test operation
        try {
            test.wsdl.uddiv2.api_v2.BusinessList list = null;
            list = binding.find_business(find);
            test.wsdl.uddiv2.api_v2.BusinessInfos infos = list.getBusinessInfos();
            test.wsdl.uddiv2.api_v2.BusinessInfo[] infos2 = infos.getBusinessInfo();
            for(int i=0;i<infos2.length;i++){
                System.out.println(infos2[i].getBusinessKey());
            }
        }
        catch (test.wsdl.uddiv2.api_v2.DispositionReport e1) {
            throw new junit.framework.AssertionFailedError("error Exception caught: " + e1);
        }
    }
}
