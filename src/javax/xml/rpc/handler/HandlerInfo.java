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
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The <code>javax.xml.rpc.handler.HandlerInfo</code> represents
 * information about a handler in the HandlerChain. A HandlerInfo
 * instance is passed in the <code>Handler.init</code> method to
 * initialize a <code>Handler</code> instance.
 *
 * @version 1.0
 * @see HandlerChain
 */
public class HandlerInfo implements Serializable {

    /** Default constructor */
    public HandlerInfo() {
        handlerClass = null;
        config       = new HashMap();
    }

    /**
     *  Constructor for HandlerInfo
     *  <p>
     *  @param  handlerClass Java Class for the Handler
     *  @param  config Handler Configuration as a java.util.Map
     *  @param  headers QNames for the header blocks processed
     *          by this Handler.  QName is the qualified name
     *          of the outermost element of a header block
     */
    public HandlerInfo(Class handlerClass, Map config, QName[] headers) {

        this.handlerClass = handlerClass;
        this.config       = config;
        this.headers      = headers;
    }

    /**
     *  Sets the Handler class
     *  @param  handlerClass Class for the Handler
     */
    public void setHandlerClass(Class handlerClass) {
        this.handlerClass = handlerClass;
    }

    /**
     *  Gets the Handler class
     *  @return Returns null if no Handler class has been
     *    set; otherwise the set handler class
     */
    public Class getHandlerClass() {
        return handlerClass;
    }

    /**
     *  Sets the Handler configuration as <code>java.util.Map</code>
     *  @param  config Configuration map
     */
    public void setHandlerConfig(Map config) {
        this.config = config;
    }

    /**
     *  Gets the Handler configuration
     *  @return  Returns empty Map if no configuration map
     *     has been set; otherwise returns the set configuration map
     */
    public Map getHandlerConfig() {
        return config;
    }

    /**
     * Sets the header blocks processed by this Handler.
     * @param headers QNames of the header blocks. QName
     *            is the qualified name of the outermost
     *            element of the SOAP header block
     */
    public void setHeaders(QName[] headers) {
        this.headers = headers;
    }

    /**
     * Gets the header blocks processed by this Handler.
     * @return Array of QNames for the header blocks. Returns
     *      <code>null</code> if no header blocks have been
     *      set using the <code>setHeaders</code> method.
     */
    public QName[] getHeaders() {
        return headers;
    }

    /** Handler Class */
    private Class handlerClass;

    /** Configuration Map */
    private Map config;

    /** headers */
    private QName[] headers;
}

