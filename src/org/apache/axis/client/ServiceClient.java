



















// PLEASE DO NOT ADD MORE METHODS TO THIS FILE - IT IS GOING AWAY
// ADD NEW METHODS/LOGIC TO Call.java INSTEAD !!!!































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

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.encoding.DeserializerFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.ServiceDescription;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPFaultElement;
import org.apache.axis.message.SOAPHeader;
import org.apache.axis.transport.http.HTTPTransport;
import org.apache.axis.utils.QName;
import org.apache.log4j.Category;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

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
    static Category category =
            Category.getInstance(ServiceClient.class.getName());

    /***************************************************************
     * Static stuff
     */

    public static final String TRANSPORT_PROPERTY =
            "java.protocol.handler.pkgs";

    /**
     * A Hashtable mapping protocols (Strings) to Transports (classes)
     */
    private static Hashtable transports = new Hashtable();
    private static boolean initialized = false;

    private static FileProvider configProvider = new FileProvider("client-config.xml");

    /** Register a Transport that should be used for URLs of the specified
     * protocol.
     *
     * @param protocol the URL protocol (i.e. "tcp" for "tcp://" urls)
     * @param transportClass the class of a Transport type which will be used
     *                       for matching URLs.
     */
    public static void setTransportForProtocol(String protocol,
                                               Class transportClass)
    {
        if (Transport.class.isAssignableFrom(transportClass))
            transports.put(protocol, transportClass);
        else
            throw new NullPointerException();
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

            setTransportForProtocol("local", org.apache.axis.transport.local.LocalTransport.class);
            setTransportForProtocol("http", HTTPTransport.class);
            setTransportForProtocol("https", HTTPTransport.class);

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

    // Collection of properties to store and put in MessageContext at
    // invoke() time
    private Hashtable myProperties = null;

    private int timeout;
    private boolean maintainSession;

    // Our Transport, if any
    private Transport transport;
    private String    transportName;

    // A place to store output parameters
    private Vector outParams = null;

    // A place to store any client-specified headers
    private Vector myHeaders = null;

    /**
     * Basic, no-argument constructor.
     */
    public ServiceClient () {
        this(new AxisClient(configProvider));
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
            throws AxisFault
    {
        this(endpointURL, new AxisClient(configProvider));
    }

    /**
     * Construct a ServiceClient with a given endpoint URL & engine
     */
    public ServiceClient(String endpointURL, AxisEngine engine)
            throws AxisFault
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
        this(transport, new AxisClient(configProvider));
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
        if (category.isInfoEnabled())
            category.info("Transport is " + transport);
    }

    /**
     * Set the URL (and the transport state).
     */
    public void setURL (String endpointURL)
            throws AxisFault
    {
        try {
            URL url = new URL(endpointURL);
            String protocol = url.getProtocol();
            Transport transport = getTransportForProtocol(protocol);
            if (transport == null)
                throw new AxisFault("ServiceClient.setURL",
                        "No transport mapping for protocol: " + protocol,
                        null, null);
            transport.setUrl(endpointURL);
            setTransport(transport);
        } catch (MalformedURLException e) {
            throw new AxisFault("ServiceClient.setURL",
                    "Malformed URL Exception: " + e.getMessage(), null, null);
        }
    }

    /**
     * Returns the URL of the transport
     */
    public String getURL() {
      if ( transport == null ) return( null );
      return( transport.getUrl() );
    }

    /**
     * Set the name of the transport chain to use.
     */
    public void setTransportName(String name) {
        transportName = name ;
        if ( transport != null )
            transport.setTransportName( name );
    }

    /** Get the Transport registered for the given protocol.
     *
     * @param protocol a protocol such as "http" or "local" which may
     *                 have a Transport object associated with it.
     * @return the Transport registered for this protocol, or null if none.
     */
    public Transport getTransportForProtocol(String protocol)
    {
        Class transportClass = (Class)transports.get(protocol);
        Transport ret = null;
        if (transportClass != null) {
            try {
                ret = (Transport)transportClass.newInstance();
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            }
        }
        return ret;
    }

    /**
     * Set a property which should be carried through our MessageContexts
     * into the engine.
     *
     * @param name the property name to set.
     * @param value the value of the property.
     */
    public void set (String name, Object value) {
        if (name == null || value == null)
            return;
        if (myProperties == null) {
            myProperties = new Hashtable();
        }
        myProperties.put(name, value);
    }

    /**
     * Get a property from our list of persistent ones.
     *
     * @param name the property name to retrieve.
     * @return the property's value.
     */
    public Object get (String name) {
        return (name == null || myProperties == null) ? null :
                                                        myProperties.get(name);
    }

    /**
     * Removes the named property from our list of persistent ones.
     *
     * @param name the property name to remove
     */
    public void remove(String name) {
        if ( name == null || myProperties == null ) return ;
        myProperties.remove( name );
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
     * Directly get the response message in our MessageContext.
     *
     * Shortcut for having to go thru the msgContext
     *
     * @return the response Message object in the msgContext
     */
    public Message getResponseMessage() {
        return msgContext.getResponseMessage();
    }

    /**
     * Determine whether we'd like to track sessions or not.
     *
     * This just passes through the value into the MessageContext.
     *
     * @param yesno true if session state is desired, false if not.
     */
    public void setMaintainSession (boolean yesno) {
        maintainSession = yesno;
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
     * Get the output parameters (if any) from the last invocation.
     *
     * NOTE that the params returned are all RPCParams, containing
     * name and value - if you want the value, you'll need to call
     * param.getValue().
     *
     * @return a Vector of RPCParams
     */
    public Vector getOutputParams()
    {
        return this.outParams;
    }

    /**
     * Add a header which should be inserted into each outgoing message
     * we generate.
     *
     * @param header a SOAPHeader to be inserted into messages
     */
    public void addHeader(SOAPHeader header)
    {
        if (myHeaders == null) {
            myHeaders = new Vector();
        }
        myHeaders.add(header);
    }

    /**
     * Clear the list of headers which we insert into each message
     */
    public void clearHeaders()
    {
        myHeaders = null;
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
        category.debug("Enter: ServiceClient::invoke(ns, meth, args)" );
        RPCElement  body = new RPCElement(namespace, method, args, serviceDesc);
        Object ret = invoke( body );
        category.debug("Exit: ServiceClient::invoke(ns, meth, args)" );
        return ret;
    }

    /** Convenience method to invoke a method with a default (empty)
     * namespace.  Calls invoke() above.
     *
     * @param method the method name
     * @param args an array of Objects representing the arguments to the
     *             invoked method.  If any of these objects are RPCParams,
     *             Axis will use the embedded name of the RPCParam as the
     *             name of the parameter.  Otherwise, we will serialize
     *             each argument as an XML element called "arg<n>".
     * @return a deserialized Java Object containing the return value
     * @exception AxisFault
     */
    public Object invoke( String method, Object [] args ) throws AxisFault
    {
        return invoke("", method, args);
    }

    /** Invoke an RPC service with a pre-constructed RPCElement.
     *
     * @param body an RPCElement containing all the information about
     *             this call.
     * @return a deserialized Java Object containing the return value
     * @exception AxisFault
     */
    public Object invoke( RPCElement body ) throws AxisFault {
        category.debug("Enter: ServiceClient::invoke(RPCElement)" );
        SOAPEnvelope         reqEnv = new SOAPEnvelope();
        SOAPEnvelope         resEnv = null ;
        Message              reqMsg = new Message( reqEnv );
        Message              resMsg = null ;
        Vector               resArgs = null ;
        Object               result = null ;

        // Clear the output params
        outParams = null;

        // If we have headers to insert, do so now.
        if (myHeaders != null) {
            for (int i = 0; i < myHeaders.size(); i++) {
                reqEnv.addHeader((SOAPHeader)myHeaders.get(i));
            }
        }

        String uri = null;
        if (serviceDesc != null) uri = serviceDesc.getEncodingStyleURI();
        if (uri != null) reqEnv.setEncodingStyleURI(uri);

        if (serviceDesc != null && serviceDesc.isRPC()) 
            msgContext.setProperty(MessageContext.ISRPC, "true" );

        msgContext.setRequestMessage(reqMsg);
        msgContext.setResponseMessage(resMsg);

        reqEnv.addBodyElement(body);
        reqEnv.setMessageType(Message.REQUEST);

        if ( body.getPrefix() == null )       body.setPrefix( "m" );
        if ( body.getNamespaceURI() == null ) {
            throw new AxisFault("ServiceClient.invoke", "Cannot invoke ServiceClient with null namespace URI for method "+body.getMethodName(),
                    null, null);
        } else if (msgContext.getServiceHandler() == null) {
            msgContext.setTargetService(body.getNamespaceURI());
        }


        if (category.isDebugEnabled()) {
            StringWriter writer = new StringWriter();
            try {
                SerializationContext ctx = new SerializationContext(writer,
                                                                   msgContext);
                reqEnv.output(ctx);
                writer.close();
            } catch (Exception e) {
                e.printStackTrace(new PrintWriter(writer));
            } finally {
                category.debug(writer.getBuffer().toString());
            }
        }

        try {
            invoke();
        }
        catch( Exception e ) {
            category.error( e );
            if ( !(e instanceof AxisFault ) ) e = new AxisFault( e );
            throw (AxisFault) e ;
        }

        resMsg = msgContext.getResponseMessage();

        if (resMsg == null)
            throw new AxisFault(new Exception("Null response message!"));

        /** This must happen before deserialization...
         */
        resMsg.setMessageType(Message.RESPONSE);

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

            /**
             * Are there out-params?  If so, return a Vector instead.
             */
            if (resArgs.size() > 1) {
                outParams = new Vector();
                for (int i = 1; i < resArgs.size(); i++) {
                    outParams.add(resArgs.get(i));
                }
            }
        }

        category.debug("Exit: ServiceClient::invoke(RPCElement)" );
        return( result );
    }

    /**
     * Set engine option.
     */
    public void addOption(String name, Object value) {
        engine.addOption(name, value);
    }

    /**
     * Invoke this ServiceClient with its established MessageContext
     * (perhaps because you called this.setRequestMessage())
     *
     * @exception AxisFault
     */
    public void invoke() throws AxisFault {
        category.debug("Enter: Service::invoke()" );

        msgContext.reset();

        msgContext.setTimeout(timeout);

        if (myProperties != null) {
            Enumeration enum = myProperties.keys();
            while (enum.hasMoreElements()) {
                String name = (String) enum.nextElement();
                Object value = myProperties.get(name);
                msgContext.setProperty(name, value);
            }
        }

        msgContext.setMaintainSession(maintainSession);

        // set up message context if there is a transport
        if (transport != null) {
// Temporary - this class will soon become a shell for Call
            transport.setupMessageContext(msgContext, new Call(), this.engine);
        }
        else
            msgContext.setTransportName( transportName );

        try {
            engine.invoke( msgContext );

            if (transport != null)
                transport.processReturnedMessageContext(msgContext);
        }
        catch( AxisFault fault ) {
            category.error( fault );
            throw fault ;
        }

        category.debug("Exit: Service::invoke()" );
    }

}

