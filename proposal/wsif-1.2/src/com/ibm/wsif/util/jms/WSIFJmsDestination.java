// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.util.jms;

import java.io.*;
import java.util.*;

import javax.jms.*;

import com.ibm.wsif.*;
import com.ibm.wsif.util.WSIFProperties;

/**
 * A WSIFJmsDestination is a pair of queues, one that read from and
 * the other that is written to. This class provides various methods
 * for different flavours of reading and writing messages to those
 * queues. This class hides the JMS interface.
 *
 * @author Mark Whitlock
 */
 
public class WSIFJmsDestination 
{
  protected WSIFJmsFinder   finder;
  protected QueueConnection connection = null;
  protected QueueSession    session    = null;
  protected Queue           readQ      = null;
  protected Queue           writeQ     = null;
  protected QueueSender     sender     = null;

  protected boolean asyncMode = false;
  protected WSIFJmsAsyncListener asyncListener = null;
  protected Queue syncTempQueue = null;

  protected WSIFJmsAttributes inAttrs;
  protected WSIFJmsAttributes outAttrs;
  protected Message lastMessage = null;
  protected String header = null;
  protected long timeout;
  protected String replyToName=null;

  /**
   * Public constructor.
   * @param finder used to find JMS objects.
   */
  public WSIFJmsDestination(WSIFJmsFinder finder) throws WSIFException 
  { this(finder,WSIFJmsConstants.WAIT_FOREVER); }

  /**
   * Public constructor.
   * @param finder used to find JMS objects.
   * @param timeout is the maximum time to wait on a synchronous receive
   */
  public WSIFJmsDestination(WSIFJmsFinder finder, long timeout) throws WSIFException 
  { this(finder,null,timeout); }

  /**
   * Public constructor.
   * @param finder used to find JMS objects.
   * @param altdestName is an alterative JMS provider destination name
   * @param timeout is the maximum time to wait on a synchronous receive
   */
  public WSIFJmsDestination(WSIFJmsFinder finder, String altDestName, long timeout) 
    throws WSIFException 
  {
    inAttrs  = new WSIFJmsAttributes(WSIFJmsAttributes.IN );
    outAttrs = new WSIFJmsAttributes(WSIFJmsAttributes.OUT);
    this.timeout = timeout;
    this.finder = finder;

    try 
    {
      connection = finder.getFactory().createQueueConnection();
      session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

      Destination initDest = finder.getInitialDestination();
      if (initDest!=null && altDestName!=null) throw new WSIFException(
        "Both jndiDestinationName and jmsproviderDestinationName cannot be specified");
      if (initDest==null && altDestName==null) throw new WSIFException(
        "Either jndiDestinationName or jmsproviderDestinationName must be specified");

      if (altDestName!=null) initDest = session.createQueue(altDestName);
      
      writeQ  = (Queue)initDest;
      readQ = null;

      connection.start();

    } catch( JMSException je ) { throw WSIFJmsConstants.ToWsifException(je); }
  }

  /**
   * Close all objects.
   */
  public void close() throws WSIFException {
  	try {
  	  QueueSender     sndr   = sender;
  	  QueueSession    sssn   = session;
  	  QueueConnection cnnctn = connection;
  	  
  	  sender     = null;      // Ensure these are nulled (flagging the close()),
  	  session    = null;      // even if a close() throws a JMSException
  	  connection = null;
  	  
      if (sndr  != null) sndr  .close();
      if (sssn  != null) sssn  .close();
      if (cnnctn!= null) cnnctn.close();
    } catch( JMSException je ) { throw WSIFJmsConstants.ToWsifException(je); }
  }
  	
  /**
   * close the destination at finalize.
   */
  public void finalize() throws WSIFException { close(); }
  
  /**
   * Send a message to the write queue
   * @param data is the message
   * @return the id of the message that was sent.
   */
  public String send(String data) throws WSIFException {
    return send(data,null);
  }

  /**
   * Send a message to the write queue
   * @param data is the message
   * @param id is the correlation id to set on the message
   * @return the id of the message that was sent.
   */
  public String send(String data, String id) throws WSIFException {
    areWeClosed();
    try {
      TextMessage msg = session.createTextMessage();
      msg.setText(data);
      return send(msg,id);
    } 
    catch( JMSException je ) { throw WSIFJmsConstants.ToWsifException(je); }
  }

  /**
   * Send a message to the write queue
   * @param data is the message
   * @return the id of the message that was sent.
   */
  public String send(Serializable data) throws WSIFException {
    return send(data,null);
  }

  /**
   * Send a message to the write queue
   * @param data is the message
   * @param id is the correlation id to set on the message
   * @return the id of the message that was sent.
   */
  public String send(Serializable data, String id) 
  	throws WSIFException {
    areWeClosed();

  	try {
      ObjectMessage msg = session.createObjectMessage();
      msg.setObject(data);
      return send(msg,id);
  	}
    catch( JMSException je ) { throw WSIFJmsConstants.ToWsifException(je); }
  }
  	
