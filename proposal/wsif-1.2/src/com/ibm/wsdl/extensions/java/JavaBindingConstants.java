// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsdl.extensions.java;

import javax.wsdl.*;
import com.ibm.wsdl.*;

/**
 * @author <a href="mailto:gpfau@de.ibm.com">Gerhard Pfau</a>
 */
public class JavaBindingConstants
{
  // Namespace URIs. 
  public static final String NS_URI_JAVA = "http://schemas.xmlsoap.org/wsdl/java/";
   
  // Element names.
  public static final String ELEM_ADDRESS = "address";

  // Qualified element names.
  public static final QName Q_ELEM_JAVA_BINDING         = new QName(NS_URI_JAVA, Constants.ELEM_BINDING);
  public static final QName Q_ELEM_JAVA_OPERATION       = new QName(NS_URI_JAVA, Constants.ELEM_OPERATION);
  public static final QName Q_ELEM_JAVA_ADDRESS         = new QName(NS_URI_JAVA, ELEM_ADDRESS);
}
