package samples.message ;

import org.w3c.dom.Element ;

public class MessageService {
    public Element[] echoElements(Element [] elems) {
        Element[]  result = new Element[elems.length];

        for ( int i = 0 ; i < elems.length ; i++ )
            result[i] = (Element) elems[i];
        
        return( result );
    }
}
