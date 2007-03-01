package samples.wsa ;

import org.apache.axis.client.Call ;
import org.apache.axis.client.Service ;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.utils.Options;
import javax.xml.rpc.ParameterMode;
import javax.xml.namespace.QName;
import org.apache.axis.transport.http.SimpleAxisServer ;

public class wsaClient {
  static public void main(String[] args) throws Exception {
    try {
      Options opts = new Options( args );

      args = opts.getRemainingArgs();

      (new SimpleAxisServer()).startListening(88);

      Service  service = new Service();
      Call     call    = (Call) service.createCall();

      call.setTargetEndpointAddress( opts.getURL() );
      call.setProperty( "useWSA", "true" );
      call.addParameter( "text", XMLType.XSD_STRING, ParameterMode.IN );
      call.setReturnType( XMLType.XSD_STRING );
      call.setEncodingStyle("");
      call.setSOAPActionURI("ping");

      System.out.println( "Calling ping (one way)" );
      call.invokeOneWay( "ping", new Object[] { "hi" } );

      Thread.sleep(1000);

      call.setSOAPActionURI("echo");
      call.setReplyTo( "http://localhost:81/axis/services/asyncService" );
      System.out.println( call.invoke( "echo", new Object[] { "hi" } ) );

    }
    catch( Exception e ) {
      e.printStackTrace();
    }
  }
}
