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

import java.util.List;
import java.util.Map;

/**
 * The <code>javax.xml.rpc.handler.HandlerChain</code> represents an
 * ordered list of handlers. All elements in the HandlerChain are of
 * the type javax.xml.rpc.handler.HandlerInfo.
 * <p>An implementation class for the HandlerChain interface abstracts
 * the policy and mechanism for the invocation of the registered
 * handlers.
 */
public interface HandlerChain extends List {

    /**
     * The handleRequest method initiates the request processing for this
     * handler chain.
     * @param context - MessageContext parameter provides access to the request
     *                  SOAP message.
     *
     * @throws JAXRPCException - if any processing error happens
     */
    public boolean handleRequest(MessageContext context);

    /**
     * The handleResponse method initiates the response processing for this
     * handler chain.
     *
     * @param context - MessageContext parameter provides access to the response
     *                  SOAP message.
     *
     * @throws JAXRPCException - if any processing error happens
     */
    public boolean handleResponse(MessageContext context);

    /**
     * The handleFault method initiates the SOAP fault processing 
     * for this handler chain.
     *
     * @param  context - MessageContext parameter provides access to the SOAP
     *         message.
     *
     * @returns Returns true if all handlers in chain have been processed. 
     *          Returns false  if a handler in the chain returned 
     *          false from its handleFault method.
     * 
     * @throws JAXRPCException - if any processing error happens
     */
    public boolean handleFault(MessageContext context);

    /**
     * Initializes the configuration for a HandlerChain.
     *
     * @param config - Configuration for the initialization of this handler
     *                 chain
     *
     * @throws JAXRPCException - If any error during initialization
     */
    public void init(Map config);

    /**
     * Indicates the end of lifecycle for a HandlerChain.
     *
     * @throws JAXRPCException - If any error during destroy
     */
    public void destroy();

    /**
     * Sets SOAP Actor roles for this HandlerChain. This specifies the set of
     * roles in which this HandlerChain is to act for the SOAP message
     * processing at this SOAP node. These roles assumed by a HandlerChain must
     * be invariant during the processing of an individual SOAP message.
     * <p>
     * A HandlerChain always acts in the role of the special SOAP actor next.
     * Refer to the SOAP specification for the URI name for this special SOAP
     * actor. There is no need to set this special role using this method.
     *
     * @param soapActorNames - URIs for SOAP actor name
     */
    public void setRoles(String[] soapActorNames);

    /**
     * Gets SOAP actor roles registered for this HandlerChain at this SOAP node.
     * The returned array includes the special SOAP actor next.
     */
    public java.lang.String[] getRoles();
}
