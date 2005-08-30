package test.wsdl.arrays4;

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
        ArrayTest4SOAPBindingStub binding;
        try {
            binding = (ArrayTest4SOAPBindingStub)
                    new ArrayTest4ServiceLocator().getArrayTest4();
        } catch (javax.xml.rpc.ServiceException jre) {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);
        binding._setProperty("sendMultiRefs",Boolean.FALSE);

        // Test operation
        Integer a1[] = new Integer[]{new Integer(1), new Integer(2), new Integer(3)};
        Integer a2[] = new Integer[] {new Integer(9), new Integer(8), new Integer(7)};
        Integer a3[][] = new Integer[][] { 
            {new Integer(1), new Integer(2), new Integer(3)},
            {new Integer(9), new Integer(8), new Integer(7)} }; 
        final StructureType inStruct = new StructureType(a1, a2, a3);
        StructureType value = null;
        value = binding.echoStruct(inStruct);
        Integer r1[] = value.getFld1();
        assertEquals("return struct #1 didn't match", 1, r1[0].intValue() );
        assertEquals("return struct #1 didn't match", 2, r1[1].intValue() );
        assertEquals("return struct #1 didn't match", 3, r1[2].intValue() );

        Integer r2[] = value.getFld2();
        assertEquals("return struct #2 didn't match", 9, r2[0].intValue() );
        assertEquals("return struct #2 didn't match", 8, r2[1].intValue() );
        assertEquals("return struct #2 didn't match", 7, r2[2].intValue() );
 
        Integer r3[][] = value.getFld3();
        assertEquals("return struct #3 didn't match", 1, r3[0][0].intValue() );
        assertEquals("return struct #3 didn't match", 2, r3[0][1].intValue() );
        assertEquals("return struct #3 didn't match", 3, r3[0][2].intValue() );
        assertEquals("return struct #3 didn't match", 9, r3[1][0].intValue() );
        assertEquals("return struct #3 didn't match", 8, r3[1][1].intValue() );
        assertEquals("return struct #3 didn't match", 7, r3[1][2].intValue() );
    }
}

