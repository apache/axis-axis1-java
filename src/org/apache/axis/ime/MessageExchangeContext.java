package org.apache.axis.ime;

import org.apache.axis.MessageContext;

import java.io.Serializable;

/**
 * Note: the only challenge with making this class serializable
 * is that org.apache.axis.MessageContext is currently NOT
 * serializable.  MessageContext needs to change in order to 
 * take advantage of persistent Channels and CorrelatorServices
 * 
 * For thread safety, instances of this class are immutable
 * 
 * @author James M Snell (jasnell@us.ibm.com)
 */
public final class MessageExchangeContext
        implements Serializable {

    public static MessageExchangeContext newInstance(
            MessageExchangeCorrelator correlator,
            MessageExchangeStatusListener statusListener,
            MessageExchangeReceiveListener receiveListener,
            MessageExchangeFaultListener faultListener,
            MessageContext context) {
        MessageExchangeContext mectx =
                new MessageExchangeContext();
        mectx.correlator = correlator;
        mectx.statusListener = statusListener;
        mectx.receiveListener = receiveListener;
        mectx.faultListener = faultListener;
        mectx.context = context;
        return mectx;
    }

    protected MessageExchangeCorrelator correlator;
    protected MessageExchangeStatusListener statusListener;
    protected MessageExchangeReceiveListener receiveListener;
    protected MessageExchangeFaultListener faultListener;
    protected MessageContext context;

    protected MessageExchangeContext() {
    }

    public MessageExchangeCorrelator getMessageExchangeCorrelator() {
        return this.correlator;
    }

    public MessageExchangeReceiveListener getMessageExchangeReceiveListener() {
        return this.receiveListener;
    }

    public MessageExchangeStatusListener getMessageExchangeStatusListener() {
        return this.statusListener;
    }

    public MessageExchangeFaultListener getMessageExchangeFaultListener() {
        return this.faultListener;
    }

    public MessageContext getMessageContext() {
        return this.context;
    }

}
