/**
 * AddressBook.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 */

package samples.addr;

public interface AddressBook extends java.rmi.Remote {
    public void addEntry(String name, Address address) throws java.rmi.RemoteException;
    public Address getAddressFromName(String name) throws java.rmi.RemoteException;
}
