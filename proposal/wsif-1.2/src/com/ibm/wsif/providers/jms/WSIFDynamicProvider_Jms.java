// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.providers.jms;

import java.util.*;

import javax.wsdl.*;
import com.ibm.wsdl.extensions.jms.JmsBinding;

import com.ibm.wsif.*;
import com.ibm.wsif.logging.*;
import com.ibm.wsif.util.*;
import com.ibm.wsif.spi.*;
import com.ibm.wsif.providers.*;



/**
 * WSIF Jms provider
 * 
 * @author <a href="mailto:ake@de.ibm.com">Hermann Akermann</a>
 */
public class WSIFDynamicProvider_Jms implements WSIFProvider {

	 private static final String[] supportedNameSpaceURIs = 
       { "http://schemas.xmlsoap.org/wsdl/jms/" };
       
     public WSIFDynamicProvider_Jms() {
        WSIFServiceImpl.addExtensionRegistry(new com.ibm.wsdl.extensions.jms.JmsExtensionRegistry()) ;
        WSIFServiceImpl.addExtensionRegistry(new com.ibm.wsdl.extensions.format.FormatExtensionRegistry()) ;        
     }
   
	/**
	 * @see WSIFDynamicProvider#createDynamicWSIFPort(Definition, Service, Port, WSIFDynamicTypeMap)
	 */
	public WSIFPort createDynamicWSIFPort(
		Definition def,
		Service service,
		Port port,
		WSIFDynamicTypeMap typeMap)
		throws WSIFException {

		TraceLogger.getGeneralTraceLogger().entry(
			new Object[] { def.getQName(), service.getQName(), port.getName(), typeMap });

		// check that Port binding has Jms binding extensibility element
		Binding binding = port.getBinding();
		List exs = binding.getExtensibilityElements();
		for (Iterator i = exs.iterator(); i.hasNext();) {
			Object o = i.next();

			if (o instanceof JmsBinding) {
				// if so try to create Jms dynamic port instance
				WSIFPort wp = new WSIFPort_Jms(def, port, typeMap);
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