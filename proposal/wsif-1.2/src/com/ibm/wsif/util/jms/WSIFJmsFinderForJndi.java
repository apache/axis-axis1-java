// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.util.jms;

import java.util.*;

import javax.jms.*;
import javax.naming.*;
import javax.naming.directory.*;

import com.ibm.wsif.*;

/**
 * Finds JMS objects by looking them up in JNDI.
 * @author Mark Whitlock
 */

class WSIFJmsFinderForJndi extends WSIFJmsFinder 
{
  private InitialDirContext jndiContext;
  private QueueConnectionFactory factory;
  private Destination initialDestination=null;
  private String style;

  /**
   * Package private constructor.
   * @param jmsVendorURL uniquely identifies the JMS implementation. Unused at present.
   * @param initialContextFactory used to get JNDI's InitialContext in a non-managed environment
   * @param jndiProviderURL of the JNDI repository
   * @param style is either queue or topic
   * @param jndiConnectionFactory is the JNDI name of the qcf
   * @param jndiDestinationName is the JNDI name of the initial queue.
   * @param jmsproviderDestinationName is the JMS implementation's name of the initial queue.
   */
  WSIFJmsFinderForJndi(String jmsVendorURL,
                       String initialContextFactory, 
	                   String jndiProviderURL, 
	                   String style,
	                   String jndiConnectionFactory, 
	                   String jndiDestinationName) throws WSIFException 
  {
    if (!allStyles.contains(style)) 
      throw new WSIFException("Style must either be queue or topic");
    this.style = style;

    if (initialContextFactory!=null && jndiProviderURL!=null)
    {
      Hashtable environment = new Hashtable();
      environment.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
      environment.put(Context.PROVIDER_URL, jndiProviderURL);
       
      try { jndiContext = new InitialDirContext(environment); }
      catch( NamingException ne ) {
        throw new WSIFException("WSIFJmsFinderForJndi caught '"+ne+"'. "+
          "InitialContextFactory was '"+initialContextFactory+"' "+
          "ProviderUrl was '"+jndiProviderURL+"'");
      }
    }
    else if (initialContextFactory==null && jndiProviderURL==null) try 
    { 
      jndiContext = new InitialDirContext(); 
    }
    catch ( NamingException ne ) {
      throw new WSIFException("WSIFJmsFinderForJndi caught '"+ne+"' "+
                              "using the default JNDI repository.");
    }
    else throw new WSIFException("Either both initialContextFactory and jndiProviderURL " +
                                 "must be specified or neither of them must be specified");
      
    if (STYLE_TOPIC.equals(style)) throw new WSIFException("Topics not implemented");
    else if (!STYLE_QUEUE.equals(style)) 
      throw new WSIFException("jms:address must either be a queue or a topic not a '"+
                              (style==null?"null":style)+"'");

    if (jndiConnectionFactory==null) 
      throw new WSIFException("jndiConnectionFactory must be specified");      
    try {
      factory = (QueueConnectionFactory)jndiContext.lookup(jndiConnectionFactory);
    } catch (ClassCastException cce) {
      throw new WSIFException("WSIFJmsFinderForJndi caught ClassCastException. "+
   	    "The ConnectionFactory "+jndiConnectionFactory+" in JNDI was not "+
   	    "defined to be a connection factory.");
    } catch (NamingException ne) {
      throw new WSIFException("WSIFJmsFinderForJndi caught NamingException. "+
        "The ConnectionFactory "+jndiConnectionFactory+" in JNDI was not "+
        "defined to be a connection factory.");
    }

    if (jndiDestinationName!=null) try {
      initialDestination = (Destination)jndiContext.lookup(jndiDestinationName);
    } catch (ClassCastException cce) {
      throw new WSIFException("WSIFJmsFinderForJndi caught ClassCastException. "+
        "The Destination "+jndiDestinationName+" in JNDI was not defined to be a destination.");
    } catch (NamingException cce) {
      throw new WSIFException("WSIFJmsFinderForJndi caught NamingException. "+
        "The Destination "+jndiDestinationName+" in JNDI was not defined to be a destination.");
    }
  }

  QueueConnectionFactory getFactory() { return factory; }
  Destination getInitialDestination() { return initialDestination; }
  String getStyle() { return style; }
  
  Queue findQueue(String name) throws WSIFException
  {
    try {
      return (Queue)jndiContext.lookup(name);
    } catch (ClassCastException cce) {
      throw new WSIFException("WSIFJmsFinderForJndi caught ClassCastException. "+
   	    "The Queue "+name+" in JNDI was not defined to be a queue.");
    } catch (NamingException cce) {
  	  throw new WSIFException("WSIFJmsFinderForJndi caught NamingException. "+
   	    "The Queue "+name+" in JNDI was not defined to be a queue.");
    }
  }
}

