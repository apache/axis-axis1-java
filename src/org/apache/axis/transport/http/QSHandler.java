package org.apache.axis.transport.http;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;

/**
 * The QSHandler interface defines an interface for classes that handle the
 * actions necessary when a particular query string is encountered in an AXIS
 * servlet invocation.
 *
 * @author Curtiss Howard
 */

public interface QSHandler {
     /**
      * Performs the action associated with this particular query string
      * handler.
      *
      * @param msgContext a MessageContext object containing message context
      *        information for this query string handler.
      * @throws AxisFault if an error occurs.
      */
     
     public void invoke (MessageContext msgContext) throws AxisFault;
}
