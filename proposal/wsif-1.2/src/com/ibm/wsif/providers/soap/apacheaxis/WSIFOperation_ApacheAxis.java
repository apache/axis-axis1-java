// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.

package com.ibm.wsif.providers.soap.apacheaxis;

import com.ibm.wsif.*;
import com.ibm.wsif.logging.*;
import com.ibm.wsif.providers.*;
import com.ibm.wsif.util.*;
import com.ibm.wsif.util.jms.*;
import com.ibm.wsdl.extensions.jms.*;
import java.io.PrintStream;
import java.util.*;
import java.io.Serializable;
import javax.jms.TextMessage;
import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.*;  // was java.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.QName;
import javax.xml.rpc.JAXRPCException;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.Message;
import org.apache.axis.message.*;
import org.apache.axis.client.Service;
import org.apache.axis.client.Transport;
import org.apache.axis.client.Call;
import org.apache.axis.encoding.BeanSerializer;
import org.apache.axis.encoding.TypeMappingRegistry;

// Referenced classes of package com.ibm.wsif.providers.soap.apacheaxis:
//            WSIFPort_ApacheAxis

public class WSIFOperation_ApacheAxis
  extends WSIFDefaultOperation 
  implements WSIFOperation, Serializable
{
	private static final boolean DEBUG = false;
	transient protected WSIFPort_ApacheAxis portInstance;
	transient protected Operation operation;
	transient protected Definition definition;
	transient protected List partNames;
	transient protected String names[];
	transient protected Class types[];
	transient protected String inputEncodingStyle;
	transient protected String inputNamespace;
	transient protected String outputEncodingStyle;
	transient protected String actionUri;
    transient protected WSIFDynamicTypeMap typeMap;
    transient protected HashMap inJmsAttrs = new HashMap();
    transient protected HashMap outJmsAttrs = new HashMap(); 
    transient protected String jmsHeader = null;
    
    // for async operation
    transient protected boolean asyncOperation;
    transient protected String asyncRequestID;

    // everything other than what is needed to process async response should be transient
    protected WSIFResponseHandler responseHandler;  
	protected String returnName=null;
	protected Class returnType=null;
    
    public WSIFOperation_ApacheAxis(WSIFPort_ApacheAxis wsifport_apacheaxis, Operation operation1, WSIFDynamicTypeMap wsifdynamictypemap)
        throws WSIFException
    {
        inputEncodingStyle = "http://schemas.xmlsoap.org/soap/encoding/";
        outputEncodingStyle = "http://schemas.xmlsoap.org/soap/encoding/";
        typeMap = wsifdynamictypemap;
        setDynamicWSIFPort(wsifport_apacheaxis);
        setOperation(operation1);
        setDefinition(wsifport_apacheaxis.getDefinition());
    }

    /**
     * Create a new copy of this object. This is not a clone, since 
     * it does not copy the referenced objects as well.
     */
    public WSIFOperation_ApacheAxis copy() throws WSIFException {
  	
  	   WSIFOperation_ApacheAxis op = 
  	     new WSIFOperation_ApacheAxis(portInstance,operation,typeMap);
  	   
  	   op.setSoapActionURI      (getSoapActionURI      ());
  	   op.setInputNamespace     (getInputNamespace     ());
  	   op.setInputEncodingStyle (getInputEncodingStyle ());
  	   op.setOutputEncodingStyle(getOutputEncodingStyle());
  	   op.setPartNames          (getPartNames          ());
  	   op.setReturnName         (getReturnName         ());
  	   op.setAsyncOperation     (isAsyncOperation      ());
  	   op.setResponseHandler    (getResponseHandler    ());
       op.setInputJmsAttributes (getInputJmsAttributes ());
  	   op.setOutputJmsAttributes(getOutputJmsAttributes());
  	   op.setJmsHeader          (getJmsHeader          ());
  	 
  	   return op;
    }
  
    public Definition getDefinition()
    {
        return definition;
    }

    public WSIFPort_ApacheAxis getDynamicWSIFPort()
    {
        return portInstance;
    }

    public String getInputEncodingStyle()
    {
        return inputEncodingStyle;
    }

    public String getInputNamespace()
    {
        return inputNamespace;
    }

    public String getName()
    {
        return operation.getName();
    }

    public Operation getOperation()
    {
        return operation;
    }

    public String getOutputEncodingStyle()
    {
        return outputEncodingStyle;
    }

    public List getPartNames()
    {
        return partNames;
    }

    public String getReturnName()
    {
        return returnName;
    }

    public String getSoapActionURI()
    {
        return actionUri;
    }

    public Transport getTransport() {
    	return portInstance.getAxisTransport();
    }

    public String getAsyncRequestID() {
    	return asyncRequestID;
    }

    /**
     * Tests if the currently executing request is an asynchronous request.
     * 
     * @return   true if the current request is a asynchronous request,
     *            otherwise false
     */
    public boolean isAsyncOperation() {
    	return asyncOperation;
    }

    // package visable as its used by WSIFJmsTransport
    void setAsyncRequestID(String asyncRequestID) {
    	this.asyncRequestID = asyncRequestID;
    }

    public void executeInputOnlyOperation(WSIFMessage wsifmessage)
        throws WSIFException
    {
        setAsyncOperation( false );
    	invokeRequestResponseOperation(wsifmessage,null,null);
    	return;
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

       Serializable id = performAsyncOperation( input, handler );
       TraceLogger.getGeneralTraceLogger().exit( id );
       return id;
       	
    }
  
    /** 
     * Performs a request response operation asynchronously.
     * The underlying transport is JMS and supports asynchronous
     * opeations so tell the transport this is an async request
     * and invoke the request on a copy of this WSIFOperation.
     */
    private Serializable performAsyncOperation(WSIFMessage input, 
                                               WSIFResponseHandler handler)
                                               throws WSIFException {
       setAsyncOperation( true );
       setResponseHandler( handler );
       WSIFJmsTransport transport = (WSIFJmsTransport) getTransport();
       transport.setWsifOperation( this );
       transport.setAsyncOperation( "true" );
       invokeRequestResponseOperation( input, null, null ); 
       transport.setAsyncOperation( "false" );
       return getAsyncRequestID();        

    }
  
    /**
     * fireAsyncResponse is called by an AsyncListener when a response
     * has been received for a previous executeRequestResponseAsync call.
     * It passes the response to the executeAsyncResponse method of the
     * associated WSIFResponseHandler.
     * @see WSIFOperation#fireAsyncResponse()
     * @param response   an Object representing the response. The response
     *            should be a JMS TextMessage containging the XML response.
     */
    public void fireAsyncResponse(Object response) throws WSIFException {

       TraceLogger.getGeneralTraceLogger().entry( new Object[] { response } );
                                                    	
       if( ! (response  instanceof TextMessage) ) {
          throw new WSIFException(
             "expecting response of type JMS TextMessage, recieved: " +
             response.getClass().getName() );
       } 
       
       Object result = processAsyncResponse( (TextMessage)response ); 

       //TODO: need to turn exceptions into Fault msg to pass to handler
       if( result instanceof AxisFault) {
          throw new WSIFException(((AxisFault)response).getFaultString());
       } else if(returnType == null) {
          throw new WSIFException("Received async response: " + response + 
             " but Operation returnType is null");
       }

       if( !returnType.isAssignableFrom( result.getClass() ) ) {
          throw new WSIFException("return value " + result + 
             " has unexpected type " + result.getClass() + " instead of " + returnType);
       } 

       WSIFMessage outMsg = createOutputMessage();
       outMsg.setObjectPart(returnName, result);
       getResponseHandler().executeAsyncResponse( outMsg, null);

       TraceLogger.getGeneralTraceLogger().exit( 
          new Object[] { outMsg } );

    }
  
    /**
     * This deserialises and unmarshalls the response message.
     * This is copied, with minor changes, from the 2nd half
     * of the Apache Axis Call class invoke method.
     */
    private Object processAsyncResponse(TextMessage msg) throws WSIFException {
     
        if (msg == null) {
            throw new WSIFException("null response to async send");
        }
        
        try {
           Message responseMessage = new Message( msg.getText() );
           responseMessage.setMessageType( Message.RESPONSE );
           
           Service service = new Service();
           MessageContext msgContext = new MessageContext( service.getEngine() );
           msgContext.setResponseMessage( responseMessage );
                     	
           Message resMsg = msgContext.getResponseMessage();
           org.apache.axis.SOAPPart soapPart = resMsg.getSOAPPart();
           
           SOAPEnvelope resEnv = (SOAPEnvelope)soapPart.getAsSOAPEnvelope();

           Object b = resEnv.getFirstBody();
           if ( b instanceof SOAPFaultElement ) {
              return ( (SOAPFaultElement) b ).getFault();
           }

          // RPCElement body = (RPCElement)resEnv.getFirstBody();
           RPCElement body = (RPCElement)b;

           Object result = null;
           Vector outParams;
           Vector resArgs = body.getParams();

           if (resArgs != null && resArgs.size() > 0) {
              RPCParam param = (RPCParam)resArgs.get(0);
              result = param.getValue();

              /**
               * Are there out-params?  If so, return a Vector instead.
               * ???ant??? Whats going on here? Call seemed to never use
               * outParams again???
               */
              if (resArgs.size() > 1) {
                 outParams = new Vector();
                 for (int i = 1; i < resArgs.size(); i++) {
                    outParams.add(resArgs.get(i));
                 }
              }
           }
           return result;
        } catch (Exception ex) {
           throw new WSIFException( ex.getMessage() );
        }
   	
    }
 
    public boolean executeRequestResponseOperation(
      WSIFMessage wsifmessage, WSIFMessage wsifmessage1, WSIFMessage wsifmessage2)
        throws WSIFException
    {
        setAsyncOperation( false );

    	return invokeRequestResponseOperation(
    	  wsifmessage,wsifmessage1,wsifmessage2);
    }
    
    public boolean invokeRequestResponseOperation(
      WSIFMessage wsifmessage, WSIFMessage wsifmessage1, WSIFMessage wsifmessage2)
        throws WSIFException
    {

        Call call = null;
	    Transport axistransport = getTransport();

        java.net.URL url = portInstance.getEndPoint();
        try
        {
          if (url!=null)
          {
            call = new Call(url);
            if (axistransport != null) {
            	axistransport.setUrl(url.toString());
            }
          }
          else call = new Call(new Service());
        } catch (JAXRPCException e) {
			e.printStackTrace();
			throw new WSIFException(e.toString());
		}
        
        WSIFJmsDestination dest=null;
        if (axistransport != null) 
        {
        	call.setTransport(axistransport);
            if (axistransport instanceof WSIFJmsTransport)
            {
              dest = ((WSIFJmsTransport)axistransport).getDestination();
              if (jmsHeader!=null) dest.setHeader(jmsHeader);
              dest.setAsyncMode(isAsyncOperation());
            }
        }
		
        if(names == null) prepare(call);
            
        ArrayList objects = new ArrayList();
        for(int i=0; i<names.length; i++)
        {
            Object obj = wsifmessage.getObjectPart(names[i]);
            if(obj != null)
            {
              if (types[i]==null)
              	throw new WSIFException("Cannot map type "+names[i]);
              
              if (!types[i].isAssignableFrom(obj.getClass()))
                throw new WSIFException("value " + obj + 
                  " has unexpected type " + obj.getClass() + 
                  " instead of " + types[i]);
            }
            if (inJmsAttrs.containsKey(names[i]) && dest!=null) 
              dest.setAttribute((String)(inJmsAttrs.get(names[i])),obj);
            else 
              objects.add(obj);
        }
        
        Object response;
        try {
            response = call.invoke(getInputNamespace(), operation.getName(), objects.toArray());
        } catch (AxisFault e) {
			e.printStackTrace();
			throw new WSIFException(e.getFaultString());
		}
        if(response instanceof AxisFault)
        {
            throw new WSIFException(((AxisFault)response).getFaultString());
        }
        
        if( !isAsyncOperation() && returnType != null )
        {
            if(response != null && !returnType.isAssignableFrom(response.getClass()))
                throw new WSIFException("return value " + response + 
                  " has unexpected type " + response.getClass() + " instead of " + returnType);
            wsifmessage1.setObjectPart(returnName, response);
        }
        return true;
    }
 

    private void prepare(Call call) throws WSIFException 
    {
        TypeMappingRegistry registry = call.getMessageContext().getTypeMappingRegistry();
		Class objClass;
		String namespaceURI, localPart;
		WSIFDynamicTypeMapping wsifdynamictypemapping;
        for (Iterator iterator = typeMap.iterator(); iterator.hasNext();) {
			wsifdynamictypemapping = (WSIFDynamicTypeMapping)iterator.next();
			objClass = wsifdynamictypemapping.getJavaType();
			namespaceURI = wsifdynamictypemapping.getXmlType().getNamespaceURI();
			localPart = wsifdynamictypemapping.getXmlType().getLocalPart();
			javax.xml.rpc.namespace.QName qn = new javax.xml.rpc.namespace.QName(namespaceURI, localPart);
			call.addSerializer(objClass, qn, new BeanSerializer(objClass));
			call.addDeserializerFactory(qn, objClass, BeanSerializer.getFactory());
		}

        Input input = operation.getInput();
        if(input != null) {
            Object obj;
            if(partNames != null) {
                obj = new Vector();
                Part part1;
                for(Iterator iterator1 = partNames.iterator(); iterator1.hasNext(); ((List) (obj)).add(part1))
                {
                    String s = (String)iterator1.next();
                    part1 = input.getMessage().getPart(s);
                    if(part1 == null)
                        throw new WSIFException("no input part named " + s + " for binding operation " + getName());
                }

            } else {
                obj = input.getMessage().getOrderedParts(null);
            }
            int i = ((List) (obj)).size();
            names = new String[i];
            types = new Class[i];
            for(int j = 0; j < i; j++)
            {
                Part part2 = (Part)((List) (obj)).get(j);
                names[j] = part2.getName();
                javax.wsdl.QName qname1 = part2.getTypeName();
                if(qname1 == null)
                    throw new WSIFException("part " + names[j] + " must have type name declared");
                javax.xml.rpc.namespace.QName axisqname =
                	new javax.xml.rpc.namespace.QName(qname1.getNamespaceURI(), qname1.getLocalPart());
                types[j] = registry.getClassForQName(axisqname);
            }

        } else
        {
            names = new String[0];
            types = new Class[0];
        }
        Output output = operation.getOutput();
        if(output != null)
        {
            Part part = null;
            if(returnName != null)
            {
                part = output.getMessage().getPart(returnName);
                if(part == null)
                    throw new WSIFException("no output part named " + returnName + " for bining operation " + getName());
            } else
            {
                List list = output.getMessage().getOrderedParts(null);
                if(list.size() > 0)
                {
                    part = (Part)list.get(0);
                    returnName = part.getName();
                }
            }
            if(part != null)
            {
                javax.wsdl.QName qname = part.getTypeName();
                javax.xml.rpc.namespace.QName retqname =
					new javax.xml.rpc.namespace.QName(qname.getNamespaceURI(), qname.getLocalPart());
                returnType = registry.getClassForQName(retqname);
            }
        }
    }

    /**
     * Sets the response handler that will be used to
     * process the response to an asynchronous request.
     * @param responseHandler   the responseHandler to use 
     */
    private void setResponseHandler(WSIFResponseHandler responseHandler) {
       this.responseHandler = responseHandler;
    }

    /**
     * Gets the response handler that will be used to
     * process the response to a asynchronous request.
     * @return the current response handler.
     * package visable as its used by the transport
     */
    WSIFResponseHandler getResponseHandler() {
       return responseHandler;
    }

    public void setDefinition(Definition definition1)
    {
        definition = definition1;
    }

    public void setDynamicWSIFPort(WSIFPort_ApacheAxis wsifport_apacheaxis)
    {
        portInstance = wsifport_apacheaxis;
    }

    public void setInputEncodingStyle(String s)
    {
        inputEncodingStyle = s;
    }

    public void setInputNamespace(String s)
    {
        inputNamespace = s;
    }

    public void setOperation(Operation operation1)
    {
        operation = operation1;
    }

    public void setOutputEncodingStyle(String s)
    {
        outputEncodingStyle = s;
    }

    public void setPartNames(List list)
    {
        partNames = list;
    }

    public void setReturnName(String s)
    {
        returnName = s;
    }

    public void setSoapActionURI(String s)
    {
        actionUri = s;
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
	 * Creates a new output WSIFMessage. This overrides the 
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
	
}
