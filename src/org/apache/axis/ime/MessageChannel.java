package org.apache.axis.ime;

/**
 * A MessageChannel is a low level hybrid FIFO Queue and Keyed map
 * that serves as the storage for inbound or outbound messages.
 * Each MessageExchange implementation will create at least two
 * MessageChannels, one for messages being sent, and another for
 * messages that have been received.
 * 
 * MessageChannels differ from traditional FIFO Queues in that 
 * elements put in are keyed and can be taken out of order.
 * 
 * Different implementations may allow for variations on 
 * how the MessageChannel model is implemented.  For instance,
 * the code will ship with a NonPersistentMessageChannel that
 * will store all contained objects in memory.  The fact that 
 * everything is stored in memory means that the Channel is not 
 * fault tolerant.  If fault tolerance is required, then a 
 * PersistentMessageChannel must be created that stores the 
 * MessageContext objects somehow.
 * 
 * @author James M Snell (jasnell@us.ibm.com)
 */
public interface MessageChannel {

    /**
     * Select, but do not remove the next message on the
     * channel.  If one does not exist, return null
     */
    public MessageExchangeContext peek();

    /**
     * Put a message onto the channel
     */
    public void put(
            Object key,
            MessageExchangeContext context);

    /**
     * Cancel a message that has been put on the channel.
     * Unlike select(Object key), this method will not block
     * and wait for a message with the specified key to be
     * put onto the MessageChannel.
     */
    public MessageExchangeContext cancel(
            Object key);

    /**
     * Select and remove all of the messages currently in
     * the channel (useful for bulk operations).  This 
     * method will not block.  It is also not guaranteed
     * that the Channel will be empty once this operation
     * returns (it is possible that another thread may 
     * put new MessageContexts into the channel before this
     * operation completes)
     */
    public MessageExchangeContext[] selectAll();

    /**
     * Select and remove the next message in the channel
     * If a message is not available, wait indefinitely for one
     */
    public MessageExchangeContext select()
            throws InterruptedException;

    /**
     * Select and remove the next message in the channel
     * If a message is not available, wait the specified amount
     * of time for one
     */
    public MessageExchangeContext select(
            long timeout)
            throws InterruptedException;

    /**
     * Select and remove a specific message in the channel
     * If the message is not available, wait indefinitely 
     * for one to be available
     */
    public MessageExchangeContext select(
            Object key)
            throws InterruptedException;

    /**
     * Select and remove a specific message in the channel
     * If the message is not available, wait the specified 
     * amount of time for one
     */
    public MessageExchangeContext select(
            Object key,
            long timeout)
            throws InterruptedException;
}
