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
package javax.xml.rpc;

import javax.xml.rpc.encoding.XMLType;
import javax.xml.rpc.namespace.QName;

/**
 * The javax.xml.rpc.Call interface provides support for dynamic
 * invocation of a service port. The javax.xml.rpc.Service interface
 * acts as a factory for the creation of Call instances.
 * 
 *   Once a Call instance is created, various setter and getter 
 * methods may be used to configure this Call instance. The 
 * properties configured on a Call instance include the 
 * following:
 * <UL>
 * <LI>Name of a specific operation and port type for the 
 *   target service port
 * <LI>Encoding style specified as a namespace URI
 * <LI>Endpoint address of the target service port
 * <LI>Properties specific to the binding to an XML based 
 * protocol and transpor
 * <LI>Name, type and mode (IN, INOUT, OUT) of the parameters
 * <LI>Return type
 * </UL>
 *
 * @version 0.1
 */
public interface Call {

    /** Field PARAM_MODE_IN           */
    static public final int PARAM_MODE_IN = 1;

    /** Field PARAM_MODE_OUT           */
    static public final int PARAM_MODE_OUT = 2;

    /** Field PARAM_MODE_INOUT           */
    static public final int PARAM_MODE_INOUT = 3;

    /**
     * Method getEncodingStyle
     *
     * @return namespace URI of the Encoding Style
     */
    public String getEncodingStyle();

    /**
     * Method setEncodingStyle
     *
     * @param namespaceURI
     */
    public void setEncodingStyle(String namespaceURI);

    /**
     * Adds a parameter type and mode for a specific operation. Note that the client code is not required to call any 
     * addParameter and setReturnType methods before calling the invoke method. A Call implementation class can 
     * determine the parameter types by using the Java reflection and configured type mapping registry.
     *
     * @param paramName - Name of the parameter
     * @param paramType - XML datatype of the parameter
     * @param parameterMode - Mode of the parameter-whether PARAM_MODE_IN, PARAM_MODE_OUT or PARAM_MODE_INOUT
     */
    public void addParameter(String paramName, XMLType paramType,
                             int parameterMode);

    /**
     * Sets the return type for a specific operation.
     *
     * @param type - XML data type of the return value
     */
    public void setReturnType(XMLType type);

    /**
     * Removes all specified parameters from this Call instance
     */
    public void removeAllParameters();

    /**
     * Gets the name of the operation to be invoked using this Call instance.
     *
     * @return Name of the operation
     */
    public String getOperationName();

    /**
     * Sets the name of the operation to be invoked using this Call instance.
     *
     * @param operationName - Name of the operation to be invoked using the Call instance
     */
    public void setOperationName(String operationName);

    /**
     * Gets the qualified name of the port type.
     *
     * @return Qualified name of the port type
     */
    public QName getPortTypeName();

    /**
     * Sets the qualified name of the port type.
     *
     * @param portType - Qualified name of the port type
     */
    public void setPortTypeName(QName portType);

    /**
     * Sets the endpoint address of the target service port. This address must correspond to the transport specified 
     * in the binding for this Call instance.
     *
     * @param address - Endpoint address of the target service port; specified as URI
     */
    public void setTargetEndpointAddress(java.net.URL address);

    /**
     * Gets the endpoint address of a target service port.
     *
     * @return Endpoint address of the target service port as an URI
     */
    public java.net.URL getTargetEndpointAddress();

    /**
     * Sets the value for a named property. JAX-RPC 1.0 specification 
     * specifies a standard set of properties that may be passed 
     * to the Call.setProperty method. The properties include:
     * <UL>
     * <LI>http.auth.username: Username for the HTTP Basic Authentication
     * <LI>http.auth.password: Password for the HTTP Basic Authentication
     * <LI>security.auth.subject: JAAS Subject that carries client 
     * principal and its credentials
     * <LI>encodingstyle.namespace.uri: Encoding style specified as a 
     * namespace URI
     * <LI>soap.http.soapaction.use: Boolean property that indicates
     * whether or not SOAPAction is to be used
     * <LI>soap.http.soapaction.uri: Indicates the SOAPAction URI if the 
     * "soap.http.soapaction.use" property is set to true
     * </UL>
     * 
     * @param name - Name of the property
     * @param value - Value of the property
     */
    public void setProperty(String name, Object value);

    /**
     * Gets the value of a named property.
     *
     * @param name - Name of the property
     *
     * @return Value of the named property
     */
    public Object getProperty(String name);

    /**
     * Removes a named property.
     *
     * @param name - Name of the property
     */
    public void removeProperty(String name);

    // Remote Method Invocation methods
    /**
     * Invokes a specific operation using a synchronous request-response interaction mode. The invoke method takes 
     * as parameters the object values corresponding to these defined parameter types. Implementation of the invoke 
     * method must check whether the passed parameter values correspond to the number, order and types of parameters 
     * specified in the corresponding operation specification.
     *
     * @param params  - Parameters for this invocation
     *
     * @return the value returned from the other end. 
     *
     * @throws java.rmi.RemoteException - if there is any error in the remote method invocation or if the Call 
     * object is not configured properly.
     */
    public Object invoke(Object[] params) throws java.rmi.RemoteException;

    /**
     * Invokes a remote method using the one-way interaction mode. The client thread does not block waiting for the 
     * completion of the server processing for this remote method invocation. This method must not throw any remote 
     * exceptions. This method may throw a JAXRPCException during the processing of the one-way remote call.
     *
     * @param params - Parameters for this invocation
     *
     * @throws javax.xml.rpc.JAXRPCException - if there is an error in the configuration of the Call object (example: 
     * a non-void return type has been incorrectly specified for the one-way call) or if there is any error during 
     * the invocation of the one-way remote call
     */
    public void invokeOneWay(Object[] params)
        throws javax.xml.rpc.JAXRPCException;
}


