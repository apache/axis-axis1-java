// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.stub;

import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.reflect.*;
import org.xml.sax.*;
import javax.wsdl.*;
import com.ibm.wsdl.util.*;
import com.ibm.wsdl.Constants;
import com.ibm.wsif.*;
import com.ibm.wsif.logging.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.wsdl.factory.*;
import javax.wsdl.xml.*;

/**
 * This class provides utilities for WSIF runtime and generated stubs.
 *
 * @author Alekander Slominski
 * @author Matthew J. Duftler
 * @author Sanjiva Weerawarana
 */
public class WSIFUtils {
  private final static String DEF_FACTORY_PROPERTY_NAME =
    "javax.wsdl.factory.WSDLFactory";
  private final static String PRIVATE_DEF_FACTORY_CLASS =
    "com.ibm.wsif.stub.WSIFPrivateWSDLFactoryImpl";
  private static Class initContextClass;
  
  /**
   * This checks whether JNDI classes are available at runtime.
   * If the return value is true, then generated stubs should
   * invoke lookupFactoryFromJNDI to get the WSIFService.
   * If not, then there's no need to do so.
   */
  public static boolean isJNDIAvailable () {
    try {
      initContextClass = Class.forName ("javax.naming.InitialContext", true,
                                        WSIFUtils.class.getClassLoader ());
    } catch (Exception e) {
      return false;
    }
    return true;
  }
  
  /**
   * Given the service and portType identification, return a factory
   * that can produce WSIFPorts for that service/portType combination.
   */
  public static WSIFService lookupFactoryFromJNDI (String serviceNS,
                                                       String serviceName,
                                                       String portTypeNS,
                                                       String portTypeName)
    throws WSIFException
  {
    if(serviceNS == null) throw new IllegalArgumentException(
        "service namespace can not be null");
    if(serviceName == null) throw new IllegalArgumentException(
        "service name can not be null");
    if(portTypeNS == null) throw new IllegalArgumentException(
        "port type namespace can not be null");
    if(portTypeName == null) throw new IllegalArgumentException(
        "port type name can not be null");
    
    try {
      if (initContextClass == null) {
        initContextClass = Class.forName ("javax.naming.InitialContext", true,
                                          WSIFUtils.class.getClassLoader ());
      }
      Object ic = initContextClass.newInstance ();
      Class[] lookupSig = new Class[] {String.class};
      Object[] lookupArgs = new String[] {serviceNS + "::" +
          serviceName + "::" +
          portTypeNS + "::" +
          portTypeName};
      Method m = initContextClass.getMethod ("lookup", lookupSig);
      return (WSIFService) m.invoke (ic, lookupArgs);
    } catch (Exception e) {
      throw new WSIFException ("Exception while looking up JNDI factory: " +
                                 e.getMessage (), e);
    }
  }
  
  public static Service selectService(Definition def,
                                      String serviceNS,
                                      String serviceName)
    throws WSIFException
  {
    Map services = getAllItems(def, "Service");
    QName serviceQName = ((serviceNS != null && serviceName != null)
                            ? new QName(serviceNS, serviceName)
                            : null);
    Service service =
      (Service)getNamedItem(services, serviceQName, "Service");
    
    return service;
  }
  
  public static PortType selectPortType(Definition def,
                                        String portTypeNS,
                                        String portTypeName)
    throws WSIFException
  {
    Map portTypes = getAllItems(def, "PortType");
    QName portTypeQName = ((portTypeNS != null && portTypeName != null)
                             ? new QName(portTypeNS, portTypeName)
                             : null);
    PortType portType =
      (PortType)getNamedItem(portTypes, portTypeQName, "PortType");
    
    return portType;
  }
  
