package samples.encoding;

import org.apache.axis.client.* ;
import org.apache.axis.utils.* ;
import java.io.*;
import java.net.*;
import javax.xml.rpc.namespace.*;
import org.w3c.dom.Element ;

public class testElement {
    static String xml = "<x:hello xmlns:x=\"urn:foo\">a string</x:hello>" ;

    public static String doit(String[] args,String xml) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());

        String  sURL = "http://localhost:8080/axis/services/ElementService" ;
        QName   sqn  = new QName(sURL, "ElementServiceService" );
        QName   pqn  = new QName(sURL, "ElementService" );

        //Service service=new Service(new URL("file:ElementService.wsdl"),sqn);
        Service service = new Service(new URL(sURL+"?wsdl"),sqn);
        Call    call    = (Call) service.createCall( pqn, "echoElement" );

        Options opts = new Options(args);
        opts.setDefaultURL( call.getTargetEndpointAddress() );
        call.setTargetEndpointAddress( new URL(opts.getURL()) );

        Element elem = XMLUtils.newDocument(bais).getDocumentElement();

        elem = (Element) call.invoke( new Object[] { "a string", elem } );
        return( XMLUtils.ElementToString( elem ) );
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Sent: " + xml );
        String res = doit(args, xml);
        System.out.println("Returned: " + res );
    }
}
