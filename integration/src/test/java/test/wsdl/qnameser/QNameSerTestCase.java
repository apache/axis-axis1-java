package test.wsdl.qnameser;

import javax.xml.namespace.QName;

import test.HttpTestUtil;

public class QNameSerTestCase extends junit.framework.TestCase {

    public QNameSerTestCase(String name) {
        super(name);
    }
    
    public void testQName() throws Exception {
        PlanWSSoap binding;
        try {
            PlanWS2Locator locator = new PlanWS2Locator();
            binding = locator.getPlanWS2Soap(HttpTestUtil.getTestEndpoint(locator.getPlanWS2SoapAddress()));
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        } 
        catch (Exception e) {
            throw new junit.framework.AssertionFailedError("Binding initialization Exception caught: " + e);
        }
        assertTrue("binding is null", binding != null);

        binding.getPlan(PlanService.Q_1);
        binding.getPlan(PlanService.Q_2);
        binding.getPlan(PlanService.Q_3);
    }


    public void testQNameList() throws Exception {
        PlanWSSoap binding;
        try {
            PlanWS2Locator locator = new PlanWS2Locator();
            binding = locator.getPlanWS2Soap(HttpTestUtil.getTestEndpoint(locator.getPlanWS2SoapAddress()));
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        } 
        catch (Exception e) {
            throw new junit.framework.AssertionFailedError("Binding initialization Exception caught: " + e);
        }
        assertTrue("binding is null", binding != null);

        QName [] list =
            new QName[] {PlanService.Q_1, PlanService.Q_2, PlanService.Q_3};
        GetMPlan in = new GetMPlan(list);
        binding.getMPlan(in);
    }
}

