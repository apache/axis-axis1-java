package org.apache.axis.handlers.providers;

import org.apache.axis.MessageContext;

public class ComProvider extends BasicProvider { 
    
    public static final String OPTION_PROGID = "ProgID";
    public static final String OPTION_CLSID = "CLSID";
    public static final String OPTION_THREADING_MODEL = "threadingModel";
    
    public void invoke(MessageContext msgContext) {
    }

    public void undo(MessageContext msgContext) {
    }
    
}
