/**
 * SequenceTestServiceTestCase.java
 *
 * @author: Rich Scheuerle (scheu@us.ibm.com)
 */

package test.sequence;

public class SequenceTestServiceTestCase extends junit.framework.TestCase {
    public SequenceTestServiceTestCase(String name) {
        super(name);
    }

    public void testSequenceTest() {
        test.sequence.SequenceTestPortType binding =
            new SequenceTest().getSequenceTest();
        assertTrue("binding is null", binding != null);
        try {
            int value = -3;
            value = binding.testSequence(new test.sequence.SequenceInfo());
            assertTrue("Test Sequence Failed="+value , (value == 0));
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
    }
}

