package org.apache.axis ;

import java.util.* ;
import org.apache.axis.* ;
import org.apache.axis.utils.* ;

public class SimpleChain implements Chain {
  private Vector  handlers ;

  public void init() {
    for ( int i = 0 ; i < handlers.size() ; i++ )
      ((Handler) handlers.elementAt( i )).init();
  }

  public void cleanup() {
    for ( int i = 0 ; i < handlers.size() ; i++ )
      ((Handler) handlers.elementAt( i )).cleanup();
  }

  /**
   * Iterate over the chain invoking each handler.  If there's a fault
   * then call 'undo' for each completed handler in reverse order, then 
   * rethrow the exception.
   */
  public void invoke(MessageContext msgContext) throws Exception {
    int i = 0 ;
    try {
      for ( i = 0 ; i < handlers.size() ; i++ )
        ((Handler) handlers.elementAt( i )).invoke( msgContext );
    }
    catch( Exception e ) {
      // undo in reverse order - rethrow
      while( --i >= 0 )
        ((Handler) handlers.elementAt( i )).undo( msgContext );
      throw e ;
    }
  }

  /**
   * Undo all of the work this chain completed because some handler
   * later on has faulted - in reverse order.
   */
  public void undo(MessageContext msgContext) {
    System.err.println( "In SimpleChain::undo" );
    for ( int i = handlers.size()-1 ; i >= 0 ; i-- )
      ((Handler) handlers.elementAt( i )).undo( msgContext );
      
  }

  public boolean canHandleBlock(QName qname) {
    for ( int i = 0 ; i < handlers.size() ; i++ )
      if ( ((Handler) handlers.elementAt( i )).canHandleBlock(qname) )
        return( true );
    return( false );
  }

  public QName[] getBlocksHandled() {
    // Could there be dups??
    if ( handlers.size() == 0 ) return( null );
    ArrayList  result = new ArrayList();
    for ( int i = 0 ; i < handlers.size() ; i++ ) {
      QName[]  tmp = ((Handler) handlers.elementAt(i)).getBlocksHandled();
      result.addAll( Arrays.asList( tmp ) );
    }

    return( (QName[]) result.toArray() );
  }

  public void addHandler(Handler handler) {
    if ( handlers == null ) handlers = new Vector();
    handlers.add( handler );
  }

  public void removeHandler(int index) {
    if ( handlers != null )
      handlers.removeElementAt( index );
  }

  public void clear() {
    handlers.clear();
  }

  public boolean contains(Handler handler) {
    return( handlers != null ? handlers.contains( handler ) : false );
  }

  public Handler[] getHandlers() {
    return( (Handler[]) handlers.toArray() );
  }
};
