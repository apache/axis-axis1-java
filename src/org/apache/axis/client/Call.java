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

import org.apache.axis.AxisFault;
import org.apache.axis.AxisProperties;
import org.apache.axis.Constants;
import org.apache.axis.Handler;
import org.apache.axis.InternalException;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.AxisEngine;
import org.apache.axis.attachments.Attachments;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.description.FaultDesc;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.encoding.DeserializerFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.SerializationContextImpl;
import org.apache.axis.encoding.SerializerFactory;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.encoding.ser.BaseDeserializerFactory;
import org.apache.axis.encoding.ser.BaseSerializerFactory;
import org.apache.axis.enum.Style;
import org.apache.axis.enum.Use;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCHeaderParam;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPFault;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.transport.http.HTTPTransport;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.LockableHashtable;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.Parameter;
import org.apache.axis.wsdl.symbolTable.Parameters;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.FaultInfo;
import org.apache.axis.wsdl.toJava.Utils;
import org.apache.commons.logging.Log;

import javax.wsdl.Binding;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.Operation;
import javax.wsdl.extensions.mime.MIMEPart;
import javax.wsdl.extensions.mime.MIMEMultipartRelated;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.ParameterMode;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.rmi.RemoteException;

/**
 * Axis' JAXRPC Dynamic Invocation Interface implementation of the Call
 * interface.  This class should be used to actually invoke the Web Service.
 * It can be prefilled by a WSDL document (on the constructor to the Service
 * object) or you can fill in the data yourself.
 * <pre>
 * Standard properties defined by in JAX-RPC's javax..xml.rpc.Call interface:
 *     USERNAME_PROPERTY        - User name for authentication
 *     PASSWORD_PROPERTY        - Password for authentication
 *     SESSION_PROPERTY         - Participate in a session with the endpoint?
 *     OPERATION_STYLE_PROPERTY - "rpc" or "document"
 *     SOAPACTION_USE_PROPERTY  - Should SOAPAction be used?
 *     SOAPACTION_URI_PROPERTY  - If SOAPAction is used, this is that action
 *     ENCODING_STYLE_PROPERTY  - Default is SOAP 1.1:  "http://schemas.xmlsoap.org/soap/encoding/"
 *
 * AXIS properties:
 *     SEND_TYPE_ATTR - Should we send the XSI type attributes (true/false)
 *     TIMEOUT        - Timeout used by transport sender in milliseconds
 *     TRANSPORT_NAME - Name of transport handler to use
 *     ATTACHMENT_ENCAPSULATION_FORMAT- Send attachments as MIME the default, or DIME.
 * </pre>
 *
 * @author Doug Davis (dug@us.ibm.com)
 * @author Steve Loughran
 */

public class Call implements javax.xml.rpc.Call {
    protected static Log log =
        LogFactory.getLog(Call.class.getName());
    private static Log tlog =
        LogFactory.getLog(Constants.TIME_LOG_CATEGORY);

    // The enterprise category is for stuff that an enterprise product might
    // want to track, but in a simple environment (like the AXIS build) would
    // be nothing more than a nuisance.
    protected static Log entLog =
        LogFactory.getLog(Constants.ENTERPRISE_LOG_CATEGORY);

    private boolean            parmAndRetReq   = true ;
    private Service            service         = null ;
    private QName              portName        = null;
    private QName              operationName   = null ;

    private MessageContext     msgContext      = null ;

    // Collection of properties to store and put in MessageContext at
    // invoke() time.  Known ones are stored in actual variables for
    // efficiency/type-consistency.  Unknown ones are in myProperties.
    private LockableHashtable  myProperties    = new LockableHashtable();
    private String             username        = null;
    private String             password        = null;
    private boolean            maintainSession = false;
    private boolean            useSOAPAction   = false;
    private String             SOAPActionURI   = null;
    private Integer            timeout         = null;

    /** Metadata for the operation associated with this Call */
    private OperationDesc      operation       = null;
    /** This will be true if an OperationDesc is handed to us whole */
    private boolean operationSetManually       = false;

    // Is this a one-way call?
    private boolean invokeOneWay               = false;
    private boolean isMsg                      = false;

    // Our Transport, if any
    private Transport          transport       = null ;
    private String             transportName   = null ;

    // A couple places to store output parameters.
    // As a HashMap, retrievable via QName (for getOutputParams).
    private HashMap            outParams       = null;
    // As a list, retrievable by index (for getOutputValues).
    private ArrayList          outParamsList   = null;

    // A place to store any client-specified headers
    private Vector             myHeaders       = null;

    public static final String SEND_TYPE_ATTR    = "send_type_attr" ;

    /**
     * This is the name of a property to set the transport of the message
     *
     * @see #setProperty
     */
    public static final String TRANSPORT_NAME    = "transport_name" ;

    /**
     * This is not the name of a property that can be set with
     * setProperty, despite its name.
     */
    public static final String TRANSPORT_PROPERTY= "java.protocol.handler.pkgs";

    /**
     * this is a property set in the message context when the invocation
     * process begins, for the benefit of handlers
     */
    public static final String WSDL_SERVICE      = "wsdl.service";

    /**
     * this is a property set in the message context when the invocation
     * process begins, for the benefit of handlers
     */
    public static final String WSDL_PORT_NAME    = "wsdl.portName";

    /**
     * @deprecated use WSDL_SERVICE instead.
     */
    public static final String JAXRPC_SERVICE    = WSDL_SERVICE;

    /**
     * @deprecated use WSDL_PORT_NAME instead.
     */
    public static final String JAXRPC_PORTTYPE_NAME = WSDL_PORT_NAME;

    /**
     * If this property is true, the code will throw a fault if there is no
     * response message from the server.  Otherwise, the
     * invoke method will return a null.
     */
    public static final String FAULT_ON_NO_RESPONSE = "call.FaultOnNoResponse";

    /**
     * Property for setting attachment format.
     * Can be set to either DIME or MIME (default)
     * @see #setProperty
     * @see #ATTACHMENT_ENCAPSULATION_FORMAT_DIME
     * @see #ATTACHMENT_ENCAPSULATION_FORMAT_MIME
     */
    public static final String ATTACHMENT_ENCAPSULATION_FORMAT=
      "attachment_encapsulation_format";
    /**
     * Property value for setting attachment format as MIME.
     */
    public static final String ATTACHMENT_ENCAPSULATION_FORMAT_MIME=
      "axis.attachment.style.mime";
    /**
     * Property value for setting attachment format as DIME.
     */
    public static final String ATTACHMENT_ENCAPSULATION_FORMAT_DIME=
      "axis.attachment.style.dime";

    /**
     * Timeout property: should be accompanies by an integer
     * @see #setProperty
     */
    public static final String CONNECTION_TIMEOUT_PROPERTY =
            "axis.connection.timeout";

    /**
     * A Hashtable mapping protocols (Strings) to Transports (classes)
     */
    private static Hashtable transports  = new Hashtable();

    static ParameterMode [] modes = new ParameterMode [] { null,
                                            ParameterMode.IN,
                                            ParameterMode.OUT,
                                            ParameterMode.INOUT };

    /** This is true when someone has called setEncodingStyle() */
    private boolean encodingStyleExplicitlySet = false;
    /** This is true when someone has called setOperationUse() */
    private boolean useExplicitlySet = false;

    /**
     * the name of a SOAP service that the call is bound to
     */
    private SOAPService myService = null;

    /**
     * these are our attachments
     */
    protected java.util.Vector attachmentParts = new java.util.Vector();

    /************************************************************************/
    /* Start of core JAX-RPC stuff                                          */
    /************************************************************************/

    /**
     * Default constructor - not much else to say.
     *
     * @param service  the <code>Service</code> this <code>Call</code> will
     *              work with
     */
    public Call(Service service) {
        this.service = service ;
        AxisEngine engine = service.getEngine();
        msgContext = new MessageContext( engine );
        myProperties.setParent(engine.getOptions());
        maintainSession = service.getMaintainSession();
        initialize();
    }

    /**
     * Build a call from a URL string.
     *
     * This is handy so that you don't have to manually call Call.initialize()
     * in order to register custom transports.  In other words, whereas doing
     * a new URL("local:...") would fail, new Call("local:...") works because
     * we do the initialization of our own and any configured custom protocols. 
     *
     * @param url the target endpoint URL
     * @exception MalformedURLException
     */
    public Call(String url) throws MalformedURLException {
        this(new Service());
        setTargetEndpointAddress(new URL(url));
    }

    /**
     * Build a call from a URL.
     *
     * @param url the target endpoint URL
     */
    public Call(URL url) {
        this(new Service());
        setTargetEndpointAddress(url);
    }

    ////////////////////////////
    //
    // Properties and the shortcuts for common ones.
    //

