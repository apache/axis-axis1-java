// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif;

import java.io.Serializable;

public interface WSIFOperation extends Serializable {
	
  /**
   * Execute a request-response operation. The signature allows for
   * input, output and fault messages. WSDL in fact allows one to
   * describe the set of possible faults an operation may result
   * in, however, only one fault can occur at any one time.
   *
   * @param op name of operation to execute
   * @param input input message to send to the operation
   * @param output an empty message which will be filled in if
   *        the operation invocation succeeds. If it does not
   *        succeed, the contents of this message are undefined.
   *        (This is a return value of this method.)
   * @param fault an empty message which will be filled in if
   *        the operation invocation fails. If it succeeds, the
   *        contents of this message are undefined. (This is a
   *        return value of this method.)
   *
   * @return true or false indicating whether a fault message was
   *         generated or not. The truth value indicates whether
   *         the output or fault message has useful information.
   *
   * @exception WSIFException if something goes wrong.
   */
  public boolean executeRequestResponseOperation (WSIFMessage input,
                                                  WSIFMessage output,
                                                  WSIFMessage fault)
    throws WSIFException;


  /**
   * Execute an asynchronous request
   * @param input   input message to send to the operation
   *
   * @return the correlation ID or the request. The correlation ID
   *         is used to associate the request with the WSIFOperation.
   *
   * @exception WSIFException if something goes wrong.
   */
  public Serializable executeRequestResponseAsync (WSIFMessage input)
    throws WSIFException;
  
  /**
   * Execute an asynchronous request
   * @param input   input message to send to the operation
   * @param handler   the response handler that will be notified 
   *        when the asynchronous response becomes available.
   *
   * @return the correlation ID or the request. The correlation ID
   *         is used to associate the request with the WSIFOperation.
   *
   * @exception WSIFException if something goes wrong.
   */
  public Serializable executeRequestResponseAsync (WSIFMessage input,
                                                   WSIFResponseHandler handler)
    throws WSIFException;
  
  /**
   * fireAsyncResponse is called when a response has been received
   * for a previous executeRequestResponseAsync call.
   * @param response   an Object representing the response
   */
  public void fireAsyncResponse(Object response) throws WSIFException;
  
  /**
   * Execute an input-only operation.
   *
   * @param op name of operation to execute
   * @param input input message to send to the operation
   *
   * @exception WSIFException if something goes wrong.
   */
  public void executeInputOnlyOperation(WSIFMessage input)
    throws WSIFException;
  
  /**
   * Create an input message that will be sent via this port.
   * It is responsibility of caller to set message name.
   */
  public WSIFMessage createInputMessage ();
  
  /**
   * Create an input message that will be sent via this port.
   *
   * @parameter name for created message
   */
  public WSIFMessage createInputMessage (String name);

  /**
   * Create an output message that will be received into via this port.
   * It is responsibility of caller to set message name.
   */
  public WSIFMessage createOutputMessage ();
  
  /**
   * Create an output message that will be received into via this port.
   *
   * @parameter name for created message
   */
  public WSIFMessage createOutputMessage (String name);

  /**
   * Create a fault message that may be received into via this port.
   * It is responsibility of caller to set message name.
   */
  public WSIFMessage createFaultMessage ();
  
  /**
   * Create a fault message that may be received into via this port.
   *
   * @parameter name for created message
   */
  public WSIFMessage createFaultMessage (String name);

}

