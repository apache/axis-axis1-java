/*
 * The Apache Software License, Version 1.1
 *
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
 *    Apache Software Foundation (http://www.apache.org/)."
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

package org.apache.axis.client ;

import java.util.* ;
import org.apache.axis.* ;
import org.apache.axis.utils.Debug ;
import org.apache.axis.handlers.* ;
import org.apache.axis.registries.* ;
import org.apache.axis.transport.http.HTTPSender;
import org.apache.axis.transport.local.LocalSender;

/**
 * Provides the equivalent of an "Axis engine" on the client side.
 * Subclasses hardcode initialization & setup logic for particular
 * client-side transports.
 *
 * @author Rob Jellinghaus (robj@unrealities.com)
 * @author Doug Davis (dug@us.ibm.com)
 * @author Glen Daniels (gdaniels@allaire.com)
 */
public class AxisClient extends AxisEngine
{
    public AxisClient()
    {
        super();
    }
    
    /**
     * Main routine of the AXIS engine.  In short we locate the appropriate
     * handler for the desired service and invoke() it.
     */
    public void invoke(MessageContext msgContext) throws AxisFault {
        Debug.Print( 1, "Enter: AxisClient::invoke" );
        
        String  hName = null ;
        Handler h     = null ;
        
        HandlerRegistry hr = getHandlerRegistry();
        HandlerRegistry sr = getServiceRegistry();
        
        try {
            hName = msgContext.getStrProp( MessageContext.ENGINE_HANDLER );
            Debug.Print( 2, "EngineHandler: " + hName );
            
            if ( hName != null ) {
                h = hr.find( hName );
                if ( h != null )
                    h.invoke(msgContext);
                else
                    throw new AxisFault( "Client.error",
                                        "Can't locate handler: " + hName,
                                        null, null );
            }
            else {
                // This really should be in a handler - but we need to discuss it
                // first - to make sure that's what we want.
                
                /* Now we do the 'real' work.  The flow is basically:         */
                /*                                                            */
                /*   Service Specific Request Chain                           */
                /*   Global Request Chain                                     */
                /*   Transport Request Chain - must have a send at the end    */
                /*   Transport Response Chain                                 */
                /*   Global Response Chain                                    */
                /*   Service Specific Response Chain                          */
                /*   Protocol Specific-Handler/Checker                        */
                /**************************************************************/
                
                // When do we call init/cleanup??
                
                SimpleTargetedChain service = null ;
                
                /* Process the Service Specific Request Chain */
                /**********************************************/
                hName =  msgContext.getTargetService();
                if ( hName != null && (h = sr.find( hName )) != null ) {
                    if ( h instanceof SimpleTargetedChain ) {
                        service = (SimpleTargetedChain) h ;
                        h = service.getRequestHandler();
                    }
                    if ( h != null ) h.invoke( msgContext );
                }
                
                /* Process the Global Request Chain */
                /**********************************/
                hName = Constants.GLOBAL_REQUEST ;
                if ( hName != null  && (h = hr.find( hName )) != null )
                    h.invoke(msgContext);
                
                /** Process the Transport Specific stuff
                 * 
                 * NOTE: Somewhere in here there is a handler which actually
                 * sends the message and receives a response.  Generally
                 * this is the pivot point in the Transport chain.
                 */
                hName = msgContext.getTransportName();
                HandlerRegistry tr = getTransportRegistry();
                if ( hName != null && (h = tr.find( hName )) != null )
                    h.invoke(msgContext);
                                
                /* Process the Global Response Chain */
                /***********************************/
                hName = Constants.GLOBAL_RECEIVE ;
                if ( hName != null && (h = hr.find( hName )) != null )
                    h.invoke(msgContext);
                
                if ( service != null ) {
                    h = service.getResponseHandler();
                    if ( h != null )
                        h.invoke(msgContext);
                }
                
                // Do SOAP Semantics checks here - this needs to be a call to
                // a pluggable object/handler/something
            }
        }
        catch( Exception e ) {
            // Should we even bother catching it ?
            Debug.Print( 1, e );
            if ( !(e instanceof AxisFault) ) e = new AxisFault( e );
            throw (AxisFault) e ;
        }
        Debug.Print( 1, "Exit: AxisClient::invoke" );
    };
    
    public void undo(MessageContext msgContext) {
        Debug.Print( 1, "Enter: AxisClient::undo" );
        Debug.Print( 1, "Exit: AxisClient::undo" );
    }
}

