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
*        Apache Software Foundation (http://www.apache.org/)."
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

package org.apache.axis.client ;

import javax.wsdl.extensions.soap.SOAPAddress;
import org.apache.axis.AxisEngine;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.configuration.DefaultEngineConfigurationFactory;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;

import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.encoding.TypeMappingRegistry;
import javax.xml.rpc.handler.HandlerRegistry;
import javax.xml.rpc.namespace.QName;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.lang.reflect.Proxy;

/**
 * Axis' JAXRPC Dynamic Invoation Interface implementation of the Service
 * interface.
 *
 * The Service class should be used a the starting point for access
 * SOAP Web Services.  Typically, a Service will be created with a WSDL
 * document and along with a serviceName you can then ask for a Call
 * object that will allow you to invoke a Web Service.
 *
 * @author Doug Davis (dug@us.ibm.com)
 */

public class Service implements javax.xml.rpc.Service, Serializable, Referenceable {
    private transient AxisEngine          engine = null;
    private transient EngineConfiguration config =
        (new DefaultEngineConfigurationFactory()).getClientEngineConfig();

    private URL                 wsdlLocation    = null ;
    private Definition          wsdlDefinition  = null ;
    private javax.wsdl.Service  wsdlService     = null ;
    private boolean             maintainSession = false ;

    /**
     * Thread local storage used for storing the last call object
     */
    private static ThreadLocal previousCall = new ThreadLocal();
    static private HashMap      cachedWSDL  = new HashMap();
    static private boolean      cachingWSDL = true ;


    Definition getWSDLDefinition() {
        return( wsdlDefinition );
    }

    javax.wsdl.Service getWSDLService() {
        return( wsdlService );
    }

    protected AxisClient getAxisClient()
    {
        return new AxisClient(config);
    }

    /**
     * Constructs a new Service object - this assumes the caller will set
     * the appropriate fields by hand rather than getting them from the
     * WSDL.
     */
    public Service() {
        engine = getAxisClient();
    }

    /**
     * Constructs a new Service object as above, but also passing in
     * the EngineConfiguration which should be used to set up the
     * AxisClient.
     */
    public Service(EngineConfiguration config) {
        this.config = config;
        engine = getAxisClient();
    }

    /**
     * Constructs a new Service object for the service in the WSDL document
     * pointed to by the wsdlDoc URL and serviceName parameters.
     *
     * @param wsdlDoc          URL of the WSDL document
     * @param serviceName      Qualified name of the desired service
     * @throws ServiceException If there's an error finding or parsing the WSDL
     */
    public Service(URL wsdlDoc, QName serviceName) throws ServiceException {
        engine = getAxisClient();
        this.wsdlLocation = wsdlDoc;
        Definition def = null ;

        if ( cachingWSDL &&
             (def = (Definition) cachedWSDL.get(this.wsdlLocation.toString())) != null ){
          initService( def, serviceName );
        }
        else {
          Document doc = XMLUtils.newDocument(wsdlDoc.toString());
          initService(doc, serviceName);
        }
    }

    /**
     * Constructs a new Service object for the service in the WSDL document
     * pointed to by the wsdlLocation and serviceName parameters.  This is
     * just like the previous constructor but instead of URL the
     * wsdlLocation parameter points to a file on the filesystem relative
     * to the current directory.
     *
     * @param  wsdlLocation    Location of the WSDL relative to the current dir
     * @param  serviceName     Qualified name of the desired service
     * @throws ServiceException If there's an error finding or parsing the WSDL
     */
    public Service(String wsdlLocation, QName serviceName)
                           throws ServiceException {
        engine = getAxisClient();
        try {
            this.wsdlLocation = new URL(wsdlLocation);
        }
        catch (MalformedURLException mue) {
        }
        try {
            // Start by reading in the WSDL using WSDL4J
            Definition def = null ;
            if ( cachingWSDL && 
                 (def = (Definition) cachedWSDL.get(this.wsdlLocation.toString())) != null ) {
              initService( def, serviceName );
            }
            else {
              FileInputStream  fis = new FileInputStream(wsdlLocation);
              Document         doc = XMLUtils.newDocument(fis);
              initService(doc, serviceName);
            }
        }
        catch( FileNotFoundException exp ) {
            throw new ServiceException(
                    JavaUtils.getMessage("wsdlError00", "" + wsdlLocation, "\n" + exp) );
        }
    }

    /**
     * Constructs a new Service object for the service in the WSDL document
     * in the wsdlInputStream and serviceName parameters.  This is
     * just like the previous constructor but instead of reading the WSDL
     * from a file (or from a URL) it is in the passed in InputStream.
     *
     * @param  wsdlInputStream InputStream containing the WSDL
     * @param  serviceName     Qualified name of the desired service
     * @throws ServiceException If there's an error finding or parsing the WSDL
     */
    public Service(InputStream wsdlInputStream, QName serviceName)
                           throws ServiceException {
        engine = getAxisClient();
        Document doc = XMLUtils.newDocument(wsdlInputStream);
        initService(doc, serviceName);
    }

    /**
     * Common code for building up the Service from a WSDL document
     *
     * @param doc               A DOM document containing WSDL
     * @param serviceName       Qualified name of the desired service
     * @throws ServiceException  If there's an error finding or parsing the WSDL
     */
    private void initService(Document doc, QName serviceName)
            throws ServiceException {
        try {
            // Start by reading in the WSDL using WSDL4J
            WSDLReader           reader = WSDLFactory.newInstance()
                                                     .newWSDLReader();
            reader.setFeature("javax.wsdl.verbose", false);
            Definition           def    = reader.readWSDL( null, doc );

            if ( cachingWSDL && this.wsdlLocation != null )
              cachedWSDL.put( this.wsdlLocation.toString(), def );

            initService( def, serviceName );
        }
        catch( Exception exp ) {
            throw new ServiceException(
                    JavaUtils.getMessage("wsdlError00", "" + "", "\n" + exp) );
        }
    }

    private void initService(Definition def, QName serviceName)
            throws ServiceException {
        try {
            this.wsdlDefinition = def ;

            // grrr!  Too many flavors of QName
            String           ns = serviceName.getNamespaceURI();
            String           lp = serviceName.getLocalPart();
            javax.wsdl.QName qn = new javax.wsdl.QName( ns, lp );

            this.wsdlService    = def.getService( qn );
            if ( this.wsdlService == null )
                throw new ServiceException(
                        JavaUtils.getMessage("noService00", "" + serviceName));
        }
        catch( Exception exp ) {
            throw new ServiceException(
                    JavaUtils.getMessage("wsdlError00", "" + "", "\n" + exp) );
        }
    }

    /**
     * Not implemented yet
     *
     * @param  portName        ...
     * @param  proxyInterface  ...
     * @return java.rmi.Remote ...
     * @throws ServiceException If there's an error
     */
    public java.rmi.Remote getPort(QName portName, Class proxyInterface)
                           throws ServiceException {
        return( null );
    }

    /**
     * Not implemented yet
     *
     * @param  proxyInterface  ...
     * @return java.rmi.Remote ...
     * @throws ServiceException If there's an error
     */
    public java.rmi.Remote getPort(Class proxyInterface)
            throws ServiceException {
        return null;
    }

    /**
     * Return an object which acts as a dynamic proxy for the passed
     * interface class.  This is a more "dynamic" version in that it
     * doesn't actually require WSDL, simply an endpoint address.
     *
     * Note: Not part of the JAX-RPC spec.
     *
     * @param endpoint the URL which will be used as the SOAP endpoint
     * @param proxyInterface the interface class which we wish to mimic
     *                       via a dynamic proxy
     * @throws ServiceException
     */
    public java.rmi.Remote getPort(String endpoint, Class proxyInterface)
        throws ServiceException
    {
        if (!proxyInterface.isInterface()) {
            throw new ServiceException(JavaUtils.getMessage("mustBeIface00"));
        }

        if (!(java.rmi.Remote.class.isAssignableFrom(proxyInterface))) {
            throw new ServiceException(
                            JavaUtils.getMessage("mustExtendRemote00"));
        }

        try {
            Call call = (org.apache.axis.client.Call)createCall();
            call.setTargetEndpointAddress(new URL(endpoint));
            ClassLoader classLoader =
                    Thread.currentThread().getContextClassLoader();
            return (java.rmi.Remote)Proxy.newProxyInstance(classLoader,
                                                new Class[] { proxyInterface },
                                                new AxisClientProxy(call));
        } catch (Exception e) {
            throw new ServiceException(e.toString());
        }
    }

    /**
     * Creates a new Call object - will prefill as much info from the WSDL
     * as it can.  Right now it's just the target URL of the Web Service.
     *
     * @param  portName        PortName in the WSDL doc to search for
     * @return Call            Used for invoking the Web Service
     * @throws ServiceException If there's an error
     */
    public javax.xml.rpc.Call createCall(QName portName)
                            throws ServiceException {
        javax.wsdl.QName qn = new javax.wsdl.QName( portName.getNamespaceURI(),
                                                    portName.getLocalPart() );
        if ( wsdlDefinition == null )
            throw new ServiceException( JavaUtils.getMessage("wsdlMissing00") );

        Port port = wsdlService.getPort( portName.getLocalPart() );
        if ( port == null )
            throw new ServiceException( JavaUtils.getMessage("noPort00", "" + portName) );

        Binding   binding  = port.getBinding();
        PortType  portType = binding.getPortType();
        if ( portType == null )
            throw new ServiceException( JavaUtils.getMessage("noPortType00", "" + portName) );

        Call call = (org.apache.axis.client.Call)createCall();
        call.setPortTypeName( portName );

        // Get the URL
        ////////////////////////////////////////////////////////////////////
        List list = port.getExtensibilityElements();
        for ( int i = 0 ; list != null && i < list.size() ; i++ ) {
            Object obj = list.get(i);
            if ( obj instanceof SOAPAddress ) {
                try {
                    SOAPAddress addr = (SOAPAddress) obj ;
                    URL         url  = new URL(addr.getLocationURI());
                    call.setTargetEndpointAddress(url);
                }
                catch(Exception exp) {
                    throw new ServiceException(
                            JavaUtils.getMessage("cantSetURI00", "" + exp) );
                }
            }
        }

        return( call );
    }

    /**
     * Creates a new Call object - will prefill as much info from the WSDL
     * as it can.  Right now it's target URL, SOAPAction, Parameter types,
     * and return type of the Web Service.
     *
     * @param  portName        PortName in the WSDL doc to search for
     * @param  operationName   Operation(method) that's going to be invoked
     * @return Call            Used for invoking the Web Service
     * @throws ServiceException If there's an error
     */
    public javax.xml.rpc.Call createCall(QName portName,
                                         String operationName)
                           throws ServiceException {

        Call call = (org.apache.axis.client.Call)createCall();
        call.setOperation( portName, operationName );
        return( call );
    }

    /**
     * Creates a new Call object - will prefill as much info from the WSDL
     * as it can.  Right now it's target URL, SOAPAction, Parameter types,
     * and return type of the Web Service.
     *
     * @param  portName        PortName in the WSDL doc to search for
     * @param  operationName   Operation(method) that's going to be invoked
     * @return Call            Used for invoking the Web Service
     * @throws ServiceException If there's an error
     */
    public javax.xml.rpc.Call createCall(QName portName,
                                         QName operationName)
                           throws ServiceException {

        Call call = (org.apache.axis.client.Call)createCall();
        call.setOperation( portName, operationName.getLocalPart() );
        return( call );
    }

    /**
     * Creates a new Call object with no prefilled data.  This assumes
     * that the caller will set everything manually - no checking of
     * any kind will be done against the WSDL.
     *
     * @return Call            Used for invoking the Web Service
     * @throws ServiceException If there's an error
     */
    public javax.xml.rpc.Call createCall() throws ServiceException {
        Call call = new org.apache.axis.client.Call(this);
        previousCall.set(call);
        return call;
    }

    /**
     * Gets an array of preconfigured Call objects for invoking operations
     * on the specified port. There is one Call object per operation that
     * can be invoked on the specified port. Each Call object is
     * pre-configured and does not need to be configured using the setter
     * methods on Call interface.
     *
     * This method requires the Service implementation class to have access
     * to the WSDL related metadata.
     *
     * Not implemented.
     *
     * @param portName - Qualified name for the target service endpoint
     *
     * @throws ServiceException - If this Service class does not have access
     * to the required WSDL metadata or if an illegal portName is specified.
     */
    public javax.xml.rpc.Call[] getCalls() throws ServiceException {
        if (wsdlLocation == null) {
            throw new ServiceException();
        }
        else {
            return new javax.xml.rpc.Call[0];
        }
    }

    /**
     * Returns the configured HandlerRegistry instance for this Service
     * instance.
     *
     * NOTE:  This Service currently does not support the configuration
     *        of a HandlerRegistry!  It will throw a
     *        java.lang.UnsupportedOperationException.
     *
     * @return HandlerRegistry
     * @throws java.lang.UnsupportedOperationException - if the Service
     *         class does not support the configuration of a
     *         HandlerRegistry.
     */
    public HandlerRegistry getHandlerRegistry() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the location of the WSDL document used to prefill the data
     * (if one was used at all).
     *
     * @return URL URL pointing to the WSDL doc
     */
    public URL getWSDLDocumentLocation() {
        return wsdlLocation ;
    }

    /**
     * Returns the qualified name of the service (if one is set).
     *
     * @return QName Fully qualified name of this service.
     */
    public QName getServiceName() {
        if ( wsdlService == null ) return( null );
        javax.wsdl.QName  qn = wsdlService.getQName();
        return( new QName( qn.getNamespaceURI(), qn.getLocalPart() ) );
    }

    /**
     * Returns an Iterator that can be used to get all of the ports
     * specified in the WSDL file associated with this Service (if there
     * is a WSDL file).
     *
     * @return Iterator The ports specified in the WSDL file
     */
    public Iterator getPorts() {
        if ( wsdlService == null ) return( null );
        Map map = wsdlService.getPorts();

        if ( map == null ) return( null );

        return map.values().iterator();
    }

    /**
     * Defines the current Type Mappig Registry.
     *
     * @param  registry The TypeMappingRegistry
     * @throws ServiceException if there's an error
     */
    public void setTypeMappingRegistry(TypeMappingRegistry registry)
                    throws ServiceException  {
    }

    /**
     * Returns the current TypeMappingRegistry or null.
     *
     * @return TypeMappingRegistry The registry
     */
    public TypeMappingRegistry getTypeMappingRegistry() {
        return( engine.getTypeMappingRegistry() );
    }

    /**
     * Returns a reference to this object.
     *
     * @return Reference ...
     */
    public Reference getReference() {
        String classname = this.getClass().getName();
        Reference reference = new Reference(classname,
                "org.apache.axis.client.ServiceFactory", null);
        StringRefAddr addr = null;
        if (!classname.equals("org.apache.axis.client.Service")) {
            // This is a generated derived class.  Don't bother with
            // all the Service instance variables.
            addr = new StringRefAddr(
                    ServiceFactory.SERVICE_CLASSNAME, classname);
            reference.add(addr);
        }
        else {
            if (wsdlLocation != null) {
                addr = new StringRefAddr(
                        ServiceFactory.WSDL_LOCATION, wsdlLocation.toString());
                reference.add(addr);
            }
            QName serviceName = getServiceName();
            if (serviceName != null) {
                addr = new StringRefAddr(ServiceFactory.SERVICE_NAMESPACE,
                        serviceName.getNamespaceURI());
                reference.add(addr);
                addr = new StringRefAddr(ServiceFactory.SERVICE_LOCAL_PART,
                        serviceName.getLocalPart());
                reference.add(addr);
            }
        }
        if (maintainSession) {
            addr = new StringRefAddr(ServiceFactory.MAINTAIN_SESSION, "true");
            reference.add(addr);
        }
        return reference;
    }

    /**
     * Sets this Service's AxisEngine.  This engine will be shared by all
     * Call objects created from this Service object.
     *
     * Note: Not part of the JAX-RPC spec.
     *
     * @param engine  Sets this Service's AxisEngine to the passed in one
     */
    public void setEngine(AxisEngine engine) {
        this.engine = engine ;
    }

    /**
     * Returns the current AxisEngine used by this Service and all of the
     * Call objects created from this Service object.
     *
     * Note: Not part of the JAX-RPC spec.
     *
     * @return AxisEngine  the engine
     */
    public AxisEngine getEngine() {
        return( engine );
    }

    /**
     * Set this Service's engine configuration.
     *
     * Note that since all of the constructors create the AxisClient right
     * now, this is basically a no-op.  Putting it in now so that we can make
     * lazy engine instantiation work, and not have to duplicate every single
     * Service constructor with a EngineConfiguration argument.
     *
     * @param config the EngineConfiguration we want to use.
     */
    public void setEngineConfiguration(EngineConfiguration config) {
        this.config = config;
    }

    /**
     * Determine whether we'd like to track sessions or not.
     * This information is passed to all Call objects created
     * from this service.  Calling setMaintainSession will
     * only affect future instantiations of the Call object,
     * not those that already exist.
     *
     * Note: Not part of JAX-RPC specification.
     *
     * @param yesno true if session state is desired, false if not.
     */
    public void setMaintainSession(boolean yesno) {
        maintainSession = yesno;
    }

    /**
     * If true, this service wants to track sessions.
     */
    public boolean getMaintainSession() {
        return maintainSession;
    }

    public Call getCall() throws ServiceException {
        Call call = (Call) previousCall.get();
        return call;
    }

    /**
     * Tells whether or not we're caching WSDL
     */
    public boolean getCacheWSDL() {
      return cachingWSDL ;
    }

    /**
     * Allows users to turn caching of WSDL documents on or off.
     * Default is 'true' (on).
     */
    public void setCacheWSDL(boolean flag) {
      cachingWSDL = flag ;
    }
}
