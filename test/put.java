package test;

import java.io.*;
import org.apache.axis.*;
import org.apache.axis.client.ServiceClient;
import org.apache.axis.utils.*;

/**
 * A convenient little test program which will send a message as is to
 * the server.  Useful for debugging interoperability problems or 
 * handling of ill-formed messages that are hard to reproduce programmatically.
 *
 * Accepts the standard options, followed by a list of files containing
 * the contents to be sent.
 */
class put {
    static void main(String[] args) throws Exception {
        Options opts = new Options(args);
        Debug.setDebugLevel( opts.isFlagSet( 'd' ) );
        args = opts.getRemainingArgs();

        ServiceClient sc = new ServiceClient(opts.getURL());
  
        for (int i=0; i<args.length; i++) {
            FileInputStream stream = new FileInputStream(new File(args[i]));
            sc.setRequestMessage(new Message(stream));
    
            sc.invoke();
        
            MessageContext mc = sc.getMessageContext();
            System.out.println(mc.getResponseMessage().getAsString());
        }
    }
}
