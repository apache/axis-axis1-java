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

package org.apache.axis.handlers;

import org.apache.axis.*;
import org.apache.axis.utils.Debug;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.registries.* ;

/** 
 * @author Doug Davis (dug@us.ibm.com)
 */
public class SOAPServerHandler extends BasicHandler 
{
    public void invoke(MessageContext msgContext) throws AxisFault
    {
        Debug.Print( 1, "Enter: SOAPServerHandler::invoke" );
        SimpleTargetedChain stc = null ;

        String target = msgContext.getTargetService();
        if ( target == null ) {
          Debug.Print( 1, "No target set - looking in the Body element" );
        }
        else
          Debug.Print( 1, "Target: " + target );
        
        if ( target == null )
          throw new AxisFault( "AxisServer.error",
                               "No target field set", null, null );

        HandlerRegistry serviceReg = null ;
        serviceReg = 
          (HandlerRegistry)msgContext.getProperty(Constants.SERVICE_REGISTRY);

        if ( serviceReg == null )
          throw new AxisFault("AxisServer.error",
                              "No service registry set", null, null );
                         
        Handler service  = serviceReg.find( target );
        Handler h = null ;
        if ( service == null )
          throw new AxisFault("AxisSever.error",
                              "Can't find target handler: " + target, 
                              null, null );
        msgContext.setProperty( MessageContext.SVC_HANDLER, service );
        
        if ( service instanceof SimpleTargetedChain ) {
          Debug.Print( 2, "Invoking input chain" );
          stc = (SimpleTargetedChain) service ;
          h = stc.getInputChain() ;
          if ( h != null ) h.invoke(msgContext);
        }

        // Do SOAP semantics here

        if ( stc != null ) {
          h = stc.getPivotHandler();
          if ( h != null ) {
            Debug.Print( 2, "Invoking service/pivot" );
            h.invoke(msgContext);
          }
          h = stc.getOutputChain();
          if ( h != null ) {
            Debug.Print( 2, "Invoking output chain" );
            h.invoke(msgContext);
          }
        }
        else {
          Debug.Print( 2, "Invoking service" );
          service.invoke(msgContext);
        }

        Debug.Print( 1, "Exit : SOAPServerHandler::invoke" );
    }

    public void undo(MessageContext msgContext) 
    {
        Debug.Print( 1, "Enter: SOAPServerHandler::undo" );
        Debug.Print( 1, "Exit: SOAPServerHandler::undo" );
    }
}
