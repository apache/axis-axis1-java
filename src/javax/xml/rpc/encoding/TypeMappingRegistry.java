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

package javax.xml.rpc.encoding;

import javax.xml.rpc.JAXRPCException;
import java.util.Iterator;

/**
 * The interface javax.xml.rpc.encoding.TypeMappingRegistry 
 * defines a registry for TypeMapping instances for 
 * the different encoding styles. 
 *
 * @version 0.7
 */
public interface TypeMappingRegistry extends java.io.Serializable {

    /**
     * Registers a TypeMapping instance with the TypeMappingRegistry. This
     * method replaces any existing registered TypeMapping instance for the
     * specified namespaceURI.
     *
     * @param namespaceURI - An encoding style or XML schema namespace specified
     *                       as an URI. An example is
     *                       "http://schemas.xmlsoap.org/soap/encoding/"
     * @param mapping - TypeMapping instance
     *
     * @return Previous TypeMapping associated with the specified namespaceURI,
     * or null if there was no TypeMapping associated with the specified namespaceURI
     *
     * @throws JAXRPCException - If there is any error in the registration
     * of the TypeMapping for the specified namespace URI
     */
    public TypeMapping register(String namespace, TypeMapping mapping);


    /**
     * Registers the TypeMapping instance that is default for all encoding
     * styles and XML schema namespaces supported by the TypeMappingRegistry. A
     * default TypeMapping should include serializers and deserializers that are
     * independent of and usable with any encoding style or XML namespaces. The
     * successive invocations of the registerDefault method replace any existing
     * default TypeMapping instance.
     * <p>
     * If the default TypeMapping is registered, any other TypeMapping instances
     * registered through the TypeMappingRegistry.register method (for a set of
     * namespace URIs) override the default TypeMapping.
     *
     * @param mapping - TypeMapping instance
     *
     * @throws JAXRPCException - If there is any error in the registration
     * of the TypeMapping for the specified namespace URI
     */
    public void registerDefault(TypeMapping mapping);

    /**
     * Return the registered default TypeMapping instance
     * @return TypeMapping or null
     **/
    public TypeMapping getDefaultTypeMapping();

    /**
     * Returns the registered TypeMapping for the specified namespace URI. If
     * there is no registered TypeMapping for the specified namespaceURI, this
     * method returns null.
     *
     * @param namespaceURI - Encoding style or XML schema namespace specified
     *                       as an URI
     * @return TypeMapping for the specified namespace URI or null
     */
    public TypeMapping getTypeMapping(String namespaceURI);

    /**
     * Unregisters a TypeMapping instance, if present, from the specified
     * namespaceURI.
     *
     * @param namespaceURI - Encoding style or XML schema namespace specified
     *                       as an URI
     * @return The registered TypeMapping or null.
     */
    public TypeMapping unregisterTypeMapping(String namespaceURI);

    /**
     * Removes a TypeMapping from the TypeMappingRegistry. A TypeMapping is
     * associated with 1 or more namespaceURIs. This method unregisters the
     * specified TypeMapping instance from all associated namespaceURIs and then
     * removes this TypeMapping instance from the registry.
     *
     * @param mapping - TypeMapping to remove
     * @return true if specified TypeMapping is removed from the TypeMappingRegistry;
     *         false if the specified TypeMapping was not in the TypeMappingRegistry
     */
    public boolean removeTypeMapping(TypeMapping mapping);


    /**
     * Creates a new empty TypeMapping object.
     *
     * @return TypeMapping instance.
     */
    public TypeMapping createTypeMapping();

    /**
     * Gets a list of namespace URIs registered with this TypeMappingRegistry.
     *
     * @return String[] containing names of all registered namespace URIs
     */
    public String[] getRegisteredEncodingStyleURIs();


    /**
     * Removes all TypeMappings and namespaceURIs from this TypeMappingRegistry.
     */
    public void clear();

}


