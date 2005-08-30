/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
