/**
 * PlanWSSoapImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.extension;

public class PlanWSSoapImpl implements test.wsdl.extension.PlanWSSoap{
    public test.wsdl.extension.Plan getPlan() throws java.rmi.RemoteException {
        test.wsdl.extension.Disposition disposition = new test.wsdl.extension.Disposition();
        disposition.setCode("CODE #1");
        disposition.setDescription("CODE #1 Description");
        test.wsdl.extension.Plan plan = new test.wsdl.extension.Plan();
        plan.setDisposition(disposition);
        return plan;
    }

}
