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
 * @version 0.6
 */
public interface TypeMappingRegistry extends java.io.Serializable {

    /**
     * The method register adds a TypeMapping instance for a specific 
     * namespace                        
     *
     * @param mapping - TypeMapping for specific type namespaces
     * @param namespaceURIs
     *
     * @throws JAXRPCException - If there is any error in the registration
     * of the TypeMapping for the specified namespace URI
     * java.lang.IllegalArgumentException - if an invalid namespace URI is specified
     */
    public void register(TypeMapping mapping, String[] namespaceURIs)
        throws JAXRPCException;

    /**
     * The method register adds a default TypeMapping instance.  If a specific
     * TypeMapping is not found, the default TypeMapping is used.  
     *
     * @param mapping - TypeMapping for specific type namespaces
     *
     * @throws JAXRPCException - If there is any error in the registration
     * of the TypeMapping for the specified namespace URI
     * java.lang.IllegalArgumentException - if an invalid namespace URI is specified
     */
    public void registerDefault(TypeMapping mapping)
        throws JAXRPCException;

    /**
     * Gets the TypeMapping namespace.  If not found, the default TypeMapping 
     * is returned.
     *
     * @param namespaceURI - The namespace URI
     * @return The registered TypeMapping (which may be the default TypeMapping) or null.
     */
    public TypeMapping getTypeMapping(String namespaceURI);

    /**
     * Removes the TypeMapping for the namespace.
     *
     * @param namespaceURI - The namespace URI
     * @return The registered TypeMapping or null.
     */
    public TypeMapping removeTypeMapping(String namespaceURI);


    /**
     * Creates a new empty TypeMapping object for the specified encoding style or XML schema namespace.
     *
     * @return An empty generic TypeMapping object
     */
    public TypeMapping createTypeMapping();

    /**
     * Gets a list of namespace URIs registered with this TypeMappingRegistry.
     *
     * @return String[] containing names of all registered namespace URIs
     */
    public String[] getSupportedNamespaces();


    /**
     * Removes all registered TypeMappings from the registery                   
     */
    public void clear(String namespaceURI);

    /**
     * Return the default TypeMapping
     * @return TypeMapping or null
     **/
    public javax.xml.rpc.encoding.TypeMapping getDefaultTypeMapping();

}


