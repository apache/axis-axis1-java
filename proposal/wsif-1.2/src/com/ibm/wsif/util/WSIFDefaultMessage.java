// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.util;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;

import com.ibm.wsif.*;

/**
 * A DefaultWSIFMessage is a default implementation of
 * WSIFMessage holding a collection of WSIFParts corresponding
 * to the parts of a message as defined in WSDL.
 *
 * @author Paul Fremantle
 * @author Alekander Slominski
 * @author Matthew J. Duftler
 * @author Sanjiva Weerawarana
 * @author Nirmal Mukhi
 */
public class WSIFDefaultMessage implements WSIFMessage {
  protected Map parts;
  protected String name;
  protected String style;
  private boolean JROMAvailable = false;
  
  public WSIFDefaultMessage() {
	JROMAvailable = WSIFServiceImpl.getJROMAvailability();
  }

  /**
   * Set the name of this message.
   */
  public String getName () {
    return name;
  }

  /**
   * Get the name of this message.
   */
  public void setName (String name) {
    this.name = name;
  }

  /**
   * Return the representation style for all parts in this message
   */
  public String getRepresentationStyle() {
  	return style;
  }

  /**
   * Set the representation style for all parts in this message
   */  
  public void setRepresentationStyle(String rStyle) {
  	style = rStyle;
  }

  /**
   * Return an iterator of the parts in the message.
   * Supercedes void getParts(Map)
   */
  public Iterator getParts() {
    if (parts == null) {
      parts = new HashMap ();
    }
  	return parts.values().iterator();
  }
  
  /**
   * @deprecated
   */
  public void getParts (Map targetParts)
  {
    if(parts == null) return;
    for(Iterator names=parts.keySet().iterator(); names.hasNext(); )
    {
      String name = (String) names.next();
      Object part = parts.get(name);
      targetParts.put(name, part);
    }
  }
  
  public void setParts (Map sourceParts)
  {
    if (parts == null) {
      parts = new HashMap ();
    } else {
      parts.clear();
    }
    
    for(Iterator names=sourceParts.keySet().iterator(); names.hasNext(); )
    {
      String name = (String) names.next();
      Object part = sourceParts.get(name);
      parts.put(name, part);
    }
  }
  
  
  public void setObjectPart(String name, Object part) throws WSIFException {  	
	if (parts == null) {
	  parts = new HashMap();
    }
    if (JROMAvailable && part instanceof com.ibm.jrom.JROMValue) {
      if (parts.size() > 0 && !WSIFConstants.JROM_REPR_STYLE.equals(style)) {
    	throw new WSIFException("Mixed representation"
    	  + "styles are not permitted in a message");
      }
      style = WSIFConstants.JROM_REPR_STYLE;
    }
    else if (WSIFConstants.JROM_REPR_STYLE.equals(style))
    {
      throw new WSIFException("Mixed representation"
        + "styles are not permitted in a message");
    }    
    parts.put(name, part);
  }

  public Object getObjectPart(String name) throws WSIFException {
    if (parts == null) {
    	handleNoPartsException(name, "Object");     
    	return null;
    }
    if (parts.get(name) == null) {
    	if (!parts.keySet().contains(name)) {
    		handlePartNotFoundException(name);
    	}    	
    	return null;
    }    
    return parts.get(name);
  }

  public Object getObjectPart(String name, Class sourceClass) throws WSIFException {
    if (parts == null) {
      handleNoPartsException(name, "Object");     
      return null;
    }
    Object part = parts.get(name);
    if (part == null) {
      if (!parts.keySet().contains(name)) {
    	handlePartNotFoundException(name);
      }    	
      return null;
    }
    else
    {
  	  if (part.getClass().getName().equals(sourceClass.getName())) {
        return part;
  	  } else {
  	  	handleSourcedPartNotFoundException(name, sourceClass);
  	  	return null;
  	  }    	
    }    
  }
  
  public byte getBytePart(String name) throws WSIFException {
  	if (parts == null) handleNoPartsException(name, "byte");  	  	
    try
    {
      return ((Byte) parts.get(name)).byteValue();
    }
    catch(NullPointerException ne)
    {
      handlePartNotFoundException(name);
      return 0;	
    }
    catch(ClassCastException ce)
    {
      handlePartCastException(name, parts.get(name).getClass().getName(), "Byte");
      return 0;
    }
  }

