package org.apache.axis.common ;

import org.apache.axis.common.* ;

public class MessageContext {
  /**
   * Just a placeholder until we figure out how many messages we'll actually
   * be passing around.
   */
  private Message inMessage ;

  /**
   * Just a placeholder until we figure out how many messages we'll actually
   * be passing around.
   */
  private Message outMessage ;

  /**
   * Placeholder.
   */
  public Message getIncomingMessage() { 
    return inMessage ; 
  };

  /**
   * Placeholder.
   */
  public void setIncomingMessage(Message inMsg) { 
    inMessage = inMsg ; 
  };

  /**
   * Placeholder.
   */
  public Message getOutgoingMessage() { return outMessage ; }

  /**
   * Placeholder.
   */
  public void setOutgoingMessage(Message inMsg) { 
    outMessage = inMsg ;
  };

};
