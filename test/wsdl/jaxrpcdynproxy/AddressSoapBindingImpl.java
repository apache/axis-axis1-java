package test.wsdl.jaxrpcdynproxy;
import test.wsdl.jaxrpcdynproxy.holders.AddressBeanHolder;

public class AddressSoapBindingImpl implements test.wsdl.jaxrpcdynproxy.AddressService {
    
    public String updateAddress(AddressBeanHolder addressBeanHolder, int newPostCode) throws java.rmi.RemoteException {
        addressBeanHolder.value.setPostcode(newPostCode);
        return ("Your street : " + addressBeanHolder.value.getStreet() + "\nYour postCode : " + newPostCode);
    }
}
