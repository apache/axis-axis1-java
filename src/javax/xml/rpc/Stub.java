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
package javax.xml.rpc;

import java.util.Iterator;

/**
 * The interface <code>javax.xml.rpc.Stub</code> is the common base interface
 * for the stub classes. All generated stub classes are required to
 * implement the <code>javax.xml.rpc.Stub</code> interface. An instance
 * of a stub class represents a client side proxy or stub instance for
 * the target service endpoint.
 *
 * <p>The <code>javax.xml.rpc.Stub</code> interface provides an
 * extensible property mechanism for the dynamic configuration of
 * a stub instance.
 *
 * @version 1.0
 */
public interface Stub {

    // Constants for the standard properties

    /**
     * Standard property: User name for authentication.
     * <p>Type: java.lang.String
     */
    public static final String USERNAME_PROPERTY = Call.USERNAME_PROPERTY;

    /**
     * Standard property: Password for authentication.
     * <p>Type: java.lang.String
     */
    public static final String PASSWORD_PROPERTY = Call.PASSWORD_PROPERTY;

    /**
     * Standard property: Target service endpoint address. The
     * URI scheme for the endpoint address specification must
     * correspond to the protocol/transport binding for this
     * stub class.
     * <p>Type: java.lang.String
     */
    public static final String ENDPOINT_ADDRESS_PROPERTY =
        "javax.xml.rpc.service.endpoint.address";

    /**
     * Standard property: This boolean property is used by a service
     * client to indicate whether or not it wants to participate in
     * a session with a service endpoint. If this property is set to
     * true, the service client indicates that it wants the session
     * to be maintained. If set to false, the session is not maintained.
     * The default value for this property is false.
     * <p>Type: java.lang.Boolean
     */
    public static final String SESSION_MAINTAIN_PROPERTY =
        Call.SESSION_MAINTAIN_PROPERTY;

    /**
     * Sets the name and value of a configuration property
     * for this Stub instance. If the Stub instances contains
     * a value of the same property, the old value is replaced.
     * <p>Note that the <code>_setProperty</code> method may not
     * perform validity check on a configured property value. An
     * example is the standard property for the target service
     * endpoint address that is not checked for validity in the
     * <code>_setProperty</code> method.
     * In this case, stub configuration errors are detected at
     * the remote method invocation.
     *
     * @param name Name of the configuration property
     * @param value Value of the property
     * @throws JAXRPCException <ul>
     *     <li>If an optional standard property name is
     *         specified, however this Stub implementation
     *         class does not support the configuration of
     *         this property.
     *     <li>If an invalid or unsupported property name is
     *         specified or if a value of mismatched property
     *         type is passed.
     *     <li>If there is any error in the configuration of
     *         a valid property.
     *     </ul>
     */
    public void _setProperty(String name, Object value);

    /**
     * Gets the value of a specific configuration property.
     *
     * @param name Name of the property whose value is to be
     *          retrieved
     * @return Value of the configuration property
     * @throws JAXRPCException if an invalid or
     *     unsupported property name is passed.
     */
    public Object _getProperty(String name);

    /**
     * Returns an <code>Iterator</code> view of the names of the properties
     * that can be configured on this stub instance.
     *
     * @return Iterator for the property names of the type
     *     <code>java.lang.String</code>
     */
    public Iterator _getPropertyNames();
}    // interface Stub

