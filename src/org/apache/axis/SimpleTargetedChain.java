/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xerces" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.ibm.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

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
