package samples.userguide.example5;

/** This is a JavaBean which represents an order for some products.
 * 
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class Order
{
    /** Who's ordering  */
    private String customerName;
    /** Where do they live  */
    private String shippingAddress;
    /** Which items do we want */
    private String itemCodes[];
    /** And how many */
    private int quantities[];
    
    // Bean accessors
    
    public String getCustomerName()
    { return customerName; }
    public void setCustomerName(String name)
    { customerName = name; }
    
    public String getShippingAddress()
    { return shippingAddress; }
    public void setShippingAddress(String address)
    { shippingAddress = address; }
    
    public String [] getItemCodes()
    { return itemCodes; }
    public void setItemCodes(String [] items)
    { itemCodes = items; }
    
    public int [] getQuantities()
    { return quantities; }
    public void setQuantities(int [] quants)
    { quantities = quants; }
}
