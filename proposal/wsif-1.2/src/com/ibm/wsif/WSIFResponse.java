// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif;

import javax.wsdl.QName;
import java.io.Serializable;

/**
 * This class represents a service response coming out of WSIF. It
 * contains the outgoing message or fault message, context information
 * as well as other information.
 *
 * @author Sanjiva Weerawarana <sanjiva@watson.ibm.com>
 * @author Paul Fremantle <pzf@uk.ibm.com>
 */
public class WSIFResponse implements Serializable {
  QName serviceID;
  String operationName;
  boolean isFault = false;
  WSIFMessage outgoingMessage;
  WSIFMessage faultMessage;
  WSIFMessage contextMessage;

  /**
   * Constructor.
   */
  public WSIFResponse (QName serviceID) {
    this.serviceID = serviceID;
  }

  /**
   * Get the service ID.
   */
  public QName getServiceID () {
    return serviceID;
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
   * Indicate whether this response contains a a fault message or an 
   * ok response message. Defaults to ok (i.e., the value of the flag
   * is false).
   */
  public void setIsFault (boolean isFault) {
    this.isFault = isFault;
  }

  /**
   * Get the value of the isFault flag. True if response contains a
   * fault and false otherwise.
   */
  public boolean getIsFault () {
    return isFault;
  }

  /**
   * Set the outgoing message. The outgoing message or the fault 
   * message must be set for any given response.
   */
  public void setOutgoingMessage (WSIFMessage outgoingMessage) {
    this.outgoingMessage = outgoingMessage;
  }
 
  /**
   * Get the outgoing message. 
   */
  public WSIFMessage getOutgoingMessage () {
    return outgoingMessage;
  }

  /**
   * Set the fault message. The outgoing message or the fault 
   * message must be set for any given response.
   */
  public void setFaultMessage (WSIFMessage faultMessage) {
    this.faultMessage = faultMessage;
  }
 
  /**
   * Get the fault message. 
   */
  public WSIFMessage getFaultMessage () {
    return faultMessage;
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
    return "[WSIFResponse:\n" +
      "\t serviceID = '" + serviceID + "'\n" +
      "\t operationName = '" + operationName + "'\n" +
      "\t isFault = '" + isFault + "'\n" +
      "\t outgoingMessage = '" + outgoingMessage + "'\n" +
      "\t faultMessage = '" + faultMessage + "'\n" +
      "\t contextMessage = '" + contextMessage + "']";
  }
}
