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
import org.apache.axis.encoding.DeserializerFactory;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.ServiceDescription;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeader;
import org.apache.log4j.Category;

import javax.xml.rpc.namespace.QName;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

/**
 * Allows an Axis service to be invoked from the client side.
 * Contains message and session state which may be reused
 * across multiple invocations on the same ServiceClient.
 *
 * @author Rob Jellinghaus (robj@unrealities.com)
 * @author Doug Davis (dug@us.ibm.com)
 * @author Glen Daniels (gdaniels@macromedia.com)
 * 
 * @deprecated To be replaced by the Call class before the 1.0 release
 * @see org.apache.axis.client.Call
 */


// Need to add proxy, ssl.... other cool things - but it's a start
// Only supports String

public class ServiceClient {
    static Category category =
            Category.getInstance(ServiceClient.class.getName());

    /***************************************************************
     * Static stuff
     */
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
        Call.setTransportForProtocol(protocol, transportClass);
    }

    /**
     * Set up the default transport URL mappings.
     *
     * This must be called BEFORE doing non-standard URL parsing (i.e. if you
     * want the system to accept a "local:" URL).  This is why the Options class
     * calls it before parsing the command-line URL argument.
     */
    public static void initialize()
    {
        Call.initialize();
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
    public static void addTransportPackage(String packageName)
    {
        Call.addTransportPackage(packageName);
    }

    private Call call;
    
    /**
     * Basic, no-argument constructor.
     */
    public ServiceClient () {
        call = new Call();
    }

    /**
     * Construct a ServiceClient with just an AxisEngine.
     */
    public ServiceClient (AxisEngine engine) {
        call = new Call();
        call.setEngine(engine);
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
        try {
            call = new Call(endpointURL);
        } catch (MalformedURLException e) {
            throw new AxisFault(e);
        }
    }

    /**
     * Construct a ServiceClient with a given endpoint URL & engine
     */
    public ServiceClient(String endpointURL, AxisEngine engine)
            throws AxisFault
    {
        try {
            call = new Call(endpointURL);
            call.setEngine(engine);
        } catch (MalformedURLException e) {
            throw new AxisFault(e);
        }
    }

    /**
     * Construct a ServiceClient with the given Transport.
     *
     * @param transport a pre-constructed Transport object which will be used
     *                  to set up the MessageContext appropriately for each
     *                  request
     */
    public ServiceClient (Transport transport) {
        call = new Call();
        call.setTransport(transport);
    }

    /**
     * Construct a ServiceClient with the given Transport & engine.
     */
    public ServiceClient (Transport transport, AxisEngine engine) {
        this(engine);
        call.setTransport(transport);
    }

    /**
     * Set the Transport for this ServiceClient.
     *
     * @param transport the Transport object we'll use to set up
     *                  MessageContext properties.
     */
    public void setTransport(Transport transport) {
        call.setTransport(transport);
    }

    /**
     * Set the URL (and the transport state).
     */
    public void setURL (String endpointURL)
            throws AxisFault
    {
        try {
            URL url = new URL(endpointURL);
            
        } catch (MalformedURLException e) {
            throw new AxisFault("ServiceClient.setURL",
                    "Malformed URL Exception: " + e.getMessage(), null, null);
        }
    }

    /**
     * Returns the URL of the transport
     */
    public String getURL() {
        URL url = call.getTargetEndpointAddress();
        if (url == null)
            return null;
        return url.toString();
    }

    /** Get the Transport registered for the given protocol.
     *
     * @param protocol a protocol such as "http" or "local" which may
     *                 have a Transport object associated with it.
     * @return the Transport registered for this protocol, or null if none.
     */
    public Transport getTransportForProtocol(String protocol)
    {
        return call.getTransportForProtocol(protocol);
    }

    /**
     * Set a property which should be carried through our MessageContexts
     * into the engine.
     *
     * @param name the property name to set.
     * @param value the value of the property.
     */
    public void set (String name, Object value) {
        call.setProperty(name, value);
    }

    /**
     * Get a property from our list of persistent ones.
     *
     * @param name the property name to retrieve.
     * @return the property's value.
     */
    public Object get (String name) {
        return call.getProperty(name);
    }

    /**
     * Removes the named property from our list of persistent ones.
     *
     * @param name the property name to remove
     */
    public void remove(String name) {
        call.removeProperty(name);
    }

    /**
     * Set timeout in our MessageContext.
     *
     * @param value the maximum amount of time, in milliseconds
     */
    public void setTimeout (int value) {
        call.setProperty(Call.TIMEOUT,  new Integer(value));
    }

    /**
     * Get timeout from our MessageContext.
     *
     * @return value the maximum amount of time, in milliseconds
     */
    public int getTimeout () {
        Integer timeout = (Integer)call.getProperty(Call.TIMEOUT);
        if (timeout == null) return -1;
        
        return timeout.intValue();
    }

    /**
     * Directly set the request message in our MessageContext.
     *
     * This allows custom message creation.
     *
     * @param msg the new request message.
     */
    public void setRequestMessage(Message msg) {
        call.setRequestMessage(msg);
    }

    /**
     * Directly get the response message in our MessageContext.
     *
     * Shortcut for having to go thru the msgContext
     *
     * @return the response Message object in the msgContext
     */
    public Message getResponseMessage() {
        return call.getResponseMessage();
    }

    /**
     * Determine whether we'd like to track sessions or not.
     *
     * This just passes through the value into the MessageContext.
     *
     * @param yesno true if session state is desired, false if not.
     */
    public void setMaintainSession (boolean yesno) {
        call.setMaintainSession(yesno);
    }

    /**
     * Obtain a reference to our MessageContext.
     *
     * @return the ServiceClient's MessageContext.
     */
    public MessageContext getMessageContext () {
        return call.getMessageContext();
    }

    /**
     * Set the ServiceDescription associated with this ServiceClient.
     *
     * @param serviceDesc a ServiceDescription.
     */
    public void setServiceDescription(ServiceDescription serviceDesc)
    {
        // !!! MEN WORKING - go through the SD and set parameters, etc.
        //     as appropriate
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
        return call.getOutputParams();
    }

    /**
     * Add a header which should be inserted into each outgoing message
     * we generate.
     *
     * @param header a SOAPHeader to be inserted into messages
     */
    public void addHeader(SOAPHeader header)
    {
        call.addHeader(header);
    }

    /**
     * Clear the list of headers which we insert into each message
     */
    public void clearHeaders()
    {
        call.clearHeaders();
    }

    /**
     * Map a type for serialization.
     *
     * @param _class the Java class of the data type.
     * @param qName the xsi:type QName of the associated XML type.
     * @param serializer a Serializer which will be used to write the XML.
     */
    public void addSerializer(Class _class,
                              QName qName,
                              Serializer serializer) {
        call.addSerializer(_class, qName, serializer);
    }

    /**
     * Map a type for deserialization.
     *
     * @param qName the xsi:type QName of an XML Schema type.
     * @param _class the class of the associated Java data type.
     * @param deserializerFactory a factory which can create deserializer
     *                            instances for this type.
     */
    public void addDeserializerFactory(QName qName,
                                       Class _class,
                                       DeserializerFactory deserializerFactory)
    {
        call.addDeserializerFactory(qName, _class, deserializerFactory);
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
        try {
            return call.invoke(env);
        } catch (java.rmi.RemoteException e) {
            throw new AxisFault(e);
        }
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
    public Object invoke( String namespace, String method, Object[] args ) 
            throws AxisFault {
        return call.invoke(namespace, method, args);
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
        return call.invoke(body);
    }

    /**
     * Set engine option.
     */
    public void addOption(String name, Object value) {
        call.addOption(name, value);
    }

    /**
     * Invoke this ServiceClient with its established MessageContext
     * (perhaps because you called this.setRequestMessage())
     *
     * @exception AxisFault
     */
    public void invoke() throws AxisFault {
        call.invoke();
    }

}

