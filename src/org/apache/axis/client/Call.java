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
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.encoding.DeserializerFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPFaultElement;
import org.apache.axis.message.SOAPHeader;
import javax.xml.rpc.encoding.XMLType;
import javax.xml.rpc.namespace.QName;
import org.apache.axis.transport.http.HTTPTransport;
import org.apache.log4j.Category;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Axis' JAXRPC Dynamic Invocation Interface implementation of the Call
 * interface.  This class should be used to actually invoke the Web Service.
 * It can be prefilled by a WSDL document (on the constructor to the Service
 * object) or you can fill in the data yourself.
 * <pre>
 * Properties:
 *     NAMESPACE        - Namespace URI of RPC Body
 *     SEND_TYPE_ATTR   - Should we send the XSI type attributes (true/false)
 *     TIMEOUT          - Timeout used by transport sender in seconds
 *     TRANSPORT_NAME   - Name of transport handler to use
 * </pre>
 *
 * @author Doug Davis (dug@us.ibm.com)
 */

public class Call implements javax.xml.rpc.Call {
    static Category category = Category.getInstance(Call.class.getName());

    private QName              portTypeName    = null ;
    private String             operationName   = null ;
    private Vector             paramNames      = null ;
    private Vector             paramTypes      = null ;
    private Vector             paramModes      = null ;
    private String             encodingStyle   = null ;
    private XMLType            returnType      = null ;

    private AxisEngine         engine          = null ;
    private MessageContext     msgContext      = null ;

    // Collection of properties to store and put in MessageContext at
    // invoke() time
    private Hashtable          myProperties    = null ;

    private boolean            maintainSession = false ;


    // Our Transport, if any
    private Transport          transport       = null ;
    private String             transportName   = null ;

    // A place to store output parameters
    private Vector             outParams       = null;

    // A place to store any client-specified headers
    private Vector             myHeaders       = null;


    public static final String NAMESPACE         = "namespace" ;
    public static final String SEND_TYPE_ATTR    = "send_type_attr" ;
    public static final String TIMEOUT           = "timeout" ;
    public static final String TRANSPORT_NAME    = "transport_name" ;
    public static final String TRANSPORT_PROPERTY= "java.protocol.handler.pkgs";

    /**
     * A Hashtable mapping protocols (Strings) to Transports (classes)
     */
    private static Hashtable transports  = new Hashtable();
    private static boolean   initialized = false;

    private static FileProvider configProvider =
                           new FileProvider(Constants.CLIENT_CONFIG_FILE);


    /************************************************************************/
    /* Start of core JAX-RPC stuff                                          */
    /************************************************************************/

    /**
     * Default constructor - not much else to say.
     */
    public Call() {
        setEngine( new AxisClient(configProvider) );
        if ( !initialized ) initialize();
    }

    /**
     * Build a call from a URL string
     *
     * @param url the target endpoint URL
     * @exception MalformedURLException
     */
    public Call(String url) throws MalformedURLException {
        this();
        setTargetEndpointAddress(new URL(url));
    }

    /**
     * Build a call from a URL
     *
     * @param url the target endpoint URL
     */
    public Call(URL url) {
        this();
        setTargetEndpointAddress(url);
    }

    /**
     * Returns the encoding style as a URI that should be used for the SOAP
     * message.
     *
     * @return String URI of the encoding style to use
     */
    public String getEncodingStyle() {
        return( encodingStyle );
    }

    /**
     * Sets the encoding style to the URL passed in.
     *
     * @param namespaceURI URI of the encoding to use.
     */
    public void setEncodingStyle(String namespaceURI) {
        encodingStyle = namespaceURI ;
    }

    /**
     * Adds the specified parameter to the list of parameters for the
     * operation associated with this Call object.
     *
     * @param paramName      Name that will be used for the parameter in the XML
     * @param paramType      XMLType of the parameter
     * @param parameterMode  one of PARAM_MODE_IN, PARAM_MODE_OUT
     *                       or PARAM_MODE_INOUT
     */
    public void addParameter(String paramName, XMLType paramType,
                             int parameterMode) {

        QName qn = paramType.getType();

        if ( paramNames == null ) {
            paramNames = new Vector();
            paramTypes = new Vector();
            paramModes = new Vector();
        }

        paramNames.add( paramName );
        paramTypes.add( paramType.getType() );
        paramModes.add( new Integer(parameterMode) );
    }

