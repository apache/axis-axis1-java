/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.axis.client ;

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.components.threadpool.TaskManager;
import org.apache.axis.components.threadpool.TaskManagerFactory;
import org.apache.axis.configuration.EngineConfigurationFactoryFinder;
import org.apache.axis.handlers.HandlerInfoChainFactory;
import org.apache.axis.handlers.soap.MustUnderstandChecker;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsa.AsyncService;
import org.apache.axis.wsa.MIHeader;
import org.apache.axis.wsa.WSAHandler;
import org.apache.commons.logging.Log;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.HandlerChain;

/**
 * Provides the equivalent of an "Axis engine" on the client side.
 * Subclasses hardcode initialization & setup logic for particular
 * client-side transports.
 *
 * @author Rob Jellinghaus (robj@unrealities.com)
 * @author Doug Davis (dug@us.ibm.com)
 * @author Glen Daniels (gdaniels@allaire.com)
 */
public class AxisClient extends AxisEngine {
    protected static Log log =
        LogFactory.getLog(AxisClient.class.getName());

    protected static TaskManager taskManager =
        TaskManagerFactory.getTaskManager();

    MustUnderstandChecker checker     = new MustUnderstandChecker(null);
    HandlerChain          handlerImpl = null ;

    public AxisClient(EngineConfiguration config) {
        super(config);
    }
    
    public AxisClient() {
        this(EngineConfigurationFactoryFinder.newFactory().
                getClientEngineConfig());
    }

    /**
     * @return this instance, as this is the client engine
     */
    public AxisEngine getClientEngine () {
        return this;
    }

    public void invokeOutbound(MessageContext msgContext) throws Exception {
        Handler h = null ;

        /* Process the Service Specific Request Chain */
        /**********************************************/
        SOAPService service = msgContext.getService();
        if ( service != null ) {
            h = service.getRequestHandler();
            if ( h != null )
                h.invoke( msgContext );
        }

        /* Process the Global Request Chain */
        /**********************************/
        if ((h = getGlobalRequest()) != null )
            h.invoke(msgContext);

        /* Process the JAX-RPC Handlers - handleRequest */
        /* Make sure to set the pastPivot to true if this returns a
         * false. In that case we do not invoke the transport request
         * chain. Also note that if a a false was returned from the
         * JAX-RPC handler chain, then the chain still holds the index

         * of the handler that returned false. So when we invoke the
         * handleResponse method of the chain, it will correctly call
         * the handleResponse from that specific handler instance. So
         * do not destroy the chain at this point - the chain will be
         * destroyed in the finally block.
         */
        // invokeJAXRPCHandlers(msgContext);
        if ( handlerImpl == null )
          handlerImpl = getJAXRPChandlerChain(msgContext);
        if (handlerImpl != null ) {
          try {
            if (!handlerImpl.handleRequest(msgContext)) {
              msgContext.setPastPivot(true);
            }
          } catch( RuntimeException re ) {
            handlerImpl.destroy(); // WS4EE 1.1 6.2.2.1 Handler Life Cycle. "RuntimeException" --> destroy handler
            throw re ;
          }
        }

        // Run the security code - Init security sessions if needed
        String secCls = msgContext.getStrProp( "WSSecurity" );
        if ( secCls == null ) 
          secCls = (String) msgContext.getAxisEngine().getOption("WSSecurity");
        // Add code here... Dug
        // securityCode.init();
    }

    public void invokeTransport(MessageContext msgContext) throws Exception {
        /** Process the Transport Specific stuff
         *
         * NOTE: Somewhere in here there is a handler which actually
         * sends the message and receives a response.  Generally
         * this is the pivot point in the Transport chain. But invoke
         * this only if pivot point has not been set to false. This
         * can be set to false if any of the JAX-RPC handler's
         * handleRequest returned false.
         */
        String  hName = msgContext.getTransportName();
        Handler h     = null ;

        if ( hName != null && (h = getTransport( hName )) != null )  {
          // Piggy-back any RM headers (like ACKs)
          // add code here... Dug
          // rmcode.addRMHeaders();

          // Run security - Protect
          // add code here... Dug
          // securityCode.protect();

          // Invoke the actual transport chain
          h.invoke(msgContext);

          // Make sure the first thing we do on the response side is
          // run WSA processing
          WSAHandler.invoke( msgContext );

          // Run security - Verify
          // add code here... Dug
          // securityCode.verify();
        }
        else 
            throw new AxisFault(Messages.getMessage("noTransport00", hName));
    }

