// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
//-------------------------74-columns-wide-------------------------------|
package com.ibm.wsif;

import java.util.Iterator;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.Map;
import java.util.Collection;
import java.util.Hashtable;

import javax.wsdl.*;

import com.ibm.wsif.*;
import com.ibm.wsif.logging.*;
import com.ibm.wsif.providers.*;
import com.ibm.wsif.stub.*;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import javax.wsdl.*;
import javax.wsdl.extensions.*;
import javax.wsdl.factory.*;
import com.ibm.wsdl.*;
import com.ibm.wsdl.util.*;
import com.ibm.wsdl.util.xml.*;

import com.ibm.wsdl.extensions.*;
import com.ibm.wsdl.factory.*;

import org.apache.soap.Constants;
import com.ibm.wsif.spi.WSIFProvider;
import com.ibm.wsif.compiler.schema.*;
import com.ibm.wsif.compiler.schema.tools.*;
import com.ibm.wsif.compiler.util.*;
import com.ibm.wsif.util.WSIFProperties;
import com.ibm.wsif.util.WSIFDefaultMessageFactory;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * An entry point to dynamic WSDL invocations.
 *
 * @author Alekander Slominski
 * @author Sanjiva Weerawarana
 */
public class WSIFServiceImpl implements WSIFService {
	private static boolean autoLoadProviders = true;
    private static HashMap namespaceProviders;
    private static WSIFProvider[] availableProviders = null;
    private static PrivateCompositeExtensionRegistry providersExtRegs =
        new PrivateCompositeExtensionRegistry();
    private static WSIFMessageFactory msgFactory = null;
    private Definition def;
    private Service service;
    private PortType portType;
    private Port[] myPortsArr;
    private Map myPortsMap;
    private WSIFDynamicTypeMap typeMap = new WSIFDynamicTypeMap();
    private boolean typeMapInitialised = false;
    private String preferredPort = null;

    private static final Class WSIF_DYNAMIC_PROVIDER_CLASS =
        com.ibm.wsif.spi.WSIFProvider.class;
        
    private static boolean jromAvailable = false;

    /**
     * Create a WSIF service instance from WSDL document URL.
     * <br> If serviceName or serviceNS is null,
     *   then WSDL document must have exactly one service in it.
     * <br> If portTypeName or portTypeNS is null,
     *   then WSDL document must have exactly one portType in it
     *   and all ports of the selected service must
     *    implement the same portType.
     */
    WSIFServiceImpl(
	        String wsdlLoc,
	        String serviceNS,
	        String serviceName,
	        String portTypeNS,
	        String portTypeName)
	        throws WSIFException {
        TraceLogger.getGeneralTraceLogger().entry(
            new Object[] { wsdlLoc, serviceNS, serviceName, portTypeNS, portTypeName });
            
        // load WSIFProviders
        if (availableProviders == null) availableProviders = getAllDynamicWSIFProviders();   

        // load WSDL defintion
        Definition def = null;
        try {
            def = WSIFUtils.readWSDL(null, wsdlLoc);
            checkWSDL(def);
        } catch (WSDLException ex) {
            throw new WSIFException("could not load " + wsdlLoc, ex);
        }

        // select WSDL service if given name
        Service service = WSIFUtils.selectService(def, serviceNS, serviceName);

        // select WSDL portType if given name
        PortType portType = WSIFUtils.selectPortType(def, portTypeNS, portTypeName);

        init(def, service, portType);
        TraceLogger.getGeneralTraceLogger().exit();
    }

    /**
      * Create a WSIF service instance from WSDL document URL
      * using a ClassLoader to find local resources.
      * <br> If serviceName or serviceNS is null,
      *   then WSDL document must have exactly one service in it.
      * <br> If portTypeName or portTypeNS is null,
      *   then WSDL document must have exactly one portType in it
      *   and all ports of the selected service must
      *    implement the same portType.
      */
    WSIFServiceImpl(
	        String wsdlLoc,
	        ClassLoader cl,
	        String serviceNS,
	        String serviceName,
	        String portTypeNS,
	        String portTypeName)
	        throws WSIFException {
        TraceLogger.getGeneralTraceLogger().entry(
            new Object[] { wsdlLoc, cl, serviceNS, serviceName, portTypeNS, portTypeName });

        // load WSIFProviders
        if (availableProviders == null) availableProviders = getAllDynamicWSIFProviders();   

        // load WSDL defintion
        Definition def = null;
        try {
            def = WSIFUtils.readWSDL(null, wsdlLoc, cl);
            checkWSDL(def);
        } catch (WSDLException ex) {
            throw new WSIFException("could not load " + wsdlLoc, ex);
        }

        // select WSDL service if given name
        Service service = WSIFUtils.selectService(def, serviceNS, serviceName);

        // select WSDL portType if given name
        PortType portType = WSIFUtils.selectPortType(def, portTypeNS, portTypeName);

        init(def, service, portType);
        TraceLogger.getGeneralTraceLogger().exit();
    }

