// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.

package com.ibm.wsif.providers.soap.apacheaxis;

import javax.wsdl.extensions.soap.SOAPBinding;
import com.ibm.wsif.*;
import com.ibm.wsif.spi.WSIFProvider;
import com.ibm.wsif.providers.WSIFDynamicTypeMap;
import java.util.Iterator;
import java.util.List;
import javax.wsdl.*;

public class WSIFDynamicProvider_ApacheAxis
    implements WSIFProvider
{

    private static final String[] supportedNameSpaceURIs = 
       { "http://schemas.xmlsoap.org/wsdl/soap/" };
	
    public WSIFDynamicProvider_ApacheAxis()
    {
       WSIFServiceImpl.addExtensionRegistry(new com.ibm.wsdl.extensions.java.JavaExtensionRegistry()) ;    
       WSIFServiceImpl.addExtensionRegistry(new com.ibm.wsdl.extensions.format.FormatExtensionRegistry()) ;        
       WSIFServiceImpl.addExtensionRegistry(new com.ibm.wsdl.extensions.jms.JmsExtensionRegistry()) ;
    }

    public WSIFPort createDynamicWSIFPort(Definition definition, Service service, 
                                          Port port, WSIFDynamicTypeMap wsifdynamictypemap)
        throws WSIFException
    {
        Binding binding = port.getBinding();
        List list = binding.getExtensibilityElements();
        for(Iterator iterator = list.iterator(); iterator.hasNext();)
        {
            Object obj = iterator.next();
            if(obj instanceof SOAPBinding)
                return new WSIFPort_ApacheAxis(definition, service, port, wsifdynamictypemap);
        }

        return null;
    }
    
    /**
     * Returns the WSDL namespace URIs of any bindings this provider supports.
     * To make this provider dependent on the Aixs jar being available in the
     * classpath this attempts to load an Axis class and will return an string
     * array with no elements if an Axis class cannot be loaded.
     */
    public String[] getBindingNamespaceURI() {
       try {
	      ClassLoader cl = this.getClass().getClassLoader();
	      cl.loadClass( "org.apache.axis.AxisEngine" );
          return supportedNameSpaceURIs;
       } catch (ClassNotFoundException e) {
          return new String[0];
       }
    }


}
