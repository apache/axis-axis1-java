package org.apache.axis ;

import java.io.Serializable ;
import org.apache.axis.* ;
import org.apache.axis.utils.* ;

public interface Handler extends Serializable {
  /**
   * Init is called when the chain containing this Handler object
   * is instantiated.
   */
  public void init();

  /**
   * Cleanup is called when the chain containing this Handler object
   * is done processing the chain.
   */
  public void cleanup();

  /**
   * Invoke is called to do the actual work of the Handler object.
   * If there is a fault during the processing of this method it is
   * invoke's job to catch the exception and undo any partial work
   * that has been completed.  Once we leave 'invoke' if a fault
   * is thrown, this classes 'undo' method will be called to undo
   * the work that 'invoke' did.
   * Invoke should rethrow any exceptions it catches.
   */
  public void invoke(MessageContext msgContext) throws Exception ;

  /**
   * Called when a fault occurs to 'undo' whatever 'invoke' did.
   */
  public void undo(MessageContext msgContext);

  /**
   * Can this Handler process this QName?
   */
  public boolean canHandleBlock(QName qname);

  /**
   * Get the list of QNames that are processed by this Handler
   */
  public QName[] getBlocksHandled();

};
