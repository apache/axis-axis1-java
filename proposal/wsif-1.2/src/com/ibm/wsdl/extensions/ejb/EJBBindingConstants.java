// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsdl.extensions.ejb;

import javax.wsdl.*;
import com.ibm.wsdl.*;

/**
 * @author <a href="mailto:gpfau@de.ibm.com">Gerhard Pfau</a>
 */
public class EJBBindingConstants
{
  // Namespace URIs.
  public static final String NS_URI_EJB = "http://schemas.xmlsoap.org/wsdl/ejb/";

  // Element names.
  public static final String ELEM_ADDRESS = "address";

  // Qualified element names.
  public static final QName Q_ELEM_EJB_BINDING         = new QName(NS_URI_EJB, Constants.ELEM_BINDING);
  public static final QName Q_ELEM_EJB_OPERATION       = new QName(NS_URI_EJB, Constants.ELEM_OPERATION);
  public static final QName Q_ELEM_EJB_ADDRESS         = new QName(NS_URI_EJB, ELEM_ADDRESS);
}
