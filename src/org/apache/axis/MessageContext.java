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

package org.apache.axis ;

import org.apache.axis.client.AxisClient;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.registries.HandlerRegistry;
import org.apache.axis.session.Session;
import org.apache.axis.utils.AxisClassLoader;
import org.apache.log4j.Category;

import java.util.Hashtable;

/**
 * Some more general docs will go here.
 *
 * This class also contains constants for accessing some
 * well-known properties. Using a hierarchical namespace is
 * strongly suggested in order to lower the chance for
 * conflicts.
 *
 * (These constants should be viewed as an explicit list of well
 *  known and widely used context keys, there's nothing wrong
 *  with directly using the key strings. This is the reason for
 *  the hierarchical constant namespace.
 *
 *  Actually I think we might just list the keys in the docs and
 *  provide no such constants since they create yet another
 *  namespace, but we'd have no compile-time checks then.
 *
 *  Whaddya think? - todo by Jacek)
 *
 *
 * @author Doug Davis (dug@us.ibm.com)
 * @author Jacek Kopecky (jacek@idoox.com)
 */
public class MessageContext {
    static Category category =
            Category.getInstance(MessageContext.class.getName());

    /**
     * The request message.  If we're on the client, this is the outgoing
     * message heading to the server.  If we're on the server, this is the
     * incoming message we've received from the client.
     */
    private Message requestMessage ;

    /**
     * The response message.  If we're on the server, this is the outgoing
     * message heading back to the client.  If we're on the client, this is the
     * incoming message we've received from the server.
     */
    private Message responseMessage ;

    /**
     * That unique key/name that the next router/dispatch handler should use
     * to determine what to do next.
     */
    private String           targetService ;
    
    /**
     * The name of the Transport which this message was received on (or is
     * headed to, for the client).
     */
    private String           transportName;

    /**
     * The default classloader that this service should use
     */
    private AxisClassLoader  classLoader ;
    
    /**
     * The AxisEngine which this context is involved with
     */
    private AxisEngine       axisEngine;
    
    /**
     * A Session associated with this request.
     */
    private Session          session;
    
    /**
     * Should we track session state, or not?
     * default is not.
     * Could potentially refactor this so that
     * maintainSession iff session != null...
     */
    private boolean          maintainSession = false;
    
    /**
     * Are we doing request stuff, or response stuff?
     */
    private boolean          havePassedPivot = false;

    /**
     * Maximum amount of time to wait on a request, in milliseconds.
     */
    private int              timeout = 0;

    /**
     * Storage for an arbitrary bag of properties associated with this
     * MessageContext.
     */
    private Hashtable bag ;

    public MessageContext(AxisEngine engine) {
        this.axisEngine = engine;
    }

    
    /**
     * Mappings of QNames to serializers/deserializers (and therfore
     * to Java types).
     */
    private TypeMappingRegistry mappingRegistry = null;

    public void setTypeMappingRegistry(TypeMappingRegistry reg) {
        mappingRegistry = reg;
    }

    /**
     * Get the currently in-scope type mapping registry.
     *
     * By default, will return a reference to the AxisEngine's TMR until
     * someone sets our local one (usually as a result of setting the
     * serviceHandler).
     *
     * @return the type mapping registry to use for this request.
     */
    public TypeMappingRegistry getTypeMappingRegistry() {
        if (mappingRegistry == null)
            return axisEngine.getTypeMappingRegistry();
        
        return mappingRegistry;
    }

    /**
     * Transport
     */
    public String getTransportName()
    {
        return transportName;
    }
    
    public void setTransportName(String transportName)
    {
        this.transportName = transportName;
    }
    
    /**
     * Sessions
     */
    public Session getSession()
    {
        return session;
    }
    
    public void setSession(Session session)
    {
        this.session = session;
    }
    
    /**
     * Set whether we are maintaining session state
     */
    public void setMaintainSession (boolean yesno) {
        maintainSession = yesno;
    }
    