    /**
     * Allows you to set a named property to the passed in value.
     * There are a few known properties (like username, password, etc)
     * that are variables in Call.  The rest of the properties are
     * stored in a Hashtable.  These common properties should be
     * accessed via the accessors for speed/type safety, but they may
     * still be obtained via this method.  It's up to one of the
     * Handlers (or the Axis engine itself) to go looking for
     * one of them.
     *
     * There are various well defined properties defined in the
     * JAX-RPC specification and declared in the Call and Stub classes.
     * It is not possible to set any other properties beginning in java. or
     * javax. that are not in the specification.
     * @see javax.xml.rpc.Stub
     * @see javax.xml.rpc.Call
     *
     * There are other properties implemented in this class above and
     * beyond those of the JAX-RPC spec
     * Specifically, ATTACHMENT_ENCAPSULATION_FORMAT, CONNECTION_TIMEOUT_PROPERTY,
     * and TRANSPORT_NAME.
     *
     * It is intended that all future Axis-specific properties will begin
     * with axis. or apache. To ensure integration with future versions Axis,
     * use different prefixes for your own properties.
     *
     * Axis developers: keep this in sync with propertyNames below
     * @see #ATTACHMENT_ENCAPSULATION_FORMAT
     * @see #TRANSPORT_NAME
     * @see #CONNECTION_TIMEOUT_PROPERTY
     * @param name  Name of the property
     * @param value Value of the property
     */
    public void setProperty(String name, Object value) {
        if (name == null || value == null) {
            throw new JAXRPCException(
                    Messages.getMessage(name == null ?
                                         "badProp03" : "badProp04"));
        }
        else if (name.equals(USERNAME_PROPERTY)) {
            verifyStringProperty(name, value);
            setUsername((String) value);
        }
        else if (name.equals(PASSWORD_PROPERTY)) {
            verifyStringProperty(name, value);
            setPassword((String) value);
        }
        else if (name.equals(SESSION_MAINTAIN_PROPERTY)) {
            verifyBooleanProperty(name, value);
            setMaintainSession(((Boolean) value).booleanValue());
        }
        else if (name.equals(OPERATION_STYLE_PROPERTY)) {
            verifyStringProperty(name, value);
            setOperationStyle((String) value);
            if (getOperationStyle() == Style.DOCUMENT ||
                getOperationStyle() == Style.WRAPPED) {
                setOperationUse(Use.LITERAL_STR);
            } else if (getOperationStyle() == Style.RPC) {
                setOperationUse(Use.ENCODED_STR);
            }
        }
        else if (name.equals(SOAPACTION_USE_PROPERTY)) {
            verifyBooleanProperty(name, value);
            setUseSOAPAction(((Boolean) value).booleanValue());
        }
        else if (name.equals(SOAPACTION_URI_PROPERTY)) {
            verifyStringProperty(name, value);
            setSOAPActionURI((String) value);
        }
        else if (name.equals(ENCODINGSTYLE_URI_PROPERTY)) {
            verifyStringProperty(name, value);
            setEncodingStyle((String) value);
        }
        else if (name.equals(Stub.ENDPOINT_ADDRESS_PROPERTY)) {
            verifyStringProperty(name, value);
            setTargetEndpointAddress((String) value);
        }
        else if ( name.equals(TRANSPORT_NAME) ) {
            verifyStringProperty(name, value);
            transportName = (String) value ;
            if (transport != null) {
                transport.setTransportName((String) value);
            }
        }
        else if ( name.equals(ATTACHMENT_ENCAPSULATION_FORMAT) ) {
            verifyStringProperty(name, value);
            if(!value.equals(ATTACHMENT_ENCAPSULATION_FORMAT_MIME ) &&
               !value.equals(ATTACHMENT_ENCAPSULATION_FORMAT_DIME ))
                throw new JAXRPCException(
                        Messages.getMessage("badattachmenttypeerr", new String[] {
                        (String) value, ATTACHMENT_ENCAPSULATION_FORMAT_MIME + " "
                        +ATTACHMENT_ENCAPSULATION_FORMAT_DIME  }));
        }
        else if (name.equals(CONNECTION_TIMEOUT_PROPERTY)) {
            verifyIntegerProperty(name,value);
            setTimeout((Integer)value);
        }
        else if (name.startsWith("java.") || name.startsWith("javax.")) {
            throw new JAXRPCException(
                    Messages.getMessage("badProp05", name));
        }
        myProperties.put(name, value);
    } // setProperty

    /**
     * Verify that the type of the object is a String, and throw
     * an i18n-ized exception if not
     * @param name
     * @param value
     * @throws JAXRPCException if value is not a String
     */
    private void verifyStringProperty(String name, Object value) {
        if (!(value instanceof String)) {
            throw new JAXRPCException(
                    Messages.getMessage("badProp00", new String[]
                    {name,
                    "java.lang.String",
                    value.getClass().getName()}));
        }
    }

    /**
     * Verify that the type of the object is a Boolean, and throw
     * an i18n-ized exception if not
     * @param name
     * @param value
     * @throws JAXRPCException if value is not a Boolean
     */
    private void verifyBooleanProperty(String name, Object value) {
        if (!(value instanceof Boolean)) {
            throw new JAXRPCException(
                    Messages.getMessage("badProp00", new String[]
                    {name,
                    "java.lang.Boolean",
                    value.getClass().getName()}));
        }
    }

    /**
     * Verify that the type of the object is an Integer, and throw
     * an i18n-ized exception if not
     * @param name
     * @param value
     * @throws JAXRPCException if value is not an Integer
     */
    private void verifyIntegerProperty(String name, Object value) {
        if (!(value instanceof Integer)) {
            throw new JAXRPCException(
                    Messages.getMessage("badProp00", new String[]
                    {name,
                     "java.lang.Integer",
                     value.getClass().getName()}));
        }
    }

    /**
     * Returns the value associated with the named property.
     *
     * @param name the name of the property
     * @return Object value of the property or null if the property is not set
     * @throws JAXRPCException if the requested property is not a supported property
     */
    public Object getProperty(String name) {
        if (name == null || !isPropertySupported(name)) {
            throw new JAXRPCException(name == null ?
                  Messages.getMessage("badProp03") :
                  Messages.getMessage("badProp05", name));
        }
        return myProperties.get(name);
    } // getProperty

    /**
      * Removes (if set) the named property.
      *
      * @param name name of the property to remove
      */
     public void removeProperty(String name) {
         if (name == null || !isPropertySupported(name)) {
            throw new JAXRPCException(name == null ?
                  Messages.getMessage("badProp03") :
                  Messages.getMessage("badProp05", name));
         }
         myProperties.remove(name);
     } // removeProperty

    /**
     * Configurable properties supported by this Call object.
     */
    private static ArrayList propertyNames = new ArrayList();
    static {
        propertyNames.add(USERNAME_PROPERTY);
        propertyNames.add(PASSWORD_PROPERTY);
        propertyNames.add(SESSION_MAINTAIN_PROPERTY);
        propertyNames.add(OPERATION_STYLE_PROPERTY);
        propertyNames.add(SOAPACTION_USE_PROPERTY);
        propertyNames.add(SOAPACTION_URI_PROPERTY);
        propertyNames.add(ENCODINGSTYLE_URI_PROPERTY);
        propertyNames.add(Stub.ENDPOINT_ADDRESS_PROPERTY);
        propertyNames.add(TRANSPORT_NAME);
        propertyNames.add(ATTACHMENT_ENCAPSULATION_FORMAT);
        propertyNames.add(CONNECTION_TIMEOUT_PROPERTY);
    }

    public Iterator getPropertyNames() {
        return propertyNames.iterator();
    }

    public boolean isPropertySupported(String name) {
        return propertyNames.contains(name) || (!name.startsWith("java.")
               && !name.startsWith("javax."));
    }

    /**
     * Set the username.
     *
     * @param username  the new user name
     */
    public void setUsername(String username) {
        this.username = username;
    } // setUsername

    /**
     * Get the user name.
     *
     * @return the user name
     */
    public String getUsername() {
        return username;
    } // getUsername

    /**
     * Set the password.
     *
     * @param password  plain-text copy of the password
     */
    public void setPassword(String password) {
        this.password = password;
    } // setPassword

    /**
     * Get the password.
     *
     * @return a plain-text copy of the password
     */
    public String getPassword() {
        return password;
    } // getPassword

    /**
     * Determine whether we'd like to track sessions or not.  This
     * overrides the default setting from the service.
     * This just passes through the value into the MessageContext.
     * Note: Not part of JAX-RPC specification.
     *
     * @param yesno true if session state is desired, false if not.
     */
    public void setMaintainSession(boolean yesno) {
        maintainSession = yesno;
    }

    /**
     * Get the value of maintainSession flag.
     *
     * @return true if session is maintained, false otherwise
     */
    public boolean getMaintainSession() {
        return maintainSession;
    }

    /**
     * Set the operation style: "document", "rpc"
     * @param operationStyle string designating style
     */
    public void setOperationStyle(String operationStyle) {
        Style style = Style.getStyle(operationStyle, Style.DEFAULT);
        setOperationStyle(style);
    } // setOperationStyle

    /**
     * Set the operation style
     *
     * @param operationStyle
     */
    public void setOperationStyle(Style operationStyle) {
        if (operation == null) {
            operation = new OperationDesc();
        }

        operation.setStyle(operationStyle);

        // If no one has explicitly set the use, we should track
        // the style.  If it's non-RPC, default to LITERAL.
        if (!useExplicitlySet) {
            if (operationStyle != Style.RPC) {
                operation.setUse(Use.LITERAL);
            }
        }

        // If no one has explicitly set the encodingStyle, we should
        // track the style.  If it's RPC, default to SOAP-ENC, otherwise
        // default to "".
        if (!encodingStyleExplicitlySet) {
            String encStyle = "";
            if (operationStyle == Style.RPC) {
                // RPC style defaults to encoded, otherwise default to literal
                encStyle = msgContext.getSOAPConstants().getEncodingURI();
            }
            msgContext.setEncodingStyle(encStyle);
        }
    }

    /**
     * Get the operation style.
     *
     * @return the <code>Style</code> of the operation
     */
    public Style getOperationStyle() {
        if (operation != null) {
            return operation.getStyle();
        }
        return Style.DEFAULT;
    } // getOperationStyle

    /**
     * Set the operation use: "literal", "encoded"
     * @param operationUse string designating use
     */
    public void setOperationUse(String operationUse) {
        Use use = Use.getUse(operationUse, Use.DEFAULT);
        setOperationUse(use);
    } // setOperationUse

    /**
     * Set the operation use
     * @param operationUse
     */
    public void setOperationUse(Use operationUse) {
        useExplicitlySet = true;

        if (operation == null) {
            operation = new OperationDesc();
        }

        operation.setUse(operationUse);
        if (!encodingStyleExplicitlySet) {
            String encStyle = "";
            if (operationUse == Use.ENCODED) {
                // RPC style defaults to encoded, otherwise default to literal
                encStyle = msgContext.getSOAPConstants().getEncodingURI();
            }
            msgContext.setEncodingStyle(encStyle);
        }
    }

