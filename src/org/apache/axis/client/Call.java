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

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.encoding.ServiceDescription;
import org.apache.axis.rpc.encoding.XMLType;
import org.apache.axis.rpc.namespace.QName;

/**
 * Axis' JAXRPC Dynamic Invocation Interface implementation of the Call
 * interface.  This class should be used to actually invoke the Web Service.
 * It can be prefilled by a WSDL document (on the constructor to the Service
 * object) or you can fill in the data yourself.
 *
 * @author Doug Davis (dug@us.ibm.com)
 */

public class Call implements org.apache.axis.rpc.Call {
    private QName              portTypeName  = null ;
    private ServiceClient      client        = null ;
    private ServiceDescription serviceDesc   = null ;
    private String             operationName = null ;

    /**
     * Default constructor - not much else to say.
     */
    public Call() {
        client = new ServiceClient();
        serviceDesc = new ServiceDescription(null, true);
    }

    /**
     * Returns the encoding style as a URI that should be used for the SOAP
     * message.
     *
     * @return String URI of the encoding style to use
     */
    public String getEncodingStyle() {
        return( serviceDesc.getEncodingStyleURI() );
    }

    /**
     * Sets the encoding style to the URL passed in.
     *
     * @param namespaceURI URI of the encoding to use.
     */
    public void setEncodingStyle(String namespaceURI) {
        serviceDesc.setEncodingStyleURI( namespaceURI );
    }

    /**
     * Adds the specified parameter to the list of parameters for the
     * operation associated with this Call object.
     *
     * @param paramName      Name that will be used for the parameter in the XML
     * @param paramType      XMLType of the parameter
     * @param parameterMode  one of PARAM_MODE_IN, PARAM_MODE_OUT
     *                       or PARAM_MODE_INOUT
     */
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

    /**
     * Sets the return type of the operation associated with this Call object.
     *
     * @param type XMLType of the return value.
     */
    public void setReturnType(XMLType type) {
        QName qn = type.getType();
        serviceDesc.setOutputType(
            new org.apache.axis.utils.QName(qn.getNamespaceURI(),
                                            qn.getLocalPart()));
    }

    /**
     * Clears the list of parameters.
     */
    public void removeAllParameters() {
        serviceDesc.removeAllParams();
    }

    /**
     * Returns the operation name associated with this Call object.
     *
     * @return String Name of the operation or null if not set.
     */
    public String getOperationName() {
        return( operationName );
    }

    /**
     * Sets the operation name associated with this Call object.  This will
     * not check the WSDL (if there is WSDL) to make sure that it's a valid
     * operation name.
     *
     * @param opName Name of the operation.
     */
    public void setOperationName(String opName) {
        operationName = opName ;
    }

    /**
     * Returns the fully qualified name of the port for this Call object
     * (if there is one).
     *
     * @return QName Fully qualified name of the port (or null if not set)
     */
    public QName getPortTypeName() {
        return( portTypeName );
    }

    /**
     * Sets the port type of this Call object.  This call will not set
     * any additional fields, nor will it do any checking to verify that
     * this port type is actually defined in the WSDL - for now anyway.
     *
     * @param portType Fully qualified name of the portType
     */
    public void setPortTypeName(QName portType) {
        portTypeName = portType ;
    }

    /**
     * Sets the URL of the target Web Service.
     *
     * @param address URL of the target Web Service
     */
    public void setTargetEndpointAddress(java.net.URL address) {
        try {
            client.setURL( address.toString() );
        }
        catch( org.apache.axis.AxisFault exp ) {
            // do what?
        }
    }

    /**
     * Returns the URL of the target Web Service.
     *
     * @return URL URL of the target Web Service
     */
    public java.net.URL getTargetEndpointAddress() {
        try {
            return( new java.net.URL(client.getURL()) );
        }
        catch( Exception exp ) {
            return( null );
        }
    }

    /**
     * Allows you to set a named property to the passed in value.
     * This will just be stored in a Hashtable - it's then up to
     * one of the Handler (or the Axis engine itself) to go looking for
     * one of them.
     *
     * @param name  Name of the property
     * @param value Value of the property
     */
    public void setProperty(String name, Object value) {
        client.set( name, value );
    }

    /**
     * Returns the value associated with the named property - or null if not
     * defined/set.
     *
     * @return Object value of the property - or null
     */
    public Object getProperty(String name) {
        return( client.get( name ) );
    }

    /**
     * Removes (if set) the named property.
     *
     * @param name name of the property to remove
     */
    public void removeProperty(String name) {
        client.remove( name );
    }

    /**
     * Invokes the operation associated with this Call object using the
     * passed in parameters as the arguments to the method.
     *
     * @param  params Array of parameters to invoke the Web Service with
     * @return Object Return value of the operation/method - or null
     * @throws RemoteException if there's an error
     */
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

    /**
     * Invokes the operation associated with this Call object using the passed
     * in parameters as the arguments to the method.  This will return
     * immediately rather than waiting for the server to complete its
     * processing.
     *
     * NOTE: the return immediately part isn't implemented yet
     *
     * @param  params Array of parameters to invoke the Web Service with
     * @throws JAXRPCException is there's an error
     */
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
