package org.apache.axis.handlers.providers;

import org.apache.axis.MessageContext;

public class BSFProvider extends BasicProvider { 

    public static final String OPTION_LANGUAGE = "Language";
    public static final String OPTION_SRC = "Src";
    public static final String OPTION_SCRIPT = "Script";
    
    public void invoke(MessageContext msgContext) {
        System.out.println(getOption("Script"));    
    }

    public void undo(MessageContext msgContext) {
    }
}
