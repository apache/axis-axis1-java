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

package org.apache.axis.server ;

import java.util.* ;
import org.apache.axis.* ;
import org.apache.axis.utils.* ;
import org.apache.axis.handlers.* ;
import org.apache.axis.handlers.soap.* ;
import org.apache.axis.registries.* ;
import org.apache.axis.encoding.SOAPTypeMappingRegistry;
import org.apache.axis.encoding.TypeMappingRegistry;

/**
 *
 * @author Doug Davis (dug@us.ibm.com)
 * @author Glen Daniels (gdaniels@allaire.com)
 */
public class AxisServer extends AxisEngine
{
    public AxisServer() {
      super(Constants.SERVER_HANDLER_REGISTRY,
            Constants.SERVER_SERVICE_REGISTRY);
    }

    /**
     * Allows the Listener to specify which handler/service registry
     * implementation they want to use.
     */
    public AxisServer(HandlerRegistry handlers, HandlerRegistry services)
    {
        super(handlers, services);
    }

    /**
     * Is this running on the server?
     */
    public boolean isOnServer() { return true; }

    /**
     * Main routine of the AXIS server.  In short we locate the appropriate
     * handler for the desired service and invoke() it.
     */
    public void invoke(MessageContext msgContext) throws AxisFault {
        Debug.Print( 1, "Enter: AxisServer::invoke" );

        String  hName = null ;
        Handler h     = null ;

        /* Do some prep-work.  Get the registries and put them in the */
        /* msgContext so they can be used by later handlers.          */
        /**************************************************************/
        HandlerRegistry hr = getHandlerRegistry();
        HandlerRegistry sr = getServiceRegistry();
        TypeMappingRegistry tmr =
            (TypeMappingRegistry) getOption(Constants.TYPEMAP_REGISTRY);

        msgContext.setAxisEngine( this );

        msgContext.setTypeMappingRegistry(tmr);

        try {
          hName = msgContext.getStrProp( MessageContext.ENGINE_HANDLER );
          if ( hName != null ) {
              if ( hr == null || (h = hr.find(hName)) == null ) {
                AxisClassLoader cl = msgContext.getClassLoader();
                try {
                  Debug.Print( 2, "Trying to load class: " + hName );
                  Class cls = cl.loadClass( hName );
                  h = (Handler) cls.newInstance();
                }
                catch( Exception e ) {
                  h = null ;
                }
              }
              if ( h != null )
                h.invoke(msgContext);
              else
                throw new AxisFault( "Server.error",
                                     "Can't locate handler: " + hName,
                                     null, null );
          }
          else {
          // This really should be in a handler - but we need to discuss it
          // first - to make sure that's what we want.
              /* Now we do the 'real' work.  The flow is basically:         */
              /*   Transport Specific Input Handler/Chain                   */
              /*   Global Input Handler/Chain                               */
              /*   Protocol Specific-Handler(ie. SOAP, XP)                  */
              /*     ie. For SOAP Handler:                                  */
              /*           - Service Specific Input Handler/Chain           */
              /*           - SOAP Semantic Checks                           */
              /*           - Service Specific Output Handler/Chain          */
              /*   Global Output Handler/Chain                              */
              /*   Transport Specific Output Handler/Chain                  */
              /**************************************************************/
  
              // When do we call init/cleanup??
              Debug.Print(1, "Calling default logic in AxisServer" );

              /*  This is what the entirety of this logic might evolve to:
              
              hName = msgContext.getStrProp(MessageContext.TRANSPORT);
              if ( hName != null ) {
                if ((h = hr.find( hName )) != null ) {
                  h.invoke(msgContext);
                } else {
                  System.err.println("Couldn't find transport " + hName);
                }
              } else {
                // No transport set, so use the default (probably just
                // calls the global->service handlers)
                defaultTransport.invoke(msgContext);
              }

              */
              
              /* Process the Transport Specific Input Chain */
              /**********************************************/
              hName = msgContext.getStrProp(MessageContext.TRANS_INPUT);
              if ( hName != null && (h = hr.find( hName )) != null )
                h.invoke(msgContext);
      
              /* Process the Global Input Chain */
              /**********************************/
              hName = Constants.GLOBAL_INPUT ;
              if ( hName != null  && (h = hr.find( hName )) != null )
                  h.invoke(msgContext);
      
              /* Process the Protocol Specific-Handler/Chain */
              /***********************************************/
              /*
              hName = msgContext.getStrProp(MessageContext.PROTOCOL_HANDLER);
              if ( hName == null ) hName = "SOAPServer" ;
              if ( hName != null && (h = hr.find( hName )) != null )
                h.invoke(msgContext);
              else
                throw new AxisFault( "Server.error",
                                     "Can't find '" + hName + "' handler",
                                     null, null );
              */
              h = msgContext.getServiceHandler();
              if (h != null)
                  h.invoke(msgContext);
              else
                  throw new AxisFault("Server.NoService",
                                      "The Axis engine couldn't find a " +
                                      "target service to invoke!",
                                      null, null );
      
              /* Process the Global Output Chain */
              /***********************************/
              hName = Constants.GLOBAL_OUTPUT ;
              if ( hName != null && (h = hr.find( hName )) != null )
                h.invoke(msgContext);
      
              /* Process the Transport Specific Output Chain */
              /***********************************************/
              hName = msgContext.getStrProp(MessageContext.TRANS_OUTPUT);
              if ( hName != null  && (h = hr.find( hName )) != null )
                h.invoke(msgContext);
          }
        }
        catch( Exception e ) {
            // Should we even bother catching it ?
            if ( !(e instanceof AxisFault) ) e = new AxisFault( e );
            throw (AxisFault) e ;
        }
        Debug.Print( 1, "Exit: AxisServer::invoke" );
    };

    public void undo(MessageContext msgContext) {
        Debug.Print( 1, "Enter: AxisServer::undo" );
        Debug.Print( 1, "Exit: AxisServer::undo" );
    };
};
