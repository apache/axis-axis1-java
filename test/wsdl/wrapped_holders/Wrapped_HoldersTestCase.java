/**
 * Wrapped_HoldersTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package test.wsdl.wrapped_holders;

/**
 * This test verify's that arrays in a wrapped doc/lit service get holders
 * generated for them, and that they work.
 * 
 * @author Tom Jordahl
 */ 
public class Wrapped_HoldersTestCase extends junit.framework.TestCase {
    public Wrapped_HoldersTestCase(java.lang.String name) {
        super(name);
    }
    public void test1ThingFind_AThing() throws Exception {
        test.wsdl.wrapped_holders.Thing binding;
        try {
            binding = new test.wsdl.wrapped_holders.Wrapped_HoldersLocator().getThing();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        // expected results
        test.wsdl.wrapped_holders.AThing[] things = new test.wsdl.wrapped_holders.AThing[2];
        things[0] = new AThing("Thing one");
        things[1] = new AThing("Thing two");
        test.wsdl.wrapped_holders.OtherData[] others = new test.wsdl.wrapped_holders.OtherData[2];
        others[0] = new OtherData("Other 1");
        others[1] = new OtherData("Other 2");
        
        // input arguments
        AThing AthingInput = new AThing("This is a test");
        test.wsdl.wrapped_holders.holders.AThingArrayHolder AThingsOut = new test.wsdl.wrapped_holders.holders.AThingArrayHolder();
        test.wsdl.wrapped_holders.holders.OtherDataArrayHolder OtherDataOut = new test.wsdl.wrapped_holders.holders.OtherDataArrayHolder();

        // call the operation
        binding.find_AThing(AthingInput, AThingsOut, OtherDataOut);
        
        // verify results
        assertEquals("Output argument (Things) does not match expected", things[0], AThingsOut.value[0]);
        assertEquals("Output argument (Things) does not match expected", things[1], AThingsOut.value[1]);
        assertEquals("Output argument (OtherData) does not match expected", others[0], OtherDataOut.value[0]);
        assertEquals("Output argument (OtherData) does not match expected", others[1], OtherDataOut.value[1]);
    }

}
