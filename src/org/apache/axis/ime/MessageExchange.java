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

import java.util.Map;

/**
 * Represents the boundary interface through which messages
 * are exchanged.  This interface supports both push and pull
 * models for receiving inbound messages.
 * 
 * @author James M Snell (jasnell@us.ibm.com)
 * @author Ray Chun (rchun@sonicsoftware.com)
 */
public interface MessageExchange
        extends FeatureEnabled {

    /**
     * Send an outbound message.  (Impl's of this method
     * need to create a new MessageExchangeCorrelator and 
     * put it into the MessageContext if one does not already
     * exist.)
     * @param MessageContext The Axis MessageContext being sent
     * @return MessageExchangeCorrelator The correlator for the sent MessageContext
     * @throws AxisFault
     */
    public MessageExchangeCorrelator send(
            MessageContext context)
            throws AxisFault;

    /**
     * Send an outbound message.  (Impl's of this method
     * need to create a new MessageExchangeCorrelator and 
     * put it into the MessageContext if one does not already
     * exist.)
     * @param MessageContext The Axis MessageContext being sent
     * @param MessageContextListener The listener to which responses, faults, and status updates should be delivered
     * @return MessageExchangeCorrelator The correlator for the sent MessageContext
     * @throws AxisFault
     */
    public MessageExchangeCorrelator send(
            MessageContext context,
            MessageExchangeEventListener listener)
            throws AxisFault;

    /**
     * Waits indefinitely for a message to be received
     * (blocking)
     * @return MessageContext The received MessageContext
     * @throws AxisFault
     */
    public MessageContext receive()
            throws AxisFault;

    /**
     * Waits the specified amount of time for a message to 
     * be received
     * (blocking)
     * @param long The amount of time (ms) to wait
     * @return MessageContext The received MessageContext
     * @throws AxisFault
     */
    public MessageContext receive(
            long timeout)
            throws AxisFault;

    /**
     * Waits indefinitely for a message matching the 
     * specified correlator
     * (blocking)
     * @param MessageExchangeCorrelator
     * @return MessageContext
     * @throws AxisFault
     */
    public MessageContext receive(
            MessageExchangeCorrelator correlator)
            throws AxisFault;

    /**
     * Waits the specified amount of time for a message matching the 
     * specified correlator
     * (blocking)
     * @param MessageExchangeCorrelator
     * @param long timeout
     * @returns MessageContext
     * @throws AxisFault
     */
    public MessageContext receive(
            MessageExchangeCorrelator correlator,
            long timeout)
            throws AxisFault;

    /**
     * Registers a listener for receiving messages
     * (nonblocking)
     * @param MessageContextListener
     * @throws AxisFault
     */
    public void receive(
            MessageExchangeEventListener listener)
            throws AxisFault;

    /**
     * Registers a listener for receiving messages
     * (nonblocking)
     * @param MessageExchangeCorrelator
     * @param MessageContextListener
     * @throws AxisFault
     */            
    public void receive(
            MessageExchangeCorrelator correlator,
            MessageExchangeEventListener listener)
            throws AxisFault;

    /**
     * Synchronized send and receive
     * @param MessageContext The MessageContext to send
     * @return MessageContext The received MessageContext (not guaranteed to be the same object instance as the sent MessageContext)
     * @throws AxisFault
     */
    public MessageContext sendAndReceive(
            MessageContext context)
            throws AxisFault;

    /**
     * Synchronized send and receive with timeout
     * @param MessageContext The MessageContext to send
     * @param long The length of time (ms) to wait for a response. If a response is not received within the specified amount of time, an AxisFault indicating timeout must be thrown
     * @return MessageContext The received MessageContext (not guaranteed to be the same object instance as the sent MessageContext)
     * @throws AxisFault
     */
    public MessageContext sendAndReceive(
            MessageContext context,
            long timeout)
            throws AxisFault;

    public void setMessageExchangeEventListener(
            MessageExchangeEventListener listener);
        
    public MessageExchangeEventListener getMessageExchangeEventListener();

    /**
     * @param String The id of the property
     * @param Object The value of the property
     */
    public void setProperty(
            String propertyId,
            Object propertyValue);

    /**
     * @param String The id of the property
     * @return Object The value of the property
     */
    public Object getProperty(
            String propertyId);

    /**
     * @param String The id of the property
     * @param Object The default value of the property
     * @return Object The value of the property
     */
    public Object getProperty(
            String propertyId,
            Object defaultValue);

    /**
     * @return java.lang.Map The collection of properties
     */
    public Map getProperties();

    /**
     * @param java.lang.Map The collection of properties
     */
    public void setProperties(Map properties);

    public void clearProperties();

}
