// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.util.jms;

import java.util.*;
import java.lang.reflect.*;
import javax.jms.*;
import com.ibm.wsif.*;

/**
 * WSIFJmsAttributes is a HashMap of jms attributes. The WSIFJmsAttributes can either be IN 
 * or OUT. If IN, the attributes can only set on a QueueSender. If OUT, the attributes 
 * can only be got from a message. Reflection is used to set and get the attributes in JMS. 
 * Using reflection avoids having a table of attributes that would have to updated for 
 * different JMS implementations and different versions of JMS. 
 * 
 * @author Mark Whitlock
 */

public class WSIFJmsAttributes extends HashMap 
{
  public static final String IN  = "in";
  public static final String OUT = "out";
  private static final ArrayList allDirections=new ArrayList(Arrays.asList(new Object[]{IN,OUT}));

  private String direction;
  
  /**
   * Constructor for WSIFJmsAttributes
   */
  public WSIFJmsAttributes(String direction) throws WSIFException
  {
  	super(); 

  	if (!allDirections.contains(direction)) 
  	  throw new WSIFException("Invalid direction "+direction);
  	this.direction = direction; 
  }
  	
  /**
   * Constructor for WSIFJmsAttributes from another HashMap.
   */
  public WSIFJmsAttributes(WSIFJmsAttributes attrs)
  {
  	super(attrs); 
  	direction = attrs.direction; 
  }

  /**
   * Set all the attributes that have been loaded into this HashMap on a QueueSender.
   * @return whether any attributes were set on this QueueSender
   */
  public boolean setAttributesOnProducer(MessageProducer producer) throws WSIFException
  {
  	if (direction!=IN) 
  	  throw new WSIFException("Only input attributes can be set on a MessageProducer");
  	  
  	if (super.isEmpty()) return false;
  	
    // Should unset attributes be reset in the sender here ??
  	
    Method[] methods = producer.getClass().getMethods();
    if (methods==null || methods.length==0) 
      throw new WSIFException("No methods on QueueSender");
        
    try 
    {    	
      for (Iterator it=super.keySet().iterator(); it.hasNext(); )
      {
      	String att = (String)(it.next());
      	String upperAtt = att.substring(0,1).toUpperCase() + att.substring(1);
      	Object value = super.get(att);
      	Class type = value.getClass();
      	boolean found = false;
        
        for (int i=0; i<methods.length && !found; i++)
        {
          if (methods[i].getName().equals("set"+upperAtt))
          {
          	// Check types in case setXxx is overloaded.
          	Class[] types = methods[i].getParameterTypes();
          	if (types==null || types.length!=1 ) continue;
          	
          	if (types[0].equals(type) ||
          	    (types[0].equals(int.class) && type.equals(Integer.class)))
            {
              found = true;
              methods[i].invoke(producer,new Object[]{value});
            }
          }
        }  // end for
        
        if (!found) 
          throw new WSIFException("No set method found to set JMS attribute "+att+
            " with type "+type+" to value "+value+" in class "+producer.getClass());
      }  // end for
    }
    catch (InvocationTargetException ite) 
      { throw WSIFJmsConstants.ToWsifException(ite.getTargetException()); }
    catch (IllegalAccessException    iae) 
      { throw WSIFJmsConstants.ToWsifException(iae); }
    
    return true;
  }

  /**
   * Get all the attributes from a Message and load them into this HashMap.
   */
  public void getAttributesFromMessage(Message message) throws WSIFException
  {
  	if (direction!=OUT) 
  	  throw new WSIFException("Only output attributes can be get from a message");
  	
    Method[] methods = message.getClass().getMethods();
    if (methods==null || methods.length==0) 
      throw new WSIFException("No methods on Message");
        
    super.clear();
    
    for (int i=0; i<methods.length; i++)
    {
      try 
      {    	
      	if (methods[i].getName().startsWith("get") &&
      	    methods[i].getParameterTypes().length==0)
      	{
      	  Object value = methods[i].invoke(message,null);
      	  
          String att;
          if (methods[i].getName().startsWith("getJMS"))
            att = methods[i].getName().substring(6);
          else 
            att = methods[i].getName().substring(3);
      	  String lowerAtt = att.substring(0,1).toLowerCase() + att.substring(1);

      	  super.put(lowerAtt,value);
      	}
      }
      catch (InvocationTargetException ignored) {}
      catch (IllegalAccessException    ignored) {}
    }
  }

}

