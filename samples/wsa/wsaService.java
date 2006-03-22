package samples.wsa;

import org.apache.axis.MessageContext ;

public class wsaService {
  public void ping(String str) {
    System.out.println("Ping: " + str );
    MessageContext.getCurrentContext().setIsOneWay(true);
    MessageContext.getCurrentContext().setEncodingStyle("");
  }

  public String echo(String str) {
    System.out.println("Echo: " + str );
    return str ;
  }
}
