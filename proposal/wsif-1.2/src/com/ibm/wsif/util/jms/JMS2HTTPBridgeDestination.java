// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.util.jms;

import java.util.*;
import javax.jms.*;
import com.ibm.wsif.*;

/**
 * Server destination for the JMS2HTTPBridge
 * @author Mark Whitlock
 */
public class JMS2HTTPBridgeDestination extends WSIFJmsDestination
{
  public static final String COLDSTART = "cold";
  public static final String WARMSTART = "warm";
  
  private static final ArrayList allStarts=
    new ArrayList(Arrays.asList(new Object[]{COLDSTART,WARMSTART}));

  /**
   * Public constructor.
   * @param finder used to find JMS objects.
   * @param altdestName is an alterative JMS provider destination name
   * @param timeout is the maximum time to wait on a synchronous receive
   * @param startType is WARMSTART or COLDSTART. Cold means wipe the read queue on startup.
   */
  public JMS2HTTPBridgeDestination(
    WSIFJmsFinder finder, String altDestName, long timeout, String startType) 
    throws WSIFException 
  {
  	super(finder,altDestName,timeout);

    // Swap the queues because we're a server not a client.  	
  	readQ = writeQ;
  	writeQ = null;  	

    if (!allStarts.contains(startType)) 
      throw new WSIFException("StartType must either be warm or cold");

    if (COLDSTART.equals(startType)) 
    {
      System.out.println("Wiping messages off the read queue");
      Message msg=null;
      try 
      {
        QueueReceiver rec = session.createReceiver(readQ);
        for (;;) 
        {
          msg=rec.receive(100);
          if (msg!=null) System.out.println("Removing an input message");
          else break;
        }
      } catch (Exception ignored) {}
    }
  }

  /**
   * Create a listener thread to listen for messages. This waits forever until it gets 
   * an InterruptedException. This listens on the read queue.
   * @param listener is the JMS message and exception callback interface implementation
   */
  public void listen(WSIFJmsListener listener) throws WSIFException { listen(listener,readQ); }

  /**
   * Create a listener thread to listen for messages. This waits forever until it gets 
   * an InterruptedException. 
   * @param listener is the JMS message and exception callback interface implementation
   * @param queue to listen on
   */
  public void listen(WSIFJmsListener listener, Queue queue) throws WSIFException 
  {
    areWeClosed();

	try {
      QueueReceiver qr = session.createReceiver(queue);
      qr.setMessageListener(listener);
      connection.setExceptionListener(listener);

      connection.start();

      for (int i=1; !Thread.interrupted(); i++) {
	      Thread.yield();
	      Thread.sleep(5000);
	      System.out.println("Waiting... " + i);
      }
    } catch (JMSException je) { throw WSIFJmsConstants.ToWsifException(je); 
    } catch (InterruptedException ignored) {
      System.out.println("Exitting");
    }
  }
  
  // Stop anyone overwriting our readQ
  public void setReplyToQueue() throws WSIFException {}
  public void setReplyToQueue(String replyTo) throws WSIFException {}

  /**
   * Set the replyTo queue. Special bridge version.
   * @param replyTo queue.
   */
  public void setReplyToQueue(Queue replyTo) throws WSIFException 
  {
    areWeClosed();

    if (writeQ==null) writeQ = replyTo;
    else if (!writeQ.equals(replyTo)) {
	    
      if (sender!=null) {
        try { sender.close(); } catch (Exception e) {}
        sender=null;
      }
      writeQ = replyTo;
    }
  }

}

