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
 *
 * The TypeMappingRegistry keeps track of the individual TypeMappings.
 * 
 * The TypeMappingRegistry for axis contains a default type mapping
 * that is set for either SOAP 1.1 or SOAP 1.2
 * The default type mapping is a singleton used for the entire
 * runtime and should not have anything new registered in it.
 *
 * Instead the new TypeMappings for the deploy and service are
 * made in a separate TypeMapping which is identified by
 * the soap encoding.  These new TypeMappings delegate back to 
 * the default type mapping when information is not found.
 *
 * So logically we have:
 *
 *         TMR
 *         | |  
 *         | +---------------> DefaultTM 
 *         |                      ^
 *         |                      |
 *         +----> TM --delegate---+
 *
 *
 * But in the implementation, the TMR references
 * "delegate" TypeMappings (TM') which then reference the actual TM's
 *
 * So the picture is really:
 *         TMR
 *         | |  
 *         | +-----------TM'------> DefaultTM 
 *         |              ^
 *         |              |
 *         +-TM'-> TM ----+
 *
 * This extra indirection is necessary because the user may want to 
 * change the default type mapping.  In such cases, the TMR
 * just needs to adjust the TM' for the DefaultTM, and all of the
 * other TMs will properly delegate to the new one.  Here's the picture:
 *
 *         TMR
 *         | |  
 *         | +-----------TM'--+     DefaultTM 
 *         |              ^   |
 *         |              |   +---> New User Defined Default TM
 *         +-TM'-> TM ----+
 *
 * The other reason that it is necessary is when a deploy
 * has a TMR, and then TMR's are defined for the individual services
 * in such cases the delegate() method is invoked on the service
 * to delegate to the deploy TMR
 *
 *       Deploy TMR
 *         | |  
 *         | +-----------TM'------> DefaultTM 
 *         |              ^
 *         |              |
 *         +-TM'-> TM ----+
 *
 *       Service TMR
 *         | |  
 *         | +-----------TM'------> DefaultTM 
 *         |              ^
 *         |              |
 *         +-TM'-> TM ----+
 *
 *    ServiceTMR.delegate(DeployTMR)
 *
 *       Deploy TMR
 *         | |  
 *         | +------------TM'------> DefaultTM 
 *         |              ^ ^ 
 *         |              | |
 *         +-TM'-> TM ----+ |
 *           ^              |
 *   +-------+              |
 *   |                      |
 *   |   Service TMR        |
 *   |     | |              |
 *   |     | +----------TM'-+               
 *   |     |              
 *   |     |              
 *   |     +-TM'-> TM +
 *   |                |
 *   +----------------+
 *
 * So now the service uses the DefaultTM of the Deploy TMR, and
 * the Service TM properly delegates to the deploy's TM.  And
 * if either the deploy defaultTM or TMs change, the links are not broken.
 */
public class TypeMappingRegistryImpl implements TypeMappingRegistry { 
    
    private HashMap mapTM;          // Type Mappings keyed with Namespace URI
    private TypeMapping defaultDelTM;  // Delegate to default Type Mapping 



    /**
     * Construct TypeMappingRegistry
     */
    public TypeMappingRegistryImpl() {
        mapTM = new HashMap();
        if (Constants.URI_CURRENT_SOAP_ENC.equals(Constants.URI_SOAP_ENC)) {
            defaultDelTM = 
                new TypeMappingImpl(DefaultTypeMappingImpl.create()); 
        } else {
            defaultDelTM = 
                new TypeMappingImpl(DefaultSOAP12TypeMappingImpl.create()); 
        }
    }
    
    /**
     * delegate
     *
     * Changes the contained type mappings to delegate to 
     * their corresponding types in the secondary TMR.
     */
    public void delegate(TypeMappingRegistry secondaryTMR) {

        if (secondaryTMR == null || secondaryTMR == this) {
            return;
        }
        String[]  keys = secondaryTMR.getRegisteredNamespaces();
//        String[]  keys = null;
        if (keys != null) {
            for(int i=0; i < keys.length; i++) {
                try {
                    String nsURI = keys[i];
                    TypeMapping tm = (TypeMapping) getTypeMapping(nsURI);
                    if (tm == null || tm == getDefaultTypeMapping() ) {
                        tm = (TypeMapping) createTypeMapping();
                        tm.setSupportedNamespaces(new String[] { nsURI });
                        register(nsURI, tm);
                    }
                    
                    if (tm != null) {
                        // Get the secondaryTMR's TM'
                        TypeMapping del = (TypeMapping)
                            ((TypeMappingRegistryImpl)
                             secondaryTMR).mapTM.get(nsURI);
                        tm.setDelegate(del);
                    }
                    
                } catch (Exception e) {
                }
            }
        }
        // Change our defaultDelTM to delegate to the one in 
        // the secondaryTMR
        if (defaultDelTM != null) {
            defaultDelTM.setDelegate(
            ((TypeMappingRegistryImpl)secondaryTMR).defaultDelTM);
        }
        
    }            
            


    /********* JAX-RPC Compliant Method Definitions *****************/
    
    /**
     * The method register adds a TypeMapping instance for a specific 
     * namespace                 
     *
     * @param namespaceURI 
     * @param mapping - TypeMapping for specific namespaces
     *
     * @return Previous TypeMapping associated with the specified namespaceURI,
     * or null if there was no TypeMapping associated with the specified namespaceURI
     *
     * @throws JAXRPCException - If there is any error in the registration
     * of the TypeMapping for the specified namespace URI
     */
    public javax.xml.rpc.encoding.TypeMapping register(String namespaceURI,
                         javax.xml.rpc.encoding.TypeMapping mapping) {
//        namespaceURI = "";
        if (mapping == null || 
            !(mapping instanceof TypeMapping)) {
            throw new IllegalArgumentException();
        } 
        if (namespaceURI == null) {
            throw new java.lang.IllegalArgumentException();
        }
        // Get or create a TypeMappingDelegate and set it to 
        // delegate to the new mapping.
        TypeMappingDelegate del = (TypeMappingDelegate)
            mapTM.get(namespaceURI);
        if (del == null) {
            del = new TypeMappingDelegate((TypeMapping) mapping);
            mapTM.put(namespaceURI, del);
        } else {
            del.setDelegate((TypeMapping) mapping);
        }
        return null; // Needs works
    }
    
    /**
     * The method register adds a default TypeMapping instance.  If a specific
     * TypeMapping is not found, the default TypeMapping is used.  
     *
     * @param mapping - TypeMapping for specific type namespaces
     *
     * java.lang.IllegalArgumentException - 
     * if an invalid namespace URI is specified
     */
    public void registerDefault(javax.xml.rpc.encoding.TypeMapping mapping) {
        if (mapping == null || 
            !(mapping instanceof TypeMapping) ||
            // Don't allow this call after the delegate() method since
            // the TMR's TypeMappings will be using the default type mapping
            // of the secondary TMR.
            defaultDelTM.getDelegate() instanceof TypeMappingDelegate) {
            throw new IllegalArgumentException();
        }
        defaultDelTM.setDelegate((TypeMapping) mapping);
    }
        
    /**
     * Gets the TypeMapping for the namespace.  If not found, the default
     * TypeMapping is returned.
     *
     * @param namespaceURI - The namespace URI of a Web Service
     * @return The registered TypeMapping 
     * (which may be the default TypeMapping) or null.
     */
    public javax.xml.rpc.encoding.TypeMapping 
        getTypeMapping(String namespaceURI) {
//        namespaceURI = "";
        TypeMapping del = (TypeMapping) mapTM.get(namespaceURI);
        TypeMapping tm = null;
        if (del != null) {
            tm = del.getDelegate();
        }
        if (tm == null) {
            tm = (TypeMapping)getDefaultTypeMapping();
        }
        return tm;
    }

    /**
     * Unregisters the TypeMapping for the namespace.
     *
     * @param namespaceURI - The namespace URI
     * @return The registered TypeMapping .
     */
    public javax.xml.rpc.encoding.TypeMapping 
        unregisterTypeMapping(String namespaceURI) {
        TypeMapping del = (TypeMapping) mapTM.get(namespaceURI);
        TypeMapping tm = null;
        if (del != null) {
            tm = del.getDelegate();
            del.setDelegate(null);
        }
        return tm;
    }

    /**
     * Removes the TypeMapping for the namespace.
     *
     * @param typeMapping- The type mapping   to remove
     * @return true if found and removed
     */
    public boolean removeTypeMapping(
                                     javax.xml.rpc.encoding.TypeMapping mapping) {
        String[] ns = getRegisteredNamespaces();
        boolean rc = false;
        for (int i=0; i < ns.length; i++) {
            if (getTypeMapping(ns[i]) == mapping) {
                rc = true;
                unregisterTypeMapping(ns[i]);
            }
        }
        return rc;
    }

    /**
     * Creates a new empty TypeMapping object for the specified
     * encoding style or XML schema namespace.
     *
     * @return An empty generic TypeMapping object
     */
    public javax.xml.rpc.encoding.TypeMapping createTypeMapping() {
        return new TypeMappingImpl(defaultDelTM);
    }
        

    /**
     * Gets a list of namespace URIs registered with this TypeMappingRegistry.
     *
     * @return String[] containing names of all registered namespace URIs
     */
    public String[] getRegisteredNamespaces() {
        java.util.Set s = mapTM.keySet(); 
        if (s != null) { 
            String[] rc = new String[s.size()];
            int i = 0;
            java.util.Iterator it = s.iterator();
            while(it.hasNext()) {
                rc[i++] = (String) it.next();
            }
            return rc;
        }
        return null;
    } 


    /**
     * Removes all TypeMappings and namespaceURIs from this TypeMappingRegistry.
     */
    public void clear() {
        mapTM.clear();
    }

    /**
     * Return the default TypeMapping
     * @return TypeMapping or null
     **/
    public javax.xml.rpc.encoding.TypeMapping getDefaultTypeMapping() {
        TypeMapping defaultTM = defaultDelTM;
        while(defaultTM != null && defaultTM instanceof TypeMappingDelegate) {
            defaultTM = defaultTM.getDelegate();
        }
        return defaultTM;
    }

}
