package org.apache.axis.resolver.sd.schema;

import org.apache.axis.Handler;
import org.apache.axis.SimpleChain;

import java.util.Iterator;
import java.util.Vector;

/**
 * @author James Snell (jasnell@us.ibm.com)
 */

public class HandlerList extends SDElement {

    private Vector handlers = new Vector();
    
    public void addHandler(Handler handler) {
        handlers.add(handler);
    }
    
    public void addHandlerList(HandlerList handlerList) {
        handlers.add(handlerList);
    }
    
    public Handler newInstance() {   
        SimpleChain chain = new SimpleChain();
        for (Iterator i = handlers.iterator();i.hasNext();) {
            Object o = i.next();
            if (o instanceof Handler) 
                chain.addHandler((Handler)o);
            if (o instanceof HandlerList)
                chain.addHandler(((HandlerList)o).newInstance());
        }
        return chain;
    }
    
}
