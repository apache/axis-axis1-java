package org.apache.axis.handlers ;

import org.w3c.dom.* ;
import org.apache.axis.* ;
import org.apache.axis.utils.* ;

public class AdminHandler implements Handler {
  public void init() {
  }

  public void cleanup() {
  }

  public void invoke(MessageContext msgContext) throws Exception {
    System.err.println("In AdminHandler");

    Document doc = msgContext.getIncomingMessage().getAsDOMDocument();
    Admin    admin = new Admin();

    admin.process( doc );
    Message msg = new Message( "Done processing deployment data", "String" );
    msgContext.setOutgoingMessage( msg );
  }

  public void undo(MessageContext msgContext) { }

  public boolean canHandleBlock(QName qname) {
    return( false );
  }

  public QName[] getBlocksHandled() {
    return( null );
  }
};
