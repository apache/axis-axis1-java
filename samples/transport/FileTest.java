package samples.transport ;

import java.lang.Thread ;

import org.apache.axis.AxisFault ;
import org.apache.axis.client.AxisClient ;
import org.apache.axis.client.ServiceClient ;
import org.apache.axis.client.Transport ;
import org.apache.axis.client.http.HTTPTransport ;
import org.apache.axis.utils.Debug ;
import org.apache.axis.utils.Options ;
import org.apache.axis.encoding.* ;

/* Tests the simple File transport.  To run:
 *      java org.apache.axis.utils.Admin client client_deploy.xml
 *      java org.apache.axis.utils.Admin server deploy.xml
 *      java samples.transport.FileTest IBM
 *      java samples.transport.FileTest XXX
 */

public class FileTest {
  public static void main(String args[]) throws Exception {
    FileReader  reader = new FileReader();
    reader.start();

    try {
      Options opts = new Options( args );
    
      Debug.setDebugLevel( opts.isFlagSet( 'd' ) );
    
      args = opts.getRemainingArgs();
    
      if ( args == null ) {
        System.err.println( "Usage: GetQuote <symbol>" );
        System.exit(1);
      }
    
      String   symbol = args[0] ;
      ServiceClient call = new ServiceClient
            (new HTTPTransport(opts.getURL(), "urn:xmltoday-delayed-quotes"));
      ServiceDescription sd = new ServiceDescription("stockQuotes", true);
      sd.addOutputParam("return", SOAPTypeMappingRegistry.XSD_FLOAT);
      call.setServiceDescription(sd);
    
      if ( opts.isFlagSet('t') > 0 ) call.doLocal = true ;
    
      call.set(Transport.USER, opts.getUser() );
      call.set(Transport.PASSWORD, opts.getPassword() );
      call.setTransportInput( "FileSender" );
    
      Float res = new Float(0.0F);
      res = (Float) call.invoke( "urn:xmltoday-delayed-quotes",
                                 "getQuote",
                                 new Object[] {symbol} );
    
      System.out.println( symbol + ": " + res );

      // Once more just for fun...
      res = (Float) call.invoke( "urn:xmltoday-delayed-quotes",
                                 "getQuote",
                                 new Object[] {symbol} );
    
      System.out.println( symbol + ": " + res );
    }
    catch( Exception e ) {
      if ( e instanceof AxisFault ) ((AxisFault)e).dump();
      e.printStackTrace();
        throw e;
    }

    reader.halt();
  }
}
