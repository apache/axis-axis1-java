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
import org.apache.axis.Constants;
import org.apache.axis.ConfigurationProvider;
import org.apache.axis.AxisFault;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.XMLUtils;
import org.apache.axis.utils.AxisClassLoader;
import org.w3c.dom.Document;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.encoding.TypeMappingRegistry;
import javax.xml.rpc.namespace.QName;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
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

public class Service implements javax.xml.rpc.Service {
    private AxisEngine          engine          = null;

    private URL                 wsdlLocation    = null ;
    private Definition          wsdlDefinition  = null ;
    private javax.wsdl.Service  wsdlService     = null ;
    private boolean             maintainSession = false ;

    private ConfigurationProvider configProvider =
            new FileProvider(Constants.CLIENT_CONFIG_FILE);

    Definition getWSDLDefinition() {
        return( wsdlDefinition );
    }

    javax.wsdl.Service getWSDLService() {
        return( wsdlService );
    }

    protected AxisClient getAxisClient()
    {
        return new AxisClient(configProvider);
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
     * the ConfigurationProvider which should be used to set up the
     * AxisClient.
     */
    public Service(ConfigurationProvider configProvider) {
        this.configProvider = configProvider;
        engine = getAxisClient();
    }

    /**
     * Constructs a new Service object for the service in the WSDL document
     * pointed to by the wsdlDoc URL and serviceName parameters.
     *
     * @param wsdlDoc          URL of the WSDL document
     * @param serviceName      Qualified name of the desired service
     * @throws JAXRPCExceptionIif there's an error finding or parsing the WSDL
     */
    public Service(URL wsdlDoc, QName serviceName) throws JAXRPCException {
        engine = getAxisClient();
        Document doc = XMLUtils.newDocument(wsdlDoc.toString());
        initService(doc, serviceName);
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
     * @throws JAXRPCException If there's an error finding or parsing the WSDL
     */
    public Service(String wsdlLocation, QName serviceName)
                           throws JAXRPCException {
        engine = getAxisClient();
        try {
            // Start by reading in the WSDL using WSDL4J
            FileInputStream      fis = new FileInputStream(wsdlLocation);
            Document doc = XMLUtils.newDocument(fis);
            initService(doc, serviceName);
        }
        catch( FileNotFoundException exp ) {
            throw new JAXRPCException(
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
     * @throws JAXRPCException If there's an error finding or parsing the WSDL
     */
    public Service(InputStream wsdlInputStream, QName serviceName)
                           throws JAXRPCException {
        engine = getAxisClient();
        Document doc = XMLUtils.newDocument(wsdlInputStream);
        initService(doc, serviceName);
    }

    /**
     * Common code for building up the Service from a WSDL document
     *
     * @param doc               A DOM document containing WSDL
     * @param serviceName       Qualified name of the desired service
     * @throws JAXRPCException  If there's an error finding or parsing the WSDL
     */
    private void initService(Document doc, QName serviceName)
            throws JAXRPCException {
        try {
            // Start by reading in the WSDL using WSDL4J
            WSDLReader           reader = WSDLFactory.newInstance()
                                                     .newWSDLReader();
            reader.setVerbose( false );
            Definition           def    = reader.readWSDL( null, doc );

            this.wsdlLocation   = null ;
            this.wsdlDefinition = def ;

            // grrr!  Too many flavors of QName
            String           ns = serviceName.getNamespaceURI();
            String           lp = serviceName.getLocalPart();
            javax.wsdl.QName qn = new javax.wsdl.QName( ns, lp );

            this.wsdlService    = def.getService( qn );
            if ( this.wsdlService == null )
                throw new JAXRPCException(
                        JavaUtils.getMessage("noService00", "" + serviceName));
        }
        catch( Exception exp ) {
            throw new JAXRPCException(
                    JavaUtils.getMessage("wsdlError00", "" + "", "\n" + exp) );
        }
    }

    /**
     * Not implemented yet
     *
     * @param  portName        ...
     * @param  proxyInterface  ...
     * @return java.rmi.Remote ...
     * @throws JAXRPCException If there's an error
     */
    public java.rmi.Remote getPort(QName portName, Class proxyInterface)
                           throws JAXRPCException {
        return( null );
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
     * @throws JAXRPCException
     */
    public java.rmi.Remote getPort(String endpoint, Class proxyInterface)
        throws JAXRPCException
    {
        if (!proxyInterface.isInterface()) {
            throw new JAXRPCException(JavaUtils.getMessage("mustBeIface00"));
        }

        if (!(java.rmi.Remote.class.isAssignableFrom(proxyInterface))) {
            throw new JAXRPCException(
                            JavaUtils.getMessage("mustExtendRemote00"));
        }

        try {
            Call call = new Call(endpoint);
            ClassLoader classLoader = AxisClassLoader.getClassLoader();
            return (java.rmi.Remote)Proxy.newProxyInstance(classLoader,
                                                new Class[] { proxyInterface },
                                                new AxisClientProxy(call));
        } catch (Exception e) {
            throw new JAXRPCException(e.toString());
        }
    }

    /**
     * Creates a new Call object - will prefill as much info from the WSDL
     * as it can.  Right now it's just the target URL of the Web Service.
     *
     * @param  portName        PortName in the WSDL doc to search for
     * @return Call            Used for invoking the Web Service
     * @throws JAXRPCException If there's an error
     */
    public javax.xml.rpc.Call createCall(QName portName)
                            throws JAXRPCException {
        javax.wsdl.QName qn = new javax.wsdl.QName( portName.getNamespaceURI(),
                                                    portName.getLocalPart() );
        if ( wsdlDefinition == null )
            throw new JAXRPCException( JavaUtils.getMessage("wsdlMissing00") );

        Port port = wsdlService.getPort( portName.getLocalPart() );
        if ( port == null )
            throw new JAXRPCException( JavaUtils.getMessage("noPort00", "" + portName) );

        Binding   binding  = port.getBinding();
        PortType  portType = binding.getPortType();
        if ( portType == null )
            throw new JAXRPCException( JavaUtils.getMessage("noPortType00", "" + portName) );

        org.apache.axis.client.Call call = new org.apache.axis.client.Call(this);
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
                    throw new JAXRPCException(
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
     * @throws JAXRPCException If there's an error
     */
    public javax.xml.rpc.Call createCall(QName portName,
                                         String operationName)
                           throws JAXRPCException {

        org.apache.axis.client.Call call=new org.apache.axis.client.Call(this);
        call.setOperation( portName, operationName );
        return( call );
    }

    /**
     * Creates a new Call object with no prefilled data.  This assumes
     * that the caller will set everything manually - no checking of
     * any kind will be done against the WSDL.
     *
     * @return Call            Used for invoking the Web Service
     * @throws JAXRPCException If there's an error
     */
    public javax.xml.rpc.Call createCall() throws JAXRPCException {
        return( new org.apache.axis.client.Call(this) );
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
        Map       map  = wsdlService.getPorts();

        if ( map == null ) return( null );

        Set       set   = map.keySet();
        Iterator  iter  = set.iterator();
        Set       ports = null ;

        while ( iter.hasNext() ) {
            String name = (String) iter.next();
            Port   port = (Port) map.get( name );
            if ( ports == null ) ports = new HashSet();
            ports.add( port );
        }
        if ( ports == null ) return( null );
        return( ports.iterator() );
    }

    /**
     * Defines the current Type Mappig Registry.
     *
     * @param  registry The TypeMappingRegistry
     * @throws JAXRPCException if there's an error
     */
    public void setTypeMappingRegistry(TypeMappingRegistry registry)
                    throws JAXRPCException  {
    }

    /**
     * Returns the current TypeMappingRegistry or null.
     *
     * @return TypeMappingRegistry The registry
     */
    public TypeMappingRegistry getTypeMappingRegistry() {
        return( null );
    }

    /**
     * Not implemented yet
     *
     * @return Reference ...
     */
    public javax.naming.Reference getReference() {
        return( null );
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
     * Set this Service's configuration provider.
     *
     * Note that since all of the constructors create the AxisClient right
     * now, this is basically a no-op.  Putting it in now so that we can make
     * lazy engine instantiation work, and not have to duplicate every single
     * Service constructor with a ConfigurationProvider argument.
     *
     * @param configProvider the ConfigurationProvider we want to use.
     */
    public void setConfigProvider(ConfigurationProvider configProvider) {
        this.configProvider = configProvider;
    }

    /**
     * Determine whether we'd like to track sessions or not.
     *
     * This just passes through the value into the MessageContext.
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
}
