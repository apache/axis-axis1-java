package org.apache.axis.server ;

import org.apache.axis.* ;
import org.apache.axis.utils.* ;
import org.apache.axis.handlers.* ;
import org.apache.axis.registries.* ;

public class SimpleAxisEngine implements Handler {
  /**
   * This entry point into the SOAP server 
   */

  public void init() {
  }

  public void cleanup() {
  };

  public void invoke(MessageContext msgContext) throws Exception {
    // Load the simple handler registry and init it
    HandlerRegistry  hr = new SimpleHandlerRegistry();
    hr.init();

    // Load the simple deployed services registry and init it
    HandlerRegistry  sr = new SimpleServiceRegistry();
    sr.init();

    String action = (String) msgContext.getProperty( "SOAPAction" );
    if ( action == null ) action = "EchoService" ;

    Handler h = sr.find( action );

    if ( h == null ) {
      Message msg = new Message( "Service '"+action+"' not found", "String" );
      msgContext.setOutgoingMessage( msg );
      return ;
    }

    h.init();
    try {
      h.invoke( msgContext );
    }
    catch( Exception e ) {
      // Should we even bother catching it ?
      throw e ;
    }
    h.cleanup();
  };

  public void undo(MessageContext msgContext) {
  };

  public boolean canHandleBlock(QName qname) {
    return( false );
  };

  public QName[] getBlocksHandled() {
    return( null );
  }

};
