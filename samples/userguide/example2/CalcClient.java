import org.apache.axis.client.ServiceClient;

public class CalcClient
{
   public static void main(String [] args) throws Exception {
       String endpoint = "http://localhost:8080/axis/Calculator.jws";
       
       if (args.length != 3) {
           System.err.println("Usage: CalcClient <add|subtract> arg1 arg2");
           return;
       }
       
       String method = args[0];
       if (!(method.equals("add") || method.equals("subtract"))) {
           System.err.println("Usage: CalcClient <add|subtract> arg1 arg2");
           return;
       }
       
       Integer i1 = new Integer(args[1]);
       Integer i2 = new Integer(args[2]);

       ServiceClient client = new ServiceClient(endpoint);
       Integer ret = (Integer)client.invoke("",
                                          method,
                                          new Object [] { i1, i2 });
       
       System.out.println("Got result : " + ret);
   }
}