    /**
     * Get the operation use.
     *
     * @return the <code>Use</code> of the operation
     */
    public Use getOperationUse() {
        if (operation != null) {
            return operation.getUse();
        }
        return Use.DEFAULT;
    } // getOperationStyle

    /**
     * Flag to indicate if soapAction should be used.
     *
     * @param useSOAPAction  true if the soapAction header is to be used to
     *              help find the method to invoke, false otherwise
     */
    public void setUseSOAPAction(boolean useSOAPAction) {
        this.useSOAPAction = useSOAPAction;
    } // setUseSOAPAction

    /**
     * Discover if soapAction is being used.
     *
     * @return true if it is, false otherwise
     */
    public boolean useSOAPAction() {
        return useSOAPAction;
    } // useSOAPAction

    /**
     * Set the soapAction URI.
     *
     * @param SOAPActionURI  the new SOAP action URI
     */
    public void setSOAPActionURI(String SOAPActionURI) {
        useSOAPAction = true;
        this.SOAPActionURI = SOAPActionURI;
    } // setSOAPActionURI

    /**
     * Get the soapAction URI.
     *
     * @return the curretn SOAP action URI
     */
    public String getSOAPActionURI() {
        return SOAPActionURI;
    } // getSOAPActionURI

    /**
     * Sets the encoding style to the URL passed in.
     *
     * @param namespaceURI URI of the encoding to use.
     */
    public void setEncodingStyle(String namespaceURI) {
        encodingStyleExplicitlySet = true;
        msgContext.setEncodingStyle(namespaceURI);
    }

    /**
     * Returns the encoding style as a URI that should be used for the SOAP
     * message.
     *
     * @return String URI of the encoding style to use
     */
    public String getEncodingStyle() {
        return msgContext.getEncodingStyle();
    }

    /**
     * Sets the endpoint address of the target service port. This address must
     * correspond to the transport specified in the binding for this Call
     * instance.
     *
     * @param address - Endpoint address of the target service port; specified
     *                  as URI
     */
    public void setTargetEndpointAddress(String address) {
        URL urlAddress;
        try {
            urlAddress = new URL(address);
        }
        catch (MalformedURLException mue) {
            throw new JAXRPCException(mue);
        }
        setTargetEndpointAddress(urlAddress);
    }

