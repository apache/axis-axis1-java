package org.apache.axis.handlers ;

import java.io.* ;
import java.sql.Time ;
import org.apache.axis.* ;
import org.apache.axis.utils.* ;

public class LogHandler implements Handler {
  public void init() {
  }

  public void cleanup() {
  }

  public void invoke(MessageContext msgContext) throws Exception {
    System.err.println( "In LogHandler" );
    try {
      FileWriter  fw   = new FileWriter( "axis.log", true );
      PrintWriter pw   = new PrintWriter( fw );

      Message inMsg = msgContext.getIncomingMessage();
      Message outMsg = msgContext.getOutgoingMessage();
  
      pw.println( "=======================================================" );
      pw.println( "= " + ( new Time(System.currentTimeMillis()) ).toString() );
      pw.println( "= InMsg: " + inMsg );
      pw.println( "= InMsg: " + (inMsg == null ? "-" : inMsg.getAsString()));
      pw.println( "= OutMsg: " + outMsg );
      pw.println( "= OutMsg: " + (outMsg == null ? "-" : outMsg.getAsString()));
      pw.println( "=======================================================" );
  
      pw.close();
    }
    catch( Exception e ) {
      e.printStackTrace( System.err );
    }
  }

  public void undo(MessageContext msgContext) {
    try {
      System.err.println( "In LogHandler:undo" );
      FileWriter  fw   = new FileWriter( "axis.log", true );
      PrintWriter pw   = new PrintWriter( fw );
      pw.println( "=====================" );
      pw.println( "= Fault occurred " );
      pw.println( "=====================" );
      pw.close();
    } catch( Exception e ) {
      e.printStackTrace( System.err );
    }
  }

  public boolean canHandleBlock(QName qname) {
    return( false );
  }

  public QName[] getBlocksHandled() {
    return( null );
  }
};
