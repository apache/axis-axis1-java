// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.util;

import com.ibm.wsif.WSIFException;
import com.ibm.wsif.WSIFMessage;
import com.ibm.wsif.WSIFOperation;
import com.ibm.wsif.WSIFCorrelationService;
import com.ibm.wsif.WSIFResponseHandler;
import com.ibm.wsdl.extensions.jms.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.Serializable;
import java.util.*;

public abstract class WSIFDefaultOperation implements WSIFOperation {
     
	/**
	 * @see WSIFOperation#executeRequestResponseOperation(WSIFMessage, WSIFMessage, WSIFMessage)
	 */
	public abstract boolean executeRequestResponseOperation(
		WSIFMessage input,
		WSIFMessage output,
		WSIFMessage fault)
		throws WSIFException;
		
	/**
	 * @see WSIFOperation#executeInputOnlyOperation(WSIFMessage)
	 */
	public abstract void executeInputOnlyOperation(WSIFMessage input) throws WSIFException;

    /**
     * Default implementation of executeRequestResponseAsync.
     * By default async operation is not supported so this just
     * throws an exception.
	 * @see WSIFOperation#executeRequestResponseAsync()
     */
    public Serializable executeRequestResponseAsync(WSIFMessage input,
                                                    WSIFResponseHandler handler)
      throws WSIFException {
         throw new WSIFException( "asynchronous operations not supportted" );
    }
  
    /**
     * Default implementation of executeRequestResponseAsync.
     * By default async operation is not supported so this just
     * throws an exception.
	 * @see WSIFOperation#executeRequestResponseAsync()
     */
    public Serializable executeRequestResponseAsync(WSIFMessage input)
      throws WSIFException {
         throw new WSIFException( "asynchronous operations not supportted" );
    }
  
    /**
     * Default implemantation of fireAsyncResponse.
     * By default async operation is not supported so this just
     * throws an exception.
	 * @see WSIFOperation#fireAsyncResponse()
     * @param response   an Object representing the response
     */
    public void fireAsyncResponse(Object response) throws WSIFException {
         throw new WSIFException( "asynchronous operations not supportted" );
    }
		
	/**
	 * @see WSIFOperation#createInputMessage()
	 */
	public WSIFMessage createInputMessage() {
		return new WSIFDefaultMessage();
	}

	/**
	 * @see WSIFOperation#createInputMessage(String)
	 */
	public WSIFMessage createInputMessage(String name) {
		WSIFMessage msg = new WSIFDefaultMessage();
		if (msg!=null) msg.setName(name);
		return msg;
	}

	/**
	 * @see WSIFOperation#createOutputMessage()
	 */
	public WSIFMessage createOutputMessage() {
		return new WSIFDefaultMessage();
	}

	/**
	 * @see WSIFOperation#createOutputMessage(String)
	 */
	public WSIFMessage createOutputMessage(String name) {
		WSIFMessage msg = new WSIFDefaultMessage();
		if (msg!=null) msg.setName(name);
		return msg;
	}

	/**
	 * @see WSIFOperation#createFaultMessage()
	 */
	public WSIFMessage createFaultMessage() {
		return new WSIFDefaultMessage();
	}

	/**
	 * @see WSIFOperation#createFaultMessage(String)
	 */
	public WSIFMessage createFaultMessage(String name) {
		WSIFMessage msg = new WSIFDefaultMessage();
		if (msg!=null) msg.setName(name);
		return msg;
	}

	/**
	 * @see WSIFOperation#close()
	 */
	public void close() throws WSIFException {
	}
	
    /**
     * Utility method that sets the jms attributes for this operation
     */
    protected HashMap makeJmsAttributeMap(List list)
    {
  	  HashMap attrs = new HashMap(list.size());
  	  for (Iterator it=list.iterator(); it.hasNext(); )
  	  {
  	  	Object ee = it.next();
  	  	if (ee instanceof JmsAttribute) {
  	    	JmsAttribute attr = (JmsAttribute) ee;
	  	    attrs.put(attr.getPart(),attr.getName());
  	  	}
  	  }
  	  return attrs;
    }
}