    /**
     * Are we maintaining session state?
     */
    public boolean getMaintainSession () {
        return maintainSession;
    }

    /**
     * Get the request message.
     *
     * @return the request message (may be null).
     */
    public Message getRequestMessage() {
        return requestMessage ;
    };

    /**
     * Set the request message, and make sure that message is associated
     * with this MessageContext.
     *
     * @param reqMsg the new request Message.
     */
    public void setRequestMessage(Message reqMsg) {
        requestMessage = reqMsg ;
        if (requestMessage != null) requestMessage.setMessageContext(this);
    };

    /**
     * Get the response message.
     *
     * @return the response message (may be null).
     */
    public Message getResponseMessage() { return responseMessage ; }

    /**
     * Set the response message, and make sure that message is associated
     * with this MessageContext.
     *
     * @param reqMsg the new response Message.
     */
    public void setResponseMessage(Message respMsg) {
        responseMessage = respMsg ;
        if (responseMessage != null) responseMessage.setMessageContext(this);
    };
    
    /**
     * Return the current (i.e. request before the pivot, response after)
     * message.
     */
    public Message getCurrentMessage()
    {
        return (havePassedPivot ? responseMessage : requestMessage);
    }
    
    /**
     * Set the current (i.e. request before the pivot, response after)
     * message.
     */
    public void setCurrentMessage(Message curMsg)
    {
        curMsg.setMessageContext(this);

        if (havePassedPivot) {
            responseMessage = curMsg;
        } else {
            requestMessage = curMsg;
        }
    }
    
    /**
     * Determine when we've passed the pivot
     */
    public boolean getPastPivot()
    {
        return havePassedPivot;
    }

    /**
     * Indicate when we've passed the pivot
     */
    public void setPastPivot(boolean pastPivot)
    {
        havePassedPivot = pastPivot;
    }

    /**
     * Set timeout in our MessageContext.
     * 
     * @param value the maximum amount of time, in milliseconds
     */
    public void setTimeout (int value) {
        timeout = value;
    }
    
    /**
     * Get timeout from our MessageContext.
     * 
     * @return value the maximum amount of time, in milliseconds
     */
    public int getTimeout () {
        return timeout;
    }
    
    public AxisClassLoader getClassLoader() {
        if ( classLoader == null )
            classLoader = AxisClassLoader.getClassLoader();
        return( classLoader );
    }

    public void setClassLoader(AxisClassLoader cl ) {
        classLoader = cl ;
    }

    public String getTargetService() {
        return( targetService );
    }
    
    public AxisEngine getAxisEngine()
    {
        return axisEngine;
    }

    /**
     * Set the target service for this message.
     *
     * This looks up the named service in the registry, and has
     * the side effect of setting our TypeMappingRegistry to the
     * service's.
     *
     * @param tServ the name of the target service.
     */
    public void setTargetService(String tServ) {
        category.debug("MessageContext: setTargetService(" + tServ+")");
        targetService = tServ ;

        if (targetService == null)
            setServiceHandler(null);
        else {
            // Do NOT throw an exception if the service handler is not found,
            // since we may be on the client!  -- yow... this is messy. -- RobJ
            try {
                setServiceHandler(getAxisEngine().getService(tServ));
            } catch (AxisFault fault) {
            }
        }
    }

    /** ServiceHandler is the handler that is the "service".  This handler
     * can (and probably will actually be a chain that contains the
     * service specific request/response/pivot point handlers
     */
    private Handler          serviceHandler ;
    public Handler getServiceHandler() {
        return( serviceHandler );
    }
    
    public void setServiceHandler(Handler sh)
    {
        category.debug("MessageContext: setServiceHandler("+sh+")");
        serviceHandler = sh;
        if (sh != null && sh instanceof SOAPService) {
            SOAPService service = (SOAPService)sh;
            TypeMappingRegistry tmr = service.getTypeMappingRegistry();
            setTypeMappingRegistry(tmr);
        }
    }
    
