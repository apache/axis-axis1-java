// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
//-------------------------74-columns-wide-------------------------------|
package com.ibm.wsif.providers;

import javax.wsdl.QName;

/**
 * This class encapsultes a simple association between XML and Java type
 * (QName and Class).
 *
 * @author Alekander Slominski
 */
public class WSIFDynamicTypeMapping
{
  protected QName xmlType;
  protected Class javaType;

  public WSIFDynamicTypeMapping(QName xmlType, Class javaType) {
    this.xmlType = xmlType;
    this.javaType = javaType;
  }

  public QName getXmlType() { return xmlType; }

  public Class getJavaType() { return javaType; }
  
  public String toString() { return "QName:"+xmlType+" Class:"+javaType; }
}

