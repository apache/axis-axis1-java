package org.apache.axis;

import java.util.Vector;

final public class ChainContainer implements Handler {
    final private Vector singleChain;
    
    public ChainContainer() {
        this.singleChain = new Vector();
    }

    public void addHandler(Handler handler) {
        this.singleChain.add(handler);
    }
  
    public void init() {};

    public void cleanup() {};

    public void invoke(MessageContext msgCntxt) {
        int length = this.singleChain.size();
        for( int i=0; i<length; i++) {
            Handler handler = (Handler)this.singleChain.get(i);
            handler.invoke(msgCntxt);
        }        
    };
};
