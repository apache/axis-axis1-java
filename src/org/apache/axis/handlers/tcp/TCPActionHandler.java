/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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

package org.apache.axis.handlers.tcp;

import java.net.URL;
import java.util.*;

import org.apache.axis.*;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.message.*;
import org.apache.axis.registries.*;
import org.apache.axis.utils.*;

/** A <code>TCPActionHandler</code> sets the context's TargetService
 * property from the first method name in the first RPCElement of the
 * Body.
 *
 * This is not at all the right thing, really, but it's not clear to me
 * how TargetService is *supposed* to be set in transports which have no
 * SOAPAction!
 *
 * Once Sam's changes go in, this probably goes away altogether.
 *
 * @author Rob Jellinghaus (robj@unrealities.com)
 * @author Glen Daniels (gdaniels@allaire.com)
 * @author Doug Davis (dug@us.ibm.com)
 */
public class TCPActionHandler extends BasicHandler
{
  public void invoke(MessageContext msgContext) throws AxisFault
  {
    Debug.Print( 1, "Enter: TCPActionHandler::invoke" );
    
    /** If there's already a targetService then just return.
     */
    if ( msgContext.getServiceHandler() == null ) {
      
      String targetServiceName = null;

      // There *IS* no URL for TCP transport!
      // (If there were, we might have the service name on the end of it....)
      /*
      String urlStr = msgContext.getStrProp(msgContext.TRANS_URL);
      try {
        URL url = new URL(urlStr);
        targetServiceName = url.getPath().substring(1);
      } catch (Exception ex) {
        throw new AxisFault("TCPActionHandler.invoke", "Can't parse service part of url "+urlStr, null, null);
      }
       */
      
      // This is icky (the full extraction of the SOAPEnvelope kills
      // performance), but doing it properly would require patching
      // RPCDispatchHandler to deal with a null TargetService at
      // RPCDispatch time.
      // For now, I'm trying to implement the TCP transport without
      // patching *any* existing files.  -- RobJ
      
      SOAPEnvelope    env    = (SOAPEnvelope) msgContext.getRequestMessage().getAsSOAPEnvelope();

      Vector          bodies = env.getBodyElements();
      for ( int bNum = 0 ; bNum < bodies.size() ; bNum++ ) {
        if (!(bodies.get(bNum) instanceof RPCElement))
            continue;
        RPCElement   body  = (RPCElement) bodies.get( bNum );
        String       mName = body.getMethodName();
        
        // do we have a namespace URI?
        // if so, use it
        if (body.getNamespaceURI() != null) {
          targetServiceName = body.getNamespaceURI();
        } else {
          // Do linear search over the HandlerRegistry, looking for the first
          // service with mName as its method name.  This is definitely inefficient
          // and possibly wrong.
          // ROBJ 911 What *is* the right way to get to the HandlerRegistry from here?!
          // Is it in the MessageContext?  ... let's look ...
          AxisEngine engine = (AxisEngine)msgContext.getAxisEngine();
          HandlerRegistry hr = engine.getHandlerRegistry();
          String[] list = hr.list();
          Handler handler = null;
          for (int i = 0; i < list.length; i++) {
            handler = hr.find(list[i]);
            // why would handler ever be null here?!?!
            if (handler != null) {
              String  methodName = (String) handler.getOption( "methodName" );
              if (methodName != null && methodName.equals(mName)) {
                // we found it
                targetServiceName = list[i];
                break;
              }
            }
          }
        }
        
        // if we found a target service, set it!
        if (targetServiceName != null) {
          msgContext.setTargetService( targetServiceName );
          Debug.Print( 2, "  First method name: " + mName );
        }
      }
      
    }
    
    // Just for kicks, try getting the message back as String, to cure
    // downstream handlers... don't really understand this bit
    // YOW, THIS WORKS!
    // ROBJ 911 remove this once I verify that getAs("SOAPEnvelope") twice in a row is a
    // Bad Thing.  (and once I figure out what else I should be doing!)
    String test = (String)msgContext.getRequestMessage().getAsString();
    
    Debug.Print( 1, "Exit: TCPActionHandler::invoke" );
  }
  
  public void undo(MessageContext msgContext)
  {
    Debug.Print( 1, "Enter: HTTPActionHandler::undo" );
    Debug.Print( 1, "Exit: HTTPActionHandler::undo" );
  }
}
