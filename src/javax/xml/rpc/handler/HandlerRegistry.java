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

package javax.xml.rpc.handler;

import java.io.Serializable;

import javax.xml.namespace.QName;

/**
 * The <code>javax.xml.rpc.handler.HandlerRegistry</code>
 * provides support for the programmatic configuration of
 * handlers in a <code>HandlerRegistry</code>.
 * <p>
 * A handler chain is registered per service endpoint, as
 * indicated by the qualified name of a port. The getHandlerChain
 * returns the handler chain (as a java.util.List) for the
 * specified service endpoint. The returned handler chain is
 * configured using the java.util.List interface. Each element
 * in this list is required to be of the Java type
 * <code>javax.xml.rpc.handler.HandlerInfo</code>
 *
 * @version 1.0
 */
public interface HandlerRegistry extends Serializable {

    /**
     * Gets the handler chain for the specified service endpoint.
     * The returned <code>List</code> is used to configure this
     * specific handler chain in this <code>HandlerRegistry</code>.
     * Each element in this list is required to be of the Java type
     * <code>javax.xml.rpc.handler.HandlerInfo</code>.
     *
     * @param   portName Qualified name of the target service
     * @return  HandlerChain java.util.List Handler chain
     * @throws java.lang.IllegalArgumentException If an invalid <code>portName</code> is specified
     */
    public java.util.List getHandlerChain(QName portName);

    /**
     * Sets the handler chain for the specified service endpoint
     * as a <code>java.util.List</code>. Each element in this list
     * is required to be of the Java type
     * <code>javax.xml.rpc.handler.HandlerInfo</code>.
     *
     *  @param   portName Qualified name of the target service endpoint
     *  @param   chain A List representing configuration for the
     *             handler chain
     *  @throws  JAXRPCException If any error in the configuration of
     *             the handler chain
     *  @throws java.lang.UnsupportedOperationException If this
     *     set operation is not supported. This is done to
     *     avoid any overriding of a pre-configured handler
     *     chain.
     *  @throws java.lang.IllegalArgumentException If an invalid
     *     <code>portName</code> is specified
     */
    public abstract void setHandlerChain(
            QName portName, java.util.List chain);
}
