// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
//-------------------------74-columns-wide-------------------------------|
package com.ibm.wsif.spi;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;

import com.ibm.wsif.*;
import com.ibm.wsif.providers.*;

/**
 * A WSIFProvider is reponsible for translating  WSDL port model
 * into a dynamic WSIF port .
 *
 * <b>NOTE:</b> providers MUST be stateless
 *   it MUST be safe to call provider methods in multiple threads.
 *
 * @author Alekander Slominski
 */

public interface WSIFProvider {
  
  /**
   * For the given WSDL definition, service and port
   * try to provide dynamic port,
   * or return null if this provider can not do it.
   * It is required to pass definition and service in addition to port
   *   as in current WSDL4J it is not posssible to retrieve service to
   *   which port belongs and definition in which it was defined.
   */
  public WSIFPort createDynamicWSIFPort(
    Definition def, Service service, Port port,
    WSIFDynamicTypeMap typeMap)
    throws WSIFException;
    
  /**
   * Returns the WSDL namespace URIs of any bindings this provider supports
   */
  public String[] getBindingNamespaceURI();
   
}

