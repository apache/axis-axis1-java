package org.apache.axis.handlers ;

import org.w3c.dom.* ;
import org.apache.axis.* ;
import org.apache.axis.utils.* ;

public class DispatchHandler implements Handler {
  public void init() {
  }

  public void cleanup() {
  }

  public void invoke(MessageContext msgContext) throws Exception {
    System.err.println("In DispatchHandler");

    Document  doc = msgContext.getIncomingMessage().getAsDOMDocument();
    Message msg = new Message( doc, "Document" );
    msgContext.setOutgoingMessage( msg );

    // Right now its just an echo - but I'll add more once the
    // reader/writer is there
  }

  public void undo(MessageContext msgContext) { 
    System.err.println( "In DispatchHandler:undo" );
  }

  public boolean canHandleBlock(QName qname) {
    return( false );
  }

  public QName[] getBlocksHandled() {
    return( null );
  }
};