    /**
     * Sets the return type of the operation associated with this Call object.
     *
     * @param type XMLType of the return value.
     */
    public void setReturnType(XMLType type) {
        returnType = type ;
    }

    /**
     * Clears the list of parameters.
     */
    public void removeAllParameters() {
        paramNames = null ;
        paramTypes = null ;
        paramModes = null ;
    }

    /**
     * Returns the operation name associated with this Call object.
     *
     * @return String Name of the operation or null if not set.
     */
    public String getOperationName() {
        return( operationName );
    }

    /**
     * Sets the operation name associated with this Call object.  This will
     * not check the WSDL (if there is WSDL) to make sure that it's a valid
     * operation name.
     *
     * @param opName Name of the operation.
     */
    public void setOperationName(String opName) {
        operationName = opName ;
    }

    /**
     * Returns the fully qualified name of the port for this Call object
     * (if there is one).
     *
     * @return QName Fully qualified name of the port (or null if not set)
     */
    public QName getPortTypeName() {
        return( portTypeName );
    }

    /**
     * Sets the port type of this Call object.  This call will not set
     * any additional fields, nor will it do any checking to verify that
     * this port type is actually defined in the WSDL - for now anyway.
     *
     * @param portType Fully qualified name of the portType
     */
    public void setPortTypeName(QName portType) {
        portTypeName = portType ;
    }

    /**
     * Sets the URL of the target Web Service.
     *
     * @param address URL of the target Web Service
     */
    public void setTargetEndpointAddress(java.net.URL address) {
        try {
            String protocol = address.getProtocol();
            Transport transport = getTransportForProtocol(protocol);
            if (transport == null)
                throw new AxisFault("Call.setTargetEndpointAddress",
                        "No transport mapping for protocol: " + protocol,
                        null, null);
            transport.setUrl(address.toString());
            setTransport(transport);
        }
        catch( org.apache.axis.AxisFault exp ) {
            // do what?
            // throw new AxisFault("Call.setTargetEndpointAddress",
                    //"Malformed URL Exception: " + e.getMessage(), null, null);
        }
    }

    /**
     * Returns the URL of the target Web Service.
     *
     * @return URL URL of the target Web Service
     */
    public java.net.URL getTargetEndpointAddress() {
        try {
            if ( transport == null ) return( null );
            return( new java.net.URL( transport.getUrl() ) );
        }
        catch( Exception exp ) {
            return( null );
        }
    }

    /**
     * Allows you to set a named property to the passed in value.
     * This will just be stored in a Hashtable - it's then up to
     * one of the Handler (or the Axis engine itself) to go looking for
     * one of them.
     *
     * @param name  Name of the property
     * @param value Value of the property
     */
    public void setProperty(String name, Object value) {
        if (name == null || value == null) return;

        if ( name.equals(TRANSPORT_NAME) ) {
            transportName = name ;
            if ( transport != null )
                transport.setTransportName( name );
            return ;
         }
        if (myProperties == null)
            myProperties = new Hashtable();
        myProperties.put(name, value);
    }

    /**
     * Returns the value associated with the named property - or null if not
     * defined/set.
     *
     * @return Object value of the property - or null
     */
    public Object getProperty(String name) {
        return (name == null || myProperties == null) ? null :
                                                        myProperties.get(name);
    }

    /**
     * Removes (if set) the named property.
     *
     * @param name name of the property to remove
     */
    public void removeProperty(String name) {
        if ( name == null || myProperties == null ) return ;
        myProperties.remove( name );
    }

