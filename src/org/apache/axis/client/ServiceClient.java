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

package org.apache.axis.client ;

import java.util.* ;
import java.net.*;
import org.apache.axis.encoding.ServiceDescription;
import org.apache.axis.message.*;
import org.apache.axis.handlers.* ;
import org.apache.axis.utils.* ;
import org.apache.axis.* ;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.transport.http.HTTPSender;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.encoding.DeserializerFactory;
import org.apache.axis.registries.HandlerRegistry;
import org.apache.axis.transport.http.HTTPTransport;

import org.w3c.dom.* ;

import java.io.*;
import org.apache.axis.encoding.SerializationContext;

/**
 * Allows an Axis service to be invoked from the client side.
 * Contains message and session state which may be reused
 * across multiple invocations on the same ServiceClient.
 *
 * @author Rob Jellinghaus (robj@unrealities.com)
 * @author Doug Davis (dug@us.ibm.com)
 * @author Glen Daniels (gdaniels@macromedia.com)
 */


// Need to add proxy, ssl.... other cool things - but it's a start
// Only supports String

public class ServiceClient {
    private static final boolean DEBUG_LOG = false;

    /***************************************************************
     * Static stuff
     */
    
    public static final String TRANSPORT_PROPERTY =
                                              "java.protocol.handler.pkgs";

    private static Hashtable transports = new Hashtable();
    private static boolean initialized = false;
    
                                                        
    /** Register a Transport that should be used for URLs of the specified
     * protocol.
     * 
     * @param protocol the URL protocol (i.e. "tcp" for "tcp://" urls)
     * @param transport a Transport object which will be used for matching
     *        URLs.
     */
    public static void setTransportForProtocol(String protocol,
                                               Transport transport)
    {
        transports.put(protocol, transport);
    }
    
    /**
     * Set up the default transport URL mappings.
     * 
     * This must be called BEFORE doing non-standard URL parsing (i.e. if you
     * want the system to accept a "local:" URL).  This is why the Options class
     * calls it before parsing the command-line URL argument.
     */
    public static synchronized void initialize()
    {
      if (!initialized) {
        addTransportPackage("org.apache.axis.transport");
        
        setTransportForProtocol("local", new org.apache.axis.transport.local.LocalTransport());
        setTransportForProtocol("http", new HTTPTransport());
        
        initialized = true;
      }
    }

    /** Add a package to the system protocol handler search path.  This
     * enables users to create their own URLStreamHandler classes, and thus
     * allow custom protocols to be used in Axis (typically on the client
     * command line).
     * 
     * For instance, if you add "samples.transport" to the packages property,
     * and have a class samples.transport.tcp.Handler, the system will be able
     * to parse URLs of the form "tcp://host:port..."
     * 
     * @param packageName the package in which to search for protocol names.
     */
    public static synchronized void addTransportPackage(String packageName)
    {
        String currentPackages = System.getProperty(TRANSPORT_PROPERTY);
        if (currentPackages == null) {
          currentPackages = "";
        } else {
          currentPackages += "|";
        }
        currentPackages += packageName;
        
        System.setProperty(TRANSPORT_PROPERTY, currentPackages);
    }
    
    /*****************************************************************************
     * END STATICS
     */
    
    // Our AxisClient
    private AxisEngine engine;
    
    // The description of our service
    private ServiceDescription serviceDesc;
    
    // The message context we use across invocations
    private MessageContext msgContext;
    
    // Our Transport, if any
    private Transport transport;

    /**
     * Basic, no-argument constructor.
     */
    public ServiceClient () {
        this(new AxisClient());
    }
    
    /**
     * Construct a ServiceClient with just an AxisEngine.
     */
    public ServiceClient (AxisEngine engine) {
        this.engine = engine;
        msgContext = new MessageContext(engine);
        if (!initialized)
          initialize();
    }
        
    
    /**
     * Construct a ServiceClient with a given endpoint URL
     * 
     * @param endpointURL a string containing the transport endpoint for this
     *                    service.
     */
    public ServiceClient(String endpointURL)
    {
        this(endpointURL, new AxisClient());
    }
    
    /**
     * Construct a ServiceClient with a given endpoint URL & engine
     */
    public ServiceClient(String endpointURL, AxisEngine engine)
    {
        this(engine);
        this.setURL(endpointURL);
    }
    
    /**
     * Construct a ServiceClient with the given Transport.
     * 
     * @param transport a pre-constructed Transport object which will be used
     *                  to set up the MessageContext appropriately for each
     *                  request
     */
    public ServiceClient (Transport transport) {
        this(transport, new AxisClient());
    }
    
