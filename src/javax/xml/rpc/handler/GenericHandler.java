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

import javax.xml.namespace.QName;

/**
 * The <code>javax.xml.rpc.handler.GenericHandler</code> class
 * implements the <code>Handler</code> interface. SOAP Message
 * Handler developers should typically subclass
 * <code>GenericHandler</code> class unless the Handler class
 * needs another class as a superclass.
 *
 * <p>
 * The <code>GenericHandler</code> class is a convenience abstract
 * class that makes writing Handlers easy. This class provides
 * default implementations of the lifecycle methods <code>init</code>
 * and <code>destroy</code> and also different handle methods.
 * A Handler developer should only override methods that it needs
 * to specialize as part of the derived <code>Handler</code>
 * implementation class.
 *
 * @version 1.0
 */
public abstract class GenericHandler implements Handler {

    /**
     * Default constructor
     */
    protected GenericHandler() {}

    /**
     * The <code>handleRequest</code> method processes the request
     * SOAP message. The default implementation of this method returns
     * <code>true</code>. This indicates that the handler chain
     * should continue processing of the request SOAP message.
     * This method should be overridden if the derived Handler class
     * needs to specialize implementation of this method.
     *
     * @param context the message context
     * @return true/false
     */
    public boolean handleRequest(MessageContext context) {
        return true;
    }

    /**
     * The <code>handleResponse</code> method processes the response
     * message. The default implementation of this method returns
     * <code>true</code>. This indicates that the handler chain
     * should continue processing of the response SOAP message.
     * This method should be overridden if the derived Handler class
     * needs to specialize implementation of this method.
     *
     * @param context the message context
     * @return true/false
     */
    public boolean handleResponse(MessageContext context) {
        return true;
    }

    /**
     * The <code>handleFault</code> method processes the SOAP faults
     * based on the SOAP message processing model. The default
     * implementation of this method returns <code>true</code>. This
     * indicates that the handler chain should continue processing
     * of the SOAP fault. This method should be overridden if
     * the derived Handler class needs to specialize implementation
     * of this method.
     *
     * @param context the message context
     * @return true/false
     */
    public boolean handleFault(MessageContext context) {
        return true;
    }

    /**
     * The <code>init</code> method to enable the Handler instance to
     * initialize itself. This method should be overridden if
     * the derived Handler class needs to specialize implementation
     * of this method.
     *
     * @param config handler configuration
     */
    public void init(HandlerInfo config) {}

    /**
     * The <code>destroy</code> method indicates the end of lifecycle
     * for a Handler instance. This method should be overridden if
     * the derived Handler class needs to specialize implementation
     * of this method.
     */
    public void destroy() {}

    /**
     * Gets the header blocks processed by this Handler instance.
     *
     * @return Array of QNames of header blocks processed by this handler instance.
     * <code>QName</code> is the qualified name of the outermost element of the Header block.
     */
    public abstract QName[] getHeaders();
}

