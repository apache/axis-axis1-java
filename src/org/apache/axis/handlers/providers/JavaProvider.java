package org.apache.axis.handlers.providers;

import org.apache.axis.MessageContext;

public class JavaProvider extends BasicProvider { 
    
    public static final String OPTION_CLASSNAME = "className";
    public static final String OPTION_IS_STATIC = "isStatic";
    public static final String OPTION_CLASSPATH = "classPath";
    
    public void invoke(MessageContext msgContext) {
    }

    public void undo(MessageContext msgContext) {
    }
}
