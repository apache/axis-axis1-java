// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif;

import javax.wsdl.QName;
import java.io.Serializable;

/**
 * This class represents a service request coming into WSIF. It
 * contains the incoming WSIF message, context information (in the
 * form of a WSIF message) as well as other information (service ID,
 * port name, operation name etc.).
 *
 * @author Sanjiva Weerawarana <sanjiva@watson.ibm.com>
 * @author Paul Fremantle <pzf@uk.ibm.com>
 */
public class WSIFRequest implements Serializable {
  QName serviceID;
  String portName;
  String operationName;
  WSIFMessage incomingMessage;
  WSIFMessage contextMessage;

  /**
   * Constructor.
   */
  public WSIFRequest (QName serviceID) {
    this.serviceID = serviceID;
  }

  /**
   * Get the service ID.
   */
  public QName getServiceID () {
    return serviceID;
  }

  /**
   * Set the name of the port within the service that is to be used.
   * If not set the either the service must have only one port/portType
   * or someone must be able to figure out the port from the service ID.
   */
  public void setPortName (String portName) {
    this.portName = portName;
  }

  /**
   * Get the port name.
   */
  public String getPortName () {
    return portName;
  }

  /**
   * Set the operation name.
   */
  public void setOperationName (String operationName) {
    this.operationName = operationName;
  }

  /**
   * Get the operation name.
   */
  public String getOperationName () {
    return operationName;
  }

  /**
   * Set the incoming message. 
   */
  public void setIncomingMessage (WSIFMessage incomingMessage) {
    this.incomingMessage = incomingMessage;
  }
 
  /**
   * Get the incoming message. 
   */
  public WSIFMessage getIncomingMessage () {
    return incomingMessage;
  }

  /**
   * Set the context message.
   */
  public void setContextMessage (WSIFMessage contextMessage) {
    this.contextMessage = contextMessage;
  }

  /**
   * Get the context message.
   */
  public WSIFMessage getContextMessage () {
    return contextMessage;
  }

  /**
   * Printable version.
   */
  public String toString () {
    return "[WSIFRequest:\n" +
      "\t serviceID = '" + serviceID + "'\n" +
      "\t operationName = '" + operationName + "'\n" +
      "\t incomingMessage = '" + incomingMessage + "'\n" +
      "\t contextMessage = '" + contextMessage + "']";
  }
}