    WSIFServiceImpl(Definition def) throws WSIFException {
        this(def, null);
    }

    WSIFServiceImpl(Definition def, Service service) throws WSIFException {
        this(def, service, null);
    }

    WSIFServiceImpl(Definition def, Service service, PortType portType)
        	throws WSIFException {
        // load WSIFProviders
        if (availableProviders == null) availableProviders = getAllDynamicWSIFProviders();   

        init(def, service, portType);
    }
    
    WSIFServiceImpl(Definition def, String serviceNS, String serviceName)
	        throws WSIFException {
        // load WSIFProviders
        if (availableProviders == null) availableProviders = getAllDynamicWSIFProviders();   
	        	
        // select WSDL service if given by name or only one
        Service service = WSIFUtils.selectService(def, serviceNS, serviceName);	
        init(def, service, null);
    }    

    WSIFServiceImpl(
	        Definition def,
	        String serviceNS,
	        String serviceName,
	        String portTypeNS,
	        String portTypeName)
	        throws WSIFException {
        TraceLogger.getGeneralTraceLogger().entry(
            new Object[] {
                def.getQName(),
                serviceNS,
                serviceName,
                portTypeNS,
                portTypeName });

        // load WSIFProviders
        if (availableProviders == null) availableProviders = getAllDynamicWSIFProviders();   

        checkWSDLForWSIF(def);

        // select WSDL service if given by name or only one
        Service service = WSIFUtils.selectService(def, serviceNS, serviceName);

        // select WSDL portType if given by name or only one portType
        PortType portType = WSIFUtils.selectPortType(def, portTypeNS, portTypeName);

        init(def, service, portType);
        TraceLogger.getGeneralTraceLogger().exit();
    }

    /**
     * Set the preferred port
     * @param portName The name of the port to use
     */
    public void setPreferredPort(String portName) throws WSIFException {
    	if (portName == null) {
    		throw new WSIFException("Preferred port name is null");
    	}
        PortType pt = getPortTypeFromPortName(portName);
        if (pt.getQName().equals(this.portType.getQName())) {
        	this.preferredPort = portName;
        } else  {
        	throw new WSIFException("Preferred port " + portName +
 	       		"is not available for the port type "+this.portType.getQName());
        }
    }

    /**
     * Create a PortType object from the name of a port
     * @param portName The name of the port
     * @return A PortType corresponding to the port type used by the
     * specified port
     */
    private PortType getPortTypeFromPortName(String portName)
        	throws WSIFException {
        if (portName == null) {
            throw new WSIFException("Unable to find port type from a null port name");
        }
        Port port = (Port) myPortsMap.get(portName);
        if (port == null) {
            throw new WSIFException(
                "Preferred port '" + portName + "' cannot be found in the wsdl");
        }
        Binding binding = port.getBinding();
        if (port == null) {
            throw new WSIFException("No binding found for port '" + portName + "'");
        }
        PortType pt = binding.getPortType();
        if (pt == null) {
            throw new WSIFException(
                "No port type found for binding '" + binding.getQName() + "'");
        }
        checkPortTypeInformation(def, pt);
        return pt;
    }

    /**
     * Get the names of the available ports
     * @return Iterator for list of available port names.
     */
    public Iterator getAvailablePortNames() throws WSIFException {
        try {
            return this.myPortsMap.keySet().iterator();
        } catch (NullPointerException ne) {
            return null;
        }
    }

    /**
     * Create dynamic port instance from WSDL model defnition and port.
     */
    private WSIFPort createDynamicWSIFPort(
	        Definition def,
	        Service service,
	        Port port)
	        throws WSIFException {
        checkWSDLForWSIF(def);
        List bindingExList = port.getBinding().getExtensibilityElements();
        ExtensibilityElement bindingFirstEx =
            (ExtensibilityElement) bindingExList.get(0);
        String bindingNS = bindingFirstEx.getElementType().getNamespaceURI();
        WSIFProvider provider = getDynamicWSIFProvider(bindingNS);
        if (provider != null) {
            return provider.createDynamicWSIFPort(def, service, port, typeMap);
        } else {
            throw new WSIFException(
                "could not find suitable provider for binding namespace '" + bindingNS + "'");
        }
    }

