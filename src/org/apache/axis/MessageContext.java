package org.apache.axis ;

import java.util.* ;
import org.apache.axis.* ;

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
   * 
   */
  private Hashtable bag ;

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

  public Object getProperty(String propName) {
    if ( bag == null ) return( null );
    return( bag.get(propName) );
  }

  public void setProperty(String propName, Object propValue) {
    if ( bag == null ) bag = new Hashtable() ;
    bag.put( propName, propValue );
  }

};
