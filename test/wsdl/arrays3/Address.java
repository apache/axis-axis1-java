package test.wsdl.arrays3;

public class Address  implements java.io.Serializable {
    private String city;
    private Phone phoneNumber;
    private StateType state;
    private String streetName;
    private int streetNum;
    private int zip;
    private Phone[] otherPhones;

    public Address() {
    }

    /**
     * Gets the city value for this Address.
     * 
     * @return city
     */
    public java.lang.String getCity() {
        return city;
    }


    /**
     * Sets the city value for this Address.
     * 
     * @param city
     */
    public void setCity(java.lang.String city) {
        this.city = city;
    }


    /**
     * Gets the phoneNumber value for this Address.
     * 
     * @return phoneNumber
     */
    public Phone getPhoneNumber() {
        return phoneNumber;
    }


    /**
     * Sets the phoneNumber value for this Address.
     * 
     * @param phoneNumber
     */
    public void setPhoneNumber(Phone phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    /**
     * Gets the state value for this Address.
     * 
     * @return state
     */
    public StateType getState() {
        return state;
    }


    /**
     * Sets the state value for this Address.
     * 
     * @param state
     */
    public void setState(StateType state) {
        this.state = state;
    }


    /**
     * Gets the streetName value for this Address.
     * 
     * @return streetName
     */
    public java.lang.String getStreetName() {
        return streetName;
    }


    /**
     * Sets the streetName value for this Address.
     * 
     * @param streetName
     */
    public void setStreetName(java.lang.String streetName) {
        this.streetName = streetName;
    }


    /**
     * Gets the streetNum value for this Address.
     * 
     * @return streetNum
     */
    public int getStreetNum() {
        return streetNum;
    }


    /**
     * Sets the streetNum value for this Address.
     * 
     * @param streetNum
     */
    public void setStreetNum(int streetNum) {
        this.streetNum = streetNum;
    }


    /**
     * Gets the zip value for this Address.
     * 
     * @return zip
     */
    public int getZip() {
        return zip;
    }


    /**
     * Sets the zip value for this Address.
     * 
     * @param zip
     */
    public void setZip(int zip) {
        this.zip = zip;
    }

    public void setOtherPhones(Phone[] phones) {
        otherPhones = phones;
    }

    public Phone[] getOtherPhones() {
        return otherPhones;
    }
}