  public static void addDefinedItems(Map fromItems,
                                     String itemType,
                                     Map toItems)
  {
    if (fromItems != null)
    {
      Iterator entryIterator = fromItems.entrySet().iterator();
      
      if (itemType.equals("Message"))
      {
        while (entryIterator.hasNext())
        {
          Map.Entry entry = (Map.Entry)entryIterator.next();
          Message message = (Message)entry.getValue();
          
          if (!message.isUndefined())
          {
            toItems.put(entry.getKey(), message);
          }
        }
      }
      else if (itemType.equals("Operation"))
      {
        while (entryIterator.hasNext())
        {
          Map.Entry entry = (Map.Entry)entryIterator.next();
          Operation operation = (Operation)entry.getValue();
          
          if (!operation.isUndefined())
          {
            toItems.put(entry.getKey(), operation);
          }
        }
      }
      else if (itemType.equals("PortType"))
      {
        while (entryIterator.hasNext())
        {
          Map.Entry entry = (Map.Entry)entryIterator.next();
          PortType portType = (PortType)entry.getValue();
          
          if (!portType.isUndefined())
          {
            toItems.put(entry.getKey(), portType);
          }
        }
      }
      else if (itemType.equals("Binding"))
      {
        while (entryIterator.hasNext())
        {
          Map.Entry entry = (Map.Entry)entryIterator.next();
          Binding binding = (Binding)entry.getValue();
          
          if (!binding.isUndefined())
          {
            toItems.put(entry.getKey(), binding);
          }
        }
      }
      else if (itemType.equals("Service"))
      {
        while (entryIterator.hasNext())
        {
          Map.Entry entry = (Map.Entry)entryIterator.next();
          Service service = (Service)entry.getValue();
          
          toItems.put(entry.getKey(), service);
        }
      }
    }
  }
  
  private static void getAllItems(Definition def,
                                  String itemType,
                                  Map toItems)
  {
    Map items = null;
    
    if (itemType.equals("PortType"))
    {
      items = def.getPortTypes();
    }
    else if (itemType.equals("Service"))
    {
      items = def.getServices();
    }
    else
    {
      throw new IllegalArgumentException("Don't know how to find all " +
                                           itemType + "s.");
    }
    
    addDefinedItems(items, itemType, toItems);
    
    Map imports = def.getImports();
    
    if (imports != null)
    {
      Iterator valueIterator = imports.values().iterator();
      
      while (valueIterator.hasNext())
      {
        List importList = (List)valueIterator.next();
        
        if (importList != null)
        {
          Iterator importIterator = importList.iterator();
          
          while (importIterator.hasNext())
          {
            Import tempImport = (Import)importIterator.next();
            
            if (tempImport != null)
            {
              Definition importedDef = tempImport.getDefinition();
              
              if (importedDef != null)
              {
                getAllItems(importedDef, itemType, toItems);
              }
            }
          }
        }
      }
    }
  }
  
  public static Map getAllItems(Definition def, String itemType)
  {
    Map ret = new HashMap();
    
    getAllItems(def, itemType, ret);
    
    return ret;
  }
  
  public static Object getNamedItem(Map items,
                                    QName qname,
                                    String itemType) throws WSIFException
  {
    if (qname != null)
    {
      Object item = items.get(qname);
      
      if (item != null)
      {
        return item;
      }
      else
      {
        throw new WSIFException(itemType + " '" + qname +
                                  "' not found. Choices are: " +
                                  getCommaListFromQNameMap(items));
      }
    }
    else
    {
      int size = items.size();
      
      if (size == 1)
      {
        Iterator valueIterator = items.values().iterator();
        
        return valueIterator.next();
      }
      else if (size == 0)
      {
        throw new WSIFException("WSDL document contains no " + itemType +
                                  "s.");
      }
      else
      {
        throw new WSIFException("Please specify a " + itemType +
                                  ". Choices are: " +
                                  getCommaListFromQNameMap(items));
      }
    }
  }
  
  private static String getCommaListFromQNameMap(Map qnameMap)
  {
    StringBuffer strBuf = new StringBuffer("{");
    Set keySet = qnameMap.keySet();
    Iterator keyIterator = keySet.iterator();
    int index = 0;
    
    while (keyIterator.hasNext())
    {
      QName key = (QName)keyIterator.next();
      
      strBuf.append((index > 0 ? ", " : "") + key);
      index++;
    }
    
    strBuf.append("}");
    
    return strBuf.toString();
  }
  
