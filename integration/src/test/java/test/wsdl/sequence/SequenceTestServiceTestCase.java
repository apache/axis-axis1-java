/**
 * SequenceTestServiceTestCase.java
 *
 * @author Rich Scheuerle (scheu@us.ibm.com)
 * @author Glen Daniels (gdaniels@apache.org)
 */

package test.wsdl.sequence;

import test.HttpTestUtil;

public class SequenceTestServiceTestCase extends junit.framework.TestCase {
    public SequenceTestServiceTestCase(String name) {
        super(name);
    }

    public void testSequenceTest() throws Exception {
        test.wsdl.sequence.SequenceTestPortType binding;
        SequenceTestLocator loc = new SequenceTestLocator();
        binding = new SequenceTestLocator().getSequenceTest(HttpTestUtil.getTestEndpoint(loc.getSequenceTestAddress()));
        assertTrue("binding is null", binding != null);
        assertTrue("Test failed!",
                   binding.testSequence(new test.wsdl.sequence.SequenceInfo()));
    }
}

