package samples.bidbuy;

import java.util.Date;

public class PurchaseOrder {

    // constructors

    public PurchaseOrder() {};

    public PurchaseOrder(String id, Date createDate, Address shipTo,
                         Address billTo, LineItem[] items)
    {
         this.poID=id;
         this.createDate=createDate;
         this.shipTo=shipTo;
         this.billTo=billTo;
         this.items=items;
    }
    
    // properties

    private String poID;
    public String getPoID() { return poID; }
    public void setPoID(String value) { poID=value; }

    private Date createDate;
    public Date getCreateDate() { return createDate; }
    public void setCreateDate(Date value) { createDate=value; }

    private Address shipTo;
    public Address getShipTo() { return shipTo; }
    public void setShipTo(Address value) { shipTo=value; }

    private Address billTo;
    public Address getBillTo() { return billTo; }
    public void setBillTo(Address value) { billTo=value; }

    private LineItem[] items;
    public LineItem[] getItems() { return items; }
    public void setItems(LineItem[] value) { items=value; }
}
