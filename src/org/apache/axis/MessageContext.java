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

package org.apache.axis ;

import org.apache.axis.attachments.Attachments;
import org.apache.axis.client.AxisClient;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.enum.Style;
import org.apache.axis.enum.Use;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.schema.SchemaVersion;
import org.apache.axis.session.Session;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.LockableHashtable;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

// fixme: fields are declared throughout this class, some at the top, and some
//  near to where they are used. We should move all field declarations into a
//  single block - it makes it easier to see what is decalred in this class and
//  what is inherited. It also makes it easier to find them.
/**
 * A MessageContext is the Axis implementation of the javax
 * SOAPMessageContext class, and is core to message processing
 * in handlers and other parts of the system.
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
public class MessageContext implements SOAPMessageContext {
    /** The <code>Log</code> used for logging all messages. */
    protected static Log log =
            LogFactory.getLog(MessageContext.class.getName());

    /**
     * The request message.  If we're on the client, this is the outgoing
     * message heading to the server.  If we're on the server, this is the
     * incoming message we've received from the client.
     */
    private Message      requestMessage;

    /**
     * The response message.  If we're on the server, this is the outgoing
     * message heading back to the client.  If we're on the client, this is the
     * incoming message we've received from the server.
     */
    private Message      responseMessage;

    /**
     * That unique key/name that the next router/dispatch handler should use
     * to determine what to do next.
     */
    private String       targetService;

    /**
     * The name of the Transport which this message was received on (or is
     * headed to, for the client).
     */
    private String       transportName;

    /**
     * The default <code>ClassLoader</code> that this service should use.
     */
    private ClassLoader  classLoader;

    /**
     * The AxisEngine which this context is involved with.
     */
    private AxisEngine   axisEngine;

    /**
     * A Session associated with this request.
     */
    private Session      session;

    /**
     * Should we track session state, or not?
     * default is not.
     * Could potentially refactor this so that
     * maintainSession iff session != null...
     */
    private boolean      maintainSession = false;

    // fixme: ambiguity here due to lac of docs - havePassedPivot vs
    //  request/response, vs sending/processing & recieving/responding
    //  I may have just missed the key bit of text
    /**
     * Are we doing request stuff, or response stuff? True if processing
     * response (I think).
     */
    private boolean      havePassedPivot = false;

    /**
     * Maximum amount of time to wait on a request, in milliseconds.
     */
    private int          timeout = Constants.DEFAULT_MESSAGE_TIMEOUT;

    /**
     * An indication of whether we require "high fidelity" recording of
     * deserialized messages for this interaction.  Defaults to true for
     * now, and can be set to false, usually at service-dispatch time.
     */
    private boolean      highFidelity = true;

    /**
     * Storage for an arbitrary bag of properties associated with this
     * MessageContext.
     */
    private LockableHashtable bag = new LockableHashtable();

    /*
     * These variables are logically part of the bag, but are separated
     * because they are used often and the Hashtable is more expensive.
     *
     * fixme: this may be fixed by moving to a plain Map impl like HashMap.
     *  Alternatively, we could hide all this magic behind a custom Map impl -
     *  is synchronization on the map needed? these properties aren't
     *  synchronized so I'm guessing not.
     */
    private String  username       = null;
    private String  password       = null;
    private String  encodingStyle  = Use.ENCODED.getEncoding();
    private boolean useSOAPAction  = false;
    private String  SOAPActionURI  = null;

    /**
     * SOAP Actor roles.
     */
    private String[] roles;

    /** Our SOAP namespaces and such. */
    private SOAPConstants soapConstants = Constants.DEFAULT_SOAP_VERSION;

    /** Schema version information - defaults to 2001. */
    private SchemaVersion schemaVersion = SchemaVersion.SCHEMA_2001;

    /** Our current operation. */
    private OperationDesc currentOperation = null;

    /**
     * The current operation.
     *
     * @return the current operation; may be <code>null</code>
     */
    public  OperationDesc getOperation()
    {
        return currentOperation;
    }

    /**
     * Set the current operation.
     *
     * @param operation  the <code>Operation</code> this context is executing
     */
    public void setOperation(OperationDesc operation)
    {
        currentOperation = operation;
    }

    /**
     * Returns a list of operation descriptors that could may
     * possibly match a body containing an element of the given QName.
     * For non-DOCUMENT, the list of operation descriptors that match
     * the name is returned.  For DOCUMENT, all the operations that have
     * qname as a parameter are returned
     *
     * @param qname of the first element in the body
     * @return list of operation descriptions
     * @throws AxisFault if the operation names could not be looked up
     */
    public OperationDesc [] getPossibleOperationsByQName(QName qname) throws AxisFault
    {
        if (currentOperation != null) {
            return new OperationDesc [] { currentOperation };
        }

        OperationDesc [] possibleOperations = null;

        if (serviceHandler == null) {
            try {
                if (log.isDebugEnabled()) {
                    log.debug(Messages.getMessage("dispatching00",
                                                   qname.getNamespaceURI()));
                }

                // Try looking this QName up in our mapping table...
                setService(axisEngine.getConfig().
                           getServiceByNamespaceURI(qname.getNamespaceURI()));
            } catch (ConfigurationException e) {
                // Didn't find one...
            }

        }

        if (serviceHandler != null) {
            ServiceDesc desc = serviceHandler.getInitializedServiceDesc(this);

            if (desc != null) {
                if (desc.getStyle() != Style.DOCUMENT) {
                    possibleOperations = desc.getOperationsByQName(qname);
                } else {
                    // DOCUMENT Style
                    // Get all of the operations that have qname as
                    // a possible parameter QName
                    ArrayList allOperations = desc.getOperations();
                    ArrayList foundOperations = new ArrayList();
                    for (int i=0; i < allOperations.size(); i++ ) {
                        OperationDesc tryOp =
                            (OperationDesc) allOperations.get(i);
                        if (tryOp.getParamByQName(qname) != null) {
                            foundOperations.add(tryOp);
                        }
                    }
                    if (foundOperations.size() > 0) {
                        possibleOperations = (OperationDesc[])
                            JavaUtils.convert(foundOperations,
                                              OperationDesc[].class);
                    }
                }
            }
        }
        return possibleOperations;
    }

    /**
     * get the first possible operation that could match a
     * body containing an element of the given QName. Sets the currentOperation
     * field in the process; if that field is already set then its value
     * is returned instead
     * @param qname name of the message body
     * @return an operation or null
     * @throws AxisFault
     */
    public OperationDesc getOperationByQName(QName qname) throws AxisFault
    {
        if (currentOperation == null) {
            OperationDesc [] possibleOperations = getPossibleOperationsByQName(qname);
            if (possibleOperations != null && possibleOperations.length > 0) {
                currentOperation = possibleOperations[0];
            }
        }

        return currentOperation;
    }

    /**
     * Get the active message context.
     *
     * @return the current active message context
     */
    public static MessageContext getCurrentContext() {
       return AxisEngine.getCurrentMessageContext();
    }

    /**
     * Temporary directory to store attachments.
     */
    protected static String systemTempDir= null;
    /**
     * set the temp dir
     * TODO: move this piece of code out of this class and into a utilities
     * class.
     */
    static {
        try {
            //get the temp dir from the engine
            systemTempDir=AxisProperties.getProperty(AxisEngine.ENV_ATTACHMENT_DIR);
        } catch(Throwable t) {
            systemTempDir= null;
        }

        if(systemTempDir== null) {
            try {
                //or create and delete a file in the temp dir to make
                //sure we have write access to it.
                File tf= File.createTempFile("Axis", ".tmp");
                File dir= tf.getParentFile();
                if (tf.exists()) {
                    tf.delete();
                }
                if (dir != null) {
                  systemTempDir= dir.getCanonicalPath();
                }
            } catch(Throwable t) {
                log.debug("Unable to find a temp dir with write access");
                systemTempDir= null;
            }
        }
    }

    /**
     * Create a message context.
     * @param engine the controlling axis engine. Null is actually accepted here,
     * though passing a null engine in is strongly discouraged as many of the methods
     * assume that it is in fact defined.
     */
    public MessageContext(AxisEngine engine) {
        this.axisEngine = engine;

        if(null != engine){
            java.util.Hashtable opts= engine.getOptions();
            String attachmentsdir= null;
            if(null!=opts) {
                attachmentsdir= (String) opts.get(AxisEngine.PROP_ATTACHMENT_DIR);
            }
            if(null == attachmentsdir) {
                attachmentsdir= systemTempDir;
            }
            if(attachmentsdir != null){
                setProperty(ATTACHMENTS_DIR, attachmentsdir);
            }

            // If SOAP 1.2 has been specified as the default for the engine,
            // switch the constants over.
            String defaultSOAPVersion = (String)engine.getOption(
                                                 AxisEngine.PROP_SOAP_VERSION);
            if (defaultSOAPVersion != null && "1.2".equals(defaultSOAPVersion)) {
                setSOAPConstants(SOAPConstants.SOAP12_CONSTANTS);
            }

            String singleSOAPVersion = (String)engine.getOption(
                                        AxisEngine.PROP_SOAP_ALLOWED_VERSION);
            if (singleSOAPVersion != null) {
                if ("1.2".equals(singleSOAPVersion)) {
                    setProperty(Constants.MC_SINGLE_SOAP_VERSION,
                                SOAPConstants.SOAP12_CONSTANTS);
                } else if ("1.1".equals(singleSOAPVersion)) {
                    setProperty(Constants.MC_SINGLE_SOAP_VERSION,
                                SOAPConstants.SOAP11_CONSTANTS);
                }
            }
        }
    }

    /**
     * during finalization, the dispose() method is called.
     * @see #dispose()
     */
    protected void finalize() {
        dispose();
    }

    /**
     * Mappings of QNames to serializers/deserializers (and therfore
     * to Java types).
     */
    private TypeMappingRegistry mappingRegistry = null;

    /**
     * Replace the engine's type mapping registry with a local one. This will
     * have no effect on any type mappings obtained before this call.
     *
     * @param reg  the new <code>TypeMappingRegistry</code>
     */
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
        if (mappingRegistry == null) {
            return axisEngine.getTypeMappingRegistry();
        }

        return mappingRegistry;
    }

    /**
     * Return the type mapping currently in scope for our encoding style.
     *
     * @return the type mapping
     */
    public TypeMapping getTypeMapping()
    {
        return (TypeMapping)getTypeMappingRegistry().
                getTypeMapping(encodingStyle);
    }

    /**
     * The name of the transport for this context.
     *
     * @return the transport name
     */
    public String getTransportName()
    {
        return transportName;
    }

    // fixme: the transport names should be a type-safe e-num, or the range
    //  of legal values should be specified in the documentation and validated
    //  in the method (raising IllegalArgumentException)
    /**
     * Set the transport name for this context.
     *
     * @param transportName the name of the transport
     */
    public void setTransportName(String transportName)
    {
        this.transportName = transportName;
    }

    /**
     * Get the <code>SOAPConstants</code> used by this message context.
     *
     * @return the soap constants
     */
    public SOAPConstants getSOAPConstants() {
        return soapConstants;
    }

    /**
     * Set the <code>SOAPConstants</code> used by this message context.
     * This may also affect the encoding style.
     *
     * @param soapConstants  the new soap constants to use
     */
    public void setSOAPConstants(SOAPConstants soapConstants) {
        // when changing SOAP versions, remember to keep the encodingURI
        // in synch.
        if (this.soapConstants.getEncodingURI().equals(encodingStyle)) {
            encodingStyle = soapConstants.getEncodingURI();
        }

        this.soapConstants = soapConstants;
    }

    /**
     * Get the XML schema version information.
     *
     * @return the <code>SchemaVersion</code> in use
     */
    public SchemaVersion getSchemaVersion() {
        return schemaVersion;
    }

    /**
     * Set the XML schema version this message context will use.
     *
     * @param schemaVersion  the new <code>SchemaVersion</code>
     */
    public void setSchemaVersion(SchemaVersion schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    /**
     * Get the current session.
     *
     * @return the <code>Session</code> this message context is within
     */
    public Session getSession()
    {
        return session;
    }

    /**
     * Set the current session.
     *
     * @param session  the new <code>Session</code>
     */
    public void setSession(Session session)
    {
        this.session = session;
    }

    /**
     * Indicates if the opration is encoded.
     *
     * @return <code>true</code> if it is encoded, <code>false</code> otherwise
     */
    public boolean isEncoded() {
        return (getOperationUse() == Use.ENCODED);
        //return soapConstants.getEncodingURI().equals(encodingStyle);
    }

    /**
     * Set whether we are maintaining session state.
     *
     * @param yesno flag to set to <code>true</code> to maintain sessions
     */
    public void setMaintainSession (boolean yesno) {
        maintainSession = yesno;
    }

    /**
     * Discover if we are maintaining session state.
     *
     * @return <code>true</code> if we are maintaining state, <code>false</code>
     *              otherwise
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
    }

    /**
     * Set the request message, and make sure that message is associated
     * with this MessageContext.
     *
     * @param reqMsg the new request Message.
     */
    public void setRequestMessage(Message reqMsg) {
        requestMessage = reqMsg ;
        if (requestMessage != null) {
            requestMessage.setMessageContext(this);
        }
    }

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
     * @param respMsg the new response Message.
     */
    public void setResponseMessage(Message respMsg) {
        responseMessage = respMsg;
        if (responseMessage != null) {
            responseMessage.setMessageContext(this);

            //if we have received attachments of a particular type
            // than that should be the default type to send.
            Message reqMsg = getRequestMessage();
            if (null != reqMsg) {
                Attachments reqAttch = reqMsg.getAttachmentsImpl();
                Attachments respAttch = respMsg.getAttachmentsImpl();
                if (null != reqAttch && null != respAttch) {
                    if (respAttch.getSendType() == Attachments.SEND_TYPE_NOTSET)
                        //only if not explicity set.
                        respAttch.setSendType(reqAttch.getSendType());
                }
            }
        }
    }

    /**
     * Return the current (i.e. request before the pivot, response after)
     * message.
     *
     * @return the current <code>Message</code>
     */
    public Message getCurrentMessage()
    {
        return (havePassedPivot ? responseMessage : requestMessage);
    }

    /**
     *  Gets the SOAPMessage from this message context.
     *
     *  @return the <code>SOAPMessage</code>, <code>null</code> if no request
     *          <code>SOAPMessage</code> is present in this
     *          <code>SOAPMessageContext</code>
     */
    public javax.xml.soap.SOAPMessage getMessage() {
        return getCurrentMessage();
    }

    /**
     * Set the current message. This will set the request before the pivot,
     * and the response afterwards, as guaged by the passedPivod property.
     *
     * @param curMsg  the <code>Message</code> to assign
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
     * Sets the SOAPMessage for this message context.
     * This is equivalent to casting <code>message</code> to
     * <code>Message</code> and then passing it on to
     * <code>setCurrentMessage()</code>.
     *
     * @param message  the <code>SOAPMessage</code> this context is for
     */
    public void setMessage(javax.xml.soap.SOAPMessage message) {
        setCurrentMessage((Message)message);
    }

    /**
     * Determine when we've passed the pivot.
     *
     * @return <code>true</code> if we have, <code>false</code> otherwise
     */
    public boolean getPastPivot()
    {
        return havePassedPivot;
    }

    // fixme: is there any legitimate case where we could pass the pivot and
    //  then go back again? Is there documentation about the life-cycle of a
    //  MessageContext, and in particular the re-use of instances that would be
    //  relevant?
    /**
     * Indicate when we've passed the pivot.
     *
     * @param pastPivot  true if we are past the pivot point, false otherwise
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

    /**
     * Get the classloader, implicitly binding to the thread context
     * classloader if an override has not been supplied.
     *
     * @return the class loader
     */
    public ClassLoader getClassLoader() {
        if ( classLoader == null ) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        return( classLoader );
    }

    /**
     * Set a new classloader. Setting to null will result in getClassLoader()
     * binding back to the thread context class loader.
     *
     * @param cl    the new <code>ClassLoader</code> or <code>null</code>
     */
    public void setClassLoader(ClassLoader cl ) {
        classLoader = cl ;
    }

    /**
     * Get the name of the targed service for this message.
     *
     * @return the target service
     */
    public String getTargetService() {
        return targetService;
    }

    /**
     * Get the axis engine. This will be <code>null</code> if the message was
     * created outside an engine
     *
     * @return the current axis engine
     */
    public AxisEngine getAxisEngine()
    {
        return axisEngine;
    }

    /**
     * Set the target service for this message.
     * <p>
     * This looks up the named service in the registry, and has
     * the side effect of setting our TypeMappingRegistry to the
     * service's.
     *
     * @param tServ the name of the target service
     * @throws AxisFault  if anything goes wrong in resolving or setting the
     *              service
     */
    public void setTargetService(String tServ) throws AxisFault {
        log.debug("MessageContext: setTargetService(" + tServ+")");

        if (tServ == null) {
            setService(null);
        }
        else {
            try {
                setService(getAxisEngine().getService(tServ));
            } catch (AxisFault fault) {
                // If we're on the client, don't throw this fault...
                if (!isClient()) {
                    throw fault;
                }
            }
        }
        targetService = tServ;
    }

    /** ServiceHandler is the handler that is the "service".  This handler
     * can (and probably will actually be a chain that contains the
     * service specific request/response/pivot point handlers
     */
    private SOAPService serviceHandler ;

    /**
     * Get the <code>SOAPService</code> used to handle services in this
     * context.
     *
     * @return the service handler
     */
    public SOAPService getService() {
        return  serviceHandler;
    }

    /**
     * Set the <code>SOAPService</code> used to handle services in this
     * context. This method configures a wide range of
     * <code>MessageContext</code> properties to suit the handler.
     *
     * @param sh the new service handler
     * @throws AxisFault if the service could not be set
     */
    public void setService(SOAPService sh) throws AxisFault
    {
        log.debug("MessageContext: setServiceHandler("+sh+")");
        serviceHandler = sh;
        if (sh != null) {
            if(!sh.isRunning()) {
                throw new AxisFault(Messages.getMessage("disabled00"));
            }
            targetService = sh.getName();
            SOAPService service = sh;
            TypeMappingRegistry tmr = service.getTypeMappingRegistry();
            setTypeMappingRegistry(tmr);

            // styles are not "soap version aware" so compensate...
            setEncodingStyle(service.getUse().getEncoding());

            // This MessageContext should now defer properties it can't find
            // to the Service's options.
            bag.setParent(sh.getOptions());

            // Note that we need (or don't need) high-fidelity SAX recording
            // of deserialized messages according to the setting on the
            // new service.
            highFidelity = service.needsHighFidelityRecording();

            service.getInitializedServiceDesc(this);
        }
    }

    /**
     * Let us know whether this is the client or the server.
     *
     * @return true if we are a client
     */
    public boolean isClient()
    {
        return (axisEngine instanceof AxisClient);
    }

    // fixme: public final statics tend to go in a block at the top of the
    //  class deffinition, not marooned in the middle
    // fixme: chose public static final /or/ public final static
    /** Contains an instance of Handler, which is the
     *  ServiceContext and the entrypoint of this service.
     *
     *  (if it has been so configured - will our deployment
     *   tool do this by default?  - todo by Jacek)
     */
    public static final String ENGINE_HANDLER      = "engine.handler";

    /** This String is the URL that the message came to.
     */
    public static final String TRANS_URL           = "transport.url";

    /** Has a quit been requested? Hackish... but useful... -- RobJ */
    public static final String QUIT_REQUESTED = "quit.requested";

    /** Place to store an AuthenticatedUser. */
    public static final String AUTHUSER            = "authenticatedUser";

    /** If on the client - this is the Call object. */
    public static final String CALL                = "call_object" ;

    /** Are we doing Msg vs RPC? - For Java Binding. */
    public static final String IS_MSG              = "isMsg" ;

    /** The directory where in coming attachments are created. */
    public static final String ATTACHMENTS_DIR   = "attachments.directory" ;

    /** A boolean param, to control whether we accept missing parameters
     * as nulls or refuse to acknowledge them.
     */
    public final static String ACCEPTMISSINGPARAMS = "acceptMissingParams";

    /** The value of the property is used by service WSDL generation (aka ?WSDL)
     * For the service's interface namespace if not set TRANS_URL property is used.
     */
    public static final String WSDLGEN_INTFNAMESPACE      = "axis.wsdlgen.intfnamespace";

    /** The value of the property is used by service WSDL generation (aka ?WSDL).
     * For the service's location if not set TRANS_URL property is used.
     *  (helps provide support through proxies.
     */
    public static final String WSDLGEN_SERV_LOC_URL      = "axis.wsdlgen.serv.loc.url";

    // fixme: should this be a type-safe e-num?
    /** The value of the property is used by service WSDL generation (aka ?WSDL).
     *  Set this property to request a certain level of HTTP.
     *  The values MUST use org.apache.axis.transport.http.HTTPConstants.HEADER_PROTOCOL_10
     *    for HTTP 1.0
     *  The values MUST use org.apache.axis.transport.http.HTTPConstants.HEADER_PROTOCOL_11
     *    for HTTP 1.1
     */
    public static final String HTTP_TRANSPORT_VERSION  = "axis.transport.version";

    // fixme: is this the name of a security provider, or the name of a security
    //  provider class, or the actualy class of a security provider, or
    //  something else?
    /**
     * The security provider.
     */
    public static final String SECURITY_PROVIDER = "securityProvider";

    /*
     * IMPORTANT.
     * If adding any new constants to this class. Make them final. The
     * ones above are left non-final for compatibility reasons.
     */

    /**
     * Get a <code>String</code> property by name.
     *
     * @param propName the name of the property to fetch
     * @return the value of the named property
     * @throws ClassCastException if the property named does not have a
     *              <code>String</code> value
     */
    public String getStrProp(String propName) {
        return (String) getProperty(propName);
    }

    /**
     * Tests to see if the named property is set in the 'bag', returning
     * <code>false</code> if it is not present at all.
     * This is equivalent to <code>isPropertyTrue(propName, false)</code>.
     *
     * @param propName  the name of the property to check
     * @return true or false, depending on the value of the property
     */
    public boolean isPropertyTrue(String propName) {
        return isPropertyTrue(propName, false);
    }

    /**
     * Test if a property is set to something we consider to be true in the
     * 'bag'.
     * <ul>
     * <li>If not there then <code>defaultVal</code> is returned.</li>
     * <li>If there, then...<ul>
     *   <li>if its a <code>Boolean</code>, we'll return booleanValue()</li>
     *   <li>if its an <code>Integer</code>,  we'll return <code>false</code>
     *   if its <code>0</code> else <code>true</code></li>
     *   <li>if its a <code>String</code> we'll return <code>false</code> if its
     *   <code>"false"</code>" or <code>"0"</code> else <code>true</code></li>
     *   <li>All other types return <code>true</code></li>
     * </ul></li>
     * </ul>
     *
     * @param propName  the name of the property to check
     * @param defaultVal  the default value
     * @return true or false, depending on the value of the property
     */
    public boolean isPropertyTrue(String propName, boolean defaultVal) {
        return JavaUtils.isTrue(getProperty(propName), defaultVal);
    }

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
     * @param name  Name of the property
     * @param value Value of the property
     */
    public void setProperty(String name, Object value) {
        if (name == null || value == null) {
            return;
            // Is this right?  Shouldn't we throw an exception like:
            // throw new IllegalArgumentException(msg);
        }
        else if (name.equals(Call.USERNAME_PROPERTY)) {
            if (!(value instanceof String)) {
                throw new IllegalArgumentException(
                        Messages.getMessage("badProp00", new String[] {
                        name, "java.lang.String", value.getClass().getName()}));
            }
            setUsername((String) value);
        }
        else if (name.equals(Call.PASSWORD_PROPERTY)) {
            if (!(value instanceof String)) {
                throw new IllegalArgumentException(
                        Messages.getMessage("badProp00", new String[] {
                        name, "java.lang.String", value.getClass().getName()}));
            }
            setPassword((String) value);
        }
        else if (name.equals(Call.SESSION_MAINTAIN_PROPERTY)) {
            if (!(value instanceof Boolean)) {
                throw new IllegalArgumentException(
                        Messages.getMessage("badProp00", new String[]
                        {name,
                        "java.lang.Boolean",
                        value.getClass().getName()}));
            }
            setMaintainSession(((Boolean) value).booleanValue());
        }
        else if (name.equals(Call.SOAPACTION_USE_PROPERTY)) {
            if (!(value instanceof Boolean)) {
                throw new IllegalArgumentException(
                        Messages.getMessage("badProp00", new String[]
                        {name,
                        "java.lang.Boolean",
                        value.getClass().getName()}));
            }
            setUseSOAPAction(((Boolean) value).booleanValue());
        }
        else if (name.equals(Call.SOAPACTION_URI_PROPERTY)) {
            if (!(value instanceof String)) {
                throw new IllegalArgumentException(
                        Messages.getMessage("badProp00", new String[]
                        {name,
                        "java.lang.String",
                        value.getClass().getName()}));
            }
            setSOAPActionURI((String) value);
        }
        else if (name.equals(Call.ENCODINGSTYLE_URI_PROPERTY)) {
            if (!(value instanceof String)) {
                throw new IllegalArgumentException(
                        Messages.getMessage("badProp00", new String[]
                        {name,
                        "java.lang.String",
                        value.getClass().getName()}));
            }
            setEncodingStyle((String) value);
        }
        else {
            bag.put(name, value);
        }
    } // setProperty

    /**
     *  Returns true if the MessageContext contains a property with the specified name.
     *  @param   name Name of the property whose presense is to be tested
     *  @return  Returns true if the MessageContext contains the
          property; otherwise false
     */
    public boolean containsProperty(String name) {
        Object propertyValue = getProperty(name);
        return (propertyValue != null);
    }

    /**
     * Returns an <code>Iterator</code> view of the names of the properties in
     * this <code>MessageContext</code>.
     *
     * @return an <code>Iterator</code> over all property names
     */
    public java.util.Iterator getPropertyNames() {
        // fixme: this is potentially unsafe for the caller - changing the
        //  properties will kill the iterator. Consider iterating over a copy:
        // return new HashSet(bag.keySet()).iterator();
        return bag.keySet().iterator();
    }

    /**
     *  Returns an Iterator view of the names of the properties 
     *  in this MessageContext and any parents of the LockableHashtable
     *  @return Iterator for the property names
     */
    public java.util.Iterator getAllPropertyNames() {
        return bag.getAllKeys().iterator();
    }

    /**
     * Returns the value associated with the named property - or null if not
     * defined/set.
     *
     * @param name  the property name
     * @return Object value of the property - or null
     */
    public Object getProperty(String name) {
        if (name != null) {
            if (name.equals(Call.USERNAME_PROPERTY)) {
                return getUsername();
            }
            else if (name.equals(Call.PASSWORD_PROPERTY)) {
                return getPassword();
            }
            else if (name.equals(Call.SESSION_MAINTAIN_PROPERTY)) {
                return getMaintainSession() ? Boolean.TRUE : Boolean.FALSE;
            }
            else if (name.equals(Call.OPERATION_STYLE_PROPERTY)) {
                return (getOperationStyle() == null) ? null : getOperationStyle().getName();
            }
            else if (name.equals(Call.SOAPACTION_USE_PROPERTY)) {
                return useSOAPAction() ? Boolean.TRUE : Boolean.FALSE;
            }
            else if (name.equals(Call.SOAPACTION_URI_PROPERTY)) {
                return getSOAPActionURI();
            }
            else if (name.equals(Call.ENCODINGSTYLE_URI_PROPERTY)) {
                return getEncodingStyle();
            }
            else if (bag == null) {
                return null;
            }
            else {
                return bag.get(name);
            }
        }
        else {
            return null;
        }
    }

    // fixme: this makes no copy of parent, so later modifications to parent
    //  can alter this context - is this intended? If so, it needs documenting.
    //  If not, it needs fixing.
    /**
     * Set the Hashtable that contains the default values for our
     * properties.
     *
     * @param parent
     */
    public void setPropertyParent(Hashtable parent)
    {
        bag.setParent(parent);
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
     * @return the user name as a <code>String</code>
     */
    public String getUsername() {
        return username;
    } // getUsername

    /**
     * Set the password.
     *
     * @param password  a <code>String</code> containing the new password
     */
    public void setPassword(String password) {
        this.password = password;
    } // setPassword

    /**
     * Get the password.
     *
     * @return the current password <code>String</code>
     */
    public String getPassword() {
        return password;
    } // getPassword

    /**
     * Get the operation style. This is either the style of the current
     * operation or if that is not set, the style of the service handler, or
     * if that is not set, <code>Style.RPC</code>.
     *
     * @return the <code>Style</code> of this message
     */
    public Style getOperationStyle() {
        if (currentOperation != null) {
            return currentOperation.getStyle();
        }

        if (serviceHandler != null) {
            return serviceHandler.getStyle();
        }

        return Style.RPC;
    } // getOperationStyle

    /**
     * Get the operation use.
     *
     * @return the operation <code>Use</code>
     */
    public Use getOperationUse() {
        if (currentOperation != null) {
            return currentOperation.getUse();
        }

        if (serviceHandler != null) {
            return serviceHandler.getUse();
        }

        return Use.ENCODED;
    } // getOperationUse

    /**
     * Enable or dissable the use of soap action information. When enabled,
     * the message context will attempt to use the soap action URI
     * information during binding of soap messages to service methods. When
     * dissabled, it will make no such attempt.
     *
     * @param useSOAPAction  <code>true</code> if soap action URI information
     *              should be used, <code>false</code> otherwise
     */
    public void setUseSOAPAction(boolean useSOAPAction) {
        this.useSOAPAction = useSOAPAction;
    } // setUseSOAPAction

    // fixme: this doesn't follow beany naming conventions - should be
    //  isUseSOAPActions or getUseSOAPActions or something prettier
    /**
     * Indicates wether the soap action URI is being used or not.
     *
     * @return <code>true</code> if it is, <code>false</code> otherwise
     */
    public boolean useSOAPAction() {
        return useSOAPAction;
    } // useSOAPAction

    // fixme: this throws IllegalArgumentException but never raises it -
    //  perhaps in a sub-class?
    // fixme: IllegalArgumentException is unchecked. Best practice says you
    //  should document unchecked exceptions, but not list them in throws
    /**
     * Set the soapAction URI.
     *
     * @param SOAPActionURI  a <code>String</code> giving the new soap action
     *              URI
     * @throws IllegalArgumentException if the URI is not liked
     */
    public void setSOAPActionURI(String SOAPActionURI)
            throws IllegalArgumentException {
        this.SOAPActionURI = SOAPActionURI;
    } // setSOAPActionURI

    /**
     * Get the soapAction URI.
     *
     * @return the URI of this soap action
     */
    public String getSOAPActionURI() {
        return SOAPActionURI;
    } // getSOAPActionURI

    /**
     * Sets the encoding style to the URI passed in.
     *
     * @param namespaceURI URI of the encoding to use.
     */
    public void setEncodingStyle(String namespaceURI) {
        if (namespaceURI == null) {
            namespaceURI = Constants.URI_LITERAL_ENC;
        }
        else if (Constants.isSOAP_ENC(namespaceURI)) {
            namespaceURI = soapConstants.getEncodingURI();
        }

        encodingStyle = namespaceURI;
    } // setEncodingStype

    /**
     * Returns the encoding style as a URI that should be used for the SOAP
     * message.
     *
     * @return String URI of the encoding style to use
     */
    public String getEncodingStyle() {
        return encodingStyle;
    } // getEncodingStyle

    public void removeProperty(String propName)
    {
        if (bag != null) {
            bag.remove(propName);
        }
    }

    /**
     * Return this context to a clean state.
     */
    public void reset()
    {
        if (bag != null) {
            bag.clear();
        }
        serviceHandler = null;
        havePassedPivot = false;
        currentOperation = null;
    }

    /**
     * Read the high fidelity property.
     * <p>
     * Some behavior may be apropreate for high fidelity contexts that is not
     * relevant for low fidelity ones or vica-versa.
     *
     * @return <code>true</code> if the context is high fidelity,
     *              <code>false</code> otherwise
     */
    public boolean isHighFidelity() {
        return highFidelity;
    }

    /**
     * Set the high fidelity propert.
     * <p>
     * Users of the context may be changing what they do based upon this flag.
     *
     * @param highFidelity  the new value of the highFidelity property
     */
    public void setHighFidelity(boolean highFidelity) {
        this.highFidelity = highFidelity;
    }

    /**
     * Gets the SOAP actor roles associated with an execution of the
     * <code>HandlerChain</code> and its contained <code>Handler</code>
     * instances.
     * <p>
     * <i>Not (yet) implemented method in the SOAPMessageContext interface</i>.
     * <p>
     * <b>Note:</b> SOAP actor roles apply to the SOAP node and are managed
     * using <code>HandlerChain.setRoles()</code> and
     * <code>HandlerChain.getRoles()</code>. Handler instances in the
     * <code>HandlerChain</code> use this information about the SOAP actor roles
     * to process the SOAP header blocks. Note that the SOAP actor roles are
     * invariant during the processing of SOAP message through the
     * <code>HandlerChain</code>.
     *
     * @return an array of URIs for SOAP actor roles
     * @see javax.xml.rpc.handler.HandlerChain#setRoles(java.lang.String[]) HandlerChain.setRoles(java.lang.String[])
     * @see javax.xml.rpc.handler.HandlerChain#getRoles() HandlerChain.getRoles()
     */
    public String[] getRoles() {
        //TODO: Flesh this out.
        return roles;
    }

    /**
     * Set the SOAP actor roles associated with an executioni of
     * <code>CodeHandlerChain</code> and its contained <code>Handler</code>
     * instances.
     *
     * @param roles an array of <code>String</code> instances, each representing
     *              the URI for a SOAP actor role
     */
    public void setRoles( String[] roles) {
        this.roles = roles;
    }

    /**
     * if a message (or subclass) has any disposal needs, this method
     * is where it goes. Subclasses *must* call super.dispose(), and
     * be prepared to be called from the finalizer as well as earlier
     */
    public synchronized void dispose() {
        log.debug("disposing of message context");
        if(requestMessage!=null) {
            requestMessage.dispose();
            requestMessage=null;
        }
        if(responseMessage!=null) {
            responseMessage.dispose();
            responseMessage=null;
        }
    }
}
