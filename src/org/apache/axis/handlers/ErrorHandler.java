package org.apache.axis.handlers ;

import org.apache.axis.* ;
import org.apache.axis.utils.* ;

public class ErrorHandler implements Handler {
  public void init() {
  }

  public void cleanup() {
  }

  public void invoke(MessageContext msgContext) throws Exception {
    System.err.println("In EchoHandler");
    throw new Exception( "ERROR" );
  }

  public void undo(MessageContext msgContext) {
    System.err.println( "In EchoHandler::undo" );
  }

  public boolean canHandleBlock(QName qname) {
    return( false );
  }

  public QName[] getBlocksHandled() {
    return( null );
  }
};
