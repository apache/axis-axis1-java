/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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
