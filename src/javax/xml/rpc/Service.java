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

import javax.xml.namespace.QName;
import javax.xml.rpc.encoding.TypeMappingRegistry;
import javax.xml.rpc.handler.HandlerRegistry;

/**
 * <code>Service</code> class acts as a factory of the following:
 * <ul>
 * <li>Dynamic proxy for the target service endpoint.
 * <li>Instance of the type <code>javax.xml.rpc.Call</code> for
 *     the dynamic invocation of a remote operation on the
 *     target service endpoint.
 * <li>Instance of a generated stub class
 * </ul>
 *
 * @version 1.0
 */
public interface Service {

    /**
     * The getPort method returns either an instance of a generated
     * stub implementation class or a dynamic proxy. A service client
     * uses this dynamic proxy to invoke operations on the target
     * service endpoint. The <code>serviceEndpointInterface</code>
     * specifies the service endpoint interface that is supported by
     * the created dynamic proxy or stub instance.
     *
     * @param portName Qualified name of the service endpoint in
     *              the WSDL service description
     * @param serviceEndpointInterface Service endpoint interface
     *              supported by the dynamic proxy or stub
     *              instance
     * @return java.rmi.Remote Stub instance or dynamic proxy that
     *              supports the specified service endpoint
     *              interface
     * @throws ServiceException This exception is thrown in the
     *              following cases:
     *              <ul>
     *              <li>If there is an error in creation of
     *                  the dynamic proxy or stub instance
     *              <li>If there is any missing WSDL metadata
     *                  as required by this method
     *              <li>Optionally, if an illegal
     *                  <code>serviceEndpointInterface</code>
     *                  or <code>portName</code> is specified
     *              </ul>
     */
    public java.rmi
        .Remote getPort(QName portName, Class serviceEndpointInterface)
            throws ServiceException;

    /**
     * The getPort method returns either an instance of a generated
     * stub implementation class or a dynamic proxy. The parameter
     * <code>serviceEndpointInterface</code> specifies the service
     * endpoint interface that is supported by the returned stub or
     * proxy. In the implementation of this method, the JAX-RPC
     * runtime system takes the responsibility of selecting a protocol
     * binding (and a port) and configuring the stub accordingly.
     * The returned <code>Stub</code> instance should not be
     * reconfigured by the client.
     *
     * @param serviceEndpointInterface Service endpoint interface
     * @return Stub instance or dynamic proxy that supports the
     *              specified service endpoint interface
     *
     * @throws ServiceException <ul>
     *              <li>If there is an error during creation
     *                  of stub instance or dynamic proxy
     *              <li>If there is any missing WSDL metadata
     *                  as required by this method
     *              <li>Optionally, if an illegal
     *                  <code>serviceEndpointInterface</code>
     *
     *                  is specified
     *              </ul>
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface)
        throws ServiceException;

    /**
     * Gets an array of preconfigured <code>Call</code> objects for
     * invoking operations on the specified port. There is one
     * <code>Call</code> object per operation that can be invoked
     * on the specified port. Each <code>Call</code> object is
     * pre-configured and does not need to be configured using
     * the setter methods on <code>Call</code> interface.
     *
     * <p>Each invocation of the <code>getCalls</code> method
     * returns a new array of preconfigured <code>Call</code>
     *
     * objects
     *
     * <p>This method requires the <code>Service</code> implementation
     * class to have access to the WSDL related metadata.
     *
     * @param portName Qualified name for the target service endpoint
     * @return Call[]  Array of pre-configured Call objects
     * @throws ServiceException If this Service class does not
     *              have access to the required WSDL metadata
     *              or if an illegal <code>portName</code> is
     *              specified.
     */
    public Call[] getCalls(QName portName) throws ServiceException;

    /**
     * Creates a <code>Call</code> instance.
     *
     * @param portName Qualified name for the target service endpoint
     * @return Call instance
     * @throws ServiceException If any error in the creation of
     *              the <code>Call</code> object
     */
    public Call createCall(QName portName) throws ServiceException;

    /**
     * Creates a <code>Call</code> instance.
     *
     * @param portName Qualified name for the target service
     *              endpoint
     * @param operationName Qualified Name of the operation for
     *              which this <code>Call</code> object is to
     *              be created.
     * @return Call instance
     * @throws ServiceException If any error in the creation of
     *              the <code>Call</code> object
     */
    public Call createCall(QName portName, QName operationName)
        throws ServiceException;

    /**
     * Creates a <code>Call</code> instance.
     *
     * @param portName Qualified name for the target service
     *              endpoint
     * @param operationName Name of the operation for which this
     *                  <code>Call</code> object is to be
     *                  created.
     * @return Call instance
     * @throws ServiceException If any error in the creation of
     *              the <code>Call</code> object
     */
    public Call createCall(QName portName, String operationName)
        throws ServiceException;

    /**
     * Creates a <code>Call</code> object not associated with
     * specific operation or target service endpoint. This
     * <code>Call</code> object needs to be configured using the
     * setter methods on the <code>Call</code> interface.
     *
     * @return  Call object
     * @throws ServiceException If any error in the creation of
     *              the <code>Call</code> object
     */
    public Call createCall() throws ServiceException;

    /**
     * Gets the name of this Service.
     *
     * @return Qualified name of this service
     */
    public QName getServiceName();

    /**
     * Returns an <code>Iterator</code> for the list of
     * <code>QName</code>s of service endpoints grouped by this
     * service
     *
     * @return Returns <code>java.util.Iterator</code> with elements
     *     of type <code>javax.xml.namespace.QName</code>
     * @throws ServiceException If this Service class does not
     *     have access to the required WSDL metadata
     */
    public java.util.Iterator getPorts() throws ServiceException;

    /**
     * Gets location of the WSDL document for this Service.
     *
     * @return URL for the location of the WSDL document for
     *     this service
     */
    public java.net.URL getWSDLDocumentLocation();

    /**
     * Gets the <code>TypeMappingRegistry</code> for this
     * <code>Service</code> object. The returned
     * <code>TypeMappingRegistry</code> instance is pre-configured
     * to support the standard type mapping between XML and Java
     * types types as required by the JAX-RPC specification.
     *
     * @return  The TypeMappingRegistry for this Service object.
     * @throws java.lang.UnsupportedOperationException if the <code>Service</code> class does not support
     *     the configuration of <code>TypeMappingRegistry</code>.
     */
    public TypeMappingRegistry getTypeMappingRegistry();

    /**
     * Returns the configured <code>HandlerRegistry</code> instance
     * for this <code>Service</code> instance.
     *
     * @return HandlerRegistry
     * @throws java.lang.UnsupportedOperationException - if the <code>Service</code> class does not support
     *     the configuration of a <code>HandlerRegistry</code>
     */
    public HandlerRegistry getHandlerRegistry();
}