  /**
   * Read WSDL - it is different from standard readWSDL method as it is
   * using extensibility elements that were registered for dynamic port
   * factory.
   */
  public static Definition readWSDL(String contextURL,
                                    String wsdlLoc)
    throws WSDLException
  {
    TraceLogger.getGeneralTraceLogger().entry(
      new Object[] {contextURL,wsdlLoc});
  	
    initializeProviders();

    Properties props = System.getProperties();
    String oldPropValue = props.getProperty(DEF_FACTORY_PROPERTY_NAME);
    
    props.setProperty(DEF_FACTORY_PROPERTY_NAME,
                      PRIVATE_DEF_FACTORY_CLASS);
    
    WSDLFactory factory = WSDLFactory.newInstance();
    WSDLReader wsdlReader = factory.newWSDLReader();
    wsdlReader.setFeature(Constants.FEATURE_VERBOSE, false);
    try
    {
    	Definition def = wsdlReader.readWSDL(contextURL, wsdlLoc);
    
   		if (oldPropValue != null)
    	{
    		props.setProperty(DEF_FACTORY_PROPERTY_NAME, oldPropValue);
    	}
    	else
    	{
      		props.remove(DEF_FACTORY_PROPERTY_NAME);
    	}
        TraceLogger.getGeneralTraceLogger().exit(def);
    	return def;
  	}
    catch (WSDLException e)
    {
    	// Log message
        MessageLogger messageLog =
            MessageLogger.newMessageLogger("WSIF", "com.ibm.wsif.catalog.Messages");
        messageLog.message(
            WSIFConstants.TYPE_ERROR,
            "WSIF.0002E",
            new Object[] { wsdlLoc });
        messageLog.destroy();
        // End message
    	throw e;
    }
  }
  
  /**
   * Read WSDL - it is different from standard readWSDL method as it is
   * using extensibility elements that were registered for dynamic port
   * factory.  It also uses the accompanying class loader to load imported WSDL
   * resources.
   */
  public static Definition readWSDL(URL contextURL,
                                    Reader reader,
                                    ClassLoader cl)
    throws WSDLException
  {
  	TraceLogger.getGeneralTraceLogger().entry(
      new Object[] {contextURL,reader,cl});
      
    initializeProviders();

    Properties props = System.getProperties();
    String oldPropValue = props.getProperty(DEF_FACTORY_PROPERTY_NAME);
    
    props.setProperty(DEF_FACTORY_PROPERTY_NAME,
                      PRIVATE_DEF_FACTORY_CLASS);
    
    WSDLFactory factory = WSDLFactory.newInstance();
    WSDLReader wsdlReader = factory.newWSDLReader();
    wsdlReader.setFeature(Constants.FEATURE_VERBOSE, false);
    try
    {
    	String url = (contextURL == null) ? null: contextURL.toString();
    	Definition def = wsdlReader.readWSDL(url, new InputSource(reader), cl);
    	TraceLogger.getGeneralTraceLogger().exit(def);
    
    	if (oldPropValue != null)
    	{
    	  props.setProperty(DEF_FACTORY_PROPERTY_NAME, oldPropValue);
    	}
    	else
    	{
    	  props.remove(DEF_FACTORY_PROPERTY_NAME);
    	}    
    	return def;
    }
    catch (WSDLException e)
    {
    	// Log message
        MessageLogger messageLog =
            MessageLogger.newMessageLogger("WSIF", "com.ibm.wsif.catalog.Messages");
        messageLog.message(
            WSIFConstants.TYPE_ERROR,
            "WSIF.0002E",
            new Object[] { contextURL });
        messageLog.destroy();
        // End message
    	throw e;
    }
  }
  
