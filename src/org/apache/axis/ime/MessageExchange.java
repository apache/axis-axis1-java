package org.apache.axis.ime;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;

/**
 * Represents the boundary interface through which messages
 * are exchanged.  This interface supports both push and pull
 * models for receiving inbound messages.
 * 
 * @author James M Snell (jasnell@us.ibm.com)
 */
public interface MessageExchange {

    /**
     * Send an outbound message.  (Impl's of this method
     * need to create a new MessageExchangeCorrelator and 
     * put it into the MessageContext if one does not already
     * exist.)
     */
    public MessageExchangeCorrelator send(
            MessageContext context)
            throws AxisFault;

    /**
     * Will attempt to cancel the outbound MessageExchange 
     * process for a given message context. Returns true if 
     * an only if the MessageContext was canceled.  A false 
     * response indicates that the MessageContext could not 
     * be removed from the outbound channel for whatever 
     * reason.
     */
    public MessageContext cancel(
            MessageExchangeCorrelator correlator)
            throws AxisFault;

    /**
     * Waits indefinitely for a message to be received
     */
    public MessageContext receive()
            throws AxisFault;

    /**
     * Waits the specified amount of time for a message to 
     * be received
     */
    public MessageContext receive(
            long timeout)
            throws AxisFault;

    /**
     * Will instruct the MessageExchange provider to 
     * wait for a message to be received.
     */
    public void startListening();

    /**
     * Will instruct the MessageExchange provider to
     * wait for a specific MessageExchangeCorrelator
     */
    public void startListening(
            MessageExchangeCorrelator correlator);

    /**
     * Will instruct the MessageExchange provider to 
     * stop listening
     */
    public void stopListening();

    /**
     * Synchronized send and receive
     */
    public MessageContext sendAndReceive(
            MessageContext context)
            throws AxisFault;

    /**
     * Synchronized send and receive with timeout
     */
    public MessageContext sendAndReceive(
            MessageContext context,
            long timeout)
            throws AxisFault;

    /**
     * Allows applications to listen for changes to
     * the current disposition of the MessageExchange operation
     * (push model)
     */
    public void setMessageExchangeStatusListener(
            MessageExchangeStatusListener listener)
            throws AxisFault;

    /**
     * Allows applications to listen for inbound messages
     * (push model)
     */
    public void setMessageExchangeReceiveListener(
            MessageExchangeReceiveListener listener)
            throws AxisFault;

    /**
     * Allows applications to listen for faults/exceptions
     * (push model)
     */
    public void setMessageExchangeFaultListener(
            MessageExchangeFaultListener listener)
            throws AxisFault;

    /**
     * Allows MessageExchange consumers low level access
     * to the Send message channel
     */
    public MessageChannel getSendChannel();

    /**
     * Allows MessageExchange consumers low level access
     * to the Receive message channel
     */
    public MessageChannel getReceiveChannel();

}