    public void invokeTransportOneWay(final MessageContext msgContext) 
      throws Exception
    {
      Runnable runnable = new Runnable() {
        public void run() {
          try {
            msgContext.getAxisEngine().setCurrentMessageContext( msgContext );
            invokeTransport( msgContext );
          }
          catch( Exception exp ) {
            log.debug( Messages.getMessage( "exceptionPrinting" ) , exp );
          }
        }
      };
      if (taskManager == null) {
          (new Thread(runnable)).start();
      } else {
          taskManager.execute(runnable);
      }
    }

    public void invokeInbound(MessageContext msgContext) throws Exception {
        Handler h = null ;

        /* Process the JAXRPC Handlers */
        // invokeJAXRPCHandlers(msgContext);
        if ( handlerImpl == null )
          handlerImpl = getJAXRPChandlerChain(msgContext);
        if (handlerImpl != null ) {
          try {
            if (!handlerImpl.handleResponse(msgContext)) {
              msgContext.setPastPivot(true);
            }
          } catch( RuntimeException re ) {
            handlerImpl.destroy(); // WS4EE 1.1 6.2.2.1 Handler Life Cycle. "RuntimeException" --> destroy handler
            throw re ;
          }
        }

        /* Process the Global Response Chain */
        /*************************************/
        SOAPService service = msgContext.getService();
        if ((h = getGlobalResponse()) != null) 
            h.invoke(msgContext);

        if ( service != null ) {
            h = service.getResponseHandler();
            if ( h != null ) {
                h.invoke(msgContext);
            }
        }
        // Do SOAP Semantics checks here - this needs to be a call
        // to a pluggable object/handler/something
        if (msgContext.isPropertyTrue(Call.CHECK_MUST_UNDERSTAND,
                true)) {
            checker.invoke(msgContext);
        }
    }

