package org.apache.axis.ime.internal;

import org.apache.axis.ime.MessageContextListener;
import org.apache.axis.ime.MessageExchangeFaultListener;
import org.apache.axis.ime.MessageExchangeStatusListener;
import org.apache.axis.ime.MessageExchangeCorrelator;

/**
 * @author James M Snell (jasnell@us.ibm.com)
 */
public class MessageExchangeReceiveContext {

    public static MessageExchangeReceiveContext newInstance(
            MessageExchangeCorrelator correlator,
            MessageContextListener listener,
            MessageExchangeFaultListener faultListener,
            MessageExchangeStatusListener statusListener) {
        MessageExchangeReceiveContext mectx =
                new MessageExchangeReceiveContext();
        mectx.correlator = correlator;
        mectx.listener = listener;
        mectx.faultListener = faultListener;
        mectx.statusListener = statusListener;
        return mectx;
    }

  protected MessageContextListener listener;
  protected MessageExchangeFaultListener faultListener;
  protected MessageExchangeStatusListener statusListener;
  protected MessageExchangeCorrelator correlator;

  protected MessageExchangeReceiveContext() {}
  
    public MessageExchangeCorrelator getMessageExchangeCorrelator() {
        return this.correlator;
    }

    public MessageContextListener getMessageContextListener() {
        return this.listener;
    }

    public MessageExchangeFaultListener getMessageExchangeFaultListener() {
        return this.faultListener;
    }
    
    public MessageExchangeStatusListener getMessageExchangeStatusListener() {
        return this.statusListener;
    } 
}
