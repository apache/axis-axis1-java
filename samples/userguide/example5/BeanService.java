package samples.userguide.example5;

public class BeanService
{
    public String processOrder(Order order)
    {
        String sep = System.getProperty("line.separator");
        
        String response = "Hi, " + order.getCustomerName() + "!" + sep;
        
        response += sep + "You seem to have ordered the following:" + sep;
        
        String [] items = order.getItemCodes();
        int [] quantities = order.getQuantities();
        
        for (int i = 0; i < items.length; i++) {
            response += sep + quantities[i] + " of item : " + items[i];
        }
        
        response += sep + sep +
                    "If this had been a real order processing system, "+
                    "we'd probably have charged you about now.";
        
        return response;
    }
}
