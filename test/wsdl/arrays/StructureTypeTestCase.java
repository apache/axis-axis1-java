package test.wsdl.arrays;

/**
 * StructureTypeTestCase
 * <p/>
 * This test only needs to compile, as we are testing to make sure that the
 * Schema in the WSDL generates the correctly wrapped Integer arrays.
 */
public class StructureTypeTestCase extends junit.framework.TestCase {

    public StructureTypeTestCase(String name) {
        super(name);
    }

    public void testEchoStruct() throws Exception {
        test.wsdl.arrays.PersonalInfoBookSOAPBindingStub binding;
        try {
            binding = (test.wsdl.arrays.PersonalInfoBookSOAPBindingStub)
                    new test.wsdl.arrays.PersonalInfoBookServiceLocator().getPersonalInfoBook();
        } catch (javax.xml.rpc.ServiceException jre) {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        Integer a1[] = new Integer[]{new Integer(1), new Integer(2), new Integer(3)};
        //Integer a2[] = new Integer[] {new Integer(9), new Integer(8), new Integer(7)};
        int a2[] = new int[]{9, 8, 7};
        final test.wsdl.arrays.StructureType inStruct = new test.wsdl.arrays.StructureType(a1, a2);
        test.wsdl.arrays.StructureType value = null;
        value = binding.echoStruct(inStruct);
        Integer r1[] = value.getFld1();
        assertEquals("return struct #1 didn't match", 1, r1[0].intValue() );
        assertEquals("return struct #1 didn't match", 2, r1[1].intValue() );
        assertEquals("return struct #1 didn't match", 3, r1[2].intValue() );

        //Integer r2[] = value.getFld2();
        int r2[] = value.getFld2();
        assertEquals("return struct #2 didn't match", 9, r2[0] );
        assertEquals("return struct #2 didn't match", 8, r2[1] );
        assertEquals("return struct #2 didn't match", 7, r2[2] );

    }


}

