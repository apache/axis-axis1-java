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

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.client.AxisClient;
import org.apache.axis.configuration.DefaultEngineConfigurationFactory;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.JavaUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
/**
 *
 * @author Doug Davis (dug@us.ibm.com)
 * @author Glen Daniels (gdaniels@allaire.com)
 */
public class AxisServer extends AxisEngine
{
    protected static Log log =
        LogFactory.getLog(AxisServer.class.getName());

    private static AxisServerFactory factory = null;
    
    public static AxisServer getServer(Map environment) throws AxisFault
    {
        if (factory == null) {
            String factoryClassName = getGlobalProperty("axis.ServerFactory");
            if (factoryClassName != null) {
                try {
                    Class factoryClass = ClassUtils.forName(factoryClassName);
                    if (AxisServerFactory.class.isAssignableFrom(factoryClass))
                        factory = (AxisServerFactory)factoryClass.newInstance();
                } catch (Exception e) {
                    // If something goes wrong here, should we just fall
                    // through and use the default one?
                    log.error(JavaUtils.getMessage("exception00"), e);
                }
            }
            
            if (factory == null) {
                factory = new DefaultAxisServerFactory();
            }
        }
        
        return factory.getServer(environment);                
    }

    /**
     * the AxisClient to be used by outcalling Services
     */
    private AxisEngine clientEngine;

    public AxisServer()
    {
        this((new DefaultEngineConfigurationFactory()).getServerEngineConfig());
    }

    public AxisServer(EngineConfiguration config)
    {
        super(config);
        // Server defaults to persisting configuration
        setShouldSaveConfig(true);
    }

    /** Is this server active?  If this is false, any requests will
     * cause a SOAP Server fault to be generated.
     */
    private boolean running = true;

    public boolean isRunning() { return running; }

    /** Start the server.
     */
    public void start()
    {
        // re-init...
        init();
        running = true;
    }

    /** Stop the server.
     */
    public void stop()
    {
        running = false;
    }

    /**
     * Get this server's client engine.  Create it if it does
     * not yet exist.
     */
    public synchronized AxisEngine getClientEngine () {
        if (clientEngine == null) {
            clientEngine = new AxisClient(); // !!!!
        }
        return clientEngine;
    }