    /**
     * Construct a ServiceClient with the given Transport & engine.
     */
    public ServiceClient (Transport transport, AxisEngine engine) {
        this(engine);
        setTransport(transport);
    }
    
    /**
     * Set the Transport for this ServiceClient.
     * 
     * @param transport the Transport object we'll use to set up
     *                  MessageContext properties.
     */
    public void setTransport(Transport transport) {
        this.transport = transport;
        Debug.Print(1, "Transport is " + transport);
    }
    
    /**
     * Set the URL (and the transport state).
     */
    public void setURL (String endpointURL)
    {
        try {
            URL url = new URL(endpointURL);
            String protocol = url.getProtocol();
            setTransport(getTransportForProtocol(protocol));
            set(MessageContext.TRANS_URL, endpointURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
 /** Get the Transport registered for the given protocol.
     * 
     * @param protocol a protocol such as "http" or "local" which may
     *                 have a Transport object associated with it.
     * @return the Transport registered for this protocol, or null if none.
     */
    public Transport getTransportForProtocol(String protocol)
    {
      return (Transport)transports.get(protocol);
    }
    
    /**
     * Set property in our MessageContext.
     * 
     * @param name the property name to set.
     * @param value the value of the property.
     */
    public void set (String name, Object value) {
        msgContext.setProperty(name, value);
    }
    
    /**
     * Get a property from our MessageContext.
     * 
     * @param name the property name to retrieve.
     * @return the property's value.
     */
    public Object get (String name) {
        return msgContext.getProperty(name);
    }

    /**
     * Set timeout in our MessageContext.
     * 
     * @param value the maximum amount of time, in milliseconds
     */
    public void setTimeout (int value) {
        msgContext.setTimeout(value);
    }
    
    /**
     * Get timeout from our MessageContext.
     * 
     * @return value the maximum amount of time, in milliseconds
     */
    public int getTimeout () {
        return msgContext.getTimeout();
    }
    
    /**
     * Directly set the request message in our MessageContext.
     * 
     * This allows custom message creation.
     * 
     * @param msg the new request message.
     */
    public void setRequestMessage(Message msg) {
        msgContext.setRequestMessage(msg);
    }
    
    /**
     * Determine whether we'd like to track sessions or not.
     * 
     * This just passes through the value into the MessageContext.
     * 
     * @param yesno true if session state is desired, false if not.
     */
    public void setMaintainSession (boolean yesno) {
        msgContext.setMaintainSession(yesno);
    }
    
    /**
     * Obtain a reference to our MessageContext.
     * 
     * @return the ServiceClient's MessageContext.
     */
    public MessageContext getMessageContext () {
        return msgContext;
    }
    
    /**
     * Set the ServiceDescription associated with this ServiceClient.
     * 
     * @param serviceDesc a ServiceDescription.
     */
    public void setServiceDescription(ServiceDescription serviceDesc)
    {
        this.serviceDesc = serviceDesc;
    }
    
    /**
     * Map a type for serialization.
     * 
     * @param _class the Java class of the data type.
     * @param qName the xsi:type QName of the associated XML type.
     * @param serializer a Serializer which will be used to write the XML.
     */
    public void addSerializer(Class _class, QName qName, Serializer serializer) {
        TypeMappingRegistry typeMap = msgContext.getTypeMappingRegistry();
        typeMap.addSerializer(_class, qName, serializer);
    }
    
    /**
     * Map a type for deserialization.
     * 
     * @param qName the xsi:type QName of an XML Schema type.
     * @param _class the class of the associated Java data type.
     * @param deserializerFactory a factory which can create deserializer
     *                            instances for this type.
     */
    public void addDeserializerFactory(QName qName, Class _class,
                                       DeserializerFactory deserializerFactory) {
        TypeMappingRegistry typeMap = msgContext.getTypeMappingRegistry();
        typeMap.addDeserializerFactory(qName, _class, deserializerFactory);
    }

    /************************************************
     * Invocation
     */
    
    /** Invoke the service with a custom SOAPEnvelope.
     * 
     * @param env a SOAPEnvelope to send.
     * @exception AxisFault
     */
    public SOAPEnvelope invoke(SOAPEnvelope env) throws AxisFault
    {
        msgContext.reset();
        msgContext.setRequestMessage(new Message(env));
        invoke();
        return msgContext.getResponseMessage().getAsSOAPEnvelope();
    }
    
    /** Invoke an RPC service with a method name and arguments.
     * 
     * This will call the service, serializing all the arguments, and
     * then deserialize the return value.
     * 
     * @param namespace the desired namespace URI of the method element
     * @param method the method name
     * @param args an array of Objects representing the arguments to the
     *             invoked method.  If any of these objects are RPCParams,
     *             Axis will use the embedded name of the RPCParam as the
     *             name of the parameter.  Otherwise, we will serialize
     *             each argument as an XML element called "arg<n>".
     * @return a deserialized Java Object containing the return value
     * @exception AxisFault
     */
    public Object invoke( String namespace, String method, Object[] args ) throws AxisFault {
        Debug.Print( 1, "Enter: ServiceClient::invoke(ns, meth, args)" );
        RPCElement  body = new RPCElement(namespace, method, args, serviceDesc);
        Object ret = invoke( body );
        Debug.Print( 1, "Exit: ServiceClient::invoke(ns, meth, args)" );
        return ret;
    }
    
    /** Invoke an RPC service with a pre-constructed RPCElement.
     * 
     * @param body an RPCElement containing all the information about
     *             this call.
     * @return a deserialized Java Object containing the return value
     * @exception AxisFault
     */
    public Object invoke( RPCElement body ) throws AxisFault {
        Debug.Print( 1, "Enter: ServiceClient::invoke(RPCElement)" );
        SOAPEnvelope         reqEnv = new SOAPEnvelope();
        SOAPEnvelope         resEnv = null ;
        Message              reqMsg = new Message( reqEnv );
        Message              resMsg = null ;
        Vector               resArgs = null ;
        Object               result = null ;
        
        String uri = null;
        if (serviceDesc != null) uri = serviceDesc.getEncodingStyleURI();
        if (uri != null) reqEnv.setEncodingStyleURI(uri);
        
        msgContext.setRequestMessage(reqMsg);
        msgContext.setServiceDescription(this.serviceDesc);
        
        reqEnv.addBodyElement(body);
        reqEnv.setMessageType(ServiceDescription.REQUEST);
        
        if ( body.getPrefix() == null )       body.setPrefix( "m" );
        if ( body.getNamespaceURI() == null ) {
            throw new AxisFault("ServiceClient.invoke", "Cannot invoke ServiceClient with null namespace URI for method "+body.getMethodName(),
                                null, null);
        } else if (msgContext.getServiceHandler() == null) {
            msgContext.setTargetService(body.getNamespaceURI());
        }
       
        
        if (DEBUG_LOG) {
            try {
                SerializationContext ctx = new SerializationContext(new PrintWriter(System.out), msgContext);
                System.out.println("");
                System.out.println("**DEBUG**");
                reqEnv.output(ctx);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            } finally {
                System.out.println("");
                System.out.println("**DEBUG**");
                System.out.println("");
            }
        }
        
        try {
            invoke();
        }
        catch( Exception e ) {
            Debug.Print( 1, e );
            if ( !(e instanceof AxisFault ) ) e = new AxisFault( e );
            throw (AxisFault) e ;
        }
        
        resMsg = msgContext.getResponseMessage();
        
        if (resMsg == null)
            throw new AxisFault(new Exception("Null response message!"));
        
        /** This must happen before deserialization...
         */
        resMsg.setMessageType(ServiceDescription.RESPONSE);
        
        resEnv = (SOAPEnvelope)resMsg.getAsSOAPEnvelope();

        SOAPBodyElement respBody = resEnv.getFirstBody();
        if (respBody instanceof SOAPFaultElement) {
            throw ((SOAPFaultElement)respBody).getAxisFault();
        }
        
        body = (RPCElement)resEnv.getFirstBody();
        resArgs = body.getParams();
        
        if (resArgs != null && resArgs.size() > 0) {
            RPCParam param = (RPCParam)resArgs.get(0);
            result = param.getValue();
        }
        
        Debug.Print( 1, "Exit: ServiceClient::invoke(RPCElement)" );
        return( result );
    }
    
    /**
     * Invoke this ServiceClient with its established MessageContext
     * (perhaps because you called this.setRequestMessage())
     * 
     * @exception AxisFault
     */
    public void invoke() throws AxisFault {
        Debug.Print( 1, "Enter: Service::invoke()" );
        
        msgContext.setServiceDescription(serviceDesc);

        // set up message context if there is a transport
        if (transport != null) {
            transport.setupMessageContext(msgContext, this, this.engine);
        }
        
        try {
            engine.invoke( msgContext );
        }
        catch( AxisFault fault ) {
            Debug.Print( 1,  fault );
            throw fault ;
        }
        
        Debug.Print( 1, "Exit: Service::invoke()" );
    }
    
}

