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

package org.apache.axis.handlers;

import javax.xml.namespace.QName;
import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.SOAPPart;
import org.apache.axis.handlers.BasicHandler;
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
  private String wsdlURL = null;
  private QName  serviceQName = null;
  private QName  portQName = null;

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
        target = new String();
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
