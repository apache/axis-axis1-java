/**
 * Nested2ServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 */

package test.wsdl.nested;

public class Nested2ServiceTestCase extends junit.framework.TestCase {
    public Nested2ServiceTestCase(String name) {
        super(name);
    }
    public void test1NestedNestedSvc2() {
        test.wsdl.nested.Nested2PortType binding;
        try {
            binding = new test.wsdl.nested.Nested2ServiceLocator().getNested();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            test.wsdl.nested.Out value = null;
            value = binding.nestedSvc2(new java.lang.String("0000001000"), new java.lang.String("01"), new java.lang.String("00"), new java.lang.String(""), new java.lang.String("1000"));
            test.wsdl.nested.PEADDRESS address = value.getPEADDRESS();
            test.wsdl.nested.RETURN ret = value.getRETURN();
            System.out.println("NAME:" + address.getNAME());
            assertTrue("NAME is wrong", address.getNAME().equals("Becker Berlin"));
            System.out.println("LOGMSGNO:" + ret.getLOGMSGNO());
            assertTrue("LOGMSGNO is wrong", ret.getLOGMSGNO().equals("123456"));
        }
        catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public static void main(String[] args){
        Nested2ServiceTestCase testcase = new Nested2ServiceTestCase("Nested2ServiceTestCase");
        testcase.test1NestedNestedSvc2();
    }
}

