// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
//-------------------------74-columns-wide-------------------------------|
package com.ibm.wsif.providers;

import java.util.*;
import javax.wsdl.QName;

/**
 * Container for type mappings that can be used by dynamic providers.
 *
 * @author Alekander Slominski
 */
public class WSIFDynamicTypeMap
{
  protected Vector typeMapList = new Vector();

  public WSIFDynamicTypeMap() {
  }

  /**
   * Add new mapping between XML and Java type.
   */
  public void mapType(QName xmlType, Class javaType) {
    typeMapList.add(new WSIFDynamicTypeMapping(xmlType, javaType));
  }

  /**
   * Return iterator with all mappings.
   */
  public Iterator iterator() {
    return typeMapList.iterator();
  }
  
  public String toString() {
  	Iterator it=iterator();
  	int i=0;
  	String buff=new String(super.toString()+": size:"+typeMapList.size());
  	while (it.hasNext()) {
  		WSIFDynamicTypeMapping wdtm=(WSIFDynamicTypeMapping)it.next();
  		buff += "\ntypeMapList["+i+"]:"+wdtm+" ";
  		i++;
  	}
  	return buff;
  }
}

