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

package org.apache.axis.encoding;

import org.apache.axis.Constants;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.utils.JavaUtils;

import javax.xml.rpc.namespace.QName;
import javax.xml.rpc.JAXRPCException;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

/**
 * @author James Snell (jasnell@us.ibm.com)
 * @author Sam Ruby (rubys@us.ibm.com)
 * Re-written for JAX-RPC Compliance by
 * @author Rich Scheuerle (scheu@us.ibm.com
 */
public class TypeMappingRegistryImpl implements TypeMappingRegistry { 
    
    private HashMap mapTM;          // Type Mappings keyed by the Web Service Namespace URI
    private TypeMapping defaultTM;  // Default Type Mapping 
    
    /**
     * Construct TypeMappingRegistry
     */
    public TypeMappingRegistryImpl() {
        mapTM = new HashMap();
        defaultTM = DefaultTypeMappingImpl.create();
    }
    
    /********* JAX-RPC Compliant Method Definitions *****************/
    
    /**
     * The method register adds a TypeMapping instance for a specific 
     * namespace                 
     *
     * @param mapping - TypeMapping for specific namespaces
     * @param namespaceURIs 
     *
     * @throws JAXRPCException - If there is any error in the registration
     * of the TypeMapping for the specified namespace URI
     * java.lang.IllegalArgumentException - if an invalid namespace URI is specified
     */
    public void register(javax.xml.rpc.encoding.TypeMapping mapping, String[] namespaceURIs)
        throws JAXRPCException {
        if (mapping == null || 
            !(mapping instanceof TypeMapping)) {
            throw new IllegalArgumentException();
        } 
        for (int i = 0; i < namespaceURIs.length; i++) {
            if (namespaceURIs[i] == null) {
                throw new java.lang.IllegalArgumentException();
            }
            mapTM.put(namespaceURIs[i], mapping);
        }            
    }
    
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
    public void registerDefault(javax.xml.rpc.encoding.TypeMapping mapping) {
        if (mapping == null || 
            !(mapping instanceof TypeMapping)) {
            throw new IllegalArgumentException();
        }
        defaultTM = (TypeMapping) mapping;
    }
        
    /**
     * Gets the TypeMapping for the namespace.  If not found, the default TypeMapping 
     * is returned.
     *
     * @param namespaceURI - The namespace URI of a Web Service
     * @return The registered TypeMapping (which may be the default TypeMapping) or null.
     */
    public javax.xml.rpc.encoding.TypeMapping getTypeMapping(String namespaceURI) {
        TypeMapping tm = (TypeMapping) mapTM.get(namespaceURI);
        if (tm == null) {
            tm = defaultTM;
        }
        return tm;
    }

    /**
     * Removes the TypeMapping for the namespace.
     *
     * @param namespaceURI - The namespace URI
     * @return The registered TypeMapping .
     */
    public javax.xml.rpc.encoding.TypeMapping removeTypeMapping(String namespaceURI) {
        TypeMapping tm = (TypeMapping) mapTM.remove(namespaceURI);
        return tm;
    }


    /**
     * Creates a new empty TypeMapping object for the specified encoding style or XML schema namespace.
     *
     * @return An empty generic TypeMapping object
     */
    public javax.xml.rpc.encoding.TypeMapping createTypeMapping() {
        return new TypeMappingImpl(this);
    }
        

    /**
     * Gets a list of namespace URIs registered with this TypeMappingRegistry.
     *
     * @return String[] containing names of all registered namespace URIs
     */
    public String[] getSupportedNamespaces() {
        return (String[]) mapTM.keySet().toArray();
    }


    /**
     * Removes all registered TypeMappings from the registery                   
     */
    public void clear(String namespaceURI) {
        mapTM.clear();
    }

     /********* End JAX-RPC Compliant Method Definitions *****************/

    /**
     * Return the default TypeMapping
     * @return TypeMapping or null
     **/
    public javax.xml.rpc.encoding.TypeMapping getDefaultTypeMapping() {
        return defaultTM;
    }


    // This code is no longer valid.  If this information should
    // be serialized, the WSDD should do it.  Not the TypeMappingRegistry.
    /* 
    public void dumpToElement(Element root)
    {
        if ((d == null) || (parent == null)) {
            return;
        }

        Document doc = root.getOwnerDocument();
        
        Enumeration enum = d.keys();
        while (enum.hasMoreElements()) {
            QName typeQName = (QName)enum.nextElement();
            DeserializerDescriptor desc = 
                                   (DeserializerDescriptor)d.get(typeQName);
            if (desc.cls == null)
                continue;
            
            Element mapEl = doc.createElementNS("", "typeMapping");

            mapEl.setAttribute("type", "ns:" + typeQName.getLocalPart());
            mapEl.setAttribute("xmlns:ns", typeQName.getNamespaceURI());
            
            mapEl.setAttribute("classname", desc.cls.getName());
            
            String dser = desc.factory.getClass().getName();
            mapEl.setAttribute("deserializerFactory", dser);
            
            SerializerDescriptor serDesc =
                                      (SerializerDescriptor)s.get(desc.cls);
            if (serDesc != null) {
                mapEl.setAttribute("serializer", serDesc.serializer.
                                                      getClass().getName());
            }

            root.appendChild(mapEl);
        }
    }
    */
}
