// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.providers.ejb;

import java.util.*;

import javax.wsdl.*;
import com.ibm.wsdl.extensions.ejb.EJBBinding;

import com.ibm.wsif.*;
import com.ibm.wsif.logging.*;
import com.ibm.wsif.util.*;
import com.ibm.wsif.spi.*;
import com.ibm.wsif.providers.*;

/**
 * EJB specific provider of dynamic WSDL invocations.
 * @see PortInstance_EJB
 *
 * @author <a href="mailto:gpfau@de.ibm.com">Gerhard Pfau</a>
 * Based on DynamicWSIFProvider_ApacheSOAP by Aleksander Slominski
 */
public class WSIFDynamicProvider_EJB implements WSIFProvider
{

  private static final String[] supportedNameSpaceURIs = 
     { "http://schemas.xmlsoap.org/wsdl/ejb/" };
   
  public WSIFDynamicProvider_EJB() {
     WSIFServiceImpl.addExtensionRegistry(new com.ibm.wsdl.extensions.format.FormatExtensionRegistry()) ;        
     WSIFServiceImpl.addExtensionRegistry(new com.ibm.wsdl.extensions.ejb.EJBExtensionRegistry());
  }
   
  /**
   * Check if WSDL port has EJB binding and if successful try
   * to create EJB port instance.
   */
  public WSIFPort createDynamicWSIFPort(
     Definition def,
     Service service, 
     Port port,
     WSIFDynamicTypeMap typeMap)
     throws WSIFException
  {
     TraceLogger.getGeneralTraceLogger().entry(
       new Object[] {def.getQName(),service.getQName(),port.getName(),typeMap});

     // check that Port binding has EJB binding extensibility element
     Binding binding = port.getBinding();
     List exs = binding.getExtensibilityElements();
     for (Iterator i = exs.iterator(); i.hasNext();)
     {
        Object o = i.next();
        if (o instanceof EJBBinding)
        {
           // if so try to create EJB dynamic port instance
 	       WSIFPort wp=new WSIFPort_EJB(def, port, typeMap);
           TraceLogger.getGeneralTraceLogger().exit(wp);
	       return wp;
        }
     }

     // otherwise return null (so other providers can be checked)
     TraceLogger.getGeneralTraceLogger().exit();
     return null;
  }

    /**
     * Returns the WSDL namespace URIs of any bindings this provider supports
     */
    public String[] getBindingNamespaceURI() {
       return supportedNameSpaceURIs;
    }

}
