package org.apache.axis.ime.internal;

import org.apache.axis.ime.MessageExchangeContextListener;
import org.apache.axis.ime.MessageExchange;

/**
 * Serves as a base class for MessageExchangeProviders that
 * need to thread pooling on send AND receive message 
 * flows (as opposed to MessageExchangeProvider2 which only
 * does thread pooling on send flows).
 * 
 * @author James M Snell (jasnell@us.ibm.com)
 */
public abstract class MessageExchangeProvider1
  extends MessageExchangeProvider {

  protected abstract MessageExchangeContextListener createSendMessageContextListener();
  
  protected abstract MessageExchangeContextListener createReceiveMessageContextListener();

  public void init(long THREAD_COUNT) {
    if (initialized)
      throw new IllegalStateException();
    for (int n = 0; n < THREAD_COUNT; n++) {
      WORKERS.addWorker(SEND, createSendMessageContextListener());
      WORKERS.addWorker(RECEIVE, createReceiveMessageContextListener());
    }
    initialized = true;
  }
  
}