  public void setBytePart(String name, byte part) {
    if (parts == null) {
      parts = new HashMap();
    }
    parts.put(name, new Byte(part));
  }

  public char getCharPart(String name) throws WSIFException {
  	if (parts == null) handleNoPartsException(name, "char");  	  	
    try
    {
      return ((Character) parts.get(name)).charValue();
    }
    catch(NullPointerException ne)
    {
      handlePartNotFoundException(name);
      return 0;	
    }
    catch(ClassCastException ce)
    {
      handlePartCastException(name, parts.get(name).getClass().getName(), "Character");
      return 0;
    }
  }

  public void setCharPart(String name, char part) {
    if (parts == null) {
	  parts = new HashMap();
    }
    parts.put(name, new Character(part));
  }

  public int getIntPart(String name) throws WSIFException {
  	if (parts == null) handleNoPartsException(name, "int");  	  	
    try
    {
      return ((Integer) parts.get(name)).intValue();
    }
    catch(NullPointerException ne)
    {
      handlePartNotFoundException(name);
      return 0;	
    }
    catch(ClassCastException ce)
    {
      handlePartCastException(name, parts.get(name).getClass().getName(), "Integer");
      return 0;
    }
  }

  public void setIntPart(String name, int part) {
    if (parts == null) {
      parts = new HashMap();
    }
    parts.put(name, new Integer(part));
  }

  public long getLongPart(String name) throws WSIFException {
  	if (parts == null) handleNoPartsException(name, "long");  	  	
    try
    {
      return ((Long) parts.get(name)).longValue();
    }
    catch(NullPointerException ne)
    {
      handlePartNotFoundException(name);
      return 0;	
    }
    catch(ClassCastException ce)
    {
      handlePartCastException(name, parts.get(name).getClass().getName(), "Long");
      return 0;
    }
  }

  public void setLongPart(String name, long part) {
    if (parts == null) {
      parts = new HashMap();
    }
    parts.put(name, new Long(part));
  }

  public short getShortPart(String name) throws WSIFException {
  	if (parts == null) handleNoPartsException(name, "short");  	  	
    try
    {
      return ((Short) parts.get(name)).shortValue();
    }
    catch(NullPointerException ne)
    {
      handlePartNotFoundException(name);
      return 0;	
    }
    catch(ClassCastException ce)
    {
      handlePartCastException(name, parts.get(name).getClass().getName(), "Short");
      return 0;
    }
  }

  public void setShortPart(String name, short part) {
    if (parts == null) {
      parts = new HashMap();
    }
    parts.put(name, new Short(part));
  }

  public float getFloatPart(String name) throws WSIFException {
  	if (parts == null) handleNoPartsException(name, "float");  	  	
    try
    {
      return ((Float) parts.get(name)).floatValue();
    }
    catch(NullPointerException ne)
    {
      handlePartNotFoundException(name);
      return 0;	
    }
    catch(ClassCastException ce)
    {
      handlePartCastException(name, parts.get(name).getClass().getName(), "Float");
      return 0;
    }
  }

  public void setFloatPart(String name, float part) {
    if (parts == null) {
      parts = new HashMap();
    }
    parts.put(name, new Float(part));
  }

  public double getDoublePart(String name) throws WSIFException {
   	if (parts == null) handleNoPartsException(name, "double");  	  	
    try
    {
      return ((Double) parts.get(name)).doubleValue();
    }
    catch(NullPointerException ne)
    {
      handlePartNotFoundException(name);
      return 0;	
    }
    catch(ClassCastException ce)
    {
      handlePartCastException(name, parts.get(name).getClass().getName(), "Double");
      return 0;
    }
  }

  public void setDoublePart(String name, double part) {
    if (parts == null) {
      parts = new HashMap();
    }
    parts.put(name, new Double(part));
  }

