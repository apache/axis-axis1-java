package org.apache.axis ;

import org.apache.axis.* ;

public interface Chain extends Handler {
  /**
   * Adds a handler to the end of the chain
   */
  public void addHandler(Handler handler);

  /**
   * Removes the specified handler from the chain
   */
  public void removeHandler(int index);

  /**
   * Is this handler in the chain?
   */
  public boolean contains(Handler handler);

  /**
   * Get the list of handlers in the chain - is Handler[] the right form?
   */
  public Handler[] getHandlers();

  /**
   * Erase the contents of the chain
   */
  public void clear();

  // How many do we want to force people to implement?
};
