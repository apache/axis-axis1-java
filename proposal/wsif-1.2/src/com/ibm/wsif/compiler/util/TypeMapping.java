// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.compiler.util;

import java.io.Serializable;
import javax.wsdl.QName;

/**
 * This class keeps all the info about a type mapping: the encoding style,
 * the XML element type, the Java type that's spsed to map to and the names
 * of the Java classes that implement the mapping between XML and Java.
 *
 * @author Sanjiva Weerawarana (sanjiva@watson.ibm.com)
 */
public class TypeMapping implements Serializable
{
  public QName elementType;
  public String javaType;

  public TypeMapping(QName elementType, String javaType)
  {
    this.elementType = elementType;
    this.javaType = javaType;
  }

  public String toString()
  {
    return "[TypeMapping elementType=" + elementType + "," +
           "javaType=" + javaType + "]";
  }
}
