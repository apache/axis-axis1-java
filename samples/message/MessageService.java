package samples.message ;

import org.w3c.dom.Element ;
import org.apache.axis.MessageContext ;
import java.util.Vector ;

public class MessageService {
    public Element[] echoElements(Vector elems) {
        Element[]  result = new Element[elems.size()];

        for ( int i = 0 ; i < elems.size() ; i++ )
            result[i] = (Element) elems.get(i);
        
        return( result );
    }
}
