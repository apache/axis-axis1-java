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

import java.util.Map;

import javax.xml.rpc.namespace.QName;

/**
 * The javax.xml.rpc.Call interface provides support for dynamic
 * invocation of a service port. The javax.xml.rpc.Service interface
 * acts as a factory for the creation of Call instances.
 * 
 * Once a Call instance is created, various setter and getter 
 * methods may be used to configure this Call instance. The 
 * properties configured on a Call instance include the 
 * following:
 * <ul>
 * <li>Name of a specific operation and port type for the 
 *   target service port
 * <li>Encoding style specified as a namespace URI
 * <li>Endpoint address of the target service port
 * <li>Properties specific to the binding to an XML based 
 * protocol and transpor
 * <li>Name, type and mode (IN, INOUT, OUT) of the parameters
 * <li>Return type
 * </ul>
 *
 * @version 0.1
 */
public interface Call {

    /**
     * Is the caller required to provide the parameter and return type
     * specification?  If true, then addParameter and setReturnType MUST be
     * called to provide the meta data. If false, then addParameter and
     * setReturnType CANNOT be called because the Call object already has the
     * metadata and the user is not allowed to mess with it. These methods
     * throw JAXRPCException if this method returns false.
     */
    public boolean isParameterAndReturnSpecRequired();

    /**
     * Adds a parameter type and mode for a specific operation. Note that the
     * client code is not required to call any addParameter and setReturnType
     * methods before calling the invoke method. A Call implementation class
     * can determine the parameter types by using the Java reflection and
     * configured type mapping registry.
     *
     * @param paramName - Name of the parameter
     * @param paramType - XML datatype of the parameter
     * @param parameterMode - Mode of the parameter-whether PARAM_MODE_IN,
     *                        PARAM_MODE_OUT or PARAM_MODE_INOUT
     * @exception JAXRPCException - if isParameterAndReturnSpecRequired returns
     *                              false, then addParameter will throw
     *                              JAXRPCException.
     */
    public void addParameter(String paramName, QName paramType,
            ParameterMode parameterMode);

    /**
     * Given a parameter name, return the QName of its type. If the parameter
     * doesn't exist, this method returns null.
     *
     * @param paramName - Name of the parameter.
     */
    public QName getParameterTypeByName(String paramName);

    /**
     * Sets the return type for a specific operation.
     *
     * @param xmlType - QName of the data type of the return value
     * @exception JAXRPCException - if isParameterAndReturnSpecRequired returns
     * false, then setReturnType will throw JAXRPCException.
     */
    public void setReturnType(QName xmlType);

    /**
     * Get the QName of the return type.
     */
    public QName getReturnType();

    /**
     * Removes all specified parameters from this Call instance.
     * @exception JAXRPCException - if isParameterAndReturnSpecRequired returns
     * false, then removeAllParameters will throw JAXRPCException.
     */
    public void removeAllParameters();

    /**
     * Gets the name of the operation to be invoked using this Call instance.
     *
     * @return QName of the operation
     */
    public QName getOperationName();

    /**
     * Sets the name of the operation to be invoked using this Call instance.
     *
     * @param operationName - QName of the operation to be invoked using the
     *                        Call instance
     */
    public void setOperationName(QName operationName);

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
     * Sets the endpoint address of the target service port. This address must
     * correspond to the transport specified in the binding for this Call
     * instance.
     *
     * @param address - Endpoint address of the target service port; specified
     *                  as URI
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
     * <ul>
     * <li>http.auth.username: Username for the HTTP Basic Authentication
     * <li>http.auth.password: Password for the HTTP Basic Authentication
     * <li>security.auth.subject: JAAS Subject that carries client 
     * principal and its credentials
     * <li>encodingstyle.namespace.uri: Encoding style specified as a 
     * namespace URI
     * <li>soap.http.soapaction.use: Boolean property that indicates
     * whether or not SOAPAction is to be used
     * <li>soap.http.soapaction.uri: Indicates the SOAPAction URI if the 
     * "soap.http.soapaction.use" property is set to true
     * </ul>
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
     * Invokes a specific operation using a synchronous request-response
     * interaction mode. The invoke method takes as parameters the object
     * values corresponding to these defined parameter types. Implementation of
     * the invoke method must check whether the passed parameter values
     * correspond to the number, order and types of parameters specified in the
     * corresponding operation specification.
     *
     * @param operationName - Name of the operation to invoke
     * @param params  - Parameters for this invocation
     *
     * @return the value returned from the other end. 
     *
     * @throws java.rmi.RemoteException - if there is any error in the remote
     *                                    method invocation or if the Call 
     * object is not configured properly.
     */
    public Object invoke(QName operationName, Object[] params)
            throws java.rmi.RemoteException;

    /**
     * Invokes a specific operation using a synchronous request-response
     * interaction mode. The invoke method takes as parameters the object
     * values corresponding to these defined parameter types. Implementation of
     * the invoke method must check whether the passed parameter values
     * correspond to the number, order and types of parameters specified in the
     * corresponding operation specification.
     *
     * @param params  - Parameters for this invocation
     *
     * @return the value returned from the other end. 
     *
     * @throws java.rmi.RemoteException - if there is any error in the remote
     *                                    method invocation or if the Call 
     *                                    object is not configured properly.
     */
    public Object invoke(Object[] params) throws java.rmi.RemoteException;

    /**
     * Invokes a remote method using the one-way interaction mode. The client
     * thread does not block waiting for the completion of the server
     * processing for this remote method invocation. This method must not
     * throw any remote exceptions. This method may throw a JAXRPCException
     * during the processing of the one-way remote call.
     *
     * @param params - Parameters for this invocation
     *
     * @throws javax.xml.rpc.JAXRPCException - if there is an error in the
     *   configuration of the Call object (example: a non-void return type has
     *   been incorrectly specified for the one-way call) or if there is any
     *   error during the invocation of the one-way remote call
     */
    public void invokeOneWay(Object[] params);

    /**
     * This method returns a java.util.Map of {name, value} for the
     * PARAM_MODE_OUT and PARAM_MODE_INOUT parameters for the last invoked
     * operation. If there are no output parameters, this method returns an
     * empty map. The parameter names in the returned Map are of type String.
     * The type of a value depends on the mapping between the Java and XML
     * types.
     *
     * @throws javax.xml.rpc.JAXRPCException - if this method is invoked on a
     *                                         one-way operation or if it is
     *                                         invoked before any invoke method
     *                                         has been called.
     */
    public Map getOutputParams();
}

