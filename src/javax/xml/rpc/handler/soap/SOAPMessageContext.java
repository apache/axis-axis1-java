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
package javax.xml.rpc.handler.soap;

import javax.xml.rpc.handler.MessageContext;
import javax.xml.soap.SOAPMessage;

/**
 * The interface <code>javax.xml.rpc.soap.SOAPMessageContext</code>
 * provides access to the SOAP message for either RPC request or
 * response. The <code>javax.xml.soap.SOAPMessage</code> specifies
 * the standard Java API for the representation of a SOAP 1.1 message
 * with attachments.
 *
 * @version 1.0
 * @see javax.xml.soap.SOAPMessage
 */
public interface SOAPMessageContext extends MessageContext {

    /**
     *  Gets the SOAPMessage from this message context
     *  @return Returns the SOAPMessage; returns null if no request
     *          SOAPMessage is present in this SOAPMessageContext
     */
    public abstract SOAPMessage getMessage();

    /**
     *  Sets the SOAPMessage for this message context
     *  @param   message  SOAP message
     *  @throws  JAXRPCException  If any error during the setting
     *     of the SOAPMessage in this message context
     *  @throws java.lang.UnsupportedOperationException If this
     *     operation is not supported
     */
    public abstract void setMessage(SOAPMessage message);

    /**
     * Gets the SOAP actor roles associated with an execution
     * of the HandlerChain and its contained Handler instances.
     * Note that SOAP actor roles apply to the SOAP node and
     * are managed using <code>HandlerChain.setRoles</code> and
     * <code>HandlerChain.getRoles</code>. Handler instances in
     * the HandlerChain use this information about the SOAP actor
     * roles to process the SOAP header blocks. Note that the
     * SOAP actor roles are invariant during the processing of
     * SOAP message through the HandlerChain.
     *
     * @return Array of URIs for SOAP actor roles
     * @see javax.xml.rpc.handler.HandlerChain#setRoles(java.lang.String[]) HandlerChain.setRoles(java.lang.String[])
     * @see javax.xml.rpc.handler.HandlerChain#getRoles() HandlerChain.getRoles()
     */
    public abstract String[] getRoles();
}