    /**
     * Sets the URL of the target Web Service.
     *
     * Note: Not part of JAX-RPC specification.
     *
     * @param address URL of the target Web Service
     */
    public void setTargetEndpointAddress(java.net.URL address) {
        try {
            if ( address == null ) {
                setTransport(null);
                return ;
            }

            String protocol = address.getProtocol();

            // Handle the case where the protocol is the same but we
            // just want to change the URL - if so just set the URL,
            // creating a new Transport object will drop all session
            // data - and we want that stuff to persist between invoke()s.
            // Technically the session data should be in the message
            // context so that it can be persistent across transports
            // as well, but for now the data is in the Transport object.
            ////////////////////////////////////////////////////////////////
            if ( this.transport != null ) {
                String oldAddr = this.transport.getUrl();
                if ( oldAddr != null && !oldAddr.equals("") ) {
                    URL     tmpURL   = new URL( oldAddr );
                    String  oldProto = tmpURL.getProtocol();
                    if ( protocol.equals(oldProto) ) {
                        this.transport.setUrl( address.toString() );
                        return ;
                    }
                }
            }

            // Do we already have a transport for this address?
            Transport transport = service.getTransportForURL(address);
            if (transport != null) {
                setTransport(transport);
            }
            else {
            // We don't already have a transport for this address.  Create one.
                transport = getTransportForProtocol(protocol);
                if (transport == null)
                    throw new AxisFault("Call.setTargetEndpointAddress",
                                 Messages.getMessage("noTransport01",
                                 protocol), null, null);
                transport.setUrl(address.toString());
                setTransport(transport);
                service.registerTransportForURL(address, transport);
            }
        }
        catch( Exception exp ) {
            log.error(Messages.getMessage("exception00"), exp);
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
    public String getTargetEndpointAddress() {
        try {
            if ( transport == null ) return( null );
            return( transport.getUrl() );
        }
        catch( Exception exp ) {
            return( null );
        }
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
    //
    // end properties code.
    //
    ////////////////////////////

    /**
     * Is the caller required to provide the parameter and return type
     * specification?
     * If true, then
     *  addParameter and setReturnType MUST be called to provide the meta data.
     * If false, then
     *  addParameter and setReturnType SHOULD NOT be called because the
     *  Call object already has the meta data describing the
     *  parameters and return type. If addParameter is called, the specified
     *  parameter is added to the end of the list of parameters.
     */
    public boolean isParameterAndReturnSpecRequired(QName operationName) {
        return parmAndRetReq;
    } // isParameterAndReturnSpecRequired

    /**
     * Adds the specified parameter to the list of parameters for the
     * operation associated with this Call object.
     *
     * Note: Not part of JAX-RPC specification.
     *
     * @param paramName Name that will be used for the parameter in the XML
     * @param xmlType XMLType of the parameter
     * @param parameterMode one of IN, OUT or INOUT
     */
    public void addParameter(QName paramName, QName xmlType,
            ParameterMode parameterMode) {
        Class javaType = null;
        TypeMapping tm = getTypeMapping();
        if (tm != null) {
            javaType = tm.getClassForQName(xmlType);
        }
        addParameter(paramName, xmlType, javaType, parameterMode);
    }

    /**
     * Adds the specified parameter to the list of parameters for the
     * operation associated with this Call object.
     *
     *
     * Note: Not part of JAX-RPC specification.
     *
     * @param paramName Name that will be used for the parameter in the XML
     * @param xmlType XMLType of the parameter
     * @param javaType The Java class of the parameter
     * @param parameterMode one of IN, OUT or INOUT
     */
    public void addParameter(QName paramName, QName xmlType,
            Class javaType, ParameterMode parameterMode) {

        if (operationSetManually) {
            throw new RuntimeException(
                    Messages.getMessage("operationAlreadySet"));
        }

        if (operation == null)
            operation = new OperationDesc();

        // In order to allow any Call to be re-used, Axis
        // chooses to allow parameters to be added when
        // parmAndRetReq==false.  This does not conflict with
        // JSR 101 which indicates an exception MAY be thrown.

        //if (parmAndRetReq) {
        ParameterDesc param = new ParameterDesc();
        param.setQName( paramName );
        param.setTypeQName( xmlType );
        param.setJavaType( javaType );
        byte mode = ParameterDesc.IN;
        if (parameterMode == ParameterMode.INOUT) {
            mode = ParameterDesc.INOUT;
        } else if (parameterMode == ParameterMode.OUT) {
            mode = ParameterDesc.OUT;
        }
        param.setMode(mode);

        operation.addParameter(param);
        parmAndRetReq = true;
        //}
        //else {
        //throw new JAXRPCException(Messages.getMessage("noParmAndRetReq"));
        //}
    }

    /**
     * Adds the specified parameter to the list of parameters for the
     * operation associated with this Call object.
     *
     * @param paramName      Name that will be used for the parameter in the XML
     * @param xmlType      XMLType of the parameter
     * @param parameterMode  one of IN, OUT or INOUT
     */
    public void addParameter(String paramName, QName xmlType,
            ParameterMode parameterMode) {
        Class javaType = null;
        TypeMapping tm = getTypeMapping();
        if (tm != null) {
            javaType = tm.getClassForQName(xmlType);
        }
        addParameter(new QName("", paramName), xmlType,
                     javaType, parameterMode);
    }

    /**
     * Adds a parameter type and mode for a specific operation. Note that the
     * client code is not required to call any addParameter and setReturnType
     * methods before calling the invoke method. A Call implementation class
     * can determine the parameter types by using the Java reflection and
     * configured type mapping registry.
     *
     * @param paramName - Name of the parameter
     * @param xmlType - XML datatype of the parameter
     * @param javaType - The Java class of the parameter
     * @param parameterMode - Mode of the parameter-whether IN, OUT or INOUT
     * @exception JAXRPCException - if isParameterAndReturnSpecRequired returns
     *                              false, then addParameter MAY throw
     *                              JAXRPCException....actually Axis allows
     *                              modification in such cases
     */
    public void addParameter(String paramName, QName xmlType,
                             Class javaType, ParameterMode parameterMode) {
        addParameter(new QName("", paramName), xmlType,
                     javaType, parameterMode);
    }

    /**

     * Adds a parameter type as a soap:header.
     * @param paramName - Name of the parameter
     * @param xmlType - XML datatype of the parameter
     * @param javaType - The Java class of the parameter
     * @param parameterMode - Mode of the parameter-whether IN, OUT or INOUT
     * @param headerMode - Mode of the header.  Even if this is an INOUT
     *                     parameter, it need not be in the header in both
     *                     directions.
     * @exception JAXRPCException - if isParameterAndReturnSpecRequired returns
     *                              false, then addParameter MAY throw
     *                              JAXRPCException....actually Axis allows
     *                              modification in such cases
     */
    public void addParameterAsHeader(QName paramName, QName xmlType,
            Class javaType, ParameterMode parameterMode,
            ParameterMode headerMode) {
        if (operationSetManually) {
            throw new RuntimeException(
                    Messages.getMessage("operationAlreadySet"));
        }

        if (operation == null)
            operation = new OperationDesc();

        ParameterDesc param = new ParameterDesc();
        param.setQName(paramName);
        param.setTypeQName(xmlType);
        param.setJavaType(javaType);
        if (parameterMode == ParameterMode.IN) {
            param.setMode(ParameterDesc.IN);
        }
        else if (parameterMode == ParameterMode.INOUT) {
            param.setMode(ParameterDesc.INOUT);
        }
        else if (parameterMode == ParameterMode.OUT) {
            param.setMode(ParameterDesc.OUT);
        }
        if (headerMode == ParameterMode.IN) {
            param.setInHeader(true);
        }
        else if (headerMode == ParameterMode.INOUT) {
            param.setInHeader(true);
            param.setOutHeader(true);
        }
        else if (headerMode == ParameterMode.OUT) {
            param.setOutHeader(true);
        }
        operation.addParameter(param);
        parmAndRetReq = true;
    } // addParameterAsHeader

    /**
     * Return the QName of the type of the parameters with the given name.
     *
     * @param  paramName  name of the parameter to return
     * @return XMLType    XMLType of paramName, or null if not found.
     */
    public QName getParameterTypeByName(String paramName) {
        QName paramQName = new QName("", paramName);

        return getParameterTypeByQName(paramQName);
    }

    /**
     * Return the QName of the type of the parameters with the given name.
     *
     * Note: Not part of JAX-RPC specification.
     *
     * @param  paramQName  QName of the parameter to return
     * @return XMLType    XMLType of paramQName, or null if not found.
     */
    public QName getParameterTypeByQName(QName paramQName) {
        ParameterDesc param = operation.getParamByQName(paramQName);
        if (param != null) {
            return param.getTypeQName();
        }
        return( null );
    }

    /**
     * Sets the return type of the operation associated with this Call object.
     *
     * @param type QName of the return value type.
     */
    public void setReturnType(QName type) {
        if (operationSetManually) {
            throw new RuntimeException(
                    Messages.getMessage("operationAlreadySet"));
        }

        if (operation == null)
            operation = new OperationDesc();

        // In order to allow any Call to be re-used, Axis
        // chooses to allow setReturnType to be changed when
        // parmAndRetReq==false.  This does not conflict with
        // JSR 101 which indicates an exception MAY be thrown.

        //if (parmAndRetReq) {
        operation.setReturnType(type);
        TypeMapping tm = getTypeMapping();
        operation.setReturnClass(tm.getClassForQName(type));
        parmAndRetReq = true;
        //}
        //else {
        //throw new JAXRPCException(Messages.getMessage("noParmAndRetReq"));
        //}
    }

    /**
     * Sets the return type for a specific operation.
     *
     * @param xmlType - QName of the data type of the return value
     * @param javaType - Java class of the return value
     * @exception JAXRPCException - if isParameterAndReturnSpecRequired returns
     * false, then setReturnType MAY throw JAXRPCException...Axis allows
     * modification without throwing the exception.
     */
    public void setReturnType(QName xmlType, Class javaType) {
        setReturnType(xmlType);
        // Use specified type as the operation return
        operation.setReturnClass(javaType);
    }

    /**
     * Set the return type as a header
     */
    public void setReturnTypeAsHeader(QName xmlType) {
        setReturnType(xmlType);
        operation.setReturnHeader(true);
    } // setReturnTypeAsHeader

    /**
     * Set the return type as a header
     */
    public void setReturnTypeAsHeader(QName xmlType, Class javaType) {
        setReturnType(xmlType, javaType);
        operation.setReturnHeader(true);
    } // setReturnTypeAsHeader

    /**
     * Returns the QName of the type of the return value of this Call - or null
     * if not set.
     *
     * Note: Not part of JAX-RPC specification.
     *
     * @return the XMLType specified for this Call (or null).
     */
    public QName getReturnType() {
        if (operation != null)
            return operation.getReturnType();

        return null;
    }

    /**
     * Set the QName of the return element
     *
     * NOT part of JAX-RPC
     */
    public void setReturnQName(QName qname) {
        if (operationSetManually) {
            throw new RuntimeException(
                    Messages.getMessage("operationAlreadySet"));
        }

        if (operation == null)
            operation = new OperationDesc();

        operation.setReturnQName(qname);
    }
    /**
     * Sets the desired return Java Class.  This is a convenience method
     * which will cause the Call to automatically convert return values
     * into a desired class if possible.  For instance, we return object
     * arrays by default now for SOAP arrays - you could specify:
     *
     * setReturnClass(Vector.class)
     *
     * and you'd get a Vector back from invoke() instead of having to do
     * the conversion yourself.
     *
     * Note: Not part of JAX-RPC specification.  To be JAX-RPC compliant,
     *       use setReturnType(QName, Class).
     *
     * @param cls the desired return class.
     */
    public void setReturnClass(Class cls) {
        if (operationSetManually) {
            throw new RuntimeException(
                    Messages.getMessage("operationAlreadySet"));
        }

        if (operation == null)
            operation = new OperationDesc();

        operation.setReturnClass(cls);
        TypeMapping tm = getTypeMapping();
        operation.setReturnType(tm.getTypeQName(cls));
        parmAndRetReq = true;
    }

    /**
     * Clears the list of parameters.
     * @exception JAXRPCException - if isParameterAndReturnSpecRequired returns
     *  false, then removeAllParameters MAY throw JAXRPCException...Axis allows
     *  modification to the Call object without throwing an exception.
     */
    public void removeAllParameters() {
        //if (parmAndRetReq) {
        operation = new OperationDesc();
        operationSetManually = false;
        parmAndRetReq = true;
        //}
        //else {
        //throw new JAXRPCException(Messages.getMessage("noParmAndRetReq"));
        //}
    }

    /**
     * Returns the operation name associated with this Call object.
     *
     * @return String Name of the operation or null if not set.
     */
    public QName getOperationName() {
        return( operationName );
    }

    /**
     * Sets the operation name associated with this Call object.  This will
     * not check the WSDL (if there is WSDL) to make sure that it's a valid
     * operation name.
     *
     * @param opName Name of the operation.
     */
    public void setOperationName(QName opName) {
        operationName = opName ;
    }

    /**
     * This is a convenience method.  If the user doesn't care about the QName
     * of the operation, the user can call this method, which converts a String
     * operation name to a QName.
     */
    public void setOperationName(String opName) {
        operationName = new QName(opName);
    }

    /**
     * Prefill as much info from the WSDL as it can.
     * Right now it's SOAPAction, operation qname, parameter types
     * and return type of the Web Service.
     *
     * This methods considers that port name and target endpoint address have
     * already been set. This is useful when you want to use the same Call
     * instance for several calls on the same Port
     *
     * Note: Not part of JAX-RPC specification.
     *
     * @param  opName          Operation(method) that's going to be invoked
     * @throws JAXRPCException
     */
    public void setOperation(String opName) {
        if ( service == null ) {
            throw new JAXRPCException( Messages.getMessage("noService04") );
        }

        // remove all settings concerning an operation
        // leave portName and targetEndPoint as they are
        this.setOperationName( opName );
        this.setEncodingStyle( null );
        this.setReturnType( null );
        this.removeAllParameters();

        javax.wsdl.Service wsdlService = service.getWSDLService();
        // Nothing to do is the WSDL is not already set.
        if(wsdlService == null) {
            return;
        }

        Port port = wsdlService.getPort( portName.getLocalPart() );
        if ( port == null ) {
            throw new JAXRPCException( Messages.getMessage("noPort00", "" +
                                                           portName) );
        }

        Binding   binding  = port.getBinding();
        PortType  portType = binding.getPortType();
        if ( portType == null ) {
            throw new JAXRPCException( Messages.getMessage("noPortType00", "" +
                                                           portName) );
        }

        List operations = portType.getOperations();
        if ( operations == null ) {
            throw new JAXRPCException( Messages.getMessage("noOperation01",
                                                           opName) );
        }

        Operation op = null ;
        for ( int i = 0 ; i < operations.size() ; i++, op=null ) {
            op = (Operation) operations.get( i );
            if ( opName.equals( op.getName() ) ) {
                break ;
            }
        }
        if ( op == null ) {
            throw new JAXRPCException( Messages.getMessage("noOperation01",
                                                           opName) );
        }

        // Get the SOAPAction
        ////////////////////////////////////////////////////////////////////
        List list = port.getExtensibilityElements();
        String opStyle = null;
        BindingOperation bop = binding.getBindingOperation(opName,
                                                           null, null);
        if ( bop == null ) {
            throw new JAXRPCException( Messages.getMessage("noOperation02",
                                                            opName ));
        }
        list = bop.getExtensibilityElements();
        for ( int i = 0 ; list != null && i < list.size() ; i++ ) {
            Object obj = list.get(i);
            if ( obj instanceof SOAPOperation ) {
                SOAPOperation sop    = (SOAPOperation) obj ;
                opStyle = ((SOAPOperation) obj).getStyle();
                String action = sop.getSoapActionURI();
                if ( action != null ) {
                    setUseSOAPAction(true);
                    setSOAPActionURI(action);
                }
                else {
                    setUseSOAPAction(false);
                    setSOAPActionURI(null);
                }
                break ;
            }
        }

        // Get the body's namespace URI and encoding style
        ////////////////////////////////////////////////////////////////////
        BindingInput bIn = bop.getBindingInput();
        if ( bIn != null ) {
            list = bIn.getExtensibilityElements();
            for ( int i = 0 ; list != null && i < list.size() ; i++ ) {
                Object obj = list.get(i);
                if( obj instanceof MIMEMultipartRelated){
                  MIMEMultipartRelated mpr=(MIMEMultipartRelated) obj;
                  Object part= null;
                  List l=  mpr.getMIMEParts();
                  for(int j=0; l!= null && j< l.size() && part== null; j++){
                     MIMEPart mp = (MIMEPart)l.get(j);
                     List ll= mp.getExtensibilityElements();
                     for(int k=0; ll != null && k < ll.size() && part == null;
                           k++){
                       part= ll.get(k);
                       if ( !(part instanceof SOAPBody)) {
                           part = null;
                       }
                     }
                  }
                  if(null != part) {
                      obj= part;
                  }
                }

                if ( obj instanceof SOAPBody ) {
                    SOAPBody sBody  = (SOAPBody) obj ;
                    list = sBody.getEncodingStyles();
                    if ( list != null && list.size() > 0 ) {
                        this.setEncodingStyle( (String) list.get(0) );
                    }
                    String ns = sBody.getNamespaceURI();
                    if (ns != null && !ns.equals("")) {
                      setOperationName( new QName( ns, opName ) );
                    }
                    break ;
                }
            }
        }

        Service service = this.getService();
        SymbolTable symbolTable = service.getWSDLParser().getSymbolTable();
        BindingEntry bEntry = symbolTable.getBindingEntry(binding.getQName());
        Parameters parameters = bEntry.getParameters(bop.getOperation());

        // loop over paramters and set up in/out params
        for (int j = 0; j < parameters.list.size(); ++j) {
            Parameter p = (Parameter) parameters.list.get(j);
            // Get the QName representing the parameter type
            QName paramType = Utils.getXSIType(p);
            this.addParameter( p.getQName(), paramType, modes[p.getMode()]);
        }

        Map faultMap = bEntry.getFaults();
        // Get the list of faults for this operation
        ArrayList faults = (ArrayList) faultMap.get(bop);

        // check for no faults
        if (faults == null) {
            return;
        }
        // For each fault, register its information
        for (Iterator faultIt = faults.iterator(); faultIt.hasNext();) {
            FaultInfo info = (FaultInfo) faultIt.next();
            QName qname = info.getQName();
            info.getMessage();

            // if no parts in fault, skip it!
            if (qname == null) {
                continue;
            }

            QName xmlType = info.getXMLType();
            Class clazz = getTypeMapping().getClassForQName(xmlType);
            if (clazz != null) {
                addFault(qname, clazz, xmlType, true);
            } else {
                //we cannot map from the info to a java class
                //In Axis1.1 and before this was silently swallowed. Now we log it

                log.debug(Messages.getMessage("clientNoTypemapping", xmlType.toString()));
            }
        }

        // set output type
        if (parameters.returnParam != null) {
            // Get the QName for the return Type
            QName returnType = Utils.getXSIType(parameters.returnParam);
            QName returnQName = parameters.returnParam.getQName();

            // Get the javaType
            String javaType = null;
            if (parameters.returnParam.getMIMEInfo() != null) {
                javaType = "javax.activation.DataHandler";
            }
            else {
                javaType = parameters.returnParam.getType().getName();
            }
            if (javaType == null) {
                javaType = "";
            }
            else {
                javaType = javaType + ".class";
            }
            this.setReturnType(returnType);
            try {
                Class clazz = ClassUtils.forName(javaType);
                this.setReturnClass(clazz);
            } catch (ClassNotFoundException swallowedException) {
                //log that this lookup failed,
                log.debug(Messages.getMessage("clientNoReturnClass",
                        javaType));
            }
            this.setReturnQName(returnQName);
        }
        else {
            this.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        }

        boolean hasMIME = Utils.hasMIME(bEntry, bop);
        Use use = bEntry.getInputBodyType(bop.getOperation());
        Style style = Style.getStyle(opStyle, bEntry.getBindingStyle());
        if (use == Use.LITERAL) {
            // Turn off encoding
            setEncodingStyle(null);
            // turn off XSI types
            setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        }
        if (hasMIME || use == Use.LITERAL) {
            // If it is literal, turn off multirefs.
            //
            // If there are any MIME types, turn off multirefs.
            // I don't know enough about the guts to know why
            // attachments don't work with multirefs, but they don't.
            setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        }

        if (style == Style.DOCUMENT && symbolTable.isWrapped()) {
            style = Style.WRAPPED;
        }

        // Operation name
        if (style == Style.WRAPPED) {
            // We need to make sure the operation name, which is what we
            // wrap the elements in, matches the Qname of the parameter
            // element.
            Map partsMap = bop.getOperation().getInput().getMessage().getParts();
            Part p = (Part)partsMap.values().iterator().next();
            QName q = p.getElementName();
            setOperationName(q);
        } else {
            QName elementQName =
                Utils.getOperationQName(bop, bEntry, symbolTable);
            if (elementQName != null) {
                setOperationName(elementQName);
            }
        }

        // Indicate that the parameters and return no longer
        // need to be specified with addParameter calls.
        parmAndRetReq = false;
        return;

    }


    /**
     * prefill as much info from the WSDL as it can.
     * Right now it's target URL, SOAPAction, Parameter types,
     * and return type of the Web Service.
     *
     * If wsdl is not present, this function set port name and operation name
     * and does not modify target endpoint address.
     *
     * Note: Not part of JAX-RPC specification.
     *
     * @param  portName        PortName in the WSDL doc to search for
     * @param  opName          Operation(method) that's going to be invoked
     */
    public void setOperation(QName portName, String opName) {
        if ( service == null )
            throw new JAXRPCException( Messages.getMessage("noService04") );

        // Make sure we're making a fresh start.
        this.setPortName( portName );
        this.setOperationName( opName );
        this.setEncodingStyle( null );
        this.setReturnType( null );
        this.removeAllParameters();

        javax.wsdl.Service wsdlService = service.getWSDLService();
        // Nothing to do is the WSDL is not already set.
        if(wsdlService == null) {
            return;
        }

        // we reinitialize target endpoint only if we have wsdl
        this.setTargetEndpointAddress( (URL) null );

        Port port = wsdlService.getPort( portName.getLocalPart() );
        if ( port == null ) {
            throw new JAXRPCException( Messages.getMessage("noPort00", "" +
                                                           portName) );
        }

        Binding   binding  = port.getBinding();
        PortType  portType = binding.getPortType();
        if ( portType == null ) {
            throw new JAXRPCException( Messages.getMessage("noPortType00", "" +
                                                           portName) );
        }

        // Get the URL
        ////////////////////////////////////////////////////////////////////
        List list = port.getExtensibilityElements();
        for ( int i = 0 ; list != null && i < list.size() ; i++ ) {
            Object obj = list.get(i);
            if ( obj instanceof SOAPAddress ) {
                try {
                    SOAPAddress addr = (SOAPAddress) obj ;
                    URL         url  = new URL(addr.getLocationURI());
                    this.setTargetEndpointAddress(url);
                }
                catch(Exception exp) {
                    throw new JAXRPCException(
                            Messages.getMessage("cantSetURI00", "" + exp) );
                }
            }
        }

        // Get the SOAPAction
        ////////////////////////////////////////////////////////////////////
        BindingOperation bop = binding.getBindingOperation(opName,
                                                           null, null);
        if ( bop == null ) {
            throw new JAXRPCException( Messages.getMessage("noOperation02",
                                                            opName ));
        }
        list = bop.getExtensibilityElements();
        for ( int i = 0 ; list != null && i < list.size() ; i++ ) {
            Object obj = list.get(i);
            if ( obj instanceof SOAPOperation ) {
                SOAPOperation sop = (SOAPOperation) obj ;
                String action = sop.getSoapActionURI();
                if ( action != null ) {
                    setUseSOAPAction(true);
                    setSOAPActionURI(action);
                }
                else {
                    setUseSOAPAction(false);
                    setSOAPActionURI(null);
                }
                break ;
            }
        }
        setOperation(opName);
    }

    /**
     * Returns the fully qualified name of the port for this Call object
     * (if there is one).
     *
     * @return QName Fully qualified name of the port (or null if not set)
     */
    public QName getPortName() {
        return( portName );
    } // getPortName

    /**
     * Sets the port name of this Call object.  This call will not set
     * any additional fields, nor will it do any checking to verify that
     * this port name is actually defined in the WSDL - for now anyway.
     *
     * @param portName Fully qualified name of the port
     */
    public void setPortName(QName portName) {
        this.portName = portName;
    } // setPortName

    /**
     * Returns the fully qualified name of the port for this Call object
     * (if there is one).
     *
     * @return QName Fully qualified name of the port
     *
     * @deprecated This is really the service's port name, not portType name.
     *            Use getPortName instead.
     */
    public QName getPortTypeName() {
        return portName == null ? new QName("") : portName;
    }

    /**
     * Sets the port name of this Call object.  This call will not set
     * any additional fields, nor will it do any checking to verify that
     * this port type is actually defined in the WSDL - for now anyway.
     *
     * @param portType Fully qualified name of the portType
     *
     * @deprecated This is really the service's port name, not portType name.
     *            Use setPortName instead.
     */
    public void setPortTypeName(QName portType) {
        setPortName(portType);
    }

    /**
     * Allow the user to set the default SOAP version.  For SOAP 1.2, pass
     * SOAPConstants.SOAP12_CONSTANTS.
     *
     * @param soapConstants the SOAPConstants object representing the correct
     *                      version
     */
    public void setSOAPVersion(SOAPConstants soapConstants) {
        msgContext.setSOAPConstants(soapConstants);
    }

    /**
     * Invokes a specific operation using a synchronous request-response interaction mode. The invoke method takes
     * as parameters the object values corresponding to these defined parameter types. Implementation of the invoke
     * method must check whether the passed parameter values correspond to the number, order and types of parameters
     * specified in the corresponding operation specification.
     *
     * @param operationName - Name of the operation to invoke
     * @param params  - Parameters for this invocation
     *
     * @return the value returned from the other end.
     *
     * @throws java.rmi.RemoteException - if there is any error in the remote method invocation or if the Call
     * object is not configured properly.
     */
    public Object invoke(QName operationName, Object[] params)
      throws java.rmi.RemoteException {
        QName origOpName = this.operationName;
        this.operationName = operationName;
        try {
            return this.invoke(params);
        }
        catch (AxisFault af) {
            this.operationName = origOpName;
            if(af.detail != null && af.detail instanceof RemoteException) {
                throw ((RemoteException)af.detail);
            }
            throw af;
        }
        catch (java.rmi.RemoteException re) {
            this.operationName = origOpName;
            throw re;
        }
        catch (RuntimeException re) {
            this.operationName = origOpName;
            throw re;
        }
        catch (Error e) {
            this.operationName = origOpName;
            throw e;
        }
    } // invoke

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
     * @throws java.rmi.RemoteException if there's an error
     */
    public Object invoke(Object[] params) throws java.rmi.RemoteException {
        long t0=0, t1=0;
        if( tlog.isDebugEnabled() ) {
            t0=System.currentTimeMillis();
        }
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
            isMsg = true ;
            env = new SOAPEnvelope(msgContext.getSOAPConstants(),
                                   msgContext.getSchemaVersion());

            for (i = 0; i < params.length; i++) {
                env.addBodyElement((SOAPBodyElement) params[i]);
            }

            Message msg = new Message( env );
            setRequestMessage(msg);

            invoke();

            msg = msgContext.getResponseMessage();
            if (msg == null) {
              if (msgContext.isPropertyTrue(FAULT_ON_NO_RESPONSE, false)) {
                throw new AxisFault(Messages.getMessage("nullResponse00"));
              } else {
                return null;
              }
            }

            env = msg.getSOAPEnvelope();
            return( env.getBodyElements() );
        }


        if ( operationName == null ) {
            throw new AxisFault( Messages.getMessage("noOperation00") );
        }
        try {
            Object res=this.invoke(operationName.getNamespaceURI(),
                    operationName.getLocalPart(), params);
            if( tlog.isDebugEnabled() ) {
                t1=System.currentTimeMillis();
                tlog.debug("axis.Call.invoke: " + (t1-t0)  + " " + operationName);
            }
            return res;
        }
        catch( AxisFault af) {
            if(af.detail != null && af.detail instanceof RemoteException) {
                throw ((RemoteException)af.detail);
            }
            throw af;
        }
        catch( Exception exp ) {
            entLog.debug(Messages.getMessage("toAxisFault00"), exp);
            throw AxisFault.makeFault(exp);
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
    public void invokeOneWay(Object[] params) {
        try {
            invokeOneWay = true;
            invoke( params );
        } catch( Exception exp ) {
            throw new JAXRPCException( exp.toString() );
        } finally {
            invokeOneWay = false;
        }
    }

    /************************************************************************/
    /* End of core JAX-RPC stuff                                            */
    /************************************************************************/

    /**
     * Invoke the service with a custom Message.
     * This method simplifies invoke(SOAPEnvelope).
     * <p>
     * Note: Not part of JAX-RPC specification.
     *
     * @param msg a Message to send
     * @throws AxisFault if there is any failure
     */
    public SOAPEnvelope invoke(Message msg) throws AxisFault {
        try {
            setRequestMessage( msg );
            invoke();
            msg = msgContext.getResponseMessage();
            if (msg == null) {
                if (msgContext.isPropertyTrue(FAULT_ON_NO_RESPONSE, false)) {
                    throw new AxisFault(Messages.getMessage("nullResponse00"));
                } else {
                    return null;
                }
            }
            SOAPEnvelope res = null;
            res = msg.getSOAPEnvelope();
            return res;
        }
        catch (Exception exp) {
            if (exp instanceof AxisFault) {
                throw (AxisFault) exp ;
            }
            entLog.debug(Messages.getMessage("toAxisFault00"), exp);
            throw new AxisFault(
                    Messages.getMessage("errorInvoking00", "\n" + exp));
        }
    }

    /**
     * Invoke the service with a custom SOAPEnvelope.
     * <p>
     * Note: Not part of JAX-RPC specification.
     *
     * @param env a SOAPEnvelope to send
     * @throws AxisFault if there is any failure
     */
    public SOAPEnvelope invoke(SOAPEnvelope env) throws AxisFault {
        try {
            Message msg = null ;

            msg = new Message( env );
            if (msgContext.getProperty(SOAPMessage.CHARACTER_SET_ENCODING) != null) {
                msg.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, msgContext.getProperty(SOAPMessage.CHARACTER_SET_ENCODING));
             }
            setRequestMessage( msg );
            invoke();
            msg = msgContext.getResponseMessage();
            if (msg == null) {
              if (msgContext.isPropertyTrue(FAULT_ON_NO_RESPONSE, false)) {
                throw new AxisFault(Messages.getMessage("nullResponse00"));
              } else {
                return null;
              }
            }
            return( msg.getSOAPEnvelope() );
        }
        catch( Exception exp ) {
            if ( exp instanceof AxisFault ) {
                throw (AxisFault) exp ;
            }

            entLog.debug(Messages.getMessage("toAxisFault00"), exp);
            throw AxisFault.makeFault(exp);
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
        if (Transport.class.isAssignableFrom(transportClass)) {
            transports.put(protocol, transportClass);
        }
        else {
            throw new InternalException(transportClass.toString());
        }
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
        addTransportPackage("org.apache.axis.transport");

        setTransportForProtocol("java",
                org.apache.axis.transport.java.JavaTransport.class);
        setTransportForProtocol("local",
                org.apache.axis.transport.local.LocalTransport.class);
        setTransportForProtocol("http", HTTPTransport.class);
        setTransportForProtocol("https", HTTPTransport.class);
    }

    /**
     * Cache of transport packages we've already added to the system
     * property.
     */
    private static ArrayList transportPackages = null;

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
        if (transportPackages == null) {
            transportPackages = new ArrayList();
            String currentPackages =
                    AxisProperties.getProperty(TRANSPORT_PROPERTY);
            if (currentPackages != null) {
                StringTokenizer tok = new StringTokenizer(currentPackages,
                                                          "|");
                while (tok.hasMoreTokens()) {
                    transportPackages.add(tok.nextToken());
                }
            }
        }

        if (transportPackages.contains(packageName)) {
            return;
        }

        transportPackages.add(packageName);

        StringBuffer currentPackages = new StringBuffer();
        for (Iterator i = transportPackages.iterator(); i.hasNext();) {
            String thisPackage = (String) i.next();
            currentPackages.append(thisPackage);
            currentPackages.append('|');
        }

        System.setProperty(TRANSPORT_PROPERTY, currentPackages.toString());
    }

    /**
     * Convert the list of objects into RPCParam's based on the paramNames,
     * paramXMLTypes and paramModes variables.  If those aren't set then just
     * return what was passed in.
     *
     * @param  params   Array of parameters to pass into the operation/method
     * @return Object[] Array of parameters to pass to invoke()
     */
    private Object[] getParamList(Object[] params) {
        int  numParams = 0 ;

        // If we never set-up any names... then just return what was passed in
        //////////////////////////////////////////////////////////////////////
        if (log.isDebugEnabled()) {
            log.debug( "operation=" + operation);
            if (operation != null) {
                log.debug("operation.getNumParams()=" +
                          operation.getNumParams());
            }
        }
        if ( operation == null || operation.getNumParams() == 0 ) {
            return( params );
        }

        // Count the number of IN and INOUT params, this needs to match the
        // number of params passed in - if not throw an error
        /////////////////////////////////////////////////////////////////////
        numParams = operation.getNumInParams();

        if ( params == null || numParams != params.length ) {
            throw new JAXRPCException(
                    Messages.getMessage("parmMismatch00",
                    "" + params.length, "" + numParams) );
        }

        log.debug( "getParamList number of params: " + params.length);

        // All ok - so now produce an array of RPCParams
        //////////////////////////////////////////////////
        Vector result = new Vector();
        int    j = 0 ;
        ArrayList parameters = operation.getParameters();

        for (int i = 0; i < parameters.size(); i++) {
            ParameterDesc param = (ParameterDesc)parameters.get(i);
            if (param.getMode() != ParameterDesc.OUT) {
                QName paramQName = param.getQName();

                // Create an RPCParam if param isn't already an RPCParam.
                RPCParam rpcParam = null;
                Object p = params[j++];
                if(p instanceof RPCParam) {
                    rpcParam = (RPCParam)p;
                } else {
                    rpcParam = new RPCParam(paramQName.getNamespaceURI(),
                                            paramQName.getLocalPart(),
                                            p);
                }
                // Attach the ParameterDescription to the RPCParam
                // so that the serializer can use the (javaType, xmlType)
                // information.
                rpcParam.setParamDesc(param);

                // Add the param to the header or vector depending
                // on whether it belongs in the header or body.
                if (param.isInHeader()) {
                    addHeader(new RPCHeaderParam(rpcParam));
                } else {
                    result.add(rpcParam);
                }
            }
        }
        return( result.toArray() );
    }

    /**
     * Set the Transport
     *
     * Note: Not part of JAX-RPC specification.
     *
     * @param trans the Transport object we'll use to set up
     *                  MessageContext properties.
     */
    public void setTransport(Transport trans) {
        transport = trans;
        if (log.isDebugEnabled())
            log.debug(Messages.getMessage("transport00", "" + transport));
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
     * @throws RuntimeException containing the text of an AxisFault, if any
     * AxisFault was thrown
     */
    public void setRequestMessage(Message msg) {
         String attachformat= (String)getProperty(
           ATTACHMENT_ENCAPSULATION_FORMAT);

         if(null != attachformat) {
              Attachments attachments=msg.getAttachmentsImpl();
              if(null != attachments) {
                  if( ATTACHMENT_ENCAPSULATION_FORMAT_MIME.equals(attachformat)) {
                    attachments.setSendType(Attachments.SEND_TYPE_MIME);
                  } else if ( ATTACHMENT_ENCAPSULATION_FORMAT_DIME.equals(attachformat)) {
                    attachments.setSendType(Attachments.SEND_TYPE_DIME);
                  }
              }
         }

        if(null != attachmentParts && !attachmentParts.isEmpty()){
            try{
                Attachments attachments= msg.getAttachmentsImpl();
                if(null == attachments) {
                  throw new RuntimeException(
                          Messages.getMessage("noAttachments"));
                }

                attachments.setAttachmentParts(attachmentParts);
            }catch(AxisFault ex){
              log.info(Messages.getMessage("axisFault00"), ex);
              throw new RuntimeException(ex.getMessage());
            }
        }

        msgContext.setRequestMessage(msg);
        attachmentParts.clear();
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
     * @param header a SOAPHeaderElement to be inserted into messages
     */
    public void addHeader(SOAPHeaderElement header)
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

    public TypeMapping getTypeMapping()
    {
        // Get the TypeMappingRegistry
        TypeMappingRegistry tmr = msgContext.getTypeMappingRegistry();

        // If a TypeMapping is not available, add one.
        return tmr.getOrMakeTypeMapping(getEncodingStyle());
    }

    /**
     * Register type mapping information for serialization/deserialization
     *
     * Note: Not part of JAX-RPC specification.
     *
     * @param javaType is  the Java class of the data type.
     * @param xmlType the xsi:type QName of the associated XML type.
     * @param sf/df are the factories (or the Class objects of the factory).
     */
    public void registerTypeMapping(Class javaType, QName xmlType,
                                    SerializerFactory sf,
                                    DeserializerFactory df) {
        registerTypeMapping(javaType, xmlType, sf, df, true);
    }

    /**
     * Register type mapping information for serialization/deserialization
     *
     * Note: Not part of JAX-RPC specification.
     *
     * @param javaType is  the Java class of the data type.
     * @param xmlType the xsi:type QName of the associated XML type.
     * @param sf/df are the factories (or the Class objects of the factory).
     * @param force Indicates whether to add the information if already registered.
     */
    public void registerTypeMapping(Class javaType, QName xmlType,
                                    SerializerFactory sf,
                                    DeserializerFactory df,
                                    boolean force) {
        TypeMapping tm = getTypeMapping();
        if (!force && tm.isRegistered(javaType, xmlType)) {
            return;
        }

        // Register the information
        tm.register(javaType, xmlType, sf, df);
    }

    /**
     * register this type matting
     * @param javaType
     * @param xmlType
     * @param sfClass
     * @param dfClass
     */
    public void registerTypeMapping(Class javaType, QName xmlType,
                                    Class sfClass, Class dfClass) {
        registerTypeMapping(javaType, xmlType, sfClass, dfClass, true);
    }

    /**
     * register a type. This only takes place if either the serializer or
     * deserializer factory could be created.
     * @param javaType java type to handle
     * @param xmlType XML mapping
     * @param sfClass class of serializer factory
     * @param dfClass class of deserializer factory
     * @param force
     */
    public void registerTypeMapping(Class javaType,
                                    QName xmlType,
                                    Class sfClass,
                                    Class dfClass,
                                    boolean force) {
        // Instantiate the factory using introspection.
        SerializerFactory   sf =
                BaseSerializerFactory.createFactory(sfClass, javaType, xmlType);
        DeserializerFactory df =
                BaseDeserializerFactory.createFactory(dfClass,
                                                      javaType,
                                                      xmlType);
        if (sf != null || df != null) {
            registerTypeMapping(javaType, xmlType, sf, df, force);
        }
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

        if (log.isDebugEnabled()) {
            log.debug("Enter: Call::invoke(ns, meth, args)");
        }

        /**
         * Since JAX-RPC requires us to specify all or nothing, if setReturnType
         * was called (returnType != null) and we have args but addParameter
         * wasn't called (paramXMLTypes == null), then toss a fault.
         */
        if (getReturnType() != null && args != null && args.length != 0
                && operation.getNumParams() == 0) {
            throw new AxisFault(Messages.getMessage("mustSpecifyParms"));
        }

        RPCElement body = new RPCElement(namespace, method, getParamList(args));

        Object ret = invoke( body );

        if (log.isDebugEnabled()) {
            log.debug("Exit: Call::invoke(ns, meth, args)");
        }

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
        if (log.isDebugEnabled()) {
            log.debug("Enter: Call::invoke(RPCElement)");
        }

        /**
         * Since JAX-RPC requires us to specify a return type if we've set
         * parameter types, check for this case right now and toss a fault
         * if things don't look right.
         */
        if (!invokeOneWay && operation != null &&
                operation.getNumParams() > 0 && getReturnType() == null) {
            // TCK:
            // Issue an error if the return type was not set, but continue processing.
            //throw new AxisFault(Messages.getMessage("mustSpecifyReturnType"));
            log.error(Messages.getMessage("mustSpecifyReturnType"));
        }

        SOAPEnvelope         reqEnv =
                new SOAPEnvelope(msgContext.getSOAPConstants(),
                                 msgContext.getSchemaVersion());
        SOAPEnvelope         resEnv = null ;
        Message              reqMsg = new Message( reqEnv );
        Message              resMsg = null ;
        Vector               resArgs = null ;
        Object               result = null ;

        // Clear the output params
        outParams = new HashMap();
        outParamsList = new ArrayList();

        // Set both the envelope and the RPCElement encoding styles
        try {
            body.setEncodingStyle(getEncodingStyle());

            setRequestMessage(reqMsg);

            reqEnv.addBodyElement(body);
            reqEnv.setMessageType(Message.REQUEST);

            invoke();
        } catch (Exception e) {
            entLog.debug(Messages.getMessage("toAxisFault00"), e);
            throw AxisFault.makeFault(e);
        }

        resMsg = msgContext.getResponseMessage();

        if (resMsg == null) {
          if (msgContext.isPropertyTrue(FAULT_ON_NO_RESPONSE, false)) {
            throw new AxisFault(Messages.getMessage("nullResponse00"));
          } else {
            return null;
          }
        }

        resEnv = resMsg.getSOAPEnvelope();
        SOAPBodyElement bodyEl = resEnv.getFirstBody();
        if (bodyEl == null) {
            return null;
        }

        if (bodyEl instanceof RPCElement) {
            try {
                resArgs = ((RPCElement) bodyEl).getParams();
            } catch (Exception e) {
                log.error(Messages.getMessage("exception00"), e);
                throw AxisFault.makeFault(e);
            }

            if (resArgs != null && resArgs.size() > 0) {

                // If there is no return, then we start at index 0 to create the outParams Map.
                // If there IS a return, then we start with 1.
                int outParamStart = 0;

                // If we have resArgs and the returnType is specified, then the first
                // resArgs is the return.  If we have resArgs and neither returnType
                // nor paramXMLTypes are specified, then we assume that the caller is
                // following the non-JAX-RPC AXIS shortcut of not having to specify
                // the return, in which case we again assume the first resArgs is
                // the return.
                // NOTE 1:  the non-JAX-RPC AXIS shortcut allows a potential error
                // to escape notice.  If the caller IS NOT following the non-JAX-RPC
                // shortcut but instead intentionally leaves returnType and params
                // null (ie., a method that takes no parameters and returns nothing)
                // then, if we DO receive something it should be an error, but this
                // code passes it through.  The ideal solution here is to require
                // this caller to set the returnType to void, but there's no void
                // type in XML.
                // NOTE 2:  we should probably verify that the resArgs element
                // types match the expected returnType and paramXMLTypes, but I'm not
                // sure how to do that since the resArgs value is a Java Object
                // and the returnType and paramXMLTypes are QNames.

                // GD 03/15/02 : We're now checking for invalid metadata
                // config at the top of this method, so don't need to do it
                // here.  Check for void return, though.

                boolean findReturnParam = false;
                QName returnParamQName = null;
                if (operation != null) {
                    returnParamQName = operation.getReturnQName();
                }

                if (!XMLType.AXIS_VOID.equals(getReturnType())) {
                    if (returnParamQName == null) {
                        // Assume the first param is the return
                        RPCParam param = (RPCParam)resArgs.get(0);
                        result = param.getValue();
                        outParamStart = 1;
                    } else {
                        // If the QName of the return value was given to us, look
                        // through the result arguments to find the right name
                        findReturnParam = true;
                    }
                }

                // The following loop looks at the resargs and
                // converts the value to the appropriate return/out parameter
                // value.  If the return value is found, is value is
                // placed in result.  The remaining resargs are
                // placed in the outParams list (note that if a resArg
                // is found that does not match a operation parameter qname,
                // it is still placed in the outParms list).
                for (int i = outParamStart; i < resArgs.size(); i++) {
                    RPCParam param = (RPCParam) resArgs.get(i);

                    Class javaType = getJavaTypeForQName(param.getQName());
                    Object value = param.getValue();

                    // Convert type if needed
                    if (javaType != null && value != null &&
                           !javaType.isAssignableFrom(value.getClass())) {
                        value = JavaUtils.convert(value, javaType);
                    }

                    // Check if this parameter is our return
                    // otherwise just add it to our outputs
                    if (findReturnParam &&
                          returnParamQName.equals(param.getQName())) {
                        // found it!
                        result = value;
                        findReturnParam = false;
                    } else {
                        outParams.put(param.getQName(), value);
                        outParamsList.add(value);
                    }
                }

                // added by scheu:
                // If the return param is still not found, that means
                // the returned value did not have the expected qname.
                // The soap specification indicates that this should be
                // accepted (and we also fail interop tests if we are strict here).
                // Look through the outParms and find one that
                // does not match one of the operation parameters.
                if (findReturnParam) {
                    Iterator it = outParams.keySet().iterator();
                    while (it.hasNext() && findReturnParam) {
                        QName qname = (QName) it.next();
                        ParameterDesc paramDesc =
                            operation.getOutputParamByQName(qname);
                        if (paramDesc == null) {
                            // Doesn't match a paramter, so use this for the return
                            findReturnParam = false;
                            result = outParams.remove(qname);
                        }
                    }
                }

                // If we were looking for a particular QName for the return and
                // still didn't find it, throw an exception
                if (findReturnParam) {
                    String returnParamName = returnParamQName.toString();
                    throw new AxisFault(Messages.getMessage("noReturnParam",
                                                            returnParamName));
                }
            }
        } else {
            // This is a SOAPBodyElement, try to treat it like a return value
            try {
                result = bodyEl.getValueAsType(getReturnType());
            } catch (Exception e) {
                // just return the SOAPElement
                result = bodyEl;
            }

        }

        if (log.isDebugEnabled()) {
            log.debug("Exit: Call::invoke(RPCElement)");
        }

        // Convert type if needed
        if (operation != null && operation.getReturnClass() != null) {
            result = JavaUtils.convert(result, operation.getReturnClass());
        }

        return( result );
    }

    /**
     * Get the javaType for a given parameter.
     *
     * @param name  the QName of the parameter
     * @return the class associated with that parameter
     */
    private Class getJavaTypeForQName(QName name) {
        if (operation == null) {
            return null;
        }
        ParameterDesc param = operation.getOutputParamByQName(name);
        return param == null ? null : param.getJavaType();
    }

    /**
     * Set engine option.
     *
     * Note: Not part of JAX-RPC specification.
     */
    public void setOption(String name, Object value) {
        service.getEngine().setOption(name, value);
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
        if (log.isDebugEnabled()) {
            log.debug("Enter: Call::invoke()");
        }

        Message      reqMsg  = null ;
        SOAPEnvelope reqEnv  = null ;

        msgContext.reset();
        msgContext.setResponseMessage(null);
        msgContext.setProperty( MessageContext.CALL, this );
        msgContext.setProperty( WSDL_SERVICE, service );
        msgContext.setProperty( WSDL_PORT_NAME, getPortName() );
        if ( isMsg ) {
            msgContext.setProperty( MessageContext.IS_MSG, "true" );
        }

        if (username != null) {
            msgContext.setUsername(username);
        }
        if (password != null) {
            msgContext.setPassword(password);
        }
        msgContext.setMaintainSession(maintainSession);

        if (operation != null) {
            msgContext.setOperation(operation);
            operation.setStyle(getOperationStyle());
            operation.setUse(getOperationUse());
        }

        if (useSOAPAction) {
            msgContext.setUseSOAPAction(true);
        }
        if (SOAPActionURI != null) {
            msgContext.setSOAPActionURI(SOAPActionURI);
        }
        if (timeout != null) {
            msgContext.setTimeout(timeout.intValue());
        }

        // Determine client target service
        if (myService != null) {
            // If we have a SOAPService kicking around, use that directly
            msgContext.setService(myService);
        } else {
            if (portName != null) {
                // No explicit service.  If we have a target service name,
                // try that.
                msgContext.setTargetService(portName.getLocalPart());
            } else {
                // No direct config, so try the namespace of the first body.
                reqMsg = msgContext.getRequestMessage();

                if (reqMsg != null) {
                    reqEnv = reqMsg.getSOAPEnvelope();

                    SOAPBodyElement body = reqEnv.getFirstBody();

                    if (body != null) {
                        if ( body.getNamespaceURI() == null ) {
                            throw new AxisFault("Call.invoke",
                                                Messages.getMessage("cantInvoke00", body.getName()),
                                                null, null);
                        } else {
                            msgContext.setTargetService(body.getNamespaceURI());
                        }
                    }
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug(Messages.getMessage("targetService",
                    msgContext.getTargetService()));
        }

        Message requestMessage = msgContext.getRequestMessage();
        if (requestMessage != null) {
            try {
                msgContext.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, requestMessage.getProperty(SOAPMessage.CHARACTER_SET_ENCODING));
            } catch (SOAPException e) {
            }
            reqEnv = requestMessage.getSOAPEnvelope();

            // If we have headers to insert, do so now.
            for (int i = 0 ; myHeaders != null && i < myHeaders.size() ; i++ ) {
                reqEnv.addHeader((SOAPHeaderElement)myHeaders.get(i));
            }
        }

        // set up transport if there is one
        if (transport != null) {
            transport.setupMessageContext(msgContext, this, service.getEngine());
        }
        else {
            msgContext.setTransportName( transportName );
        }

        SOAPService svc = msgContext.getService();
        if (svc != null) {
            svc.setPropertyParent(myProperties);
        } else {
            msgContext.setPropertyParent(myProperties);
        }

        // For debugging - print request message
        if (log.isDebugEnabled()) {
            StringWriter writer = new StringWriter();
            try {
                SerializationContext ctx = new SerializationContextImpl(writer,
                                                                   msgContext);
                reqEnv.output(ctx);
                writer.close();
            } catch (Exception e) {
                log.debug(Messages.getMessage("exceptionPrinting"), e);
            } finally {
                log.debug(writer.getBuffer().toString());
            }
        }

        if(!invokeOneWay) {
            invokeEngine(msgContext);
        } else {
            invokeEngineOneWay(msgContext);
        }

        if (log.isDebugEnabled()) {
            log.debug("Exit: Call::invoke()");
        }
    }

    /**
     * Invoke the message on the current engine and do not wait for a response.
     *
     * @param msgContext  the <code>MessageContext</code> to use
     * @throws AxisFault if the invocation raised a fault
     */
    private void invokeEngine(MessageContext msgContext) throws AxisFault {
        service.getEngine().invoke( msgContext );

        if (transport != null) {
            transport.processReturnedMessageContext(msgContext);
        }

        Message resMsg = msgContext.getResponseMessage();

        if (resMsg == null) {
          if (msgContext.isPropertyTrue(FAULT_ON_NO_RESPONSE, false)) {
            throw new AxisFault(Messages.getMessage("nullResponse00"));
          } else {
            return;
          }
        }

        /** This must happen before deserialization...
         */
        resMsg.setMessageType(Message.RESPONSE);

        SOAPEnvelope resEnv = resMsg.getSOAPEnvelope();

        SOAPBodyElement respBody = resEnv.getFirstBody();
        if (respBody instanceof SOAPFault) {
            //we got a fault
            if(operation == null ||
                    operation.getReturnClass() == null ||
                    operation.getReturnClass() !=
                        javax.xml.soap.SOAPMessage.class) {
                //unless we don't care about the return value or we want
                //a raw message back
                //get the fault from the body and throw it
                throw ((SOAPFault)respBody).getFault();
            }
        }
    }

    /**
     * Implement async invocation by running the request in a new thread
     * @todo this is not a good way to do stuff, as it has no error reporting facility
     * @param msgContext
     */
    private void invokeEngineOneWay(final MessageContext msgContext) {
        //create a new class
        Runnable runnable = new Runnable(){
            public void run() {
                try {
                    service.getEngine().invoke( msgContext );
                } catch (AxisFault af){
                    //TODO: handle errors properly
                    log.debug(Messages.getMessage("exceptionPrinting"), af);
                }
            }
        };
        //create a thread to run it
        Thread thread = new Thread(runnable);
        //run it
        thread.start();
    }

    /**
     * Get the output parameters (if any) from the last invocation.
     *
     * NOTE that the params returned are all RPCParams, containing
     * name and value - if you want the value, you'll need to call
     * param.getValue().
     *
     * @return Vector of RPCParams
     */
    public Map getOutputParams()
    {
        return this.outParams;
    }

    /**
     * Returns a List values for the output parameters of the last
     * invoked operation.
     *
     * @return Values for the output parameters. An empty List is
     *         returned if there are no output values.
     *
     * @throws JAXRPCException - If this method is invoked for a
     *                           one-way operation or is invoked
     *                           before any invoke method has been called.
     */
    public List getOutputValues() {
        return outParamsList;
    }

    /**
     * Get the Service object associated with this Call object.
     *
     * Note: Not part of JAX-RPC specification.
     *
     * @return Service the Service object this Call object is associated with
     */
    public Service getService()
    {
        return this.service;
    }

    /**
     *
     * Set the service so that it defers missing property gets to the
     * Call.  So when client-side Handlers get at the MessageContext,
     * the property scoping will be MC -> SOAPService -> Call
     */
    public void setSOAPService(SOAPService service)
    {
        myService = service;
        if (service != null) {
            // Set the service so that it defers missing property gets to the
            // Call.  So when client-side Handlers get at the MessageContext,
            // the property scoping will be MC -> SOAPService -> Call -> Engine
            
            // THE ORDER OF THESE TWO CALLS IS IMPORTANT, since setting the
            // engine on a service will set the property parent for the service
            service.setEngine(this.service.getAxisClient());
            service.setPropertyParent(myProperties);
        }
    }

    /**
     * Sets the client-side request and response Handlers.  This is handy
     * for programatically setting up client-side work without deploying
     * via WSDD or the EngineConfiguration mechanism.
     */
    public void setClientHandlers(Handler reqHandler, Handler respHandler)
    {
        // Create a SOAPService which will be used as the client-side service
        // handler.
        setSOAPService(new SOAPService(reqHandler, null, respHandler));
    }


    /**
     * This method adds an attachment.
     * <p>
     * Note: Not part of JAX-RPC specification.
     *
     * @param attachment the <code>Object</code> to attach
     * @exception RuntimeException if there is no support for attachments
     *
     */
     public void addAttachmentPart( Object attachment){
         attachmentParts.add(attachment);
     }

    /**
     * Add a fault for this operation.
     * <p>
     * Note: Not part of JAX-RPC specificaion.
     *
     * @param qname     qname of the fault
     * @param cls       class of the fault
     * @param xmlType   XML type of the fault
     * @param isComplex true if xmlType is a complex type, false otherwise
     */
    public void addFault(QName qname, Class cls,
                         QName xmlType, boolean isComplex) {
        if (operationSetManually) {
            throw new RuntimeException(
                    Messages.getMessage("operationAlreadySet"));
        }

        if (operation == null) {
            operation = new OperationDesc();
        }

        FaultDesc fault = new FaultDesc();
        fault.setQName(qname);
        fault.setClassName(cls.getName());
        fault.setXmlType(xmlType);
        fault.setComplex(isComplex);
        operation.addFault(fault);
    }

    /**
     * Hand a complete OperationDesc to the Call, and note that this was
     * done so that others don't try to mess with it by calling addParameter,
     * setReturnType, etc.
     *
     * @param operation the OperationDesc to associate with this call.
     */
    public void setOperation(OperationDesc operation) {
        this.operation = operation;
        operationSetManually = true;
    }

    public OperationDesc getOperation()
    {
        return operation;
    }

    public void clearOperation() {
        operation = null;
        operationSetManually = false;
    }
}
