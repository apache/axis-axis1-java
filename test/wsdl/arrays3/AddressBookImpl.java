package test.wsdl.arrays3;

import java.util.Hashtable;
import java.util.Map;


public class AddressBookImpl 
{
    private static Map addresses = new Hashtable();

    public void addEntry(String name, Address address) 
    {
        this.addresses.put(name, address);
    }
    
    public Address getAddressFromName(String name)
    {
        return (Address) this.addresses.get(name);
    }
    

    public Address[] getAddressFromNames(String[] name) {
        if (name == null) return null;
        Address[] result = new Address[name.length];
        for(int i=0; i< name.length; i++) {
            result[i] = getAddressFromName(name[i]);
        }
        return result;
    } 

}
