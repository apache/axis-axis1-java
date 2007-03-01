package test.functional;

import junit.framework.TestCase;
import org.apache.axis.client.AdminClient;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service ;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.utils.Options;
import javax.xml.rpc.ParameterMode;
import javax.xml.namespace.QName;
import org.apache.axis.transport.http.SimpleAxisServer ;

/**
 * Test WSAddressing
 */
public class TestWSA extends TestCase {
    public Call call = null ;

    public TestWSA(String s) {
        super(s);
    }

    /**
     * Sets up the WSA Async listener
     */
    protected void setUp() throws Exception {
        (new SimpleAxisServer()).startListening(8888);
        AdminClient.main( new String[] { "samples/wsa/deploy.wsdd" } );
    }

    public void testWSA() throws Exception {
      Service  service = new Service();
      Call     call    = (Call) service.createCall();
      Object   rc      = null ;
      String   rcStr   = null ;

      call.setTargetEndpointAddress( "http://localhost:8080/axis/services/wsaService");
      call.addParameter( "text", XMLType.XSD_STRING, ParameterMode.IN );
      call.setReturnType( XMLType.XSD_STRING );
      call.setEncodingStyle("");
      call.setSOAPActionURI("echo");

      System.out.println( "Running non-WSA echo" );
      rc = call.invoke( "echo", new Object[] { "hi1" } );
      System.out.println( "rc: " + rc );
      assertEquals( rc, "hi1" );

      System.out.println( "Turning WSA on, and redoing echo" );
      call.setProperty( "useWSA", "true" );
      rc = call.invoke( "echo", new Object[] { "hi2" } );
      System.out.println( "rc: " + rc );
      assertEquals( rc, "hi2" );
      assertEquals( null, call.getMessageContext().getProperty("ASYNCRESPONSE") );

      System.out.println( "Setting wsa:ReplyTo to async" );
      call.setReplyTo( "http://localhost:8888/axis/services/asyncService" );
      rc = call.invoke( "echo", new Object[] { "hi3" } );
      System.out.println( "rc: " + rc );
      assertEquals( rc, "hi3" );
      assertEquals( "true", call.getMessageContext().getProperty("ASYNCRESPONSE") );

      /*
      System.out.println( "Calling ping (one way)" );
      call.invokeOneWay( "ping", new Object[] { "hi" } );

      Thread.sleep(1000);
      */
    }

}