    /**
     * Main routine of the AXIS server.  In short we locate the appropriate
     * handler for the desired service and invoke() it.
     */
    public void invoke(MessageContext msgContext) throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug("Enter: AxisServer::invoke");
        }

        if (!isRunning()) {
            throw new AxisFault("Server.disabled",
                                JavaUtils.getMessage("serverDisabled00"),
                                null, null);
        }

        String  hName = null ;
        Handler h     = null ;

        // save previous context
        MessageContext previousContext = getCurrentMessageContext();

        try {
            // set active context
            setCurrentMessageContext(msgContext);

            hName = msgContext.getStrProp( MessageContext.ENGINE_HANDLER );
            if ( hName != null ) {
                if ( (h = getHandler(hName)) == null ) {
                    ClassLoader cl = msgContext.getClassLoader();
                    try {
                        log.debug( JavaUtils.getMessage("tryingLoad00", hName) );
                        Class cls = ClassUtils.forName(hName, true, cl);
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
                                         JavaUtils.getMessage("noHandler00", hName),
                                         null, null );
            }
            else {
                // This really should be in a handler - but we need to discuss it
                // first - to make sure that's what we want.
                /* Now we do the 'real' work.  The flow is basically:         */
                /*   Transport Specific Request Handler/Chain                   */
                /*   Global Request Handler/Chain                               */
                /*   Protocol Specific-Handler(ie. SOAP, XP)                  */
                /*     ie. For SOAP Handler:                                  */
                /*           - Service Specific Request Handler/Chain           */
                /*           - SOAP Semantic Checks                           */
                /*           - Service Specific Response Handler/Chain          */
                /*   Global Response Handler/Chain                              */
                /*   Transport Specific Response Handler/Chain                  */
                /**************************************************************/

                // When do we call init/cleanup??
                if (log.isDebugEnabled()) {
                    log.debug(JavaUtils.getMessage("defaultLogic00") );
                }

                /*  This is what the entirety of this logic might evolve to:

                hName = msgContext.getStrProp(MessageContext.TRANSPORT);
                if ( hName != null ) {
                if ((h = hr.find( hName )) != null ) {
                h.invoke(msgContext);
                } else {
                log.error(JavaUtils.getMessage("noTransport02", hName));
                }
                } else {
                // No transport set, so use the default (probably just
                // calls the global->service handlers)
                defaultTransport.invoke(msgContext);
                }

                */

                /* Process the Transport Specific Request Chain */
                /**********************************************/
                hName = msgContext.getTransportName();
                SimpleTargetedChain transportChain = null;

                if (log.isDebugEnabled())
                    log.debug(JavaUtils.getMessage("transport01", "AxisServer.invoke", hName));

                if ( hName != null && (h = getTransport( hName )) != null ) {
                    if (h instanceof SimpleTargetedChain) {
                        transportChain = (SimpleTargetedChain)h;
                        h = transportChain.getRequestHandler();
                        if (h != null)
                            h.invoke(msgContext);
                    }
                }

                /* Process the Global Request Chain */
                /**********************************/
                if ((h = getGlobalRequest()) != null )
                    h.invoke(msgContext);

                /**
                 * At this point, the service should have been set by someone
                 * (either the originator of the MessageContext, or one of the
                 * transport or global Handlers).  If it hasn't been set, we
                 * fault.
                 */
                h = msgContext.getService();
                if (h == null) {
                    // It's possible that we haven't yet parsed the
                    // message at this point.  This is a kludge to
                    // make sure we have.  There probably wants to be
                    // some kind of declarative "parse point" on the handler
                    // chain instead....
                    Message rm = msgContext.getRequestMessage();
                    rm.getSOAPEnvelope().getFirstBody();
                    h = msgContext.getService();
                    if (h == null)
                        throw new AxisFault("Server.NoService",
                                            JavaUtils.getMessage("noService05",
                                                                 "" + msgContext.getTargetService()),
                                            null, null );
                }

                h.invoke(msgContext);

                /* Process the Global Response Chain */
                /***********************************/
                if ((h = getGlobalResponse()) != null)
                    h.invoke(msgContext);

                /* Process the Transport Specific Response Chain */
                /***********************************************/
                if (transportChain != null) {
                    h = transportChain.getResponseHandler();
                    if (h != null)
                        h.invoke(msgContext);
                }
            }
        } catch (AxisFault e) {
            throw e;
        } catch (Exception e) {
            // Should we even bother catching it ?
            throw AxisFault.makeFault(e);

        } finally {
            // restore previous state
            setCurrentMessageContext(previousContext);
        }
        
        if (log.isDebugEnabled()) {
            log.debug("Exit: AxisServer::invoke");
        }
    }

    /**
     *
     */
    public void generateWSDL(MessageContext msgContext) throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug("Enter: AxisServer::generateWSDL");
        }

        if (!isRunning()) {
            throw new AxisFault("Server.disabled",
                                JavaUtils.getMessage("serverDisabled00"),
                                null, null);
        }

        String  hName = null ;
        Handler h     = null ;

        // save previous context
        MessageContext previousContext = getCurrentMessageContext();

        try {
            // set active context
            setCurrentMessageContext(msgContext);

            hName = msgContext.getStrProp( MessageContext.ENGINE_HANDLER );
            if ( hName != null ) {
                if ( (h = getHandler(hName)) == null ) {
                    ClassLoader cl = msgContext.getClassLoader();
                    try {
                        log.debug( JavaUtils.getMessage("tryingLoad00", hName) );
                        Class cls = ClassUtils.forName(hName, true, cl);
                        h = (Handler) cls.newInstance();
                    }
                    catch( Exception e ) {
                        throw new AxisFault(
                                "Server.error",
                                JavaUtils.getMessage("noHandler00", hName),
                                null, null );
                    }
                }
                h.generateWSDL(msgContext);
            }
            else {
                // This really should be in a handler - but we need to discuss it
                // first - to make sure that's what we want.
                /* Now we do the 'real' work.  The flow is basically:         */
                /*   Transport Specific Request Handler/Chain                   */
                /*   Global Request Handler/Chain                               */
                /*   Protocol Specific-Handler(ie. SOAP, XP)                  */
                /*     ie. For SOAP Handler:                                  */
                /*           - Service Specific Request Handler/Chain           */
                /*           - SOAP Semantic Checks                           */
                /*           - Service Specific Response Handler/Chain          */
                /*   Global Response Handler/Chain                              */
                /*   Transport Specific Response Handler/Chain                  */
                /**************************************************************/

                // When do we call init/cleanup??
                log.debug( JavaUtils.getMessage("defaultLogic00") );

                /*  This is what the entirety of this logic might evolve to:

                hName = msgContext.getStrProp(MessageContext.TRANSPORT);
                if ( hName != null ) {
                if ((h = hr.find( hName )) != null ) {
                h.generateWSDL(msgContext);
                } else {
                log.error(JavaUtils.getMessage("noTransport02", hName));
                }
                } else {
                // No transport set, so use the default (probably just
                // calls the global->service handlers)
                defaultTransport.generateWSDL(msgContext);
                }

                */

                /* Process the Transport Specific Request Chain */
                /**********************************************/
                hName = msgContext.getTransportName();
                SimpleTargetedChain transportChain = null;

                if (log.isDebugEnabled())
                    log.debug(JavaUtils.getMessage("transport01",
                                                   "AxisServer.generateWSDL",
                                                   hName));
                if ( hName != null && (h = getTransport( hName )) != null ) {
                    if (h instanceof SimpleTargetedChain) {
                        transportChain = (SimpleTargetedChain)h;
                        h = transportChain.getRequestHandler();
                        if (h != null)
                            h.generateWSDL(msgContext);
                    }
                }

                /* Process the Global Request Chain */
                /**********************************/
                if ((h = getGlobalRequest()) != null )
                    h.generateWSDL(msgContext);

                /**
                 * At this point, the service should have been set by someone
                 * (either the originator of the MessageContext, or one of the
                 * transport or global Handlers).  If it hasn't been set, we
                 * fault.
                 */
                h = msgContext.getService();
                if (h == null) {
                    // It's possible that we haven't yet parsed the
                    // message at this point.  This is a kludge to
                    // make sure we have.  There probably wants to be
                    // some kind of declarative "parse point" on the handler
                    // chain instead....
                    Message rm = msgContext.getRequestMessage();
                    if (rm != null) {
                        rm.getSOAPEnvelope().getFirstBody();
                        h = msgContext.getService();
                    }
                    if (h == null)
                        throw new AxisFault("Server.NoService",
                                            JavaUtils.getMessage("noService05",
                                                                 "" + msgContext.getTargetService()),
                                            null, null );
                }

                h.generateWSDL(msgContext);

                /* Process the Global Response Chain */
                /***********************************/
                if ((h = getGlobalResponse()) != null )
                    h.generateWSDL(msgContext);

                /* Process the Transport Specific Response Chain */
                /***********************************************/
                if (transportChain != null) {
                    h = transportChain.getResponseHandler();
                    if (h != null)
                        h.generateWSDL(msgContext);
                }
            }
        } catch (AxisFault e) {
            throw e;
        } catch(Exception e) {
            // Should we even bother catching it ?
            throw AxisFault.makeFault(e);
        } finally {
            // restore previous state
            setCurrentMessageContext(previousContext);
        }

        if (log.isDebugEnabled()) {
            log.debug("Exit: AxisServer::generateWSDL");
        }
    }
}
