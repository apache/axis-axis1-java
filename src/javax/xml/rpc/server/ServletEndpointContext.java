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
package javax.xml.rpc.server;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.xml.rpc.handler.MessageContext;
import java.security.Principal;

/**
 * The <code>ServletEndpointContext</code> provides an endpoint
 * context maintained by the underlying servlet container based
 * JAX-RPC runtime system. For service endpoints deployed on a
 * servlet container based JAX-RPC runtime system, the context
 * parameter in the <code>ServiceLifecycle.init</code> method is
 * required to be of the Java type
 * <code>javax.xml.rpc.server.ServletEndpointContext</code>.
 * <p>
 * A servlet container based JAX-RPC runtime system implements
 * the <code>ServletEndpointContext</code> interface. The JAX-RPC
 * runtime system is required to provide appropriate session,
 * message context, servlet context and user principal information
 * per method invocation on the endpoint class.
 *
 * @version 1.0
 */
public interface ServletEndpointContext {

    /**
     * The method <code>getMessageContext</code> returns the
     * <code>MessageContext</code> targeted for this endpoint instance.
     * This enables the service endpoint instance to acccess the
     * <code>MessageContext</code> propagated by request
     * <code>HandlerChain</code> (and its contained <code>Handler</code>
     * instances) to the target endpoint instance and to share any
     * SOAP message processing related context. The endpoint instance
     * can access and manipulate the <code>MessageContext</code>
     * and share the SOAP message processing related context with
     * the response <code>HandlerChain</code>.
     *
     * @return MessageContext; If there is no associated
     *     <code>MessageContext</code>, this method returns
     *     <code>null</code>.
     * @throws java.lang.IllegalStateException if this method is invoked outside a
     * remote method implementation by a service endpoint instance.
     */
    public MessageContext getMessageContext();

    /**
     * Returns a <code>java.security.Principal</code> instance that
     * contains the name of the authenticated user for the current
     * method invocation on the endpoint instance. This method returns
     * <code>null</code> if there is no associated principal yet.
     * The underlying JAX-RPC runtime system takes the responsibility
     * of providing the appropriate authenticated principal for a
     * remote method invocation on the service endpoint instance.
     *
     * @return A <code>java.security.Principal</code> for the
     * authenticated principal associated with the current
     * invocation on the servlet endpoint instance;
     * Returns <code>null</code> if there no authenticated
     * user associated with a method invocation.
     */
    public Principal getUserPrincipal();

    /**
     * The <code>getHttpSession</code> method returns the current
     * HTTP session (as a <code>javax.servlet.http.HTTPSession</code>).
     * When invoked by the service endpoint within a remote method
     * implementation, the <code>getHttpSession</code> returns the
     * HTTP session associated currently with this method invocation.
     * This method returns <code>null</code> if there is no HTTP
     * session currently active and associated with this service
     * endpoint. An endpoint class should not rely on an active
     * HTTP session being always there; the underlying JAX-RPC
     * runtime system is responsible for managing whether or not
     * there is an active HTTP session.
     * <p>
     * The getHttpSession method throws <code>JAXRPCException</code>
     * if invoked by an non HTTP bound endpoint.
     *
     * @return The HTTP session associated with the current
     * invocation or <code>null</code> if there is no active session.
     * @throws JAXRPCException - If this method invoked by a non-HTTP bound
     *         endpoints.
     */
    public HttpSession getHttpSession();

    /**
     * The method <code>getServletContext</code> returns the
     * <code>ServletContex</code>t associated with the web
     * application that contain this endpoint. According to
     * the Servlet specification, There is one context per web
     * application (installed as a WAR) per JVM . A servlet
     * based service endpoint is deployed as part of a web
     * application.
     * @return <code>ServletContext</code>
     */
    public ServletContext getServletContext();
}
