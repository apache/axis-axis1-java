// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
//-------------------------74-columns-wide-------------------------------|
package com.ibm.wsif.providers.soap.apachesoap;

import java.util.*;

import javax.wsdl.*;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPAddress;

import com.ibm.wsif.*;
import com.ibm.wsif.logging.*;
import com.ibm.wsif.util.*;
import com.ibm.wsif.spi.*;
import com.ibm.wsif.providers.*;

/**
 * ApacheSOAP specific provider of dynamic WSDL invocations.
 *
 * <P>Limitations of this Apache SOAP dynamic port provider
 *     (relative to WSDL 1.1 SOAP binding):<UL>
 * <LI>only rpc style is supported (not document)
 * <LI>only HTTP transport is supported
 * <LI>only soap:body use 'encoded' is supported (not literal)
 * <LI>soap:header is not allowed
 * <LI>soap:fault is ignored
 * <LI>only first encodingStyle is used from input soap:body
 *  (when the space separated list of encoding styles is provided)
 * <LI>output soap:body namespaceURI and encodingStyles are ignored
 * <LI>first part from output soap:body is used as return value
 * <LI>fault processing is not yet implemented - SOAP faults
 *    as provided by Apache SOAP exceptions are always wrapped into
 *    WSIFException.
 * </UL>
 *
 * @author Aleksander Slominski
 */
public class WSIFDynamicProvider_ApacheSOAP
  implements WSIFProvider
{
  protected String partSerializerName = null;
  
  //static 
  //{
  //  WSIFServiceImpl.addExtensionRegistry(
  //    new com.ibm.wsdl.extensions.jms.JmsExtensionRegistry());
  //}

  private static final String[] supportedNameSpaceURIs = 
     { "http://schemas.xmlsoap.org/wsdl/soap" };
   
  public WSIFDynamicProvider_ApacheSOAP() {
       WSIFServiceImpl.addExtensionRegistry(new com.ibm.wsdl.extensions.java.JavaExtensionRegistry()) ;    
       WSIFServiceImpl.addExtensionRegistry(new com.ibm.wsdl.extensions.format.FormatExtensionRegistry()) ;        
       WSIFServiceImpl.addExtensionRegistry(new com.ibm.wsdl.extensions.jms.JmsExtensionRegistry()) ;
  }
  /**
   * Check if WSDL port has SOAP binding and if successful try
   * to create ApacheSOAP port instance.
   */
  public WSIFPort createDynamicWSIFPort(
    Definition def, Service service, Port port,
    WSIFDynamicTypeMap typeMap)
    throws WSIFException
  {
    TraceLogger.getGeneralTraceLogger().entry(
      new Object[] {def.getQName(),service.getQName(),port.getName(),typeMap});
    
    // check that Port binding has SOAP binding extensibility element
    Binding binding = port.getBinding();
    List exs = binding.getExtensibilityElements();
    for(Iterator i = exs.iterator(); i.hasNext(); ) {
      Object o = i.next();
      if(o instanceof SOAPBinding) {
        // if so try to create ApacheSOAP dynamic port instance
        WSIFPort wp=new WSIFPort_ApacheSOAP(
          def, service, port, typeMap, partSerializerName);
        TraceLogger.getGeneralTraceLogger().exit(wp);
        return wp;
      }
    }
    
    // otherwise return null (so other providers can be checked)
    TraceLogger.getGeneralTraceLogger().exit();
    return null;
  }

  /**
   * Gets the partSerializerName.
   * @return Returns a String
   */
  public String getPartSerializerName() 
  {
  	return partSerializerName;
  }

  /**
   * Sets the partSerializerName.
   * @param partSerializerName The partSerializerName to set
   */
  public void setPartSerializerName(String partSerializerName) 
  {
	this.partSerializerName = partSerializerName;
  }

  /**
   * Returns the WSDL namespace URIs of any bindings this provider supports
   */
  public String[] getBindingNamespaceURI() {
     return supportedNameSpaceURIs;
  }

}
