/**
 * ThingImpl.java
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
public class ThingImpl implements test.wsdl.wrapped_holders.Thing{
    public void find_aThing(test.wsdl.wrapped_holders.AThing aThing, 
                            test.wsdl.wrapped_holders.holders.AThingArrayHolder aThingUnbounded, 
                            test.wsdl.wrapped_holders.holders.OtherDataArrayHolder otherDataUnbounded) 
            throws java.rmi.RemoteException {
        // Verify we get a string in aThing input argument
        String input = aThing.getValue();
        if (input == null || !input.equals("This is a test")) {
            String error = "Input argument did not match expected string, got: ";
            error += input != null ? "'" + input + "'" : "NULL";
            error += " Expected: 'This is a test'";
            throw new java.rmi.RemoteException(error);
        }
        
        // now send something back
        test.wsdl.wrapped_holders.AThing[] things = new test.wsdl.wrapped_holders.AThing[2];
        things[0] = new AThing("Thing one");
        things[1] = new AThing("Thing two");
        aThingUnbounded.value = things;
        test.wsdl.wrapped_holders.OtherData[] others = new test.wsdl.wrapped_holders.OtherData[2];
        others[0] = new OtherData("Other 1");
        others[1] = new OtherData("Other 2");
        otherDataUnbounded.value = others;
    }

}
