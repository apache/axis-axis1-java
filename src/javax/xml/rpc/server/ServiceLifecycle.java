/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
package javax.xml.rpc.server;

import javax.xml.rpc.ServiceException;

/**
 * The <code>javax.xml.rpc.server.ServiceLifecycle</code> defines a lifecycle interface for a
 * JAX-RPC service endpoint. If the service endpoint class implements the
 * <code>ServiceLifeycle</code>  interface, the servlet container based JAX-RPC runtime system
 * is required to manage the lifecycle of the corresponding service endpoint objects.
 *
 * @version 1.0
 */
public interface ServiceLifecycle {

    /**
     * Used for initialization of a service endpoint. After a service
     * endpoint instance (an instance of a service endpoint class) is
     * instantiated, the JAX-RPC runtime system invokes the
     * <code>init</code> method. The service endpoint class uses the
     * <code>init</code> method to initialize its configuration
     * and setup access to any external resources. The context parameter
     * in the <code>init</code> method enables the endpoint instance to
     * access the endpoint context provided by the underlying JAX-RPC
     * runtime system.
     * <p>
     * The init method implementation should typecast the context
     * parameter to an appropriate Java type. For service endpoints
     * deployed on a servlet container based JAX-RPC runtime system,
     * the <code>context</code> parameter is of the Java type
     * <code>javax.xml.rpc.server.ServletEndpointContext</code>. The
     * <code>ServletEndpointContext</code> provides an endpoint context
     * maintained by the underlying servlet container based JAX-RPC
     * runtime system
     * <p>
     * @param context Endpoint context for a JAX-RPC service endpoint
     * @throws ServiceException  If any error in initialization of the service endpoint; or if any
     * illegal context has been provided in the init method
     */
    public abstract void init(Object context) throws ServiceException;

    /**
     * JAX-RPC runtime system ends the lifecycle of a service endpoint instance by
     * invoking the destroy method. The service endpoint releases its resources in
     * the implementation of the destroy method.
     */
    public abstract void destroy();
}
