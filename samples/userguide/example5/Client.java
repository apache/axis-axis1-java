package samples.userguide.example5;

import org.apache.axis.AxisFault;
import org.apache.axis.client.ServiceClient;
import org.apache.axis.utils.Options;
import org.apache.axis.utils.QName;
                                           
public class Client
{
    public static void main(String [] args) throws Exception
    {
        Options options = new Options(args);
        
        Order order = new Order();
        order.setCustomerName("Glen Daniels");
        order.setShippingAddress("275 Grove Street, Newton, MA");
        
        String [] items = new String[] { "mp3jukebox", "1600mahBattery" };
        int [] quantities = new int [] { 1, 4 };
        
        order.setItemCodes(items);
        order.setQuantities(quantities);
        
        ServiceClient client = new ServiceClient(options.getURL());
        client.addSerializer(Order.class, new QName("urn:BeanService", "Order"),
                             new org.apache.axis.encoding.BeanSerializer(Order.class));
        
        String result;
        try {
            result = (String)client.invoke("OrderProcessor",
                                           "processOrder",
                                           new Object[] { order });
        } catch (AxisFault fault) {
            result = "Error : " + fault.toString();
        }
        
        System.out.println(result);
    }
}
