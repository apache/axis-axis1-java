package test.wsdl.arrays3;

public class Phone  implements java.io.Serializable {
    private int areaCode;
    private java.lang.String exchange;
    private java.lang.String number;

    public Phone() {
    }

    /**
     * Gets the areaCode value for this Phone.
     * 
     * @return areaCode
     */
    public int getAreaCode() {
        return areaCode;
    }


    /**
     * Sets the areaCode value for this Phone.
     * 
     * @param areaCode
     */
    public void setAreaCode(int areaCode) {
        this.areaCode = areaCode;
    }


    /**
     * Gets the exchange value for this Phone.
     * 
     * @return exchange
     */
    public java.lang.String getExchange() {
        return exchange;
    }


    /**
     * Sets the exchange value for this Phone.
     * 
     * @param exchange
     */
    public void setExchange(java.lang.String exchange) {
        this.exchange = exchange;
    }


    /**
     * Gets the number value for this Phone.
     * 
     * @return number
     */
    public java.lang.String getNumber() {
        return number;
    }


    /**
     * Sets the number value for this Phone.
     * 
     * @param number
     */
    public void setNumber(java.lang.String number) {
        this.number = number;
    }

}