    /**
     * Invokes the operation associated with this Call object using the
     * passed in parameters as the arguments to the method.
     *
     * For Messaging (ie. non-RPC) the params argument should be an array
     * of SOAPBodyElements.  <b>All</b> of them need to be SOAPBodyElements,
     * if any of them are not this method will default back to RPC.  In the
     * Messaging case the return value will be a vector of SOAPBodyElements.
     *
     * @param  params Array of parameters to invoke the Web Service with
     * @return Object Return value of the operation/method - or null
     * @throws RemoteException if there's an error
     */
    public Object invoke(Object[] params) throws java.rmi.RemoteException {
        /* First see if we're dealing with Messaging instead of RPC.        */
        /* If ALL of the params are SOAPBodyElements then we're doing       */
        /* Messaging, otherwise just fall through to normal RPC processing. */
        /********************************************************************/
        SOAPEnvelope  env = null ;
        int i ;

        for ( i = 0 ; params != null && i < params.length ; i++ )
            if ( !(params[i] instanceof SOAPBodyElement) ) break ;

        if ( params != null && params.length > 0 && i == params.length ) {
            /* ok, we're doing Messaging, so build up the message */
            /******************************************************/
            env = new SOAPEnvelope();

            for ( i = 0 ; myHeaders != null && i < myHeaders.size() ; i++ )
                env.addHeader((SOAPHeader)myHeaders.get(i));

            if ( !(params[0] instanceof SOAPEnvelope) )
                for ( i = 0 ; i < params.length ; i++ )
                    env.addBodyElement( (SOAPBodyElement) params[i] );

            Message msg = new Message( env );
            msgContext.setRequestMessage( msg );

            invoke();

            msg = msgContext.getResponseMessage();
            if (msg == null)
                throw new AxisFault(new Exception("Null response message!"));

            env = msg.getAsSOAPEnvelope();
            return( env.getBodyElements() );
        }


        if ( operationName == null )
            throw new AxisFault( "No operation name specified" );
        try {
            String ns = (String) getProperty( Call.NAMESPACE );
            if ( ns == null )
                return( this.invoke(operationName,getParamList(params)) );
            else
                return( this.invoke(ns,operationName,getParamList(params)) );
        }
        catch( Exception exp ) {
            if ( exp instanceof AxisFault ) throw (AxisFault) exp ;

            throw new AxisFault( "Error invoking operation:\n" + exp );
        }
    }

    /**
     * Invokes the operation associated with this Call object using the passed
     * in parameters as the arguments to the method.  This will return
     * immediately rather than waiting for the server to complete its
     * processing.
     *
     * NOTE: the return immediately part isn't implemented yet
     *
     * @param  params Array of parameters to invoke the Web Service with
     * @throws JAXRPCException is there's an error
     */
    public void invokeOneWay(Object[] params)
                           throws javax.xml.rpc.JAXRPCException {
        try {
            invoke( getParamList(params) );
        }
        catch( Exception exp ) {
            throw new javax.xml.rpc.JAXRPCException( exp.toString() );
        }
    }

    /************************************************************************/
    /* End of core JAX-RPC stuff                                            */
    /************************************************************************/

    /** Invoke the service with a custom SOAPEnvelope.
     *
     * Note: Not part of JAX-RPC specification.
     *
     * @param env a SOAPEnvelope to send.
     * @exception AxisFault
     */
    public SOAPEnvelope invoke(SOAPEnvelope env)
                                  throws java.rmi.RemoteException {
        try {
            Message msg = null ;
            int     i ;

            for ( i = 0 ; myHeaders != null && i < myHeaders.size() ; i++ )
                env.addHeader((SOAPHeader)myHeaders.get(i));

            msg = new Message( env );
            msgContext.setRequestMessage( msg );
            invoke();
            msg = msgContext.getResponseMessage();
            if (msg == null)
                throw new AxisFault(new Exception("Null response message!"));
            return( msg.getAsSOAPEnvelope() );
        }
        catch( Exception exp ) {
            if ( exp instanceof AxisFault ) throw (AxisFault) exp ;

            throw new AxisFault( "Error invoking operation",
                                                exp );
        }
    }


