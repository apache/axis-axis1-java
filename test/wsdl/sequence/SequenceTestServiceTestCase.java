/**
 * SequenceTestServiceTestCase.java
 *
 * @author: Rich Scheuerle (scheu@us.ibm.com)
 */

package test.wsdl.sequence;

public class SequenceTestServiceTestCase extends junit.framework.TestCase {
    public SequenceTestServiceTestCase(String name) {
        super(name);
    }

    public void testSequenceTest() {
        test.wsdl.sequence.SequenceTestPortType binding;
        try {
            binding = new SequenceTestLocator().getSequenceTest();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre );
        }
        assertTrue("binding is null", binding != null);
        try {
            Integer value = null;
            value = binding.testSequence(new test.wsdl.sequence.SequenceInfo());
            assertTrue("Test Sequence Failed="+value,             (value != null));
            assertTrue("Test Sequence Failed="+value.intValue() , (value.intValue() == 0));
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
    }
}

