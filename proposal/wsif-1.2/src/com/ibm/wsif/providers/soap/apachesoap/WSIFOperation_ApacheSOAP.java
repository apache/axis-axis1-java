// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
//-------------------------74-columns-wide-------------------------------|
package com.ibm.wsif.providers.soap.apachesoap;

import java.io.*;
import java.util.*;
import java.net.*;
import org.w3c.dom.*; 
import org.apache.soap.*;
import org.apache.soap.encoding.*;
import org.apache.soap.encoding.soapenc.*;
import org.apache.soap.messaging.Message;
import org.apache.soap.rpc.*;
import org.apache.soap.transport.*;
import org.apache.soap.util.*;
import org.apache.soap.util.xml.Serializer;
import org.apache.soap.util.xml.Deserializer;
import org.apache.soap.util.xml.XMLParserUtils;

import javax.wsdl.*;

import com.ibm.wsif.*;
import com.ibm.wsif.logging.*;
import com.ibm.wsif.util.*;
import com.ibm.wsif.providers.*;
import com.ibm.wsif.util.jms.*;
import com.ibm.wsdl.extensions.jms.*;

import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.InputSource;

/**
 * Provide concrete implementation of WSDL operation with Apache SOAP
 * RPC method invocation.
 *
 * @author Alekander Slominski
 */

