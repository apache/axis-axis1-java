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
package org.apache.axis.ime;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;

import java.util.Hashtable;

/**
 * Represents the boundary interface through which messages
 * are exchanged.  This interface supports both push and pull
 * models for receiving inbound messages.
 * 
 * @author James M Snell (jasnell@us.ibm.com)
 * @author Ray Chun (rchun@sonicsoftware.com)
 */
public interface MessageExchange {

    /**
     * Send an outbound message.  (Impl's of this method
     * need to create a new MessageExchangeCorrelator and 
     * put it into the MessageContext if one does not already
     * exist.)
     * @param context The Axis MessageContext being sent
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
     * @param Object The value of the Option
     */
    public void setOption(
            String OptionId,
            Object OptionValue);

    /**
     * @param String The id of the Option
     * @return Object The value of the Option
     */
    public Object getOption(
            String OptionId);

    /**
     * @param String The id of the Option
     * @param Object The default value of the Option
     * @return Object The value of the Option
     */
    public Object getOption(
            String OptionId,
            Object defaultValue);

    /**
     * @return java.lang.Hashtable The collection of properties
     */
    public Hashtable getOptions();

    /**
     * @param java.lang.Hashtable The collection of properties
     */
    public void setOptions(Hashtable options);

    public void clearOptions();

}
