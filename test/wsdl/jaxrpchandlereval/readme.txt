This testcase tests the evaluation of JAX-RPC handler methods return value.

There are two client handlers: ClientHandler and ClientHander2.
There are two server handlers: ServiceHandler1 and Servicehandler2.

The order of handler is :
  
   ClientHandler -> ClientHandler2  ~~~~~> ServiceHandler1 --> Servicehandler2 

Each handler adds notes on the soap header and the test client check it to
determine handler's call order.

There are five tests in it.

1. Happy Path
  This is the normal operation.
  All handler's handlerRequest and handleResponse methods are called.
  The test client check that all the client/server handler are called in order.

2. Server-Return-False
  This checks the following scenario: 
    (SH for server-side handler, CH for client-side handler)

  SH1.handleRequest
  SH2.handleRequest  <-- return false
  SH2.handleResponse
  SH1.handleResponse

3. Server-throw-SOAPFaultexception
  This checks the following scenario:

  SH1.handleRequest
  SH2.handleRequest  <-- throws SOAPFaultException
  SH2.handleFault
  SH1.handleFault

4. Client-Return-False
  This checks the following scenario:

  CH1.handleRequest
  CH2.handleRequest  <-- return false;
  CH2.handleResponse
  CH1.handleResponse

5. Client-Return-JAXRPCException
  This checks the following scenario:

  CH1.handleRequest
  CH2.handleRequest  <-- throws JAXRPCException. stop processing and destroy handlers
  

The original test program is from Shantanu Sen(ssen@pacbell.net). 
This document and converted test case is compiled by Jongjin Choi(jongjin.choe@gmail.com).


