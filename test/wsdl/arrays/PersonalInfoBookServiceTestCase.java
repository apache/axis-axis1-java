/**
 * PersonalInfoBookServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 */

package test.wsdl.arrays;

public class PersonalInfoBookServiceTestCase extends junit.framework.TestCase {
    public PersonalInfoBookServiceTestCase(String name) {
        super(name);
    }



    public void testPersonalInfoBook() {

        // Set up some testcase values
        String name = "Joe Geek";
        String[] movies = new String[] { "Star Trek", "A.I." };
        String[] hobbies= new String[] { "programming", "reading about programming" };
        String[] pets   = new String[] { "Byte", "Nibbles" };
        int[]    id     = new int[]    { 0, 0, 7 };
        int id2         = 123;
        String[] foods  = new String[] { "Cheeze Whiz", "Jolt Cola" };
        Byte[]   nickName = new Byte[] { new Byte((byte)'g'), new Byte((byte)'e'),
                                         new Byte((byte)'e'), new Byte((byte)'k') };
        PersonalInfo pi = new PersonalInfo(name,movies,hobbies,pets, id, id2,
                                                  foods,
                                                  nickName);

        // Get the stub and set Session
        test.wsdl.arrays.PersonalInfoBook binding;
        try {
            binding = new PersonalInfoBookService().getPersonalInfoBook();
        }
        catch (javax.xml.rpc.JAXRPCException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC Exception caught: " + jre );
        }
        assertTrue("binding is null", binding != null);
        ((PersonalInfoBookSOAPBindingStub) binding).setMaintainSession (true);

        // Add the name and personal info for Joe Geek
        try {
            binding.addEntry(name, pi);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }

        // Now get the personal info and check validity
        try {
            test.wsdl.arrays.PersonalInfo value = null;
            value = binding.getPersonalInfoFromName(name);
            assertTrue("Name is corrupted " + value, 
                       value.getName().equals(pi.getName()));
            assertTrue("Movies are corrupted " + value, 
                       value.getFavoriteMovies()[1].equals(pi.getFavoriteMovies()[1]));
            assertTrue("Hobbies are corrupted " + value, 
                       value.getHobbies()[1].equals(pi.getHobbies()[1]));
            assertTrue("Pets are corrupted " + value, 
                       value.getPets()[1].equals(pi.getPets()[1]));
            assertTrue("Id is corrupted " + value, 
                       value.getId()[0] == 0 && value.getId()[1] == 0 && value.getId()[2] == 7);
            assertTrue("Id2 is corrupted " + value, 
                       value.getId2() == pi.getId2());
            assertTrue("Food are corrupted " + value, 
                       value.getFoods(1).equals(pi.getFoods(1)));
            assertTrue("Nickname is corrupted " + value, 
                       value.getNickName()[1].equals(pi.getNickName()[1]));
            
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }

        try {
            java.lang.String[] value = null;
            value = binding.getPetsFromName(name);
            assertTrue("PetsFromName is broken " + value, 
                       value[1].equals(pi.getPets()[1]));
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            int[] value = null;
            value = binding.getIDFromName(name);
            assertTrue("getIDFromName is brokent " + value, 
                       value[0] == 0 && value[1] == 0 && value[2] == 7);
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
        try {
            int value = -3;
            value = binding.getID2FromName(name);
            assertTrue("getID2FromName is brokent " + value, 
                       value == pi.getId2());
        } catch (java.rmi.RemoteException re) {
            throw new junit.framework.AssertionFailedError("Remote Exception caught: " + re );
        }
    }
}

