// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.util.jms;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.soap.util.net.*;
import org.apache.soap.util.*;
import org.apache.soap.rpc.SOAPContext;
import org.apache.soap.Constants;
import org.apache.soap.transport.*;
import javax.mail.MessagingException;

import javax.jms.*;

import com.ibm.wsif.*;
import com.ibm.wsif.util.*;

/**
 * This class implements a JMS to HTTP bridge. That is it takes SOAP
 * messages off of a JMS queue and posts them using HTTP. The SOAP message
 * in the HTTP response is put on a JMS reply queue. This class contains a
 * main method which takes as parameters all the JMS and HTTP information
 * needed. This bridge can be cold or warm started. Cold starting wipes
 * messages off queues on startup, whereas warm starting does not.
 * 
 * @author Mark Whitlock
 */

public class JMS2HTTPBridge {
  private URL httpURL = null;
  private JMS2HTTPBridgeDestination destination = null;
  
  private static final ArrayList interestingAttributes = 
    new ArrayList(Arrays.asList(new String[] {"priority","deliveryMode"}));
  
  private WSIFJmsListener list = new WSIFJmsListener() {
      public void onException(JMSException arg1) {
        System.out.println("Caught an exception!");
	    arg1.printStackTrace();
      }

      public void onMessage(Message message) {
	    receiveMessage(message);
      }
    };

  public JMS2HTTPBridge(String initialContextFactory, 
                        String jndiUrl, 
                        String queueConnectionFactory, 
                        String readQueue, 
                        String httpUrlString, 
                        String startType) throws Exception {

    System.out.println("Starting the JMS2HTTPBridge with"    + "\n" +
	    "Initial Context Factory = "   + initialContextFactory  + "\n" +
	    "JNDI URL = "                  + jndiUrl                + "\n" +
	    "Queue Connection Factory  = " + queueConnectionFactory + "\n" +
	    "JNDI Read Queue = "           + readQueue              + "\n" +
	    "HTTP URL = "                  + httpUrlString          + "\n" +
	    "Start Type = "                + startType              );
    
    destination = new JMS2HTTPBridgeDestination(
      new WSIFJmsFinderForJndi(null,
                               initialContextFactory, 
                               jndiUrl,
                               WSIFJmsFinder.STYLE_QUEUE,
                               queueConnectionFactory,
                               readQueue),
      null,
      WSIFJmsConstants.WAIT_FOREVER,
      startType);
    
    httpURL = new URL(httpUrlString);
  }
  
  public static void main (String[] args) throws Exception {

    String usage = "Usage: java " + JMS2HTTPBridge.class.getName() + 
                   " [-cold|-warm] "                +
                   "-icf <initialContextFactory> "  + 
                   "-jndi <jndiUrl> "               +
                   "-s <sampleName> "               +
                   "-qcf <queueConnectionFactory> " +
                   "-q <readQueue> "                +
                   "-http <httpUrl>";
                   
    String startType              = JMS2HTTPBridgeDestination.WARMSTART;
    String initialContextFactory  = "com.sun.jndi.fscontext.RefFSContextFactory";
	String jndiUrl                = "file://C:/JNDI-Directory";
	String sampleName             = null;
    String queueConnectionFactory = "WSIFSampleQCF";
    String readQueue              = null;
    String httpUrlString          = "http://localhost:8080/soap/servlet/rpcrouter";

    for (int idx=0; idx<args.length; idx++) {
      if (!args[idx].startsWith("-")) throw new Exception("Bad parameter\n"+usage);

      if (args[idx].equals("-cold")) {
        startType=JMS2HTTPBridgeDestination.COLDSTART;
      } else if (args[idx].equals("-warm")) {
        startType=JMS2HTTPBridgeDestination.WARMSTART;
      } else if (args[idx].equals("-icf")) {
        idx++;
        initialContextFactory=args[idx];
      } else if (args[idx].equals("-jndi")) {
        idx++;
        jndiUrl=args[idx];
      } else if (args[idx].equals("-s")) {
        idx++;
        sampleName=args[idx];
      } else if (args[idx].equals("-qcf")) {
        idx++;
        queueConnectionFactory=args[idx];
      } else if (args[idx].equals("-q")) {
        idx++;
        readQueue=args[idx];
      } else if (args[idx].equals("-http")) {
        idx++;
        httpUrlString=args[idx];
      } else throw new Exception("Bad parameter\n"+usage);
    }

    if (readQueue==null && sampleName!=null) 
      readQueue="SoapJms"+sampleName+"Queue";
      
    if (startType             ==null ||
        initialContextFactory ==null ||
	    jndiUrl               ==null ||
        queueConnectionFactory==null ||
        readQueue             ==null ||
        httpUrlString         ==null )
      throw new Exception("Missing parameter\n"+usage);
     
    JMS2HTTPBridge j2h = new JMS2HTTPBridge(initialContextFactory,
                                            jndiUrl,
                                            queueConnectionFactory,
                                            readQueue,
                                            httpUrlString,
                                            startType);
                                            
    j2h.listen();
  }

  public void listen() throws WSIFException { destination.listen(list); }
  
  void receiveMessage (Message msg) 
  {
    String payload = null;

    try 
    {
      System.out.println("Caught a message!");

      if (msg instanceof TextMessage) 
      {
        String body = ((TextMessage)msg).getText();
        if (body != null) 
        {
          System.out.println("Message contained '"+body+"'");

          TransportMessage tmsg = 
            new TransportMessage(body,new SOAPContext(),new Hashtable());
          tmsg.save();

          TransportMessage response = HTTPUtils.post(httpURL,tmsg,30000,null,0);
          payload = IOUtils.getStringFromReader (response.getEnvelopeReader());
          System.out.println ("HTTP RESPONSE IS: '" + payload + "'");
        } 
        else 
        {
          System.err.println("error: message contained no body");
          payload = "error: message contained no body";
        }
      } 
      else 
      {
        System.err.println("error: message was not a TextMessage as expected");
        System.err.println(msg);
        payload = "error: message was not a TextMessage as expected";
      }

    } 
    catch (Exception e) 
    { 
      e.printStackTrace(); 
      payload=e.toString();
    }

    try
    {
      // Put the attributes from the received message onto the message we are 
      // about to send. Filter out everything but those attributes that we 
      // know about and are interested in, since there'll be lots of stuff in
      // attrs that aren't really attributes at all.
      WSIFJmsAttributes attrs = new WSIFJmsAttributes(WSIFJmsAttributes.OUT);
      attrs.getAttributesFromMessage(msg);
      HashMap kept = new HashMap();
      Iterator it=interestingAttributes.iterator(); 
      while (true)
      {
      	try 
      	{
          if (!it.hasNext()) break;
      	  String attr=(String)it.next();
      	  if (attrs.containsKey(attr)) kept.put(attr,attrs.get(attr));
      	} 
      	catch (Exception e) 
      	{
      	  System.err.println("JMS2HTTPBridge attributes caught "+e);
      	}
      }

      // Hack to force all reply messages to be non-persistent. 
      // Will need to change to support persistence.
      //if (kept.containsKey("deliveryMode")) kept.remove("deliveryMode");
      //kept.put("deliveryMode",new Integer(javax.jms.DeliveryMode.NON_PERSISTENT));
      
      destination.setAttributes(kept);
      destination.setReplyToQueue((Queue)msg.getJMSReplyTo());
      destination.send(payload,msg.getJMSMessageID());
    }
    catch (JMSException je)
    {
      je.printStackTrace();
    }
    catch (WSIFException we)
    {
      we.printStackTrace();
    }

    return;
  }
}
