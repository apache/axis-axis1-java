package samples.jaxrpc.address;

public class AddressBean implements java.io.Serializable {
    private java.lang.String street;
    private int postcode;

    public AddressBean() {
    }

    public java.lang.String getStreet() {
        return street;
    }

    public void setStreet(java.lang.String street) {
        this.street = street;
    }

    public int getPostcode() {
        return postcode;
    }

    public void setPostcode(int postcode) {
        this.postcode = postcode;
    }
}