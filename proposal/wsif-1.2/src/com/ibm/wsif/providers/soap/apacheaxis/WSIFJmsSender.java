// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.providers.soap.apacheaxis;

/**
 * @author Mark Whitlock
 */

import com.ibm.wsif.*;
import com.ibm.wsif.util.*;
import com.ibm.wsif.util.jms.*;
import com.ibm.wsif.WSIFCorrelationService;
import com.ibm.wsif.util.WSIFCorrelationServiceLocator;
import com.ibm.wsdl.extensions.jms.*;
import org.apache.axis.*;
import org.apache.axis.handlers.*;
import java.io.Serializable;
import java.util.*;

public class WSIFJmsSender extends BasicHandler 
{
    private static final long ASYNC_TIMEOUT = WSIFProperties.getAsyncTimeout();
	private static final String DUMMY_RESPONSE = "<?xml version='1.0' encoding='UTF-8'?>\n<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/1999/XMLSchema\">\n<SOAP-ENV:Body>\n<ns1:addEntryResponse xmlns:ns1=\"http://www.ibm.com/namespace/wsif/samples/ab\" SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n</ns1:addEntryResponse>\n\n</SOAP-ENV:Body>\n</SOAP-ENV:Envelope>";

    public void invoke(MessageContext messageContext) throws AxisFault
    {
      try 
      {
        // Determine if this is an async operation
        boolean asyncMode = 
           messageContext.isPropertyTrue( WSIFJmsTransport.ASYNCOPERATION );
        WSIFJmsDestination dest = (WSIFJmsDestination)messageContext.getProperty(WSIFJmsTransport.DESTINATION);

        Message message = messageContext.getRequestMessage();
        String contents = message.getSOAPPart().getAsString();
      
        if ( asyncMode ) {
           performAsyncSend( messageContext, dest, contents );
        } else {
           String id = dest.send(contents,null);
           String response = dest.receive(id);
           Message responseMessage = new Message(response);
           messageContext.setResponseMessage(responseMessage);
        }
      }
      catch (WSIFException we) { throw new AxisFault(we.toString()); }
    }

    public void undo(MessageContext messageContext) {}
    
    /**
     * Send the request asynchronously.
     * If there is a WISFResponseHandler associated with the operation then
     * the the WSIFOperation is stored in the correlation service with the JMS 
     * messgage ID of the request. A listener is then set to listen for the 
     * response to the request and when the response arrives the listener 
     * looks up the responses ID in the correlation service and forwards 
     * the response to the associated WSIFOperation.
     */
    private void performAsyncSend(MessageContext messageContext,
                                  WSIFJmsDestination dest,
                                  String data) throws WSIFException {
       String msgID;
       
       WSIFOperation_ApacheAxis wsifOp = 
          (WSIFOperation_ApacheAxis) messageContext.getProperty( 
             WSIFJmsTransport.WSIFOPERATION );
             
       if ( wsifOp.getResponseHandler() == null ) {
          msgID = dest.send( data );
       } else {
          WSIFCorrelationService correlator =
             WSIFCorrelationServiceLocator.getCorrelationService();
          synchronized( correlator ) {   
             msgID = dest.send( data );
             correlator.put( (Serializable)msgID, (Serializable)wsifOp, ASYNC_TIMEOUT );
          }
       }

       // Save msg ID in the WSIFop for this calling client
       wsifOp.setAsyncRequestID( msgID );

       // Axis doesn't like a null response so give it something
       Message responseMessage = new Message( DUMMY_RESPONSE );
       messageContext.setResponseMessage(responseMessage);
       
    }  
    
}

