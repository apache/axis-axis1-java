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
import java.util.List ;
import java.util.Iterator ;

import org.apache.axis.utils.XMLUtils ;

import org.apache.axis.rpc.JAXRPCException ;
import org.apache.axis.rpc.namespace.QName ;

import javax.wsdl.Definition ;
import javax.wsdl.PortType ;
import javax.wsdl.Operation ;

import com.ibm.wsdl.xml.WSDLReader ;

public class Service implements org.apache.axis.rpc.Service {
    private URL         wsdlLocation   = null ;
    private Definition  wsdlDefinition = null ;

    public Service() throws JAXRPCException { 
        this.wsdlLocation   = null ;
        this.wsdlDefinition = null ;
    }

    public Service(URL WSDLdoc) throws JAXRPCException {
        try {
            org.w3c.dom.Document doc = XMLUtils.newDocument(WSDLdoc.toString());
            WSDLReader           reader = new WSDLReader();
            Definition           def    = reader.readWSDL( null, doc );

            this.wsdlLocation = WSDLdoc ;
            this.wsdlDefinition = def ;
        }
        catch( Exception exp ) {
            throw new JAXRPCException( "Error processing WSDL document: " + 
                                       WSDLdoc + "\n" + exp.toString() );
        }
    }

    public Service(String wsdlLocation) throws JAXRPCException {
        try {
            org.w3c.dom.Document doc = XMLUtils.newDocument(wsdlLocation);
            WSDLReader           reader = new WSDLReader();
            Definition           def    = reader.readWSDL( null, doc );

            this.wsdlLocation = new URL(wsdlLocation) ;
            this.wsdlDefinition = def ;
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
        PortType portType = wsdlDefinition.getPortType( qn );
        if ( portType == null )
            throw new JAXRPCException( "Can't find portType: " + portName );
        org.apache.axis.client.Call call = new org.apache.axis.client.Call();
        call.setPortTypeName( portName );
        return( call );
    }

    public org.apache.axis.rpc.Call createCall(QName portName, 
                                               String operationName)
                           throws JAXRPCException {
        javax.wsdl.QName qn = new javax.wsdl.QName( portName.getNamespaceURI(),
                                                    portName.getLocalPart() );
        if ( wsdlDefinition == null )
            throw new JAXRPCException( "Missing WSDL document" );
        PortType portType = wsdlDefinition.getPortType( qn );
        if ( portType == null )
            throw new JAXRPCException( "Can't find portType: " + portName );
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

        // set other fields from WSDL  - dug

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
        return( null );
    }

    public Iterator getPorts() {
        // not implemented yet
        return( null );
    }

    public javax.naming.Reference getReference() {
        // not implementated yet
        return( null );
    }
}
