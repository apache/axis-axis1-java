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

/**
 * Axis' JAXRPC Dynamic Invocation Interface implementation of the Service
 * interface.
 *
 * @author Doug Davis (dug@us.ibm.com)
 */

import java.net.URL ;
import java.lang.String ;
import java.util.Map ;
import java.util.Set ;
import java.util.List ;
import java.util.Iterator ;
import java.util.HashSet ;

import java.io.InputStream ;

import org.apache.axis.utils.XMLUtils ;

import org.apache.axis.rpc.JAXRPCException ;
import org.apache.axis.rpc.namespace.QName ;
import org.apache.axis.transport.http.HTTPConstants ;

import javax.wsdl.Definition ;
import javax.wsdl.Binding ;
import javax.wsdl.BindingOperation ;
import javax.wsdl.Message ;
import javax.wsdl.Operation ;
import javax.wsdl.Output ;
import javax.wsdl.Port ;
import javax.wsdl.PortType ;

import com.ibm.wsdl.xml.WSDLReader ;
import com.ibm.wsdl.extensions.soap.SOAPAddress ;
import com.ibm.wsdl.extensions.soap.SOAPOperation ;

public class Service implements org.apache.axis.rpc.Service {
    private URL                 wsdlLocation   = null ;
    private Definition          wsdlDefinition = null ;
    private javax.wsdl.Service  wsdlService    = null ;

    public Service() throws JAXRPCException { 
        this.wsdlLocation   = null ;
        this.wsdlDefinition = null ;
    }

    public Service(URL WSDLdoc, QName serviceName) throws JAXRPCException {
        try {
            org.w3c.dom.Document doc = XMLUtils.newDocument(WSDLdoc.toString());
            WSDLReader           reader = new WSDLReader();
            Definition           def    = reader.readWSDL( null, doc );

            this.wsdlLocation   = WSDLdoc ;
            this.wsdlDefinition = def ;

            // grrr!
            String           ns = serviceName.getNamespaceURI();
            String           lp = serviceName.getLocalPart();
            javax.wsdl.QName qn = new javax.wsdl.QName( ns, lp );
 
            this.wsdlService    = def.getService( qn );
            if ( this.wsdlService == null )
                throw new JAXRPCException( "Can't find service: " + 
                                           serviceName );
        }
        catch( Exception exp ) {
            throw new JAXRPCException( "Error processing WSDL document: " + 
                                       WSDLdoc + "\n" + exp.toString() );
        }
    }

    public Service(String wsdlLocation, QName serviceName) 
                           throws JAXRPCException {
        try {
            org.w3c.dom.Document doc = XMLUtils.newDocument(wsdlLocation);
            WSDLReader           reader = new WSDLReader();
            Definition           def    = reader.readWSDL( null, doc );

            this.wsdlLocation = new URL(wsdlLocation) ;
            this.wsdlDefinition = def ;

            // grrr!
            String           ns = serviceName.getNamespaceURI();
            String           lp = serviceName.getLocalPart();
            javax.wsdl.QName qn = new javax.wsdl.QName( ns, lp );
 
            this.wsdlService    = def.getService( qn );
            if ( this.wsdlService == null )
                throw new JAXRPCException( "Can't find service: " + 
                                           serviceName );
        }
        catch( java.net.MalformedURLException exp ) {
            throw new JAXRPCException( "Malformed WSDL URI: " + wsdlLocation +
                                       "\n" + exp.toString() );
        }
        catch( Exception exp ) {
            throw new JAXRPCException( "Error processing WSDL document: " + 
                                       wsdlLocation + "\n" + exp.toString() );
        }
    }

    public Service(InputStream wsdlInputStream, QName serviceName) 
                           throws JAXRPCException {
        try {
            org.w3c.dom.Document doc = XMLUtils.newDocument(wsdlInputStream);
            WSDLReader           reader = new WSDLReader();
            Definition           def    = reader.readWSDL( null, doc );

            this.wsdlLocation   = null ;
            this.wsdlDefinition = def ;

            // grrr!
            String           ns = serviceName.getNamespaceURI();
            String           lp = serviceName.getLocalPart();
            javax.wsdl.QName qn = new javax.wsdl.QName( ns, lp );
 
            this.wsdlService    = def.getService( qn );
            if ( this.wsdlService == null )
                throw new JAXRPCException( "Can't find service: " + 
                                           serviceName );
        }
        catch( Exception exp ) {
            throw new JAXRPCException( "Error processing WSDL document:\n" + 
                                       exp.toString() );
        }
    }

