// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif;

/**
 * A WSIFPort represents the handle by which the operations
 * from the <portType> of the <port> of this WSIFPort can be
 * executed. This is an interface which must implemented by
 * specific implementations for the ports. That is, the actual
 * logic is dependent on the binding associated with this port.
 * An interface is used to enable dynamic implementation generation
 * using JDK1.3 dynamic proxy stuff.
 *
 * @author Paul Fremantle
 * @author Alekander Slominski
 * @author Matthew J. Duftler
 * @author Sanjiva Weerawarana
 * @author Nirmal Mukhi
 */
public interface WSIFPort {
  public WSIFOperation createOperation(String operationName) 
    throws WSIFException;
  
  public WSIFOperation createOperation(
    String operationName,String inputName,String outputName) 
    throws WSIFException;
  
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
   * @deprecated
   */
  public boolean executeRequestResponseOperation (String op,
                                                  WSIFMessage input,
                                                  WSIFMessage output,
                                                  WSIFMessage fault)
    throws WSIFException;
  
  /**
   * Execute an input-only operation.
   *
   * @param op name of operation to execute
   * @param input input message to send to the operation
   *
   * @exception WSIFException if something goes wrong.
   * @deprecated
   */
  public void executeInputOnlyOperation(String op, WSIFMessage input)
    throws WSIFException;
  
  /**
   * Create an input message that will be sent via this port.
   * It is responsibility of caller to set message name.
   * @deprecated
   */
  public WSIFMessage createInputMessage ();
  
  /**
   * Create an input message that will be sent via this port.
   *
   * @parameter name for created message
   * @deprecated
   */
  public WSIFMessage createInputMessage (String name);

  /**
   * Create an output message that will be received into via this port.
   * It is responsibility of caller to set message name.
   * @deprecated
   */
  public WSIFMessage createOutputMessage ();
  
  /**
   * Create an output message that will be received into via this port.
   *
   * @parameter name for created message
   * @deprecated
   */
  public WSIFMessage createOutputMessage (String name);

  /**
   * Create a fault message that may be received into via this port.
   * It is responsibility of caller to set message name.
   * @deprecated
   */
  public WSIFMessage createFaultMessage ();
  
  /**
   * Create a fault message that may be received into via this port.
   *
   * @parameter name for created message
   * @deprecated
   */
  public WSIFMessage createFaultMessage (String name);

   /**
   * Close this port; indicates that the user is done using it. This
   * is only essential for WSIFPorts that are being used in a stateful
   * or resource-shared manner. Responsible stubs will call this if
   * feasible at the right time.
   */
  public void close () throws WSIFException;
  
  /**
   * Tests if this port supports synchronous calls to operations.
   * 
   * @return true if this port support synchronous calls, otherwise false
   */
  public boolean supportsSync();
  
  /**
   * Tests if this port supports asynchronous calls to operations.
   * 
   * @return true if this port support synchronous calls, otherwise false
   */
  public boolean supportsAsync();
  

}

