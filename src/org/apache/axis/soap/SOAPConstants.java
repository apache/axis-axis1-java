/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
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
package org.apache.axis.soap;

import javax.xml.namespace.QName;
import java.io.Serializable;

/**
 * An interface definining SOAP constants.  This allows various parts of the
 * engine to avoid hardcoding dependence on a particular SOAP version and its
 * associated URIs, etc.
 *
 * This might be fleshed out later to encapsulate factories for behavioral
 * objects which act differently depending on the SOAP version, but for now
 * it just supplies common namespaces + QNames.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 * @author Andras Avar (andras.avar@nokia.com)
 */
public interface SOAPConstants extends Serializable {
    /** SOAP 1.1 constants - thread-safe and shared */
    public SOAP11Constants SOAP11_CONSTANTS = new SOAP11Constants();
    /** SOAP 1.2 constants - thread-safe and shared */
    public SOAP12Constants SOAP12_CONSTANTS = new SOAP12Constants();

    /**
     * Obtain the envelope namespace for this version of SOAP
     */
    public String getEnvelopeURI();

    /**
     * Obtain the encoding namespace for this version of SOAP
     */
    public String getEncodingURI();

    /**
     * Obtain the QName for the Fault element
     */
    public QName getFaultQName();

    /**
     * Obtain the QName for the Header element
     */
    public QName getHeaderQName();

    /**
     * Obtain the QName for the Body element
     */
    public QName getBodyQName();

    /**
     * Obtain the QName for the role attribute (actor/role)
     */
    public QName getRoleAttributeQName();

    /**
     * Obtain the MIME content type
     */
    public String getContentType();
    
    /**
     * Obtain the "next" role/actor URI
     */ 
    public String getNextRoleURI();

    /**
     * Obtain the href attribute name
     */
    public String getAttrHref();

    /**
     * Obtain the item type name of an array
     */
    public String getAttrItemType();

    /**
     * Obtain the Qname of VersionMismatch fault code
     */
    public QName getVerMismatchFaultCodeQName();

}
