/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.axis;

import java.io.*;
import java.net.URL;
import java.util.* ;
import org.apache.axis.* ;
import org.apache.axis.client.Transport;
import org.apache.axis.utils.* ;
import org.apache.axis.handlers.* ;
import org.apache.axis.handlers.soap.* ;
import org.apache.axis.registries.* ;
import org.apache.axis.session.Session;
import org.apache.axis.session.SimpleSession;
import org.apache.axis.encoding.*;

import org.w3c.dom.*;

/**
 * An <code>AxisEngine</code> is the base class for AxisClient and
 * AxisServer.  Handles common functionality like dealing with the
 * handler/service registries and loading properties.
 *
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public abstract class AxisEngine extends BasicHandler
{
    /** The handler registry this Engine uses. */
    protected HandlerRegistry _handlerRegistry;
    protected String _handlerRegFilename;
    
    /** The service registry this Engine uses. */
    protected HandlerRegistry _serviceRegistry;
    protected String _serviceRegFilename;
    
    private boolean readRegistryFiles = true;
    private boolean dontSaveYet = false;
    
    /** This Engine's global type mappings     */
    protected TypeMappingRegistry _typeMappingRegistry =
                                     new SOAPTypeMappingRegistry();
    
    protected Properties props = new Properties();
    
    /** A map of protocol names to "client" (sender) transports
     */
    protected SupplierRegistry transportRegistry = new SupplierRegistry();
                                                        
    //protected SupplierRegistry listenerRegistry = new SupplierRegistry();
    
    /**
     * This engine's Session.  This Session supports "application scope"
     * in the Apache SOAP sense... if you have a service with "application
     * scope", have it store things in this Session.
     */
    private Session session = new SimpleSession();
    
    /**
     * No-arg constructor.  Loads properties from the "axis.properties"
     * file if it exists.
     *
     */
    public AxisEngine()
    {
        Debug.Print( 1, "Enter: AxisEngine no-arg constructor");
        try {
            File propFile = new File("axis.properties");
            if (propFile.exists()) {
                FileInputStream propFileInputStream =
                         new FileInputStream(propFile);
                props.load(propFileInputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Debug.Print( 1, "Exit: AxisEngine no-arg constructor");
    }
    
    /**
     * Allows the Listener to specify which handler/service registry
     * implementation they want to use.
     *
     * @param handlers the Handler registry.
     * @param services the Service registry.
     */
    public AxisEngine(HandlerRegistry handlers, HandlerRegistry services)
    {
        this();
        setHandlerRegistry(handlers);
        setServiceRegistry(services);
        
        readRegistryFiles = false;
        
        init();
    }

    /**
     * Constructor specifying registry filenames.
     *
     * @param handlerRegFilename the name of the Handler registry file.
     * @param serviceRegFilename the name of the Service registry file.
     */
    public AxisEngine(String handlerRegFilename, String serviceRegFilename)
    {
        this();
        setHandlerRegistry(new SupplierRegistry());
        setServiceRegistry(new SupplierRegistry());
        
        _handlerRegFilename = handlerRegFilename;
        _serviceRegFilename = serviceRegFilename;
        
        init();
    }


    /**
     * Subclasses (client and server) must define the defaults.
     */
    abstract protected void deployDefaultHandlers();
    abstract protected void deployDefaultServices();
    abstract protected void deployDefaultTransports();

    /**
     * (re)initialize - What should really go in here???
     */
    public void init() {
        // Load the simple handler registry and init it
        Debug.Print( 1, "Enter: AxisEngine::init" );
        
        String propVal = props.getProperty("debugLevel", "0");
        Debug.setDebugLevel(Integer.parseInt(propVal));
        
        propVal = props.getProperty("debugFile");
        Debug.setToFile(propVal != null);

        initializeHandlers();
        initializeServices();
        initializeTransports();

        // Later...
        //initializeTypeMappings();

        // Load the registry of deployed types
        TypeMappingRegistry tmr = new TypeMappingRegistry("typemap-supp.reg");
        tmr.setParent(new SOAPTypeMappingRegistry());
        _typeMappingRegistry = tmr;
        
        tmr.init();
        
        Debug.Print( 1, "Exit: AxisEngine::init" );
    }
    
    /**
     * Set up our handler registry, either by reading from the designated
     * XML file or setting up the defaults if that doesn't work.
     */
    private void initializeHandlers()
    {
      if (!readRegistryFiles)
        return;
      
      dontSaveYet = true;

      try {
        FileInputStream    fis = new FileInputStream(_handlerRegFilename);
        
        Document doc = XMLUtils.newDocument(fis);
        
        Element root = doc.getDocumentElement();
        Element elem;
        
        NodeList list = root.getElementsByTagName("handler");
        for (int i = 0; i < list.getLength(); i++) {
          elem = (Element)list.item(i);
          Admin.registerHandler(elem, this);
        }
        
        list = root.getElementsByTagName("chain");
        for (int i = 0; i < list.getLength(); i++) {
          elem = (Element)list.item(i);
          Admin.registerChain(elem, this);
        }
        
        fis.close();
        return;
      }
      catch( Exception e ) {
        if ( !(e instanceof FileNotFoundException) ) {
          e.printStackTrace( System.err );
        }
      } finally {
        dontSaveYet = false;
      }
      
      Debug.Print(2, "Deploying default handlers...");
      deployDefaultHandlers();
      dontSaveYet = false;
      
      // We don't actually need to save right now, since by definition
      // nothing has changed from the persistent version (or the defaults)
    }
    
    /**
     * Set up our service registry, either by reading from the designated
     * XML file or setting up the defaults if that doesn't work.
     */
    private void initializeServices()
    {
      if (!readRegistryFiles)
        return;
      
      dontSaveYet = true;
      try {
        FileInputStream    fis = new FileInputStream(_serviceRegFilename);
        
        Document doc = XMLUtils.newDocument(fis);
        
        Element root = doc.getDocumentElement();
        
        NodeList list = root.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
          if (!(list.item(i) instanceof Element))
            continue;
          Element elem = (Element)list.item(i);
          if (!elem.getTagName().equals("service")) {
            System.err.println("Found element '" + elem.getTagName() +
                  " in service reg file, wanted <service>");
          }
          Admin.registerService(elem, this);
        }
        
        fis.close();
        return;
      }
      catch( Exception e ) {
        if ( !(e instanceof FileNotFoundException) ) {
          e.printStackTrace( System.err );
        }
      } finally {
        dontSaveYet = false;
      }
      
      Debug.Print(2, "Deploying default services...");
      deployDefaultServices();
      dontSaveYet = false;
    }
    
    public void initializeTransports()
    {
      deployDefaultTransports();
    }

    public HandlerRegistry getHandlerRegistry()
    {
        return _handlerRegistry;
    }
    
    public void setHandlerRegistry(HandlerRegistry registry)
    {
        _handlerRegistry = registry;
    }
    
    public HandlerRegistry getServiceRegistry()
    {
        return _serviceRegistry;
    }
    
    public void setServiceRegistry(HandlerRegistry registry)
    {
        _serviceRegistry = registry;
    }
    
    public SupplierRegistry getTransportRegistry()
    {
        return transportRegistry;
    }
    
    public void setTransportRegistry(SupplierRegistry registry)
    {
        transportRegistry = registry;
    }
    
    public TypeMappingRegistry getTypeMappingRegistry()
    {
        return _typeMappingRegistry;
    }
    
    public void saveHandlerRegistry()
    {
      if (dontSaveYet || (_handlerRegFilename == null))
        return;
      
      try {
        FileOutputStream fos = new FileOutputStream(_handlerRegFilename);
        Document doc = XMLUtils.newDocument();
        Element el = Admin.list(doc, this, false);
        doc.appendChild(el);
        XMLUtils.DocumentToStream(doc, fos);
        fos.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    public void saveServiceRegistry()
    {
      if (dontSaveYet || (_serviceRegFilename == null))
        return;
      
      try {
        FileOutputStream fos = new FileOutputStream(_serviceRegFilename);
        Document doc = XMLUtils.newDocument();
        Element el = Admin.list(doc, this, true);
        doc.appendChild(el);
        XMLUtils.DocumentToStream(doc, fos);
        fos.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    /*********************************************************************
     * Administration and management APIs
     *
     * These can get called by various admin adapters, such as JMX MBeans,
     * our own Admin client, web applications, etc...
     *
     *********************************************************************
     */
    
    /**
     * Register a new global type mapping
     */
    public void registerTypeMapping(QName qName,
                                    Class cls,
                                    DeserializerFactory deserFactory,
                                    Serializer serializer)
    {
        if (deserFactory != null)
            _typeMappingRegistry.addDeserializerFactory(qName, cls, deserFactory);
        if (serializer != null)
            _typeMappingRegistry.addSerializer(cls, qName, serializer);
    }
        
    /**
     * Unregister a global type mapping
     */
    public void unregisterTypeMapping(QName qName, Class cls)
    {
        _typeMappingRegistry.removeDeserializer(qName);
        _typeMappingRegistry.removeSerializer(cls);
    }
    
    /**
     * Deploy a Handler into our handler registry
     */
    public void deployHandler(String key, Handler handler)
    {
        handler.setName(key);
        getHandlerRegistry().add(key, handler);
        saveHandlerRegistry();
    }
    
    /**
     * Undeploy (remove) a Handler from the handler registry
     */
    public void undeployHandler(String key)
    {
        getHandlerRegistry().remove(key);
        saveHandlerRegistry();
    }

    /**
     * Deploy a Service into our service registry
     */
    public void deployService(String key, SOAPService service)
    {
        service.setName(key);
        service.setEngine(this);
        
        getServiceRegistry().add(key, service);
        saveServiceRegistry();
    }
    
    /**
     * Undeploy (remove) a Service from the handler registry
     */
    public void undeployService(String key)
    {
        getServiceRegistry().remove(key);
        saveServiceRegistry();
    }

    /**
     * Deploy a (client) Transport
     */
    public void deployTransport(String key, Handler transport)
    {
        transportRegistry.add(key, transport);
    }
    
    /**
     * Deploy a (client) Transport
     */
    public void deployTransport(String key, Supplier supplier)
    {
        transportRegistry.add(key, supplier);
    }
    
    /**
     * Undeploy (remove) a client Transport
     */
    public void undeployTransport(String key)
    {
        transportRegistry.remove(key);
    }

    /**
     * accessor only, for application session
     * (could call it "engine session" instead, but named with reference
     * to Apache SOAP's notion of "application scope")
     */
    public Session getApplicationSession () {
        return session;
    }

};