  /**
   * Read WSDL - it is different from standard readWSDL method as it is
   * using extensibility elements that were registered for dynamic port
   * factory.  It also uses the accompanying class loader to load imported WSDL
   * resources.
   */
  public static Definition readWSDL(URL contextURL,
                                    String wsdlLoc,
                                    ClassLoader cl)
    throws WSDLException
  {
  	  	TraceLogger.getGeneralTraceLogger().entry(
      new Object[] {contextURL,wsdlLoc,cl});
      
    initializeProviders();

    Properties props = System.getProperties();
    String oldPropValue = props.getProperty(DEF_FACTORY_PROPERTY_NAME);
    
    props.setProperty(DEF_FACTORY_PROPERTY_NAME,
                      PRIVATE_DEF_FACTORY_CLASS);
    
    WSDLFactory factory = WSDLFactory.newInstance();
    WSDLReader wsdlReader = factory.newWSDLReader();
    wsdlReader.setFeature(Constants.FEATURE_VERBOSE, false);
    
    try
    {
    	String url = (contextURL == null) ? null: contextURL.toString();
    	Definition def = wsdlReader.readWSDL(url, wsdlLoc, cl);
    	TraceLogger.getGeneralTraceLogger().exit(def);
    
    	if (oldPropValue != null)
    	{
    	  props.setProperty(DEF_FACTORY_PROPERTY_NAME, oldPropValue);
    	}
    	else
    	{
    	  props.remove(DEF_FACTORY_PROPERTY_NAME);
    	}    
    	return def;
    }
    catch (WSDLException e)
    {
    	// Log message
        MessageLogger messageLog =
            MessageLogger.newMessageLogger("WSIF", "com.ibm.wsif.catalog.Messages");
        messageLog.message(
            WSIFConstants.TYPE_ERROR,
            "WSIF.0002E",
            new Object[] { wsdlLoc });
        messageLog.destroy();
        // End message
    	throw e;
    }
  }
   
  /**
   * Read WSDL - it is different from standard readWSDL method as it is
   * using extensibility elements that were registered for dynamic port
   * factory.
   */
  public static Definition readWSDL(String contextURL,
                                    Reader reader)
    throws WSDLException
  {
    TraceLogger.getGeneralTraceLogger().entry(
      new Object[] {contextURL,reader});

    initializeProviders();

    Properties props = System.getProperties();
    String oldPropValue = props.getProperty(DEF_FACTORY_PROPERTY_NAME);
    
    props.setProperty(DEF_FACTORY_PROPERTY_NAME,
                      PRIVATE_DEF_FACTORY_CLASS);
    
    WSDLFactory factory = WSDLFactory.newInstance();
    WSDLReader wsdlReader = factory.newWSDLReader();
    wsdlReader.setFeature(Constants.FEATURE_VERBOSE, false);
    Definition def = wsdlReader.readWSDL(contextURL, new InputSource(reader));
    
    if (oldPropValue != null)
    {
      props.setProperty(DEF_FACTORY_PROPERTY_NAME, oldPropValue);
    }
    else
    {
      props.remove(DEF_FACTORY_PROPERTY_NAME);
    }
    
    TraceLogger.getGeneralTraceLogger().exit(def);
    return def;
  }
  
  /**
   * Read WSDL - it is different from standard readWSDL method as it is
   * using extensibility elements that were registered for dynamic port
   * factory.
   */
  public static Definition readWSDL(String contextURL,
                                    Document wsdlDocument)
    throws WSDLException
  {
    TraceLogger.getGeneralTraceLogger().entry(
      new Object[] {contextURL,wsdlDocument});
      
    initializeProviders();

    Properties props = System.getProperties();
    String oldPropValue = props.getProperty(DEF_FACTORY_PROPERTY_NAME);
    
    props.setProperty(DEF_FACTORY_PROPERTY_NAME,
                      PRIVATE_DEF_FACTORY_CLASS);
    
    WSDLFactory factory = WSDLFactory.newInstance();
    WSDLReader wsdlReader = factory.newWSDLReader();
    wsdlReader.setFeature(Constants.FEATURE_VERBOSE, false);
    Definition def = wsdlReader.readWSDL(contextURL, wsdlDocument);
    
    if (oldPropValue != null)
    {
      props.setProperty(DEF_FACTORY_PROPERTY_NAME, oldPropValue);
    }
    else
    {
      props.remove(DEF_FACTORY_PROPERTY_NAME);
    }
    
    TraceLogger.getGeneralTraceLogger().exit(def);
    return def;
  }

