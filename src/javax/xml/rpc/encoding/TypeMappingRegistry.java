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
package javax.xml.rpc.encoding;

/**
 * The interface <code>javax.xml.rpc.encoding.TypeMappingRegistry</code>
 * defines a registry of TypeMapping instances for various encoding
 * styles.
 *
 * @version 1.0
 */
public interface TypeMappingRegistry extends java.io.Serializable {

    /**
     * Registers a <code>TypeMapping</code> instance with the
     * <code>TypeMappingRegistry</code>. This method replaces any
     * existing registered <code>TypeMapping</code> instance for
     * the specified <code>encodingStyleURI</code>.
     *
     * @param encodingStyleURI An encoding style specified as an URI.
     *             An example is "http://schemas.xmlsoap.org/soap/encoding/"
     * @param mapping TypeMapping instance
     *
     * @return Previous TypeMapping associated with the specified
     *     <code>encodingStyleURI</code>, or <code>null</code>
     *     if there was no TypeMapping associated with the specified
     *     <code>encodingStyleURI</code>
     *
     * @throws JAXRPCException If there is an error in the
     *              registration of the <code>TypeMapping</code> for
     *              the specified <code>encodingStyleURI</code>.
     */
    public TypeMapping register(String encodingStyleURI, TypeMapping mapping);

    /**
     * Registers the <code>TypeMapping</code> instance that is default
     * for all encoding styles supported by the
     * <code>TypeMappingRegistry</code>. A default <code>TypeMapping</code>
     * should include serializers and deserializers that are independent
     * of and usable with any encoding style. Successive invocations
     * of the <code>registerDefault</code> method replace any existing
     * default <code>TypeMapping</code> instance.
     * <p>
     * If the default <code>TypeMapping</code> is registered, any
     * other TypeMapping instances registered through the
     * <code>TypeMappingRegistry.register</code> method (for a set
     * of encodingStyle URIs) override the default <code>TypeMapping</code>.
     *
     * @param mapping TypeMapping instance
     *
     * @throws JAXRPCException If there is an error in the
     *             registration of the default <code>TypeMapping</code>
     */
    public void registerDefault(TypeMapping mapping);

    /**
     * Gets the registered default <code>TypeMapping</code> instance.
     * This method returns <code>null</code> if there is no registered
     * default TypeMapping in the registry.
     *
     * @return The registered default <code>TypeMapping</code> instance
     *     or <code>null</code>
     */
    public TypeMapping getDefaultTypeMapping();

    /**
     * Returns a list of registered encodingStyle URIs in this
     * <code>TypeMappingRegistry</code> instance.
     *
     * @return Array of the registered encodingStyle URIs
     */
    public String[] getRegisteredEncodingStyleURIs();

    /**
     * Returns the registered <code>TypeMapping</code> for the specified
     * encodingStyle URI. If there is no registered <code>TypeMapping</code>
     * for the specified <code>encodingStyleURI</code>, this method
     * returns <code>null</code>.
     *
     * @param encodingStyleURI Encoding style specified as an URI
     * @return TypeMapping for the specified encodingStyleURI or
     *     <code>null</code>
     */
    public TypeMapping getTypeMapping(String encodingStyleURI);

    /**
     * Creates a new empty <code>TypeMapping</code> object.
     *
     * @return TypeMapping instance.
     */
    public TypeMapping createTypeMapping();

    /**
     * Unregisters a TypeMapping instance, if present, from the specified
     * encodingStyleURI.
     *
     * @param encodingStyleURI Encoding style specified as an URI
     * @return <code>TypeMapping</code> instance that has been unregistered
     *     or <code>null</code> if there was no TypeMapping
     *     registered for the specified <code>encodingStyleURI</code>
     */
    public TypeMapping unregisterTypeMapping(String encodingStyleURI);

    /**
     * Removes a <code>TypeMapping</code> from the TypeMappingRegistry. A
     * <code>TypeMapping</code> is associated with 1 or more
     * encodingStyleURIs. This method unregisters the specified
     * <code>TypeMapping</code> instance from all associated
     * <code>encodingStyleURIs</code> and then removes this
     * TypeMapping instance from the registry.
     *
     * @param mapping TypeMapping to remove
     * @return <code>true</code> if specified <code>TypeMapping</code>
     *     is removed from the TypeMappingRegistry; <code>false</code>
     *     if the specified <code>TypeMapping</code> was not in the
     *     <code>TypeMappingRegistry</code>
     */
    public boolean removeTypeMapping(TypeMapping mapping);

    /**
     * Removes all registered TypeMappings and encodingStyleURIs
     * from this TypeMappingRegistry.
     */
    public void clear();
}

