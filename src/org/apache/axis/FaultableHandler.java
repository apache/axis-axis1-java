package org.apache.axis ;

import java.util.*;
import java.io.Serializable ;
import org.apache.axis.* ;
import org.apache.axis.utils.* ;

public class FaultableHandler implements Handler {
  Handler    workHandler ;
  Hashtable  faultHandlers ;

  public void init() {
    workHandler.init();
  }

  public void cleanup() {
    workHandler.cleanup();
  }

  /**
   * Invokes the specified handler.  If there's a fault the appropriate
   * key will be calculated and used to find the fault chain to be
   * invoked.  This assumes that the workHandler has caught the exception
   * and already processed it's undo logic - as needed.
   */
  public void invoke(MessageContext msgContext) throws Exception {
    try {
      workHandler.invoke( msgContext );
    }
    catch( Exception e ) {
      // Is this a Java Exception? a SOAPException? an AxisException?
      String   key          = "blah" ; // add logic to map from e -> key
      Handler  faultHandler = (Handler) faultHandlers.get( key );
      if ( faultHandler == null ) throw e ;
      faultHandler.invoke( msgContext );
      throw e ;
    }
  }

  /**
   * Some handler later on has faulted so we need to undo our work.
   */
  public void undo(MessageContext msgContext) {
    System.err.println( "In FaultableHandler:undo" );
    workHandler.undo( msgContext );
  };

  public boolean canHandleBlock(QName qname) {
    return( workHandler.canHandleBlock(qname) );
  }

  public QName[] getBlocksHandled() {
    return( workHandler.getBlocksHandled() );
  }

};
