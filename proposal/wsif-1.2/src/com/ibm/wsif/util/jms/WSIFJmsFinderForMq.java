// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.util.jms;

import java.util.*;
import javax.jms.*;
import com.ibm.wsif.*;

/**
 * Finds JMS objects by going directly to MQSeries.
 * @author Mark Whitlock
 */
class WSIFJmsFinderForMq extends WSIFJmsFinder 
{
  private QueueConnectionFactory factory;
  private Destination initialDestination;
  private String style;

  WSIFJmsFinderForMq (String jmsVendorURL, String implSpecURL) throws WSIFException
  {
    throw new WSIFException("not yet implemented");
    
/*    try {
      factory = new MQQueueConnectionFactory();
      factory.setQueueManager("");

      connection = factory.createQueueConnection();
      session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

      readQ  = session.createQueue(readQName );
      writeQ = session.createQueue(writeQName);
      connection.start();

      if (startType==COLDSTART) {
        System.out.println("Wiping messages off the queues");
        Message msg=null;
        try {
	      if (receiver==null) receiver=session.createReceiver(readQ);
          for (;;) {
            msg=receiver.receive(100);
            if (msg!=null) System.out.println("Removing an input message");
            else break;
          }
        } catch (Exception e) {}

        try {
	      QueueReceiver tmpRec = session.createReceiver(writeQ);
          for (;;) {
            msg=tmpRec.receive(100);
            if (msg!=null) System.out.println("Removing an output message");
            else break;
          }
        } catch (Exception e) {}
      }
      
    } catch( JMSException je ) {
      System.out.println("caught JMSException: " + je);
      Exception le = je.getLinkedException();
      if (le != null) System.out.println("linked exception: "+le);
	
    } catch( Exception e ) {
      System.out.println("Caught exception: " + e );
    } */

  }
  
  QueueConnectionFactory getFactory() { return factory; }
  Destination getInitialDestination() { return initialDestination; }
  String getStyle() { return style; }
  
  Queue findQueue(String name) throws WSIFException
  {
  	return null;
  }
}

