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

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.HandlerChain;

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.configuration.EngineConfigurationFactoryFinder;
import org.apache.axis.handlers.HandlerInfoChainFactory;
import org.apache.axis.handlers.soap.MustUnderstandChecker;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

/**
 * Provides the equivalent of an "Axis engine" on the client side.
 * Subclasses hardcode initialization &amp; setup logic for particular
 * client-side transports.
 *
 * @author Rob Jellinghaus (robj@unrealities.com)
 * @author Doug Davis (dug@us.ibm.com)
 * @author Glen Daniels (gdaniels@allaire.com)
 */
public class AxisClient extends AxisEngine {
    protected static Log log =
            LogFactory.getLog(AxisClient.class.getName());

    MustUnderstandChecker checker = new MustUnderstandChecker(null);

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
        String hName = null;
        Handler h = null;
        HandlerChain handlerImpl = null;
        
        // save previous context
        MessageContext previousContext = getCurrentMessageContext();
        try {
            // set active context
            setCurrentMessageContext(msgContext);
            hName = msgContext.getStrProp(MessageContext.ENGINE_HANDLER);
            if (log.isDebugEnabled()) {
                log.debug("EngineHandler: " + hName);
            }
            if (hName != null) {
                h = getHandler(hName);
                if (h != null)
                    h.invoke(msgContext);
                else
                    throw new AxisFault("Client.error",
                            Messages.getMessage("noHandler00",
                                    hName),
                            null, null);
            } else {
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
                SOAPService service = null;
                msgContext.setPastPivot(false);

                /* Process the Service Specific Request Chain */
                /**********************************************/
                service = msgContext.getService();
                if (service != null) {
                    h = service.getRequestHandler();
                    if (h != null)
                        h.invoke(msgContext);
                }

                /* Process the Global Request Chain */
                /**********************************/
                if ((h = getGlobalRequest()) != null)
                    h.invoke(msgContext);

                /* Process the JAX-RPC Handlers  - handleRequest.
                 * Make sure to set the pastPivot to true if this returns a
                 * false. In that case we do not invoke the transport request
                 * chain. Also note that if a a false was returned from the
                 * JAX-RPC handler chain, then the chain still holds the index
                 * of the handler that returned false. So when we invoke the
                 * handleResponse method of the chain, it will correctly call
                 * the handleResponse from that specific handler instance. So
                 * do not destroy the chain at this point - the chain will be
                 * destroyed in the finally block.
                 */
                handlerImpl = getJAXRPChandlerChain(msgContext);
                if (handlerImpl != null) {
                    try {
                        if (!handlerImpl.handleRequest(msgContext)) {
                            msgContext.setPastPivot(true);
                        }
                    } catch (RuntimeException re) {
                        handlerImpl.destroy();  // WS4EE 1.1 6.2.2.1 Handler Life Cycle. "RuntimeException" --> destroy handler
                        throw re;
                    }
                }

                /** Process the Transport Specific stuff
                 *
                 * NOTE: Somewhere in here there is a handler which actually
                 * sends the message and receives a response.  Generally
                 * this is the pivot point in the Transport chain. But invoke
                 * this only if pivot point has not been set to false. This
                 * can be set to false if any of the JAX-RPC handler's
                 * handleRequest returned false.
                 */
                if (!msgContext.getPastPivot()) {
                    hName = msgContext.getTransportName();
                    if (hName != null && (h = getTransport(hName)) != null) {
                        try {
                            h.invoke(msgContext);
                        } catch (AxisFault e) {
                            throw e;
                        }
                    } else {
                        throw new AxisFault(Messages.getMessage("noTransport00",
                                hName));
                    }
                }
                
                msgContext.setPastPivot(true);
                if (!msgContext.isPropertyTrue(Call.ONE_WAY)) {
                    if ((handlerImpl != null) &&
                            !msgContext.isPropertyTrue(Call.ONE_WAY)) {
                        try {
                            handlerImpl.handleResponse(msgContext);                            
                        } catch (RuntimeException ex) {
                            handlerImpl.destroy();  // WS4EE 1.1 6.2.2.1 Handler Life Cycle. "RuntimeException" --> destroy handler
                            throw ex;    
                        }                        
                    }

                    /* Process the Global Response Chain */
                    /***********************************/
                    if ((h = getGlobalResponse()) != null) {
                        h.invoke(msgContext);
                    }
                    
                    /* Process the Service-Specific Response Chain */
                    /***********************************************/
                    if (service != null) {
                        h = service.getResponseHandler();
                        if (h != null) {
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
            }
        } catch (Exception e) {
            // Should we even bother catching it ?
            if (e instanceof AxisFault) {
                throw (AxisFault) e;
            } else {
                log.debug(Messages.getMessage("exception00"), e);
                throw AxisFault.makeFault(e);
            }
        } finally {
            if (handlerImpl != null) {
                handlerImpl.destroy();
            }
            // restore previous state
            setCurrentMessageContext(previousContext);
        }
        if (log.isDebugEnabled()) {
            log.debug("Exit: AxisClient::invoke");
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

        Service service = (Service) context.getProperty(Call.WSDL_SERVICE);
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
            return null;
        }

        return hiChainFactory.createHandlerChain();
    }

}
