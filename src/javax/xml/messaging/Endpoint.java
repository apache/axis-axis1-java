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
package javax.xml.messaging;

/**
 * An opaque representation of an application endpoint. Typically, an
 * <code>Endpoint</code> object represents a business entity, but it
 * may represent a party of any sort. Conceptually, an
 * <code>Endpoint</code> object is the mapping of a logical name
 * (example, a URI) to a physical location, such as a URL.
 * <P>
 * For messaging using a provider that supports profiles, an application
 * does not need to specify an endpoint when it sends a message because
 * destination information will be contained in the profile-specific header.
 * However, for point-to-point plain SOAP messaging, an application must supply
 * an <code>Endpoint</code> object to
 * the <code>SOAPConnection</code> method <code>call</code>
 * to indicate the intended destination for the message.
 * The subclass {@link URLEndpoint URLEndpoint} can be used when an application
 * wants to send a message directly to a remote party without using a
 * messaging provider.
 * <P>
 * The default identification for an <code>Endpoint</code> object
 * is a URI. This defines what JAXM messaging
 * providers need to support at minimum for identification of
 * destinations. A messaging provider
 * needs to be configured using a deployment-specific mechanism with
 * mappings from an endpoint to the physical details of that endpoint.
 * <P>
 * <code>Endpoint</code> objects can be created using the constructor, or
 * they can be looked up in a naming
 * service. The latter is more flexible because logical identifiers
 * or even other naming schemes (such as DUNS numbers)
 * can be bound and rebound to specific URIs.
 */
public class Endpoint {

    /**
     * Constructs an <code>Endpoint</code> object using the given string identifier.
     * @param  uri  a string that identifies the party that this <code>Endpoint</code> object represents; the default is a URI
     */
    public Endpoint(String uri) {
        id = uri;
    }

    /**
     * Retrieves a string representation of this <code>Endpoint</code> object. This string is likely to be provider-specific, and
     * programmers are discouraged from parsing and programmatically interpreting the contents of this string.
     * @return  a <code>String</code> with a provider-specific representation of this <code>Endpoint</code> object
     */
    public String toString() {
        return id;
    }

    /** A string that identifies the party that this <code>Endpoint</code> object represents; a URI is the default. */
    protected String id;
}
