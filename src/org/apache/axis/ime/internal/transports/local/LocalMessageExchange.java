package org.apache.axis.ime.internal.transports.local;

import org.apache.axis.ime.internal.util.handler.HandlerMessageExchange;
import org.apache.axis.transport.local.LocalSender;

/**
 * Wraps the existing synchronous LocalSender handler with an
 * asynchronous MessageExchangeProvider.
 * 
 * @author James M Snell (jasnell@us.ibm.com)
 */
public class LocalMessageExchange
        extends HandlerMessageExchange {

  public LocalMessageExchange() {
    super(new LocalSender());
  }

}
