// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.util.jms;

import javax.jms.*;
import com.ibm.wsif.*;

/**
 * Useful JMS constants.
 * @author Mark Whitlock
 */
public final class WSIFJmsConstants 
{
  public static final long WAIT_FOREVER = 0;       // sync and async getwaittimeout
  static final String REPLY_TO = "replyTo"; // name of the JMS attribute
  static final String JMS_CORRELATION_ID = "JMSCorrelationID"; // QueueReceiver selector
  
  static final WSIFException ToWsifException(Throwable t)
  {
    return new WSIFException("WSIF Jms support caught '"+t+
      ((t instanceof JMSException) ?
      ("' linked exception '"+((JMSException)t).getLinkedException()+"'") : "'"));
  }
    
}

