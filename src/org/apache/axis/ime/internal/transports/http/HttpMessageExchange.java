package org.apache.axis.ime.internal.transports.http;

import org.apache.axis.ime.internal.util.handler.HandlerMessageExchange;
import org.apache.axis.transport.http.HTTPSender;

/**
 * Wraps the existing synchronous HttpSender handler with an
 * asynchronous MessageExchangeProvider.
 * 
 * @author James M Snell (jasnell@us.ibm.com)
 */
public class HttpMessageExchange
        extends HandlerMessageExchange {

  public HttpMessageExchange() {
    super(new HTTPSender());
  }

}
