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
 * 4. The names "Axis" and "Apache Software Foundation" must
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
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.axis.handlers ;

import java.util.* ;
import java.lang.reflect.* ;
import org.apache.axis.* ;
import org.apache.axis.utils.* ;
import org.apache.axis.utils.cache.* ;
import org.apache.axis.message.* ;

/**
 *
 * @author Doug Davis (dug@us.ibm.com)
 */
public class RPCDispatchHandler extends BasicHandler {
  private static final boolean DEBUG_LOG = false;

  public void invoke(MessageContext msgContext) throws AxisFault {
    Debug.Print( 1, "Enter: RPCDispatchHandler::invoke" );

    /* Find the service we're invoking so we can grab it's options */
    /***************************************************************/
    Handler service ;
    service = msgContext.getServiceHandler();

    /* Now get the service (RPC) specific info  */
    /********************************************/
    String  clsName    = (String) service.getOption( "className" );
    String  methodName = (String) service.getOption( "methodName" );

    try {
      /* We know we're doing a Java/RPC call so we can ask for the */
      /* SOAPBody as an RPCBody and process it accordingly.        */
      /*************************************************************/
      int             i ;
      AxisClassLoader cl     = msgContext.getClassLoader();
      JavaClass       jc     = cl.lookup(clsName); 
      Class           cls    = jc.getJavaClass();
      Object          obj    = cls.newInstance();
      Message         inMsg  = msgContext.getRequestMessage();
      Message         outMsg = msgContext.getResponseMessage();
      SOAPEnvelope    env    = (SOAPEnvelope) inMsg.getAs("SOAPEnvelope");
      Vector          bodies = env.getBodyElements();
      SOAPEnvelope    resEnv = (outMsg == null) ?
                                new SOAPEnvelope() :
                                (SOAPEnvelope)outMsg.getAs("SOAPEnvelope");
      
      /* Loop over each entry in the SOAPBody - each one is a different */
      /* RPC call.                                                      */
      /******************************************************************/
      for ( int bNum = 0 ; bNum < bodies.size() ; bNum++ ) {
        if (!(bodies.get(bNum) instanceof RPCElement))
            continue;
        
        RPCElement   body  = (RPCElement) bodies.get( bNum );
        String       mName = body.getMethodName();
        Vector       args  = body.getParams();
  
        if ( methodName != null && !methodName.equals(mName) )
          throw new AxisFault( "AxisServer.error", 
                               "Method names don't match\n" +
                                 "Body name=" + mName + "\n" +
                                 "Service name=" + methodName,
                               null, null );  // should they??

        Debug.Print( 2, "mName: " + mName );
        Debug.Print( 2, "MethodName: " + methodName );
        Method       method = jc.getMethod(mName, args.size());
        if ( method == null )
          throw new AxisFault( "AxisServer.error", 
                               "Method not found\n" +
                                 "Method name=" + clsName + "\n" +
                                 "Service name=" + methodName,
                               null, null );

        Object[] argValues  =  null ;

        if ( args != null && args.size() > 0 ) {
          argValues = new Object[ args.size()];
          for ( i = 0 ; i < args.size() ; i++ ) {
            argValues[i]  = ((RPCParam)args.get(i)).getValue() ;

            if (DEBUG_LOG) {
              System.out.println("  value: " + argValues[i] );
            }
          }
        }
  
        Object objRes = method.invoke( obj, argValues );
  
        /* Now put the result in the result SOAPEnvelope */
        /*************************************************/
        RPCElement resBody = new RPCElement(mName + "Response");
        resBody.setPrefix( body.getPrefix() );
        resBody.setNamespaceURI( body.getNamespaceURI() );
        if ( objRes != null ) {
          RPCParam param = new RPCParam("return", objRes);
          resBody.addParam(param);
        }
        resEnv.addBodyElement( resBody );
      }

      if (outMsg == null) {
        outMsg = new Message(resEnv, "SOAPEnvelope");
        msgContext.setResponseMessage( outMsg );
      }
    }
    catch( Exception exp ) {
      Debug.Print( 1, exp );
      if ( !(exp instanceof AxisFault) ) exp = new AxisFault(exp);
      throw (AxisFault) exp ;
    } 
    Debug.Print( 1, "Exit: RPCDispatchHandler::invoke" );
  }

  public void undo(MessageContext msgContext) { 
    Debug.Print( 1, "Enter: RPCDispatchHandler::undo" );
    Debug.Print( 1, "Exit: RPCDispatchHandler::undo" );
  }

};
