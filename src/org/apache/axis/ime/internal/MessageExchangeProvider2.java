package org.apache.axis.ime.internal;

import org.apache.axis.ime.MessageExchangeContextListener;
import org.apache.axis.ime.MessageExchange;

/**
 * Serves as a base class for MessageExchangeProviders that
 * need to thread pooling only on  send message flows (as 
 * opposed to MessageExchangeProvider1 which does thread 
 * pooling on send AND receive flows)
 * 
 * @author James M Snell (jasnell@us.ibm.com)
 */
public abstract class MessageExchangeProvider2
  extends MessageExchangeProvider {

  protected abstract MessageExchangeContextListener createSendMessageContextListener();

  public void init(long THREAD_COUNT) {
    if (initialized)
      throw new IllegalStateException();
    for (int n = 0; n < THREAD_COUNT; n++) {
      WORKERS.addWorker(SEND, createSendMessageContextListener());
    }
    initialized = true;
  }
  
}
