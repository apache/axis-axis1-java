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
