package org.apache.axis.ime.internal;

import org.apache.axis.ime.MessageExchangeFactory;
import org.apache.axis.ime.MessageExchange;
import org.apache.axis.ime.MessageChannel;

/**
 * Serves as a base class for MessageExchangeProviders that
 * need to thread pooling on send AND receive message 
 * flows (as opposed to MessageExchangeProvider2 which only
 * does thread pooling on send flows).
 * 
 * @author James M Snell (jasnell@us.ibm.com)
 */
public abstract class MessageExchangeProvider
  implements MessageExchangeFactory {

  public static final long DEFAULT_THREAD_COUNT = 5;
 
  protected final MessageWorkerGroup WORKERS = new MessageWorkerGroup();
  protected final MessageChannel SEND        = new NonPersistentMessageChannel(WORKERS);
  protected final MessageChannel RECEIVE     = new NonPersistentMessageChannel(WORKERS);
 
  protected boolean initialized = false;
  
  public MessageExchange createMessageExchange() {
    return new MessageExchangeImpl(this,SEND,RECEIVE);
  }
  
  public void init() {
    init(DEFAULT_THREAD_COUNT);
  }
  
  public abstract void init(long THREAD_COUNT);
  
  public void shutdown() {
    shutdown(false);
  }

  public void shutdown(boolean force) {
    if (!force) {
      WORKERS.safeShutdown();
    } else {
      WORKERS.shutdown();
    }
  }
  
  public void awaitShutdown()
    throws InterruptedException {
      WORKERS.awaitShutdown();
  }
  
  public void awaitShutdown(long shutdown)
    throws InterruptedException {
      WORKERS.awaitShutdown(shutdown);
  }
  
}
