package samples.transport ;

import org.apache.axis.AxisEngine;
import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.client.Transport;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.suppliers.TransportSupplier;
import org.apache.axis.utils.Options;

/** Tests the simple File transport.  To run:
 *      java org.apache.axis.utils.Admin client client_deploy.xml
 *      java org.apache.axis.utils.Admin server deploy.xml
 *      java samples.transport.FileTest IBM
 *      java samples.transport.FileTest XXX
 */

public class FileTest {
    public static void main(String args[]) throws Exception {
        FileReader  reader = new FileReader();
        reader.setDaemon(true);
        reader.start();

        Options opts = new Options( args );

        args = opts.getRemainingArgs();

        if ( args == null ) {
            System.err.println( "Usage: GetQuote <symbol>" );
            System.exit(1);
        }

        String   symbol = args[0] ;
        Service  service = new Service();
        Call     call    = (Call) service.createCall();

        AxisEngine engine = call.getEngine();

        // Manually deploy file sender and file transport for this example
        SimpleTargetedChain c = new SimpleTargetedChain();
        c.setPivotHandler(new FileSender());
        engine.deployTransport("FileTransport", c);

        call.setOperationName( "getQuote" );
        call.addParameter( "symbol", XMLType.XSD_STRING, Call.PARAM_MODE_IN );
        call.setProperty( Call.NAMESPACE, "urn:xmltoday-delayed-quotes" );
        call.setReturnType( XMLType.XSD_FLOAT );
        call.setTransport( new FileTransport() );
        call.setProperty(Transport.USER, opts.getUser() );
        call.setProperty(Transport.PASSWORD, opts.getPassword() );
        call.setProperty(Call.TIMEOUT, "10000");

        Float res = new Float(0.0F);
        res = (Float) call.invoke( new Object[] {symbol} );

        System.out.println( symbol + ": " + res );

        reader.halt();
    }
}
