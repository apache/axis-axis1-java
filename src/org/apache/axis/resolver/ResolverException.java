package org.apache.axis.resolver;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author James Snell (jasnell@us.ibm.com)
 */

public class ResolverException extends Exception {

    private String message = null;
    
    public ResolverException() {}
    
    public ResolverException(String message) {
        this.message = message;
    }
    
    public ResolverException(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        this.message = sw.toString();
    }
    
    public String getMessage() {
        return this.message;
    }
   
}
