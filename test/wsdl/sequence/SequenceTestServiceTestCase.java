/**
 * SequenceTestServiceTestCase.java
 *
 * @author Rich Scheuerle (scheu@us.ibm.com)
 * @author Glen Daniels (gdaniels@apache.org)
 */

package test.wsdl.sequence;

public class SequenceTestServiceTestCase extends junit.framework.TestCase {
    public SequenceTestServiceTestCase(String name) {
        super(name);
    }

    public void testSequenceTest() throws Exception {
        test.wsdl.sequence.SequenceTestPortType binding;
        binding = new SequenceTestLocator().getSequenceTest();
        assertTrue("binding is null", binding != null);
        assertTrue("Test failed!",
                   binding.testSequence(new test.wsdl.sequence.SequenceInfo()));
    }
}

