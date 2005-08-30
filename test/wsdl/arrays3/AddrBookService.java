package test.wsdl.arrays3;

public interface AddrBookService extends java.rmi.Remote {
    public void addEntry(String name, Address address) throws java.rmi.RemoteException;
    public Address[] getAddressFromNames(String[] names) throws java.rmi.RemoteException;
    public Address getAddressFromName(String name) throws java.rmi.RemoteException;
    public Address[] echoAddresses(Address[] addrs) throws java.rmi.RemoteException;
}
