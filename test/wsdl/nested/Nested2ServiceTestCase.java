/**
 * Nested2ServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 */

package test.wsdl.nested;
import org.apache.axis.message.MessageElement;
import test.wsdl.nested.holders.PE_ADDRESSHolder;
import test.wsdl.nested.holders.RETURNHolder;

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
            PE_ADDRESSHolder pE_ADDRESS = new PE_ADDRESSHolder();
            RETURNHolder rETURN = new RETURNHolder();
            binding.nestedSvc2(new java.lang.String("0000001000"),
                               new java.lang.String("01"),
                               new java.lang.String("00"),
                               new java.lang.String(""),
                               new java.lang.String("1000"),
                               pE_ADDRESS,
                               rETURN);
            PE_ADDRESS address = pE_ADDRESS.value;
            RETURN ret = rETURN.value;
            assertTrue("NAME is wrong", address.getNAME().equals("Becker Berlin"));
            assertTrue("LOGMSGNO is wrong", ret.getLOG_MSG_NO().equals("123456"));
            MessageElement [] any = address.get_any();
            assertNotNull("No 'any' content", any);
            assertTrue("any is wrong:" + any[0],
                       any[0].getObjectValue().equals("Test Any"));
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

