package test.wsdl.arrays3;

public class AddrBookServiceImpl implements AddrBookService {
    AddressBookImpl addressBook;

    public AddrBookServiceImpl() {
        addressBook = new AddressBookImpl();
    }

    public void addEntry(String name, Address address) {
        addressBook.addEntry(name, address);
    }

    public Address[] getAddressFromNames(String[] name) {
        return addressBook.getAddressFromNames(name);
    }

    public Address getAddressFromName(String name) {
        return addressBook.getAddressFromName(name);
    }

    public Address[] echoAddresses(Address[] addrs) {
        return addrs;
    }
}
