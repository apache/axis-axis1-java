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

package org.apache.axis.handlers;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.SOAPPart;
import org.apache.axis.monitor.SOAPMonitorConstants;
import org.apache.axis.monitor.SOAPMonitorService;

/**
 * This handler is used to route SOAP messages to the
 * SOAP monitor service.
 *
 * @author Brian Price (pricebe@us.ibm.com)
 */

public class SOAPMonitorHandler extends BasicHandler {

  private static long next_message_id = 1;

  /**
   * Constructor
   */
  public SOAPMonitorHandler() {
    super();
  }

  /**
   * Process and SOAP message
   */
  public void invoke(MessageContext messageContext) throws AxisFault {
    String  target = messageContext.getTargetService();
    // Check for null target
    if (target == null) {
        target = "";
    }
    // Get id, type and content
    Long    id;
    Integer type;
    Message message;
    if (!messageContext.getPastPivot()) {
      id = assignMessageId(messageContext);
      type = new Integer(SOAPMonitorConstants.SOAP_MONITOR_REQUEST);
      message = messageContext.getRequestMessage();
    } else {
      id = getMessageId(messageContext);
      type = new Integer(SOAPMonitorConstants.SOAP_MONITOR_RESPONSE);
      message = messageContext.getResponseMessage();
    }
    // Get the SOAP portion of the message
    String  soap = null;
    if (message != null) {
      soap = ((SOAPPart)message.getSOAPPart()).getAsString();
    }
    // If we have an id and a SOAP portion, then send the
    // message to the SOAP monitor service
    if ((id != null) && (soap != null)) {
      SOAPMonitorService.publishMessage(id,type,target,soap);
    }
  }

  /**
   * Assign a new message id
   */
  private Long assignMessageId(MessageContext messageContext) {
    Long id = null;
    synchronized(SOAPMonitorConstants.SOAP_MONITOR_ID) {
      id = new Long(next_message_id);
      next_message_id++;
    }
    messageContext.setProperty(SOAPMonitorConstants.SOAP_MONITOR_ID, id);
    return id;
  }

  /**
   * Get the already assigned message id
   */
  private Long getMessageId(MessageContext messageContext) {
    Long id = null;
    id = (Long) messageContext.getProperty(SOAPMonitorConstants.SOAP_MONITOR_ID);
    return id;
  }
}
