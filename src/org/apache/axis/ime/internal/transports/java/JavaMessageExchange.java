package org.apache.axis.ime.internal.transports.java;

import org.apache.axis.ime.internal.util.handler.HandlerMessageExchange;
import org.apache.axis.transport.java.JavaSender;

/**
 * Wraps the existing synchronous JavaSender handler with an
 * asynchronous MessageExchangeProvider.
 * 
 * @author James M Snell (jasnell@us.ibm.com)
 */
public class JavaMessageExchange
        extends HandlerMessageExchange {

  public JavaMessageExchange() {
    super(new JavaSender());
  }

}
