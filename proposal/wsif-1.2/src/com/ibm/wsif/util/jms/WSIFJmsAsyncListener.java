// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.util.jms;

import com.ibm.wsif.WSIFOperation;
import com.ibm.wsif.WSIFException;
import com.ibm.wsif.WSIFCorrelationService;
import com.ibm.wsif.util.WSIFCorrelationServiceLocator;

import java.io.Serializable;
import java.util.*;
import javax.jms.*;

/**
 * WSIFJmsAsyncListener provides an implementation 
 * of a JMS Listener to be used for receiving
 * notifications that responses to asychronous sends have 
 * become available. This listener will then forward the 
 * response to the WSIFOperation that originated the request. 
 * 
 * @author Ant Elder
 */ 
public class WSIFJmsAsyncListener implements WSIFJmsListener {

    private QueueConnection asyncConn;
    private QueueSession asyncSess;
    private QueueReceiver asyncReceiver;
    private Queue tmpQ = null;
    private Queue readQ = null;
	
    /**
     * Create a new WSIFJmsAsyncListener listenening on the specified Queue.
     * 
     * @param factory   the JMS QueueConnectionFactory
     * @param readQ    the queue to listen on for messages. If this is
     *                 null then a temporary queue is created to listen on.
     *                 The client can find the temporary queue with the
     *                 getReadQ() method.
     */ 
    public WSIFJmsAsyncListener(QueueConnectionFactory factory) 
	                                                        throws JMSException {
        asyncConn = factory.createQueueConnection();
        asyncSess = asyncConn.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
	}
	
    /**
     * Returns the Queue that this WSIFJmsAsyncListener is listening on.
     * 
     * @return the queue being listened on for messages.
     */
    public Queue getReadTempQ() throws JMSException {
       if (tmpQ==null) tmpQ = asyncSess.createTemporaryQueue();
       readQ = tmpQ;
       return tmpQ;
    }

    /**
     * Sets the Queue that this WSIFJmsAsyncListener is listening on.
     */
    public void setReadQ(Queue q) {
    	readQ = q;
    }

    /**
     * Starts this listener listening for any messages on the readQ
     */
	public void startListening() throws JMSException {
		startListening( null );
	}

    /**
     * Starts this listener listening on the readQ for a message with
     * a specific correlation ID.
     * 
     * @param id   the correlation ID of the message to listen for
     */
	public void startListening(String id) throws JMSException {
        if ( id != null ) {
           asyncReceiver = 
             asyncSess.createReceiver(readQ,WSIFJmsConstants.JMS_CORRELATION_ID+"='"+id+"'");
        } else {
           asyncReceiver = asyncSess.createReceiver( readQ );
        }
        asyncReceiver.setMessageListener( this );
        asyncConn.setExceptionListener( this ); 
        asyncConn.start();
	}

    /**
     * Stops this listener, freeing the JMS resources.
     * ???ant??? this isn't working properly yet. Will be sorted with the
     *  JMS changes to have the WSIFPort close method close the listener
     */
	public void endListening() {
	   try {
          if ( asyncConn != null ) asyncConn.close();
	   } catch (JMSException ex ) {
          // log exception
          ex.printStackTrace();
	   }
	}

    /**
     * onMessage is called by the remote service to notify us 
     * that the response to an asynchronus send is now available.
     * 
     * @parm id   the id of the response handler that has been 
     *            stored in the correlation service.
     */
    public void onMessage(final Message msg) {
    	System.out.println( "WSIFJmsAsyncListener.onMessage called" );

    	WSIFCorrelationService cs = 
    	   WSIFCorrelationServiceLocator.getCorrelationService();

        try {	
           Serializable so;
           synchronized( cs ) { 
              so = cs.get( msg.getJMSCorrelationID() ); 
           }
           if ( so != null && so instanceof WSIFOperation ) {      
    	      cs.remove( msg.getJMSCorrelationID() ); 
              ((WSIFOperation) so).fireAsyncResponse( msg );
           }
        } catch (WSIFException ex) {
    	    // log exception
        	ex.printStackTrace();
        } catch (JMSException ex) {
        	ex.printStackTrace();
       	    // log exception
        } finally {
            //endListening(); ???ant???when to do this?	
        }

    }
    
    /**
     * ???ant??? what to do about exceptions?
     */
    public void onException(final JMSException ex) {
    	System.err.println( "WSIFJmsAsyncListener.onException called" );
      	ex.printStackTrace();
    	// log exception
        endListening();	
    }
    
}


