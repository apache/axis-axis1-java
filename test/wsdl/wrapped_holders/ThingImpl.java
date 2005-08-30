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
    public void find_aThing(String aThing, 
                            test.wsdl.wrapped_holders.holders.AThingArrayHolder aThingUnbounded, 
                            test.wsdl.wrapped_holders.holders.OtherDataArrayHolder otherDataUnbounded) 
            throws java.rmi.RemoteException {
        // Verify we get a string in aThing input argument
        String input = aThing;
        if (input == null || !input.equals("This is a test")) {
            String error = "Input argument did not match expected string, got: ";
            error += input != null ? "'" + input + "'" : "NULL";
            error += " Expected: 'This is a test'";
            throw new java.rmi.RemoteException(error);
        }
        
        // now send something back
        String[] things = new String[2];
        things[0] = new String("Thing one");
        things[1] = new String("Thing two");
        aThingUnbounded.value = things;
        String[] others = new String[2];
        others[0] = new String("Other 1");
        others[1] = new String("Other 2");
        otherDataUnbounded.value = others;
    }

}