    public WSIFPort getPort() throws WSIFException {
		return getPort(this.preferredPort);
    }

    /**
     * Return dynamic port instance selected by port name.
     */
    public WSIFPort getPort(String portName) throws WSIFException {
        TraceLogger.getGeneralTraceLogger().entry(portName);

        Port port;
        if (portName == null) {
            // find first port if there is exactly one...
            if (myPortsArr.length == 0) {
                throw new WSIFException("WSDL service must contain at least one port");
            } else if (myPortsArr.length > 1) {
                throw new WSIFException("port name required when more than one port in WSDL service");
            }
            port = myPortsArr[0];
        } else {
            port = (Port) myPortsMap.get(portName);
            if (port == null) {
                throw new WSIFException("no port named " + portName + " available.");
            }
        }
		portName = port.getName();
		
        WSIFPort portInstance = createDynamicWSIFPort(def, service, port);
        if (portInstance == null) {
            throw new WSIFException("Provider was unable to create WSIFPort for port " + portName);
        }

        TraceLogger.getGeneralTraceLogger().exit(portInstance);
        return portInstance;
    }

    /**
     * Add association between XML and Java type.
     */
    public void mapType(QName xmlType, Class javaType) throws WSIFException {
        typeMap.mapType(xmlType, javaType);
    }

    /**
     * Gets the factory for creating WSIFMessages
     */
    public static WSIFMessageFactory getMessageFactory() {
    	if ( msgFactory == null ) {
    		msgFactory = createMsgFactory();
    	}
    	return msgFactory;
    }

    private static WSIFMessageFactory createMsgFactory() {
    	return new WSIFDefaultMessageFactory();
    }
    
    /**
     * Gets a WSIFProvider for a particular namespace URI.
     * @param namespaceURI  the URI of the namespace the WSIFProvider must support 
     * @return    a WSIFProvider supporting the requested URI,
     *            or null if no providers are available. 
     */
    public static WSIFProvider getDynamicWSIFProvider(String namespaceURI) {
        WSIFProvider provider;

        // the namespaceProviders Hashtable URIs end with a '/'
        if (!namespaceURI.endsWith("/")) {
            namespaceURI += "/";
        }

        if (namespaceProviders == null) {
            namespaceProviders = new HashMap();
        } else {
            provider = (WSIFProvider) namespaceProviders.get(namespaceURI);
            if (provider != null) {
                return provider;
            }
        }
        WSIFProvider[] providers = getSupportingProviders(namespaceURI);
        if (providers.length == 0) {
            return null;
        }
        if (providers.length == 1) {
            provider = providers[0];
        } else {
            provider = chooseProvider(providers, namespaceURI);
        }
        namespaceProviders.put(namespaceURI, provider);
        return provider;
    }

    /**
     * Gets all the available WSIFProvider that support a particular 
     * namespace URI.
     * @param namesapceURI   the namespace the WSIFProvider must support. 
     * @return    an array of WSIFProvider. The array will have a length
     * of zero if no WSIFProvider are available for the requested namespace.
     */
    private static WSIFProvider[] getSupportingProviders(String namespaceURI) {
        if (availableProviders == null) {
            availableProviders = getAllDynamicWSIFProviders();
        }
        ArrayList supportingProviders = new ArrayList();
        String[] uris;
        for (int i = 0; i < availableProviders.length; i++) {
            uris = availableProviders[i].getBindingNamespaceURI();
            for (int j = 0; j < uris.length; j++) {
                if (StringUtils.areURIsEquivalent(uris[j], namespaceURI)) {
                    supportingProviders.add(availableProviders[i]);
                }
            }
        }
        WSIFProvider[] providerArray = new WSIFProvider[supportingProviders.size()];
        providerArray = (WSIFProvider[]) supportingProviders.toArray(providerArray);
        if (providerArray.length > 1) {
            issueMultipleProvidersMsg(namespaceURI, providerArray);
        }
        return providerArray;
    }

