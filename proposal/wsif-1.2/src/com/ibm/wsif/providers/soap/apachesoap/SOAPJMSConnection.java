// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.providers.soap.apachesoap;

import java.net.*;
import java.util.*;
import java.io.*;

import javax.jms.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import javax.naming.*;
import javax.naming.directory.*;

import org.apache.soap.*;
import org.apache.soap.encoding.*;
import org.apache.soap.encoding.soapenc.*;
import org.apache.soap.util.xml.*;
import org.apache.soap.util.mime.*;
import org.apache.soap.messaging.*;
import org.apache.soap.transport.*;
import org.apache.soap.rpc.*;

import com.ibm.wsif.*;
import com.ibm.wsif.util.*;
import com.ibm.wsif.util.jms.*;
import com.ibm.wsdl.extensions.jms.*;

/**
 * This class is a SOAPTransport that supports JMS.
 *
 * @author Mark Whitlock
 */

public class SOAPJMSConnection implements SOAPTransport 
{
  private BufferedReader responseReader = null;
  private SOAPContext responseSOAPContext = null;
  private WSIFJmsDestination destination = null;

  // folowing are for async operation
  private boolean asyncOperation = false;
  private WSIFOperation_ApacheSOAP wsifOperation = null;
  private static final long ASYNC_TIMEOUT = WSIFProperties.getAsyncTimeout();
   
  private static final String DUMMY_RESPONSE = "<?xml version='1.0' encoding='UTF-8'?>\n<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/1999/XMLSchema\">\n<SOAP-ENV:Body>\n<ns1:addEntryResponse xmlns:ns1=\"http://www.ibm.com/namespace/wsif/samples/ab\" SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n</ns1:addEntryResponse>\n\n</SOAP-ENV:Body>\n</SOAP-ENV:Envelope>";

  public SOAPJMSConnection(JmsAddress ja) throws WSIFException 
  {
    destination = new WSIFJmsDestination(WSIFJmsFinder.newFinder(ja), 
                                         ja.getJmsProvDestName(), 
                                         WSIFProperties.getSyncTimeout());
  }

  public Hashtable getHeaders () { return null; }
  public SOAPContext getResponseSOAPContext () { return responseSOAPContext; }
  public BufferedReader receive () { return responseReader; }

  /**
   * This send method is really doing a send followed by a receive. The
   * receive method just returns the BufferedReader that is set up by
   * this method.
   */
  public void send (URL sendTo, 
	                String action, 
	                Hashtable headers,
                    Envelope env, 
                    SOAPMappingRegistry smr,
                    SOAPContext ctx)    throws SOAPException {

    try {
      if ( isAsyncOperation() ) {
      	performAsyncSend( sendTo, action, headers, env, smr, ctx );
      } else {
        StringWriter payloadSW = new StringWriter ();
        env.marshall (payloadSW, smr, ctx);
        String id = destination.send(payloadSW.toString(),null);
        String response = destination.receive(id);
        responseSOAPContext = new SOAPContext();
        responseSOAPContext.setRootPart(response,"text/xml");
        responseReader = new BufferedReader(new StringReader(response));
      }
    } catch (IOException ioe) {
      // Not sure what the faultCode should be - this may be wrong
      throw new SOAPException("WSIF SOAPJMSConnection ",ioe.toString()); 
    } catch (MessagingException me) {
      // Not sure what the faultCode should be - this may be wrong
      throw new SOAPException("WSIF SOAPJMSConnection ",me.toString()); 
    }
  }
  
  /**
   * Send the request asynchronously.
   * After sending the request this associates the JMS message correlation
   * ID with the WSIFOperation in the correlation service, and stores the
   * the correlation ID in the message context so the WSIFOperation can
   * pass it back to the executeRequestResponseAsync caller.  
   */
  private void performAsyncSend(URL sendTo, 
	                            String action, 
	                            Hashtable headers,
                                Envelope env, 
                                SOAPMappingRegistry smr,
                                SOAPContext ctx)    throws IOException,
                                                           SOAPException, 
                                                           MessagingException {
     String msgID;
     StringWriter payloadSW = new StringWriter ();
     env.marshall (payloadSW, smr, ctx);
     WSIFOperation_ApacheSOAP wsifOp = (WSIFOperation_ApacheSOAP) getWsifOperation();
     
     if ( wsifOp.getResponseHandler() == null ) {
        msgID = destination.send(payloadSW.toString(),null);
     } else {
        destination.setAsyncMode( true );
        WSIFCorrelationService correlator =
           WSIFCorrelationServiceLocator.getCorrelationService();
        synchronized( correlator ) {   
           msgID = destination.send(payloadSW.toString(),null);
           correlator.put( 
              (Serializable)msgID,
              (Serializable)getWsifOperation(),
              ASYNC_TIMEOUT );
        }
        destination.setAsyncMode( false );
     }
     
     wsifOp.setAsyncRequestID( msgID );
     
     // SOAP doesn't like a null response so give it a dummy null
     responseSOAPContext = new SOAPContext();
     responseSOAPContext.setRootPart( DUMMY_RESPONSE, "text/xml");
     responseReader = new BufferedReader( new StringReader( DUMMY_RESPONSE ) );
     
  }  

  public void setAsyncOperation(boolean b) {
     asyncOperation = b;
  }

  public boolean isAsyncOperation() {
     return asyncOperation;
  }

  public WSIFOperation_ApacheSOAP getWsifOperation() {
     return wsifOperation;
  }

  public void setWsifOperation(WSIFOperation_ApacheSOAP op) {
     wsifOperation = op;
  }
  
  public void setJmsAttribute(String name, Object value) throws WSIFException { 
  	destination.setAttribute(name,value);  
  }

  public void setJmsHeader(String value) throws WSIFException { 
  	destination.setHeader(value);  
  }

  void close() throws WSIFException {
  	destination.close();
  }
}