    /** Register a Transport that should be used for URLs of the specified
     * protocol.
     *
     * Note: Not part of JAX-RPC specification.
     *
     * @param protocol the URL protocol (i.e. "tcp" for "tcp://" urls)
     * @param transportClass the class of a Transport type which will be used
     *                       for matching URLs.
     */
    public static void setTransportForProtocol(String protocol,
                                               Class transportClass) {
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
     *
     * Note: Not part of JAX-RPC specification.
     */
    public static synchronized void initialize() {
        if (!initialized) {
            addTransportPackage("org.apache.axis.transport");

            setTransportForProtocol("local",
                         org.apache.axis.transport.local.LocalTransport.class);
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
     * Note: Not part of JAX-RPC specification.
     *
     * @param packageName the package in which to search for protocol names.
     */
    public static synchronized void addTransportPackage(String packageName) {
        String currentPackages = System.getProperty(TRANSPORT_PROPERTY);
        if (currentPackages == null) {
            currentPackages = "";
        } else {
            currentPackages += "|";
        }
        currentPackages += packageName;

        System.setProperty(TRANSPORT_PROPERTY, currentPackages);
    }

    /**
     * Change the AxisEngine being used by this Call object.  This should
     * be called right away after creating the Call object since any
     * values in the old msgContext object associated with the old engine
     * will NOT be carried over to the new msgContext of this new engine.
     *
     * Note: Not part of JAX-RPC specification.
     *
     * @param newEngine the new AxisEngine to use
     */
    public void setEngine(AxisEngine newEngine) {
        engine = newEngine ;
        msgContext = new MessageContext( engine );
    }

    /**
     * Returns the current AxisEngine being used by this Call object.
     *
     * Note: Not part of JAX-RPC specification.
     *
     * @return AxisEngine the current AxisEngine
     */
    public AxisEngine getEngine() {
        return( engine );
    }


    /**
     * Convert the list of objects into RPCParam's based on the paramNames,
     * paramTypes and paramModes variables.  If those aren't set then just
     * return what was passed in.
     *
     * @param  params   Array of parameters to pass into the operation/method
     * @return Object[] Array of parameters to pass to invoke()
     */
    private Object[] getParamList(Object[] params)
                           throws javax.xml.rpc.JAXRPCException {
        int  numParams = 0 ;
        int  i ;

        // If we never set-up any names... then just return what was passed in
        //////////////////////////////////////////////////////////////////////
        if ( paramNames == null ) return( params );

        // Count the number of IN and INOUT params, this needs to match the
        // number of params passed in - if not throw an error
        /////////////////////////////////////////////////////////////////////
        for ( i = 0 ; i < paramNames.size() ; i++ ) {
            if (((Integer)paramModes.get(i)).intValue() == Call.PARAM_MODE_OUT)
                continue ;
            numParams++ ;
        }

        if ( numParams != params.length )
            throw new javax.xml.rpc.JAXRPCException(
                                       "Number of parameters passed in (" +
                                       params.length + ") doesn't match the " +
                                       "number of IN/INOUT parameters (" +
                                       numParams + ") from the addParameter" +
                                       "() calls" );

        // All ok - so now produce an array of RPCParams
        //////////////////////////////////////////////////
        Vector result = new Vector();
        int    j = 0 ;
        for ( i = 0 ; i < numParams ; i++ ) {
            if (((Integer)paramModes.get(i)).intValue() == Call.PARAM_MODE_OUT)
                continue ;
            RPCParam p = new RPCParam( (String) paramNames.get(i),
                                          params[j++] );
            result.add( p );
        }

        return( result.toArray() );
    }

    /**
     * Set the Transport
     *
     * Note: Not part of JAX-RPC specification.
     *
     * @param transport the Transport object we'll use to set up
     *                  MessageContext properties.
     */
    public void setTransport(Transport trans) {
        transport = trans;
        if (category.isInfoEnabled())
            category.info("Transport is " + transport);
    }

    /** Get the Transport registered for the given protocol.
     *
     * Note: Not part of JAX-RPC specification.
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
     * Directly set the request message in our MessageContext.
     *
     * This allows custom message creation.
     *
     * Note: Not part of JAX-RPC specification.
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
     * Note: Not part of JAX-RPC specification.
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
     * Note: Not part of JAX-RPC specification.
     *
     * @param yesno true if session state is desired, false if not.
     */
    public void setMaintainSession (boolean yesno) {
        maintainSession = yesno;
    }

    /**
     * Obtain a reference to our MessageContext.
     *
     * Note: Not part of JAX-RPC specification.
     *
     * @return the MessageContext.
     */
    public MessageContext getMessageContext () {
        return msgContext;
    }

    /**
     * Add a header which should be inserted into each outgoing message
     * we generate.
     *
     * Note: Not part of JAX-RPC specification.
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
     *
     * Note: Not part of JAX-RPC specification.
     */
    public void clearHeaders()
    {
        myHeaders = null;
    }

    /**
     * Map a type for serialization.
     *
     * Note: Not part of JAX-RPC specification.
     *
     * @param _class the Java class of the data type.
     * @param qName the xsi:type QName of the associated XML type.
     * @param serializer a Serializer which will be used to write the XML.
     */
    public void addSerializer(Class _class, QName qName,
                              Serializer serializer){
        TypeMappingRegistry typeMap = msgContext.getTypeMappingRegistry();
        typeMap.addSerializer(_class, qName, serializer);
    }

    /**
     * Map a type for deserialization.
     *
     * Note: Not part of JAX-RPC specification.
     *
     * @param qName the xsi:type QName of an XML Schema type.
     * @param _class the class of the associated Java data type.
     * @param deserializerFactory a factory which can create deserializer
     *                            instances for this type.
     */
    public void addDeserializerFactory(QName qName, Class _class,
                                       DeserializerFactory deserFactory){
        TypeMappingRegistry typeMap = msgContext.getTypeMappingRegistry();
        typeMap.addDeserializerFactory(qName, _class, deserFactory);
    }

    /************************************************
     * Invocation
     */

    /** Invoke an RPC service with a method name and arguments.
     *
     * This will call the service, serializing all the arguments, and
     * then deserialize the return value.
     *
     * Note: Not part of JAX-RPC specification.
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
    public Object invoke(String namespace, String method, Object[] args)
                    throws AxisFault {
        category.debug("Enter: Call::invoke(ns, meth, args)" );
        RPCElement  body = new RPCElement(namespace, method, args);
        Object ret = invoke( body );
        category.debug("Exit: Call::invoke(ns, meth, args)" );
        return ret;
    }

    /** Convenience method to invoke a method with a default (empty)
     * namespace.  Calls invoke() above.
     *
     * Note: Not part of JAX-RPC specification.
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
     * Note: Not part of JAX-RPC specification.
     *
     * @param body an RPCElement containing all the information about
     *             this call.
     * @return a deserialized Java Object containing the return value
     * @exception AxisFault
     */
    public Object invoke( RPCElement body ) throws AxisFault {
        category.debug("Enter: Call::invoke(RPCElement)" );
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

        String uri = encodingStyle ;
        if (uri != null) reqEnv.setEncodingStyleURI(uri);

        msgContext.setRequestMessage(reqMsg);
        msgContext.setResponseMessage(resMsg);

        reqEnv.addBodyElement(body);
        reqEnv.setMessageType(Message.REQUEST);

        if ( body.getPrefix() == null )       body.setPrefix( "m" );
        if ( body.getNamespaceURI() == null ) {
            throw new AxisFault("Call.invoke", "Cannot invoke Call with " +
                    "null namespace URI for method "+body.getMethodName(),
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
            throw new AxisFault("Null response message!");

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

        category.debug("Exit: Call::invoke(RPCElement)" );
        return( result );
    }

    /**
     * Set engine option.
     *
     * Note: Not part of JAX-RPC specification.
     */
    public void addOption(String name, Object value) {
        engine.addOption(name, value);
    }

    /**
     * Invoke this Call with its established MessageContext
     * (perhaps because you called this.setRequestMessage())
     *
     * Note: Not part of JAX-RPC specification.
     *
     * @exception AxisFault
     */
    public void invoke() throws AxisFault {
        category.debug("Enter: Call::invoke()" );

        msgContext.reset();

        if (myProperties != null) {
            Enumeration enum = myProperties.keys();
            while (enum.hasMoreElements()) {
                String name = (String) enum.nextElement();
                Object value = myProperties.get(name);
                int    intValue = 0 ;

                if ( name.equals( TIMEOUT ) ) {
                    if ( value instanceof Integer )
                        intValue = ((Integer)value).intValue();
                    else
                        intValue = Integer.parseInt((String)value);

                    msgContext.setTimeout( intValue );
                }
                else
                    msgContext.setProperty(name, value);
            }
        }

        msgContext.setMaintainSession(maintainSession);

        // set up message context if there is a transport
        if (transport != null) {
            transport.setupMessageContext(msgContext, this, this.engine);
        }
        else
            msgContext.setTransportName( transportName );

        category.debug("TargetService:" + msgContext.getTargetService());
        try {
            engine.invoke( msgContext );

            if (transport != null)
                transport.processReturnedMessageContext(msgContext);
        }
        catch( AxisFault fault ) {
            category.error( fault );
            throw fault ;
        }

        category.debug("Exit: Call::invoke()" );
    }

    /**
     * Get the output parameters (if any) from the last invocation.
     *
     * NOTE that the params returned are all RPCParams, containing
     * name and value - if you want the value, you'll need to call
     * param.getValue().
     *
     * Note: Not part of JAX-RPC specification.
     *
     * @return a Vector of RPCParams
     */
    public Vector getOutputParams()
    {
        return this.outParams;
    }
}