    /**
     * Gets all the available WSIFProviders. 
     * WSIFProviders are located using the J2SE 1.3 JAR file extensions 
     * to support service providers.
     * @return    an array of WSIFProvider.
     */
    private static WSIFProvider[] getAllDynamicWSIFProviders() {
    	
    	if ( !autoLoadProviders ) {
    		return new WSIFProvider[0];
    	}
    	
        ArrayList al =
            (ArrayList) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                Iterator ps = sun.misc.Service.providers(WSIF_DYNAMIC_PROVIDER_CLASS);
                ArrayList al2 = new ArrayList();
                while (ps.hasNext()) {
                    al2.add(ps.next());
                }
                return al2;
            }
        });
        return (WSIFProvider[]) al.toArray(new WSIFProvider[al.size()]);
    }

    /**
     * This sets if the WSIFProviders will be automatically located and loaded
     * using the J2SE 1.3 JAR file extensions to support service providers.
     * @param b   true means all the WSIFProviders will be loaded automatically,
     *            false means all WSIFProviders must be manually set with the
     *            WSIFServiceImpl.setDynamicWSIFProvider method
     */
    public static void setAutoLoadProviders(boolean b) {
    	autoLoadProviders = b;
    } 
     
    /**
     * Chooses a particular WSIFProvider. If the passed array of providers
     * contains more than one element then a choice is made based on a WSIF 
     * properties file default setting.     
     * @param providers   an array of WSIFProvider 
     * @return    a WSIFProvider. Returns null if the input array is null or
     *            has a length of zero, the first element if the array contains
     *            only one element, or an element based on the property file setting.
     */
    private static WSIFProvider chooseProvider(
        WSIFProvider[] providers,
        String uri) {
        if (providers == null || providers.length < 1) {
            return null;
        } else if (providers.length == 1) {
            return providers[0];
        }

        int i = providers.length - 1;
        while (i > 0 && !isDefaultProvider(providers[i], uri)) {
            i--;
        }

        issueChosenProviderMsg(uri, providers[i]);

        return providers[i];
    }

    /**
     * Tests if a class name is defined in the WSIF properties file as being the
     * default WSIF provider for the namespace URI.
     * @param className  the class name to test
     * @param uri the namespace URI
     * @return    true if className is defined as the default WSIFprovider, 
     *            otherwise false.
     */
    private static boolean isDefaultProvider(WSIFProvider provider, String uri) {
        String className = provider.getClass().getName();
        String defaultURI;
        try {
            String key = WSIFConstants.WSIF_PROP_PROVIDER_PFX1 + className;
            int n = Integer.parseInt( WSIFProperties.getProperty( key ) );
            for (int i = 1; i <= n; i++) {
                key = WSIFConstants.WSIF_PROP_PROVIDER_PFX2 + i + "." + className;
                defaultURI = WSIFProperties.getProperty( key );
                if ( StringUtils.areURIsEquivalent(defaultURI, uri) ) {
                    return true;
                }
            }
        } catch (NumberFormatException e) { // ignore any error
        } // ignore any error
        return false;
    }

    /**
     * This method is deprecated as the Pluggable Provider support means the correct
     * provider should be located automatically.
     * It may still be required to use this method to override which provider is chosen,
     * for example to use the ApacheSoap provider instead of the default Axis SOAP provider.
     * Calling this method with a null provider resets the provider supporting the 
     * namespaceURI, so that the next request for for that namespaceURI will return
     * to using the default provider. 
     * @param providerNamespaceURI    the namespaceURI
     * @param provider   the WSIFProvider to be used for the namespaceURI
     * @deprecated Plugable providers should make calling this unnecessary
     */
    public static void setDynamicWSIFProvider(
	        String providerNamespaceURI,
	        WSIFProvider provider) {
        if (namespaceProviders == null) {
            namespaceProviders = new HashMap();
        }

        // the namespaceProviders HashMap URIs end with a '/'
        if (!providerNamespaceURI.endsWith("/")) {
            providerNamespaceURI += "/";
        }

        if (provider == null) {
            namespaceProviders.remove(providerNamespaceURI);
        } else {
            namespaceProviders.put(providerNamespaceURI, provider);
            issueChosenProviderMsg(providerNamespaceURI, provider);
        }

    }

    /**
     * Issues a MessageLoger warning saying multiple providers 
     * exist with support for the same namespaceURI.
     * @param uri   the namespaceURI with multiple WSIFProviders 
     * @param providers  an array of the providers supporting the namespaceURI 
     */
    private static void issueMultipleProvidersMsg(
        String uri,
        WSIFProvider[] providers) {
        String providerNames = providers[0].getClass().getName();
        for (int i = 1; i < providers.length; i++) {
            providerNames += ", " + providers[i].getClass().getName();
        }
        MessageLogger messageLog =
            MessageLogger.newMessageLogger("WSIF", "com.ibm.wsif.catalog.Messages");
        messageLog.message(
            WSIFConstants.TYPE_WARNING,
            "WSIF.0006W",
            new Object[] { uri, providerNames });
        messageLog.destroy();
    }

    /**
     * Issues a MessageLoger information message saying which provider has 
     * been chosen to support a namespaceURI when multiple providers are available.
     * @param uri   the namespaceURI with multiple WSIFProviders 
     * @param providers  an array of the providers supporting the namespaceURI 
     */
    private static void issueChosenProviderMsg(String uri, WSIFProvider provider) {
        MessageLogger messageLog =
            MessageLogger.newMessageLogger("WSIF", "com.ibm.wsif.catalog.Messages");
        messageLog.message(
            WSIFConstants.TYPE_WARNING,
            "WSIF.0007I",
            new Object[] {
                provider == null ? "null" : provider.getClass().getName(),
                uri });
        messageLog.destroy();
    }

    /**
     * Get the dynamic proxy that will implement the interface iface
     * for the port portName.
     */
    public Object getStub(String portName, Class iface) throws WSIFException {
        TraceLogger.getGeneralTraceLogger().entry(new Object[] { portName, iface });

        // Initialise the type mappings here (not in the constructor) so that
        // other products which use non-standard WSDL in their complexTypes
        // that WSIF wouldn't understand, can use the DynamicInvoker 
        // successfully. Using the DynamicInvoker means we would never come
        // through this code and so never try to parse the complexTypes.
        // Obviously if the user wants to use dynamic proxies then we have to 
        // parse the complex types.
        if (!typeMapInitialised) {
            initialiseTypeMappings();
            typeMapInitialised = true;
        }

        PortType pt = getPortTypeFromPortName(portName);
        if (!pt.getQName().equals(this.portType.getQName())) {
        	throw new WSIFException("Port '" + portName + "' specified for stub "
 	       		+ "is not available for the port type "+this.portType.getQName());
        }
        // If the user has already created a proxy for this interface before
        // but is now asking for a proxy for the same interface but a different
        // portName, we should cache the proxy here and just call 
        // clientProxy.setPort() instead.
        WSIFClientProxy clientProxy =
            WSIFClientProxy.newInstance(
                iface,
                def,
                service.getQName().getNamespaceURI(),
                service.getQName().getLocalPart(),
                portType.getQName().getNamespaceURI(),
                portType.getQName().getLocalPart(),
                typeMap);

        clientProxy.setPort(getPort(portName));
        Object proxy = clientProxy.getProxy();

        // Tracing the proxy causes a hang!
        TraceLogger.getGeneralTraceLogger().exit();
        return proxy;
    }

    /**
     * Get the dynamic proxy that will implement the interface iface
     */
    public Object getStub(Class iface) throws WSIFException {
        TraceLogger.getGeneralTraceLogger().entry(new Object[] { iface });

        // Initialise the type mappings here (not in the constructor) so that
        // other products which use non-standard WSDL in their complexTypes
        // that WSIF wouldn't understand, can use the DynamicInvoker 
        // successfully. Using the DynamicInvoker means we would never come
        // through this code and so never try to parse the complexTypes.
        // Obviously if the user wants to use dynamic proxies then we have to 
        // parse the complex types.
        if (!typeMapInitialised) {
            initialiseTypeMappings();
            typeMapInitialised = true;
        }

        PortType pt = null;
        String portName = null;
        if (preferredPort != null) {
            pt = getPortTypeFromPortName(preferredPort);
            portName = preferredPort;
        } else {
            if (myPortsArr.length >= 1) {
                Port port = myPortsArr[0];
                portName = port.getName();
                pt = getPortTypeFromPortName(portName);
            } else {
                throw new WSIFException(
                    "Unable to resolve port type for stub using interface" + iface.getName());
            }
        }
        // If the user has already created a proxy for this interface before
        // but is now asking for a proxy for the same interface but a different
        // portName, we should cache the proxy here and just call 
        // clientProxy.setPort() instead.
        WSIFClientProxy clientProxy =
            WSIFClientProxy.newInstance(
                iface,
                def,
                service.getQName().getNamespaceURI(),
                service.getQName().getLocalPart(),
                pt.getQName().getNamespaceURI(),
                pt.getQName().getLocalPart(),
                typeMap);

        clientProxy.setPort(getPort(portName));
        Object proxy = clientProxy.getProxy();

        // Tracing the proxy causes a hang!
        TraceLogger.getGeneralTraceLogger().exit();
        return proxy;
    }

    /**
     * Add new WSDL model extension registry that is shared by all
     * dynamic WSIF providers.
     */
    public static void addExtensionRegistry(ExtensionRegistry reg) {
        providersExtRegs.addExtensionRegistry(reg);
    }

    /**
     * Return extension registry that contains ALL declared extensions.
     * This is special registry that does not allow to register serializers
     * but only to add new extension registreis through
     * addExtensionRegistry method.
     *
     * @see #addExtensionRegistry
     */
    public static ExtensionRegistry getCompositeExtensionRegistry() {
        return providersExtRegs;
    }

    private void init(Definition def, Service service, PortType portType)
        	throws WSIFException {
        if (def == null)
            throw new IllegalArgumentException("WSDL definition can not be null");
        checkWSDLForWSIF(def);

        if (service == null) {
            Map services = WSIFUtils.getAllItems(def, "Service");

            service = (Service) WSIFUtils.getNamedItem(services, null, "Service");
        }

        if (portType == null) {
            // if all ports have the same portType --> use it
            Map ports = service.getPorts();
            if (ports.size() == 0) {
                throw new WSIFException(
                    "WSDL must contain at least one port in " + service.getQName());
            }

            for (Iterator i = ports.values().iterator(); i.hasNext();) {
                Port port = (Port) i.next();
                if (portType == null) {
                    portType = port.getBinding().getPortType();
                } else {
                    PortType pt = port.getBinding().getPortType();
                    if (!pt.getQName().equals(portType.getQName())) {
                        throw new WSIFException(
                            "when no port type was specified all ports "
                                + "must have the same port type in WSDL service "
                                + service.getQName());
                    }
                }
            }
            if (portType == null) {
                throw new IllegalArgumentException(
                    "WSDL more than one portType in service " + service);

            }
        }
        this.def = def;
        this.service = service;
        this.portType = portType;

        // checkPortTypeIsRPC(Definition def, PortType portType) has been replaced by 
        // checkPortTypeInformation(Definition def, PortType portType) since "Input Only"
        // operations are supported.
        checkPortTypeInformation(def, portType);

        // get all ports from service that has given portType

        Map ports = service.getPorts();
        // check that service has at least one port ...
        if (ports.size() == 0) {
            throw new WSIFException(
                "WSDL must contain at least one port in " + service.getQName());
        }

        myPortsMap = new Hashtable();
        for (Iterator i = ports.values().iterator(); i.hasNext();) {
            Port port = (Port) i.next();

            Binding binding = port.getBinding();
            if (binding==null) continue; // Ignore this error for the moment

            // check if port has the same port type
            if (binding.getPortType().getQName().equals(portType.getQName())) {
                //if (port.getBinding().getPortType() == portType) {
                String portName = port.getName();
                myPortsMap.put(portName, port);
            }
        }
        int size = myPortsMap.size();
        myPortsArr = new Port[size];
        int count = 0;
        for (Iterator i = myPortsMap.values().iterator(); i.hasNext();) {
            // NOTE: there is no order in ports (it is hash function dependent...)
            Port port = (Port) i.next();
            myPortsArr[count++] = port;
        }
        
        // Check for JROM and set flag against all messages
        try {
  			Class c = Class.forName("com.ibm.jrom.JROMValue");
  			jromAvailable = true;
    	} catch (ClassNotFoundException cnf) {    	
    	}
    }

    /**
     * Use the Schema2Java class to read in the types from the WSDL
     * and generate the typeMap. This code has been mostly copied from 
     * the portType compiler generateTypes and printMappings.
     */
    private void initialiseTypeMappings() throws WSIFException {
        List typesElList = Utils.getAllTypesElements(def);
        if (typesElList.size() > 0) {
            String schemaURI1999 = Constants.NS_URI_1999_SCHEMA_XSD;
            Schema2Java s2j1999 = new Schema2Java(schemaURI1999);
            QName qElemSchema1999 = new QName(schemaURI1999, "schema");

            String schemaURI2000 = Constants.NS_URI_2000_SCHEMA_XSD;
            Schema2Java s2j2000 = new Schema2Java(schemaURI2000);
            QName qElemSchema2000 = new QName(schemaURI2000, "schema");

            String schemaURI2001 = Constants.NS_URI_2001_SCHEMA_XSD;
            Schema2Java s2j2001 = new Schema2Java(schemaURI2001);
            QName qElemSchema2001 = new QName(schemaURI2001, "schema");

            Iterator typesElIterator = typesElList.iterator();
            try {
                while (typesElIterator.hasNext()) {
                    UnknownExtensibilityElement unknExEl =
                        (UnknownExtensibilityElement) typesElIterator.next();
                    Element schemaEl = unknExEl.getElement();

                    if (qElemSchema1999.matches(schemaEl)
                        || qElemSchema2000.matches(schemaEl)
                        || qElemSchema2001.matches(schemaEl)) {
                        Hashtable typeReg = new Hashtable();
                        if (qElemSchema1999.matches(schemaEl))
                            s2j1999.createJavaMapping(schemaEl, typeReg);
                        else if (qElemSchema2000.matches(schemaEl))
                            s2j2000.createJavaMapping(schemaEl, typeReg);
                        else
                            s2j2001.createJavaMapping(schemaEl, typeReg);

                        Iterator typeMappingIterator = typeReg.values().iterator();
                        while (typeMappingIterator.hasNext()) {
                            TypeMapping tm = (TypeMapping) typeMappingIterator.next();

                            if (tm.elementType != null && tm.elementType.getNamespaceURI() != null) {
                                String namespaceURI = tm.elementType.getNamespaceURI();
                                if (!namespaceURI.equals(schemaURI1999)
                                    && !namespaceURI.equals(schemaURI2000)
                                    && !namespaceURI.equals(schemaURI2001)
                                    && tm.javaType != null) {
                                    String className =
                                        Utils.getPackageName(tm.javaType) + "." + Utils.getClassName(tm.javaType);
                                    Class clazz = null;

                                    try {
                                        clazz = Class.forName(className);
                                    } catch (ClassNotFoundException e) {
                                        throw new WSIFException(
                                            "WSIFService could not map QName "
                                                + tm.elementType
                                                + " to class "
                                                + className
                                                + " (JavaType "
                                                + tm.javaType
                                                + ")",
                                            e);
                                    }
                                    mapType(tm.elementType, clazz);
                                }
                            }
                        } // end while
                    } else {
                        throw new WSIFException(
                            "A 'wsdl:types' element must contain "
                                + "a '"
                                + qElemSchema1999
                                + "' or a '"
                                + qElemSchema2001
                                + "' element.");
                    }
                } // end while
            } catch (SchemaException se) {
                throw new WSIFException("Schema->Java problem: " + se.getMessage(), se);
            }
        }
    }

    /**
     * Check PortType information is consistent. This method can be updated when
     * new operation types are supported.
     */
    private void checkPortTypeInformation(Definition def, PortType portType)
        	throws WSIFException {
        List operationList = portType.getOperations();

        // process each operation to create dynamic operation instance
        for (Iterator i = operationList.iterator(); i.hasNext();) {
            Operation op = (Operation) i.next();
            String name = op.getName();
            if (op.isUndefined()) {
                throw new WSIFException("operation " + name + " is undefined!");
            }
            OperationType opType = op.getStyle();
            if (opType == null) {
                throw new WSIFException("operation " + name + " has no type!");
            }
            if (opType.equals(OperationType.REQUEST_RESPONSE)) {
                Input input = op.getInput();
                Output output = op.getOutput();
                if (input == null) {
                    throw new WSIFException("missing input message for operation " + name);
                }
                if (output == null) {
                    throw new WSIFException("missing output message for operation " + name);
                }
            } else if (opType.equals(OperationType.ONE_WAY)) {
                Input input = op.getInput();
                if (input == null) {
                    throw new WSIFException("missing input message for operation " + name);
                }
            } else {
                // Log message
                MessageLogger messageLog =
                    MessageLogger.newMessageLogger("WSIF", "com.ibm.wsif.catalog.Messages");
                messageLog.message(
                    WSIFConstants.TYPE_ERROR,
                    "WSIF.0004E",
                    new Object[] { opType, portType.getQName().getLocalPart()});
                messageLog.destroy();
                // End message
                throw new WSIFException(
                    "operation type "
                        + opType
                        + " is not supported in port instance for "
                        + portType.getQName());
            }
        }
    }

    private void checkWSDLForWSIF(Definition def) throws WSIFException {
        try {
            checkWSDL(def);
        } catch (WSDLException ex) {
            throw new WSIFException("invalid WSDL defintion " + def.getQName(), ex);
        }
    }

    /**
     * Check WSDL defintion to make sure it does not contain undefined
     * elements (typical case is referncing not defined portType).
     * <p><b>NOTE:</b> check is done only for curent document and not
     *  recursively for imported ones (they may be invalid but this
     *  port factory may not need them...).
     */
    private void checkWSDL(Definition def) throws WSDLException {
        for (Iterator i = def.getMessages().values().iterator(); i.hasNext();) {
            Message v = (Message) i.next();
            if (v.isUndefined()) {
                throw new WSDLException(
                    WSDLException.INVALID_WSDL,
                    "referencing undefined message " + v);
            }
        }
        for (Iterator i = def.getPortTypes().values().iterator(); i.hasNext();) {
            PortType v = (PortType) i.next();
            if (v.isUndefined()) {
                throw new WSDLException(
                    WSDLException.INVALID_WSDL,
                    "referencing undefined portType " + v);
            }
        }
        for (Iterator i = def.getBindings().values().iterator(); i.hasNext();) {
            Binding v = (Binding) i.next();
            if (v.isUndefined()) {
                throw new WSDLException(
                    WSDLException.INVALID_WSDL,
                    "referencing undefined binding " + v);
            }
        }
    }

	/**
	 * Returns a boolean to indicate if JROM is available to wsif
	 * @return The flag
	 */
	public static boolean getJROMAvailability() {
		// Return the static field via this method rather than make the 
		// field itself public. This way the field cannot be set by any 
		// classes we don't want to give access to.
		return jromAvailable;
	}
}