  public boolean getBooleanPart(String name) throws WSIFException {
  	if (parts == null) handleNoPartsException(name, "boolean");  	  	
    try
    {
      return ((Boolean) parts.get(name)).booleanValue();
    }
    catch(NullPointerException ne)
    {
      handlePartNotFoundException(name);
      return false;	
    }
    catch(ClassCastException ce)
    {
      handlePartCastException(name, parts.get(name).getClass().getName(), "Boolean");
      return false;
    }
  }

  public void setBooleanPart(String name, boolean part) {
    if (parts == null) {
      parts = new HashMap();
    }
    parts.put(name, new Boolean(part));
  }

  public Iterator getPartNames() {
    if (parts == null) {
      parts = new HashMap();
    }
    return parts.keySet().iterator();
  }
  
  public String toString()
  {
     String buff=new String(super.toString());
  	 buff += " name:"+name;
  	 if (parts==null)
  	 {
  	   buff += " parts:null";
  	 }
  	 else
  	 {
  	   buff += " size:"+parts.size();
  	   Iterator it=parts.values().iterator();
  	   int i=0;
  	   while (it.hasNext())
  	   {
  	 	  Object part=it.next();
  	 	  buff+=" Part["+i+"]:"+part;
  	 	  i++;
  	   }  	 	
  	 }
  	 return buff;
  }
  
  public Object clone() throws CloneNotSupportedException
  {
  	WSIFDefaultMessage dm = new WSIFDefaultMessage();
  	dm.setName(this.name);
  	Iterator it = getPartNames();
  	while(it.hasNext())
  	{
  	  String pn = (String) it.next();
  	  Object po = parts.get(pn); 	  	 	  	
  	  if (po instanceof Cloneable)
  	  {
  	  	Class cls = po.getClass();
  	  	try 
  	  	{
  	  	  Method clone = cls.getMethod("clone",null);
  	  	  Object poc = clone.invoke(po,null);
  	  	  dm.setObjectPart(pn,poc);
  	  	} 	  	  
  	  	catch(InvocationTargetException e)
  	  	{
  	  	  throw new CloneNotSupportedException(
  	  	    "Exception thrown whilst cloning part " +
  	  	    pn + ". Message is "+e.getTargetException().getMessage());  	  	
  	  	}
  	  	catch(Exception e)
  	  	{
  	  	  throw new CloneNotSupportedException(
  	  	    "Exception thrown whilst cloning part " +
  	  	    pn + ". Message is "+e.getMessage());
  	  	}
  	  }
  	  else if (po instanceof Serializable)
  	  {
  	    try
  	  	{
  	  	  ByteArrayOutputStream b = new ByteArrayOutputStream();
  	  	  ObjectOutputStream out = new ObjectOutputStream(b);
  	  	  out.writeObject(po);
  	  	  out.flush();
  	  	  out.close();
  	  	  byte[] data = b.toByteArray();
  	  	  ObjectInputStream in = new ObjectInputStream(
  	  	    new ByteArrayInputStream(data));
  	  	  Object poc = in.readObject();
  	  	  in.close();
  	  	  dm.setObjectPart(pn,poc);
  	  	}
  	  	catch(Exception e)
  	  	{
  	  	  throw new CloneNotSupportedException(
  	  	    "Exception thrown whilst cloning part " +
  	  	    pn + " by serialization. Message is "+e.getMessage());
  	  	}
  	  }
  	  else
  	  {
  	    throw new CloneNotSupportedException("Part "+pn+" cannot be cloned");
  	  }
  	}
  	return dm;
  }
  
  private void handleNoPartsException(String part, String type) throws WSIFException
  {
    throw new WSIFException("Cannot get "+type+" part '"+part
  	  	+"'. No parts are set on the message");
  }

  private void handlePartNotFoundException(String part) throws WSIFException
  {
    throw new WSIFException("Cannot get part '"+part+"'. Part was not found in message");
  }

  private void handleSourcedPartNotFoundException(String part, Class sclass) throws WSIFException
  {
    throw new WSIFException("Cannot get part. Part '"+part+"' with source '"
    						+ sclass + "' was not found in message");
  }

  private void handlePartCastException(String part, String act, String exp) 
  	throws WSIFException
  {
    throw new WSIFException("Cannot get part '"+part+"'. Cannot convert "
      + "from "+act+" to "+exp);
  }     
}

