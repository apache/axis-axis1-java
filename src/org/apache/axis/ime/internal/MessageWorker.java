package org.apache.axis.ime.internal;

import org.apache.axis.MessageContext;
import org.apache.axis.ime.MessageChannel;
import org.apache.axis.ime.MessageExchangeContext;
import org.apache.axis.ime.MessageExchangeContextListener;

/**
 * @author James M Snell (jasnell@us.ibm.com)
 */
public class MessageWorker implements Runnable {

  protected static final long SELECT_TIMEOUT = 1000 * 30;
  
  protected MessageWorkerGroup pool;
  protected MessageChannel channel;
  protected MessageExchangeContextListener listener;
  
  private MessageWorker() {}

  public MessageWorker(
    MessageWorkerGroup pool,
    MessageChannel channel,
    MessageExchangeContextListener listener) {
    this.pool = pool;
    this.channel = channel;
    this.listener = listener;
  }

  public MessageExchangeContextListener getMessageExchangeContextListener() {
    return this.listener;
  }
  
  public MessageChannel getMessageChannel() {
    return this.channel;
  }

  /**
   * @see java.lang.Runnable#run()
   */
  public void run() {
    try {
      while (!pool.isShuttingDown()) {
        MessageExchangeContext context = channel.select(SELECT_TIMEOUT);
        if (context != null) 
          listener.onMessageExchangeContext(context);
      }
    } catch (Throwable t) {
      // kill the thread if any type of exception occurs.
      // don't worry, we'll create another one to replace it
      // if we're not currently in the process of shutting down.
      // once I get the logging function plugged in, we'll
      // log whatever errors do occur
    } finally {
      pool.workerDone(this);
    }
  }

}