    public java.rmi.Remote getPort(QName portName, Class proxyInterface)
                           throws JAXRPCException {
        // Not implemented yet 
        return( null );
    }

    public org.apache.axis.rpc.Call createCall(QName portName) 
                            throws JAXRPCException {
        javax.wsdl.QName qn = new javax.wsdl.QName( portName.getNamespaceURI(),
                                                    portName.getLocalPart() );
        if ( wsdlDefinition == null )
            throw new JAXRPCException( "Missing WSDL document" );

        Port port = wsdlService.getPort( portName.getLocalPart() );
        if ( port == null )
            throw new JAXRPCException( "Can't find port: " + portName );

        PortType portType = wsdlDefinition.getPortType( qn );
        if ( portType == null )
            throw new JAXRPCException( "Can't find portType: " + portName );

        org.apache.axis.client.Call call = new org.apache.axis.client.Call();
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
                    throw new JAXRPCException("Can't set location URI: " + 
                                              exp.toString() );
                }
            }
        }

        return( call );
    }

    public org.apache.axis.rpc.Call createCall(QName portName, 
                                               String operationName)
                           throws JAXRPCException {
        javax.wsdl.QName qn = new javax.wsdl.QName( portName.getNamespaceURI(),
                                                    portName.getLocalPart() );
        if ( wsdlDefinition == null )
            throw new JAXRPCException( "Missing WSDL document" );

        Port port = wsdlService.getPort( portName.getLocalPart() );
        if ( port == null )
            throw new JAXRPCException( "Can't find port: " + portName );

        PortType  portType = wsdlDefinition.getPortType( qn );
        List operations = portType.getOperations();
        if ( operations == null )
            throw new JAXRPCException( "Can't find operation: " + 
                                       operationName + " - none defined" );
        Operation op = null ;
        for ( int i = 0 ; i < operations.size() ; i++, op=null ) {
            op = (Operation) operations.get( i );
            if ( operationName.equals( op.getName() ) ) break ;
        }
        if ( op == null )
            throw new JAXRPCException( "Can't find operation: " + 
                                       operationName );

        org.apache.axis.client.Call call = new org.apache.axis.client.Call();
        call.setPortTypeName( portName );
        call.setOperationName( operationName );

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
                    throw new JAXRPCException("Can't set location URI: " + 
                                              exp.toString() );
                }
            }
        }

        // Get the SOAPAction
        ////////////////////////////////////////////////////////////////////
        Binding          binding = port.getBinding();
        BindingOperation bop = binding.getBindingOperation(operationName,
                                                           null, null);
        list = bop.getExtensibilityElements();
        for ( int i = 0 ; list != null && i < list.size() ; i++ ) {
            Object obj = list.get(i);
            if ( obj instanceof SOAPOperation ) { 
                SOAPOperation sop    = (SOAPOperation) obj ;
                String        action = sop.getSoapActionURI();
                if ( action != null )
                    call.setProperty(HTTPConstants.MC_HTTP_SOAPACTION, action);
            }
        }

        // Get the parameters
        ////////////////////////////////////////////////////////////////////
        // to do - dug

        // Get the return type
        ////////////////////////////////////////////////////////////////////
        Output   output = op.getOutput();
        Message  messae = output.getMessage();

        return( call );
    }

    public org.apache.axis.rpc.Call createCall() throws JAXRPCException {
        return( new org.apache.axis.client.Call() );
    }

    public URL getWSDLDocumentLocation() {
        return wsdlLocation ;
    }

    public QName getServiceName() {
        // not implemented yet
        if ( wsdlService == null ) return( null );
        javax.wsdl.QName  qn = wsdlService.getQName();
        return( new QName( qn.getNamespaceURI(), qn.getLocalPart() ) );
    }

    public Iterator getPorts() {
        // not implemented yet
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

    public javax.naming.Reference getReference() {
        // not implementated yet
        return( null );
    }
}
