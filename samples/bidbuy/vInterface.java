package samples.bidbuy ;

import org.apache.axis.client.HTTPCall ;
import org.apache.axis.message.RPCElement ;
import org.apache.axis.message.RPCParam ;
import org.apache.axis.utils.* ;
import org.apache.axis.encoding.* ;
import org.apache.axis.* ;
import java.util.* ;

public interface vInterface {
  public void register(String registryURL, Service s) ;
  public void unregister(String registryURL, String name);
  public void register(String registryURL, String myServiceURL );
  public Boolean ping(String serverURL);
  public Vector lookupAsString(String registryURL) throws Exception ;
  public double requestForQuote(String serverURL);
  public String simpleBuy(String serverURL, int quantity );
  public String buy(String serverURL, int quantity, int numItems);
}
