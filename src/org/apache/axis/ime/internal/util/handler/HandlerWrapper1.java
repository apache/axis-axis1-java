package org.apache.axis.ime.internal.util.handler;

import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.ime.MessageExchange;
import org.apache.axis.ime.MessageExchangeContext;
import org.apache.axis.ime.MessageExchangeContextListener;
import org.apache.axis.ime.MessageExchangeFaultListener;
import org.apache.axis.ime.MessageExchangeStatus;
import org.apache.axis.ime.MessageExchangeStatusListener;
import org.apache.axis.ime.MessageExchangeReceiveListener;
import org.apache.axis.ime.MessageExchangeCorrelator;
import org.apache.axis.ime.internal.NonPersistentMessageChannel;
import org.apache.axis.ime.internal.MessageExchangeImpl;
import org.apache.axis.ime.internal.MessageExchangeProvider1;
import org.apache.axis.ime.internal.MessageWorkerGroup;
import org.apache.axis.Handler;

/**
 * Used to wrap synchronous handlers (e.g. Axis 1.0 transports)
 * 
 * @author James M Snell (jasnell@us.ibm.com)
 */
public class HandlerWrapper1 
  extends MessageExchangeProvider1 {

  private Handler handler;
  
  public HandlerWrapper1(Handler handler) {
    this.handler = handler;
  }

  /**
   * @see org.apache.axis.ime.internal.MessageExchangeProvider1#createReceiveMessageContextListener()
   */
  protected MessageExchangeContextListener createReceiveMessageContextListener() {
    return new ReceiveListener();
  }

  /**
   * @see org.apache.axis.ime.internal.MessageExchangeProvider1#createSendMessageContextListener()
   */
  protected MessageExchangeContextListener createSendMessageContextListener() {
    return new SendListener(handler);
  }


  public class SendListener
    implements MessageExchangeContextListener {
      
    private Handler handler;
    
    public SendListener(Handler handler) {
      this.handler = handler;
    }
      
    /**
     * @see org.apache.axis.ime.MessageExchangeContextListener#onMessageExchangeContext(MessageExchangeContext)
     */
    public void onMessageExchangeContext(
      MessageExchangeContext context) {
        try {
          MessageContext msgContext = 
            context.getMessageContext();
          MessageExchangeCorrelator correlator = 
            context.getMessageExchangeCorrelator();
            
          // should I do init's and cleanup's in here?  
          handler.invoke(msgContext);
          
          
          RECEIVE.put(correlator, context);
        } catch (Exception exception) {
          MessageExchangeFaultListener listener = 
            context.getMessageExchangeFaultListener();
          if (listener != null) 
            listener.onFault(
              context.getMessageExchangeCorrelator(),
              exception);
        }
    }
  }
  
  public class ReceiveListener
    implements MessageExchangeContextListener {
      
    /**
     * @see org.apache.axis.ime.MessageExchangeContextListener#onMessageExchangeContext(MessageExchangeContext)
     */
    public void onMessageExchangeContext(
      MessageExchangeContext context) {

        MessageExchangeReceiveListener receiveListener = 
          context.getMessageExchangeReceiveListener();
        MessageExchangeFaultListener faultListener = 
          context.getMessageExchangeFaultListener();
        MessageContext msgContext = 
          context.getMessageContext();
        MessageExchangeCorrelator correlator = 
          context.getMessageExchangeCorrelator();

        try {
          // there should be code here to see if the message
          // contains a fault.  if so, the fault listener should
          // be invoked
          if (msgContext != null &&
              msgContext.getResponseMessage() != null &&
              receiveListener != null) {
            receiveListener.onReceive(correlator, msgContext);
          }        
        } catch (Exception exception) {
          if (faultListener != null) 
            faultListener.onFault(
              correlator, exception);
        }
    }
  }
}