public class WSIFOperation_ApacheSOAP 
  extends WSIFDefaultOperation 
  implements WSIFOperation, Serializable
{
  private final static boolean DEBUG = false;
  transient protected String style = null;
  transient protected String inputUse = null;
  transient protected String outputUse = null;
  transient protected String partSerializerName = null;
  
  transient protected WSIFPort_ApacheSOAP portInstance;
  transient protected Operation operation;
  transient protected Definition definition;
  
  // cached information to allow efficinet operation calls
  transient protected List partNames;
  transient protected String[] names;
  transient protected Class[] types;
  transient protected String inputEncodingStyle = Constants.NS_URI_SOAP_ENC;
  transient protected String inputNamespace;
  
  transient protected Class returnType;
  transient protected String actionUri;
  transient protected WSIFDynamicTypeMap typeMap;
  transient protected HashMap inJmsAttrs = new HashMap();
  transient protected HashMap outJmsAttrs = new HashMap();
  transient protected String jmsHeader = null;
  
  // info for async operation
  transient protected boolean asyncOperation;
  transient protected String asyncRequestID;
  // everything other than what is needed for async response should be transient
  protected WSIFResponseHandler responseHandler;  
  protected String outputEncodingStyle = Constants.NS_URI_SOAP_ENC;
  protected String returnName;
  protected boolean prepared = false;
    
  /**
   * Create Apache SOAP operation instance that encapsultes all necessary
   * information required to create and execute Apache SOAP Call.
   */
  public WSIFOperation_ApacheSOAP(WSIFPort_ApacheSOAP pi,
                                  Operation op,
                                  WSIFDynamicTypeMap typeMap)
    throws WSIFException
  {
    TraceLogger.getGeneralTraceLogger().entry(
      new Object[] {pi.toShallowString(),op.getName(),typeMap});
      
    this.typeMap = typeMap;
    setDynamicWSIFPort(pi);
    setOperation(op);
    setDefinition(pi.getDefinition());
    
    TraceLogger.getGeneralTraceLogger().exit();
  }
  
  /**
   * Create a new copy of this object. This is not a clone, since 
   * it does not copy the referenced objects as well.
   */
  public WSIFOperation_ApacheSOAP copy() throws WSIFException 
  {
  	 WSIFOperation_ApacheSOAP op = 
  	   new WSIFOperation_ApacheSOAP(portInstance,operation,typeMap);
  	   
  	 op.setSoapActionURI      (getSoapActionURI      ());
  	 op.setInputNamespace     (getInputNamespace     ());
  	 op.setInputEncodingStyle (getInputEncodingStyle ());
  	 op.setOutputEncodingStyle(getOutputEncodingStyle());
  	 op.setPartNames          (getPartNames          ());
  	 op.setReturnName         (getReturnName         ());
  	 op.setStyle              (getStyle              ());
  	 op.setInputUse           (getInputUse           ());
  	 op.setOutputUse          (getOutputUse          ());
  	 op.setPartSerializerName (getPartSerializerName ());
  	 op.setResponseHandler    (getResponseHandler    ());
  	 op.setInputJmsAttributes (getInputJmsAttributes ());
  	 op.setOutputJmsAttributes(getOutputJmsAttributes());
  	 op.setJmsHeader          (getJmsHeader          ());
  	 
  	 return op;
  }

	/**
	 * Creates a new input WSIFMessage. This overrides the 
	 * WSIFDefaultOperation method to enable the use of 
	 * compiled WSIFMessages by using a WSIFMessageFactory
	 * to create the message.
	 * 
	 * @param name   the name of the message
	 * @return a WSIFMessage instance
	 * @see WSIFOperation#createInputMessage(String)
	 */
	public WSIFMessage createInputMessage(String name) {
		Definition d = getDefinition();
		String ns = ( d == null ) ? "" : d.getTargetNamespace();
		WSIFMessageFactory mf = WSIFServiceImpl.getMessageFactory();
		WSIFMessage msg = mf.createMessage( ns, name + "Message" );
		if (msg!=null) msg.setName(name);
		return msg;
	}

	/**
	 * Creates a new input WSIFMessage. This overrides the 
	 * WSIFDefaultOperation method to enable the use of 
	 * compiled WSIFMessages by using a WSIFMessageFactory
	 * to create the message.
	 * 
	 * @param name   the name of the message
	 * @return a WSIFMessage instance
	 * @see WSIFOperation#createInputMessage(String)
	 */
	public WSIFMessage createOutputMessage(String name) {
		Definition d = getDefinition();
		String ns = ( d == null ) ? "" : d.getTargetNamespace();
		WSIFMessageFactory mf = WSIFServiceImpl.getMessageFactory();
		WSIFMessage msg = mf.createMessage( ns, name + "Message" );
		if (msg!=null) msg.setName(name);
		return msg;
	}
	
  /**
   * This is utility method that when called initializes operation
   * (including reconstruction of method signature).
   */
  void prepare(WSIFMessage inputMessage, WSIFMessage outputMessage) throws WSIFException 
  {  
    BeanSerializer beanSer = new BeanSerializer();
	PartSerializer partSer = null;
	if (partSerializerName!=null) try
	{
		partSer = (PartSerializer)Class.forName(
		  partSerializerName, 
		  true, 
		  Thread.currentThread().getContextClassLoader()).newInstance();
	}
	catch(Throwable ignored){}

	Serializer serializer = null;
	Deserializer deserializer = null;
	String encoding = null;
	
	HashMap mapOfUserTypes = new HashMap();
	if ("literal".equals(inputUse) && "literal".equals(outputUse))
	{
		serializer = partSer;
		deserializer = partSer;
		encoding = "literal";
	}
	else // soap encoding
	{ 
		serializer = beanSer;
		deserializer = beanSer;
		encoding = Constants.NS_URI_SOAP_ENC;
	}

	boolean usingJROM = 
		(WSIFConstants.JROM_REPR_STYLE.equals(inputMessage.getRepresentationStyle())) ? true : false;	

	boolean jromAvailable = WSIFServiceImpl.getJROMAvailability();

	if (usingJROM && !jromAvailable)
	{
		// This should not happen unless the representation style has been
		// explicitly set incorrectly!
		throw new WSIFException("Message contains JROM parts but JROM classes cannot be found");
	}

	SOAPMappingRegistry smr = portInstance.getSOAPMappingRegistry();    							

	JROMSerializer jromSer = null;
	JROMSOAPMappingRegistry jromSMR = null;
	
	if (jromAvailable)
	{
		jromSMR = portInstance.getJROMSOAPMappingRegistry(); 
		jromSer = new JROMSerializer();
	}
    	
    // initialize ApacheSOAP specific mappings here
    for(Iterator i = typeMap.iterator(); i.hasNext(); ) 
    {
      WSIFDynamicTypeMapping mapping = (WSIFDynamicTypeMapping) i.next();
      
      Class javaClass = mapping.getJavaType();
      org.apache.soap.util.xml.QName typeName = 
		new org.apache.soap.util.xml.QName(mapping.getXmlType().getNamespaceURI(), 
	                                       mapping.getXmlType().getLocalPart());
	  mapOfUserTypes.put(typeName, mapping.getJavaType());

	  if (jromAvailable)
	  {
	  	// JROM SOAP mapping registry should return a JROMSerializer for all
	  	// type mappings
	  	jromSMR.mapTypes (encoding, typeName, javaClass, jromSer, jromSer);	
	  }

	  Serializer ser;
	  try 
	  {
		ser = smr.querySerializer(javaClass, encoding);
		if (ser != null) continue;
	  }
	  catch (IllegalArgumentException ignored) { }
	  
	  smr.mapTypes(encoding, typeName, javaClass, serializer, deserializer);
    }
    
    // first determine list of arguments
    Input input = operation.getInput();
    if (input != null) 
    {
      List parts;
      if(partNames != null) 
      {
        parts = new Vector();
        for(Iterator i = partNames.iterator(); i.hasNext(); ) 
        {
          String partName = (String) i.next();
          Part part = input.getMessage().getPart(partName);
          if(part == null) 
          {
            throw new WSIFException("no input part named "+partName
                                      +" for binding operation "+getName());
          }
          parts.add(part);
        }
      } 
      else 
      {
        parts = input.getMessage().getOrderedParts(null);
      }
      int count = parts.size();
      names = new String[count];
      types = new Class[count];
      
      // get parts in correct order
      for (int i = 0; i <count; ++i) 
      {
        Part part = (Part) parts.get(i);
        names[i] = part.getName();
        QName partType = part.getTypeName();
		if (partType == null) partType = part.getElementName();
        if (partType == null) 
        {
          throw new WSIFException(
            "part "+names[i]+" must have type name declared");
        }

		org.apache.soap.util.xml.QName qname = 
		  new org.apache.soap.util.xml.QName(partType.getNamespaceURI(), 
		                                     partType.getLocalPart());
		try { types[i] = (Class) mapOfUserTypes.get(qname);	}
		catch(Throwable ignored) {}
		if (types[i] == null) {
			types[i] = (Class) smr.queryJavaType(qname, inputEncodingStyle);
		}
      }
    } 
    else 
    {
      names = new String[0];
      types = new Class[0];
    }
    
    // now prepare return value
    Output output = operation.getOutput();
    if(output != null) 
    {
      Part returnPart = null;
      if(returnName != null) 
      {
        returnPart = output.getMessage().getPart(returnName);
        if(returnPart == null) 
        {
          throw new WSIFException("no output part named "+returnName
                                    +" for bining operation "+getName());
        }
      } 
      else 
      {
        List parts = output.getMessage().getOrderedParts(null);
        if(parts.size() > 0) 
        {
          returnPart = (Part) parts.get(0);
          returnName = returnPart.getName();
        }
      }
      
      if (returnPart != null) 
      {
        QName partType = returnPart.getTypeName();
		if (partType == null) partType = returnPart.getElementName();
		
		org.apache.soap.util.xml.QName qname = 
		  new org.apache.soap.util.xml.QName(partType.getNamespaceURI(), 
		                                     partType.getLocalPart());

		try { returnType = (Class) mapOfUserTypes.get(qname); }
		catch(Throwable ignored) {}
			
		if(returnType == null) {
		  	returnType = (Class) smr.queryJavaType(qname, outputEncodingStyle);
		}
      }
    }
    prepared = true;
  }

  public boolean executeRequestResponseOperation(WSIFMessage input,
                                                 WSIFMessage output,
                                                 WSIFMessage fault)
    throws WSIFException {
    	
    TraceLogger.getGeneralTraceLogger().entry(
      new Object[] {input,output,fault});
      
    setAsyncOperation( false );

    boolean succ; 
    if ("document".equals(style))
	  succ = invokeRequestResponseOperationDocument(input,output,fault);
    else
      succ = invokeRequestResponseOperation(input,output,fault);
    
    TraceLogger.getGeneralTraceLogger().exit(new Boolean(succ));
    return succ;
  }
  
  public void executeInputOnlyOperation(WSIFMessage input)
    throws WSIFException {
    	
    TraceLogger.getGeneralTraceLogger().entry(input);
    setAsyncOperation( false );
  	invokeRequestResponseOperation(input,null,null);
    TraceLogger.getGeneralTraceLogger().exit();
  }
    
  /**
   * Invoke RPC operation using ApacheSOAP
   */
  public boolean invokeRequestResponseOperation(WSIFMessage input,
                                                WSIFMessage output,
                                                WSIFMessage fault)
    throws WSIFException
  {
	if (!prepared) prepare(input, output);

	boolean usingJROM = 
		(WSIFConstants.JROM_REPR_STYLE.equals(input.getRepresentationStyle())) ? true : false;	
    
    String encodingStyleURI = inputEncodingStyle;
    
    Call call = new Call();
    call.setEncodingStyleURI(encodingStyleURI);
    
	SOAPMappingRegistry reg = null;
	
	if (usingJROM) {
		reg = portInstance.getJROMSOAPMappingRegistry();	    
	} else {
		reg = portInstance.getSOAPMappingRegistry();
 	}
  	
  	if(reg != null) {
      call.setSOAPMappingRegistry(reg);
    }
    
    SOAPTransport st = getTransport();
    if(st != null) {
      call.setSOAPTransport (st);

      if (st instanceof SOAPJMSConnection && jmsHeader!=null) 
        ((SOAPJMSConnection)st).setJmsHeader(jmsHeader);
    }
    
    call.setTargetObjectURI(getInputNamespace());
    call.setMethodName(getName());
    
    Vector params = new Vector();
	Object partInst;
    for(int i = 0; i < names.length; ++i) {
      partInst = input.getObjectPart(names[i]);
      if(partInst == null)
      {
      	boolean foundInputParameter = false;
      	String paramName = names[i]; 
    	Iterator partsIterator = input.getPartNames();
    	while (partsIterator.hasNext()) {
    		String partName = (String) partsIterator.next();
    		if (partName == null || paramName == null) break;
    		if (partName.equals(paramName)) {
    			foundInputParameter = true;
    		}
    	}
    	if (!foundInputParameter)
        	throw new WSIFException(
          	"expected input message to have part with name '"+names[i]+"'");
      }
      Object value = partInst;
      // some runtime param validity check
      
      if (usingJROM) {
      	types[i] = com.ibm.jrom.JROMValue.class;
      } else {      
      	if(value != null
           && ! types[i].isPrimitive()
           && ! (types[i].isAssignableFrom(value.getClass()))) {
          throw new WSIFException("value "+value
                                  +" has unexpected type "+value.getClass()
                                  +" instead of "+types[i]);
      	}      
      }
      
      if (inJmsAttrs.containsKey(names[i])) 
        ((SOAPJMSConnection)st).setJmsAttribute((String)(inJmsAttrs.get(names[i])),value);
      else
      {
        Parameter param = new Parameter(
          names[i],
          types[i],
          value,
          inputEncodingStyle
        );
        params.addElement(param);
      }
    }
    
    call.setParams(params);
    
    // invoke the operation through ApacheSOAP
    Response resp;
    URL locationUri = portInstance.getEndPoint();
    try
    {
      if(DEBUG) System.err.println(
          "invoking operation "+getName()+" on "+locationUri);
      resp = call.invoke(locationUri, getSoapActionURI());
    }
    catch (SOAPException e)
    {
      // Log message
      MessageLogger messageLog =
      MessageLogger.newMessageLogger("WSIF", "com.ibm.wsif.catalog.Messages");
      messageLog.message(
        WSIFConstants.TYPE_ERROR,
        "WSIF.0005E",
        new Object[] { "ApacheSOAP", getName()});
      messageLog.destroy();
      // End message
        
      throw new WSIFException(
        "SOAPException: "+ e.getFaultCode()+
          e.getMessage(),
        e
      );
    }
    
    if (output!=null) {
      // Check the response.
      if (resp.generatedFault()) {
        org.apache.soap.Fault soapFault = resp.getFault();
      
        //TODO wrap soapFault into Message fault and return false
        throw new WSIFException(
          "SOAP Fault:" + soapFault.getFaultCode() + " "+
            soapFault.getFaultString()
        );
      }
    
      if(returnType != null) {
        Parameter retValue = resp.getReturnValue();
        Object result = retValue.getValue ();

        if (usingJROM)
        {
       	  output.setRepresentationStyle(WSIFConstants.JROM_REPR_STYLE);
          if (result != null
          	  && !com.ibm.jrom.JROMValue.class.isAssignableFrom(result.getClass())) {
			throw new WSIFException("return value "+result
									+" has unexpected type "+result.getClass()
									+" instead of an implementation of com.ibm.jrom.JROMValue");
		  }
        } else {   
          if(result != null
             && ! returnType.isPrimitive()
             && ! (returnType.isAssignableFrom(result.getClass())) )
          {
            throw new WSIFException(
              "return value "+result
              +" has unexpected type "+result.getClass()
              +" instead of "+returnType);
          }
        }
        output.setObjectPart(returnName, result);
      }
    }
    
    return true;
  }
  
  public boolean invokeRequestResponseOperationDocument(WSIFMessage input, 
                                                        WSIFMessage output, 
                                                        WSIFMessage fault) 
    throws WSIFException 
  {
	Envelope msgEnv = new Envelope();
	Body msgBody = new Body();
	Vector vect = new Vector();

	Iterator iterator = operation.getInput().getMessage().getParts().keySet().iterator();
	while (iterator.hasNext()) 
	{
	  String partName = (String) iterator.next();
	  Object part = input.getObjectPart(partName);
	  String encoding = null;
	  if ("literal".equals(inputUse))
	  {
		// Should also include namespace
		encoding = "literal";
	  }				
	  else
	  {
		encoding = this.inputEncodingStyle;
	  }
	  
	  PartSerializer partSerializer = null;
	  Object o = portInstance.getSOAPMappingRegistry().
	                          querySerializer(part.getClass(), inputUse);
	  if (o instanceof PartSerializer)
	  {
		PartSerializer tmp = (PartSerializer)o;
		try {
			partSerializer = (PartSerializer)tmp.getClass().newInstance();
		} catch(InstantiationException e) {
		} catch(IllegalAccessException e) {
		}
		partSerializer.setPart(part);
		Part modelPart = operation.getInput().getMessage().getPart(partName);
		javax.wsdl.QName partType = modelPart.getTypeName();
		if (partType == null)
		  partType = modelPart.getElementName();
		partSerializer.setPartQName(partType);
	  }
	  else  // document with soap encoding - never been tried
	  {
		partSerializer = new SOAPEncSerializerWrapper();
		partSerializer.setPart(part);
		((SOAPEncSerializerWrapper)partSerializer).setTargetSerializer((Serializer)o);
	  }
	
	  Bean bean = new Bean(partSerializer.getClass(), partSerializer);
	  vect.add(bean);
    }

	// create message envelope and body
	URL url = portInstance.getEndPoint();
	Envelope env = null;
	msgBody.setBodyEntries(vect);
	msgEnv.setBody(msgBody);

    SOAPTransport st = getTransport();
    if (st!=null && st instanceof SOAPJMSConnection && jmsHeader!=null) 
      ((SOAPJMSConnection)st).setJmsHeader(jmsHeader);

	// create and send message
	try 
	{
	  Message msg = new Message();
       if (st!=null) msg.setSOAPTransport(st);
        
	  msg.send(url, getSoapActionURI(), msgEnv);

 	  // receive response envelope
	  env = msg.receiveEnvelope();
	}
	catch (SOAPException exn) 
	{
	  WSIFException e = new WSIFException("SOAP Exception: " + exn.getMessage());
	  e.setTargetException(exn);
	  throw e;
	}

	Body retbody = env.getBody();
	java.util.Vector v = retbody.getBodyEntries();
	int index = 0;
	
	String encoding = null;
	if ("literal".equals(outputUse))
	{
	  // Should also include namespace
	  encoding = "literal";
	}				
	else
	{
	  encoding = this.outputEncodingStyle;
	}
	
	iterator = operation.getOutput().getMessage().getParts().keySet().iterator();
	while (iterator.hasNext()) 
	{
	  Element element = (Element) v.get(index++);
	  String partName = (String) iterator.next();
	  Part modelPart = operation.getInput().getMessage().getPart(partName);
	  javax.wsdl.QName partType = modelPart.getTypeName();
	  if(partType == null)
		partType = modelPart.getElementName();
	
	  PartSerializer partSerializer = null;
	  Object o = this.portInstance.getSOAPMappingRegistry().queryDeserializer(
	    new org.apache.soap.util.xml.QName(partType.getNamespaceURI(), 
	                                       partType.getLocalPart()), encoding);
	                                       
	  if (o instanceof PartSerializer)
	  {
	  	PartSerializer tmp = (PartSerializer)o;
		try {
			partSerializer = (PartSerializer)tmp.getClass().newInstance();
		} catch(InstantiationException e) {
		} catch(IllegalAccessException e) {
		}
		partSerializer.setPartQName(partType);
		partSerializer.unmarshall(null, null, element, null, null);
		Object retBean = partSerializer.getPart();
		output.setObjectPart(partName, retBean);			
	  }
	  else  // document with soap encoding - never been tried	
	  {
		Bean bean = ((Deserializer)o).unmarshall(null, null, element, null, null);
		Object retBean = bean.value;			
	  }
	}
	return true;
  }  

    /**
     * Performs a request response operation asynchronously.
     * 
     * @param input   input message to send to the operation
     * @return the correlation ID or the request. The correlation ID
     *         is used to associate the request with the WSIFOperation.
     * @exception WSIFException if something goes wrong.
     * @see WSIFOperation#executeRequestResponseAsync()
     */
    public Serializable executeRequestResponseAsync(WSIFMessage input)
                                                    throws WSIFException {

       TraceLogger.getGeneralTraceLogger().entry( new Object[] { input } );
       Serializable id = executeRequestResponseAsync( input, null );
       TraceLogger.getGeneralTraceLogger().exit( id );
       return id;

    }
   
    /**
     * Performs a request response operation asynchronously.
     * 
     * @param input   input message to send to the operation
     * @param handler   the response handler that will be notified 
     *        when the asynchronous response becomes available.
     * @return the correlation ID or the request. The correlation ID
     *         is used to associate the request with the WSIFOperation.
     * @exception WSIFException if something goes wrong.
     * @see WSIFOperation#executeRequestResponseAsync()
     */
    public Serializable executeRequestResponseAsync(WSIFMessage input,
                                                    WSIFResponseHandler handler)
                                                    throws WSIFException {
       TraceLogger.getGeneralTraceLogger().entry(
          new Object[] { input, handler } );
                                                    	
       if ( !portInstance.supportsAsync() ) {
         throw new WSIFException( "asynchronous operations not available" );
       }

       if ( "document".equals(style) ) {
         throw new WSIFException( "docstyle asynchronous operations not implemented" );
       }

       Serializable id = performAsyncOperation( input, handler );
       TraceLogger.getGeneralTraceLogger().exit( id );
       return id;
                                                    	
    }
   
    /**
     * Performs a request response operation asynchronously.
     * The underlying transport is JMS and supports asynchronous
     * opeations so tell the transport this is an async request
     * and invoke the request.
     */
    private Serializable performAsyncOperation(WSIFMessage input, 
                                               WSIFResponseHandler handler)
                                               throws WSIFException {
       setAsyncOperation( true );
       setResponseHandler( handler );
       SOAPJMSConnection transport = (SOAPJMSConnection) getTransport();
       transport.setWsifOperation( this );
       transport.setAsyncOperation( true );
       invokeRequestResponseOperation( input, null, null ); 
       transport.setAsyncOperation( false );
       return getAsyncRequestID();          

    }
  
    /**
     * fireAsyncResponse is called by an AsyncListener when a response
     * has been received for a previous executeRequestResponseAsync call.
     * This WSIFOperation will have been serialized in the correlation
     * service when the request was sent. When the AsynListener is notified
     * that a response has arrived for the request it unserializes this
     * WSIFOperation from the corelation service and calls this method. 
     * This method will then unmarshal the reponse and pass it to the
     * executeAsyncResponse method of the associated WSIFResponseHandler.
     * @see WSIFOperation#fireAsyncResponse()
     * @param response   an Object representing the response. The response
     *                   will be raw XML
     */
    public void fireAsyncResponse(Object response) throws WSIFException {
    
       TraceLogger.getGeneralTraceLogger().entry( new Object[] { response } );
                                                    	
       Response resp = processAsyncResponse( response ); 

       WSIFMessage outMsg = createOutputMessage();
       WSIFMessage faultMsg = createFaultMessage();
    
       prepareOutputMessage( resp, outMsg, faultMsg );

       getResponseHandler().executeAsyncResponse( outMsg, faultMsg );

       TraceLogger.getGeneralTraceLogger().exit( 
          new Object[] { outMsg, faultMsg } );
     
    }
  
    /**
     * This deserialises and unmarshalls the response message.
     * Its copied, with minor changes, from the 2nd half of 
     * the SOAP 2.2 Call class invoke method.
     */
    private Response processAsyncResponse(Object msg) throws WSIFException {
     
        if (msg == null) {
            throw new WSIFException("null response to async send");
        }

        if ( !(msg instanceof javax.jms.TextMessage) ) {
            throw new WSIFException("response not a javax.jms.TextMessage");
        }

        try {

           javax.jms.TextMessage m = (javax.jms.TextMessage)msg;
           String payloadStr = m.getText();

           // Get the response context.
           SOAPContext respCtx = new SOAPContext();
           respCtx.setRootPart(payloadStr,"text/xml");

           // Parse the incoming response stream.
           DocumentBuilder xdb = XMLParserUtils.getXMLDocBuilder();
           Document respDoc =
              xdb.parse(new InputSource(new StringReader(payloadStr)));
           Element payload = null;

           if (respDoc != null) {
              payload = respDoc.getDocumentElement();
           } else {  //probably does not happen
              throw new SOAPException (Constants.FAULT_CODE_CLIENT,
                 "Parsing error, response was:\n" + payloadStr);
           }

           // Unmarshall the response envelope.
           Envelope respEnv = Envelope.unmarshall(payload, respCtx);

           // set up up SOAPMappingRegistry
           SOAPMappingRegistry smr = WSIFPort_ApacheSOAP.createSOAPMappingRegistry();

           // Extract the response from the response envelope.
           Response resp = Response.extractFromEnvelope(respEnv, smr, respCtx);

           return resp;

        } catch (Exception ex) {
           throw new WSIFException( ex.getMessage() );
        }
    }

    /**
     * Prepares the WSIFMessages to pass to the async response
     * handler.
     * TODO: duplicate as the end of the sync invokeRequestResponse
     * method but need to sort out what to do about the returnType.  
     */
    private void prepareOutputMessage(Response resp,
                                      WSIFMessage outMsg,
                                      WSIFMessage faultMsg) 
                                      throws WSIFException {
       // Check the response.
       if (resp.generatedFault()) {
          org.apache.soap.Fault soapFault = resp.getFault();
          
          //TODO wrap soapFault into Message fault and return false
          throw new WSIFException(
             "SOAP Fault:" + soapFault.getFaultCode() + " "+
             soapFault.getFaultString()
          );
       }
    
       Parameter retValue = resp.getReturnValue();
       Object result = retValue.getValue ();
       outMsg.setObjectPart(returnName, result);
      
    }
 
  /**
   * Return name of operation.
   */
  public String getName() { return operation.getName(); }
  
  public String getSoapActionURI() { return actionUri; }
  public void setSoapActionURI(String value) { actionUri = value; }
  
  public String getInputNamespace() { return inputNamespace; }
  public void setInputNamespace(String value) { inputNamespace = value; }
  
  public String getInputEncodingStyle() { return inputEncodingStyle; }
  public void setInputEncodingStyle(String value) {
    inputEncodingStyle = value; }
  public String getOutputEncodingStyle() { return outputEncodingStyle; }
  public void setOutputEncodingStyle(String value) {
    outputEncodingStyle = value; }
  
  
  public List getPartNames() { return partNames; }
  public void setPartNames(List value) { partNames = value; }
  public String getReturnName() { return returnName; }
  public void setReturnName(String value) { returnName = value; }
  
  // where is WSDL defining this abstract mesage
  public Operation getOperation() { return operation; }
  public void setOperation(Operation value) { operation = value; }
  public Definition getDefinition() { return definition; }
  public void setDefinition(Definition value) { definition = value; }
  
  // WSIF related
  public WSIFPort_ApacheSOAP getDynamicWSIFPort() { return portInstance; }
  public void setDynamicWSIFPort(WSIFPort_ApacheSOAP value)
  {
    portInstance = value;
  }
  
  /**
   * Gets the style.
   * @return Returns a String
   */
  public String getStyle() { return style; }

  /**
   * Gets the response handler that will be used to
   * process the response to a asynchronous request.
   * @return the current response handler.
   * (package visable as called from Transport)
   */
  WSIFResponseHandler getResponseHandler() {
     return responseHandler;
  }

  /**
   * Gets the transport being used by this operation
   * @return the current transport
   */
  public SOAPTransport getTransport() {
     return portInstance.getSOAPTransport();
  }

  /**
   * Gets the correlation ID of the last request sent by the
   * executeRequestResponseAsync method.
   * @return the corelation ID of the previous request
   */
  public String getAsyncRequestID() {
     return asyncRequestID;
  }

  /**
   * Sets the correlation ID of the last request sent by the
   * executeRequestResponseAsync method.
   * @return the corelation ID of the previous request
   * (package visable as its called by WSIFJmsTransport)
   */
  void setAsyncRequestID(String asyncRequestID) {
     this.asyncRequestID = asyncRequestID;
  }

  /**
   * Sets if the currently executing request is an asynchronous request.
   * 
   * @param b   true if the current request is a asynchronous request,
   *            otherwise false
   */
  private void setAsyncOperation(boolean b) {
  	asyncOperation = b;
  }

  /**
   * Sets the style.
   * @param style The style to set
   */
  public void setStyle(String style) { this.style = style; }

  /**
   * Sets the response handler that will be used to
   * process the response to an asynchronous request.
   * @param responseHandler   the responseHandler to use 
   */
  private void setResponseHandler(WSIFResponseHandler responseHandler) {
     this.responseHandler = responseHandler;
  }

  /**
   * Gets the inputUse.
   * @return Returns a String
   */
  public String getInputUse() {	return inputUse; }

  /**
   * Sets the inputUse.
   * @param inputUse The inputUse to set
   */
  public void setInputUse(String inputUse) { this.inputUse = inputUse; }

  /**
   * Gets the outputUse.
   * @return Returns a String
   */
  public String getOutputUse() { return outputUse; }

  /**
   * Sets the outputUse.
   * @param outputUse The outputUse to set
   */
  public void setOutputUse(String outputUse) {this.outputUse = outputUse; }

  /**
   * Gets the partSerializerName.
   * @return Returns a String
   */
  public String getPartSerializerName() { return partSerializerName; }

  /**
   * Tests if the currently executing request is an asynchronous request.
   * 
   * @return   true if the current request is a asynchronous request,
   *            otherwise false
   */
  public boolean isAsyncOperation() {
  	return asyncOperation;
  }

  /**
   * Sets the partSerializerName.
   * @param partSerializerName The partSerializerName to set
   */
  public void setPartSerializerName(String partSerializerName) 
  {
	this.partSerializerName = partSerializerName;
  }

  /**
   * Sets the input Jms attributes for this operation
   */
  public void setInputJmsAttributes(List list) { inJmsAttrs = makeJmsAttributeMap(list); }
  
  /**
   * Sets the output Jms attributes for this operation
   */
  public void setOutputJmsAttributes(List list) { outJmsAttrs = makeJmsAttributeMap(list); }

  public void setInputJmsAttributes (HashMap hm) { inJmsAttrs  = hm; }
  public void setOutputJmsAttributes(HashMap hm) { outJmsAttrs = hm; }
  public HashMap getInputJmsAttributes () { return inJmsAttrs ; }
  public HashMap getOutputJmsAttributes() { return outJmsAttrs; }
  public void setJmsHeader(String value) { jmsHeader = value; }
  public String getJmsHeader() { return jmsHeader; }
  
  public String toString() 
  {
  	String buff=new String(super.toString()+":\n");
  	buff += "Operation:"+ (operation == null ? "null" : operation.getName());
  	int i;
    buff+=arrayString("names", names);
    buff+="\n";  	
    buff+=arrayString("types", types);
  	buff+=" inputEncodingStyle:"+inputEncodingStyle;
  	buff+=" inputNamespace:"+inputNamespace;
  	buff+=" returnName:"+returnName;
  	buff+=" returnType:"+returnType;
  	buff+=" outputEncodingStyle:"+outputEncodingStyle;
  	buff+=" actionUri:"+actionUri;
  	buff+=" typeMap:"+typeMap;
  	return buff;
  }
  
  private String arrayString(String str, Object[] objs) {
    if (objs==null) return "";
  	int i;
  	String buff = "";
  	for (i=0;i<names.length;i++) buff+=" "+str+"["+i+"]:"+objs[i];
  	return buff;
  }
  
}