  /**
   * Sends a message to the write queue.
   */
  private String send(Message msg, String id) throws WSIFException 
  {
    areWeClosed();
  		
  	String msgId=null;
    boolean attrsSet = true;
  	
  	try 
  	{
  	  if (sender==null) sender = session.createSender(writeQ);

      // Process replyTo queues separately since they are not ordinary JMS attributes.
      if (inAttrs.containsKey(WSIFJmsConstants.REPLY_TO)) {
        String rto = (String)inAttrs.get(WSIFJmsConstants.REPLY_TO);
        setReplyToQueue(rto);
        inAttrs.remove(WSIFJmsConstants.REPLY_TO);
      }
      else setReplyToQueue();
      attrsSet = inAttrs.setAttributesOnProducer(sender);	  

      if (id!=null) msg.setJMSCorrelationID(id); 
      msg.setJMSReplyTo(readQ);
      	
      sender.send(msg);
      msgId = msg.getJMSMessageID();

      if (asyncMode) asyncListener.startListening( msgId );	
    } 
    catch( JMSException je ) { throw WSIFJmsConstants.ToWsifException(je); }
    finally 
    { // If attributes were set, trash the sender so we get the default attrs next time.
      if (attrsSet) sender = null;
      inAttrs.clear();
      header = null;
    }

    return msgId;
  } 	

  /**
   * Blocking receive for the wsif.syncrequest.timeout
   * @return the received message
   */
  public String receive() throws WSIFException { return receive(null); }

  /**
   * Blocking receive waits for a message for the wsif.syncrequest.timeout
   * @param id is the correlation id that the received message must have
   * @return the received message
   */
  public String receive(String id) throws WSIFException {
    areWeClosed();
    QueueReceiver rec = null;
    Message msg = null;
    String response = null;
    
    try {
      if (id!=null)
        rec = session.createReceiver(readQ,WSIFJmsConstants.JMS_CORRELATION_ID+"='"+id+"'");
      else 
        rec = session.createReceiver(readQ);

      msg = rec.receive(timeout);

      if (msg instanceof TextMessage) 
      {
      	response=((TextMessage)msg).getText();
        lastMessage = msg;
      }
      else throw new WSIFException("Reply message was not a TextMessage");

    } catch (JMSException e) { 
      throw WSIFJmsConstants.ToWsifException(e);
    } finally {
      try { if (rec!=null) rec.close(); }
      catch (Exception ignored) {}
    }

    return response;
  }

  /**
   * Set the replyTo queue to a temporary queue. 
   */
  public void setReplyToQueue() throws WSIFException 
  {
    areWeClosed();
  	
    Queue tmp;
    try {
      if (syncTempQueue==null) syncTempQueue = session.createTemporaryQueue();
      
  	  if (asyncMode) tmp = asyncListener.getReadTempQ();
   	  else           tmp = syncTempQueue;
    } catch (JMSException je) { throw WSIFJmsConstants.ToWsifException(je); }
    
    readQ = tmp; // So we don't overwrite readQ if there was an error.
    replyToName = null;
  }
  
  /**
   * Set the replyTo queue.
   * @param replyTo queue name.
   */
  public void setReplyToQueue(String replyTo) throws WSIFException 
  {
    areWeClosed();

    if (replyTo==null || replyTo.length()==0) {
      setReplyToQueue();
      return;
    }
    
    // If we're already using this queue, then reuse it.
    if (replyTo.equals(replyToName)) return;

    readQ = finder.findQueue(replyTo);

    replyToName = replyTo;
    if (asyncMode) asyncListener.setReadQ(readQ);
  }
  
  /**
   * Sets if this destination is to be used for asynchronous requests.
   * If this destination is to be used for asynchronous requests then a
   * WSIFJmsAsyncListener will be created to listen for the async responses.  
   * 
   * @param b   true if this destination is to be used for asynchronous requests,
   *            otherwise false.
   */
  public void setAsyncMode(boolean b) throws WSIFException 
  {
     areWeClosed();
  	 if ( asyncMode != b ) try 
  	 {
        asyncMode = b;
        
        if ( asyncMode && asyncListener == null ) 
          asyncListener = 
            new WSIFJmsAsyncListener( finder.getFactory() );
     } catch (JMSException je) { throw WSIFJmsConstants.ToWsifException(je); }
  }  
  
  /**
   * Sets a JMS attribute to a value. This attribute value will be only be used for
   * the next message that is sent, then the attribute will be reset.
   */
  public void setAttribute(String name, Object value) throws WSIFException
  {
  	if (name!=null && value!=null) inAttrs.put(name,value);
  }

  /**
   * Sets a HashMap of JMS attribute value pairs. The attribute values will be only 
   * be used for the next message that is sent, then all the attributes will be reset.
   */
  public void setAttributes(HashMap attrMap) throws WSIFException
  {
  	if (attrMap!=null && !attrMap.isEmpty()) inAttrs.putAll(attrMap);
  }

  /**
   * Gets a JMS attribute from the previous message that was received.
   */
  public Object getAttribute(String name) throws WSIFException
  {
  	if (lastMessage==null) return null;
  	if (outAttrs.isEmpty()) outAttrs.getAttributesFromMessage(lastMessage);
  	if (name!=null) return outAttrs.get(name);
  	return null;
  }

  /**
   * Gets all the JMS attributes from the previous message that was received.
   */
  public HashMap getAttributes() throws WSIFException
  {
  	if (lastMessage==null) return null;
  	if (outAttrs.isEmpty()) outAttrs.getAttributesFromMessage(lastMessage);
  	if (!outAttrs.isEmpty()) return outAttrs;
  	return null;
  }
  
  /**
   * Sets a JMS header
   */
  public void setHeader(String value) { header = value; }
  
  protected void areWeClosed() throws WSIFException 
  {
  	if (session==null) throw new WSIFException("Cannot use a closed destination");
  }
}