  /**
   * Read WSDL - it is different from standard readWSDL method as it is
   * using extensibility elements that were registered for dynamic port
   * factory.
   */
  public static Definition readWSDL(String contextURL,
                                    Element wsdlServicesElement)
    throws WSDLException
  {
    TraceLogger.getGeneralTraceLogger().entry(
      new Object[] {contextURL,wsdlServicesElement});

    initializeProviders();

    Properties props = System.getProperties();
    String oldPropValue = props.getProperty(DEF_FACTORY_PROPERTY_NAME);
    
    props.setProperty(DEF_FACTORY_PROPERTY_NAME,
                      PRIVATE_DEF_FACTORY_CLASS);
    
    WSDLFactory factory = WSDLFactory.newInstance();
    WSDLReader wsdlReader = factory.newWSDLReader();
    wsdlReader.setFeature(Constants.FEATURE_VERBOSE, false);
    Definition def = wsdlReader.readWSDL(contextURL, wsdlServicesElement);
    
    if (oldPropValue != null)
    {
      props.setProperty(DEF_FACTORY_PROPERTY_NAME, oldPropValue);
    }
    else
    {
      props.remove(DEF_FACTORY_PROPERTY_NAME);
    }
    
    TraceLogger.getGeneralTraceLogger().exit(def);
    return def;
  }
  
  /**
   * Write WSDL - it is different from standard writeWSDL method as it is
   * using extensibility elements that were registered for dynamic port
   * factory.
   */
  public static void writeWSDL(Definition def,
                               Writer sink)
    throws WSDLException
  {
    Properties props = System.getProperties();
    String oldPropValue = props.getProperty(DEF_FACTORY_PROPERTY_NAME);
    
    props.setProperty(DEF_FACTORY_PROPERTY_NAME,
                      PRIVATE_DEF_FACTORY_CLASS);
    
    WSDLFactory factory = WSDLFactory.newInstance();
    WSDLWriter wsdlWriter = factory.newWSDLWriter();
    wsdlWriter.writeWSDL(def, sink);
    
    if (oldPropValue != null)
    {
      props.setProperty(DEF_FACTORY_PROPERTY_NAME, oldPropValue);
    }
    else
    {
      props.remove(DEF_FACTORY_PROPERTY_NAME);
    }
  }
  
  public static Definition getDefinitionFromLocation(String contextURL,
                                                     String location)
    throws WSIFException
  {
    if (location == null)
    {
      throw new WSIFException("WSDL location must not be null.");
    }
    
    try
    {
      return WSIFUtils.readWSDL(contextURL, location);
    }
    catch (WSDLException e)
    {
      throw new WSIFException("Problem reading WSDL document.", e);
    }
  }
  
  public static Definition getDefinitionFromContent(String contextURL,
                                                    String content)
    throws WSIFException
  {
    if (content == null)
    {
      throw new WSIFException("WSDL content must not be null.");
    }
    
    try
    {
      return WSIFUtils.readWSDL(contextURL, new StringReader(content));
    }
    catch (WSDLException e)
    {
      throw new WSIFException("Problem reading WSDL document.", e);
    }
  }
  
  private static Boolean providersInitialized = new Boolean(false);
  
  /**
   * Initialize the WSIF providers. Each provider initializes its WSDL
   * extension registries. This has no effect if AutoLoad providers has
   * been turned off on WSIFServiceImpl ... in that case it is the
   * responsibility of the application to initialize providers.
   */
  private static void initializeProviders() {
  	synchronized (providersInitialized) {
        if (!providersInitialized.booleanValue()) {  
            WSIFServiceImpl.getDynamicWSIFProvider("/");
            providersInitialized = new Boolean(true);
  	    }
  	}
  }
}