    /**
     * Let us know whether this is the client or the server.
     */
    public boolean isClient()
    {
        return (axisEngine instanceof AxisClient);
    }
    
    /** Contains an instance of Handler, which is the
     *  ServiceContext and the entrypoint of this service.
     *
     *  (if it has been so configured - will our deployment
     *   tool do this by default?  - todo by Jacek)
     */
    public static String ENGINE_HANDLER      = "engine.handler";

    /** This String is the URL that the message came to
     */
    public static String TRANS_URL           = "transport.url";

    /** Has a quit been requested? Hackish... but useful... -- RobJ */
    public static String QUIT_REQUESTED = "quit.requested";
    
    /** A String with the user's ID (if available)
     */
    public static String USERID              = "user.id";

    /** A String with the user's password (if available)
     */
    public static String PASSWORD            = "user.password";

    /** Place to store an AuthenticatedUser */
    public static String AUTHUSER            = "authenticatedUser";

    /** Is this message an RPC message (instead of just a blob of xml) */
    public static String ISRPC               = "is_rpc" ;
  
    /** If on the client - this is the Call object */
    public static String CALL                = "call_object" ;

    /** Just a util so we don't have to cast the result
     */
    public String getStrProp(String propName) {
        return( (String) getProperty(propName) );
    }

    /**
     * Tests to see if the named property is set in the 'bag'.
     * If not there then 'false' is returned.
     * If there, then...
     *   if its a Boolean, we'll return booleanValue()
     *   if its an Integer,  we'll return 'false' if its '0' else 'true'
     *   if its a String, we'll return 'false' if its 'false' or '0' else 'true'
     *   All other types return 'true'
     */
    public boolean isPropertyTrue(String propName) {
        Object val = getProperty(propName);
        if ( val == null ) return( false );
        if ( val instanceof Boolean ) {
            Boolean b = (Boolean) val ;
            return( b.booleanValue() );
        }
        if ( val instanceof Integer ) {
            Integer i = (Integer) val ;
            if ( i.intValue() == 0 ) return( false );
            return( true );
        }
        if ( val instanceof String ) {
            String s = (String) val ;
            if ( s.equalsIgnoreCase("false") ||
                 s.equalsIgnoreCase("no") ) return( false );
            return( true );
        }
        return( true );
    }

    /**
     * Tests to see if the named property is set in the 'bag'.
     * If not there then 'defaultVal' will be returned.
     * If there, then...
     *   if its a Boolean, we'll return booleanValue()
     *   if its an Integer,  we'll return 'false' if its '0' else 'true'
     *   if its a String, we'll return 'false' if its 'false' or '0' else 'true'
     *   All other types return 'true'
     */
    public boolean isPropertyTrue(String propName, boolean defaultVal) {
        Object val = getProperty(propName);
        if ( val == null ) return( defaultVal );
        if ( val instanceof Boolean ) {
            Boolean b = (Boolean) val ;
            return( b.booleanValue() );
        }
        if ( val instanceof Integer ) {
            Integer i = (Integer) val ;
            if ( i.intValue() == 0 ) return( false );
            return( true );
        }
        if ( val instanceof String ) {
            String s = (String) val ;
            if ( s.equalsIgnoreCase("false") ||
                 s.equalsIgnoreCase("no") ) return( false );
            return( true );
        }
        return( true );
    }

    public Object getProperty(String propName) {
        if ( bag == null ) return( null );
        return( bag.get(propName) );
    }

    public void setProperty(String propName, Object propValue) {
        if (propValue == null) return;
        if ( bag == null ) bag = new Hashtable() ;
        bag.put( propName, propValue );
    }
    
    public void clearProperty(String propName)
    {
        if (bag != null) {
            bag.remove(propName);
        }
    }
    
    public void reset()
    {
        if (bag != null) {
            bag.clear();
        }
        serviceHandler = null;
        havePassedPivot = false;
    }
};
