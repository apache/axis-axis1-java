// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif;

import java.rmi.RemoteException;

/**
 * A WSIFException is used to indicate something going wrong
 * within the Web service invocation framework and not something
 * that is related to the service invocation itself.
 *
 * @author Paul Fremantle
 * @author Alekander Slominski
 * @author Matthew J. Duftler
 * @author Sanjiva Weerawarana
 * @author Nirmal Mukhi
 */
public class WSIFException extends RemoteException {

  public WSIFException (String msg) {
    super (msg);
  }

  public WSIFException (String msg, Throwable targetException) {
    this (msg);
    this.detail = targetException;
  }

  public void setTargetException (Throwable targetException) {
    this.detail = targetException;
  }

  public Throwable getTargetException () {
    return detail;
  }

//  public String getMessage () {
//    String msg = super.getMessage ();
//    if (detail != null) {
//      msg += "; target message was '" + detail.getMessage () + "'";
//    }
//    return msg;
//  }
//
//
//  public String toString () {
//    return "[WSIFException: msg=" + getMessage () +
//      ((detail != null) ? ("; targetException=" + detail)
//       : "") + "]";
//  }
}

