// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.providers.soap.apachesoap;

import javax.wsdl.QName;
import org.apache.soap.util.xml.*;

public interface PartSerializer extends Serializer, Deserializer 
{
  public void setPart(Object customBean);
  public Object getPart();
  public Object getPart(Class partClass);
  public QName getPartQName(); 
  public void setPartQName(QName qName);
}