/**
 * This is utility class that allows to aggregate multiple
 * extensions registries into one. By default all standard WSDL4J
 * extensions are made available.
 */
class PrivateCompositeExtensionRegistry extends ExtensionRegistry {
    private Vector extRegs = new Vector();

    PrivateCompositeExtensionRegistry() {
        extRegs.add(new PopulatedExtensionRegistry());
    }

    public void addExtensionRegistry(ExtensionRegistry reg) {
        extRegs.insertElementAt(reg, 0);
    }

    public void registerSerializer(
	        Class parentType,
	        Class extensionType,
	        ExtensionSerializer es) {
        throw new RuntimeException(
            getClass() + " does not allow to register serializers");
    }

    public void registerDeserializer(
	        Class parentType,
	        QName elementType,
	        ExtensionDeserializer ed) {
        throw new RuntimeException(
            getClass() + " does not allow to register deserializers");
    }

    public ExtensionSerializer querySerializer(
	        Class parentType,
	        QName extensionType)
	        throws WSDLException {
        Enumeration enum = extRegs.elements();
        while (enum.hasMoreElements()) {
            ExtensionRegistry reg = (ExtensionRegistry) enum.nextElement();
            try {
                ExtensionSerializer ser = reg.querySerializer(parentType, extensionType);
                // Check that we're not looking at the default serializer
                ExtensionSerializer def = reg.getDefaultSerializer();
                if (ser != null && !(ser.equals(def))) {
                    return ser;
                }
            } catch (WSDLException ex) {
                throw ex;
            }
        }
        return new UnknownExtensionSerializer();
    }

    public ExtensionDeserializer queryDeserializer(
	        Class parentType,
	        QName elementType)
	        throws WSDLException {
        Enumeration enum = extRegs.elements();
        while (enum.hasMoreElements()) {
            ExtensionRegistry reg = (ExtensionRegistry) enum.nextElement();
            try {
                ExtensionDeserializer deser = reg.queryDeserializer(parentType, elementType);
                // Check that we're not looking at the default deserializer
                ExtensionDeserializer def = reg.getDefaultDeserializer();
                if (deser != null && !(deser.equals(def))) {
                    return deser;
                }
            } catch (WSDLException ex) {
                throw ex;
            }
        }
        return new UnknownExtensionDeserializer();
    }

    public ExtensibilityElement createExtension(
	        Class parentType,
	        QName elementType)
	        throws WSDLException {
        Enumeration enum = extRegs.elements();
        while (enum.hasMoreElements()) {
            ExtensionRegistry reg = (ExtensionRegistry) enum.nextElement();
            try {
                return reg.createExtension(parentType, elementType);
            } catch (WSDLException ignored) {
            }
        }
        return super.createExtension(parentType, elementType);
    }

}