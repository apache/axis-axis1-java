package org.apache.axis.common ;

import java.io.Serializable ;
import org.apache.axis.common.* ;

public interface Chainable extends Serializable {
  /**
   * Init is called when the chain containing this Chainable object
   * is instantiated.
   */
  void init();

  /**
   * Cleanup is called when the chain containing this Chainable object
   * is done processing the chain.
   */
  void cleanup();

  /**
   * Invoke is called to do the actual work of the Chainable object.
   */
  void invoke(MessageContext message);

};
