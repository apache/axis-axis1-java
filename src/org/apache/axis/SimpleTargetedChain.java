package org.apache.axis ;

import java.util.* ;
import org.apache.axis.* ;
import org.apache.axis.utils.* ;

public class SimpleTargetedChain implements Handler {
  Chain      inputChain ;
  Handler    pivotHandler ;
  Chain      outputChain ;

  public void init() { 
    if ( inputChain   != null )   inputChain.init();
    if ( pivotHandler != null ) pivotHandler.init();
    if ( outputChain  != null )  outputChain.init();
  }

  public void cleanup() {
    if ( inputChain   != null )   inputChain.cleanup();
    if ( pivotHandler != null ) pivotHandler.cleanup();
    if ( outputChain  != null )  outputChain.cleanup();
  }

  /**
   * Invoke the input chain, pivot handler and output chain.  If there's
   * a fault we need to make sure that we undo any completed handler
   * that has been successfully invoked and then rethrow the fault.
   */
  public void invoke(MessageContext msgContext) throws Exception {
    if ( inputChain != null ) inputChain.invoke( msgContext );
    try {
      if ( pivotHandler != null ) pivotHandler.invoke( msgContext );
    }
    catch( Exception e ) {
      if ( inputChain != null ) inputChain.undo( msgContext );
      throw e ;
    }
    try {
      if ( outputChain != null )  outputChain.invoke( msgContext );
    }
    catch( Exception e ) {
      if ( pivotHandler != null ) pivotHandler.undo( msgContext );
      if ( inputChain   != null )   inputChain.undo( msgContext );
      throw e ;
    }
  }

  /**
   * Undo all of the work - in reverse order.
   */
  public void undo(MessageContext msgContext) {
    System.err.println( "In SimpleTargetedChain:undo" );
    if ( outputChain   != null )   outputChain.undo( msgContext );
    if ( pivotHandler  != null )  pivotHandler.undo( msgContext );
    if ( inputChain    != null )    inputChain.undo( msgContext );
  }

  public boolean canHandleBlock(QName qname) {
    return( (inputChain==null)   ? false : inputChain.canHandleBlock(qname) ||
            (pivotHandler==null) ? false : pivotHandler.canHandleBlock(qname) ||
            (outputChain==null)  ? false : outputChain.canHandleBlock(qname) );
  }

  public QName[] getBlocksHandled() {
    QName[][]  lists = new QName[3][] ;
    ArrayList  result = null ;

    if ( inputChain   != null ) lists[0] = inputChain.getBlocksHandled() ;
    if ( pivotHandler != null ) lists[1] = pivotHandler.getBlocksHandled() ;
    if ( outputChain  != null ) lists[2] = outputChain.getBlocksHandled() ;

    for ( int i = 0 ; i < 3 ; i++ ) {
      if ( lists[i] == null || lists[i].length == 0 ) continue ;
      if ( result == null ) result = new ArrayList();
      result.addAll( Arrays.asList( lists[i] ) );
    }
    if ( result == null || result.size() == 0 ) return( null );
    return( (QName[]) result.toArray() );
  }

  public Chain getInputChain() { return( inputChain ); }

  public void setInputChain(Chain inChain) { inputChain = inChain ; }

  public Handler getPivotHandler() { return( pivotHandler ); }

  public void setPivotHandler(Handler handler) { pivotHandler = handler ; }

  public Chain getOutputChain() { return( outputChain ); }

  public void setOutputChain(Chain outChain) { outputChain = outChain ; }
  
  public void clear() {
    inputChain = null ;
    pivotHandler = null ;
    outputChain = null ;
  }

};
