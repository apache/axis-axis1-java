package test;

import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.transport.http.HTTPTransport;
import org.apache.axis.utils.Options;

import java.io.File;
import java.io.FileInputStream;

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
        String action = opts.isValueSet('a');

        Service  service = new Service();
        Call     call    = (Call) service.createCall();

        call.setTargetEndpointAddress( new java.net.URL(opts.getURL()) );
        if (action != null )
            call.setProperty( HTTPTransport.ACTION, action );
  
        args = opts.getRemainingArgs();
        for (int i=0; i<args.length; i++) {
            FileInputStream stream = new FileInputStream(new File(args[i]));
            call.setRequestMessage(new Message(stream));
    
            call.invoke();
        
            System.out.println(call.getResponseMessage().getAsString());
        }
    }
}
