package samples.jaxrpc.address;

public class AddressSoapBindingImpl implements samples.jaxrpc.address.AddressService {
    public String updateAddress(AddressBean addressBean, int newPostCode) throws java.rmi.RemoteException {
        addressBean.setPostcode(newPostCode);
        return ("Your street : " + addressBean.getStreet() + "\nYour postCode : " + newPostCode);
    }
}
