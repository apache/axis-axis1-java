// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif;

import java.util.*;

/**
 * A WSIFMessage is a an interface representing a message.
 *
 * @author Paul Fremantle
 * @author Alekander Slominski
 * @author Matthew J. Duftler
 * @author Sanjiva Weerawarana
 * @author Nirmal Mukhi
 */
public interface WSIFMessage extends java.io.Serializable, Cloneable{
  /**
   * Get the name of this message.
   */
  public String getName ();

  /**
   * Set the name of this message.
   */
  public void setName (String name);

  /**
   * Return list of part names.
   * <p><b>NOTE:</b> part names are unordered.
   */
  public Iterator getPartNames();

  /** 
   * Create an iterator of the parts in this message.
   * Supercedes void getParts(Map).
   */
  public Iterator getParts();
  
  /**
   * Copy this message parts into targetParts.
   * <p><b>NOTE:</b> it is caller responsibility to call clear()
   *    on targetParts
   * @see setParts
   * @deprecated
   */
  public void getParts (Map targetParts);

  /**
   * This message parts will be replaced by sourceParts.
   * @parameter sourceParts must be Map that has as key Strings with
   *   part names and values must be instances of WSIFPart.
   */
  public void setParts (Map sourceParts);

  public String getRepresentationStyle();
  public void setRepresentationStyle(String rStyle);

  public Object getObjectPart(String name) throws WSIFException;
  public Object getObjectPart(String name, Class sourceClass) throws WSIFException;
  public void setObjectPart(String name, Object part) throws WSIFException;
	
  public char getCharPart(String name) throws WSIFException;
  public byte getBytePart(String name) throws WSIFException;
  public short getShortPart(String name) throws WSIFException;
  public int getIntPart(String name) throws WSIFException;
  public long getLongPart(String name) throws WSIFException;
  public float getFloatPart(String name) throws WSIFException;
  public double getDoublePart(String name) throws WSIFException;
  public boolean getBooleanPart(String name) throws WSIFException;

  public void setCharPart(String name, char charPart);
  public void setBytePart(String name, byte bytePart);
  public void setShortPart(String name, short shortPart);
  public void setIntPart(String name, int intPart);
  public void setLongPart(String name, long longPart);
  public void setFloatPart(String name, float floatPart);
  public void setDoublePart(String name, double doublePart);
  public void setBooleanPart(String name, boolean booleanPart);
  
  public Object clone() throws CloneNotSupportedException;
}

