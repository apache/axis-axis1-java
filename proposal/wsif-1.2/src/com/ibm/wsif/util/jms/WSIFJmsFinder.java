// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.util.jms;

import java.util.*;
import javax.jms.*;
import com.ibm.wsif.*;
import com.ibm.wsdl.extensions.jms.*;

/**
 * Finds JMS objects. Classes that extend WSIFJmsFinder find the factory and queues either
 * by going directly to an implementation of JMS or by looking them up in JNDI, or by some
 * other means. A WSIFJmsDestination can then be constructed from this finder.
 * 
 * @author Mark Whitlock
 */
public abstract class WSIFJmsFinder 
{
  public static final String STYLE_QUEUE = "queue";
  public static final String STYLE_TOPIC = "topic";

  protected static final ArrayList allStyles =
    new ArrayList(Arrays.asList(new Object[]{STYLE_QUEUE,STYLE_TOPIC}));
	
  private static final String MQ_URL_PREFIX = "mq://";
  
  abstract QueueConnectionFactory getFactory();
  abstract Destination getInitialDestination();
  abstract Queue findQueue(String name) throws WSIFException;
  abstract String getStyle();
  
  public static WSIFJmsFinder newFinder(JmsAddress ja)
    throws WSIFException
  {
  	boolean jndiSpecified = ja.getInitCxtFact()      != null ||
  	                        ja.getJndiProvURL()      != null ||
                            ja.getDestStyle()        != null ||
                            ja.getJndiConnFactName() != null ||
                            ja.getJndiDestName()     != null ||
                            ja.getJmsProvDestName()  != null;
    String implSpecURL = ja.getJmsImplSpecURL();

  	if (jndiSpecified && implSpecURL!=null) 
  	  throw new WSIFException("Cannot specify both JNDI attributes and " +
  	                          "jmsImplementationSpecificURL in the jms:address");

  	if (!jndiSpecified && implSpecURL==null) 
  	  throw new WSIFException("Must specify either JNDI attributes or " +
  	                          "jmsImplementationSpecificURL in the jms:address");

  	WSIFJmsFinder finder;
  	if (jndiSpecified)
  	  return new WSIFJmsFinderForJndi(ja.getJmsVendorURL(), 
  	                                  ja.getInitCxtFact(), 
  	                                  ja.getJndiProvURL(),
                                      ja.getDestStyle(),
                                      ja.getJndiConnFactName(), 
                                      ja.getJndiDestName());
  	else
  	{
      if (implSpecURL.startsWith(MQ_URL_PREFIX)) 
  	    return new WSIFJmsFinderForMq(ja.getJmsVendorURL(), implSpecURL);
  	  else 
  	    throw new WSIFException(
  	      "No jms implementation found for jmsImplementationSpecificURL '"+implSpecURL+"'");
  	}
  }
}

