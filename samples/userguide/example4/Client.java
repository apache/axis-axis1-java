package samples.userguide.example4;

import org.apache.axis.client.ServiceClient;
import org.apache.axis.utils.Debug;
import org.apache.axis.utils.Options;

public class Client
{
    public static void main(String [] args)
    {
        try {
            Options options = new Options(args);
            
            Debug.setDebugLevel(options.isFlagSet('d'));
            
            String endpointURL = options.getURL();
            
            ServiceClient client = new ServiceClient(endpointURL);
            
            client.invoke("LogTestService", "testMethod", new Object [] {});
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
}
