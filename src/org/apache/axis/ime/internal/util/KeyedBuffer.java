/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.axis.ime.internal.util;

/**
 * A KeyedBuffer is a low level hybrid FIFO Queue and Keyed map
 * Each MessageExchange implementation will create at least two
 * KeyedBuffer's, one for messages being sent, and another for
 * messages that have been received.
 * 
 * KeyedBuffers differ from traditional FIFO Queues in that 
 * elements put in are keyed and can be taken out of order.
 * 
 * Different implementations may allow for variations on 
 * how the KeyedBuffer model is implemented.  For instance,
 * the code will ship with a NonPersistentKeyedBuffer that
 * will store all contained objects in memory.  The fact that 
 * everything is stored in memory means that the buffer is not 
 * fault tolerant.  If fault tolerance is required, then a 
 * Persistent KeyedBuffer must be created that persists the 
 * objects somehow.
 * 
 * @author James M Snell (jasnell@us.ibm.com)
 */
public interface KeyedBuffer {

    /**
     * Select, but do not remove the next message on the
     * channel.  If one does not exist, return null
     */
    public Object peek();

    /**
     * Select, but do not remove all messages on the 
     * channel.  This method will not block.
     */
    public Object[] peekAll();

    /**
     * Put a message onto the channel
     */
    public void put(
            Object key,
            Object context);

    /**
     * Cancel a message that has been put on the channel.
     * Unlike select(Object key), this method will not block
     * and wait for a message with the specified key to be
     * put onto the MessageChannel.
     */
    public Object cancel(
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
    public Object[] selectAll();

    /**
     * Select and remove the next message in the channel
     * If a message is not available, wait indefinitely for one
     */
    public Object select()
            throws InterruptedException;

    /**
     * Select and remove the next message in the channel
     * If a message is not available, wait the specified amount
     * of time for one
     */
    public Object select(
            long timeout)
            throws InterruptedException;

    /**
     * Select and remove a specific message in the channel
     * If the message is not available, wait indefinitely 
     * for one to be available
     */
    public Object select(
            Object key)
            throws InterruptedException;

    /**
     * Select and remove a specific message in the channel
     * If the message is not available, wait the specified 
     * amount of time for one
     */
    public Object select(
            Object key,
            long timeout)
            throws InterruptedException;
    
    /**
     * Select and remove the next object in the buffer
     * (does not wait for a message to be put into the buffer)
     */        
    public Object get();

    /**
     * Select and remove the specified object in the buffer
     * (does not wait for a message to be put into the buffer)
     */            
    public Object get(Object key);
    
}