    /**
     * Main routine of the AXIS engine.  In short we locate the appropriate
     * handler for the desired service and invoke() it.
     *
     * @param msgContext the <code>MessageContext</code> to invoke relative
     *                   to
     * @throws AxisFault if anything goes wrong during invocation
     */
    public void invoke(MessageContext msgContext) throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug("Enter: AxisClient::invoke");
        }

        String  hName = null ;
        Handler h     = null ;

        // save previous context
        MessageContext previousContext = getCurrentMessageContext();

        try {
            // set active context
            setCurrentMessageContext(msgContext);

            // Do WSA processing first
            WSAHandler.invoke( msgContext );

            hName = msgContext.getStrProp( MessageContext.ENGINE_HANDLER );
            if (log.isDebugEnabled()) {
                log.debug( "EngineHandler: " + hName );
            }

            if ( hName != null ) {
                h = getHandler( hName );
                if ( h != null )
                    h.invoke(msgContext);
                else
                    throw new AxisFault( "Client.error",
                                        Messages.getMessage("noHandler00", 
                                                hName),
                                        null, null );
            }
            else {
                // This really should be in a handler - but we need to discuss 
                // it first - to make sure that's what we want.

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

                msgContext.setPastPivot(false);
                invokeOutbound(msgContext);

                // Add check for RM here - for now just do normal stuff... Dug
                // Normal transport flow
                if ( msgContext.getIsOneWay() )
                  invokeTransportOneWay( msgContext );
                else
                  invokeTransport( msgContext );

                // If there was no response message and we didn't call
                // invokeOneWay() then wait for an async response
                if ( msgContext.getResponseMessage() == null &&
                     msgContext.getIsOneWay() == false )
                  waitForResponse( msgContext );

                if ( !msgContext.getIsOneWay() )
                  invokeInbound( msgContext );

                // Do SOAP Semantics checks here - this needs to be a call to
                // a pluggable object/handler/something
            }

        } catch ( Exception e ) {
            // Should we even bother catching it ?
            if (e instanceof AxisFault)  throw (AxisFault) e ;
            log.debug(Messages.getMessage("exception00"), e);
            throw AxisFault.makeFault(e);

        } finally {
            if (handlerImpl != null) 
                handlerImpl.destroy();
            // restore previous state
            setCurrentMessageContext(previousContext);
        }

        if (log.isDebugEnabled()) {
            log.debug("Exit: AxisClient::invoke");
        }
    }

    private void waitForResponse(MessageContext msgContext) throws Exception {
      MIHeader reqMIH = MIHeader.fromRequest();
      if ( reqMIH == null || reqMIH.getMessageID() == null ) return ;

      // If this is a response msg then we shouldn't wait
      if ( reqMIH.getRelatesTo() != null ) return ;

      // Just wait for the message to appear in the persistence
      long       startTime    = System.currentTimeMillis();
      String     msgID        = reqMIH.getMessageID();
      long       maxWait      = 1000*60 ;
      String     maxWaitStr   = null ;
      
      maxWaitStr = (String) msgContext.getAxisEngine()
                                      .getOption("asyncWaitTimeout");
      if ( maxWaitStr != null && !"".equals(maxWaitStr) )
        maxWait = Long.parseLong(maxWaitStr);

      for ( ;; Thread.sleep(200) ) {
        // Check to see if we've waiting too long, if so stop 
        // ****************************************************
        long currentTime = System.currentTimeMillis();
        if ( maxWait != 0 && startTime + maxWait < currentTime )
               throw new Exception( Messages.getMessage("noResponse01") );

        // First check to see if the message magically appeared in our 
        // persistence.  This could be as a result of an async delivery
        // or because we polled for any generic message.               
        // *************************************************************
        MessageContext mc = AsyncService.getResponseContext(msgID);
        if ( mc == null ) continue ;

        msgContext.setResponseMessage( mc.getCurrentMessage() );
        if ( "true".equals(mc.getProperty("ASYNCRESPONSE")) )
          msgContext.setProperty( "ASYNCRESPONSE", "true" );
        break ;
      }
    }

    /**
     * @param context Stores the Service, port QName and optionnaly a HandlerInfoChainFactory
     * @return Returns a HandlerChain if one has been specified
     */
    protected HandlerChain getJAXRPChandlerChain(MessageContext context) {
    java.util.List chain = null;
    HandlerInfoChainFactory hiChainFactory = null;
    boolean clientSpecified = false;

        Service service
            = (Service)context.getProperty(Call.WSDL_SERVICE);
        if(service == null) {
            return null;
        }

        QName portName = (QName) context.getProperty(Call.WSDL_PORT_NAME);
        if(portName == null) {
            return null;
        }

        javax.xml.rpc.handler.HandlerRegistry registry;
        registry = service.getHandlerRegistry();
        if(registry != null) {
        chain = registry.getHandlerChain(portName);
        if ((chain != null) && (!chain.isEmpty())) {
        hiChainFactory = new HandlerInfoChainFactory(chain);
        clientSpecified = true;
        }
        }

    // Otherwise, use the container support
    if (!clientSpecified) {
        SOAPService soapService = context.getService();
        if (soapService != null) {
        // A client configuration exists for this service.  Check
        // to see if there is a HandlerInfoChain configured on it.
        hiChainFactory = (HandlerInfoChainFactory) 
                soapService.getOption(Constants.ATTR_HANDLERINFOCHAIN);
        }
    }

    if (hiChainFactory == null) {
        return null ;
    }
    return hiChainFactory.createHandlerChain();
  }
}

