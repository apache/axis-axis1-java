// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsdl.extensions.instance;

import javax.wsdl.*;
import com.ibm.wsdl.*;

/**
 * @author Aleksander Slominski
 */
public class InstanceConstants
{
  // Namespace URIs.
  public static final String NS_URI_INSTANCE =
    "http://schemas.xmlsoap.org/wsdl/instance/";
  
  // Element names.
  public static final String ELEM_ESTABLISHMENT = "establishment";
  
  // Qualified element names.
  public static final QName Q_ELEM_ESTABLISHMENT =
    new QName(NS_URI_INSTANCE, ELEM_ESTABLISHMENT);
  
  // Attribute names.
  //public static final String ATTR_ARCHIVE = "archive";
  public static final String ATTR_OPERATION = "operation";
}

