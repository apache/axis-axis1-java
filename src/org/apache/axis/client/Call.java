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
 * Axis' JAXRPC Dynamic Invocation Interface implementation of the Call
 * interface.
 *
 * @author Doug Davis (dug@us.ibm.com)
 */

import java.lang.String ;

import org.apache.axis.AxisFault ;
import org.apache.axis.Constants ;
import org.apache.axis.client.ServiceClient ;
import org.apache.axis.encoding.ServiceDescription ;

import org.apache.axis.rpc.namespace.QName ;
import org.apache.axis.rpc.encoding.XMLType ;


public class Call implements org.apache.axis.rpc.Call {
    private QName              portTypeName  = null ;
    private ServiceClient      client        = null ;
    private ServiceDescription serviceDesc   = null ;
    private String             operationName = null ;

    public Call() {
        client = new ServiceClient();
        serviceDesc = new ServiceDescription(null, true);
    }

    public String getEncodingStyle() {
        return( serviceDesc.getEncodingStyleURI() );
    }

    public void setEncodingStyle(String namespaceURI) {
        serviceDesc.setEncodingStyleURI( namespaceURI );
    }

    public void addParameter(String paramName, XMLType paramType,
                             int parameterMode) {

        QName qn = paramType.getType();

        switch( parameterMode ) {
            case PARAM_MODE_IN: 
                     serviceDesc.addInputParam( paramName,
                         new org.apache.axis.utils.QName(qn.getNamespaceURI(),
                                                         qn.getLocalPart() ) );
                     break ;

            case PARAM_MODE_OUT:
                     serviceDesc.addOutputParam( paramName,
                         new org.apache.axis.utils.QName(qn.getNamespaceURI(),
                                                         qn.getLocalPart() ) );
                     break ;

            case PARAM_MODE_INOUT:
            default:                // Unsupported - but can't throw anything!
                      throw new RuntimeException( "Unsupport parameter type" );
        }
    }

    public void setReturnType(XMLType type) {
        QName qn = type.getType();
        serviceDesc.setOutputType(
            new org.apache.axis.utils.QName(qn.getNamespaceURI(),
                                            qn.getLocalPart()));
    }

    public void removeAllParameters() {
        serviceDesc.removeAllParams();
    }

    public String getOperationName() {
        return( operationName );
    }

    public void setOperationName(String opName) {
        operationName = opName ;
    }

    public QName getPortTypeName() {
        return( portTypeName );
    }

    public void setPortTypeName(QName portType) {
        portTypeName = portType ;
    }

    public void setTargetEndpointAddress(java.net.URL address) {
        try {
            client.setURL( address.toString() );
        }
        catch( org.apache.axis.AxisFault exp ) {
            // do what?
        }
    }

    public java.net.URL getTargetEndpointAddress() {
        try {
            return( new java.net.URL(client.getURL()) );
        }
        catch( Exception exp ) {
            return( null );
        }
    }

    public void setProperty(String name, Object value) {
        client.set( name, value );
    }

    public Object getProperty(String name) {
        return( client.get( name ) );
    }

    public void removeProperty(String name) {
        client.remove( name );
    }

    // Remote Method Invocation methods
    public Object invoke(Object[] params)
                           throws java.rmi.RemoteException {
        if ( operationName == null )
            throw new java.rmi.RemoteException( "No operation name specified" );
        try {
            String ns = (String) client.get( Constants.NAMESPACE );
            if ( ns == null )
                return( client.invoke( operationName, params ) );
            else
                return( client.invoke( ns, operationName, params ) );
        }
        catch( AxisFault exp ) {
            throw new java.rmi.RemoteException( "Error invoking operation",
                                                exp );
        }
    }

    public void invokeOneWay(Object[] params)
                           throws org.apache.axis.rpc.JAXRPCException {
        try {
            invoke( params );
        }
        catch( java.rmi.RemoteException exp ) {
            throw new org.apache.axis.rpc.JAXRPCException( exp.toString() );
        }
    }
}
